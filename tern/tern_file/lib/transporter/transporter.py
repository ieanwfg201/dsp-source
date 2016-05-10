#! /usr/bin/python

import sys
import shlex
import os
import subprocess
from socket import gethostname
curr_dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(curr_dir_path,'..','compressor'))
import compressor
sys.path.append(os.path.join(curr_dir_path,'..','authenticator'))
import authenticator

def transport(command, source_dir,filename, username , transport_type, destination_host, destination_port, destination_path, timeout,other_option):
    """
    transports a file 'filename' from 'source_dir' to destination_path which by default supports rsyncbased transfer
    >>> transport('rsync %s --rsh="%s -p%s" %s %s --stats %s %s@%s:%s',os.path.dirname(os.path.realpath(__file__)),'transporter.py',os.getlogin(),'rsync','localhost','22','/tmp','--timeout=500','')
    True
    """
    hostname = gethostname()
    final_filename = hostname + "-" + filename
    compression_option = compressor.compresssion_option(transport_type)
    auth = authenticator.authentication_protocol(transport_type)
    final_command = command %(compression_option,auth,destination_port, compression_option,other_option,source_dir+'/'+filename,username,destination_host,destination_path+"/"+final_filename)
    command_parts = shlex.split(final_command)
    command_process = subprocess.Popen(command_parts, stdout=subprocess.PIPE, stderr = subprocess.PIPE)
    command_output = command_process.communicate()
    command_process.wait()
    if command_process.poll() == 0:
        #success
        output = command_output[0]
        return True
    else:
        #failure
        print "Failure - %s" %final_command + command_output[1]
        return False
    
if __name__ == '__main__':
    if(len(sys.argv) != 11):
        print "Usage: python transporter.py  'command' 'source_dir' 'filename' 'username' 'transport_type' 'destination_host'  'destination_port' 'destination_path'  'timeout' 'other_option' 'auth'"
        print "Example Usage:  python transporter.py 'rsync %s --rsh=\"%s -p%s\" %s %s --stats %s %s@%s:%s' '.' 'transporter.py' 'rohan' 'rsync' 'localhost' '22' '/tmp' '--timeout=500' ''"
    else:
        returnvalue = transport(sys.argv[1], sys.argv[2], sys.argv[3] , sys.argv[4], sys.argv[5], sys.argv[6], sys.argv[7], sys.argv[8], sys.argv[9],sys.argv[10])
        print returnvalue
