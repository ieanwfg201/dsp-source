package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestContentDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestProducer {
    public static ConvertErrorEnum convert(Request request, BidRequestContentDTO bidRequest, int version){
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
