package com.kritter.adserving.ad_rank_select.wrs_ecpm;

import com.kritter.adserving.adrankselect.common.AdRankingSelection;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

/**
 * This class takes adunits and uses their bid values along
 * with ctr value(if ad is cpc) or conversion value(if ad is
 * cpa) for a defined segment to find the final ecpm.
 */
public class WRSBasedOnEcpmSelection implements AdRankingSelection
{
    private static Random randomProbabilityFinder = new Random();
    private Logger logger;
    private int numberOfAdsToSelect;

    public WRSBasedOnEcpmSelection(String loggerName,int numberOfAdsToSelect)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.numberOfAdsToSelect = numberOfAdsToSelect;
    }

    @Override
    public ResponseAdInfo[] rankAdUnitsInDecreasingOrder(Request request, Response response)
    {
        logger.info("Inside rankAdUnitsInDecreasingOrder of WRSBasedOnEcpmSelection");

        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();

        ResponseAdInfo[] selectedAdInfosAfterRanking = randomlySelectAdIdsUsingWeightedRandomSelectionOnTotalRanks
                                                                        (responseAdInfos,this.numberOfAdsToSelect,
                                                                         request.getRequestedNumberOfAds());

        if(null == selectedAdInfosAfterRanking)
            return null;

        return selectedAdInfosAfterRanking;
    }

    /**
     * This function uses weighted random selection to select from a list of adunits
     * depending upon how many ads need to be returned.
     * @param shortListedAdUnits
     * @param numberOfAdsRequired
     * @return
     */
    private ResponseAdInfo[] randomlySelectAdIdsUsingWeightedRandomSelectionOnTotalRanks
                                    (
                                     Set<ResponseAdInfo> shortListedAdUnits,
                                     int numberOfAdsRequired,
                                     int requestedNumberOfAds
                                    )
    {
        logger.debug("Performing weighted random selection of finalized adunits inside randomlySelectAdIdsUsingWeightedRandomSelectionOnTotalRanks of WRSBasedOnEcpmSelection");

        double sumTotalRank = 0.00;

        /** this map contains probabilities as per weights for the ads. */
        HashMap<Integer, Double> probabilityDistribution = new HashMap<Integer, Double>();
        HashMap<Integer, ResponseAdInfo> adInfoMap = new HashMap<Integer, ResponseAdInfo>();

        /** calculate sum of individual ranks */
        for(ResponseAdInfo responseAdInfo : shortListedAdUnits)
        {
            if(null != responseAdInfo.getEcpmValue())
                sumTotalRank += responseAdInfo.getEcpmValue();
            else
                logger.error("Ecpm null inside randomlySelectAdIdsUsingWeightedRandomSelectionOnTotalRankscof WRSBasedOnEcpmSelection for adunitid: {}", responseAdInfo.getAdId());
        }

        for(ResponseAdInfo responseAdInfo : shortListedAdUnits)
        {
            double probabilityForAdUnit = (sumTotalRank <= 0) ? 0 : (responseAdInfo.getEcpmValue() / sumTotalRank);
            probabilityDistribution.put(responseAdInfo.getAdId(), probabilityForAdUnit);
            adInfoMap.put(responseAdInfo.getAdId(),responseAdInfo);
        }

        /**
         * see if num ads is greater or final set of ads set holds more ad
         * units.Take the minimum of two.
         */
        if (probabilityDistribution.size() < numberOfAdsRequired)
            numberOfAdsRequired = probabilityDistribution.size();

        //check if number of ads required from request is non zero
        //and if smaller than configured value then allow.
        if(requestedNumberOfAds != 0 && requestedNumberOfAds < numberOfAdsRequired)
            numberOfAdsRequired = requestedNumberOfAds;


        ResponseAdInfo[] adInfosToReturn = new ResponseAdInfo[numberOfAdsRequired];

        logger.debug("Inside WRSBasedOnEcpmSelection class , Number of ads required {}", numberOfAdsRequired);

        for (int i = 0; i < numberOfAdsRequired; i++)
        {
            /** this will hold random probability between 0 and 1. */
            double randomProbability = randomProbabilityFinder.nextDouble();

            Iterator<Map.Entry<Integer,Double>> iteratorForProbabilityDistribution =
                                                                        probabilityDistribution.entrySet().iterator();

            while (iteratorForProbabilityDistribution.hasNext())
            {
                Map.Entry<Integer, Double> distributionEntry = iteratorForProbabilityDistribution.next();

                if (
                       null != distributionEntry.getValue()                         &&
                       (randomProbability -= distributionEntry.getValue()) <= 0
                   )
                {
                    ResponseAdInfo responseAdInfo = adInfoMap.get(distributionEntry.getKey());

                    this.logger.debug("Ad unit selected inside WRSBasedOnEcpmSelection with id : {}", responseAdInfo.getAdId());

                    adInfosToReturn[i] = responseAdInfo;
                    break;
                }
            }
        }

        return adInfosToReturn;
    }
}
