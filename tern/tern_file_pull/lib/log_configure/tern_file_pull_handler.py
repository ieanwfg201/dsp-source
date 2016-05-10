#! /usr/bin/python

import logging
import os
from time import strftime

class tern_file_pull_handler(logging.FileHandler):
    def __init__(self,path,filename,pattern,mode):
        if not os.path.exists(path):
            os.makedirs(path)
        super(tern_file_pull_handler,self).__init__(os.path.join(path,filename+strftime(pattern)),mode)
