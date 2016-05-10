#! /usr/bin/python

import sqlite3
import os
import sys

def create_journal(journal_sql, journal_path, journal_name):
    """
    Reeads sql file journal_sql and create sqlite db/table in path journal_path/journal_name
    >>> create_journal( os.path.join(os.path.dirname(os.path.realpath(__file__)),'..','..','sql','setup.sql'),'/tmp','test-tern_journal.db')
    True
    """
    qry = open(journal_sql, 'r').read()
    conn = sqlite3.connect(os.path.join(journal_path,journal_name))
    c = conn.cursor()
    c.execute(qry)
    conn.commit()
    c.close()
    conn.close()
    return True

if __name__ == '__main__':
    if(len(sys.argv) != 4):
        print "Usage: python create_journal.py 'journal_sql' 'journal_path' 'journal_name'"
        print "Example Usge: python create_journal.py './../../sql/setup.sql' '/tmp' 'tern_journal.db'"
    else:
        returnvalue = create_journal(sys.argv[1], sys.argv[2], sys.argv[3])
        print returnvalue
