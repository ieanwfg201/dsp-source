from bidder.utils.log_utils import *

def sortAndConsolidateClickLogs(clickLogs):
    """
    Removes duplicate click logs from the logs and consolidates them. Duplicates are decided on the basis of dimensions
    Two log lines having the same dimensions are marked as duplicate and their impressions and clicks are added.

    :param clickLogs: click logs list
    :type clickLogs: list of ClickLogLine objects
    :returns: click logs sorted and consolidated
    :rtype: list of ClickLogLine objects
    """
    if clickLogs is None or len(clickLogs) == 0:
        return clickLogs

    resultClickLogs = []
    sortedClickLogs = sorted(clickLogs, clickLogLineComparator)
    prevClickLog = sortedClickLogs[0]
    for i in range(1, len(sortedClickLogs)):
        currentClickLog = sortedClickLogs[i]
        compResult = compLists(prevClickLog.dimensionList, currentClickLog.dimensionList)
        if compResult == 0:
            # Both are same. consolidate
            prevClickLog.impressions += currentClickLog.impressions
            prevClickLog.clicks += currentClickLog.clicks
        else:
            resultClickLogs.append(prevClickLog)
            prevClickLog = currentClickLog
    resultClickLogs.append(prevClickLog)
    return resultClickLogs

def parseLogLinesToObjects(logLines, dimDelimiter, fieldsDelimiter, logger, optionalDelimiter='=') :
    objects = []
    for line in logLines :
        fields = line.split(fieldsDelimiter)
        dimensionString = fields[0]
        if len(fields) != 3 :
            logger.error(Exception("Wrong line"))
            continue
        impressionCount = float(fields[1])
        clickCount = float(fields[2])
        dimFields = dimensionString.split(dimDelimiter)
        logLine = ClickLogLine([], clickCount, impressionCount)
        for dim in dimFields :
            actualDims = dim.split(optionalDelimiter)
            if len(actualDims) == 2:
                if actualDims[1].strip() == '':
                    dimension = -1
                else:
                    dimension = int(actualDims[1])
            else:
                dimension = int(actualDims[0])
            logLine.dimensionList.append(dimension)
        objects.append(logLine)
    return objects

