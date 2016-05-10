#! /usr/bin/python

import logging
import os
import sys
import logging.config
from settings import LOG_SETTINGS

def configure_log(conf_path, fileName):
    curr_dir_path = os.path.dirname(os.path.realpath(__file__))
    log_conf = os.path.join(conf_path, fileName)
    logging.config.fileConfig(log_conf)

def configure_log(conf_path):
    curr_dir_path = os.path.dirname(os.path.realpath(__file__))
    logging.config.fileConfig(conf_path)

def configure_log_with_output(conf_dict, outputPath, outputFileName):
    """
    Configure the logger with given map and output file
    :param conf_dict: Dict(map) containing logger properties, like formatters, handlers etc. Sample dict in settings.py
    :type conf_dict: dict
    :param outputPath: Output file path
    :type outputPath: str
    :param outputFileName: Output file name prefix, where the actual logs will go
    :type outputFileName: str
    :return:
    """
    conf_dict['handlers']['simpleFileHandler']['path'] = outputPath
    conf_dict['handlers']['simpleFileHandler']['filename'] = outputFileName
    logging.config.dictConfig(conf_dict)

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "Usage: python log_configure.py <output file path> <output file name>"
        print "Example Usage: python log_configure.py /tmp log.output"
    else:
        path = sys.argv[1]
        output_file = sys.argv[2]
        configure_log_with_output(LOG_SETTINGS, path, output_file)
        appLogger = logging.getLogger(__name__)
        appLogger.info('Works fine!')
