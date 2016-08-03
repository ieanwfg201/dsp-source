import traceback
import glob
from time import gmtime, strftime
from subprocess import call
import sys

from bidder.traffic_forecasting.conf_utils.conf_summary import *
from bidder.traffic_forecasting.core.algorithm.exponential_smoothing import *
from bidder.input_log_adapter.summary_supply_input_log_adapter import *
from utils.log_configure.log_configure import *
from bidder.metadata_utils.dimension_id_name_map_reader import *
from bidder.metadata_utils.last_processed_time import *

loggerConfigLocation = sys.argv[2]
configure_log(loggerConfigLocation)
appLogger = logging.getLogger(__name__)


if __name__ == '__main__' :
    dbConnection = None
    try :
        cfgParams = ConfigParams(sys.argv[1])
        dbConnection = MySQLdb.connect(host=cfgParams.database_host, user=cfgParams.database_user, passwd=cfgParams.database_password, db=cfgParams.database_dbname)
        tableName = cfgParams.dimension_id_name_table
        #dateFormat = "%Y-%m-%d"

        # Get the files to process.
        #curDate = strftime(dateFormat)
        #curDateRegex = curDate + '*'
        #dirRegex = cfgParams.summary_dir + '/' + curDateRegex + '/' + cfgParams.intermediate_dir + '/' + curDateRegex + '/' + curDateRegex
        dirRegex = sys.argv[3]

        filesToProcess = glob.glob(dirRegex)

        fullDimIdNameMap = getDimIdNameMap(dbConnection, tableName)
        fullDimNameIdMap = {}
        for dimId, dimName in fullDimIdNameMap.items() :
            fullDimNameIdMap[dimName] = dimId

        timeDimId = -1
        dimIdNameMap = {}
        dimList = []
        for dim in cfgParams.dimensions:
            if dim.lower() == 'time':
                timeDimId = fullDimNameIdMap[dim]
            if dim in fullDimNameIdMap :
                dimIdNameMap[dim] = fullDimNameIdMap[dim]
                dimList.append(fullDimNameIdMap[dim])
            else:
                raise Exception("Dimension id " + dim + " not found in metadata")

        timeWindowLength = cfgParams.time_window_length
        cfgParams.output_forecast = sys.argv[4]
        algorithm = ExponentialSmoothingForecasting(filesToProcess, cfgParams.dim_delimiter, cfgParams.fields_delimiter)
        algorithm.createAndDumpForecast(cfgParams, SummarySupplyInputLogAdapter(cfgParams.dim_delimiter, cfgParams.fields_delimiter, dimList, str(u'\u0001'), int(cfgParams.request_pos_summary), timeDimId, timeWindowLength))

        dbConnection.commit()

        # Take the output file and create(replace if existing) symlink to it.
        outputSymLink = sys.argv[5]
        status = call(["ln", "-snf", cfgParams.output_forecast, outputSymLink])
        if status != 0:
            appLogger.info('Run failed at %s. Symlink creation failed. Output file : %s, symlink : %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), cfgParams.output_forecast, outputSymLink)
            sys.exit(1)
        appLogger.info('Last successful run completed at %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()))
    except Exception, e:
        appLogger.info('Run failed at %s %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), traceback.format_exc())
        sys.exit(1)
    finally :
        if dbConnection is not None :
            dbConnection.close()
    sys.exit(0)
