import logging
import traceback
import sys

from bidder.utils.log_utils import *
from bidder.utils.ds_utils import *
from bidder.utils.trie import *

def getForecastTrie(forecast):
    """ Takes a forecast and builds a trie for the same

       Keyword arguments :
       forecast -- Supply forecast lines.

       return Returns the trie containing data equivalent to forecast
    """
    root = TrieNode()
    for line in forecast:
        value = line.count
        childKeyList = line.dimension_list
        root.insertChildForList(childKeyList, value)

    return root

def loadExistingForecastFile(existingForecastFileName, dimDelimiter, fieldsDelimiter):
    sfLogger = logging.getLogger(__name__)

    existingForecast = []
    try:
        # Read the file line by line and create existing segment forecasts
        with open(existingForecastFileName) as file_obj :
            for line in file_obj :
                fields = line.split(fieldsDelimiter)
                if len(fields) != 2 :
                    sfLogger.error("Wrong number of fields in the existing forecast file '%s'. Problematic line = %s", existingForecastFileName, line)
                    continue
                    # raise Exception("Wrong number of fields in the existing forecast file. Problematic line = " + line)
                dimensionString = fields[0]
                requestCount = float(fields[1])
                dimFields = dimensionString.split(dimDelimiter)
                logLine = LogLine([], 0)
                logLine.count = requestCount
                for dim in dimFields :
                    dimension = int(dim)
                    logLine.dimension_list.append(dimension)
                existingForecast.append(logLine)
    except:
        # No file found, could be due to the fact that it's a new forecast
        sfLogger.info("Existing forecast file '%s' not found. Exception %s", existingForecastFileName, traceback.format_exc())
    return existingForecast

def loadExistingForecastFileExcludeDimValues(existingForecastFileName, dimDelimiter, fieldsDelimiter, excludeValuesList):
    sfLogger = logging.getLogger(__name__)

    existingForecast = []
    try:
        # Read the file line by line and create existing segment forecasts
        with open(existingForecastFileName) as file_obj :
            for line in file_obj :
                fields = line.split(fieldsDelimiter)
                if len(fields) != 2 :
                    sfLogger.error("Wrong number of fields in the existing forecast file '%s'. Problematic line = %s", existingForecastFileName, line)
                    continue
                    # raise Exception("Wrong number of fields in the existing forecast file. Problematic line = " + line)
                dimensionString = fields[0]
                requestCount = float(fields[1])
                dimFields = dimensionString.split(dimDelimiter)
                logLine = LogLine([], 0)
                logLine.count = requestCount
                for dim in dimFields :
                    dimension = int(dim)
                    logLine.dimension_list.append(dimension)

                toSkip = 0
                for i in range(0, len(dimFields)):
                    excludeValues = excludeValuesList[i]
                    dimension = logLine.dimension_list[i]
                    if dimension in excludeValues:
                        toSkip = 1

                if toSkip == 0:
                    existingForecast.append(logLine)
    except:
        # No file found, could be due to the fact that it's a new forecast
        sfLogger.info("Existing forecast file '%s' not found. Exception %s", existingForecastFileName, traceback.format_exc())
    return existingForecast

def loadMultipleExistingForecastFiles(existingForecastFileNames, weights, dimDelimiter, fieldsDelimiter):
    """ To be used the first time supply forecasting is running. The multiple forecast files correspond
    to the various exchanges for which data is present. Also given is a list of weights to be assigned
    to each exchange (in absence of any knowledge, the weights should be equal). The final forecast is 
    the weighted average of all the forecasts """
    numFiles = len(existingForecastFileNames)
    numWeights = len(weights)
    if numFiles != numWeights :
        return []
    # Aggregate the multiple forecast files into a single list
    finalExistingForecast = []
    runningSum = 0
    for counter in range(numFiles) :
        existingForecastFileName = existingForecastFileNames[counter]
        curWeight = weights[counter]
        if curWeight == 0 :
            continue

        curTotal = runningSum + curWeight
        curExistingForecast = loadExistingForecastFile(existingForecastFileName, dimDelimiter, fieldsDelimiter)
        if counter == 0 :
            finalExistingForecast = curExistingForecast
            runningSum = curTotal
            continue

        resultingForecast = []
        curExistingForecastIndex, finalExistingForecastIndex = 0, 0
        curExistingForecastSize, finalExistingForecastSize = len(curExistingForecast), len(finalExistingForecast) 

        while curExistingForecastIndex < curExistingForecastSize and finalExistingForecastIndex < finalExistingForecastSize :
            curValue = curExistingForecast[curExistingForecastIndex]
            finalValue = finalExistingForecast[finalExistingForecastIndex]
            
            compResult = compLists(curValue.dimension_list, finalValue.dimension_list)
            result = LogLine([], 0)
            if compResult < 0 :
                result.dimension_list = curValue.dimension_list
                result.count = curValue.count * curWeight / curTotal
                curExistingForecastIndex += 1
            elif compResult > 0 :
                result.dimension_list = finalValue.dimension_list
                result.count = finalValue.count * runningSum / curTotal
                finalExistingForecastIndex += 1
            else :
                result.dimension_list = curValue.dimension_list
                result.count = (curValue.count * curWeight + finalValue.count * runningSum) / curTotal
                curExistingForecastIndex += 1
                finalExistingForecastIndex += 1
            resultingForecast.append(result)

        # Process the remaining lines from both the lists, if any
        while curExistingForecastIndex < curExistingForecastSize :
            result = LogLine([], 0)
            curValue = curExistingForecast[curExistingForecastIndex]
            result.dimension_list = curValue.dimension_list
            result.count = curValue.count * curWeight / curTotal
            curExistingForecastIndex += 1
            resultingForecast.append(result)

        while finalExistingForecastIndex < finalExistingForecastSize :
            result = LogLine([], 0)
            finalValue = finalExistingForecast[finalExistingForecastIndex]
            result.dimension_list = finalValue.dimension_list
            result.count = finalValue.count * runningSum / curTotal
            finalExistingForecastIndex += 1
            resultingForecast.append(result)

        runningSum = curTotal
        finalExistingForecast = resultingForecast
    return finalExistingForecast


def getDiffOfForecasts(forecast1, forecast2) :
    """ Given 2 forecast lists, gives out the difference in them. The difference is given as 
    forecast1's count - forecast2's count. To be used to see the difference in the forecasted
    values vs actual supply """
    resultingDifference = []
    index1, index2 = 0, 0
    size1, size2 = len(forecast1), len(forecast2)

    while index1 < size1 and index2 < size2 :
        value1 = forecast1[index1]
        value2 = forecast2[index2]

        result = LogLine([], 0)
        compResult = compLists(value1.dimension_list, value2.dimension_list)
        if compResult < 0 :
            result.dimension_list = value1.dimension_list
            result.count = value1.count
            index1 += 1
        elif compResult > 0 :
            result.dimension_list = value2.dimension_list
            result.count = -value2.count
            index2 += 1
        else :
            result.dimension_list = value1.dimension_list
            result.count = value1.count - value2.count
            index1 += 1
            index2 += 1
        resultingDifference.append(result)

    while index1 < size1 :
        value = forecast1[index1]
        result = LogLine([], 0)
        result.dimension_list = value.dimension_list
        result.count = value.count
        index1 += 1
        resultingDifference.append(result)

    while index2 < size2 :
        value = forecast2[index2]
        result = LogLine([], 0)
        result.dimension_list = value.dimension_list
        result.count = value.count
        index2 += 1
        resultingDifference.append(result)

    return resultingDifference

def dumpForecast(forecast, outFileName, dimDelimiter, fieldsDelimiter) :
    f = open(outFileName, 'w')
    for fc in forecast :
        dimensions = fc.dimension_list
        count = fc.count
        line = str()
        dimCount = len(dimensions)
        for i in range(0, dimCount):
            dim = dimensions[i]
            line += str(dim)
            if i != dimCount - 1:
                line += dimDelimiter
        line += fieldsDelimiter
        line += str(count)
        line += '\n'
        f.write(line)
    f.close()

def parseLogLinesToObjects(logLines, dimDelimiter, fieldsDelimiter, optionalDelimiter='=') :
    objects = []
    for line in logLines :
        fields = line.split(fieldsDelimiter)
        dimensionString = fields[0]
        requestCount = 1
        if len(fields) == 2 :
            requestCount = float(fields[1])
        dimFields = dimensionString.split(dimDelimiter)
        logLine = LogLine([], 0)
        logLine.count = requestCount
        for dim in dimFields :
            actualDims = dim.split(optionalDelimiter)
            if len(actualDims) == 2 :
                dimension = int(actualDims[1])
            else :
                dimension = int(actualDims[0])
            logLine.dimension_list.append(dimension)
        objects.append(logLine)
    return objects


if __name__ == '_main__':
    #existingForecast = loadExistingForecastFile('test.txt', '', ',')
    existingForecast = loadExistingForecastFile('test1', ',', u'\u0001')
    for forecast in existingForecast :
        print forecast

    files = ['test', 'test1']
    weights = [1, 1]
    existingForecast = loadMultipleExistingForecastFiles(files, weights, ',', u'\u0001')
    for forecast in existingForecast :
        print forecast
