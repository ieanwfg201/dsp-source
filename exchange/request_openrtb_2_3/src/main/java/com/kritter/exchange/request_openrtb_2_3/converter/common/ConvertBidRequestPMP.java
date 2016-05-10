package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestPMPDTO;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidRequestPMP {
    public static ConvertErrorEnum convert(Request request, BidRequestImpressionDTO bidRequest, int version){
        BidRequestPMPDTO pmp = new BidRequestPMPDTO();
        ConvertBidRequestDeal.convert(request, pmp, version);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
