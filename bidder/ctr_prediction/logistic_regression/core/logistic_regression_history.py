import logging

from bidder.utils.log_utils import *


def loadExistingClickLogSegments(clickLogSegmentFileName, dimensionDelimiter, fieldsDelimiter):
    """
    Loads existing click log segments. The segments are present in the filename supplied as input.
    The click log segments are present in the following form :
    <dim value 1><delim1><dim value 2><delim1><dim value 3>...<dim value n><delim2><clicks><delim2><impressions>

    :param clickLogSegmentFileName: File containing the click logs. Segments are stored per line
    :type clickLogSegmentFileName: str
    :param dimensionDelimiter: delimiter between segment values
    :type dimensionDelimiter: str
    :param fieldsDelimiter: delimiter between segment values and impressions and clicks
    :type fieldsDelimiter: str
    :returns: list of click log line objects, corresponding to the lines in the file. Malformed lines are ignored
    :rtype: list of ClickLogLine objects
    """
    # Read the input file line by line
    segmentClickLogLines = []
    logger = logging.getLogger(__name__)
    try:
        with open(clickLogSegmentFileName) as clickLogSegmentFile:
            for line in clickLogSegmentFile:
                # get the line from the file into click log line object
                fields = line.split(fieldsDelimiter)
                # the count of fields should be 3
                if len(fields) != 3:
                    logger.error("Malformed line in " + clickLogSegmentFileName + ", culprit line = " + line)
                    continue

                dimensionStr = fields[0]
                clicks = float(fields[1])
                impressions = float(fields[2])
                segmentValueTokens = dimensionStr.split(dimensionDelimiter)
                segmentDimValues = []
                for segmentValueToken in segmentValueTokens:
                    segmentDimValues.append(int(segmentValueToken))

                clickLogLine = ClickLogLine(segmentDimValues, clicks, impressions)
                segmentClickLogLines.append(clickLogLine)
    except:
        # File doesn't exist, no need to do anything
        pass

    return segmentClickLogLines

def dumpClickLogSegments(clickLogSegmentFileName, clickLogSegments, dimensionDelimiter, fieldsDelimiter, impressionThreshold):
    """
    Function to dump the click log segments in the click log segment file name.
    The file format is the following :
    <dim value 1><delim1><dim value 2><delim1><dim value 3>...<dim value n><delim2><clicks><delim2><impressions>

    :param clickLogSegmentFileName: name of the file in which the click log segments have to be dumped
    :type clickLogSegmentFileName: str
    :param clickLogSegments: segments to be dumped in the file
    :type clickLogSegments: list of ClickLogLine objects
    :param dimensionDelimiter: delimiter between the various values of dimensions in a segment
    :type dimensionDelimiter: str
    :param fieldsDelimiter: delimiter between fields
    :type fieldsDelimiter: str
    :param impressionThreshold: number of impressions below which the log line must be discarded
    :type impressionThreshold: float
    :returns: Click log segments that pass the threshold
    :type: list
    """
    logger = logging.getLogger(__name__)
    clickLogSegmentFile = open(clickLogSegmentFileName, 'w')
    thresholdedClickLogSegments = []
    for clickLogSegment in clickLogSegments:
        dimensions = clickLogSegment.dimensionList
        clicks = clickLogSegment.clicks
        impressions = clickLogSegment.impressions
        if impressions < impressionThreshold:
            continue

        thresholdedClickLogSegments.append(clickLogSegment)
        dimLength = len(dimensions)
        for i in range(0, dimLength):
            clickLogSegmentFile.write(str(dimensions[i]))
            if i != dimLength - 1:
                clickLogSegmentFile.write(dimensionDelimiter)

        clickLogSegmentFile.write(fieldsDelimiter)
        clickLogSegmentFile.write(str(clicks))
        clickLogSegmentFile.write(fieldsDelimiter)
        clickLogSegmentFile.write(str(impressions))
        clickLogSegmentFile.write('\n')

    clickLogSegmentFile.close()
    return thresholdedClickLogSegments

def mergeExistingNewClickLogs(newClickLogs, existingClickLogs, decayFactor):
    """
    Merge the existing and new click logs. The old click logs get decayed by the decay factor.

    :param newClickLogs: click logs for the current date
    :type newClickLogs: list of ClickLogLine objects
    :param existingClickLogs: historical logs per segment
    :type existingClickLogs: list of ClickLogLine objects
    :param decayFactor: value between 0 and 1 by which the historical click data has to be decayed
    :type decayFactor: float
    """
    logger = logging.getLogger(__name__)
    consolidatedClickLogs = []
    newClickLogs = sorted(newClickLogs, clickLogLineComparator)
    existingClickLogs = sorted(existingClickLogs, clickLogLineComparator)

    newClickLogsLen = len(newClickLogs)
    existingClickLogsLen = len(existingClickLogs)
    newClickLogsIndex = 0
    existingClickLogsIndex = 0
    while newClickLogsIndex < newClickLogsLen and existingClickLogsIndex < existingClickLogsLen:
        newClickLogsDimList = newClickLogs[newClickLogsIndex].dimensionList
        existingClickLogsDimList = existingClickLogs[existingClickLogsIndex].dimensionList
        compResult = compLists(newClickLogsDimList, existingClickLogsDimList)
        if compResult == 0:
            # Both old and new belong to the same dimension
            newClicks = newClickLogs[newClickLogsIndex].clicks
            newImpressions = newClickLogs[newClickLogsIndex].impressions
            existingClicks = existingClickLogs[existingClickLogsIndex].clicks
            existingImpressions = existingClickLogs[existingClickLogsIndex].impressions
            consolidatedClicks = newClicks * (1 - decayFactor) + existingClicks * decayFactor
            consolidatedImpressions = newImpressions * (1 - decayFactor) + existingImpressions * decayFactor
            clickLogLine = ClickLogLine(newClickLogsDimList, consolidatedClicks, consolidatedImpressions)
            consolidatedClickLogs.append(clickLogLine)
            newClickLogsIndex += 1
            existingClickLogsIndex += 1
        elif compResult < 0:
            # new click log line to be appended
            newClicks = newClickLogs[newClickLogsIndex].clicks
            newImpressions = newClickLogs[newClickLogsIndex].impressions
            clickLogLine = ClickLogLine(newClickLogsDimList, newClicks, newImpressions)
            consolidatedClickLogs.append(clickLogLine)
            newClickLogsIndex += 1
        elif compResult > 0:
            # existing click log line to be decayed and appended
            existingClicks = existingClickLogs[existingClickLogsIndex].clicks
            existingImpressions = existingClickLogs[existingClickLogsIndex].impressions
            decayedClicks = existingClicks * decayFactor
            decayedImpressions = existingImpressions * decayFactor
            clickLogLine = ClickLogLine(existingClickLogsDimList, decayedClicks, decayedImpressions)
            consolidatedClickLogs.append(clickLogLine)
            existingClickLogsIndex += 1

    while newClickLogsIndex < newClickLogsLen:
        newClickLogsDimList = newClickLogs[newClickLogsIndex].dimensionList
        # new click log line to be appended
        newClicks = newClickLogs[newClickLogsIndex].clicks
        newImpressions = newClickLogs[newClickLogsIndex].impressions
        clickLogLine = ClickLogLine(newClickLogsDimList, newClicks, newImpressions)
        consolidatedClickLogs.append(clickLogLine)
        newClickLogsIndex += 1

    while existingClickLogsIndex < existingClickLogsLen:
        existingClickLogsDimList = existingClickLogs[existingClickLogsIndex].dimensionList
        # existing click log line to be decayed and appended
        existingClicks = existingClickLogs[existingClickLogsIndex].clicks
        existingImpressions = existingClickLogs[existingClickLogsIndex].impressions
        decayedClicks = existingClicks * decayFactor
        decayedImpressions = existingImpressions * decayFactor
        clickLogLine = ClickLogLine(existingClickLogsDimList, decayedClicks, decayedImpressions)
        consolidatedClickLogs.append(clickLogLine)
        existingClickLogsIndex += 1

    return consolidatedClickLogs

if __name__ == "__main__":
    # Test for the merge function
    newCLLines = []
    newCLLines.append(ClickLogLine([1, 2, 3], 5, 100))
    newCLLines.append(ClickLogLine([2, 1, 3], 132, 1000))
    newCLLines.append(ClickLogLine([3, 10, 3], 20, 200))
    newCLLines.append(ClickLogLine([1, 1, 100], 10, 500))

    oldCLLines = []
    oldCLLines.append(ClickLogLine([1, 2, 111], 21, 10000))
    oldCLLines.append(ClickLogLine([1, 1, 100], 10, 1000))
    oldCLLines.append(ClickLogLine([3, 10, 3], 51, 100))
    oldCLLines.append(ClickLogLine([7, 10, 92], 7, 113))

    mergedCLLines = mergeExistingNewClickLogs(newCLLines, oldCLLines, 0.5)
    for mcll in mergedCLLines:
        s = ""
        for dim in mcll.dimensionList:
            s += str(dim)
            s += ":"
        s += str(mcll.clicks)
        s += ":"
        s += str(mcll.impressions)
        print s
