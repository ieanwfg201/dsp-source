#! /usr/bin/python

import sys
import os
import glob
import datetime
from time import strftime

curr_dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(curr_dir_path,'journal'))
import journal

def clean_journal(source_dir, source_date_pattern_for_cleanup, retention_period, journal_type, sqlite_journal_path, table_name):
    """
    Cleans up journal with data belonging 'retention_period' days old if in the source_dir no files having pattern
    source_date_pattern_for_cleanup was found.
    """
    exact_day = datetime.timedelta(days=retention_period)
    exact_date = (datetime.datetime.now() - exact_day).strftime(source_date_pattern_for_cleanup)
    file_list = glob.glob(source_dir + "/*" + exact_date + '*')

    if not file_list:
        journal.delete_from_journal_with_vacuum(journal_type, sqlite_journal_path, table_name, exact_date)
        return True
    return False


if __name__ == '__main__':
    if(len(sys.argv) != 7):
        print "Usage: python clean_journal.py 'source_dir' 'source_date_pattern_for_cleanup' 'retention_period' 'journal_type' 'sqlite_journal_path' 'table_name'"
        print "Example Usage: python clean_journal.py '.' '%Y-%m-%d' '1' 'sqlite' '/tmp/test-tern_journal.db' 'tern_transfer_state'"
    else:
        returnvalue = clean_journal(sys.argv[1], sys.argv[2], float(sys.argv[3]), sys.argv[4], sys.argv[5],sys.argv[6])
        
print returnvalue
