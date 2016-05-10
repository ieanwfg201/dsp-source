from bidder.utils.ds_utils import *
from bidder.traffic_forecasting.core.utils.traffic_forecasting import *
from generic_forecasting_algorithm import *

class ExponentialSmoothingForecasting(GenericForecastingAlgorithm):
    def __init__(self, filesToProcess, dimDelimiter, fieldsDelimiter):
        self.filesToProcess = filesToProcess
        self.dimDelimiter = dimDelimiter
        self.fieldsDelimiter = fieldsDelimiter
        #self.dumpCurrentLogLinesFile = 'current_log_segments.local'

    def dumpLogLinesToFile(self, logLines, dumpFileName):
        dumpFile = open(dumpFileName, 'w')
        if dumpFile is not None:
            # Dump all the logs in the dump file
            for logLine in logLines:
                dimension_list = logLine.dimension_list
                dimensions = len(dimension_list)
                for i in range(0, dimensions):
                    dumpFile.write(str(dimension_list[i]))
                    if i != dimensions - 1:
                        dumpFile.write(':')
                dumpFile.write('#')
                dumpFile.write(str(logLine.count))
                dumpFile.write('\n')
            dumpFile.close()

    # Given current log lines, existing forecast and decay factor, create the forecast for the next time slice
    def createSupplySegments(self, logLines, existingForecast, decayFactor, minTrafficThreshold):
        """ Find the number of requests per segment """
        sortedLogLines = sortAndConsolidateLogLines(logLines)
        #if self.dumpCurrentLogLinesFile is not None:
            #self.dumpLogLinesToFile(sortedLogLines, self.dumpCurrentLogLinesFile)

        # Match the same with the existing forecast and weight the existing forecast by the decay factor
        finalForecast = []
        logLineIndex, existingForecastIndex = 0, 0
        logLineSize, existingForecastSize = len(sortedLogLines), len(existingForecast) 
        while logLineIndex < logLineSize and existingForecastIndex < existingForecastSize:
            valueLogLine = sortedLogLines[logLineIndex]
            valueExistingForecast = existingForecast[existingForecastIndex]
            compResult = compLists(valueLogLine.dimension_list, valueExistingForecast.dimension_list)
            result = LogLine([], 0)
            if compResult < 0:
                result.dimension_list = valueLogLine.dimension_list
                result.count = valueLogLine.count
                logLineIndex += 1
            elif compResult > 0:
                result.dimension_list = valueExistingForecast.dimension_list
                result.count = valueExistingForecast.count * decayFactor
                existingForecastIndex += 1
            else:
                result.dimension_list = valueLogLine.dimension_list
                result.count = valueLogLine.count * (1 - decayFactor) + valueExistingForecast.count * decayFactor
                logLineIndex += 1
                existingForecastIndex += 1

            if result.count > minTrafficThreshold:
                finalForecast.append(result)

        # Process the remaining lines from both the lists, if any
        while logLineIndex < logLineSize:
            result = LogLine([], 0)
            valueLogLine = sortedLogLines[logLineIndex]
            result.dimension_list = valueLogLine.dimension_list
            result.count = valueLogLine.count
            logLineIndex += 1
            if result.count > minTrafficThreshold:
                finalForecast.append(result)

        while existingForecastIndex < existingForecastSize:
            result = LogLine([], 0)
            valueExistingForecast = existingForecast[existingForecastIndex]
            result.dimension_list = valueExistingForecast.dimension_list
            result.count = valueExistingForecast.count * decayFactor
            existingForecastIndex += 1
            if result.count > minTrafficThreshold:
                finalForecast.append(result)

        return finalForecast

    def createAndDumpForecast(self, cfgParams, logAdapter):
        """ Complete application for creating and dumping supply forecast given an old file.
        Supply the requisite log adapter to parse the impression log file """
        logLines = []
        for fileName in self.filesToProcess:
            logLines.extend(logAdapter.readLogsFromFile(fileName))
        parsedLogLines = parseLogLinesToObjects(logLines, self.dimDelimiter, self.fieldsDelimiter)
        existingForecast = loadExistingForecastFile(cfgParams.existing_forecast, self.dimDelimiter, self.fieldsDelimiter)
        forecast = self.createSupplySegments(parsedLogLines, existingForecast, cfgParams.decay_factor, cfgParams.min_traffic_threshold)
        dumpForecast(forecast, cfgParams.output_forecast, self.dimDelimiter, self.fieldsDelimiter)

