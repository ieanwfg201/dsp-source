package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestContentDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestSiteDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestContent {
    
    public static ConvertErrorEnum populateProducer(Request request, BidRequestContentDTO bidRequest, int version){
        if(bidRequest != null){
            return ConvertBidRequestProducer.convert(request, bidRequest, version);
        }
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
    public static ConvertErrorEnum convert(Request request, BidRequestSiteDTO bidRequest, int version){
        BidRequestContentDTO content = new BidRequestContentDTO();
        if(request.getLanguage() != null){
            content.setContentLanguage(request.getLanguage());
        }
        populateProducer(request, content, version);
        bidRequest.setBidRequestContent(content);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
    public static ConvertErrorEnum convert(Request request, BidRequestAppDTO bidRequest, int version){
        BidRequestContentDTO content = new BidRequestContentDTO();
        if(request.getLanguage() != null){
            content.setContentLanguage(request.getLanguage());
        }
        populateProducer(request, content, version);
        bidRequest.setBidRequestContent(content);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
