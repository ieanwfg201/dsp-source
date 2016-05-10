import MySQLdb
import pickle
from time import gmtime, strftime
from subprocess import call

from config import *
from utils.log_configure.log_configure import *
from fetch_data import *

loggerConfigLocation = sys.argv[2]
configure_log(loggerConfigLocation)
appLogger = logging.getLogger(__name__) 

def deleteDataFromDB(dbConnection, tableName, modelId):
    """ Function to delete the data for the given model id in database

        Keyword arguments:
        dbConnection -- Connection to the database
        tableName -- Name of the table
        modelId -- integer id of this model
    """
    existsQuery = "SELECT * FROM " + tableName + " WHERE id = " + str(modelId)

    cursor = None
    try:
        cursor = dbConnection.cursor()
        cursor.execute(existsQuery)
        dbConnection.commit()
        row = cursor.fetchone()
        query = None

        if row is not None:
            # Entry for the given modelId exists
            query = "UPDATE " + tableName + " SET last_modified = NOW(), data = \"\" WHERE id = " + str(modelId)
        else:
            # No entry exists. Create a new entry
            query =  "INSERT INTO " + tableName + " (id, data, last_modified) VALUES (" + str(modelId) + ", \"\", NOW())"

        cursor.execute(query)
        dbConnection.commit()
    except Exception, e:
        configLogger.info("Exception occurred %s", e)
    finally:
        if cursor != None:
            cursor.close()

def storeResultsInDB(dbConnection, tableName, modelId, results, weight):
    """ Function to store the results in database 

        Keyword arguments:
        dbConnection -- Connection to the database
        tableName -- Name of the table where to put in the results
        modelId -- integer id of this model
        results -- Output of the model that has to be stored in the db
    """
    existsQuery = "SELECT * FROM " + tableName + " WHERE id = " + str(modelId)

    cursor = None
    try:
        cursor = dbConnection.cursor()
        cursor.execute(existsQuery)
        dbConnection.commit()
        row = cursor.fetchone()
        query = None

        if row is not None:
            # Entry for the given modelId exists
            query = "UPDATE " + tableName + " SET last_modified = NOW(), data = \"" + str(results) + "\" WHERE id = " + str(modelId)
        else:
            # No entry exists. Create a new entry
            query =  "INSERT INTO " + tableName + " (id, data, last_modified) VALUES (" + str(modelId) + ", \"" + str(results) + "\", NOW())"

        cursor.execute(query)
        dbConnection.commit()
    except Exception, e:
        configLogger.info("Exception occurred %s", e)
    finally:
        if cursor != None:
            cursor.close()

def putFileContentsInDB(outputFileName, dbConnection, modelTable, modelId, weight):
    """ Function to get the contents from output file and dump into the database

    :param outputFileName: Name of the file containing results of lp solver
    :type outputFileName: str
    :param dbConnection: Connection to database
    :type dbConnection: object of class 'Connection'
    :param modelTable: Name of the table containing models
    :type modelTable: str
    :param modelId: identifier of the model
    :type modelId: int
    """
    #read the output file
    outputFile = None
    try:
        outputFile = open(outputFileName, 'r')
        results = ""

        for outputLine in outputFile:
            # Append the line into results
            results += outputLine

        # Dump the results into db
        deleteDataFromDB(dbConnection, modelTable, modelId)
        storeResultsInDB(dbConnection, modelTable, modelId, results, weight)
    except:
        if outputFile is not None:
            outputFile.close()

def createAndStoreResultsInDB(lpOutputFileName, segNameMapFileName, adNameMapFileName, dbConnection, tableName, modelId, weight):
    """ Function to store the results from lpoutput file to DB

        Keyword arguments:
        lpOutputFileName -- Output of the LP program containing alpha and beta values. Input to this function
        segNameMapFile -- Segment to name mapping object
        adNameMapFile -- Ad to name mapping object
        dbConnection -- Connection to the database
        tableName -- Name of the table where to put in the results
        modelId -- integer id of this model
    """
    segNameMapFile = None
    adNameMapFile = None
    lpOutputFile = None
    results = ""
    try:
        segNameMapFile = open(segNameMapFileName, 'r')
        adNameMapFile = open(adNameMapFileName, 'r')
        segNameMapper = pickle.load(segNameMapFile)
        adNameMapper = pickle.load(adNameMapFile)

        lpOutputFile = open(lpOutputFileName, 'r')

        for lpOutLine in lpOutputFile:
            lpOutLine = lpOutLine.strip()
            tokens = lpOutLine.split('')
            if len(tokens) != 2:
                raise Exception("Mal-formed line = " + lpOutLine)
            column = tokens[0]
            value = tokens[1]
            if column[0] == 'A':
                # Corresponding to ad column
                results += 'a'
                adTargeting = adNameMapper.nameAdTargetingMap.get(column)
                if adTargeting is None:
                    raise Exception("Name " + column + " not found in the ad name mapper")
                results += str(adTargeting.identifier)
                results += ''
                value = (1 - alphaCut) * float(value)
                results += str(value)
                results += '\n'
            else:
                # Corresponding to segment column
                results += 'b'
                segment = segNameMapper.nameSegmentMap.get(column)
                if segment is None:
                    raise Exception("Name " + column + " not found in the segment name mapper")
                for dim in segment.dimension_list:
                    results += str(dim)
                    results += ''

                results += ''
                results += str(value)
                results += '\n'

        storeResultsInDB(dbConnection, tableName, modelId, results, weight)
    finally:
        if segNameMapFile is not None:
            segNameMapFile.close()
        if adNameMapFile is not None:
            adNameMapFile.close()
        if lpOutputFile is not None:
            lpOutputFile.close()

def storeResultsInFile(lpOutputFileName, feedOutputFileName, segNameMapFileName, adNameMapFileName, campaignList, unknownRatio, outputFieldsDelim, outputDimDelim, alphaCut):
    """ Function to store the results from lpoutput file to feed output file

        Keyword arguments:
        lpOutputFileName -- Output of the LP program containing alpha and beta values. Input to this function
        feedOutputFileName -- Output of this function. Feed file to be consumed by the online system.
        segNameMapFile -- Segment to name mapping object
        adNameMapFile -- Ad to name mapping object
        campaignList -- List of all the campaigns valid for bidding
        unknownRatio -- Fraction of original bid to go into alpha for campaigns whose alpha is not known
    """
    segNameMapFile = None
    adNameMapFile = None
    lpOutputFile = None
    feedOutputFile = None
    try:
        segNameMapFile = open(segNameMapFileName, 'r')
        adNameMapFile = open(adNameMapFileName, 'r')
        segNameMapper = pickle.load(segNameMapFile)
        adNameMapper = pickle.load(adNameMapFile)

        lpOutputFile = open(lpOutputFileName, 'r')
        feedOutputFile = open(feedOutputFileName, 'w')
        campaignsDone = {}

        for lpOutLine in lpOutputFile:
            lpOutLine = lpOutLine.strip()
            tokens = lpOutLine.split('')
            if len(tokens) != 2:
                raise Exception("Mal-formed line = " + lpOutLine)
            column = tokens[0]
            value = tokens[1]
            if column[0] == 'A':
                # Corresponding to ad column
                adTargeting = adNameMapper.nameAdTargetingMap.get(column)
                if adTargeting is None:
                    raise Exception("Name " + column + " not found in the ad name mapper")
                campaignsDone[adTargeting.identifier] = 1
                #feedOutputFile.write(str(adTargeting.identifier))
                for adId in adTargeting.adIds:
                    feedOutputFile.write('a')
                    feedOutputFile.write(outputDimDelim)
                    feedOutputFile.write(str(adId))
                    feedOutputFile.write(outputFieldsDelim)
                    ad_value = float(value) * (1 - alphaCut)
                    feedOutputFile.write(str(ad_value))
                    feedOutputFile.write('\n')
            else:
                # Corresponding to segment column
                feedOutputFile.write('b')
                feedOutputFile.write(outputDimDelim)
                segment = segNameMapper.nameSegmentMap.get(column)
                counter = 0
                if segment is None:
                    raise Exception("Name " + column + " not found in the segment name mapper")
                for dim in segment.dimension_list:
                    if counter != 0:
                        feedOutputFile.write(outputDimDelim)
                    counter += 1
                    feedOutputFile.write(str(dim))

                feedOutputFile.write(outputFieldsDelim)
                feedOutputFile.write(value)
                feedOutputFile.write('\n')

        for campaign in campaignList:
            campaignId = campaign.identifier
            bid = campaign.bid
            if campaignId not in campaignsDone:
                for adId in campaign.adIds:
                    feedOutputFile.write('a')
                    feedOutputFile.write(outputDimDelim)
                    feedOutputFile.write(str(adId))
                    feedOutputFile.write(outputFieldsDelim)
                    value = unknownRatio
                    feedOutputFile.write(str(value))
                    feedOutputFile.write('\n')
    finally:
        if segNameMapFile is not None:
            segNameMapFile.close()
        if adNameMapFile is not None:
            adNameMapFile.close()
        if lpOutputFile is not None:
            lpOutputFile.close()
        if feedOutputFile is not None:
            feedOutputFile.close()


if __name__ == '__main__':
    dbConnection = None
    outputDbConnection = None
    try:
        cfg = ConfigParams(sys.argv[1])

        lpOutputFileName = sys.argv[3]
        outputFileName = sys.argv[4]
        dbConnection = MySQLdb.connect(host=cfg.databaseHost, user=cfg.databaseUser, passwd=cfg.databasePassword, db=cfg.databaseDbName)
        campaignList = getCampaignsFromDB(dbConnection, cfg.timeDimension, cfg.timeWindowDuration)
        storeResultsInFile(lpOutputFileName, outputFileName, cfg.segmentMapFileName, cfg.adNameMapFileName, campaignList,cfg.unknownBidRatio, cfg.outputFieldsDelimiter, cfg.outputDimDelimiter, cfg.alphaCut)
        newFileName = outputFileName + '-' + strftime("%Y-%m-%d-%H-%M", gmtime())
        call(["cp", outputFileName, newFileName])
        outputDbConnection = MySQLdb.connect(host=cfg.outputDatabaseHost, user=cfg.outputDatabaseUser, passwd=cfg.outputDatabasePassword, db=cfg.outputDatabaseDbName)
        if cfg.dumpToDB != 0:
            putFileContentsInDB(outputFileName, outputDbConnection, cfg.modelTable, cfg.modelId, cfg.modelWeight)
    except Exception, e:
        appLogger.info('Run failed at %s %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), e)  
        sys.exit(1)
    finally:
        if dbConnection is not None:
            dbConnection.close()
        if outputDbConnection is not None:
            outputDbConnection.close()

    appLogger.info('Run succeeded at %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()))
    sys.exit(0)

