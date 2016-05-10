#! /usr/bin/python

import sys

def compresssion_option(transport_type):
    """
    compression option returns a compression type and by default returns gzip compression type
    for rsync
    >>> compresssion_option('')
    ' -ztv '
    """
    return ' -ztv '

if __name__ == '__main__':
    if(len(sys.argv) != 2):
        print "Usage: python compressor.py 'transport_type'"
        print "Example Usge python compressor.py ''"
    else:
        returnvalue = compresssion_option(sys.argv[1])
        print returnvalue

