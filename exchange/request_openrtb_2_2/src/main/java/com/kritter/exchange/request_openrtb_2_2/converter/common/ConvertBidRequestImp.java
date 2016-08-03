package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.ExchangeConstants;
import com.kritter.constants.SupportedCurrencies;
import com.kritter.entity.reqres.entity.Request;

import java.util.ArrayList;

public class ConvertBidRequestImp {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version,
            AccountEntity accountEntity){
        if(request.getRequestId() == null){
            return ConvertErrorEnum.REQID_FR_IMP_NULL; 
        }
        if(request.getSite() == null){
            return ConvertErrorEnum.REQ_SITE_NF;
        }
        ConvertErrorEnum convertErrorEnum = ConvertErrorEnum.HEALTHY_CONVERT;
        BidRequestImpressionDTO bidRequestImpressionDTO = new BidRequestImpressionDTO();
        bidRequestImpressionDTO.setBidRequestImpressionId(request.getRequestId());
        if(!request.getSite().isNative()){
            convertErrorEnum = ConvertBidRequestImpBanner.convert(request, bidRequestImpressionDTO, version,accountEntity);
            if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
                return convertErrorEnum;
            }
        }
        if(request.getSite().isNative()){
            //TODO think of what to do here, as open rtb 2.2 does not have any native support in bidrequest.
        }
        if(!request.getSite().isNative()){
            convertErrorEnum = ConvertBidRequestImpVideo.convert(request, bidRequestImpressionDTO, version);
            if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
                return convertErrorEnum;
            }
        }
        bidRequestImpressionDTO.setIsAdInterstitial((request.isInterstitialBidRequest())? 1:0);
        bidRequestImpressionDTO.setBidFloorPrice(request.getSite().getEcpmFloorValue());
        bidRequestImpressionDTO.setBidFloorCurrency(SupportedCurrencies.getEnum(accountEntity.getCurrency()).getName());
        
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
