package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestContentDTO;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidRequestProducer {
    public static ConvertErrorEnum convert(Request request, BidRequestContentDTO bidRequest, int version){
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
