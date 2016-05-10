import logging
from time import strftime
import sys
from logistic_regression_history import *

from bidder.utils.log_utils import *

appLogger = logging.getLogger(__name__)

def getMapForRelevantDimValues(clickLogLines, dimPos, relativeThreshold, absoluteThreshold):
    """
    Takes click log lines and decides which dimension values are relevant and which are not based on
    the relative and absolute thresholds. The dimension values that have a greater number of impressions
    than the relative and absolute thresholds are deemed as relevant. The ones that don't meet the
    threshold are deemed as "OTHERS". Returns a map containing dimension values to retain.

    :param clickLogLines: list of click log lines to be processed
    :type clickLogLines: list of ClickLogLine objects
    :param dimPos: position of the dimension to be processed
    :type dimPos: int
    :param relativeThreshold: percentile of dimension values lesser than which the values need to be
    discarded (branded as "OTHERS")
    :type relativeThreshold: float
    :param absoluteThreshold: count of impressions for a dimension value less than which the dimension
    value needs to be discarded (branded as "OTHERS")
    :type absoluteThreshold: float
    :returns: map containing the dimension values that need to be preserved. The map will have a
    key->value pair of dimension value->1 for the ones that need to be kept. The dimension values
    that need to be discarded are absent in the map
    :rtype: map (int->int)
    """
    valueMap = {}
    for clickLogLine in clickLogLines:
        dimValue = clickLogLine.dimensionList[dimPos]
        if dimValue in valueMap:
            valueMap[dimValue] += clickLogLine.impressions
        else:
            valueMap[dimValue] = clickLogLine.impressions

    valueList = []
    for dimValue, impressions in valueMap.items():
        valueList.append((impressions, dimValue))
    valueList.sort()
    dimensionsToDiscard = int(len(valueList) * relativeThreshold)
    valuesToRetain = []
    for i in range(dimensionsToDiscard, len(valueList)):
        impressions, dimValue = valueList[i]
        if impressions >= absoluteThreshold:
            valuesToRetain.append((impressions, dimValue))

    dimValuesToRetain = {}
    for valueToRetain in valuesToRetain:
        impressions, dimValue = valueToRetain
        dimValuesToRetain[dimValue] = 1

    return dimValuesToRetain

def getRelevantDimValues(clickLogLines, relativeThresholdList, absoluteThresholdList):
    """
    Function to return the relevant dimension values for each dimension. Relevant dimension
    values are decided by the thresholds (relative and absolute). If the number of impressions
    for a particular dimension value are less than the given threshold, the dimension value
    is discarded.

    :param clickLogLines: list of click log lines to be processed
    :type clickLogLines: list of ClickLogLine objects
    :param relativeThresholdList: percentile of dimension values lesser than which the values need to be
    discarded (branded as "OTHERS"). It's a list having one value for each dimension
    :type relativeThreshold: list of float
    :param absoluteThreshold: count of impressions for a dimension value less than which the dimension
    value needs to be discarded (branded as "OTHERS"). It's a list having one value for each dimension
    :type absoluteThreshold: list of float
    :returns: list of maps containing dimension values for each dimension
    :rtype: list of maps. Each map is of type int->int
    """
    if clickLogLines is None or len(clickLogLines) == 0:
        return None
    dimensionList = clickLogLines[0].dimensionList
    dimensionCount = len(dimensionList)
    relevantDimValueMapList = []
    for dimPos in range(0, dimensionCount):
        relativeThreshold = 0
        if dimPos < len(relativeThresholdList):
            relativeThreshold = relativeThresholdList[dimPos]
        absoluteThreshold = 0
        if dimPos < len(absoluteThresholdList):
            absoluteThreshold = absoluteThresholdList[dimPos]

        relevantDimValueMap = getMapForRelevantDimValues(clickLogLines, dimPos, relativeThreshold, absoluteThreshold)
        relevantDimValueMapList.append(relevantDimValueMap)

    return relevantDimValueMapList

def filterAndConsolidateLogs(clickLogLines, relativeThresholdList, absoluteThresholdList, othersId):
    """
    Function to take click log lines and the different thresholds and filter and consolidate the logs
    taking into account the different thresholds.

    :param clickLogLines: list of click log lines to be processed
    :type clickLogLines: list of ClickLogLine objects
    :param relativeThresholdList: percentile of dimension values lesser than which the values need to be
    discarded (branded as "OTHERS"). It's a list having one value for each dimension
    :type relativeThreshold: list of float
    :param absoluteThreshold: count of impressions for a dimension value less than which the dimension
    value needs to be discarded (branded as "OTHERS"). It's a list having one value for each dimension
    :type absoluteThreshold: list of float
    :returns: click log lines having dimension values deemed irrelevant as OTHERS
    :rtype: list of ClickLogLine objects
    """
    relevantDimValueMapList = getRelevantDimValues(clickLogLines, relativeThresholdList, absoluteThresholdList)
    segmentImpsClicksMap = {}
    for clickLogLine in clickLogLines:
        dimensionList = []
        for i in range(0, len(clickLogLine.dimensionList)):
            dimValue = clickLogLine.dimensionList[i]
            if dimValue in relevantDimValueMapList[i]:
                dimensionList.append(dimValue)
            else:
                dimensionList.append(othersId)
        dimensionTuple = tuple(dimensionList)
        if dimensionTuple in segmentImpsClicksMap:
            impressions, clicks = segmentImpsClicksMap[dimensionTuple]
            impressions += clickLogLine.impressions
            clicks += clickLogLine.clicks
            segmentImpsClicksMap[dimensionTuple] = (clicks, impressions)
        else:
            segmentImpsClicksMap[dimensionTuple] = (clickLogLine.clicks, clickLogLine.impressions)

    consolidatedClickLogs = []
    for dimensionTuple, impClickTuple in segmentImpsClicksMap.items():
        clicks, impressions = impClickTuple
        clickLogLine = ClickLogLine(list(dimensionTuple), clicks, impressions)
        consolidatedClickLogs.append(clickLogLine)

    return consolidatedClickLogs

def dumpCSVForLogisticRegression(consolidatedClickLogs, dimNameList, csvFileName, objectiveName, minViableImpCount, othersId):
    """
    Dumps the csv that is going to be the input to the logistic regression solver. The first row will
    be the comma separated dimension names. The following rows will contain the values for dimensions.

    :param consolidatedClickLogs: click logs having segments and impressions and clicks, with the small segments
    bundled into OTHERS
    :type consolidatedClickLogs: list of ClickLogLine objects
    :param dimNameList: dimension names
    :type dimNameList: list of str
    :param csvFileName: Name of the output csv file
    :type csvFileName: str
    :param objectiveName: name of the dependent variable ('ctr' or 'cpa' or some other value to be predicted)
    :type objectiveName: str
    :param minViableImpCount: Minimum number of impressions in the segment. If the number is less than that, the
    impressions count for the segment is set to this value
    :type minViableImpCount: int
    :param othersId: others id for campaign
    :type othersId: int
    :return: 0 if the csv file is empty, 1 otherwise
    :rtype: int
    """
    csvFile = None

    try:
        csvFile = open(csvFileName, 'w')
        for dimName in dimNameList:
            csvFile.write(dimName)
            csvFile.write(',')
        #csvFile.write(objectiveName)
        #csvFile.write('\n')
        csvFile.write('clicks,impressions\n')

        if consolidatedClickLogs is None or len(consolidatedClickLogs) == 0:
            # Empty csv file to be generated
            return 0

        campaignPos = -1
        for i in range(0, len(dimNameList)):
            dimName = dimNameList[i]
            if 'campaign' in dimName:
                campaignPos = i

        for clickLog in consolidatedClickLogs:
            dimList = clickLog.dimensionList
            clicks = clickLog.clicks
            impressions = clickLog.impressions
            if impressions < clicks:
                # CTR > 1, not possible
                continue
            if impressions == 0:
                # Both clicks and impressions are 0, not possible again
                continue
            if campaignPos != -1:
                campaignId = int(dimList[campaignPos])
                if campaignId == othersId and impressions < minViableImpCount:
                    # Both clicks and impressions are 0, not possible again
                    impressions = minViableImpCount
            for dim in dimList:
                csvFile.write(str(dim))
                csvFile.write(',')
            #ctr = clicks * 1.0 / impressions
            #csvFile.write(str(ctr))
            csvFile.write(str(clicks) + "," + str(impressions))
            csvFile.write('\n')

        return 1
    finally:
        if csvFile is not None:
            csvFile.close()

if __name__ == '__main__':
    inputFileName = sys.argv[1]
    outputFileName = sys.argv[2]
    existingLogLines = loadExistingClickLogSegments(inputFileName, u'\x01', u'\x02')
    consolidatedLogLines = mergeExistingNewClickLogs([], existingLogLines, 1)
    absList = [1000, 1000, 1000, 1000, 1000, 1000]
    #relList = [0.1, 0.1, 0.1, 0.1, 0.1, 0.1]
    relList = [0, 0, 0, 0, 0, 0]
    filteredLogLines = filterAndConsolidateLogs(consolidatedLogLines, relList, absList, -2)
    print 'Number of filtered log lines = ' + str(len(filteredLogLines))
    dimNames = ['country', 'carrier', 'os', 'campaignid', 'bannerid', 'time']
    dumpCSVForLogisticRegression(filteredLogLines, dimNames, outputFileName, 'ctr', 100)
