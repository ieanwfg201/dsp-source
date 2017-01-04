package com.kritter.device.mad;

import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.detector.HandsetManufacturerCache;
import com.kritter.device.common.detector.HandsetModelCache;
import com.kritter.device.common.detector.HandsetOperatingSystemCache;
import com.kritter.device.mad.MadFileCache.HandsetInfo;
import com.kritter.device.common.entity.*;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class MadHandsetDetector implements HandsetDetectionProvider {
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    @Getter
    private final String name;

    private HandsetOperatingSystemCache handsetOperatingSystemCache;
    private HandsetManufacturerCache handsetManufacturerCache;
    private HandsetModelCache handsetModelCache;
    private MadFileCache madFileCache;

    public MadHandsetDetector(String cacheName,
                                          HandsetOperatingSystemCache handsetOperatingSystemCache,
                                          HandsetManufacturerCache handsetManufacturerCache,
                                          HandsetModelCache handsetModelCache,
                                          MadFileCache madFileCache) {
        this.name = cacheName;
        this.handsetOperatingSystemCache = handsetOperatingSystemCache;
        this.handsetManufacturerCache = handsetManufacturerCache;
        this.handsetModelCache = handsetModelCache;
        this.madFileCache = madFileCache;
    }

    protected HandsetCapabilities getHandsetCapabilitiesObjectFromHandsetInfo(HandsetInfo handsetInfo) {
        Integer resolutionWidth = null;
        Integer resolutionHeight = null;
        try {
            resolutionWidth = Integer.parseInt(handsetInfo.getResolutionWidth());
            resolutionHeight = Integer.parseInt(handsetInfo.getResolutionHeight());
        } catch (NumberFormatException nfe) {
            resolutionWidth = null;
            resolutionHeight = null;
        }

        HandsetCapabilities handsetCapabilities = new HandsetCapabilities();
        handsetCapabilities.setIsTablet(handsetInfo.isTablet());
        handsetCapabilities.setMidp2(handsetInfo.isJ2meMidp2());
        handsetCapabilities.setResolutionWidth(resolutionWidth);
        handsetCapabilities.setResolutionHeight(resolutionHeight);

        return handsetCapabilities;
    }

    public HandsetMasterData detectHandsetForUserAgent(String userAgentIn) throws Exception {
    	String userAgentMD5 = null;
    	if(userAgentIn != null){
            try{
                MessageDigest md = MessageDigest.getInstance("MD5");
                userAgentMD5 = getDigest(userAgentIn,md);
            }catch(Exception e){
                logger.error("create md5 value error for user agent {}",userAgentIn,e);
            }
    	}



        HandsetInfo handsetInfo = madFileCache.getHandsetInfo(userAgentMD5);
        if(handsetInfo == null) {
            logger.debug("null handset info got for user agent {}[md5 value:{}]",userAgentIn,userAgentMD5);
            return null;
        } else {
            logger.debug("Handset info for user agent : {}[md5 value:{}]= {}", userAgentIn,userAgentMD5, handsetInfo);
        }

        HandsetManufacturerData handsetManufacturerData = handsetManufacturerCache.query(
                handsetInfo.getBrandName().toLowerCase());
        Integer manufacturerId = null;
        if(handsetManufacturerData == null) {
            logger.debug("Handset manufacturer data is null");
            manufacturerId=-1;
        } else {
            logger.debug("Manufacturer id : {}", handsetManufacturerData.getManufacturerId());
            manufacturerId = handsetManufacturerData.getManufacturerId();
        }

        HandsetOperatingSystemData handsetOperatingSystemData =
                handsetOperatingSystemCache.query(handsetInfo.getDeviceOs().toLowerCase());
        String deviceOsVersion = null;
        Integer handsetOperatingSystemId = null;
        if(handsetOperatingSystemData != null) {
            handsetOperatingSystemId = handsetOperatingSystemData.getOperatingSystemId();
            if(handsetOperatingSystemId != null) {
                logger.debug("OS id : {}", handsetOperatingSystemId);
            } else {
                logger.debug("OS id is null");
                handsetOperatingSystemId=-1;
            }

            if (handsetOperatingSystemData.getOperatingSystemVersionSet().contains(handsetInfo.getDeviceOsVersion())) {
                deviceOsVersion = handsetInfo.getDeviceOsVersion();
                if(deviceOsVersion != null) {
                    logger.debug("OS version : {}", deviceOsVersion);
                } else {
                    logger.debug("OS version is null");
                }
            }
        } else {
            logger.debug("Handset os data is null");
        }

        Integer modelId = null;
        if(handsetManufacturerData != null) {
            String key = HandsetModelData.createId(handsetInfo.getModelName().toLowerCase(), manufacturerId);
            HandsetModelData handsetModelData = handsetModelCache.query(key);
            if (handsetModelData == null) {
                logger.debug("Handset model data is null");
                modelId=-1;
            } else {
                logger.debug("Model id : {}", handsetModelData.getModelId());
                modelId = handsetModelData.getModelId();
            }
        }

        HandsetCapabilities handsetCapabilities = getHandsetCapabilitiesObjectFromHandsetInfo(handsetInfo);

        HandsetMasterData handsetMasterData = new HandsetMasterData(manufacturerId, modelId,
                handsetInfo.getMarketingName(), handsetOperatingSystemId, deviceOsVersion, -1,
                null, handsetCapabilities);

        handsetMasterData.setBot(handsetInfo.isBot());
        handsetMasterData.setDeviceType(null);
        handsetMasterData.setDeviceJavascriptCompatible(handsetInfo.isAjaxSupportJava());
        return handsetMasterData;
    }

    public String getDigest(String s , MessageDigest md)
            throws Exception {

        md.reset();
        byte[] digest = md.digest(s.getBytes());
        String result = new String(Hex.encodeHex(digest));
        return result;
    }
}
