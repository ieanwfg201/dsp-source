import pickle

from bidder.utils.log_utils import *
from bidder.utils.trie import *

class NameSegmentMapper :
    """ Class containing the mapping from name to segment and vice versa """

    def __init__(self, nameList, segmentList) :
        """ Constructor for the object
        
            Keyword arguments :
            nameList -- List of names for segments
            segmentList -- List of segments corresponding to names. Length of both the lists must be equal
        """
        if len(nameList) != len(segmentList) :
            raise Exception("Name list size doesn't match the segment list size")

        count = len(nameList)
        self.nameSegmentMap = {}
        self.segmentNameTrie = TrieNode()
        for i in range(count) :
            segment = segmentList[i]
            name = nameList[i]
            dummySegment = LogLine(segment.dimension_list, 0)
            self.nameSegmentMap[name] = dummySegment
            self.segmentNameTrie.insertChildForList(segment.dimension_list, name)

    def getNameForLogLine(self, logLineObj) :
        """ Returns the name for the given log line object, if exists

            Keyword arguments :
            logLineObj -- Log line object for which name has to be retrieved

            return Returns the name
        """
        return self.getNameForDimList(logLineObj.dimension_list)

    def getNameForDimList(self, dimList) :
        """ Returns the name for the given dimension list, if exists

            Keyword arguments :
            dimList -- List of dimensions corresponding to the segment for which name has to be retrieved

            return Returns the name
        """
        return self.segmentNameTrie.getChildValueForList(dimList)


class AdNameMapper :
    """ Class containing the mapping from name to AdTargeting object and vice versa """
    def __init__(self, nameList, adTargetingList) :
        """ Constructor for the object
        
            Keyword arguments :
            nameList -- List of names for AdTargeting objects
            segmentList -- List of AdTargeting objects: corresponding to names. Length of both the lists must be equal
        """
        if len(nameList) != len(adTargetingList) :
            raise Exception("Name list size doesn't match the ad targeting list size")

        count = len(nameList)
        self.nameAdTargetingMap = {}
        self.adTargetingNameMap = {}
        for i in range(count) :
            adTargeting = adTargetingList[i]
            name = nameList[i]
            self.nameAdTargetingMap[name] = adTargeting
            self.adTargetingNameMap[adTargeting] = name

def createRowNameForSegments(segments, prefix) :
    """ Gets a list of segments and creates the names for the segments to be 
        put in the mps file.

        Keyword arguments :
        segments -- List of Segments for which the row name has to be generated
        prefix -- prefix for the name.

        return Returns the object of NameSegmentMapper
    """
    nameList = []
    count = 0
    for segment in segments :
        name = prefix + str(count)
        count += 1
        nameList.append(name)

    result = NameSegmentMapper(nameList, segments)
    return result

def dumpNameSegmentMapToFile(nameSegMap, fileName) :
    """ Dumps the name to segment map onto the given file

        Keyword arguments :
        nameSegMap : Dictionary containing the name to segment mapping
        fileName : name of the file to dump the data into
    """
    f = open(fileName, 'w')
    # Use the highest protocol to serialize. In the binary format.
    pickle.dump(nameSegMap, f, -1)
    f.close()

def createRowNameForAd(ads, prefix) :
    """ Gets a list of ads and creates the names for the ads to be 
        put in the mps file.

        Keyword arguments :
        ads -- List of ad targeting objects for which the row name has to be generated
        prefix -- prefix for the name.

        return Returns the list of names for the ads
    """
    nameList = []
    count = 0
    for ad in ads :
        name = prefix + str(count)
        count += 1
        nameList.append(name)

    result = AdNameMapper(nameList, ads)
    return result


def dumpAdNameMapToFile(adNames, fileName) :
    """ Dumps the names to ad map onto the given file
 
        Keyword arguments :
        nameSegMap : Dictionary containing the name to ad mapping
        fileName : name of the file to dump the data into
    """
    f = open(fileName, 'w')
    # Use the highest protocol to serialize. In the binary format.
    pickle.dump(adNames, f, -1)
    f.close()
