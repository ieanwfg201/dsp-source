package com.kritter.adserving.shortlisting.core.openrtbhelper;

import com.kritter.serving.demand.cache.CreativeSlotCache;
import com.kritter.serving.demand.entity.CreativeSlot;
import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.constants.CreativeFormat;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.entity.CreativeBanner;

public class ValidateFloorPrice {
    public static boolean validate(Logger logger, Request request, BidRequestImpressionDTO bidRequestImpressionDTO,
            boolean isBannerAllowed, CreativeBanner creativeBannerToUse, AdEntity adEntity,
            ResponseAdInfo responseAdInfo, boolean isRichmediaAllowed, Creative creative,
            Response response,CreativeSlotCache creativeSlotCache
            ) throws Exception{
        //lastly use bidfloor value of impression to see if ad qualifies.
        Double bidFloorForImpression = bidRequestImpressionDTO.getBidFloorPrice();

        ReqLog.requestDebugNew(request, " Ecpm floor value asked by exchange is : "+bidFloorForImpression);
        //for the case of creative being banner.
        logger.debug("isBannerAllowed: {} , bidFloorForImpression: {} , ecpmValueOfAd: {} , " +
                     "creativeBanner is null ? : {} ", isBannerAllowed,bidFloorForImpression,
                      responseAdInfo.getEcpmValue(), (null == creativeBannerToUse));
        if
                (
                isBannerAllowed                                                              &&
                        (null == bidFloorForImpression                                       ||
                                (
                                        null != bidFloorForImpression &&
                                                bidFloorForImpression.compareTo(responseAdInfo.getEcpmValue()) <= 0
                                )
                        )                                                                    &&
                        null != creativeBannerToUse                                          &&
                        ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(
                                                            bidRequestImpressionDTO.getBidRequestImpressionId(),
                                                            request.getSite(),
                                                            adEntity,
                                                            request,
                                                            responseAdInfo,logger
                                                           )
                )
        {
            ReqLog.debugWithDebugNew(logger, request, "Qualifying BannerId: {} with qualifying ecpm value: {} found for impressionId: {} with bidFloor as: {} ",
                    creativeBannerToUse.getId() , responseAdInfo.getEcpmValue(),
                    bidRequestImpressionDTO.getBidRequestImpressionId(),bidFloorForImpression);
            responseAdInfo.setCreativeBanner(creativeBannerToUse);
            CreativeSlot creativeSlot = creativeSlotCache.query(creativeBannerToUse.getSlotId());
            responseAdInfo.setCreativeSlot(creativeSlot);
            if(null != creativeBannerToUse)
                responseAdInfo.setSlotId(creativeBannerToUse.getSlotId());

            response.addResponseAdInfoAgainstBidRequestImpressionId
                    (
                            bidRequestImpressionDTO.getBidRequestImpressionId(),
                            responseAdInfo
                    );
            return true;
            //floorPriceMet = true;
        }
        else if
                (
                isRichmediaAllowed                                                         &&
                        (
                                null == bidFloorForImpression                                          ||
                                        (
                                                null != bidFloorForImpression &&
                                                bidFloorForImpression.compareTo(responseAdInfo.getEcpmValue()) <= 0
                                        )
                        )                                                                          &&
                        creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)              &&
                        ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(
                                                                        bidRequestImpressionDTO.getBidRequestImpressionId(),
                                                                        request.getSite(),
                                                                        adEntity,
                                                                        request,
                                                                        responseAdInfo,logger
                                                                       )
                )
        {
            ReqLog.debugWithDebugNew(logger, request, "Qualifying richmedia ad with qualifying ecpm value: {} found for impressionId: {} with bidFloor as: {} ",
                    responseAdInfo.getEcpmValue(),
                    bidRequestImpressionDTO.getBidRequestImpressionId(),bidFloorForImpression);

            responseAdInfo.setRichMediaAdIsCompatibleForAdserving(true);
            responseAdInfo.setRichMediaPayLoadFromCreative(creative.getHtmlContent());
            response.addResponseAdInfoAgainstBidRequestImpressionId
                    (
                            bidRequestImpressionDTO.getBidRequestImpressionId(),
                            responseAdInfo
                    );

            return true;
        }
        return false;
    }
    public static boolean validate(Logger logger, Request request, 
    		com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionDTO bidRequestImpressionDTO,
            boolean isBannerAllowed, CreativeBanner creativeBannerToUse, AdEntity adEntity,
            ResponseAdInfo responseAdInfo, boolean isRichmediaAllowed, Creative creative,
            Response response,CreativeSlotCache creativeSlotCache
            ) throws Exception{
        //lastly use bidfloor value of impression to see if ad qualifies.
        Double bidFloorForImpression = bidRequestImpressionDTO.getBidfloor();

        ReqLog.requestDebugNew(request, " Ecpm floor value asked by exchange is : "+bidFloorForImpression);
        //for the case of creative being banner.
        logger.debug("isBannerAllowed: {} , bidFloorForImpression: {} , ecpmValueOfAd: {} , " +
                     "creativeBanner is null ? : {} ", isBannerAllowed,bidFloorForImpression,
                      responseAdInfo.getEcpmValue(), (null == creativeBannerToUse));
        if
                (
                isBannerAllowed                                                              &&
                        (null == bidFloorForImpression                                       ||
                                (
                                        null != bidFloorForImpression &&
                                                bidFloorForImpression.compareTo(responseAdInfo.getEcpmValue()) <= 0
                                )
                        )                                                                    &&
                        null != creativeBannerToUse                                          &&
                        ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(
                                                            bidRequestImpressionDTO.getId(),
                                                            request.getSite(),
                                                            adEntity,
                                                            request,
                                                            responseAdInfo,logger
                                                           )
                )
        {
            ReqLog.debugWithDebugNew(logger, request, "Qualifying BannerId: {} with qualifying ecpm value: {} found for impressionId: {} with bidFloor as: {} ",
                    creativeBannerToUse.getId() , responseAdInfo.getEcpmValue(),
                    bidRequestImpressionDTO.getId(),bidFloorForImpression);
            responseAdInfo.setCreativeBanner(creativeBannerToUse);
            CreativeSlot creativeSlot = creativeSlotCache.query(creativeBannerToUse.getSlotId());
            responseAdInfo.setCreativeSlot(creativeSlot);
            if(null != creativeBannerToUse)
                responseAdInfo.setSlotId(creativeBannerToUse.getSlotId());

            response.addResponseAdInfoAgainstBidRequestImpressionId
                    (
                            bidRequestImpressionDTO.getId(),
                            responseAdInfo
                    );
            return true;
            //floorPriceMet = true;
        }
        else if
                (
                isRichmediaAllowed                                                         &&
                        (
                                null == bidFloorForImpression                                          ||
                                        (
                                                null != bidFloorForImpression &&
                                                bidFloorForImpression.compareTo(responseAdInfo.getEcpmValue()) <= 0
                                        )
                        )                                                                          &&
                        creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)              &&
                        ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(
                                                                        bidRequestImpressionDTO.getId(),
                                                                        request.getSite(),
                                                                        adEntity,
                                                                        request,
                                                                        responseAdInfo,logger
                                                                       )
                )
        {
            ReqLog.debugWithDebugNew(logger, request, "Qualifying richmedia ad with qualifying ecpm value: {} found for impressionId: {} with bidFloor as: {} ",
                    responseAdInfo.getEcpmValue(),
                    bidRequestImpressionDTO.getId(),bidFloorForImpression);

            responseAdInfo.setRichMediaAdIsCompatibleForAdserving(true);
            responseAdInfo.setRichMediaPayLoadFromCreative(creative.getHtmlContent());
            response.addResponseAdInfoAgainstBidRequestImpressionId
                    (
                            bidRequestImpressionDTO.getId(),
                            responseAdInfo
                    );

            return true;
        }
        return false;
    }
}
