package com.kritter.adserving.ad_rank_select.random_ecpm;

import com.kritter.adserving.adrankselect.common.AdRankingSelection;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.utils.common.CommonUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

/**
 * This class takes adunits and uses their bid values along
 * with ctr value(if ad is cpc) or conversion value(if ad is
 * cpa) for a defined segment to find the final ecpm.
 */
public class RandomEcpmSelection implements AdRankingSelection
{
    private static Random randomNumberGen = new Random();
    private Logger logger;
    private int numberOfAdsToSelect;

    public RandomEcpmSelection(String loggerName,int numberOfAdsToSelect)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.numberOfAdsToSelect = numberOfAdsToSelect;
    }

    @Override
    public ResponseAdInfo[] rankAdUnitsInDecreasingOrder(Request request, Response response)
    {
        logger.info("Inside rankAdUnitsInDecreasingOrder of RandomBasedOnEcpmSelection");

        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();

        ResponseAdInfo[] selectedAdInfosAfterRanking = randomlySelectAdIds(responseAdInfos,this.numberOfAdsToSelect,
                                                                         request.getRequestedNumberOfAds());

        if(null == selectedAdInfosAfterRanking)
            return null;

        return selectedAdInfosAfterRanking;
    }

    /**
     * This function uses random selection to select from a list of adunits
     * depending upon how many ads need to be returned.
     * @param shortListedAdUnits
     * @param numberOfAdsRequired
     * @return
     */
    private ResponseAdInfo[] randomlySelectAdIds(Set<ResponseAdInfo> shortListedAdUnits, 
            int numberOfAdsRequired, int requestedNumberOfAds) {
        logger.debug("Performing random selection of finalized adunits inside randomlySelectAdIds of RandomBasedOnEcpmSelection");


        if(requestedNumberOfAds != 0 && requestedNumberOfAds < numberOfAdsRequired)
            numberOfAdsRequired = requestedNumberOfAds;

        ArrayList<ResponseAdInfo> adInfosToReturnList = CommonUtils.getKRandomElementsFromCollection(shortListedAdUnits,
                numberOfAdsRequired);

        for(ResponseAdInfo responseAdInfo : adInfosToReturnList) {
            this.logger.debug("Ad unit selected inside RandomEcpmSelection with id : {}", responseAdInfo.getAdId());
        }

        ResponseAdInfo[] adInfosToReturn = new ResponseAdInfo[adInfosToReturnList.size()];
        adInfosToReturn = adInfosToReturnList.toArray(adInfosToReturn);
        return adInfosToReturn;
    }
}
