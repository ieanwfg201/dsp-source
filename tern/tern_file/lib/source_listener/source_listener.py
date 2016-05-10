#! /usr/bin/python

import sys
import shlex
import subprocess
import fnmatch


def list_matched_files(ls_command,source_dir,source_file_pattern):
    """
    returns file list (using a unix command ls_command) in a directory 
    source_dir and matches file name against a pattern source_file_pattern 
    >>> list_matched_files('ls -1rt','.','Hopefully This pattern DO NOT EXIST')
    []
    """    
    list_files = ls_command + " " + source_dir
    list_files_parts = shlex.split(list_files)
    lsprocess = subprocess.Popen(list_files_parts, stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    outputfiles = lsprocess.communicate()[0]

    filelist = []
    for individual_file in outputfiles.split("\n"):
        individual_file = individual_file.split("/")[-1]
        if fnmatch.fnmatch(individual_file,source_file_pattern ):
            filelist.append(individual_file)

    return filelist

if __name__ == '__main__':
    if(len(sys.argv) != 4):
        print "Usage: python source_listener.py 'ls command' source_dir' 'source_file_pattern'"
        print "Exaple Usge python source_listener.py 'ls -1rt' '/home/rohan' 'Work*'"
    else:
        filelist = list_matched_files(sys.argv[1],sys.argv[2],sys.argv[3])
        print filelist
