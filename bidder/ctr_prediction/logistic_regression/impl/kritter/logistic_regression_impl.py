import traceback
import glob
from subprocess import call
import sys
from time import gmtime, strftime

from utils.log_configure.log_configure import *
from bidder.ctr_prediction.logistic_regression.utils.config import *
from bidder.metadata_utils.dimension_id_name_map_reader import *
from bidder.input_log_adapter.summary_click_input_log_adapter import *
from bidder.ctr_prediction.logistic_regression.utils.click_log_utils import *
from bidder.ctr_prediction.logistic_regression.core.logistic_regression_history import *
from bidder.ctr_prediction.logistic_regression.core.logistic_regression_input_creator import *
from bidder.ctr_prediction.logistic_regression.core.logistic_regression_solver import *
from bidder.ctr_prediction.logistic_regression.core.convert_r_lr_results import *

loggerConfigLocation = sys.argv[2]
configure_log(loggerConfigLocation)
appLogger = logging.getLogger(__name__)

def deleteDataFromDB(dbConnection, tableName, modelId):
    """ Function to delete the model data from database.

        Keyword arguments :
        dbConnection -- Connection to the database
        tableName -- Name of the table where to put in the results
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
        else :
            # No entry exists. Create a new entry
            query = "INSERT INTO " + tableName + " (id, data, last_modified) VALUES (" + str(modelId) + ", \"\", NOW())"

        cursor.execute(query)
        dbConnection.commit()
    except Exception, e:
        appLogger.info("Exception occurred %s", traceback.format_exc())
        raise e
    finally:
        if cursor is not None:
            cursor.close()

def storeResultsInDB(dbConnection, tableName, modelId, results):
    """ Function to store the results in database

        Keyword arguments :
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
        else :
            # No entry exists. Create a new entry
            query = "INSERT INTO " + tableName + " (id, data, last_modified) VALUES (" + str(modelId) + ", \"" + str(results) + "\", NOW())"

        cursor.execute(query)
        dbConnection.commit()
    except Exception, e:
        appLogger.info("Exception occurred %s", traceback.format_exc())
    finally:
        if cursor is not None:
            cursor.close()


def putFileContentsInDB(outputFileName, dbConnection, modelTable, modelId, weight, timeWindowLength, allVariables, categoricalVariables, absoluteCTRCeiling = 1.0):
    """ Function to get the contents from output file and dump into the database

    :param outputFileName: Name of the file containing results of lp solver
    :type outputFileName: str
    :param dbConnection: Connection to database
    :type dbConnection: object of class 'Connection'
    :param modelTable: Name of the table containing models
    :type modelTable: str
    :param modelId: identifier of the model
    :type modelId: int
    :param timeWindowLength: Length of a time window. This shall be prepended to the feed file
    :type timeWindowLength: int
    :param absoluteCTRCeiling: Maximum possible value for CTR. To be used by the online system,
    :type absoluteCTRCeiling: float
    """
    #read the output file
    outputFile = None
    try:
        outputFile = open(outputFileName, 'r')

        #results = 'lines:3\n'
        #results += 'absolute_ctr_ceiling:' + str(absoluteCTRCeiling) + '\n'
        #results += 'variables:' + str(allVariables)[1:-1] + '\n'
        #results += 'categorical_variables:' + str(categoricalVariables)[1:-1] + '\n'
        #results += outputFile.read()
        results = outputFile.read()
        """
        for outputLine in outputFile:
            # Append the line into results
            results += outputLine
        """

        # Delete model data from db
        deleteDataFromDB(dbConnection, modelTable, modelId)

        # Dump the results into db
        storeResultsInDB(dbConnection, modelTable, modelId, results)
    except:
        if outputFile is not None:
            outputFile.close()

if __name__ == '__main__':
    dbConnection = None
    outputDbConnection = None
    logisticRegressionOutputFile = None
    try:
        config = ConfigParams(sys.argv[1])
        dbConnection = MySQLdb.connect(host=config.database_host, user=config.database_user, passwd=config.database_password, db=config.database_dbname)
        tableName = config.dimension_id_name_table

        print '1 = ' + sys.argv[1]
        print '2 = ' + sys.argv[2]
        print '3 = ' + sys.argv[3]
        print '4 = ' + sys.argv[4]
        print '5 = ' + sys.argv[5]
        print '6 = ' + sys.argv[6]
        print '7 = ' + sys.argv[7]
        print '8 = ' + sys.argv[8]
        print '9 = ' + sys.argv[9]
        dirRegex = sys.argv[3]
        filesToProcess = glob.glob(dirRegex)

        fullDimIdNameMap = getDimIdNameMap(dbConnection, tableName)
        fullDimNameIdMap = {}
        for dimId, dimName in fullDimIdNameMap.items():
            fullDimNameIdMap[dimName] = dimId

        timeDimId = -1
        dimIdNameMap = {}
        dimList = []
        for dim in config.dimensions:
            if dim == config.time_dimension:
                timeDimId = fullDimNameIdMap[dim]
            if dim in fullDimNameIdMap:
                dimIdNameMap[dim] = fullDimNameIdMap[dim]
                dimList.append(fullDimNameIdMap[dim])
            else:
                raise Exception("Dimension id " + dim + " not found in metadata")

        timeWindowLength = config.time_window_length

        #Read all the files one by one and put into log lines
        logLines = []
        clickLogAdapter = SummaryClickInputLogAdapter(config.dimension_delimiter, config.input_delimiter, dimList, config.fields_delimiter, timeDimId, config.time_window_length, config.impression_pos, config.click_pos, 1)
        for fileName in filesToProcess:
            logLines.extend(clickLogAdapter.readLogsFromFile(fileName))

        logLineObjects = parseLogLinesToObjects(logLines, config.dimension_delimiter, config.fields_delimiter, adapterLogger)
        sortedAndConsolidatedLogLineObjects = sortAndConsolidateClickLogs(logLineObjects)

        # Load up existing click log segments (historical)
        existingClickLogSegmentsFileName = sys.argv[4]
        existingClickLogLines = loadExistingClickLogSegments(existingClickLogSegmentsFileName, config.dimension_delimiter, config.fields_delimiter)

        # Merge the existing click logs with the new click logs
        mergedClickLogs = mergeExistingNewClickLogs(sortedAndConsolidatedLogLineObjects, existingClickLogLines, config.decay_factor)

        # Dump the merged click logs to a backup file
        nextFileName = sys.argv[5]
        mergedClickLogs = dumpClickLogSegments(nextFileName, mergedClickLogs, config.dimension_delimiter, config.fields_delimiter, config.impression_threshold)

        # Prepare the CSV file for logistic regression input
        logisticRegressionClickLogs = filterAndConsolidateLogs(mergedClickLogs, config.rel_thresholds, config.abs_thresholds, config.internal_others_id)
        csvFileName = sys.argv[6]
        csvEmpty = dumpCSVForLogisticRegression(logisticRegressionClickLogs, config.dimensions, csvFileName, config.objective_name, config.segment_impression_count, config.internal_others_id)

        rScriptFileName = sys.argv[7]
        rOutputFileName = config.r_output_file_name

        num_r_lr_dimensions = len(config.r_lr_dimensions)
        num_r_lr_factors = len(config.r_lr_factors)
        call(["rm", "-f", config.all_factors_file_name, rOutputFileName])
        # Call the r module to process the csv data and run logistic regression on it
        status = call(["Rscript", rScriptFileName, config.objective_name, csvFileName, rOutputFileName, config.all_factors_file_name, str(num_r_lr_dimensions)] + config.r_lr_dimensions + [str(num_r_lr_factors)] + config.r_lr_factors)
        if status != 0:
            appLogger.info('Run failed at %s. Regression did not converge.', strftime("%Y-%m-%d %H:%M:%S", gmtime()))
            sys.exit(1)

        logisticRegressionOutputFileName = sys.argv[8]
        #solveLogisticRegression(csvFileName, logisticRegressionOutputFileName, None, config.objective_name, config.output_delimiter, config.internal_others_id, config.others_id, csvEmpty)
        converted_string = read_file_and_convert(rOutputFileName, config.all_factors_file_name, 1, config.intercept_str, config.out_intercept_str, ',', '_', config.output_delimiter, str(config.internal_others_id), str(config.others_id))
        logisticRegressionOutputFile = open(logisticRegressionOutputFileName, 'w')
        if converted_string is not None:
            logisticRegressionOutputFile.write(converted_string)
            logisticRegressionOutputFile.close()
            logisticRegressionOutputFile = None

        if config.dumpToDB != 0:
            outputDbConnection = MySQLdb.connect(host=config.outputDatabaseHost, user=config.outputDatabaseUser, passwd=config.outputDatabasePassword, db=config.outputDatabaseDbName)
            putFileContentsInDB(logisticRegressionOutputFileName, outputDbConnection, config.modelTable, config.modelId, config.modelWeight, config.time_window_length, config.r_lr_dimensions, config.r_lr_factors, config.absolute_ctr_ceiling)

        # Take the output file and create(replace if existing) symlink to it.
        outputSymLink = sys.argv[9]
        status = call(["ln", "-snf", logisticRegressionOutputFileName, outputSymLink])
        if status != 0:
            appLogger.info('Run failed at %s. Symlink creation failed. Output file : %s, symlink : %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), logisticRegressionOutputFileName, outputSymLink)
            sys.exit(1)
    except Exception, e:
        appLogger.info('Run failed at %s. Traceback : %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), traceback.format_exc())
        sys.exit(1)
    finally:
        if dbConnection is not None:
            dbConnection.close()
        if outputDbConnection is not None:
            outputDbConnection.close()
        if logisticRegressionOutputFile is not None:
            logisticRegressionOutputFile.close()

    appLogger.info('Successfully run at %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()))
    sys.exit(0)
