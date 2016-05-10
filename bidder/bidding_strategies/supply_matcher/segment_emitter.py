from bidder.utils.log_utils import *

class DimensionDefaultValues:
    """ Contains a dimension and default values for each dimension
    """

    def __init__(self, dimension, defaults):
        """ Constructor for the class

        Keyword arguments :
        dimension -- identifier for the dimension (integer)
        defaults -- list containing all the values for this dimension
        """
        self.dimension = dimension
        self.defaults = defaults

def getSegmentsForAdFromSupplyForecastTrie(rootNode, adTargeting, segmentDefn):
    """ Generates all the segments where the ad can be targeted and which are present in the supply forecast

        Keyword arguments :
        rootNode -- root node of the Trie containing the segments from supply forecast
        adTargeting -- targeting for the ad
        segmentDefn -- contains the list of the dimension id's that defines a segment

        return List of segments
    """
    segmentsAsLists = createSegmentsFromDimsAndForecastTrie(rootNode, adTargeting, segmentDefn, 0)
    resSegments = []
    for seg in segmentsAsLists :
        resSegments.append(LogLine(seg, 0))
    return resSegments

def getSegmentsForAdFromSupplyForecastTrieIncExcl(rootNode, adTargeting, segmentDefn, exclusionInclusion):
    """  Like the overloaded function, returns all the segments where the ad can be targeted and are present in supply forecast.
    Also handles exclusion. The inclusion exclusion list is the same size as that of segmentDefn and specifies which dimensions
    are included in the targeting and which are excluded.
    For e.g.,
    segmentDefn = [2, 3, 1, 4] (Corresponding to ['country', 'carrier', 'time', 'manufacturer']
    exclusionInclusion = [0, 0, 0, 1]
    0 stands for inclusion, 1 for exclusion. Hence, the manufacturers listed in targeting have to be excluded.

    :param rootNode: Root node of the trie corresponding to supply forecast
    :type rootNode: Node type
    :param adTargeting: targeting for the ad
    :type adTargeting: Campaign object
    :param segmentDefn: id's corresponding to dimensions defining a segment. Must be in the same order as given by supply forecast
    :type segmentDefn: list of int
    :param exclusionInclusion: As specified above, list having a value(specifying exclusion or inclusion) corresponding to each
    dimension in segmentDefn.
    :type exclusionInclusion: list of int. Each value shall be either 0 or 1
    :returns: List of segments that this ad is targeted to
    :rtype: list of lists. Each internal list contains the dimension values for a segment.
    """
    segmentsAsLists = createSegmentsFromDimsAndForecastTrieIncExcl(rootNode, adTargeting, segmentDefn, exclusionInclusion, 0)
    resSegments = []
    for seg in segmentsAsLists :
        resSegments.append(LogLine(seg, 0))
    return resSegments

def createSegmentsFromDimsAndForecastTrie(node, adTargeting, segmentDefn, position):
    """ Generates the segments recursively

        Keyword arguments :
        rootNode -- root node of the Trie containing the segments from supply forecast
        adTargeting -- targeting for the ad
        segmentDefn -- contains the list of the dimension id's that defines a segment
        position -- position in the segment definition list

        return List of segments
    """
    currentDim = int(segmentDefn[position])
    #print 'current dimension = ' + str(currentDim)
    segments = []
    nextDims = []
    if currentDim in adTargeting.targeting :
        nextDims = adTargeting.targeting[currentDim]
        #print 'current dimension present, next dims = ' + str(nextDims)
    else:
        for keys in node.childrenMap.keys() :
            nextDims.append(keys)
        #print 'current dimension absent'

    if position != len(segmentDefn) - 1:
        #print 'position is not last'
        for dim in nextDims :
            #print 'dim = ' + str(dim)
            nextChild = node.getChild(dim)
            if nextChild is not None :
                nextSegments = createSegmentsFromDimsAndForecastTrie(nextChild, adTargeting, segmentDefn, position + 1)
                for nextSegment in nextSegments :
                    newSegment = [dim]
                    newSegment.extend(nextSegment)
                    segments.append(newSegment)
    else:
        for dim in nextDims:
            if dim in node.childrenMap :
                newSegment = [dim]
                segments.append(newSegment)

    return segments

def createSegmentsFromDimsAndForecastTrieIncExcl(node, adTargeting, segmentDefn, exclusionInclusion, position):
    """ Generates the segments recursively

    :param rootNode: Root node of the trie corresponding to supply forecast
    :type rootNode: Node type
    :param adTargeting: targeting for the ad
    :type adTargeting: Campaign object
    :param segmentDefn: id's corresponding to dimensions defining a segment. Must be in the same order as given by supply forecast
    :type segmentDefn: list of int
    :param exclusionInclusion: As specified above, list having a value(specifying exclusion or inclusion) corresponding to each
    dimension in segmentDefn.
    :type exclusionInclusion: list of int. Each value shall be either 0 or 1
    :param position: Position in segment definition processed thus far
    :type position: int
    :returns: List of segments that this ad is targeted to
    :rtype: list of lists. Each internal list contains the dimension values for a segment.
    """
    currentDim = int(segmentDefn[position])
    exclude = 0
    if position < len(exclusionInclusion):
        exclude = exclusionInclusion[position]
    #print 'current dimension = ' + str(currentDim)
    segments = []
    nextDims = []
    if currentDim in adTargeting.targeting and len(adTargeting.targeting[currentDim]) != 0:
        nextDims = adTargeting.targeting[currentDim]
        #print 'current dimension present, next dims = ' + str(nextDims)
    else :
        for keys in node.childrenMap.keys():
            nextDims.append(keys)
            #print 'current dimension absent '

    if position != len(segmentDefn) - 1 :
        if exclude == 0:
            # Corresponding to inclusion
            #print 'position is not last'
            for dim in nextDims:
                #print 'dim = ' + str(dim)
                nextChild = node.getChild(dim)
                if nextChild is not None:
                    nextSegments = createSegmentsFromDimsAndForecastTrieIncExcl(nextChild, adTargeting, segmentDefn, exclusionInclusion, position + 1)
                    for nextSegment in nextSegments:
                        newSegment = [dim]
                        newSegment.extend(nextSegment)
                        segments.append(newSegment)
        else:
            # Corresponding to exclusion
            nextDimsMap = {}
            for dim in nextDims:
                nextDimsMap[dim] = 1

            childMap = node.getChildMap()
            for dim, nextChild in childMap.items():
                if dim in nextDimsMap:
                    continue

                if nextChild is not None:
                    nextSegments = createSegmentsFromDimsAndForecastTrieIncExcl(nextChild, adTargeting, segmentDefn, exclusionInclusion, position + 1)
                    for nextSegment in nextSegments:
                        newSegment = [dim]
                        newSegment.extend(nextSegment)
                        segments.append(newSegment)

    else:
        # Leaf condition corresponding to inclusion
        if exclude == 0:
            for dim in nextDims :
                if dim in node.childrenMap:
                    newSegment = [dim]
                    segments.append(newSegment)
        else:
            # Leaf condition corresponding to exclusion
            nextDimsMap = {}
            for dim in nextDims:
                nextDimsMap[dim] = 1

            childMap = node.getChildMap()
            for dim, nextChild in childMap.items():
                if dim in nextDimsMap:
                    continue

                newSegment = [dim]
                segments.append(newSegment)

    return segments

def getSegmentsForAd(adTargeting, segmentDefn):
    """ Generates all the segments where the ad can be targeted

        Keyword arguments :
        adTargeting -- AdTargeting object containing the ad and it's targeting
        segmentDefn -- Definition of a segment. Contains a list of
        DimensionDefaultValues objects.

        return list of segments
    """
    return createSegmentsFromDims(adTargeting, segmentDefn, 0)

def createSegmentsFromDims(adTargeting, segmentDefn, position):
    """ Recursively generates segments

        Keyword arguments :
        adTargeting -- AdTargeting object containing the ad and it's targeting
        segmentDefn -- Definition of a segment. Contains a list of
        DimensionDefaultValues objects.
        position -- integer defining the position being processed in segmentDefn
        list

        return list of segments. Each segment is a list of dimension values
    """
    segments = []
    if position == len(segmentDefn):
        return segments

    dimDefaults = segmentDefn[position]
    valueList = []
    dimension = dimDefaults.dimension
    defaults = dimDefaults.defaults

    if dimension in adTargeting.targeting:
        valueList = adTargeting.targeting[dimension]
    else :
        valueList = defaults

    nextSegments = createSegmentsFromDims(adTargeting, segmentDefn, position + 1)
    for dimValue in valueList:
        newSegment = [dimValue]
        for seg in nextSegments:
            newSegment.extend(seg)

        segments.append(newSegment)

    return segments

