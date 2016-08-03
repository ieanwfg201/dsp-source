import MySQLdb
import logging
import json
import sys
import traceback

from bidder.bidding_strategies.supply_matcher.targeting import *
from bidder.utils.time_utils import *

def getCampaignsFromDB(dbConnection, timeDimension = -1, timePerWindow = 0):
    """ Gets all the campaigns from campaign table. Returns the targeting for each campaigns

        Keyword arguments :
        dbConnection -- Connection to the database
        timeDimension -- Value of time dimension. If < 0, implies that time doesn't exist as dimension
        timePerWindow -- Minutes per time window

        return Returns list of campaign objects
    """
    configLogger = logging.getLogger(__name__)

    cursor = None
    try :
        cursor = dbConnection.cursor()

        campaignSql = "select a.id as ad_id, a.campaign_id as campaign_id,a.internal_max_bid internal_max_bid,a.marketplace_id as marketplace_id, b.os_json as os_json, b.country_json as country_json, b.carrier_json as carrier_json,b.is_category_list_excluded as is_category_list_excluded,b.category_list as category_list, c.internal_total_budget as internal_total_budget, c.internal_total_burn as internal_total_burn, c.internal_daily_budget as internal_daily_budget, c.internal_daily_burn as internal_daily_burn  from ad as a JOIN targeting_profile as b, campaign_budget as c, campaign as d where a.targeting_guid=b.guid and a.campaign_id=c.campaign_id and a.campaign_id=d.id and a.status_id=1 and d.end_date > NOW();"
        cursor.execute(campaignSql)
        dbConnection.commit()

        campaignResults = cursor.fetchall()
        cursor.close()
        cursor = None
        campaignList = []
        campaignMap = {}
        for row in campaignResults:
            adId = int(row[0])
            campaignId = int(row[1])

            # Check if there's an active banner in this campaign. Use only if there is
            #bannerSql = "SELECT 1 FROM " + bannerTable + " WHERE status = 0 AND campaignid = " + str(campaignId)
            #bannerSql = "call sp_ox_select_banner_data(" + str(campaignId) + ")"
            #cursor.execute(bannerSql)
            #dbConnection.commit()

            revenue = 0
            if row[2] is not None:
                revenue = float(row[2])
            revType = int(row[3])
            if revType != 1 and revType != 2:
                # Revenue type must be CPC or CPM
                continue
            countryStr = row[5]
            countries = []
            if countryStr is not None and countryStr != "NULL":
                countryMap = json.loads(countryStr)
                for c in countryMap:
                    countries.append(int(c))

            carrierStr = row[6]
            carriers = []
            if carrierStr is not None and carrierStr != "NULL":
                carrierMap = json.loads(carrierStr)
                for c in carrierMap:
                    carriers.append(int(c))
            carrierExclusion = 0

            osStr = row[4]
            os = []
            if osStr is not None and osStr != "NULL":
                osMap = json.loads(osStr)
                for o in osMap:
                    os.append(int(o))
            osExclusion = 0

            siteCategories = []
            siteCategoryExclusion = 0

            totalAmountRemaining = 0
            if row[9] is not None and row[10] is not None:
                totalAmountRemaining = float(row[9]) - float(row[10])
            dailyAmountRemaining = 0
            if row[11] is not None and row[12] is not None:
                dailyAmountRemaining = float(row[11]) - float(row[12])

            amount = min(totalAmountRemaining, dailyAmountRemaining)

            targeting = {}
            targeting[0] = countries
            targeting[1] = carriers
            targeting[2] = os
            targeting[3] = siteCategories

            # Country is always included, as is time
            inclusionExclusionList = [0, carrierExclusion, osExclusion, siteCategoryExclusion]

            if timeDimension > 0:
                targeting[timeDimension] = getRemainingWindowsFromNow(timePerWindow)

            campaign = None
            if revType == 1:
                campaign = Campaign(campaignId, targeting, inclusionExclusionList, "CPC", revenue, amount)
            elif revType == 2:
                campaign = Campaign(campaignId, targeting, inclusionExclusionList, "CPM", revenue, amount)
            else:
                continue

            if campaignId not in campaignMap:
                campaignMap[campaignId] = campaign

            campaignMap[campaignId].adIds.append(adId)

        for campaignId, campaign in campaignMap.iteritems():
            campaignList.append(campaign)
        return campaignList
    except Exception, e:
        configLogger.info("Exception occurred %s", traceback.format_exc())
        sys.exit(1)
    finally :
        if cursor is not None :
            cursor.close()


if __name__ == '__main__' :
    # Test
    dbConnection = MySQLdb.connect(host='localhost', user='root', passwd='password', db='vserv_bidder')
    campaigns = getCampaignsFromDB(dbConnection, 4, 30)
    for campaign in campaigns:
        print campaign
    dbConnection.close()
