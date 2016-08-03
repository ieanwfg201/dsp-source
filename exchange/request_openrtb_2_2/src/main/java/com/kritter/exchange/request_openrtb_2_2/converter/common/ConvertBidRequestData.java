package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDataDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestData {
    public static ConvertErrorEnum convert(Request request, BidRequestUserDTO bidRequest, int version){
        BidRequestUserDataDTO data = new BidRequestUserDataDTO();
        ConvertBidRequestSegment.convert(request, data, version);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
