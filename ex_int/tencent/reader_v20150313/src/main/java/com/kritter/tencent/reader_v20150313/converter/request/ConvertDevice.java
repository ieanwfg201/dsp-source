package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import com.kritter.tencent.reader_v20150313.entity.BidRequestDeviceExtension;

import RTB.Tencent.Request.Device;

public class ConvertDevice {
    /**
    message Device {
        optional string ip = 1; //Device IP.
        optional string ua = 2; //Device user agent.
        optional Geo geo = 3; //Geo information.Only fuzzy value limited to a certain precision is provided. Please refer to instruction document for generation rules. Unavailable at present.
        optional string idfa = 4; //IDFA, not encrypted by default. (If it is encrypted in MD5, convert MD5(idfa) to uppercase.)
        optional uint32 idfa_enc = 5; //0: cleartext; 1: MD5; 2: SHA1. Filled with 0 by default.
        optional string openudid = 6; //Openudid, not encrypted by default.
        optional uint32 carrier = 7; //Carrier ID. 0: WIFI; 70120: China Mobile; 70121: China Telecom; 70123: China Unicom (different from the OpenRTB Definition).Unavailable at present.
        optional string make = 8; //Device brand, such as Apple, unavailable at present.
        optional string model = 9; //Device model, such as iPhone, unavailable at present.
        optional string os = 10; //Device OS, such as iOS.
        optional string Osv = 11; //Device OS version, such as 3.1.2, unavailable at present.
        optional uint32 Js = 12; //1: support JavaScript; 0: not support;unavailable at present.
        optional uint32 connectiontype = 13; //Connection type. 0: unknown; 1: Ethernet; 2: WIFI; 3: mobile data – unknown; 4: 2G; 5: 3G; 6: 4G; unavailable at present.
        optional uint32 devicetype = 14; //1: mobile phone/pad; 2: PC; 3: TV; unavailable at present.
        optional string mac = 15; //[V4.0]Encrypted mac. MD5(mac) toUpperCase. Perform the format conversion before encryption. (Remove the separator “:”-> to uppercase)
        optional string imei = 16; //[V4.0]Encrypted imei.MD5(imei) toUpperCase. Encrypt the original value.
        optional string androidid = 17; //[V4.0]Encrypted androidid. MD5(androidid) toUpperCase. Encrypt the original value.
    }
     */
    public static BidRequestDeviceDTO convert(Device device){
        if(device == null){
            return null;
        }
        BidRequestDeviceDTO openrtbDevice = new BidRequestDeviceDTO();
        if(device.hasIp()){
            if(device.getIp().contains(":")){
                openrtbDevice.setIpV6Address(device.getIp());
            }else{
                openrtbDevice.setIpV4AddressClosestToDevice(device.getIp());
            }
        }
        if(device.hasUa()){
            openrtbDevice.setDeviceUserAgent(device.getUa());
        }
        if(device.hasGeo()){
            BidRequestGeoDTO geo = ConvertGeo.convert(device.getGeo());
            if(geo != null){
                openrtbDevice.setGeoObject(geo);
            }
        }
        
        BidRequestDeviceExtension deviceExtension = null;
        if(device.hasIdfa()){
            if(device.hasIdfaEnc()){
                if(device.getIdfaEnc() == 0){
                    openrtbDevice.setIfa(device.getIdfa());
                }else{
                    if(deviceExtension == null){
                        deviceExtension = new BidRequestDeviceExtension();
                    }
                    if(device.getIdfaEnc() == 1){
                        deviceExtension.setMd5idfa(device.getIdfa() );
                    }else if(device.getIdfaEnc() == 2){
                        deviceExtension.setSha1idfa(device.getIdfa() );
                    }
                }
                
            }
        }
        if(device.hasOpenudid()){
            if(deviceExtension == null){
                deviceExtension = new BidRequestDeviceExtension();
            }
            deviceExtension.setOpenudid(device.getOpenudid());
            openrtbDevice.setExtensionObject(deviceExtension);
        }
        /**
         * TODO - Not Available at present
            if(device.hasCarrier()){
            }
         */
        /**
         * TODO - Not Available at present
            if(device.getMake() != null){
                openrtbDevice.setDeviceManufacturer(device.getMake());
            }
         */
        /**
         * TODO - Not Available at present
            if(device.getModel() != null){
                openrtbDevice.setDeviceModel(device.getModel());
            }
         */
        if(device.hasOs()){
            openrtbDevice.setDeviceOperatingSystem(device.getOs());
        }
        /**
         * TODO - Not Available at present        
            if(device.getOsv() != null){
                openrtbDevice.setDeviceOperatingSystemVersion(device.getOsv());
            }
        */
        /**
         * TODO - Not Available at present        
        if(device.hasJs()){
            openrtbDevice.setDoesDeviceSupportJavascript(device.getJs());
        }
        */
        /**
         * TODO - Not Available at present        
        if(device.hasConnectiontype()){
            openrtbDevice.setConnectionType(device.getConnectiontype());
        }
        */
        /**
         * TODO - Not Available at present        
        if(device.hasDevicetype()){
            openrtbDevice.setDeviceType(device.getDevicetype());
        }
        */
        if(device.hasMac()){
            openrtbDevice.setHashedMD5MacAddressOfDevice(device.getMac());
        }
        if(device.hasImei()){
            openrtbDevice.setMD5HashedDeviceId(device.getImei());
        }
        if(device.hasAndroidid()){
            openrtbDevice.setMD5HashedDevicePlatformId(device.getAndroidid());
        }
        return openrtbDevice;
    }
}
