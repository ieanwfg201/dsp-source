#! /usr/bin/python

import sys
import os
import ast
import logging
import traceback


curr_dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(curr_dir_path,'..','lib','source_listener'))
import source_listener
sys.path.append(os.path.join(curr_dir_path,'..','lib','filterer'))
import filterer
sys.path.append(os.path.join(curr_dir_path,'..','lib','destination_selector'))
import destination_selector
sys.path.append(os.path.join(curr_dir_path,'..','lib','transporter'))
import transporter
sys.path.append(os.path.join(curr_dir_path,'..','lib','journal'))
import journal

conf_path = os.path.join(curr_dir_path,'..','conf','sample')
if len(sys.argv) >= 2 and ('doctest' not in sys.argv[0]):
    conf_path=sys.argv[1]
sys.path.append(conf_path)
import tern_file_conf

sys.path.append(os.path.join(curr_dir_path,'..','lib','log_configure'))
import log_configure
log_configure.configure_log(conf_path)
tern_logger = logging.getLogger(__name__)



def tern(ls_command, source_dir, source_file_pattern, ts_format, time_interval, min_modified_time,
        command, username, transport_type, destination_path, timeout, other_option, destination_list,
        journal_type, sqlite_journal_path, table_name):
    """
    tern is the method which is responsible for transporting file belonging to source_file_pattern from
    directory source_dir to destination destination_list-destination_path using transport command
    from user username using helper configs of ts_format,time_interval ,min_modified_time,timeout, other_option
    >>> tern('ls -1rt','.','tern_file.py','%Y-%m-%d-%H-%M',1,0,'rsync %s --rsh="%s -p%s" %s %s --stats %s %s@%s:%s',os.getlogin(),'rsync','/tmp','--timeout=500','',ast.literal_eval('{"localhost": 22}'),'',os.path.join(os.path.dirname(os.path.realpath(__file__)),'..','sql','test.db'),'tern_transfer_state')
    True
    """
    try:
        filelist = source_listener.list_matched_files(ls_command,source_dir,source_file_pattern)
        for afile in filelist:
            if(filterer.should_transfer(afile,ts_format,time_interval,source_dir,min_modified_time,journal_type, sqlite_journal_path, table_name)):
                destinations = destination_selector.select_destination(destination_list)
                transport_success = False
                for host, port in destinations:
                    transport_success = transporter.transport(command, source_dir, afile, username , transport_type, host, port, destination_path, timeout, other_option)
                    # Break as it needs to be done only once
                    break
                if(transport_success):
                    journal.insert_into_journal(journal_type, sqlite_journal_path, table_name, afile,'0')
                    tern_logger.info(''.join(['TRANSFERED: ',source_dir,'/',afile,' to ',host,':',str(port),' ',destination_path]))
                else:
                    tern_logger.info(''.join(['TRANSPORT FAILED: ',source_dir,'/',afile,' to ',host,':',str(port),' ',destination_path]))
    except:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        lines = traceback.format_exception(exc_type, exc_value, exc_traceback)
        tern_logger.error(''.join('!! ' + line for line in lines))
    return True

def tern_from_config():
    """
    tern from config is the invocation of the function tern the parameters of which comes from a config
    >>> tern_from_config()
    True
    """
    returnval = tern(tern_file_conf.ls_command, tern_file_conf.source_dir, tern_file_conf.source_file_pattern, tern_file_conf.ts_format,
        tern_file_conf.time_interval, tern_file_conf.min_modified_time, tern_file_conf.command, tern_file_conf.username, 
        tern_file_conf.transport_type, tern_file_conf.destination_path, tern_file_conf.timeout, tern_file_conf.other_option,
        tern_file_conf.destination_list, tern_file_conf.journal_type, tern_file_conf.sqlite_journal_path, tern_file_conf.table_name)
    return returnval


if __name__ == '__main__':
    if(len(sys.argv) == 2):
        retval = tern_from_config()
        print retval
    elif(len(sys.argv) == 18):
        retval = tern(sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],float(sys.argv[6]),float(sys.argv[7]),sys.argv[8],sys.argv[9],sys.argv[10],sys.argv[11],sys.argv[12],sys.argv[13],ast.literal_eval(sys.argv[14]),sys.argv[15],sys.argv[16],sys.argv[17])
        print retval
    else:
        print "################################# ERROR IN USAGE #################################"
        print "USAGE 1: Read from Config: python tern_file.py  '<conf path>'"
        print "Example for USAGE 1: Read from Config python tern_file.py  '/home/rohan/workspace/ysoserious/tern/tern_file/conf/sample'"
        print "USAGE 2: Read from command line: python tern_file.py  '<conf path>' 'ls_command' 'source_dir' 'source_file_pattern' 'ts_format' 'time_interval' 'min_modified_time' 'command' 'username' 'transport_type' 'destination_path' 'timeout' 'other_option' 'destination_list' 'journal_type' 'sqlite_journal_path' 'table_name'" 
        print "Example for USAGE 2:  python tern_file.py '/home/rohan/workspace/ysoserious/tern/tern_file/conf/sample' 'ls -1rt' '.' 'tern_file.py' '%Y-%m-%d-%H-%M' '1' '0' 'rsync %s --rsh=\"%s -p%s\" %s %s --stats %s %s@%s:%s' 'rohan' 'rsync' '/tmp' '--timeout=500' '' '{\"localhost\": 22}' '' './../sql/test.db' 'tern_transfer_state'"
        print "###################################################################################"

