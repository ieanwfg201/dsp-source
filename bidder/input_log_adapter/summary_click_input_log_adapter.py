import logging
import time
import gzip

from generic_input_log_adapter import *

adapterLogger = logging.getLogger(__name__)

class SummaryClickInputLogAdapter(GenericInputLogAdapter):
    """
    Class to translate the click summaries to logs as understood by the CTR prediction module
    """
    def __init__(self, dimDelimiter, inDelimiter, dimensions, fieldsDelimiter, timeDimension, timePerWindow, impressionFieldPos, clickFieldPos, isGzipped=0):
        """
        Constructor for the adapter class

        :param dimDelimiter: Dimension delimiter
        :type dimDelimiter: str
        :param fieldsDelimiter: Fields delimiter
        :type fieldsDelimiter: str
        :param dimensions: list containing which all positions from the summary line are useful
        :type dimensions: list (int)
        :param inDelimiter: Input dimension delimiter. Delimiter in the input file
        :type inDelimiter: str
        :param timeDimension: id of time dimension
        :type timeDimension: int
        :param timePerWindow: Length of one unit of time window(in minutes)
        :type timePerWindow: int
        :param impressionFieldPos: Position in the fields where impression is found in the summary
        :type impressionFieldPos: int
        :param clickFieldPos: Position in the fields where click is found in the summary
        :type clickFieldPos: int
        :param isGzipped: If the input file(s) is(are) gzipped. 0 for not gzipped and 1 for gzipped
        :type isGzipped: int
        """
        self.dimDelimiter = dimDelimiter
        self.inDelimiter = inDelimiter
        self.dimensions = dimensions
        self.fieldsDelimiter = fieldsDelimiter
        self.timeDimension = timeDimension
        self.timePerWindow = timePerWindow
        self.impressionFieldPos = impressionFieldPos
        self.clickFieldPos = clickFieldPos
        self.isGzipped = isGzipped

    def parseLogLine(self, line):
        """
        Parses the line given from the summary and returns a delimiter separated string.

        :param line: summary line
        :type line: str
        :returns: delimiter separated representation of summary line
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

                if i != len(self.dimensions) - 1:
                    resStr += self.dimDelimiter

            resStr += self.fieldsDelimiter
            resStr += tokens[self.impressionFieldPos]
            resStr += self.fieldsDelimiter
            resStr += tokens[self.clickFieldPos]
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
        inFile = None
        try:
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
        except:
            adapterLogger.error("File not found or something wrong with file " + fileName)
        finally:
            if inFile is not None:
                inFile.close()

        return logLineList

