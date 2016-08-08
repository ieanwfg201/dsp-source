import logging
import base64
import os
import shutil
import sys
import traceback
from thrift.protocol import TBinaryProtocol
from thrift.transport import TTransport

import data_structs.AdStats.ttypes as AdStats

noAdsCountryCarrierOsBrand = 'NO_ADS_COUNTRY_CARRIER_OS_BRAND'

class AdStatsFileProcessor:
    """
    Class to process one file containing ad stats logs
    """

    def __init__(self, inputFilePath, doneFileDir, dbConnection):
        """
        Constructor for file processor
        :param inputFilePath: Ad stats log thrift file to be processed
        :type inputFilePath: str
        :param doneFileDir: directory in which to put the file once it is processed
        :type doneFileDir: str
        :param dbConnection: connection to db in which to put the results
        :type dbConnection: MySQLdb.Connection
        """
        self.appLogger = logging.getLogger(__name__)
        self.inputFilePath = inputFilePath
        self.doneFileDir = doneFileDir
        self.dbConnection = dbConnection


    def process(self):
        """
        Processes the input file, updates db and moves the file to the output location
        :return:
        """
        with open(self.inputFilePath) as inputFile:
            for line in inputFile:
                self.appLogger.debug("Processing line : %s", line)
                line = line.strip()
                if line == '':
                    continue
                decodedLine = base64.b64decode(line)
                transport = TTransport.TMemoryBuffer(decodedLine)
                protocol = TBinaryProtocol.TBinaryProtocol(transport)
                adStats = AdStats.AdStats()
                adStats.read(protocol)
                self.processAdStatsObject(adStats)

        # Move the file to output directory
        fileName = os.path.basename(self.inputFilePath)
        shutil.move(self.inputFilePath, self.doneFileDir + fileName)


    def processAdStatsObject(self, adStats):
        """

        :param adStats: AdStats object representing a log line
        :type adStats: AdStats.AdStats
        :return:
        """
        query = "INSERT INTO ad_no_fill_reason (ad_id, no_fill_reason_name, request_time, processing_time, count, " \
                "last_modified) VALUES (%s, %s, FROM_UNIXTIME(%s), now(), %s, now()) ON DUPLICATE KEY UPDATE count = " \
                "count + VALUES(count)"

        self.dbConnection.autocommit(False)
        cursor = None
        self.appLogger.debug("Going to update database for ad stats object : %s", adStats.__repr__())

        try:
            cursor = self.dbConnection.cursor()
            for adStatsHourTimestamp, adNofillMap in adStats.adStatsMap.iteritems():
                hourTimestamp = adStatsHourTimestamp / 1000
                self.appLogger.info("Updating for hour time stamp : %s", hourTimestamp)
                totalRequests = adStats.hourTotalRequestsMap[adStatsHourTimestamp]
                for adId, adToNofillReasonCount in adNofillMap.iteritems():
                    nofillReasonCountMap = adToNofillReasonCount.nofillReasonCount
                    adRequests = 0
                    for nofillReason, count in nofillReasonCountMap.iteritems():
                        adRequests += count
                        cursor.execute(query, (adId, nofillReason, hourTimestamp, count))
                        self.appLogger.info("Updating ad_no_fill_reason with ad id : %s, no fill reason : %s, hour "
                                            "time stamp : %s, count % s", adId, nofillReason, hourTimestamp,
                                            count)

                    if totalRequests != adRequests:
                        cursor.execute(query, (adId, noAdsCountryCarrierOsBrand, hourTimestamp, totalRequests - adRequests))
                        self.appLogger.info("Updating ad_no_fill_reason with ad id : %s, no fill reason %s: , hour "
                                        "time stamp : %s, count % s", adId, noAdsCountryCarrierOsBrand,
                                        hourTimestamp, totalRequests - adRequests)

            self.dbConnection.commit()
        except Exception, e:
            # rollback the transaction in case of an exception
            self.dbConnection.rollback()
            self.appLogger.error("Exception caught : %s", traceback.format_exc())
            sys.exit(1)
        finally:
            if cursor is not None:
                cursor.close()
