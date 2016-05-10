#! /usr/bin/python

import sys
import sqlite3
import datetime

def check_journal_if_file_exists(journal_type, sqlite_journal_path, table_name, filename):
    """
    By default the method supports sqlite3 and check the filename exists in sqlite_journal_path
    in the table table_name. journal_type is for supporting multiple journal types in future
    """
    conn = sqlite3.connect(sqlite_journal_path)
    cursor = conn.cursor()
    query = "select * from %s where filename = '%s'" %(table_name,filename)
    cursor.execute(query)
    row = cursor.fetchone()
    if row is None:
        cursor.close()
        conn.close()
        return False
    else:
        cursor.close()
        conn.close()
        return True
    return False

def insert_into_journal(journal_type, sqlite_journal_path, table_name, filename, state):
    """
    By default the method supports sqlite3  and inserts filename,state in the table table_name
    of db sqlite_journal_path. journal_type is for supporting multiple journal types in future
    """
    conn = sqlite3.connect(sqlite_journal_path)
    cursor = conn.cursor()
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H-%M-%S")
    query = "insert into %s values('%s','%s', %s)" %(table_name, timestamp, filename, state)
    cursor.execute(query)
    conn.commit()
    cursor.close()
    conn.close()
    return True

def delete_from_journal(journal_type, sqlite_journal_path, table_name, filename):
    """
    By default the method supports sqlite3  and deletes filename from the table table_name
    of db sqlite_journal_path. journal_type is for supporting multiple journal types in future
    """
    conn = sqlite3.connect(sqlite_journal_path)
    cursor = conn.cursor()
    query = "delete from %s where filename='%s'" %(table_name, filename)
    cursor.execute(query)
    conn.commit()
    cursor.close()
    conn.close()
    return True

def delete_from_journal_with_vacuum(journal_type, sqlite_journal_path, table_name, filename_pattern):
    """
    By default the method supports sqlite db and deletes file having patternfilename_pattern 
    from the table sqlite_journal_path of db sqlite_journal_path and runs vacuum on top of it to
    improve db performance. journal_type is for supporting multiple journal types in future
    """
    conn = sqlite3.connect(sqlite_journal_path)
    cursor = conn.cursor()
    query = "delete from %s where filename like '%%%s%%';vacuum;" %(table_name, filename_pattern)
    cursor.executescript(query)
    conn.commit()
    cursor.close()
    conn.close()
    return True

def select_last_from_journal(journal_type, sqlite_journal_path, table_name, filename_pattern):
    """
    By default the method supports sqlite db and return last entered enty of filename filename_pattern 
    in table table_name of db sqlite_journal_path.journal_type is for supporting multiple journal types 
    in future
    """
    conn = sqlite3.connect(sqlite_journal_path)
    cursor = conn.cursor()
    query = "select filename from %s where filename like '%s' order by filename desc limit 1" %(table_name,filename_pattern)
    cursor.execute(query)
    row = cursor.fetchone()
    if row is None:
        cursor.close()
        conn.close()
        return None
    else:
        data = row[0]
        cursor.close()
        conn.close()
        return data
    return None


def do_crud(db_operation_type, journal_type, sqlite_journal_path, table_name, filename, state):
    """
    The method allows do CRUD operation depending on db_operation_type
    """
    returnvalue = False
    if(db_operation_type == 'CHECK'):
        returnvalue = check_journal_if_file_exists(journal_type, sqlite_journal_path, table_name, filename)
    elif(db_operation_type == 'INSERT'):
        returnvalue = insert_into_journal(journal_type, sqlite_journal_path, table_name, filename, state)
    elif(db_operation_type == 'DELETE'):
        returnvalue = delete_from_journal(journal_type, sqlite_journal_path, table_name, filename)
    elif(db_operation_type == 'DELETEVACUUM'):
        returnvalue = delete_from_journal_with_vacuum(journal_type, sqlite_journal_path, table_name, filename)
    elif(db_operation_type == 'SELECTLAST'):
        returnvalue = select_last_from_journal(journal_type, sqlite_journal_path, table_name, filename)
    return returnvalue
    

if __name__ == '__main__':
    if(len(sys.argv) != 7):
        print "Usage: python journal.py '<DB OPERATION TYPE>' 'journal_type' 'sqlite_journal_path' 'table_name' 'filename'"
        print "Example Usage1: python journal.py 'CHECK' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'I Dont exists' ''"
        print "Example Usage2: python journal.py 'INSERT' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAME' '0'"
        print "Example Usage3: python journal.py 'CHECK' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAME' ''"
        print "Example Usage4: python journal.py 'SELECTLAST' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAME' ''"
        print "Example Usage5: python journal.py 'DELETE' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAME' ''"
        print "Example Usage6: python journal.py 'CHECK' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAME' ''"
        print "Example Usage7: python journal.py 'CHECK' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAMEPATTERN' ''"
        print "Example Usage8: python journal.py 'INSERT' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAMEPATTERN' '0'"
        print "Example Usage9: python journal.py 'CHECK' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAMEPATTERN' ''"
        print "Example Usage10: python journal.py 'DELETEVACUUM' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAME' ''"
        print "Example Usage11: python journal.py 'CHECK' '' '/tmp/test-tern_journal.db' 'tern_transfer_state' 'TESTFILENAMEPATTERN' ''"
    else:
        returnvalue = do_crud(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5],sys.argv[6])
        print returnvalue
