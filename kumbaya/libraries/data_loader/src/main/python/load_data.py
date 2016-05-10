import sys
import MySQLdb

def load_data(filepath, tablename, delimiter, hostname, username, password, database,  processing_time):
    conn = MySQLdb.connect(hostname, username, password, database, local_infile = 1)
    c = conn.cursor()
    try:
        if (processing_time != ''):
            query = "delete from "+tablename+" where processing_time = '"+processing_time+"'"
            c.execute(query)
        query = "LOAD DATA LOCAL INFILE '"+filepath+"' INTO TABLE "+tablename+" FIELDS TERMINATED BY '"+delimiter+"' ENCLOSED BY '\"' ESCAPED BY '\\\\'"
        print query
        c.execute(query)
        conn.commit()
    except Exception, e:
        conn.rollback()
        print e
    finally:
        conn.close()

if __name__ == '__main__':
    if(len(sys.argv) == 9):
        load_data(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],sys.argv[5],sys.argv[6],sys.argv[7],sys.argv[8])
    else:
        print "################################# ERROR IN USAGE #################################"
        print "################################# EXAMPLE USAGE #################################"
        print "python src/main/python/load_data.py /home/rohan/testdata/vserv/out.txt/part-r-00000 bidder_reporting ^A localhost root password vserv_bidder ''"


