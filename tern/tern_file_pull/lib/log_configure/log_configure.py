#! /usr/bin/python

import logging
import os
import sys
import logging.config


def configure_log(conf_path):
    curr_dir_path = os.path.dirname(os.path.realpath(__file__))
    log_conf = os.path.join(conf_path,'logger.conf')
    logging.config.fileConfig(log_conf)

def configure_log_from_config():
    configure_log(os.path.join(os.path.dirname(os.path.realpath(__file__)),'..','..','conf','sample'))

if __name__ == '__main__':
    if(len(sys.argv) != 1):
        print "Usage: python log_configure.py "
        print "Example Usage: python log_configure.py"
    else:
        configure_log_from_config()
