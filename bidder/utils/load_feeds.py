from bidder.traffic_forecasting.core.utils.traffic_forecasting import *
from trie import *

def loadForecastIntoTrie(forecastFileName, dimDelimiter, fieldsDelimiter) :
    """ Loads the forecast from forecastFileName and creates a trie from it.

        Keyword arguments :
        forecastFileName -- Name of the file having forecast
        dimDelimiter -- dimension delimiter in the forecast file
        fieldsDelimiter -- delimiter between the fields in the forecast file

        return Returns the root of the trie containing the forecast
    """
    forecast = loadExistingForecastFile(forecastFileName, dimDelimiter, fieldsDelimiter)
    root = TrieNode()

    for forecastLine in forecast :
        root.insertChildForList(forecastLine.dimension_list, forecastLine.count)

    return root
