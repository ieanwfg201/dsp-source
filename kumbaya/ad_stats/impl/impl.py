import MySQLdb
import traceback
import sys
import logging
import glob

import utils.log_configure.log_configure as log_configure
import kumbaya.ad_stats.utils.config as config
from adstats_file_processor import AdStatsFileProcessor

if __name__ == '__main__':
    loggerConfigLocation = sys.argv[2]
    log_configure.configure_log(loggerConfigLocation)
    appLogger = logging.getLogger(__name__)

    configLocation = sys.argv[1]
    config = config.ConfigParams(configLocation)

    inputFileRegex = sys.argv[3]
    doneFileDir = sys.argv[4]

    outputDBConnection = None
    try:
        outputDBConnection = MySQLdb.connect(host=config.outputDBHost, user=config.outputDBUserName,
                                             passwd=config.outputDBPassword, db=config.outputDBName)

        # list all the files in the input file regex and process them one by one
        filesToProcess = glob.glob(inputFileRegex)
        for fileName in filesToProcess:
            fileProcessor = AdStatsFileProcessor(inputFilePath=fileName, doneFileDir=doneFileDir,
                                                 dbConnection=outputDBConnection)
            fileProcessor.process()
    except Exception, e:
        appLogger.error("Exception occurred in main : %s", traceback.format_exc())
        sys.exit(1)
    finally:
        if outputDBConnection is not None:
            outputDBConnection.close()
