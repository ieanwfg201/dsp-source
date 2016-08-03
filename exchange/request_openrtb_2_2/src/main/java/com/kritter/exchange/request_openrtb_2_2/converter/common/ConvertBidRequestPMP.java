package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestPMPDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestPMP {
    public static ConvertErrorEnum convert(Request request, BidRequestImpressionDTO bidRequest, int version){
        BidRequestPMPDTO pmp = new BidRequestPMPDTO();
        ConvertBidRequestDeal.convert(request, pmp, version);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
