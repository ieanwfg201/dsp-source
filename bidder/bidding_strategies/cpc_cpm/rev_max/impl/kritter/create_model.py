import MySQLdb
import os
from time import gmtime, strftime

from config import *
from fetch_data import *
from utils.log_configure.log_configure import *
from bidder.bidding_strategies.cpc_cpm.rev_max.algorithm.form_mps import *
from bidder.traffic_forecasting.core.utils.traffic_forecasting import *
from bidder.ctr_prediction.logistic_regression.feed_consumer.ctr_feed_consumer import *

loggerConfigLocation = sys.argv[2]
configure_log(loggerConfigLocation)
appLogger = logging.getLogger(__name__) 

if __name__ == '__main__':
    dbConnection = None
    cursor = None
    try:
        cfg = ConfigParams(sys.argv[1])

        segPrefix = 'S'
        adPrefix = 'A'
        forecastFileName = sys.argv[3]
        excludeDimensionValueList = []
        for i in range(0, cfg.supplyForecastDimCount):
            excludeDimensionValues = []
            if i == cfg.publisherIdPos:
                excludeDimensionValues.append(cfg.directPublisherId)
            excludeDimensionValueList.append(excludeDimensionValues)

        forecast = loadExistingForecastFileExcludeDimValues(forecastFileName, cfg.dimDelimiter, cfg.fieldsDelimiter, excludeDimensionValueList)

        # Load ad targeting and impression budgets from the DB.
        dbConnection = MySQLdb.connect(host=cfg.databaseHost, user=cfg.databaseUser, passwd=cfg.databasePassword, db=cfg.databaseDbName)
        campaignList = getCampaignsFromDB(dbConnection, cfg.timeDimension, cfg.timeWindowDuration)
        campaignIdList = []
        campaignBudget = {}
        for campaign in campaignList:
            # Get campaign id for the ad
            campaignId = campaign.identifier
            amountRemaining = campaign.amount
            bid = campaign.bid
            budgetRemaining = 0
            if bid > 0 and amountRemaining > 0:
                budgetRemaining = amountRemaining / bid
            campaignBudget[campaignId] = budgetRemaining * (1 - cfg.networkCutRatio)

        mpsOutputFile = sys.argv[4]

        # Load up the ctr feed into ctr predictor, i.e., the feed consumer
        logitCTRPredictor = LogitCTRPredictor()
        ctrDataStr = ""
        ctrFeedFileName = sys.argv[5]
        if(os.path.isfile(ctrFeedFileName)):
            with open(ctrFeedFileName, 'r') as ctrFeedFile:
                ctrDataStr = ctrFeedFile.read().strip()
        logitCTRPredictor.loadCTRFeedFromString(ctrDataStr, cfg.ctrDimensions, cfg.ctrDimCoeffDelimiter, 1)

        createMPS(campaignList, campaignBudget, forecast, cfg.dimensions, cfg.dimensionNames, mpsOutputFile, segPrefix, adPrefix, cfg.segmentMapFileName, cfg.adNameMapFileName, logitCTRPredictor, cfg.othersId, "", cfg.campaignIdDimName)

    except Exception, e:
        appLogger.info('Run failed at %s %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), e)  
        sys.exit(1)
    finally:
        if cursor is not None:
            cursor.close()
        if dbConnection is not None:
            dbConnection.close()

    appLogger.info('Run succeeded at %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()))
    sys.exit(0)
