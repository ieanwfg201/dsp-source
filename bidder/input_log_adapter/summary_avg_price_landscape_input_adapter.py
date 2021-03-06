import logging
import time
import gzip

from generic_input_log_adapter import *

adapterLogger = logging.getLogger(__name__)

class SummaryAvgPriceLandscapeInputAdapter(GenericInputLogAdapter):
    def __init__(self, dimDelimiter, fieldsDelimiter, dimensions, inDelimiter, winCountFieldPos, totalWinBidFieldPos, timeDimension = -1, timePerWindow = 0, isGzipped = 1):
        """  Constructor

            :param dimDelimiter: Dimension delimiter
            :type dimDelimiter: str
            :param fieldsDelimiter: Fields delimiter
            :type fieldsDelimiter: str
            :param dimensions: list containing which all positions from the summary line are useful
            :type dimensions: list (int)
            :param inDelimiter: Input dimension delimiter. Delimiter in the input file
            :type inDelimiter: str
            :param winCountFieldPos: Position in the fields where total win count is found in the summary
            :type winCountFieldPos: int
            :param totalWinBidFieldPos: Position in the fields where total win bid amount is found in the summary
            :type totalWinBidFieldPos: int
            :param timeDimension: id of time dimension
            :type timeDimension: int
            :param timePerWindow: Length of one unit of time window(in minutes)
            :type timePerWindow: int
        """
        self.dimDelimiter = dimDelimiter
        self.inDelimiter = inDelimiter
        self.dimensions = dimensions
        self.fieldsDelimiter = fieldsDelimiter
        self.winCountFieldPos = winCountFieldPos
        self.totalWinBidFieldPos = totalWinBidFieldPos
        self.timeDimension = timeDimension
        self.timePerWindow = timePerWindow
        self.isGzipped = isGzipped

    def parseLogLine(self, line):
        """ Parses the given line and returns a string for the log line

            :param line: summary line
            :type line : str
            :returns: Delimiter separated representation of the summary line
            :rtype: str
        """
        tokens = line.split(self.inDelimiter)
        resStr = ""
        try:
            for i in range(0, len(self.dimensions)):
                dimension = self.dimensions[i]
                resStr += str(dimension)
                resStr += '='
                if dimension != self.timeDimension:
                    resStr += tokens[dimension]
                elif self.timePerWindow > 0:
                    # Convert the time to the window number
                    # Time is in format yyyy-MM-dd-hh-mm
                    t = time.strptime(tokens[dimension], '%Y-%m-%d-%H-%M')
                    # Calculated the number of elapsed minutes since the beginning of day. And get the window
                    elapsedMinutes = t.tm_hour * 60 + t.tm_min
                    window = elapsedMinutes / self.timePerWindow
                    resStr += str(window)

                if i != len(self.dimensions) - 1 :
                    resStr += self.dimDelimiter

            resStr += self.fieldsDelimiter
            resStr += tokens[self.winCountFieldPos]
            resStr += self.fieldsDelimiter
            resStr += tokens[self.totalWinBidFieldPos]
        except Exception, e:
            adapterLogger.error('Malformed line in summary ' + line + ' error = ' + e)
            resStr = ''

        return resStr

    def readLogsFromFile(self, fileName):
        """ Reads logs from file and returns the list of logs to be consumed by subsequent systems

            :param fileName: Summary log file name
            :type fileName: str
            :returns: list of log lines
            :rtype: list of str
        """
        logLineList = []
        try :
            if self.isGzipped == 1:
                inFile = gzip.open(fileName, 'r')
                for line in inFile:
                    a = self.parseLogLine(line)
                    logLineList.append(a)
            else:
                with open(fileName) as inFile :
                    for line in inFile :
                        a = self.parseLogLine(line)
                        logLineList.append(a)
        except :
            adapterLogger.error("File not found or something wrong with file " + fileName)

        return logLineList


if __name__ == '__main__':
    # Test
    # Create a random summary log file
    dimDelimiter = ','
    fieldsDelimiter = str(u'\u0001')
    inDelimiter = str(u'\u0001')
    dimensions = [3, 0, 4, 5]
    line = ''
    for i in range(0, 10) :
        line += str(i)
        line += inDelimiter

    line += '12.9423'

    obj = SummaryAvgPriceLandscapeInputAdapter(dimDelimiter, fieldsDelimiter, dimensions, inDelimiter)
    res = obj.parseLogLine(line)
    print res

    line = ''
    for i in range(0, 4) :
        line += str(i)
        line += inDelimiter

    line += '12.9423'

    obj = SummaryAvgPriceLandscapeInputAdapter(dimDelimiter, fieldsDelimiter, dimensions, inDelimiter)
    res = obj.parseLogLine(line)
    print res
