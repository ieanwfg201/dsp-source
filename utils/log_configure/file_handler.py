import logging
import os
from time import strftime

class file_handler(logging.FileHandler):
    def __init__(self,path,filename,pattern,mode):
        if not os.path.exists(path):
            os.makedirs(path)
        super(file_handler,self).__init__(os.path.join(path,filename+strftime(pattern)),mode)

