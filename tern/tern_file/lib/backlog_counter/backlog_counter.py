#! /usr/bin/python

import os
import sys
import shlex
import subprocess
import fnmatch
import time

curr_dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(curr_dir_path,'..','journal'))
import journal

def time_elapsed(filename, backlog_time_lag):
    last_modified_time = os.path.getmtime(filename)
    return time.time() - last_modified_time

def backlog_counter(source_dir, source_file_pattern, source_file_pattern_db, journal_type, sqlite_journal_path, table_name, ls_command, db_operation_type, state, backlog_time_lag):
    """
    Gives Backlog count of transfer of files source_file_pattern in dir source_dir by comparing the last 
    transfered file from the journal
    """
    last_file = journal.do_crud(db_operation_type, journal_type, sqlite_journal_path, table_name, source_file_pattern_db, state)
    list_files = ls_command + " " + source_dir
    list_files_parts = shlex.split(list_files)
    lsprocess = subprocess.Popen(list_files_parts, stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    outputfiles = lsprocess.communicate()[0]

    filelist = []
    backlog_count = 0
    backlog_time = 0
    for individual_file in outputfiles.split("\n"):
        individual_file = individual_file.split("/")[-1]
        if fnmatch.fnmatch(individual_file,source_file_pattern ):
            if last_file is None:
                backlog_count = backlog_count + 1
                if(backlog_count == 1):
                    backlog_time = time_elapsed(source_dir+'/'+ individual_file,backlog_time_lag)
            elif individual_file > last_file:
                backlog_count = backlog_count + 1
                if(backlog_count == 1):
                    backlog_time = time_elapsed(source_dir+'/'+ individual_file,backlog_time_lag)
    return (backlog_count, backlog_time)

def backlog_counter_from_config(conf_path):
    sys.path.append(conf_path)
    import tern_file_conf
    return backlog_counter(tern_file_conf.source_dir, tern_file_conf.source_file_pattern, tern_file_conf.source_file_pattern_db, tern_file_conf.journal_type, tern_file_conf.sqlite_journal_path, tern_file_conf.table_name, tern_file_conf.ls_command, 'SELECTLAST', '', tern_file_conf.backlog_time_lag)

if __name__ == '__main__':
    if(len(sys.argv) != 2):
        print "USAGE: python backlog_counter.py 'conf_path'"
        print "Example Usage: python backlog_counter.py '/usr/share/kritter/tern_file/conf/sample'"
    else:
        returnvalue = backlog_counter_from_config(sys.argv[1])
        print returnvalue
