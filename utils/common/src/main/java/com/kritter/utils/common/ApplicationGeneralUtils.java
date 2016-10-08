package com.kritter.utils.common;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.google.common.io.Files;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

public class ApplicationGeneralUtils
{
        private static final String REQUEST_ID_AD_ID_DELIMITER = ":";

        public static final Short DEFAULT_BIDDER_MODEL_ID = -1;
        public static final Integer DEFAULT_COUNTRY_ID = -1;
        public static final Integer DEFAULT_COUNTRY_CARRIER_ID = -1;
        public static final Short DEFAULT_SELECTED_SITE_CATEGORY_ID = -1;
        public static final Integer DEFAULT_INTERNAL_ID_FOR_EXTERNAL_SUPPLY_ATTRIBUTES = -1;
        public static final Integer DEFAULT_ADPOSITION_ID = -1;
        public static final Integer DEFAULT_CHANNEL_ID = -1;
        public static final short DEFAULT_MARKETPLACE_ID = -1;
        public static final int DEFAULT_STATE_ID = -1;
        public static final int DEFAULT_CITY_ID = -1;
        public static final String OS_TARGETING_VERSION_SEPARATOR = "-";
        public static final String URI_PATH_SEPARATOR = "/";
        private static final String BID_SEPARATOR = ",";
        private static final String BID_KEY_VALUE_SEPARATOR = "=";
        private static final String COMMA = ",";
        public static final String PROCESSED_WURFL_FILE_EXTENSION = ".done";
        public static final String PROCESSED_51DEGREES_FILE_EXTENSION = ".done";
        private static final ObjectMapper objectMapper = new ObjectMapper();

        /*For richmedia formatting across ad-exchanges*/
        public static final String RICHMEDIA_PAYLOAD     = "$RICHMEDIA_PAYLOAD";
        public static final String CREATIVE_CSC_BEACON   = "$CREATIVE_CSC_BEACON";
        public static final String HTML_RICHMEDIA_TEMPLATE = prepareRichmediaBannerTemplate();
        public static final String EXCHANGE_BID_REQUEST_DELIM = "|";


        /**********************Conversion event id utils size declarations ***************************************/
        @Getter
        private static ConversionEventIdUtils conversionEventIdUtils;
        static
        {
            short SIZE_INTERNAL_BID = 32;
            short SIZE_ADV_BID = 32;
            short SIZE_AD_ID = 20;
            short SIZE_SITE_ID = 20;
            short SIZE_INV_SRC = 3;
            short SIZE_CARRIER_ID = 24;
            short SIZE_COUNTRY = 14;
            short SIZE_DEVICE_MODEL_ID = 18;
            short SIZE_MANUFACTURER_ID  = 16;
            short SIZE_DEVICE_OS_ID = 10;
            short SIZE_DEVICE_BROWSER_ID = 12;
            short SIZE_EXT_SUPPLY = 32;
            short SIZE_SELECTED_SITE_CAT_ID = 14;
            short SIZE_BIDDER_MODEL_ID = 8;
            short SIZE_SUPPLY_SOURCE_ID = 5;
            short SIZE_CONN_TYPE_ID = 6;
            short SIZE_DEVICE_TYPE_ID = 6;
            short SIZE_CLICK_REQUEST_ID = 128;

            conversionEventIdUtils = new ConversionEventIdUtils
                    (SIZE_INTERNAL_BID,SIZE_ADV_BID,SIZE_AD_ID,SIZE_SITE_ID,
                     SIZE_INV_SRC,SIZE_CARRIER_ID,SIZE_COUNTRY,SIZE_DEVICE_MODEL_ID,
                     SIZE_MANUFACTURER_ID,SIZE_DEVICE_OS_ID,SIZE_DEVICE_BROWSER_ID,
                     SIZE_EXT_SUPPLY,SIZE_SELECTED_SITE_CAT_ID,
                     SIZE_BIDDER_MODEL_ID,SIZE_SUPPLY_SOURCE_ID,
                     SIZE_CONN_TYPE_ID,SIZE_DEVICE_TYPE_ID,SIZE_CLICK_REQUEST_ID);

        }
        /**************************Conversion event id utils instance created***************************************/

        public static String calculateHashForData(String data, String secretKey)
                        throws NoSuchAlgorithmException
        {
                if (null == data || null == secretKey)
                        return null;

                MessageDigest messageDigest = MessageDigest.getInstance("MD5");

                StringBuffer finalData = new StringBuffer(data);
                finalData.append(secretKey);

                messageDigest.reset();

                messageDigest.update(finalData.toString().getBytes());

                byte[] convertedBytes = messageDigest.digest();

                StringBuffer hexString = new StringBuffer();

                for (int i = 0; i < convertedBytes.length; i++) {
                        hexString.append(Integer.toHexString(0xFF & convertedBytes[i]));
                }

                return hexString.toString();
        }
           
        public static String generateImpressionId(String requestId,Integer adIncId)
        {
            StringBuffer result = new StringBuffer(requestId);
            result.append(REQUEST_ID_AD_ID_DELIMITER);
            result.append(Integer.toHexString(adIncId));

            return result.toString();
        }

        public static void logDebug(Logger logger, String... messages)
        {
            if(logger.isDebugEnabled()){
                StringBuffer sb = new StringBuffer();
                for(int i=0;i<messages.length;i++){
                    sb.append(messages[i]);
                }
                logger.debug(sb.toString());
            }

        }

        public static double[] fetchOSTargetingRanges(String range)
        {
            double[] rangeValues = new double[2];

            String parts[] = range.split(OS_TARGETING_VERSION_SEPARATOR);

            try{

                rangeValues[0] = Double.parseDouble(parts[0]);
                rangeValues[1] = Double.parseDouble(parts[1]);
            }catch (NumberFormatException e){

                return null;
            }

            return rangeValues;
        }

        public static Map<AD_BIDS_TYPE,Double> fetchBidTypesWithValues(String encodedBid)
        {
            Base64 base64 = new Base64(0);
            String decodedValue = new String(base64.decode(encodedBid.getBytes()));

            String bidParts[] = decodedValue.split(BID_SEPARATOR);
            String internalBidParts[] = bidParts[0].split(BID_KEY_VALUE_SEPARATOR);
            String externalBidParts[] = bidParts[1].split(BID_KEY_VALUE_SEPARATOR);
            Map<AD_BIDS_TYPE,Double> bidInfo = new HashMap<AD_BIDS_TYPE, Double>();

            bidInfo.put(AD_BIDS_TYPE.INTERNAL_BID,Double.valueOf(internalBidParts[1]));
            bidInfo.put(AD_BIDS_TYPE.ADVERTISER_BID,Double.valueOf(externalBidParts[1]));

            return bidInfo;
        }

        public static String formEncodedBidTypesWithValues(Double internalBid,Double externalAdvertiserBid)
        {
            StringBuffer toEncode = new StringBuffer();

            if(null != internalBid)
            {
                toEncode.append(AD_BIDS_TYPE.INTERNAL_BID.getIdentifier());
                toEncode.append(BID_KEY_VALUE_SEPARATOR);
                toEncode.append(internalBid);
                toEncode.append(BID_SEPARATOR);
            }
            if(null != externalAdvertiserBid)
            {
                toEncode.append(AD_BIDS_TYPE.ADVERTISER_BID.getIdentifier());
                toEncode.append(BID_KEY_VALUE_SEPARATOR);
                toEncode.append(externalAdvertiserBid);
            }

            Base64 base64 = new Base64(0);
            return new String(base64.encode(toEncode.toString().getBytes()));
        }

        /**
        * Class to keep bid types to be used in url formation and then extraction from it.
        */
        public enum AD_BIDS_TYPE
        {
            INTERNAL_BID(1, "i"),
            ADVERTISER_BID(2, "e");

            @Getter
            private int code;
            @Getter
            private String identifier;

            private AD_BIDS_TYPE(int code,String identifier)
            {
                this.code = code;
                this.identifier = identifier;
            }
        }

        public static int getDayOfWeek()
        {
            Date date = new Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c.get(Calendar.DAY_OF_WEEK);
        }

        public static int getTimeWindowId()
        {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            return ((hour * 60) + minutes) / 30;
        }

        public static short getHourOfDay()
        {
            Calendar calendar = Calendar.getInstance();
            return (short)calendar.get(Calendar.HOUR_OF_DAY);
        }

        public static String findCorrectIpAddressForDetection(String requestAddress,
                                                              String xForwardedFor,
                                                              List<String> privateAddressPrefixList)
        {
            String ip = requestAddress;

            if(null != xForwardedFor)
            {
                String parts[] = xForwardedFor.split(COMMA);
                if(null != parts && parts.length > 0)
                {
                    for(String part: parts)
                    {
                        part = part.trim();

                        //if the ip part is empty or null skip it...
                        if(StringUtils.isEmpty(part))
                            continue;

                        Iterator<String> privateIpIterator = privateAddressPrefixList.iterator();

                        boolean isPrivateIp = false;

                        while (privateIpIterator.hasNext())
                        {
                            if(part.startsWith(privateIpIterator.next()))
                            {
                                isPrivateIp = true;
                            }
                        }

                        if(!isPrivateIp)
                        {
                            ip = part;
                            break;
                        }
                    }
                }
            }

            return ip;
        }

    public static Map<Integer,Object> fetchDecodedConversionInfo(String encodedConversionData) throws IOException
    {

        ConversionUrlData conversionUrlData = null;

        try
        {
            conversionUrlData = conversionEventIdUtils.prepareConversionUrlDataFromConversionEventPrimaryId(encodedConversionData);

            Map<Integer,Object> dataForConversionFeedback = new HashMap<Integer, Object>();

            dataForConversionFeedback.put(CONVERSION_DATA_KEY.INTERNAL_BID.getKey(),conversionUrlData.getInternalBid());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.ADVERTISER_BID.getKey(),conversionUrlData.getAdvertiserBid());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.AD_ID.getKey(),conversionUrlData.getAdId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.SITE_ID.getKey(),conversionUrlData.getSiteId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.INVENTORY_SOURCE.getKey(),conversionUrlData.getInventorySource());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.CLICK_REQUEST_ID.getKey(),conversionUrlData.getClickRequestId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.CARRIER_ID.getKey(),conversionUrlData.getCarrierId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.COUNTRY_ID.getKey(),conversionUrlData.getCountryId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_MODEL_ID.getKey(),conversionUrlData.getDeviceModelId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_MANUFACTURER_ID.getKey(),conversionUrlData.getDeviceManufacturerId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_OS_ID.getKey(),conversionUrlData.getDeviceOsId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_BROWSER_ID.getKey(),conversionUrlData.getDeviceBrowserId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.SELECTED_SITE_CATEGORY_ID.getKey(),conversionUrlData.getSelectedSiteCategoryId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.BIDDER_MODEL_ID.getKey(),conversionUrlData.getBidderModelId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.SUPPLY_SOURCE_TYPE.getKey(),conversionUrlData.getSupplySourceType());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.INT_ID_EXT_SUPPLY_ATTR.getKey(),conversionUrlData.getExternalSupplyAttributesInternalId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.CONNECTION_TYPE_ID.getKey(),conversionUrlData.getConnectionTypeId());
            dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_TYPE_ID.getKey(),conversionUrlData.getDeviceTypeId());

            return dataForConversionFeedback;
        }
        catch (Exception e)
        {
            throw new IOException("Exception inside fetchDecodedConversionInfo ",e);
        }
    }

    public static String fetchCommonEncodedDataForConversionFeedback
                                                        (ConversionUrlData conversionUrlData) throws IOException
    {
        return fetchEncodedDataUsingBitArrayForConversionFeedback(conversionUrlData);
    }

    @Deprecated
    private static String fetchEncodedDataForConversionFeedback(
                                                                Double internalBid,
                                                                Double advertiserBid,
                                                                Integer adId,
                                                                Integer siteId,
                                                                Integer inventorySource,
                                                                String clickRequestId,
                                                                Integer carrierId,
                                                                Integer countryId,
                                                                Integer deviceModelId,
                                                                Integer deviceManufacturerId,
                                                                Integer deviceOsId,
                                                                Integer deviceBrowserId,
                                                                Short selectedSiteCategoryId,
                                                                Short bidderModelId,
                                                                Short supplySourceType,
                                                                Integer externalSupplyAttributesInternalId,
                                                                Short connectionTypeId
                                                               ) throws IOException
    {
        Map<Integer,Object> dataForConversionFeedback = new HashMap<Integer, Object>();

        dataForConversionFeedback.put(CONVERSION_DATA_KEY.INTERNAL_BID.getKey(),internalBid);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.ADVERTISER_BID.getKey(),advertiserBid);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.AD_ID.getKey(),adId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.SITE_ID.getKey(),siteId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.INVENTORY_SOURCE.getKey(),inventorySource);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.CLICK_REQUEST_ID.getKey(),clickRequestId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.CARRIER_ID.getKey(),carrierId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.COUNTRY_ID.getKey(),countryId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_MODEL_ID.getKey(),deviceModelId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_MANUFACTURER_ID.getKey(),deviceManufacturerId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_OS_ID.getKey(),deviceOsId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.DEVICE_BROWSER_ID.getKey(),deviceBrowserId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.SELECTED_SITE_CATEGORY_ID.getKey(),selectedSiteCategoryId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.BIDDER_MODEL_ID.getKey(),bidderModelId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.SUPPLY_SOURCE_TYPE.getKey(),supplySourceType);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.INT_ID_EXT_SUPPLY_ATTR.getKey(),externalSupplyAttributesInternalId);
        dataForConversionFeedback.put(CONVERSION_DATA_KEY.CONNECTION_TYPE_ID.getKey(),connectionTypeId);

        String jsonString = objectMapper.writeValueAsString(dataForConversionFeedback);

        Base64 base64 = new Base64(0);
        return new String(base64.encode(jsonString.getBytes()));
    }

    private static String fetchEncodedDataUsingBitArrayForConversionFeedback
                                                            (ConversionUrlData conversionUrlData) throws IOException
    {
        try
        {
            return conversionEventIdUtils.setConversionDataAndPrepareConversionPrimaryEventIdVersion1(conversionUrlData);
        }
        catch (Exception e)
        {
            throw new IOException("Exception inside fetchEncodedDataUsingBitArrayForConversionFeedback ", e);
        }
    }

    public enum CONVERSION_DATA_KEY
    {
        INTERNAL_BID(1),
        ADVERTISER_BID(2),
        AD_ID(3),
        SITE_ID(4),
        INVENTORY_SOURCE(5),
        CLICK_REQUEST_ID(6),
        CARRIER_ID(7),
        COUNTRY_ID(8),
        DEVICE_MODEL_ID(9),
        DEVICE_MANUFACTURER_ID(10),
        DEVICE_OS_ID(11),
        SELECTED_SITE_CATEGORY_ID(12),
        BIDDER_MODEL_ID(13),
        DEVICE_BROWSER_ID(14),
        SUPPLY_SOURCE_TYPE(15),
        INT_ID_EXT_SUPPLY_ATTR(16),
        CONNECTION_TYPE_ID(17),
        DEVICE_TYPE_ID(18);

        @Getter
        private Integer key;

        private CONVERSION_DATA_KEY(Integer key)
        {
            this.key = key;
        }
    }

    /**
     * This function moves a source file to destination file atomically.
     * @param sourceFile
     * @param destinationFile
     * @throws IOException
     */
    public static void moveFileAtomicallyToDestination(String sourceFile,String destinationFile) throws IOException
    {
        File source = new File(sourceFile);
        File destination = new File(destinationFile);
        Files.move(source, destination);
    }

    private static String prepareRichmediaBannerTemplate()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(RICHMEDIA_PAYLOAD);
        sb.append("<img src=\"");
        sb.append(CREATIVE_CSC_BEACON);
        sb.append("\" style=\"display: none;\"/>");
        return sb.toString();
    }

    /*constants for url modifications*/
    public static final String URL_QUERY_BEGIN_QUESTION_MARK = "?";
    public static final String URL_QUERY_PARAM_DELIMITER = "&";
    public static final String URL_PARAM_VALUE_DELIMITER = "=";
    public static final String EXCHANGE_USER_ID_PARAM_NAME = "eid";
    public static final String KRITTER_USER_ID_PARAM_NAME = "kid";
    public static final String ADSERVING_POSTIMPRESSION_INFO_PARAM_NAME = "iid";

    public static String modifyURLForQueryParamStart(String url)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(url);

        if(!url.contains(URL_QUERY_BEGIN_QUESTION_MARK))
            sb.append(URL_QUERY_BEGIN_QUESTION_MARK);
        if(!url.contains(URL_QUERY_PARAM_DELIMITER))
            sb.append(URL_QUERY_PARAM_DELIMITER);

        return sb.toString();
    }
    public static String modifyCSCURLForUserIds(
                                                String exchangeUserIdValue,
                                                String kritterUserIdValue,
                                                String cscURL
                                               )
    {
        if(null == exchangeUserIdValue && null == kritterUserIdValue)
            return cscURL;

        StringBuffer cscUrlToModify = new StringBuffer();
        cscUrlToModify.append(modifyURLForQueryParamStart(cscURL));

        if(null != exchangeUserIdValue)
        {
            cscUrlToModify.append(EXCHANGE_USER_ID_PARAM_NAME);
            cscUrlToModify.append(URL_PARAM_VALUE_DELIMITER);
            cscUrlToModify.append(exchangeUserIdValue);
        }

        if(null != kritterUserIdValue)
        {
            if(!cscUrlToModify.toString().endsWith(URL_QUERY_PARAM_DELIMITER))
                cscUrlToModify.append(URL_QUERY_PARAM_DELIMITER);

            cscUrlToModify.append(KRITTER_USER_ID_PARAM_NAME);
            cscUrlToModify.append(URL_PARAM_VALUE_DELIMITER);
            cscUrlToModify.append(kritterUserIdValue);
        }

        return cscUrlToModify.toString();
    }
}