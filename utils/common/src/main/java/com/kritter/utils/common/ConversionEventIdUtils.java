package com.kritter.utils.common;

import java.net.URLEncoder;
import java.util.*;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;

/**
 * This class has utility methods for conversion event handling
 * in the application.
 * Parameters that are currently used in conversion event and
 * number of bits that can be assigned to them for creating
 * encoded UTF-8 string, with example values provided.
 *
 * version                                                           // 8
 * double internalBid = 0.001f;                                      // 32
 * double advertiserBid = 0.003f;                                    // 32
 * int adId = 1234;                                                  // 20
 * int siteId = 3456;                                                // 20
 * int inventorySource = 2;                                          // 3
 * int carrierId = 2345;                                             // 24
 * int countryId = 12;                                               // 14
 * int deviceModelId = 34;                                           // 18
 * int deviceManufacturerId = 41;                                    // 16
 * int deviceOsId = 11;                                              // 10
 * int deviceBrowserId = 45;                                         // 12
 * int externalSupplyAttributesInternalId = 5678899;                 // 32
 * short selectedSiteCategoryId = 9;                                 // 14
 * short bidderModelId = 2;                                          // 8
 * short supplySourceType = 2;                                       // 5
 * short connectionTypeId = 2;                                       // 6
 * short deviceTypeId = 2;                                           // 6
 * String clickRequestId = "011c3e84-ecd5-d501-4f8d-039f7f000003";   // 128
 *  for clickRequestId use two longs as msb,lsb in uuid.
 */

public class ConversionEventIdUtils
{
    public static final short VERSION_1_VALUE_PRIMARY_EVENT_ID = 1;

    private static final short SIZE_VERSION_PRIMARY_EVENT_ID = 8;
    private short SIZE_INTERNAL_BID;
    private short SIZE_ADVERTISER_BID;
    private short SIZE_AD_ID;
    private short SIZE_SITE_ID;
    private short SIZE_INVENTORY_SOURCE;
    private short SIZE_CARRIER_ID;
    private short SIZE_COUNTRY_ID;
    private short SIZE_DEVICE_MODEL_ID;
    private short SIZE_MANUFACTURER_ID;
    private short SIZE_DEVICE_OS_ID;
    private short SIZE_DEVICE_BROWSER_ID;
    private short SIZE_EXT_SUPPLY_ATTR_ID;
    private short SIZE_SELECTED_SITE_CAT_ID;
    private short SIZE_BIDDER_MODEL_ID;
    private short SIZE_SUPPLY_SOURCE_TYPE;
    private short SIZE_CONNECTION_TYPE_ID;
    private short SIZE_DEVICE_TYPE_ID;
    private short SIZE_CLICK_REQUEST_ID;

    private static final short START_INDEX_VERSION_PRIMARY_EVENT_ID = 0;
    private short START_INDEX_INTERNAL_BID;
    private short START_INDEX_ADVERTISER_BID;
    private short START_INDEX_AD_ID;
    private short START_INDEX_SITE_ID;
    private short START_INDEX_INVENTORY_SOURCE;
    private short START_INDEX_CARRIER_ID;
    private short START_INDEX_COUNTRY_ID;
    private short START_INDEX_DEVICE_MODEL_ID;
    private short START_INDEX_MANUFACTURER_ID;
    private short START_INDEX_DEVICE_OS_ID;
    private short START_INDEX_DEVICE_BROWSER_ID;
    private short START_INDEX_EXT_SUPPLY_ATTR_ID;
    private short START_INDEX_SELECTED_SITE_CAT_ID;
    private short START_INDEX_BIDDER_MODEL_ID;
    private short START_INDEX_SUPPLY_SOURCE_TYPE;
    private short START_INDEX_CONNECTION_TYPE_ID;
    private short START_INDEX_DEVICE_TYPE_ID;
    private short START_INDEX_CLICK_REQUEST_ID;

    private int totalBitSetSizeVersion1;

    @Setter
    private boolean replaceCharactersInConversionId;
    private static final String DOUBLE_UNDERSCORE = "__";
    private static final String DOUBLE_UNDERSCORE_REPLACEMENT = "\\.";
    private static final String DOUBLE_DASH = "--";
    private static final String DOUBLE_DASH_REPLACEMENT = "\\*";

    public ConversionEventIdUtils(
                                   short SIZE_INTERNAL_BID,
                                   short SIZE_ADVERTISER_BID,
                                   short SIZE_AD_ID,
                                   short SIZE_SITE_ID,
                                   short SIZE_INVENTORY_SOURCE,
                                   short SIZE_CARRIER_ID,
                                   short SIZE_COUNTRY_ID,
                                   short SIZE_DEVICE_MODEL_ID,
                                   short SIZE_MANUFACTURER_ID,
                                   short SIZE_DEVICE_OS_ID,
                                   short SIZE_DEVICE_BROWSER_ID,
                                   short SIZE_EXT_SUPPLY_ATTR_ID,
                                   short SIZE_SELECTED_SITE_CAT_ID,
                                   short SIZE_BIDDER_MODEL_ID,
                                   short SIZE_SUPPLY_SOURCE_TYPE,
                                   short SIZE_CONNECTION_TYPE_ID,
                                   short SIZE_DEVICE_TYPE_ID,
                                   short SIZE_CLICK_REQUEST_ID
                                 )
    {
        this.SIZE_INTERNAL_BID = SIZE_INTERNAL_BID;
        this.SIZE_ADVERTISER_BID = SIZE_ADVERTISER_BID;
        this.SIZE_AD_ID = SIZE_AD_ID;
        this.SIZE_SITE_ID = SIZE_SITE_ID;
        this.SIZE_INVENTORY_SOURCE = SIZE_INVENTORY_SOURCE;
        this.SIZE_CARRIER_ID = SIZE_CARRIER_ID;
        this.SIZE_COUNTRY_ID = SIZE_COUNTRY_ID;
        this.SIZE_DEVICE_MODEL_ID = SIZE_DEVICE_MODEL_ID;
        this.SIZE_MANUFACTURER_ID = SIZE_MANUFACTURER_ID;
        this.SIZE_DEVICE_OS_ID = SIZE_DEVICE_OS_ID;
        this.SIZE_DEVICE_BROWSER_ID = SIZE_DEVICE_BROWSER_ID;
        this.SIZE_EXT_SUPPLY_ATTR_ID = SIZE_EXT_SUPPLY_ATTR_ID;
        this.SIZE_SELECTED_SITE_CAT_ID = SIZE_SELECTED_SITE_CAT_ID;
        this.SIZE_BIDDER_MODEL_ID = SIZE_BIDDER_MODEL_ID;
        this.SIZE_SUPPLY_SOURCE_TYPE = SIZE_SUPPLY_SOURCE_TYPE;
        this.SIZE_CONNECTION_TYPE_ID = SIZE_CONNECTION_TYPE_ID;
        this.SIZE_DEVICE_TYPE_ID = SIZE_DEVICE_TYPE_ID;
        this.SIZE_CLICK_REQUEST_ID = SIZE_CLICK_REQUEST_ID;

        totalBitSetSizeVersion1 = calculateBitSetSizeForVersion1();
        setStartIndexForParametersOfVersion1();
    }

    private int calculateBitSetSizeForVersion1()
    {

        return
            SIZE_VERSION_PRIMARY_EVENT_ID +
            SIZE_INTERNAL_BID             +
            SIZE_ADVERTISER_BID           +
            SIZE_AD_ID                    +
            SIZE_SITE_ID                  +
            SIZE_INVENTORY_SOURCE         +
            SIZE_CARRIER_ID               +
            SIZE_COUNTRY_ID               +
            SIZE_DEVICE_MODEL_ID          +
            SIZE_MANUFACTURER_ID          +
            SIZE_DEVICE_OS_ID             +
            SIZE_DEVICE_BROWSER_ID        +
            SIZE_EXT_SUPPLY_ATTR_ID       +
            SIZE_SELECTED_SITE_CAT_ID     +
            SIZE_BIDDER_MODEL_ID          +
            SIZE_SUPPLY_SOURCE_TYPE       +
            SIZE_CONNECTION_TYPE_ID       +
            SIZE_DEVICE_TYPE_ID           +
            SIZE_CLICK_REQUEST_ID;
    }

    private void setStartIndexForParametersOfVersion1()
    {
        START_INDEX_INTERNAL_BID   = (short)(START_INDEX_VERSION_PRIMARY_EVENT_ID + SIZE_VERSION_PRIMARY_EVENT_ID);
        START_INDEX_ADVERTISER_BID = (short)(START_INDEX_INTERNAL_BID + SIZE_INTERNAL_BID);
        START_INDEX_AD_ID = (short)(START_INDEX_ADVERTISER_BID + SIZE_ADVERTISER_BID);
        START_INDEX_SITE_ID = (short)(START_INDEX_AD_ID + SIZE_AD_ID);
        START_INDEX_INVENTORY_SOURCE = (short)(START_INDEX_SITE_ID + SIZE_SITE_ID);
        START_INDEX_CARRIER_ID = (short)(START_INDEX_INVENTORY_SOURCE + SIZE_INVENTORY_SOURCE);
        START_INDEX_COUNTRY_ID = (short)(START_INDEX_CARRIER_ID + SIZE_CARRIER_ID);
        START_INDEX_DEVICE_MODEL_ID = (short)(START_INDEX_COUNTRY_ID + SIZE_COUNTRY_ID);
        START_INDEX_MANUFACTURER_ID = (short)(START_INDEX_DEVICE_MODEL_ID + SIZE_DEVICE_MODEL_ID);
        START_INDEX_DEVICE_OS_ID = (short)(START_INDEX_MANUFACTURER_ID + SIZE_MANUFACTURER_ID);
        START_INDEX_DEVICE_BROWSER_ID = (short)(START_INDEX_DEVICE_OS_ID + SIZE_DEVICE_OS_ID);
        START_INDEX_EXT_SUPPLY_ATTR_ID = (short)(START_INDEX_DEVICE_BROWSER_ID + SIZE_DEVICE_BROWSER_ID);
        START_INDEX_SELECTED_SITE_CAT_ID = (short)(START_INDEX_EXT_SUPPLY_ATTR_ID + SIZE_EXT_SUPPLY_ATTR_ID);
        START_INDEX_BIDDER_MODEL_ID = (short)(START_INDEX_SELECTED_SITE_CAT_ID + SIZE_SELECTED_SITE_CAT_ID);
        START_INDEX_SUPPLY_SOURCE_TYPE = (short)(START_INDEX_BIDDER_MODEL_ID + SIZE_BIDDER_MODEL_ID);
        START_INDEX_CONNECTION_TYPE_ID = (short)(START_INDEX_SUPPLY_SOURCE_TYPE + SIZE_SUPPLY_SOURCE_TYPE);
        START_INDEX_DEVICE_TYPE_ID = (short)(START_INDEX_CONNECTION_TYPE_ID + SIZE_CONNECTION_TYPE_ID);
        START_INDEX_CLICK_REQUEST_ID = (short)(START_INDEX_DEVICE_TYPE_ID + SIZE_DEVICE_TYPE_ID);
    }

    public String setConversionDataAndPrepareConversionPrimaryEventIdVersion1
                                                    (ConversionUrlData conversionUrlData) throws Exception
    {
        int[] bitSetConversionEventData = new int[this.totalBitSetSizeVersion1];

        if(null == conversionUrlData)
            return null;

        if(
           null == conversionUrlData.getInternalBid() ||
           null == conversionUrlData.getAdvertiserBid() ||
           null == conversionUrlData.getAdId() ||
           null == conversionUrlData.getSiteId() ||
           null == conversionUrlData.getInventorySource() ||
           null == conversionUrlData.getCarrierId() ||
           null == conversionUrlData.getCountryId() ||
           null == conversionUrlData.getDeviceModelId() ||
           null == conversionUrlData.getDeviceManufacturerId() ||
           null == conversionUrlData.getDeviceOsId() ||
           null == conversionUrlData.getDeviceBrowserId() ||
           null == conversionUrlData.getExternalSupplyAttributesInternalId() ||
           null == conversionUrlData.getSelectedSiteCategoryId() ||
           null == conversionUrlData.getBidderModelId() ||
           null == conversionUrlData.getSupplySourceType() ||
           null == conversionUrlData.getConnectionTypeId() ||
           null == conversionUrlData.getClickRequestId() ||
           null == conversionUrlData.getDeviceTypeId()
          )
        {
            throw new Exception("One of the conversion data attributes is missing for ConversionUtils inside method " +
                                "setConversionDataAndPrepareConversionPrimaryEventIdVersion1");
        }


        if(!isShortVariableFitting(VERSION_1_VALUE_PRIMARY_EVENT_ID,SIZE_VERSION_PRIMARY_EVENT_ID))
        {
            throw new Exception("Version 1 value is not fitting into bit size defined " + SIZE_VERSION_PRIMARY_EVENT_ID);
        }
        int[] bitArray = fetchBitArrayForShort(VERSION_1_VALUE_PRIMARY_EVENT_ID,SIZE_VERSION_PRIMARY_EVENT_ID);

        setBitSetToStorage(START_INDEX_VERSION_PRIMARY_EVENT_ID, SIZE_VERSION_PRIMARY_EVENT_ID, bitArray, bitSetConversionEventData);

        bitArray = fetchBitArrayForFloat(conversionUrlData.getInternalBid().floatValue(),SIZE_INTERNAL_BID);
        setBitSetToStorage(START_INDEX_INTERNAL_BID,SIZE_INTERNAL_BID,bitArray,bitSetConversionEventData);

        bitArray = fetchBitArrayForFloat(conversionUrlData.getAdvertiserBid().floatValue(),SIZE_ADVERTISER_BID);
        setBitSetToStorage(START_INDEX_ADVERTISER_BID,SIZE_ADVERTISER_BID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getAdId(), SIZE_AD_ID))
        {
            throw new Exception("AdId value is not fitting into bit size defined " + SIZE_AD_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getAdId(),SIZE_AD_ID);
        setBitSetToStorage(START_INDEX_AD_ID,SIZE_AD_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getSiteId(),SIZE_SITE_ID))
        {
            throw new Exception("SiteId value is not fitting into bit size defined " + SIZE_SITE_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getSiteId(), SIZE_SITE_ID);
        setBitSetToStorage(START_INDEX_SITE_ID,SIZE_SITE_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getInventorySource(),SIZE_INVENTORY_SOURCE))
        {
            throw new Exception("Inventory Source value is not fitting into bit size defined " + SIZE_INVENTORY_SOURCE);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getInventorySource(), SIZE_INVENTORY_SOURCE);
        setBitSetToStorage(START_INDEX_INVENTORY_SOURCE,SIZE_INVENTORY_SOURCE,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getAdId(),SIZE_CARRIER_ID))
        {
            throw new Exception("CarrierId value is not fitting into bit size defined " + SIZE_CARRIER_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getCarrierId(), SIZE_CARRIER_ID);
        setBitSetToStorage(START_INDEX_CARRIER_ID,SIZE_CARRIER_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getCountryId(),SIZE_COUNTRY_ID))
        {
            throw new Exception("CountryId value is not fitting into bit size defined " + SIZE_COUNTRY_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getCountryId(), SIZE_COUNTRY_ID);
        setBitSetToStorage(START_INDEX_COUNTRY_ID,SIZE_COUNTRY_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getDeviceModelId(),SIZE_DEVICE_MODEL_ID))
        {
            throw new Exception("DeviceModelId value is not fitting into bit size defined " + SIZE_DEVICE_MODEL_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getDeviceModelId(), SIZE_DEVICE_MODEL_ID);
        setBitSetToStorage(START_INDEX_DEVICE_MODEL_ID,SIZE_DEVICE_MODEL_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getDeviceManufacturerId(),SIZE_MANUFACTURER_ID))
        {
            throw new Exception("ManufacturerId value is not fitting into bit size defined " + SIZE_MANUFACTURER_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getDeviceManufacturerId(), SIZE_MANUFACTURER_ID);
        setBitSetToStorage(START_INDEX_MANUFACTURER_ID,SIZE_MANUFACTURER_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getDeviceOsId(),SIZE_DEVICE_OS_ID))
        {
            throw new Exception("DeviceOsId value is not fitting into bit size defined " + SIZE_DEVICE_OS_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getDeviceOsId(), SIZE_DEVICE_OS_ID);
        setBitSetToStorage(START_INDEX_DEVICE_OS_ID,SIZE_DEVICE_OS_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getDeviceBrowserId(),SIZE_DEVICE_BROWSER_ID))
        {
            throw new Exception("DeviceBrowserId value is not fitting into bit size defined " + SIZE_DEVICE_BROWSER_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getDeviceBrowserId(),SIZE_DEVICE_BROWSER_ID);
        setBitSetToStorage(START_INDEX_DEVICE_BROWSER_ID,SIZE_DEVICE_BROWSER_ID,bitArray,bitSetConversionEventData);

        if(!isIntegerVariableFitting(conversionUrlData.getExternalSupplyAttributesInternalId(),SIZE_EXT_SUPPLY_ATTR_ID))
        {
            throw new Exception("Size ExtSupplyAttr value is not fitting into bit size defined " + SIZE_EXT_SUPPLY_ATTR_ID);
        }
        bitArray = fetchBitArrayForInteger(conversionUrlData.getExternalSupplyAttributesInternalId(),SIZE_EXT_SUPPLY_ATTR_ID);
        setBitSetToStorage(START_INDEX_EXT_SUPPLY_ATTR_ID,SIZE_EXT_SUPPLY_ATTR_ID,bitArray,bitSetConversionEventData);

        if(!isShortVariableFitting(conversionUrlData.getSelectedSiteCategoryId(), SIZE_SELECTED_SITE_CAT_ID))
        {
            throw new Exception("Selected site catid value is not fitting into bit size defined " + SIZE_SELECTED_SITE_CAT_ID);
        }
        bitArray = fetchBitArrayForShort(conversionUrlData.getSelectedSiteCategoryId(),SIZE_SELECTED_SITE_CAT_ID);
        setBitSetToStorage(START_INDEX_SELECTED_SITE_CAT_ID,SIZE_SELECTED_SITE_CAT_ID,bitArray,bitSetConversionEventData);

        if(!isShortVariableFitting(conversionUrlData.getBidderModelId(), SIZE_BIDDER_MODEL_ID))
        {
            throw new Exception("bidder model value is not fitting into bit size defined " + SIZE_BIDDER_MODEL_ID);
        }
        bitArray = fetchBitArrayForShort(conversionUrlData.getBidderModelId(), SIZE_BIDDER_MODEL_ID);
        setBitSetToStorage(START_INDEX_BIDDER_MODEL_ID,SIZE_BIDDER_MODEL_ID,bitArray,bitSetConversionEventData);

        if(!isShortVariableFitting(conversionUrlData.getSupplySourceType(),SIZE_SUPPLY_SOURCE_TYPE))
        {
            throw new Exception("supply source type value is not fitting into bit size defined " + SIZE_SUPPLY_SOURCE_TYPE);
        }
        bitArray = fetchBitArrayForShort(conversionUrlData.getSupplySourceType(), SIZE_SUPPLY_SOURCE_TYPE);
        setBitSetToStorage(START_INDEX_SUPPLY_SOURCE_TYPE,SIZE_SUPPLY_SOURCE_TYPE,bitArray,bitSetConversionEventData);

        if(!isShortVariableFitting(conversionUrlData.getConnectionTypeId(),SIZE_CONNECTION_TYPE_ID))
        {
            throw new Exception("connection typeid value is not fitting into bit size defined " + SIZE_CONNECTION_TYPE_ID);
        }
        bitArray = fetchBitArrayForShort(conversionUrlData.getConnectionTypeId(), SIZE_CONNECTION_TYPE_ID);
        setBitSetToStorage(START_INDEX_CONNECTION_TYPE_ID,SIZE_CONNECTION_TYPE_ID,bitArray,bitSetConversionEventData);

        if(!isShortVariableFitting(conversionUrlData.getDeviceTypeId(),SIZE_DEVICE_TYPE_ID))
        {
            throw new Exception("device typeid value is not fitting into bit size defined " + SIZE_DEVICE_TYPE_ID);
        }
        bitArray = fetchBitArrayForShort(conversionUrlData.getDeviceTypeId(), SIZE_DEVICE_TYPE_ID);
        setBitSetToStorage(START_INDEX_DEVICE_TYPE_ID,SIZE_DEVICE_TYPE_ID,bitArray,bitSetConversionEventData);

        UUID uuid = UUID.fromString(conversionUrlData.getClickRequestId());
        long leastSignificantBits = uuid.getLeastSignificantBits();
        long mostSignificantBits = uuid.getMostSignificantBits();

        if(!isLongVariableFitting(leastSignificantBits, (short) (SIZE_CLICK_REQUEST_ID / 2)))
        {
            throw new Exception("lsb of clickid value is not fitting into bit size defined " + (SIZE_CLICK_REQUEST_ID/2));
        }
        bitArray = fetchBitArrayForLong(leastSignificantBits, (short) (SIZE_CLICK_REQUEST_ID / 2));
        setBitSetToStorage(START_INDEX_CLICK_REQUEST_ID,SIZE_CLICK_REQUEST_ID/2,bitArray,bitSetConversionEventData);

        if(!isLongVariableFitting(mostSignificantBits,(short) (SIZE_CLICK_REQUEST_ID/2)))
        {
            throw new Exception("msb of clickid value is not fitting into bit size defined " + (SIZE_CLICK_REQUEST_ID/2));
        }
        bitArray = fetchBitArrayForLong(mostSignificantBits,(short)(SIZE_CLICK_REQUEST_ID/2));
        setBitSetToStorage((START_INDEX_CLICK_REQUEST_ID + (SIZE_CLICK_REQUEST_ID/2)),SIZE_CLICK_REQUEST_ID/2,bitArray,bitSetConversionEventData);

        Base64 base64 = new Base64(0,null,true);
        byte[] bytesToEncode = fetchByteArrayFromBitArrayWithoutShifting(bitSetConversionEventData);
        String encodedData = new String(base64.encode(bytesToEncode));

        /*Code block where we can choose to replace characters based on client config if required.*/
        if(replaceCharactersInConversionId)
        {
            encodedData = encodedData.replaceAll(DOUBLE_UNDERSCORE,DOUBLE_UNDERSCORE_REPLACEMENT);
            encodedData = encodedData.replaceAll(DOUBLE_DASH,DOUBLE_UNDERSCORE_REPLACEMENT);
        }

        return encodedData;
    }

    private static void printBitArray(int[] bits) {
        for(int i = 0; i < bits.length; ++i) {
            System.out.print(bits[i]);
            if(i != bits.length - 1)
                System.out.print(",");
        }
        System.out.println();
    }

    private void setBitSetToStorage(int startIndex,int size,int[] value,int[] bitSetConversionEventData)
    {
        int counter = 0;

        for(int i = startIndex;i < (startIndex + size); i++)
        {
            bitSetConversionEventData[i] = value[counter];
            counter ++;
        }
    }

    private int[] fetchBitArrayFromParentArray(int[] parentArray,int startIndex,int size)
    {
        int[] result = new int[size];
        int counter = 0;

        for(int i = startIndex ; i < ( startIndex + size ) ; i ++)
        {
            result[counter ++] = parentArray[i];
        }

        return result;
    }

    public ConversionUrlData prepareConversionUrlDataFromConversionEventPrimaryId
                                                                       (String conversionEventIdValue) throws Exception
    {
        try
        {
            Base64 base64 = new Base64(0,null,true);

            /*code block to rebuild conversion id in original form*/
            if(replaceCharactersInConversionId)
            {
                conversionEventIdValue = conversionEventIdValue.replaceAll(DOUBLE_UNDERSCORE_REPLACEMENT,DOUBLE_UNDERSCORE);
                conversionEventIdValue = conversionEventIdValue.replaceAll(DOUBLE_DASH_REPLACEMENT,DOUBLE_DASH);
            }

            byte[] byteArray = base64.decode(conversionEventIdValue.getBytes());

            int[] bitSetConversionEventData = fetchBitArrayFromByteArray(byteArray);

            int[] bitArrayForVersion =
                    fetchBitArrayFromParentArray
                        (bitSetConversionEventData, START_INDEX_VERSION_PRIMARY_EVENT_ID, SIZE_VERSION_PRIMARY_EVENT_ID);

            short version = fetchShortFromByteArray
                    (fetchByteArrayFromBitArray
                            (
                                    bitArrayForVersion
                            )
                    );

            if(version == VERSION_1_VALUE_PRIMARY_EVENT_ID)
            {
                return prepareConversionUrlDataFromVersion1ConversionEventPrimaryId(bitSetConversionEventData);
            }
            else
            {
                throw new Exception("Version is not defined or unknown !!! ");
            }
        }
        catch (Exception e)
        {
            throw new Exception("Exception inside ConversionEventIdUtils in decoding event id ",e);
        }
    }

    private ConversionUrlData prepareConversionUrlDataFromVersion1ConversionEventPrimaryId
                                                                       (int[] bitSetConversionEventData) throws Exception
    {

        ConversionUrlData conversionUrlData = null;

        try
        {
            conversionUrlData = new ConversionUrlData();

            conversionUrlData.setInternalBid
                    (
                         (double)
                                 ( Float.intBitsToFloat
                                    (
                                            fetchIntegerFromByteArray
                                                    (
                                                            fetchByteArrayFromBitArray(
                                                                    fetchBitArrayFromParentArray
                                                                            (bitSetConversionEventData, START_INDEX_INTERNAL_BID, SIZE_INTERNAL_BID)
                                                            )
                                                    )
                                    )
                                 )
                    );

            conversionUrlData.setAdvertiserBid(
                    (double)Float.intBitsToFloat
                            (
                                    fetchIntegerFromByteArray
                                            (
                                                    fetchByteArrayFromBitArray(
                                                            fetchBitArrayFromParentArray
                                                                    (bitSetConversionEventData,START_INDEX_ADVERTISER_BID,SIZE_ADVERTISER_BID)
                                                    )
                                            )
                            ));

            conversionUrlData.setAdId
            (
                    fetchAbsoluteValueForInteger
                            (fetchIntegerFromByteArray
                                    (
                                    fetchByteArrayFromBitArray(
                                            fetchBitArrayFromParentArray
                                                    (bitSetConversionEventData,START_INDEX_AD_ID,SIZE_AD_ID)
                                        )
                                    ),SIZE_AD_ID
                            )
            );


            conversionUrlData.setSiteId
            (
                fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                            (
                                    fetchByteArrayFromBitArray(
                                            fetchBitArrayFromParentArray
                                                    (bitSetConversionEventData, START_INDEX_SITE_ID, SIZE_SITE_ID)
                                    )
                            ),SIZE_SITE_ID)
            );

            conversionUrlData.setInventorySource
            (
                    fetchAbsoluteValueForInteger(
                     fetchIntegerFromByteArray
                     (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_INVENTORY_SOURCE, SIZE_INVENTORY_SOURCE)
                            )
                     ),SIZE_INVENTORY_SOURCE)
            );


            conversionUrlData.setCarrierId
            (
                fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_CARRIER_ID, SIZE_CARRIER_ID)
                            )
                    ),SIZE_CARRIER_ID)
            );


            conversionUrlData.setCountryId
                   (
                    fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_COUNTRY_ID, SIZE_COUNTRY_ID)
                            )
                    ),SIZE_COUNTRY_ID)
                   );


            conversionUrlData.setDeviceModelId
                   (
                    fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_DEVICE_MODEL_ID, SIZE_DEVICE_MODEL_ID)
                            )
                    ),SIZE_DEVICE_MODEL_ID)
                   );


            conversionUrlData.setDeviceManufacturerId
                   (fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_MANUFACTURER_ID, SIZE_MANUFACTURER_ID)
                            )
                    ),SIZE_MANUFACTURER_ID)
                   );

            conversionUrlData.setDeviceOsId
                   (
                    fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_DEVICE_OS_ID, SIZE_DEVICE_OS_ID)
                            )
                    ),SIZE_DEVICE_OS_ID)
                   );


            conversionUrlData.setDeviceBrowserId
                   (fetchAbsoluteValueForInteger(
                    fetchIntegerFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_DEVICE_BROWSER_ID, SIZE_DEVICE_BROWSER_ID)
                            )
                    ),SIZE_DEVICE_BROWSER_ID)
                   );


            conversionUrlData.setExternalSupplyAttributesInternalId
                    (
                     fetchAbsoluteValueForInteger(
                      fetchIntegerFromByteArray
                      (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_EXT_SUPPLY_ATTR_ID, SIZE_EXT_SUPPLY_ATTR_ID)
                            )
                      ),SIZE_EXT_SUPPLY_ATTR_ID)
                    );


            conversionUrlData.setSelectedSiteCategoryId
                   (
                    fetchAbsoluteValueForShort(
                     fetchShortFromByteArray
                     (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_SELECTED_SITE_CAT_ID, SIZE_SELECTED_SITE_CAT_ID)
                            )
                     ),SIZE_SELECTED_SITE_CAT_ID)
                   );


            conversionUrlData.setBidderModelId
                    (fetchAbsoluteValueForShort(
                     fetchShortFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_BIDDER_MODEL_ID, SIZE_BIDDER_MODEL_ID)
                            )
                    ),SIZE_BIDDER_MODEL_ID));


            conversionUrlData.setSupplySourceType
                    (fetchAbsoluteValueForShort(
                     fetchShortFromByteArray
                     (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_SUPPLY_SOURCE_TYPE, SIZE_SUPPLY_SOURCE_TYPE)
                            )
                     ),SIZE_SUPPLY_SOURCE_TYPE)
                    );


            conversionUrlData.setConnectionTypeId
                    (fetchAbsoluteValueForShort(
                     fetchShortFromByteArray
                     (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData, START_INDEX_CONNECTION_TYPE_ID, SIZE_CONNECTION_TYPE_ID)
                            )
                     ),SIZE_CONNECTION_TYPE_ID)
                    );

            conversionUrlData.setDeviceTypeId
                    (fetchAbsoluteValueForShort(
                            fetchShortFromByteArray
                                    (
                                            fetchByteArrayFromBitArray(
                                                    fetchBitArrayFromParentArray
                                                            (bitSetConversionEventData, START_INDEX_DEVICE_TYPE_ID, SIZE_DEVICE_TYPE_ID)
                                            )
                                    ),SIZE_DEVICE_TYPE_ID)
                    );

            long lsb =
                    fetchLongFromByteArray
                            (
                                    fetchByteArrayFromBitArray(
                                            fetchBitArrayFromParentArray
                                                    (bitSetConversionEventData, START_INDEX_CLICK_REQUEST_ID, SIZE_CLICK_REQUEST_ID / 2)
                                    )
                            );

            long msb = fetchLongFromByteArray
                    (
                            fetchByteArrayFromBitArray(
                                    fetchBitArrayFromParentArray
                                            (bitSetConversionEventData,(START_INDEX_CLICK_REQUEST_ID + (SIZE_CLICK_REQUEST_ID / 2)),SIZE_CLICK_REQUEST_ID/2)
                            )
                    );

            conversionUrlData.setClickRequestId(new UUID(msb, lsb).toString());
        }
        catch (Exception e)
        {
            throw new Exception("Exception inside ConversionEventIdUtils in decoding event id ",e);
        }

        return conversionUrlData;
    }


    private static int[] fetchBitArrayForInteger(int value,short maxSizeOfBitArray)
    {
        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            int positionValue = value & (1 << i);

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    private static int[] fetchBitArrayForLong(long value,short maxSizeOfBitArray)
    {
        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            long positionValue = value & (1L << i);

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    private static int[] fetchBitArrayForShort(short value,short maxSizeOfBitArray)
    {
        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            short positionValue = (short)(value & (1 << i));

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    private static int[] fetchBitArrayForFloat(float value,short maxSizeOfBitArray)
    {
        int valueToUse = Float.floatToIntBits(value);

        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            int positionValue = valueToUse & (1 << i);

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    private static int  fetchIntegerFromByteArray(byte[] b)
    {
        int value = 0;
        for(byte byteValue : b)
            value = (value << 8) + (byteValue & 0xFF);

        return value;
    }

    private static int fetchIntegerFromByte(byte b)
    {
        int value = 0;
        value = (value << 8) + (b & 0xFF);
        return value;
    }

    private static short fetchShortFromByteArray(byte[] b)
    {
        short value = 0;
        for(byte byteValue : b)
            value = (short)((value << 8) + (byteValue & 0xFF));

        return value;
    }

    private static long fetchLongFromByteArray(byte[] b)
    {
        long value = 0;
        for(byte byteValue : b)
            value = (value << 8) + (byteValue & 0xFF);

        return value;
    }

    private static byte[] fetchByteArrayFromBitArrayWithoutShifting(int bitArray[]) {
        byte[] value = new byte[(bitArray.length + 7) / 8];
        int byteTracker = 0;

        byte byteValue = 0;

        int counter = 0;
        for(int bitValue : bitArray) {
            byteValue = (byte) ((byteValue << 1) + (byte) bitValue);

            ++counter;
            if(counter % 8 == 0) {
                value[byteTracker++] = byteValue;
                byteValue = 0;
            }
        }
        for(int i = 0; i < (8 - (counter % 8)) % 8; ++i)
            byteValue <<= 1;
        if(counter % 8 != 0)
            value[byteTracker] = byteValue;

        return value;
    }

    private static byte[] fetchByteArrayFromBitArray(int bitArray[])
    {
        byte[] value = new byte[(bitArray.length + 7) / 8];
        int byteTracker = 0;

        byte byteValue = 0;

        int counter = (8 - (bitArray.length % 8)) % 8;
        for(int bitValue : bitArray) {
            byteValue = (byte) ((byteValue << 1) + (byte) bitValue);

            if((++counter) % 8 == 0) {
                value[byteTracker++] = byteValue;
                byteValue = 0;
            }
        }

        return value;
    }

    private static int[] fetchBitArrayFromByteArray(byte[] byteArray)
    {
        int[] result = new int[byteArray.length * 8];
        int counter = 0;

        for(int i = 0 ; i < byteArray.length ; i ++)
        {
            int[] bitArrayForByte = new int[8];
            byte b = byteArray[i];
            for(int j = 7; j >= 0; --j) {
                bitArrayForByte[j] = b & 0x01;
                b >>= 1;
            }

            for(int j = 0 ; j < bitArrayForByte.length ; j++)
            {
                result[counter ++] = bitArrayForByte[j];
            }
        }

        return result;
    }

    private int fetchAbsoluteValueForInteger(int value,short size)
    {
        if(value == (1 << size) - 1 )
        {
            return -1;
        }

        return value;
    }

    private short fetchAbsoluteValueForShort(short value,short size)
    {
        if(value == (short)((1 << size) - 1 ))
        {
            return -1;
        }

        return value;
    }

    private long fetchAbsoluteValueForLong(long value,short size)
    {
        if(value == ((1L << size) - 1 ))
        {
            return -1;
        }

        return value;
    }

    private boolean isIntegerVariableFitting(int value,short size)
    {
        if(value == -1)
            return true;

        if(size >= 32)
            return true;

        if((value >> size) != 0)
            return false;

        return true;
    }

    private boolean isShortVariableFitting(short value,short size)
    {
        if(value == -1)
            return true;

        if(size >= 16)
            return true;

        if((value >> size) != 0)
            return false;

        return true;
    }

    private boolean isLongVariableFitting(long value,short size)
    {
        if(value == -1)
            return true;

        if(size >= 64)
            return true;

        if((value >> size) != 0)
            return false;

        return true;
    }

    public static void main(String s[]) throws Exception
    {
        double internalBid = 0.001f;                                       // 32
        double advertiserBid = 0.002f;                                     // 32
        int adId = 2;                                                  // 20
        int siteId = 1;                                                // 20
        int inventorySource = 5;                                          // 3
        int carrierId = 1;                                             // 24
        int countryId = 2;                                               // 14
        int deviceModelId = 1;                                           // 18
        int deviceManufacturerId = 2;                                    // 16
        int deviceOsId = 50;                                              // 10
        int deviceBrowserId = 1;                                         // 12
        int externalSupplyAttributesInternalId = 4;                 // 32
        short selectedSiteCategoryId = 1;                                 // 14
        short bidderModelId = 4;                                          // 8
        short supplySourceType = -1;                                       // 5
        short connectionTypeId = -1;                                       // 6
        short deviceTypeId = 2;                                            //6
        String clickRequestId = "011c3e84-ecd5-d501-4fe4-8672e3000003";   // 128

        /*ConversionUrlData(internalBid=0.0010000000474974513, advertiserBid=0.0020000000949949026,
                adId=2,
                siteId=1,
                inventorySource=5,
                clickRequestId=011c3e84-ecd5-d501-4fe4-8672e3000003,
                carrierId=262143,
                countryId=2,
                deviceModelId=17447,
                deviceManufacturerId=21,
                deviceOsId=1,
                deviceBrowserId=1,
                selectedSiteCategoryId=16383,
                bidderModelId=255,
                supplySourceType=2,
                externalSupplyAttributesInternalId=-1,
                connectionTypeId=1)
          */

        ConversionEventIdUtils conversionEventIdUtils = new ConversionEventIdUtils((short)32,(short)32,(short)20,(short)20,(short)3,(short)24,(short)14,(short)18,
                (short)16,(short)10,(short)12,(short)32,(short)14,(short)8,(short)5,(short)3,(short)5,(short)128);

        //conversionEventIdUtils.setReplaceCharactersInConversionId(false);
        ConversionUrlData conversionUrlData = new ConversionUrlData();
        conversionUrlData.setInternalBid(internalBid);
        conversionUrlData.setAdvertiserBid(advertiserBid);
        conversionUrlData.setAdId(adId);
        conversionUrlData.setSiteId(siteId);
        conversionUrlData.setInventorySource(inventorySource);
        conversionUrlData.setCarrierId(carrierId);
        conversionUrlData.setCountryId(countryId);
        conversionUrlData.setDeviceModelId(deviceModelId);
        conversionUrlData.setDeviceManufacturerId(deviceManufacturerId);
        conversionUrlData.setDeviceOsId(deviceOsId);
        conversionUrlData.setDeviceBrowserId(deviceBrowserId);
        conversionUrlData.setExternalSupplyAttributesInternalId(externalSupplyAttributesInternalId);
        conversionUrlData.setSelectedSiteCategoryId(selectedSiteCategoryId);
        conversionUrlData.setBidderModelId(bidderModelId);
        conversionUrlData.setSupplySourceType(supplySourceType);
        conversionUrlData.setConnectionTypeId(connectionTypeId);
        conversionUrlData.setDeviceTypeId(deviceTypeId);
        conversionUrlData.setClickRequestId(clickRequestId);

        String res = conversionEventIdUtils.setConversionDataAndPrepareConversionPrimaryEventIdVersion1(conversionUrlData);
        System.out.println(res);

        ConversionUrlData c1 = conversionEventIdUtils.prepareConversionUrlDataFromConversionEventPrimaryId(res);

        System.out.println(c1);

        System.out.println(URLEncoder.encode(res,"UTF-8"));
        System.out.println(URLEncoder.encode(URLEncoder.encode(res,"UTF-8"),"UTF-8"));
    }
}