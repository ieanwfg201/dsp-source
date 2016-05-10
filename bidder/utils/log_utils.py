from ds_utils import *

class LogLine:
    """ Class containing the log to be fed in the model 
        Has the list of dimensions to feed into the model 
        and the count for the given dimensions 
        works for both impression and click logs """
    def __init__(self, dimension_list, count):
        self.dimension_list = dimension_list
        self.count = count

    def __str__(self):
        res = str()
        res += str(self.dimension_list)
        res = res + ", count = " + str(self.count)
        return res

class BidLogLine:
    """
    Class containing the log from online bidder system. Has the list of dimensions and the bid for the given dimensions.
    """
    def __init__(self, dimensionList, bid, frequency = 1):
        """
        Constructor for the class
        """
        self.dimensionList = dimensionList
        self.bid = bid
        self.frequency = frequency

class WinBidLogLine:
    """
    Class containing the log from win notification system. Has the list of dimensions and the bid for the given dimensions.
    Also contains the bid that was sent by the online system.
    """
    def __init__(self, dimensionList, bid, winBid, frequency = 1):
        """
        Constructor for the class
        """
        self.dimensionList = dimensionList
        self.bid = bid
        self.winBid = winBid
        self.frequency = frequency

class AveragePriceLandscapeLogLine:
    """
    Class containing the average price landscape log line.
    """
    def __init__(self, dimensionList, totalWinBid, numWinBids):
        """
        :param dimensionList: List of dimensions corresponding to the segment for this log line
        :type dimensionList: list
        :param totalWinBid: total win bid amount
        :type totalWinBid: float
        :param numWinBids: total number of win bids for this segment
        :type numWinBids: float
        """
        self.dimensionList = dimensionList
        self.totalWinBid = totalWinBid
        self.numWinBids = numWinBids

    def __str__(self):
        return str(self.dimensionList) + ',' + str(self.totalWinBid) + ',' + str(self.numWinBids)

def avgPriceLandscapeLogLineComparator(a, b):
    """
    Returns the comparison between two average price landscape log lines, based on dimensions
    In case the dimensions are equal, the comparison result of total win bids is taken into account, and in case of
    equality, comparison result of total win amount is used

    :param a: first object
    :type a: AveragePriceLandscapeLogLine
    :param b: second object
    :type b: AveragePriceLandscapeLogLine
    :return: 0 if both objects are equal, -1 if a < b and 1 if a > b
    :rtype: int
    """
    dim_comparison = compLists(a.dimensionList, b.dimensionList)
    if dim_comparison != 0:
        return dim_comparison
    if a.numWinBids < b.numWinBids:
        return -1
    if a.numWinBids > b.numWinBids:
        return 1
    if a.totalWinBid < b.totalWinBid:
        return -1
    if a.totalWinBid > b.totalWinBid:
        return 1
    return 0

class ClickLogLine:
    """
    Class containing the log from click notification system. Has the list of dimensions and the impressions and clicks
    corresponding to the dimensions.
    """
    def __init__(self, dimensionList, clicks, impressions):
        """
        Constructor for the class
        """
        self.dimensionList = dimensionList
        self.clicks = clicks
        self.impressions = impressions

def clickLogLineComparator(a, b):
    """
    Returns the comparison between two click log lines, based on dimensions.
    If the dimensions are equal, returns the comparison result of the clicks, else the comparison result of
    impressions

    :param a: First object
    :type a: ClickLogLine
    :param b: Second object
    :type b: ClickLogLine
    :returns: 0 if both objects are equal, -1 if a < b and 1 if a > b
    :rtype: int
    """
    dimComparison = compLists(a.dimensionList, b.dimensionList)
    if dimComparison != 0:
        return dimComparison
    if a.clicks < b.clicks:
        return -1
    if a.clicks > b.clicks:
        return 1
    if a.impressions < b.impressions:
        return -1
    if a.impressions > b.impressions:
        return 1
    return 0

def bidLogLineComparator(a, b):
    """
    Returns the comparison between two bid log lines, based on dimensions.
    If the dimensions are equal, returns the comparison result of the bids

    :param a: First object
    :type a: BidLogLine
    :param b: Second object
    :type b: BidLogLine
    """
    dimComparison = compLists(a.dimensionList, b.dimensionList)
    if dimComparison != 0:
        return dimComparison
    if a.bid < b.bid:
        return -1
    if a.bid > b.bid:
        return 1
    if a.frequency < b.frequency:
        return -1
    if a.frequency > b.frequency:
        return 1
    return 0

def winBidLogLineComparator(a, b):
    """
    Returns the comparison between two win bid log lines, based on dimensions.
    If the dimensions are equal, returns the comparison result of the win bids. If win bids are also equal,
    then returns the comparison result of the bids.

    :param a: First object
    :type a: WinBidLogLine
    :param b: Second object
    :type b: WinBidLogLine
    """
    dimComparison = compLists(a.dimensionList, b.dimensionList)
    if dimComparison != 0:
        return dimComparison
    if a.winBid < b.winBid:
        return -1
    if a.winBid > b.winBid:
        return 1
    if a.bid < b.bid:
        return -1
    if a.bid > b.bid:
        return 1
    if a.frequency < b.frequency:
        return -1
    if a.frequency > b.frequency:
        return 1
    return 0

def winBidLogLineComparatorByBid(a, b):
    """
    Returns the comparison between two win bid log lines, based on dimensions.
    If the dimensions are equal, returns the comparison result of the bids. If bids are also equal,
    then returns the comparison result of the win bids.
    Note: The order of comparison of bid and win bid is different in this function.

    :param a: First object
    :type a: WinBidLogLine
    :param b: Second object
    :type b: WinBidLogLine
    """
    dimComparison = compLists(a.dimensionList, b.dimensionList)
    if dimComparison != 0:
        return dimComparison
    if a.bid < b.bid:
        return -1
    if a.bid > b.bid:
        return 1
    if a.winBid < b.winBid:
        return -1
    if a.winBid > b.winBid:
        return 1
    if a.frequency < b.frequency:
        return -1
    if a.frequency > b.frequency:
        return 1
    return 0

class SegmentValue:
    def __init__(self, dimensions, imps, clks):
        self.dimension_list = dimensions
        self.impressions = imps
        self.clicks = clks

    @classmethod
    def fromImpLogLines(cls, impLogLine, clickLogLine):
        """ Dimension lists must be the same for impression log and click log
            If not, error condition and return -1 """
        if not eqLists(impLogLine.dimension_list, clickLogLine.dimension_list):
            raise Exception("Impression log line and click log line not in sync. Something is ghastly wrong!!!")
        dimension_list = impLogLine.dimension_list
        impressions = impLogLine.count
        clicks = clickLogLine.count
        return cls(dimension_list, impressions, clicks)

    def __str__(self):
        res = str()
        res += str(self.dimension_list)
        res = res + ", impressions = " + str(self.impressions) + ", clicks = " + str(self.clicks)
        return res

def logLineComparator(a, b):
    return compLists(a.dimension_list, b.dimension_list)

def sortLogLines(logLines):
    """ Take a series of log lines and sort them according to their dimensions """
    return sorted(logLines, logLineComparator)

def sortBidLogLines(bidLogLines):
    return sorted(bidLogLines, bidLogLineComparator)

def sortWinBidLogLines(bidLogLines):
    return sorted(bidLogLines, winBidLogLineComparator)

def intersectSortedLogLines(a, b):
    """ Returns the intersection of two sorted logline lists """
    matches = []
    index_a, index_b = 0, 0
    length_a, length_b = len(a), len(b)
    while index_a < length_a and index_b < length_b:
        value_a = a[index_a]
        value_b = b[index_b]
        compResult = compLists(value_a.dimension_list, value_b.dimension_list)
        if compResult < 0:
            index_a += 1
        elif compResult > 0:
            index_b += 1
        else:
            segment = SegmentValue.fromImpLogLines(value_a, value_b)
            matches.append(segment)
            index_a += 1
            index_b += 1
    return matches

def sortAndConsolidateLogLines(inLoglines):
    """ Sort the log lines and consolidate the log lines for each segment
    """
    loglines = sortLogLines(inLoglines)
    consolidated = []
    if inLoglines == None or len(inLoglines) == 0:
        return consolidated
    prev = loglines[0]
    index = 1
    cons = LogLine(prev.dimension_list, prev.count)
    hi = len(loglines)
    while index < hi:
        current = loglines[index]
        if eqLists(current.dimension_list, prev.dimension_list):
            cons.count += current.count
        else:
            consolidated.append(cons)
            prev = current
            cons = LogLine(prev.dimension_list, current.count)
        index += 1
    consolidated.append(cons)
    return consolidated

def sortAndConsolidateAvgPLLogLines(inLoglines):
    """ Sort the log lines and consolidate the log lines for each segment
    """
    loglines = sorted(inLoglines, avgPriceLandscapeLogLineComparator)
    consolidated = []
    if inLoglines == None or len(inLoglines) == 0:
        return consolidated
    prev = loglines[0]
    index = 1
    cons = AveragePriceLandscapeLogLine(prev.dimensionList, prev.totalWinBid, prev.numWinBids)
    hi = len(loglines)
    while index < hi:
        current = loglines[index]
        if eqLists(current.dimensionList, prev.dimensionList):
            cons.numWinBids += current.numWinBids
            cons.totalWinBid += current.totalWinBid
        else:
            consolidated.append(cons)
            prev = current
            cons = AveragePriceLandscapeLogLine(prev.dimensionList, prev.totalWinBid, prev.numWinBids)
        index += 1
    consolidated.append(cons)
    return consolidated

def logDimensionExtractor(requestResponse, dimList):
    """
    Function to extract the dimensions from a request response object

    Keyword arguments:
    :param requestResponse: Request Response object corresponding to a log line
    :param dimList: list of dimension names to extract

    return map containing dimension name to dimension value
    """
    result = {}
    for dimName in dimList:
        lowerDimName = dimName.lower()
        if lowerDimName == 'country':
            result[dimName] = requestResponse.countryId
        elif lowerDimName == 'devicemodel':
            result[dimName] = requestResponse.deviceModelId
        elif lowerDimName == 'os':
            result[dimName] = requestResponse.deviceOsId
        elif lowerDimName == 'manuf':
            result[dimName] = requestResponse.deviceManufacturerId
        elif lowerDimName == 'time':
            result[dimName] = requestResponse.time
        elif lowerDimName == 'carrier':
            result[dimName] = requestResponse.countryCarrierId
        elif lowerDimName == 'useragent':
            result[dimName] = requestResponse.userAgent
        elif lowerDimName == 'deviceid':
            result[dimName] = requestResponse.deviceId
        elif lowerDimName == 'site':
            result[dimName] = requestResponse.siteId
        elif lowerDimName == 'sitecategory':
            result[dimName] = requestResponse.siteCategoryId

    return result

class ClickCtrPerfLogLine:
    """
    Class containing the log from click notification system for ctr perf. Has the list of dimensions and the impressions and clicks
    corresponding to the dimensions.
    """
    def __init__(self, dimensionList, clicks, impressions, predictedCtr):
        """
        Constructor for the class
        """
        self.dimensionList = dimensionList
        self.clicks = clicks
        self.impressions = impressions
        self.predictedCtr = predictedCtr
