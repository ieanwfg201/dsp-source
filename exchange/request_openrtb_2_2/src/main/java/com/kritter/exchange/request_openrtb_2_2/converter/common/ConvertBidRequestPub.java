package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestPublisherDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestSiteDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestPub {
    public static ConvertErrorEnum convert(Request request, BidRequestSiteDTO bidRequest, int version){
        if(request.getSite().getPublisherId() == null){
            return ConvertErrorEnum.REQ_PUB_GUID_NF;
        }
        BidRequestPublisherDTO pub = new BidRequestPublisherDTO();
        pub.setPublisherIdOnExchange(request.getSite().getPublisherId());
        bidRequest.setBidRequestPublisher(pub);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
    public static ConvertErrorEnum convert(Request request, BidRequestAppDTO bidRequest, int version){
        if(request.getSite().getPublisherId() == null){
            return ConvertErrorEnum.REQ_PUB_GUID_NF;
        }
        BidRequestPublisherDTO pub = new BidRequestPublisherDTO();
        pub.setPublisherIdOnExchange(request.getSite().getPublisherId());
        bidRequest.setBidRequestPublisher(pub);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
