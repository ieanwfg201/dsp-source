#! /usr/bin/python

import os
import sys
import time
import glob
import shutil

if len(sys.argv) >= 2 and ('doctest' not in sys.argv[0]):
    conf_path=sys.argv[1]
sys.path.append(conf_path)
import oxpecker_conf

def delete_path(abs_path_name, retention_in_seconds, now):
    """
    Delete file abs_path_name created beyond retention_in_seconds
    """
    if(os.path.exists(abs_path_name)):
        if(os.stat(abs_path_name).st_mtime < now - retention_in_seconds):
            if(os.path.isfile(abs_path_name)):
                os.remove(abs_path_name)
                print "DELETED: ", abs_path_name
            else:
                shutil.rmtree(abs_path_name)
                print "DELETED: ", abs_path_name
    return True

def oxpecker(abs_path_file_regex, retention_in_minutes):
    """
    """
    now = time.time()
    allpaths = glob.glob(abs_path_file_regex)
    for path in allpaths:
        delete_path(path, retention_in_minutes * 60, now)
    return True

def oxpecker_from_config():
    """
    """
    path_retentionmap = oxpecker_conf.path_retentionmap
    for abs_path_file_regex, retention_in_minutes in path_retentionmap.iteritems():
        oxpecker(abs_path_file_regex, retention_in_minutes)
    return True

if __name__ == '__main__':
    if(len(sys.argv) == 2):
        retval = oxpecker_from_config()
        print retval
    elif(len(sys.argv) == 3):
        retval = oxpecker(sys.argv[1], int(sys.argv[2]))
        print retval
    else:
        print "############################# ERROR IN USAGE #############################"
        print "# USAGE 1 : python oxpecker.py <abs_path_file_regex> <retention_in_mins> #"
        print "# USAGE 2 : python oxpecker.py <conf absolute path>                      #"
        print "##########################################################################"
        

