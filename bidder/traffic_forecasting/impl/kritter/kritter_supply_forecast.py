import traceback
from time import gmtime, strftime
import sys

from bidder.traffic_forecasting.conf_utils.conf_reader import *
from bidder.traffic_forecasting.core.algorithm.exponential_smoothing import *
from bidder.input_log_adapter.kritter_supply_forecast_input_log_adapter import *
from utils.log_configure.log_configure import *
from bidder.metadata_utils.dimension_id_name_map_reader import *
from bidder.metadata_utils.last_processed_time import *

loggerConfigLocation = sys.argv[2]
configure_log(loggerConfigLocation)
appLogger = logging.getLogger(__name__)

def getFilesToProcess(directoryPath, nextDateToProcess) :
    """ Get all the files to process from the given directory. Files that belong to the next date to process are returned

    Keyword arguments :
    directoryPath -- Name of the directory containing the input log files
    nextDateToProcess -- datetime object corresponding to the next date's files to be processed

    return List of files to be processed
    """
    fileNames = []
    for (dirpath, dirnames, filenames) in os.walk(directoryPath) :
        fileNames.extend(filenames)
        break

    nextDate = datetime.datetime.strptime(nextDateToProcess, "%Y-%m-%d")
    filesToProcess = []
    for fileName in fileNames :
        tokens = fileName.split('_')
        lastToken = tokens[len(tokens) - 1]
        tokens = lastToken.split('.')
        dateTimeString = tokens[0]
        dateTimeObj = datetime.datetime.strptime(dateTimeString, "%Y-%m-%d-%H-%M")
        if dateTimeObj.date() == nextDate.date() :
            filesToProcess.append(fileName)

    return filesToProcess

if __name__ == '__main__' :
    dbConnection = None
    cursor = None
    try :
        # Get log lines from the impression log file
        cfgParams = ConfigParams(sys.argv[1])
        dbConnection = MySQLdb.connect(host=cfgParams.database_host, user=cfgParams.database_user, passwd=cfgParams.database_password, db=cfgParams.database_dbname)
        tableName = cfgParams.dimension_id_name_table
        lastProcessedTableName = cfgParams.last_processed_table_name
        dateFormat = "%Y-%m-%d"
        lastProcessedTime = getLastProcessedTime(dbConnection, lastProcessedTableName, "supply_forecast", dateFormat)
        toProcessTime = None
        if lastProcessedTime is None :
            toProcessTime = cfgParams.to_process_time
        else :
            toProcessTimeObj = lastProcessedTime + datetime.timedelta(days = 1)
            toProcessTime = toProcessTimeObj.strftime(dateFormat)

        if toProcessTime is None :
            raise Exception("For what date is the forecast supposed to run next? Please specify in the config")
        filesToProcess = getFilesToProcess(cfgParams.impression_log_dir, toProcessTime)

        fullDimIdNameMap = getDimIdNameMap(dbConnection, tableName)
        fullDimNameIdMap = {}
        for dimId, dimName in fullDimIdNameMap.items() :
            fullDimNameIdMap[dimName] = dimId

        timeDimId = -1
        dimIdNameMap = {}
        for dim in cfgParams.dimensions :
            if dim.lower == 'time' :
                timeDimId = fullDimNameIdMap[dim]
            if dim in fullDimNameIdMap :
                dimIdNameMap[dim] = fullDimNameIdMap[dim]
            else :
                raise Exception("Dimension id " + dim + " not found in metadata")

        timeWindowLength = cfgParams.time_window_length
        algorithm = ExponentialSmoothingForecasting(filesToProcess, cfgParams.dim_delimiter, cfgParams.fields_delimiter)
        algorithm.createAndDumpForecast(cfgParams, KritterSupplyInputLogAdapter(dimIdNameMap, timeDimId, timeWindowLength))
        appLogger.info('Last successful run completed at %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()))

        cursor = dbConnection.cursor()
        if lastProcessedTime is None :
            insertQuery = "INSERT INTO " + lastProcessedTableName + " (process_name, last_date) VALUES ('supply_forecast', '" + toProcessTime + "')"
            cursor.execute(insertQuery)
        else :
            updateQuery = "UPDATE " + lastProcessedTableName + " SET last_date = '" + toProcessTime + "' WHERE process_name = 'supply_forecast'"
            cursor.execute(updateQuery)
        dbConnection.commit()
    except Exception, e:
        appLogger.info('Run failed at %s %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), traceback.format_exc())
        sys.exit(1)
    finally :
        if cursor is not None :
            cursor.close()
        if dbConnection is not None :
            dbConnection.close()
    sys.exit(0)

