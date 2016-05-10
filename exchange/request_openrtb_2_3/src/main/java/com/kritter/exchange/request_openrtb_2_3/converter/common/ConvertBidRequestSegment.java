package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDataDTO;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidRequestSegment {
    public static ConvertErrorEnum convert(Request request, BidRequestUserDataDTO bidRequest, int version){
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
