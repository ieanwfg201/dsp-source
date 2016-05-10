package com.kritter.adserving.shortlisting.core.twodotthreehelper;

import org.slf4j.Logger;

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
            Response response
            ) throws Exception{
        //lastly use bidfloor value of impression to see if ad qualifies.
        Double bidFloorForImpression = bidRequestImpressionDTO.getBidFloorPrice();

        ReqLog.requestDebug(request, " Ecpm floor value asked by exchange is : "+bidFloorForImpression);
        //for the case of creative being banner.
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
            ReqLog.debugWithDebug(logger, request, "Qualifying BannerId: {} with qualifying ecpm value: {} found for " +
                    "impressionId: {} with bidFloor as: {} ",
                    creativeBannerToUse.getId() , responseAdInfo.getEcpmValue(),
                    bidRequestImpressionDTO.getBidRequestImpressionId(),bidFloorForImpression);
            responseAdInfo.setCreativeBanner(creativeBannerToUse);
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
                        creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)
                )
        {
            ReqLog.debugWithDebug(logger, request, "Qualifying richmedia ad with qualifying ecpm value: {} found for " +
                    "impressionId: {} with bidFloor as: {} ",
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
}
