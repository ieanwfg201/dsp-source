class TargetingProfile:
    """ Contains information for targeting profile. All the targeting related information
    goes in here
    """

    def __init__(self, identifier, brandList, modelList, osJson, browserJson, countryList, carrierList, stateList, cityList, zipcodeList, siteList, isSiteListExcluded, categoryList, isCategoryListExcluded):
        """
        Constructor for targeting profile

        :param identifier: identifier for the profile
        :type identifier: str
        :param brandList: list of brands that are targeted
        :type brandList: list of int
        :param modelList: list of models that are targeted
        :type modelList: list of int
        :param osJson: json containing the os ranges that are targeted
        :type osJson: str
        :param browserJson: json containing the browser ranges targeted
        :type browserJson: str
        :param countryList: list of country id's targeted
        :type countryList: list of int
        :param carrierList: list of carrier id's targeted
        :type carrierList: list of int
        :param stateList: list of state id's targeted
        :type stateList: list of int
        :param cityList: list of city id's targeted
        :type cityList: list of int
        :param zipcodeList: list of zipcodes targeted
        :type zipcodeList: list of int
        :param siteList: list of site id's targeted
        :type siteList: list of int
        :param isSiteListExcluded: whether the siteList is included or excluded. False for inclusion, true for exclusion
        :type isSiteListExcluded: bool
        :param categoryList: list of category id's targeted
        :type categoryList: list of int
        :param isCategoryListExcluded: whether the categoryList is included or excluded. False for inclusion, true for exclusion
        :type isCategoryListExcluded: bool
        """
        self.identifier = identifier
        self.brandList = brandList
        self.modelList = modelList
        self.osJson = osJson
        self.browserJson = browserJson
        self.countryList = countryList
        self.carrierList = carrierList
        self.stateList = stateList
        self.cityList = cityList
        self.zipcodeList = zipcodeList
        self.siteList = siteList
        self.isSiteListExcluded = isSiteListExcluded
        self.categoryList = categoryList
        self.isCategoryListExcluded = isCategoryListExcluded

class Campaign :
    def __init__(self, identifier, targeting, inclusionExclusionList, revType, bid, amount=0, dailyBudget=0):
        """ Constructor for the campaign object.

        :param identifier: integer (unique for each campaign)
        :type identifier: int
        :param targeting: targeting for the campaign
        :type targeting: dict
        :param revType: revenue type for the campaign. One of "CPC", "CPM" or "CPA"
        :type revType: str
        :param bid: bid for the campaign
        :type bid: float
        :param amount: Budget remaining for the campaign
        :type amount: float
        :param dailyBudget: Total daily budget for the campaign
        :type dailyBudget: float
        """
        self.identifier = identifier
        self.targeting = targeting
        self.inclusionExclusionList = inclusionExclusionList
        self.revType = revType
        self.bid = bid
        self.amount = amount
        self.dailyBudget = dailyBudget
        self.adIds = []

    def getTargetingForDimension(self, dimension):
        """ Given a dimension id, return the list containing the targeting
            for that dimension 

            Keyword arguments :
            dimension -- integer which is the id for the dimension we wish
            to extract
        """
        return self.targeting[dimension]

    def addTargeting(self, dimension, targetingList, exclude=0):
        """ Adds the given targeting list to a dimension. Creates a new dimension if non-existent, else raises exception

        :param dimension: Dimension for which targeting has to be created
        :type dimension: int
        :param targetingList: list dimension values
        :type targetingList: list of int
        :param exclude: Whether the values have to be included or excluded
        :type exclude: int (0 for inclusion, 1 for exclusion)
        """
        if dimension in self.targeting:
            raise Exception("Targeting for dimension " + str(dimension) + " already exists")

        self.targeting[dimension] = targetingList
        if exclude != 0:
            listLength = len(self.inclusionExclusionList)
            if dimension >= listLength:
                #append 0's to list till dimension
                self.inclusionExclusionList.extend([0] * (dimension + 1 - listLength))

            self.inclusionExclusionList[dimension] = exclude

if __name__ == "__main__":
    targeting = {}
    targeting[0] = [0]
    targeting[1] = [1]
    targeting[2] = [22, 23]
    targeting[3] = [31, 32, 33, 34]

    incl = [0, 0, 1, 0]

    campaign = Campaign(1, targeting, incl, "CPM", 1.0, 200)

    newTargetingList = range(57, 61)
    campaign.addTargeting(7, newTargetingList, 0)
    print campaign.targeting
    print campaign.inclusionExclusionList

    newTargetingList = [73]
    campaign.addTargeting(9, newTargetingList, 1)
    print campaign.targeting
    print campaign.inclusionExclusionList

    newTargetingList = range(101, 110)
    campaign.addTargeting(5, newTargetingList, 1)
    print campaign.targeting
    print campaign.inclusionExclusionList

    campaign.addTargeting(0, newTargetingList, 1)
