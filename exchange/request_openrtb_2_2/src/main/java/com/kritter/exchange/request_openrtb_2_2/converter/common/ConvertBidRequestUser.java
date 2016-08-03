package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestUser {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version){
        BidRequestUserDTO user = new BidRequestUserDTO();
        ConvertBidRequestData.convert(request, user, version);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
        
}
