import random
import sys

from bidder.bidding_strategies.supply_matcher.segment_emitter import *
from bidder.bidding_strategies.supply_matcher.targeting import *
from bidder.traffic_forecasting.core.utils.traffic_forecasting import *
from bidder.utils.log_utils import *
from bidder.utils.mps.segment_row_creator import *
from bidder.ctr_prediction.logistic_regression.feed_consumer.ctr_feed_consumer import *

""" Create the mps for LP to be solved.

    Steps:
    1. Load the ad impression budget for each ad
    2. Load all the segments from supply forecasting
    3. For each ad, find all the segments it's eligible to be served on
    4. Form the LP
    5. Dump the segment to row name mapping and ad to row name mapping to be retrieved later
"""

def countDigitsInInt(x):
    if x == 0:
        return 1

    res = 0
    while x != 0:
        x /= 10
        res += 1
    return res

def getFloatString(x, totalStrSize):
    """
    Returns the string representing the floating number "x" with total length equal to totalStrSize

    :param x: floating point number
    :type x: float
    :param totalStrSize: length of string to be returned
    :type totalStrSize: int
    """
    digits = countDigitsInInt(int(x))
    decimalPlaces = totalStrSize - digits - 1
    numberFormat = '{:.' + str(decimalPlaces) + 'f}'
    return numberFormat.format(x)

def getRowName(adColName, segColName, rowNameMap):
    """ Gets the ad column name, segment column name and returns the row name formed by their combination

        Keyword arguments:
        adColName -- Name of the column corresponding to the ad
        segColName -- Name of the column corresponding to the segment
        rowNameMap -- Map containing the existing mappings. If mapping exists, it's returned, else new value is inserted in the map

        return Returns name for the row corresponding to the two columns
    """
    v = 0
    totalName = adColName + segColName
    if totalName in rowNameMap:
        v = rowNameMap[totalName]
    else:
        v = len(rowNameMap)
        rowNameMap[totalName] = v
    return hex(v)[2:]

def createMPS(adTargetingList, adBudget, forecast, segmentDefn, segmentDimNames, mpsFileName, segPrefix, adPrefix, segmentNameMapFileName, adNameMapFileMap, logitCtrPredictor, othersId, adIdDimName, cmpgnIdDimName):
    """
    Creates the MPS given the ads name, impression budget for each ad, supply forecast and segment definition

    :param adTargetingList: List of ad targeting objects
    :type adTargetingList: list of AdTargeting objects
    :param adBudget: Dictionary containing the budget remaining for each ad
    :type adBudget: dict from ad targeting -> float
    :param forecast: supply forecast
    :type forecast: list
    :param segmentDefn: dimensions in the segment definition
    :type segmentDefn: list of int
    :param segmentDimNames: names of the dimensions in segment definition
    :type segmentDimNames: list of str
    :param mpsFileName: file name for the LP program
    :type mpsFileName: str
    :param logitCtrPredictor: ctr prediction
    :type logitCtrPredictor: LogitCTRPredictor object
    :param othersId: identifier for OTHERS in the ctr prediction
    :type othersId: int
    :param adIdDimName: Name of the ad dimension in ctr segment. None if ad id is absent
    :type adIdDimName: str
    :param cmpgnIdDimName: Name of the campaign dimension in ctr segment. None if ad id is absent
    :type cmpgnIdDimName: str
    """
    segmentNameMapper = createRowNameForSegments(forecast, segPrefix)
    adNameMapper = createRowNameForAd(adTargetingList, adPrefix)
    dumpNameSegmentMapToFile(segmentNameMapper, segmentNameMapFileName)
    dumpAdNameMapToFile(adNameMapper, adNameMapFileMap)

    rowNameMap = {}

    mpsFile = None
    try:
        mpsFile = open(mpsFileName, 'w')
        mpsFile.write("NAME          REV_MAX\n")
        mpsFile.write("ROWS\n")
        mpsFile.write(" N  OPTIMIZE\n")

        trieRoot = getForecastTrie(forecast)
        for adTargeting in adTargetingList:
            segments = getSegmentsForAdFromSupplyForecastTrieIncExcl(trieRoot, adTargeting, segmentDefn, adTargeting.inclusionExclusionList)
            """
            print adTargeting.targeting
            segFile = open('dump_segments', 'w')
            for segment in segments:
                for dim in segment.dimension_list:
                    segFile.write(str(dim))
                    segFile.write(',')
                segFile.write('\n')

            segFile.close()
            """
            adColName = adNameMapper.adTargetingNameMap[adTargeting]
            for segment in segments:
                segColName = segmentNameMapper.getNameForLogLine(segment)
                rowName = getRowName(adColName, segColName, rowNameMap)
                mpsFile.write(' G  ')
                mpsFile.write(rowName)
                mpsFile.write('\n')

        mpsFile.write("COLUMNS\n")
        segmentRowMap = {}
        rowNameRHSMap = {}

        ctrSegmentDimNames = list(segmentDimNames)
        #TODO: incorporate ad id in the bidder as well. Might require massive changes to the overall bidder
        #TODO: logic though
        #if adIdDimName is not None:
            #ctrSegmentDimNames.insert(ctrSegmentAdIdPos, adIdDimName)
        if cmpgnIdDimName is not None:
            #ctrSegmentDimNames.insert(ctrSegmentCmpgnIdPos, cmpgnIdDimName)
            ctrSegmentDimNames.append(cmpgnIdDimName)

        for adTargeting in adTargetingList:
            budget = adBudget[adTargeting.identifier]
            if budget <= 0:
                # No budget remaining for the given ad. Continue
                continue
            segments = getSegmentsForAdFromSupplyForecastTrieIncExcl(trieRoot, adTargeting, segmentDefn, adTargeting.inclusionExclusionList)
            adColName = adNameMapper.adTargetingNameMap[adTargeting]
            if len(segments) > 0:
                mpsFile.write('    ')
                mpsFile.write(adColName)
            else:
                continue

            adColNameLength = len(adColName)
            if adColNameLength > 8:
                raise Exception("Name should be less than 8 characters in length. Offending name = " + adColName)

            for i in range(8 - adColNameLength):
                mpsFile.write(' ')

            mpsFile.write('  ')

            adBid = adTargeting.bid

            # First define the coefficient for the optimization problem. This is the same as budget
            mpsFile.write('OPTIMIZE')
            mpsFile.write('  ')

            budgetString = getFloatString(budget, 12)
            mpsFile.write(budgetString)

            i = 1
            for segment in segments:
                # Get CTR for the ad on the segment if the ad is of type cpc, else take the bid
                ctr = 1.0
                if adTargeting.revType == 'CPC':
                    dimensionList = list(segment.dimension_list)
                    if cmpgnIdDimName is not None:
                        #dimensionList.insert(ctrSegmentCmpgnIdPos, adTargeting.identifier)
                        dimensionList.append(adTargeting.identifier)
                    ctr = logitCtrPredictor.getCTRForDimensions(ctrSegmentDimNames, dimensionList, othersId)
                    # Since we are working on CPM units, multiply by 1000 to get eCPM in the same unit as CPM
                    ctr *= 1000
                eCPM = ctr * adBid

                segColName = segmentNameMapper.getNameForLogLine(segment)
                rowName = getRowName(adColName, segColName, rowNameMap)
                rowNameLength = len(rowName)
                if rowNameLength > 8:
                    raise Exception("Name should be less than 8 characters in length. Offending row name = " + rowName)

                if i % 2 == 1:
                    mpsFile.write('   ')
                else:
                    mpsFile.write('\n')
                    mpsFile.write('    ')
                    mpsFile.write(adColName)
                    adColNameLength = len(adColName)
                    for j in range(8 - adColNameLength):
                        mpsFile.write(' ')

                    mpsFile.write('  ')

                mpsFile.write(rowName)

                for j in range(8 - rowNameLength):
                    mpsFile.write(' ')
                mpsFile.write('  ')

                eCPMString = getFloatString(eCPM, 12)
                mpsFile.write(eCPMString)

                rowNameRHSMap[rowName] = eCPMString
                if segColName not in segmentRowMap:
                    segmentRowMap[segColName] = []
                segmentRowMap[segColName].append(rowName)
                i += 1
            mpsFile.write('\n')

        for segColName, rowNameList in segmentRowMap.items():
            mpsFile.write('    ')
            mpsFile.write(segColName)
            segColNameLength = len(segColName)
            if segColNameLength > 8:
                raise Exception("Name should be less than 8 characters in length. Offending name = " + segColName)

            for i in range(8 - segColNameLength):
                mpsFile.write(' ')

            mpsFile.write('  ')

            # Define the coefficient for the optimization problem.
            segment = segmentNameMapper.nameSegmentMap[segColName]
            segmentForecast = trieRoot.getChildValueForList(segment.dimension_list)

            mpsFile.write('OPTIMIZE')
            mpsFile.write('  ')

            segmentForecastInt = int(segmentForecast)
            digits = countDigitsInInt(segmentForecastInt)
            for i in range(12 - digits):
                mpsFile.write(' ')
            mpsFile.write(str(segmentForecastInt))

            i = 1
            for rowName in rowNameList:
                rowNameLength = len(rowName)
                if rowNameLength > 8:
                    raise Exception("Name should be less than 8 characters in length. Offending row name = " + rowName)

                if i % 2 == 1:
                    mpsFile.write('   ')
                else:
                    mpsFile.write('\n')
                    mpsFile.write('    ')
                    mpsFile.write(segColName)
                    segColNameLength = len(segColName)

                    for j in range(8 - segColNameLength):
                        mpsFile.write(' ')

                    mpsFile.write('  ')

                mpsFile.write(rowName)
                for j in range(8 - rowNameLength):
                    mpsFile.write(' ')
                mpsFile.write('  ')
                mpsFile.write('           1')
                i += 1

            mpsFile.write('\n')

        # Define the RHS for the dual problem
        mpsFile.write('RHS\n')
        for rowName, rhsValue in rowNameRHSMap.items():
            mpsFile.write('    RHS1      ')
            mpsFile.write(rowName)
            rowNameLength = len(rowName)
            if rowNameLength > 8:
                raise Exception("Name should be less than 8 characters in length. Offending row name = " + rowName)

            for j in range(8 - rowNameLength):
                mpsFile.write(' ')

            mpsFile.write('   ')

            rhsValueStr = str(rhsValue)
            rhsValueStrLen = len(rhsValueStr)
            if rhsValueStrLen < 12:
                for j in range(12 - rhsValueStrLen):
                    mpsFile.write(' ')
                mpsFile.write(rhsValue)
            else:
                for j in range(12):
                    mpsFile.write(rhsValueStr[j])
            mpsFile.write('\n')
        mpsFile.write('ENDATA\n')
    finally:
        if mpsFile is not None:
            mpsFile.close()


if __name__ == '__main__':
    # Create random ad targeting, ad impression budgets, forecast etc.
    adTargetingList = []
    adBudgetList = {}
    numDims = 4
    ad_count = 1
    #ad_count = 50
    max_val = 2
    #max_val = 20
    for i in range(ad_count):
        targeting = {}
        for j in range(numDims):
            jthT = {}
            for k in range(random.randint(0, min(7, max_val))):
                v = random.randint(0, max_val - 1)
                jthT[v] = 1
                #jthT.append(random.randint(0, max_val - 1))
            if len(jthT) != 0:
                l = []
                for key in jthT.keys():
                    l.append(key)
                targeting[j] = l
        adTargetingList.append(Campaign(i, targeting, [0] * 4, "CPC", random.random() + random.randint(0, 7)))
        print targeting
        adBudgetList[i] = random.randint(2, 10000)
        #adImpBudgetList.append(random.randint(2, 10000))

    segmentDefn = [0, 1, 2, 3]
    fc = []
    for i in range(max_val):
        for j in range(max_val):
            for k in range(max_val):
                for l in range(max_val):
                    line = LogLine([i, j, k, l], random.randint(1, 1000))
                    fc.append(line)

    print 'total segments = ' + str(len(fc))
    #createMPS(adTargetingList, adBudgetList, fc, segmentDefn, 'lp.mps', 'S', 'A', 'seg_name_map', 'ad_name_map')
    segmentDimNames = ['country', 'carrier', 'manufacturer', 'sitecategory']
    mpsFileName = sys.argv[1]
    segNameFileName = sys.argv[2]
    adNameFileName = sys.argv[3]

    ctrFeedStr = 'time_window=30\n'
    delimiter = ''
    for i in range(numDims):
        dimName = segmentDimNames[i]
        for j in range(max_val):
            coefficient = random.random()
            ctrFeedStr += dimName + '_' + str(j) + delimiter + str(coefficient)
            ctrFeedStr += '\n'
        coefficient = random.random()
        ctrFeedStr += dimName + '_' + str(-1) + delimiter + str(coefficient)
        ctrFeedStr += '\n'

    for i in range(ad_count):
        coefficient = random.random()
        ctrFeedStr += 'campaignid' + '_' + str(i) + delimiter + str(coefficient)
        ctrFeedStr += '\n'

    ctrFeedStr += 'campaignid' + '_' + str(-1) + delimiter + str(coefficient)
    ctrFeedStr += '\n'

    ctrFeedStr = ctrFeedStr.strip()
    print ctrFeedStr

    logitCTRPredictor = LogitCTRPredictor()
    ctrDimNames = segmentDimNames[:]
    ctrDimNames.append('campaignid')
    logitCTRPredictor.loadCTRFeedFromString(ctrFeedStr, ctrDimNames, delimiter, 1)

    createMPS(adTargetingList, adBudgetList, fc, segmentDefn, segmentDimNames, mpsFileName, 'S', 'A', segNameFileName, adNameFileName, logitCTRPredictor, -1, '', 'campaignid')
