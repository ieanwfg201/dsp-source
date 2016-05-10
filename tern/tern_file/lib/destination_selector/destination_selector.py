#! /usr/bin/python

import random
import sys
import ast

def select_destination(destination_list):
    """
    takes in destination list of hostname and ports and randomly shuffles it and guves back the item list
    >>> select_destination(ast.literal_eval('{"localhost": 22}'))
    [('localhost', 22)]
    """
    destination_list_items = destination_list.items()
    random.shuffle(destination_list_items)
    return destination_list_items

if __name__ == '__main__':
    if(len(sys.argv) != 2):
        print "Usage: python destination_selector.py 'destination list'"
        print "Example Usge python destination_selector.py '{\"localhost\": 22}'"
    else:
        print sys.argv[1]
        returnvalue = select_destination(ast.literal_eval(sys.argv[1]))
        print returnvalue

