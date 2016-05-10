package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidRequestImpVideo {
    public static ConvertErrorEnum convert(Request request, BidRequestImpressionDTO bidRequestImpressionDTO, int version){
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
