package com.kritter.adserving.shortlisting.core.twodotthreehelper;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.kritter.common.site.entity.Site;
import com.kritter.entity.reqres.entity.AdExchangeInfo;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.entity.AdEntity;

public class ValidatePmp {
    /**
     * if ad is deal id targeted then run only on that deal id,
     * if impression has deal id then only that deal id targeted
     * ad can be selected.
     * @param site
     * @param adEntity
     * @param request
     * @return
     */
    public static boolean doesImpressionHasPMPDealIdForAdUnit(
            String impressionId,
            Site site,
            AdEntity adEntity,
            Request request,
            ResponseAdInfo responseAdInfo,
            Logger logger
    )
    {
        logger.info("Inside doesImpressionHasPMPDealIdForAdUnit of CreativeAndFloorMatchingRTBExchange...");

        ReqLog.requestDebugNew(request, "Inside doesImpressionHasPMPDealIdForAdUnit of CreativeAndFloorMatchingRTBExchange ...");

        Set<AdExchangeInfo.PrivateDealInfo> privateDealInfoSet =
                request.fetchPrivateDealInfoSetForImpressionId(impressionId);

        boolean impressionNeedsPMPAds = ((null != privateDealInfoSet) && privateDealInfoSet.size() > 0);

        String publisherId = site.getPublisherId();
        Map<String,String[]> adUnitPMPDealIdInfoMap = adEntity.getTargetingProfile().getPmpDealIdInfoMap();

        boolean adTargetsDealId = (null != adUnitPMPDealIdInfoMap && adUnitPMPDealIdInfoMap.size() > 0);

        logger.debug("Ad: {} targets pmp deals : {} and impression has pmp info: {} of size : {} ",
                      adEntity.getAdGuid(), adTargetsDealId,impressionNeedsPMPAds,
                      (null == privateDealInfoSet) ? 0: privateDealInfoSet.size());

        String[] dealIdArrayForThisPublisher = null;

        logger.debug("Getting deal map for publisher: {} ", publisherId);

        if(null != adUnitPMPDealIdInfoMap)
            dealIdArrayForThisPublisher = adUnitPMPDealIdInfoMap.get(publisherId);

        logger.debug("Deal id array for publisher by this ad , length: {} ",
                      null == dealIdArrayForThisPublisher ? 0 : dealIdArrayForThisPublisher.length);

        if( impressionNeedsPMPAds && adTargetsDealId && null != dealIdArrayForThisPublisher &&
                dealIdArrayForThisPublisher.length > 0
          )
        {
        	logger.debug("Impression Id:{} has deal id specified, looking if ad:{} is targeting deal id set: {} ",
                    impressionId,adEntity.getAdGuid(),fetchDealIdString(privateDealInfoSet));
        	if(request.isRequestForSystemDebugging()){
        		request.addDebugMessageForTestRequest("Impression Id:"+impressionId+" has deal id specified, "
        				+ "looking if ad:"+adEntity.getAdGuid()+" is targeting deal id set:  "+
                    fetchDealIdString(privateDealInfoSet));
        	}

            for(AdExchangeInfo.PrivateDealInfo privateDealInfo : privateDealInfoSet)
            {
                logger.debug("Private deal info from exchange : {} ", privateDealInfo.getDealId());

                for(String dealIdByAd : dealIdArrayForThisPublisher)
                {
                    logger.debug("Private deal info from ad : {} ", dealIdByAd);

                    if(
                            privateDealInfo.getDealId().equalsIgnoreCase(dealIdByAd) &&
                                    (null == privateDealInfo.getBidFloor() ||
                                            (
                                                    null != privateDealInfo.getBidFloor() &&
                                                            privateDealInfo.getBidFloor().compareTo(responseAdInfo.getEcpmValue()) <= 0
                                            )
                                    )
                            )
                    {
                        ReqLog.debugWithDebugNew(logger, request, "DealIdByAd: {} matches and fits deal id in impression:{} ",
                                dealIdByAd,privateDealInfo.getDealId());
                        responseAdInfo.setDealId(dealIdByAd);
                        return true;
                    }
                }
            }
        }

        if(!impressionNeedsPMPAds && !adTargetsDealId)
        {
            return true;
        }
        return false;
    }
    private static String fetchDealIdString(Set<AdExchangeInfo.PrivateDealInfo> privateDealInfoSet)
    {
        if(null == privateDealInfoSet || privateDealInfoSet.size() <= 0)
            return "";

        StringBuffer sb = new StringBuffer();

        for(AdExchangeInfo.PrivateDealInfo privateDealInfo : privateDealInfoSet)
        {
            sb.append(privateDealInfo.getDealId());
            sb.append(" , ");
        }

        return sb.toString();
    }

}
