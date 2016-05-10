#! /usr/bin/python

import os
import sys

from optparse import OptionParser
curr_dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(curr_dir_path,'..','backlog_counter'))
import backlog_counter


"""
A NAGIOS PLUGIN for TERN FILE
"""

if __name__ == '__main__':
    # CONSTANTS FOR RETURN CODES UNDERSTOOD BY NAGIOS
    # Exit statuses recognized by Nagios
    UNKNOWN = 3
    OK = 0
    WARNING = 1
    CRITICAL = 2
    # TEMPLATE FOR READING PARAMETERS FROM COMMANDLINE
    parser = OptionParser()
    parser.add_option("--conf_path", dest="conf_path", 
        default='/usr/share/kritter/tern_file/conf/sample/', help="configutation path for critter tern_file ")
    parser.add_option("--backlog_crit_count", type="int",dest="backlog_crit_count",
        default=15 , help="backlog critical count ")
    parser.add_option("--backlog_warn_count", type="int",dest="backlog_warn_count",
        default=10 , help="backlog warn count ")
    parser.add_option("--backlog_time_lag", type="int",dest="backlog_time_lag",
        default=900 , help="acceptable time lag of file yet to be transfered in seconds")
       
    (options, args) = parser.parse_args()
    sys.path.append(options.conf_path)
    import tern_file_conf
    (count, time_elapsed) = backlog_counter.backlog_counter_from_config(options.conf_path)
    backlog_crit_count = options.backlog_crit_count
    backlog_warn_count = options.backlog_warn_count
    backlog_time_lag = options.backlog_time_lag
    if(hasattr(tern_file_conf,'backlog_crit_count')):
        backlog_crit_count = tern_file_conf.backlog_crit_count
    if(hasattr(tern_file_conf,'backlog_warn_count')):
        backlog_warn_count = tern_file_conf.backlog_warn_count
    if(hasattr(tern_file_conf,'backlog_time_lag')):
        backlog_time_lag = tern_file_conf.backlog_time_lag
    if(count > backlog_crit_count):
        print 'CRITICAL - BACKLOG COUNT %s in dir %s of file pattern %s' % (count,tern_file_conf.source_dir,tern_file_conf.source_file_pattern)
        raise SystemExit, CRITICAL
    elif(backlog_time_lag < time_elapsed):
        print 'CRITICAL - TRANSFER TIME ELAPSED of file pattern %s in dir %s too high = %s' % (tern_file_conf.source_file_pattern,tern_file_conf.source_dir,time_elapsed)
        raise SystemExit, CRITICAL
    elif(count > backlog_warn_count):
        print 'WARNING - BACKLOG COUNT %s in dir %s of file pattern %s' % (count,tern_file_conf.source_dir,tern_file_conf.source_file_pattern)
        raise SystemExit, WARNING
    elif(count >= 0): 
        print 'OK - BACKLOG COUNT %s in dir %s of file pattern %s' % (count,tern_file_conf.source_dir,tern_file_conf.source_file_pattern)
        raise SystemExit, OK   
    else:
        print 'UNKNOWN ERROR - PLEASE DEBUG '
        raise SystemExit, UNKNOWN
