import MySQLdb
import datetime

def getLastProcessedTime(dbConnection, tableName, rowName, timeFormat) :
    """ Returns the last processed date from the table.

    Keyword arguments :
    dbConnection -- connection to the database
    tableName -- Name of the table containing the last processed date
    rowName -- key of the row containing the last processed date

    return datetime object containing the last processed date
    """
    cursor = dbConnection.cursor()
    query = "SELECT * FROM " + tableName + " WHERE process_name = '" + rowName + "'"
    cursor.execute(query)
    dbConnection.commit()

    dateString = None
    for row in cursor.fetchall() :
        dateString = row[1]

    if dateString is None :
        return None

    cursor.close()
    """ Change the date string to a datetime object """
    dateObject = datetime.datetime.strptime(dateString, timeFormat)
    return dateObject

if __name__ == '__main__' :
    db = MySQLdb.connect(host="localhost", user="root", db="test")
    date = getLastProcessedTime(db, "last_processed_meta", "supply_forecast", "%Y-%m-%d")
    print date
    db.close()
