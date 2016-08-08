package com.kritter.exchange.request_openrtb_2_3.converter.common;

import java.util.ArrayList;


import com.kritter.constants.BidRequestImpressionType;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.constants.ExchangeConstants;
import com.kritter.constants.SupportedCurrencies;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidRequestImp {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version,
                                           AccountEntity publisherAccountEntity,AccountEntity dspEntity){
        if(request.getRequestId() == null){
            return ConvertErrorEnum.REQID_FR_IMP_NULL; 
        }
        if(request.getSite() == null){
            return ConvertErrorEnum.REQ_SITE_NF;
        }
        ConvertErrorEnum convertErrorEnum = ConvertErrorEnum.HEALTHY_CONVERT;
        BidRequestImpressionDTO bidRequestImpressionDTO = new BidRequestImpressionDTO();
        bidRequestImpressionDTO.setBidRequestImpressionId(request.getRequestId());

        if(null != request.getBidRequestImpressionType())
        {
            if(request.getBidRequestImpressionType().getCode() == BidRequestImpressionType.BANNER.getCode()){
                convertErrorEnum = ConvertBidRequestImpBanner.convert(request, bidRequestImpressionDTO, version,publisherAccountEntity);
                if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
                    return convertErrorEnum;
                }
            }
            if(request.getBidRequestImpressionType().getCode() == BidRequestImpressionType.NATIVE.getCode()){
                convertErrorEnum = ConvertBidRequestImpNative.convert(request, bidRequestImpressionDTO, version,dspEntity);
                if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
                    return convertErrorEnum;
                }
            }
            if(request.getBidRequestImpressionType().getCode() == BidRequestImpressionType.VIDEO.getCode()){
                convertErrorEnum = ConvertBidRequestImpVideo.convert(request, bidRequestImpressionDTO, version);
                if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
                    return convertErrorEnum;
                }
            }
        }
        else
        {
            if (!request.getSite().isNative()) {
                convertErrorEnum = ConvertBidRequestImpBanner.convert(request, bidRequestImpressionDTO, version, publisherAccountEntity);
                if (convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT) {
                    return convertErrorEnum;
                }
            }
            if (request.getSite().isNative()) {
                convertErrorEnum = ConvertBidRequestImpNative.convert(request, bidRequestImpressionDTO, version,dspEntity);
                if (convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT) {
                    return convertErrorEnum;
                }
            }
            if (!request.getSite().isNative()) {
                convertErrorEnum = ConvertBidRequestImpVideo.convert(request, bidRequestImpressionDTO, version);
                if (convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT) {
                    return convertErrorEnum;
                }
            }
        }
        bidRequestImpressionDTO.setIsAdInterstitial((request.isInterstitialBidRequest())? 1:0);
        bidRequestImpressionDTO.setBidFloorPrice(request.getSite().getEcpmFloorValue());
        bidRequestImpressionDTO.setBidFloorCurrency(SupportedCurrencies.getEnum(publisherAccountEntity.getCurrency()).getName());
        
        bidRequestImpressionDTO.setSecure(ExchangeConstants.req_imp_secure);
        convertErrorEnum = ConvertBidRequestPMP.convert(request, bidRequestImpressionDTO, version);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        ArrayList<BidRequestImpressionDTO> bidRequestImpressionArray = new ArrayList<BidRequestImpressionDTO>();
        bidRequestImpressionArray.add(bidRequestImpressionDTO);
        bidRequest.setBidRequestImpressionArray(bidRequestImpressionArray.toArray(new BidRequestImpressionDTO[bidRequestImpressionArray.size()]));
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
