import random

from segment_emitter import *
from targeting import *
from bidder.traffic_forecasting.core.utils.traffic_forecasting import *

logging.basicConfig()

def segmentEmitterTests():
    # tests for segment emitter
    segmentDefn = [0, 1, 2, 3]
    # Create random supply forecast
    forecastFileName = 'supply_forecast'
    forecastFile = open(forecastFileName, 'w')
    maxi = 2
    #maxi = 10
    maxj = 2
    #maxj = 5
    maxk = 2
    #maxk = 20
    maxl = 2
    #maxl = 10
    for i in range(1, maxi) :
        for j in range(1, maxj) :
            for k in range(1, maxk) :
                for l in range(1, maxl) :
                    line = str(i) + "" + str(j) + "" + str(k) + "" + str(l) + "" + str(random.randint(1, 10000)) + "\n"
                    forecastFile.write(line)

    forecastFile.close()

    existingForecast = loadExistingForecastFile(forecastFileName, '', '')
    print 'existing forecast size = ' + str(len(existingForecast))
    for fc in existingForecast :
        print fc

    rootNode = getForecastTrie(existingForecast)
    childMap = rootNode.getChildMap()
    print childMap

    targeting = {}
    targeting[0] = [1, 2, 3]
    targeting[1] = [1, 4, 5]
    targeting[2] = [1, 6]
    targeting[3] = [1, 8, 9]
    adTargeting = Campaign(1, targeting, [0] * 4, 'CPM', 4, 100, 1)
    segments = getSegmentsForAdFromSupplyForecastTrie(rootNode, adTargeting, segmentDefn)

    for segment in segments :
        print segment


def inclusionExclusionTests():
    forecast = []
    forecast.append(LogLine([1, 2, 3], 10))
    forecast.append(LogLine([1, 3, 4], 8))
    forecast.append(LogLine([1, 3, 5], 100))
    forecast.append(LogLine([2, 10, 6], 80))
    forecast.append(LogLine([2, 11, 6], 17))
    forecast.append(LogLine([2, 11, 8], 123))
    forecast.append(LogLine([2, 11, 7], 123))
    forecast.append(LogLine([3, 11, 7], 123))
    rootNode = getForecastTrie(forecast)

    targeting = {}
    targeting[0] = [1, 2]
    targeting[1] = [3, 10]
    targeting[2] = [3, 6, 7]

    exclusionInclusion = [0, 1, 0]

    campaign = Campaign(1, targeting, exclusionInclusion, "CPM", 11)

    segmentDefn = [0, 1, 2]
    segments = getSegmentsForAdFromSupplyForecastTrieIncExcl(rootNode, campaign, segmentDefn, exclusionInclusion)
    for segment in segments:
        print segment


if __name__ == "__main__":
    inclusionExclusionTests()
