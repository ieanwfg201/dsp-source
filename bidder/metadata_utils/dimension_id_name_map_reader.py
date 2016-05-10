import MySQLdb

def getDimIdNameMap(dbConnection, dimIdNameMappingTableName) :
    """ Returns the dimension id->dimension name map from the metadata table

    Keyword arguments :
    dbConnection -- connection to the database
    dimIdNameMappingTableName -- Name of the metadata table containing the id->name mapping

    return map containing dimension id->dimension name
    """
    cursor = dbConnection.cursor()
    cursor.execute("SELECT * FROM " + dimIdNameMappingTableName)

    dimIdNameMap = {}
    for row in cursor.fetchall() :
        dimIdNameMap[int(row[0])] = row[1]

    return dimIdNameMap

if __name__ == '__main__' :
    db = MySQLdb.connect(host="localhost", user="root", db="seventynine")
    res = getDimIdNameMap(db, "dimension_name_id_mapping")
    print res
