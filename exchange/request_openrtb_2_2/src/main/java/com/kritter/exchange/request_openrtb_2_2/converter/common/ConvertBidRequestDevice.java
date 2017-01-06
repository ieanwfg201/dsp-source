package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestGeoDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.DeviceType;
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

        /*Form geo object if country code is available*/
        if(null != request.getCountry() && null != request.getCountry().getCountryCodeThreeLetter())
        {
            BidRequestGeoDTO bidRequestGeoDTO = new BidRequestGeoDTO();
            bidRequestGeoDTO.setCountry(request.getCountry().getCountryCodeThreeLetter());
            device.setGeoObject(bidRequestGeoDTO);
        }

        /**************************************************************************************************************/
        /****************Set extra parameters that might be required for better enrichment of bid request**************
         ****************and can be helpful for DSP to make better decision while bidding.*****************************/
        if(null != request.getBidRequestDeviceCarrier())
            device.setCarrier(request.getBidRequestDeviceCarrier());
        if(null != request.getBidRequestDeviceLanguage())
            device.setBrowserLanguage(request.getBidRequestDeviceLanguage());
        if(null != request.getHandsetMasterData())
        {
            if(null != request.getHandsetMasterData().getModelName())
                device.setDeviceModel(request.getHandsetMasterData().getModelName());
            if(null != request.getHandsetMasterData().getManufacturerName())
                device.setDeviceManufacturer(request.getHandsetMasterData().getManufacturerName());
            if(null != request.getHandsetMasterData().getOsName())
                device.setDeviceOperatingSystem(request.getHandsetMasterData().getOsName());
            if(null != request.getHandsetMasterData().getDeviceOperatingSystemVersion())
                device.setDeviceOperatingSystemVersion(request.getHandsetMasterData().getDeviceOperatingSystemVersion());

            Integer deviceType = null;
            if(null != request.getHandsetMasterData().getDeviceType())
            {
                if(request.getHandsetMasterData().getDeviceType().getCode() == DeviceType.DESKTOP.getCode())
                    deviceType = 2;
                if(request.getHandsetMasterData().getDeviceType().getCode() == DeviceType.MOBILE.getCode())
                    deviceType = 1;
            }
            if(null != deviceType)
                device.setDeviceType(deviceType);
        }

        if(null != request.getConnectionType())
        {
            if(request.getConnectionType().getId() == ConnectionType.CARRIER.getId())
                device.setConnectionType(5);
            if(request.getConnectionType().getId() == ConnectionType.WIFI.getId())
                device.setConnectionType(2);
        }
        /***********************************************Done setting extra parameters**********************************/
        /**************************************************************************************************************/

        bidRequest.setBidRequestDevice(device);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
