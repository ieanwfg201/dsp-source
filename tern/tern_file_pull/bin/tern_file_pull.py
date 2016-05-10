#! /usr/bin/python

import os
import subprocess
import shlex
import sys
import logging
import traceback

curr_dir_path = os.path.dirname(os.path.realpath(__file__))
conf_path = os.path.join(curr_dir_path,'..','conf','sample')
if len(sys.argv) >= 2 and ('doctest' not in sys.argv[0]):
    conf_path=sys.argv[1]
sys.path.append(conf_path)
import tern_file_pull_conf

sys.path.append(os.path.join(curr_dir_path,'..','lib','log_configure'))
import log_configure
log_configure.configure_log(conf_path)
tern_logger = logging.getLogger(__name__)



def tern_file_pull(source_dir, source_file_link_name, symlink_resolver_command, source_username, source_hostname, destination_dir, transfer_command):
    """
    Function to pull realpath of source_username@source_hostname:source_dir/source_file_link to destination_dir and create the corresponding soft link in destination_dir.
    """
    source_abs_path = os.path.join(source_dir,source_file_link_name)
    commanresolve = symlink_resolver_command %(source_username, source_hostname, source_abs_path)
    command_parts = shlex.split(commanresolve)
    command_process = subprocess.Popen(command_parts, stdout=subprocess.PIPE, stderr = subprocess.PIPE)
    command_output = command_process.communicate()
    command_process.wait()
    if command_process.poll() == 0:
    #success
        output = command_output[0]
        outputstrip = output.strip()
        head, tail = os.path.split(outputstrip)
        targetpath = os.path.join(destination_dir,tail)
        if(not os.path.exists(targetpath)):
            transfercommandresolve = transfer_command %(source_username, source_hostname, outputstrip, targetpath)
            print transfercommandresolve
            command_parts = shlex.split(transfercommandresolve)
            command_process = subprocess.Popen(command_parts, stdout=subprocess.PIPE, stderr = subprocess.PIPE)
            command_output = command_process.communicate()
            command_process.wait()
            if command_process.poll() == 0:
                 #success
                output = command_output[0]
                destination_dir_link = os.path.join(destination_dir,source_file_link_name)
                if(os.path.exists(destination_dir_link)):
                    os.unlink(destination_dir_link)
                os.symlink(targetpath,  destination_dir_link)
                return True
            else:
                #failure
                print "Failure - %s" %transfercommandresolve + command_output[1]
                return False
        else:
            print "exists"
            return True
    else:
    #failure
        print "Failure - %s" %commanresolve + command_output[1]
        return False

def tern_file_pull_from_config():
    retVal = tern_file_pull(tern_file_pull_conf.source_dir, tern_file_pull_conf.source_file_link_name, tern_file_pull_conf.symlink_resolver_command, tern_file_pull_conf.source_username, tern_file_pull_conf.source_hostname, tern_file_pull_conf.destination_dir, tern_file_pull_conf.transfer_command)
    return retVal

if __name__ == '__main__':
    print len(sys.argv)
    if(len(sys.argv) == 2):
        retVal = tern_file_pull_from_config()
        print retVal
    elif(len(sys.argv) == 9):
        retval = tern_file_pull(sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6],sys.argv[7], sys.argv[8])
        print retval
    else:
        print "########################################### ERROR IN USAGE ############################################"
        print "#                         Usage1: python tern_file_pull.py <path to conf dir>                         #"
        print "# Example: python tern_file_pull.py /home/rohan/workspace/ysoserious/tern/tern_file_pull/conf/sample/ #"
        print "#######################################################################################################"
