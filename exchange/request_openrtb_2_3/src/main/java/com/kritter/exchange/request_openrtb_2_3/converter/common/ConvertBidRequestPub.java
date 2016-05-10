package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestPublisherDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.constants.ConvertErrorEnum;

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
