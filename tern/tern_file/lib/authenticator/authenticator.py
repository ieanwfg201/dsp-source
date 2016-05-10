#! /usr/bin/python

import sys

def authentication_protocol(transport_type):
    """
    authentication protocol returns a auth protocol by default returns ssh
    for rsync
    >>> authentication_protocol('')
    "ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no'"
    """
    return "ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no'"

if __name__ == '__main__':
    if(len(sys.argv) != 2):
        print "Usage: python authenticator.py 'transport_type'"
        print "Example Usge python authenticator.py ''"
    else:
        returnvalue = authentication_protocol(sys.argv[1])
        print returnvalue

