#! /usr/bin/python

import datetime
import sys
import math
import os
import time

curr_dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(curr_dir_path,'..','journal'))
import journal



def should_transfer(filename, ts_format, time_interval, source_dir, min_modified_time, journal_type, sqlite_journal_path, table_name):
    """
    Checks whether a 'file' has the name in accordance with timestamp format 'ts_format' and current time.
    It checks whether the file has already been transfered in the journal.
    It checks whether the file was last modified more than the min_modified_time
    Based on above checks it accordingly says whether a files should be transfered or not
    >>> should_transfer('filterer.py','',float(1),os.path.dirname(os.path.realpath(__file__)),float(0),'',os.path.join(os.path.dirname(os.path.realpath(__file__)),'..','..','sql','test.db'),'tern_transfer_state')
    False
    >>> should_transfer('filterer.py','%Y',float(1),os.path.dirname(os.path.realpath(__file__)),float(0),'',os.path.join(os.path.dirname(os.path.realpath(__file__)),'..','..','sql','test.db'),'tern_transfer_state')
    True
    """
    curr_timestamp = datetime.datetime.now().strftime(ts_format)
    if (filename.find(curr_timestamp) > -1):
        return False
    if journal.check_journal_if_file_exists(journal_type, sqlite_journal_path, table_name, filename):
        return False
    elif (math.floor(time.time() / time_interval) - math.floor(os.stat(source_dir + "/" + filename).st_mtime / time_interval)) <= min_modified_time:
        return False
    return True


if __name__ == '__main__':
    if(len(sys.argv) != 9):
        print "Usage: python filterer.py  filename 'ts_format' 'time_interval' 'source_dir' 'min_modified_time' 'journal_type' 'sqlite_journal_path' 'table_name'"
        print "Example Usage: python filterer.py 'filterer.py' '%Y' '1' '.' '0' '' './../../sql/test.db' 'tern_transfer_state'"
    else:
        print os.path.realpath(__file__)
        returnvalue = should_transfer(sys.argv[1],sys.argv[2],float(sys.argv[3]),sys.argv[4],float(sys.argv[5]),sys.argv[6],sys.argv[7],sys.argv[8])
        print returnvalue
