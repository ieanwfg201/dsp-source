package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;

public class ConvertBidRequestDevice {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version){
        if(request.getUserAgent() == null){
            return ConvertErrorEnum.REQ_UA_NF;
        }
        BidRequestDeviceDTO device = new BidRequestDeviceDTO();
        if(request.getIpAddressUsedForDetection() == null){
            return ConvertErrorEnum.REQ_IP_NF;
        }
        
        device.setDeviceUserAgent(request.getUserAgent());
        device.setIpV4AddressClosestToDevice(request.getIpAddressUsedForDetection());
        ConvertErrorEnum convertErrorEnum = ConvertErrorEnum.HEALTHY_CONVERT;
        convertErrorEnum = ConvertBidRequestGeo.convert(request, device, version);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        bidRequest.setBidRequestDevice(device);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
