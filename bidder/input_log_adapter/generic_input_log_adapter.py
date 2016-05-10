from abc import ABCMeta, abstractmethod

class GenericInputLogAdapter(object) :
    """ Interface for the generic log adapter. Any log adapter has to
    implement the method parseLogLine in order to be consumed by the
    subsequent modules """
    __metaclass__ = ABCMeta

    @abstractmethod
    def parseLogLine(self, line) :
        """ The actual method that parses the log line. 'line' must be
            a string representing the log line

            :param line: Log line
            :type line: str
            :returns: Delimiter separated representation of the log line
            :rtype: str
        """
        pass

    @abstractmethod
    def readLogsFromFile(self, fileName) :
        """ Given a log file, returns a list of parsed log lines. The
            parsed log lines are strings

            :param fileName: Name of the file containing the lines
            :type fileName: str
            :returns: List of delimiter separated strings
            :rtype: list of str
        """
        pass
