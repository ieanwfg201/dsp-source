import logging
import datetime
import base64

from bidder.utils.log_utils import *
from data.OnlineBidderLogThrift.ttypes import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TTransport
from generic_input_log_adapter import *

adapterLogger = logging.getLogger(__name__)

class KritterSupplyInputLogAdapter(GenericInputLogAdapter) :
    def __init__(self, dimIdNameMap, timeDimension = -1, timePerWindow = 0, dimDelimiter = ',', fieldsDelimiter = u'\u0001') :
        """ Constructor

            Keyword arguments :
            dimIdNameMap -- Map containing the ids->name mapping of the 
                            dimensions to be extracted
            timeDimension -- The dimension id of time (if present). Special
                             handling is required for time
            timePerWindow -- If time is one of the dimensions, the length (in 
                             minutes) of each time window
            """
        self.dimIdNameMap = dimIdNameMap
        self.timeDimension = timeDimension
        self.timePerWindow = timePerWindow
        self.dimDelimiter = dimDelimiter
        self.fieldsDelimiter = fieldsDelimiter

    def getDimensionList(self, requestResponse, dimDelimiter) :
        """ Function to extract the dimensions specified in dimIdNameMap from
            requestResponse object and return a string with the corresponding 
            values.  
            For e.g., the dimIdNameMap looks like : {10:'country', 5:'carrier',
            3:'OS'} 
            The function will extract country, carrier and OS from the 
            requestResponse object and return a string as follows :
            10=91<dimDelimiter>5=1<dimDelimiter>3=2
            where 91 is the country id for 'India', 1 is the carrier id for 
            'Airtel' and 2 is the os id for 'Android'. 
            *** All values for illustration only ***

            Keyword arguments :
            requestReponse -- Request response object corresponding to a log 
                              line
            dimDelimiter -- The delimiter between dimensions in the final string
                            to be returned

            return string of the form <dimension id>=<dimension value>...
            """
        dimValueList = self.dimIdNameMap.keys()
        nameValueMap = logDimensionExtractor(requestResponse, dimValueList)
        resStr = ""
        counter = 0
        for dimName, dimId in self.dimIdNameMap.items() :
            result = str(dimId) + "="
            if dimId != self.timeDimension :
                value = nameValueMap[dimName]
                if value is not None :
                    result += str(value)
                else :
                    result += str(-1)
            elif self.timePerWindow > 0 :
                # Get time from the value
                v = nameValueMap[dimName]
                if v is not None :
                    value = long(v)
                    logStampDateTime = datetime.datetime.fromtimestamp(value)
                    totalMinutesElapsed = logStampDateTime.hour * 60 + logStampDateTime.minute
                    currentTimeWindow = totalMinutesElapsed / self.timePerWindow 
                    result += str(currentTimeWindow)
                else :
                    result += str(-1)

            if counter != 0 :
                resStr += dimDelimiter
            else :
                counter += 1
            resStr += result
            
        return resStr

    def getFields(self, requestResponse, dimDelimiter, fieldsDelimiter) :
        dimensions = self.getDimensionList(requestResponse, dimDelimiter)
        fieldList = dimensions + fieldsDelimiter
        fieldList += fieldsDelimiter
        fieldList += '1'
        return fieldList

    def parseLogLine(self, line) :
        """ base64 decode the line """
        decodedLine = base64.b64decode(line)
        # Deserialize using binary protocol
        transport = TTransport.TMemoryBuffer(decodedLine)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)
        requestResponse = OnlineBidderRequestResponse()
        requestResponse.read(protocol)
        x = self.getFields(requestResponse, self.dimDelimiter, self.fieldsDelimiter)
        return x

    def readLogsFromFile(self, fileName) :
        logLinesList = []
        try :
            with open(fileName) as infile :
                for line in infile :
                    a = self.parseLogLine(line)
                    logLinesList.append(a)
        except Exception, e:
            # Log file not found, something is wrong
            adapterLogger.error("Log file '%s' not found. Please check the log file", fileName)
        return logLinesList


if __name__ == '__main__' :
    dimensionIdNameMap = {1:'country', 2:'carrier', 5:'time'}
    a = KritterSupplyInputLogAdapter(dimensionIdNameMap, 5, 50)
    logLines = a.readLogsFromFile('imp3')
    for logLine in logLines:
        print logLine
