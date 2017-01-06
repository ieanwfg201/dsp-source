package com.kritter.postimpression.macros;

import java.io.IOException;
import java.util.Map;

import com.kritter.constants.ConnectionType;
import com.kritter.constants.DeviceType;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ConversionUrlData;
import com.kritter.utils.common.url.URLField;
import lombok.Getter;

public enum Macros {
    UNIQUE_TRACKING_ID("UNIQUE_TRACKING_ID_MACRO") {
        @Override
        public String getMacroValue(Request request) {
            String encodedConversionData = "";
            try {
                ConversionUrlData conversionUrlData = new ConversionUrlData();
                conversionUrlData.setAdvertiserBid(request.getAdvertiserBid());
                conversionUrlData.setAdId(request.getAdId());
                conversionUrlData.setSelectedSiteCategoryId(request.getSelectedSiteCategoryId());
                conversionUrlData.setDeviceOsId(request.getDeviceOsId());
                conversionUrlData.setCarrierId(request.getUiCountryCarrierId());
                conversionUrlData.setCountryId(request.getUiCountryId());
                conversionUrlData.setDeviceManufacturerId(request.getDeviceManufacturerId());
                conversionUrlData.setDeviceModelId(request.getDeviceModelId());
                conversionUrlData.setDeviceBrowserId(request.getBrowserId());
                conversionUrlData.setBidderModelId(request.getBidderModelId());
                conversionUrlData.setClickRequestId(request.getRequestId());
                conversionUrlData.setInternalBid(request.getInternalMaxBid());
                conversionUrlData.setInventorySource(request.getInventorySource());
                conversionUrlData.setSiteId(request.getSiteId());
                conversionUrlData.setSupplySourceType(request.getSupplySourceTypeCode().shortValue());
                conversionUrlData.setExternalSupplyAttributesInternalId(request.getExternalSupplyAttributesInternalId());
                Short connectionTypeId = request.getConnectionType() == null ? ConnectionType.UNKNOWN.getId() :
                        request.getConnectionType().getId();
                conversionUrlData.setConnectionTypeId(connectionTypeId);
                Short deviceTypeId = null == request.getDeviceTypeId() ? DeviceType.UNKNOWN.getCode() :
                        request.getDeviceTypeId();
                conversionUrlData.setDeviceTypeId(deviceTypeId);

                encodedConversionData = ApplicationGeneralUtils.fetchCommonEncodedDataForConversionFeedback(conversionUrlData);
            } catch (IOException ioe) {
                throw new RuntimeException("IOException inside click_id macro");
            }
            return encodedConversionData;
        }
    },
    EXTSITE("EXTSITE_MACRO") {
        @Override
        public String getMacroValue(Request request) {
            Integer externalSupplyAttributesInternalId = request.getExternalSupplyAttributesInternalId();
            if(externalSupplyAttributesInternalId != null)
                return request.getExternalSupplyAttributesInternalId().toString();
            // If the request doesn't have external site id, default to -1
            return "-1";
        }
    },
    IP("IP_MACRO") {
        @Override
        public String getMacroValue(Request request) {
            return request.getIpAddress();
        }
    },
    USERAGENT("USERAGENT_MACRO") {
        @Override
        public String getMacroValue(Request request) {
            return request.getUserAgent();
        }
    },
    XFF("XFF_MACRO") {
        @Override
        public String getMacroValue(Request request) {
            return request.getXForwardedFor();
        }
    },
    SITE_ID("SITE_ID_MACRO") {
        @Override
        public String getMacroValue(Request request) {
            return request.getDetectedSite().getSiteGuid();
        }
    },
    BID_FLOOR("BID_FLOOR"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.BID_FLOOR.getCode());
                if(null != urlField)
                    return String.valueOf((Float)(urlField.getUrlFieldProperties().getFieldValue()));
                return null;
            }
            return null;
        }
    },
    EXTERNAL_SITE_ORIGINAL_ID("EXTERNAL_SITE_ORIGINAL_ID"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.EXTERNAL_SITE_ID.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    EXCHANGE_USER_ID("EXCHANGE_USER_ID"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.EXCHANGE_USER_ID.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    KRITTER_USER_ID("KRITTER_USER_ID"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.KRITTER_USER_ID.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    ID_FOR_ADVERTISER("ID_FOR_ADVERTISER"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.ID_FOR_ADVERTISER.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    DEVICE_PLATFORM_ID_SHA1("DEVICE_PLATFORM_ID_SHA1"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.DEVICE_PLATFORM_ID_SHA1.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    DEVICE_PLATFORM_ID_MD5("DEVICE_PLATFORM_ID_MD5"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.DEVICE_PLATFORM_ID_MD5.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    MAC_ADDRESS_SHA1("MAC_ADDRESS_SHA1"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.MAC_ADDRESS_SHA1.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    },
    MAC_ADDRESS_MD5("MAC_ADDRESS_MD5"){
        @Override
        public String getMacroValue(Request request) {
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap) {
                URLField urlField = urlFieldMap.get(URLField.MAC_ADDRESS_MD5.getCode());
                if(null != urlField)
                    return (String)urlField.getUrlFieldProperties().getFieldValue();
                return null;
            }
            return null;
        }
    };

    @Getter
    private String macroString;

    public abstract String getMacroValue(Request request);

    private Macros(String macroString) {
        this.macroString = macroString;
    }
}
