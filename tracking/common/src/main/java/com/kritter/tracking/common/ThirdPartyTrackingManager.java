package com.kritter.tracking.common;

import com.kritter.tracking.common.entity.ThirdPartyTrackingData;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines methods to be implemented for integrating
 * any third party tracking system providers like Matomy, MAT,etc.
 */
public class ThirdPartyTrackingManager
{
    private Logger logger;
    private String encodedConversionDataParameter;

    public ThirdPartyTrackingManager(String loggerName,
                                     String encodedConversionDataParameter)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.encodedConversionDataParameter = encodedConversionDataParameter;
    }

    /**
     * This function must be overriden for each tracking partner...
     *
     * This function must modify the final landing url upon click event
     * and sends conversion data appended to it.
     * For manufacturing the conversion data, ThirdPartyTrackingData
     * is used which has specific implementations as per the tracker
     * involved, like for matomy we might send some specific
     * parameters and for MAT some other parameters.
     * @param landingUrl
     * @param thirdPartyTrackingData
     * @return
     */
    public String modifyLandingUrlWithConversionData(String landingUrl,ThirdPartyTrackingData thirdPartyTrackingData)
    {
        return landingUrl;
    }

    /**
     * This function takes input as parameters received from conversion
     * url S2S from a third party, use them as per the third party
     * involved and extract universal conversion information.
     * This can be overriden by specific thrid party implementation if
     * required, this function has universal conversion info reading
     * capability for now which are required in billing and click
     * association.
     * @param httpServletRequest
     * @return
     */
    public ThirdPartyTrackingData decipherConversionDataFromTrackingPartner(HttpServletRequest httpServletRequest,
                                                                            String conversionId)
    {
        Map<String,String[]> requestParameters = httpServletRequest.getParameterMap();

        String[] encodedConversionData = null;

        if(null != conversionId)
        {
            encodedConversionData = new String[1];
            encodedConversionData[0] = conversionId;
        }
        else if(null != requestParameters && requestParameters.size() > 0)
        {
            encodedConversionData = requestParameters.get(this.encodedConversionDataParameter);
        }

        if(null == encodedConversionData || encodedConversionData.length == 0)
        {
            this.logger.error("EncodedConversionData from ThirdPary S2S conversion URL is null... no data to capture");
            return null;
        }

        this.logger.debug("EncodedConversionData received from URL: {}" ,  encodedConversionData[0]);

        Map<Integer,Object> conversionDataMap = null;

        try
        {
            conversionDataMap = ApplicationGeneralUtils.fetchDecodedConversionInfo(encodedConversionData[0]);

            //since conversion data is important log it in error mode.
            this.logger.error("NO ERROR!! ImportantInfo: ConversionDataInfo {} ", conversionDataMap);

            ThirdPartyTrackingData thirdPartyTrackingData = new ThirdPartyTrackingData();

            //get individual parameter values and set, even if null then null would be set.
            thirdPartyTrackingData.setInternalBid((Double)conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.INTERNAL_BID.getKey()));
            thirdPartyTrackingData.setAdvertiserBid((Double)conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.ADVERTISER_BID.getKey()));
            thirdPartyTrackingData.setAdId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.AD_ID.getKey()));
            thirdPartyTrackingData.setSiteId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.SITE_ID.getKey()));
            thirdPartyTrackingData.setInventorySource((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.INVENTORY_SOURCE.getKey()));
            thirdPartyTrackingData.setClickRequestId((String) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.CLICK_REQUEST_ID.getKey()));
            thirdPartyTrackingData.setCarrierId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.CARRIER_ID.getKey()));
            thirdPartyTrackingData.setCountryId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.COUNTRY_ID.getKey()));
            thirdPartyTrackingData.setDeviceModelId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.DEVICE_MODEL_ID.getKey()));
            thirdPartyTrackingData.setDeviceManufacturerId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.DEVICE_MANUFACTURER_ID.getKey()));
            thirdPartyTrackingData.setDeviceOsId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.DEVICE_OS_ID.getKey()));
            thirdPartyTrackingData.setDeviceBrowserId((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.DEVICE_BROWSER_ID.getKey()));
            thirdPartyTrackingData.setSelectedSiteCategoryId((Short)conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.SELECTED_SITE_CATEGORY_ID.getKey()));
            thirdPartyTrackingData.setBidderModelId(((Short) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.BIDDER_MODEL_ID.getKey())).shortValue());
            thirdPartyTrackingData.setSupplySourceType(((Short) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.SUPPLY_SOURCE_TYPE.getKey())).shortValue());
            thirdPartyTrackingData.setExternalSupplyAttributesInternalId(((Integer) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.INT_ID_EXT_SUPPLY_ATTR.getKey())).intValue());
            thirdPartyTrackingData.setConnectionTypeId(((Short) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.CONNECTION_TYPE_ID.getKey())).shortValue());
            thirdPartyTrackingData.setDeviceTypeId(((Short) conversionDataMap.get(ApplicationGeneralUtils.CONVERSION_DATA_KEY.DEVICE_TYPE_ID.getKey())).shortValue());
            return thirdPartyTrackingData;
        }
        catch (IOException ioe)
        {
            this.logger.error("IOException inside ThirdPartyTrackingManager ",ioe);
            return null;
        }
    }
}
