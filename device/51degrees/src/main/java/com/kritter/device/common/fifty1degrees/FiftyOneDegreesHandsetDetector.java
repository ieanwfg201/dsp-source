package com.kritter.device.common.fifty1degrees;

import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.detector.HandsetBrowserCache;
import com.kritter.device.common.detector.HandsetManufacturerCache;
import com.kritter.device.common.detector.HandsetModelCache;
import com.kritter.device.common.detector.HandsetOperatingSystemCache;
import com.kritter.device.common.util.DeviceUtils;
import com.kritter.device.common.fifty1degrees.FiftyOneDegreesFileCache.HandsetInfo;
import com.kritter.device.common.entity.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiftyOneDegreesHandsetDetector implements HandsetDetectionProvider {
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    @Getter
    private final String name;

    private HandsetBrowserCache handsetBrowserCache;
    private HandsetOperatingSystemCache handsetOperatingSystemCache;
    private HandsetManufacturerCache handsetManufacturerCache;
    private HandsetModelCache handsetModelCache;
    private FiftyOneDegreesFileCache fiftyOneDegreesFileCache;

    public FiftyOneDegreesHandsetDetector(String cacheName,
                                          HandsetBrowserCache handsetBrowserCache,
                                          HandsetOperatingSystemCache handsetOperatingSystemCache,
                                          HandsetManufacturerCache handsetManufacturerCache,
                                          HandsetModelCache handsetModelCache,
                                          FiftyOneDegreesFileCache fiftyOneDegreesFileCache) {
        this.name = cacheName;
        this.handsetBrowserCache = handsetBrowserCache;
        this.handsetOperatingSystemCache = handsetOperatingSystemCache;
        this.handsetManufacturerCache = handsetManufacturerCache;
        this.handsetModelCache = handsetModelCache;
        this.fiftyOneDegreesFileCache = fiftyOneDegreesFileCache;
    }

    protected HandsetCapabilities getHandsetCapabilitiesObjectFromHandsetInfo(HandsetInfo handsetInfo) {
        Integer resolutionWidth = null;
        Integer resolutionHeight = null;
        try {
            resolutionWidth = Integer.parseInt(handsetInfo.getResolutionWidth());
            resolutionHeight = Integer.parseInt(handsetInfo.getResolutionHeight());
        } catch (NumberFormatException nfe) {
            logger.error("NumberFormatException inside FiftyOneDegreesHandsetDetector ",nfe);
            resolutionWidth = null;
            resolutionHeight = null;
        }

        /*Set high values for width and height if not available, so that any banner would pass.
        * if height and width not available then we cant check for size matching.*/
        if(null == resolutionWidth)
            resolutionWidth = 1000;
        if(null == resolutionHeight)
            resolutionHeight = 1000;

        HandsetCapabilities handsetCapabilities = new HandsetCapabilities();
        handsetCapabilities.setIsTablet(handsetInfo.isTablet());
        handsetCapabilities.setMidp2(handsetInfo.isJ2meMidp2());
        handsetCapabilities.setResolutionWidth(resolutionWidth);
        handsetCapabilities.setResolutionHeight(resolutionHeight);

        return handsetCapabilities;
    }

    public HandsetMasterData detectHandsetForUserAgent(String userAgent) throws Exception {
        HandsetInfo handsetInfo = fiftyOneDegreesFileCache.getHandsetInfo(userAgent);
        if(handsetInfo == null) {
            logger.debug("null handset info got for user agent");
            return null;
        } else {
            logger.debug("Handset info for user agent : {} = {}", userAgent, handsetInfo);
        }

        HandsetManufacturerData handsetManufacturerData = handsetManufacturerCache.query(
                handsetInfo.getBrandName().toLowerCase());
        Integer manufacturerId = null;
        if(handsetManufacturerData == null) {
            logger.debug("Handset manufacturer data is null");
        } else {
            logger.debug("Manufacturer id : {}", handsetManufacturerData.getManufacturerId());
            manufacturerId = handsetManufacturerData.getManufacturerId();
        }

        HandsetBrowserData handsetBrowserData = handsetBrowserCache.query(
                handsetInfo.getBrowserName().toLowerCase());
        // Check if the browser is present in handset browser data
        String handsetBrowserVersion = null;
        Integer handsetBrowserId = null;
        if(handsetBrowserData != null) {
            handsetBrowserId = handsetBrowserData.getBrowserId();
            if(handsetBrowserId != null) {
                logger.debug("Browser id : {}", handsetBrowserId);
            } else {
                logger.debug("Browser id is null");
            }

            if (handsetBrowserData.getBrowserVersionSet().contains(handsetInfo.getBrowserVersion())) {
                handsetBrowserVersion = handsetInfo.getBrowserVersion();
                if(handsetBrowserVersion != null) {
                    logger.debug("Browser version : {}", handsetBrowserVersion);
                } else {
                    logger.debug("Browser version is null");
                }
            }
        } else {
            logger.debug("Handset browser data is null");
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
            } else {
                logger.debug("Model id : {}", handsetModelData.getModelId());
                modelId = handsetModelData.getModelId();
            }
        }

        HandsetCapabilities handsetCapabilities = getHandsetCapabilitiesObjectFromHandsetInfo(handsetInfo);

        HandsetMasterData handsetMasterData = new HandsetMasterData(manufacturerId, modelId,
                handsetInfo.getMarketingName(), handsetOperatingSystemId, deviceOsVersion, handsetBrowserId,
                handsetBrowserVersion, handsetCapabilities);

        handsetMasterData.setBot(handsetInfo.isBot());
        handsetMasterData.setDeviceType(DeviceUtils.getDeviceTypeFrom51DegreesDeviceType(handsetInfo.getDeviceType()));
        handsetMasterData.setDeviceJavascriptCompatible(handsetInfo.isAjaxSupportJava());
        return handsetMasterData;
    }
}
