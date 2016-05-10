package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidRequestUser {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version){
        BidRequestUserDTO user = new BidRequestUserDTO();
        ConvertBidRequestData.convert(request, user, version);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
        
}
