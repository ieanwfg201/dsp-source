package com.kritter.utils.common.url;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class takes input as various fields of type integer,short,float and string,
 * stores them into a bit array and generates a string id using them to optimize
 * space. The fields have predefined lengths and generation of id is done in one shot.
 *
 * Methods involve init , stackFieldForStorage and generate
 * ,once generate is called, no more fields can be added, init refreshes the storage.
 */
public class URLFieldFactory
{
    private int[] bitSetData;
    private boolean generated;
    private Map<Short,Integer> fieldCodeWithPositionMap;
    private String generatedField;
    private int previousPositionForStringFields;
    private int secondCategoryFieldPosition;
    private static final Logger logger = LogManager.getLogger("adserving.application");

    public URLFieldFactory()
    {
        this.generated = false;
        this.fieldCodeWithPositionMap = new HashMap<Short, Integer>();
        this.generatedField = null;
        int position = 0;

        /*Fields are retreived in order here.*/
        for(URLField urlField : URLField.values())
        {
            if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.STRING.getCode())
                continue;

            this.fieldCodeWithPositionMap.put(urlField.getCode(), position);
            position += urlField.getUrlFieldProperties().getLength();
        }

        /*Start with least size array that would accommodate first category fields.*/
        this.bitSetData = new int[position];
        this.secondCategoryFieldPosition = position;
        this.previousPositionForStringFields = position;
    }

    public URLFieldFactory(String generatedField)
    {
        this.generated = false;
        this.fieldCodeWithPositionMap = new HashMap<Short, Integer>();
        this.generatedField = generatedField;
        int position = 0;

        /*Fields are retreived in order here.*/
        for(URLField urlField : URLField.values())
        {
            if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.STRING.getCode())
                continue;

            this.fieldCodeWithPositionMap.put(urlField.getCode(), position);
            position += urlField.getUrlFieldProperties().getLength();
        }

        /*Start with least size array that would accommodate first category fields.*/
        this.bitSetData = new int[position];
        this.secondCategoryFieldPosition = position;
        this.previousPositionForStringFields = position;
    }

    /**
     * This method takes input
     * @param urlField
     * @throws URLFieldProcessingException
     */
    public void stackFieldForStorage(URLField urlField) throws URLFieldProcessingException
    {
        if(this.generated)
            throw new URLFieldProcessingException("The id has been generated, no more fields can be added.");

        if(null == urlField || null == urlField.getUrlFieldProperties() || null == urlField.getUrlFieldProperties().getFieldValue())
        {
            return;
        }

        int[] bitArray = null;
        int maxLength = urlField.getUrlFieldProperties().getLength();
        Object fieldValue = urlField.getUrlFieldProperties().getFieldValue();

        /*This would be null for field type String and non-null for first category fields.*/
        Integer positionToStartBitStorage = this.fieldCodeWithPositionMap.get(urlField.getCode());

        if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.STRING.getCode())
        {
            String value = (String)fieldValue;

            if((value.length() * 8) > urlField.getUrlFieldProperties().getLength())
            {
                logger.error("String value: {} is not fitting inside size: {} , skipping it in setting to URL fields " +
                             "in URLFieldFactory",value,urlField.getUrlFieldProperties().getLength());
                return;
            }

            maxLength = (value.length() + 2) * 8;

            /*Use first byte as the position code of the string.*/
            /*Use second byte as length of the string, should not exceed 255*/
            byte[] bytesForFieldValue = value.getBytes();
            byte[] bytesForFieldValueWithSizeAndCode = new byte[bytesForFieldValue.length + 2];
            bytesForFieldValueWithSizeAndCode[0] = Short.valueOf(urlField.getCode()).byteValue();
            bytesForFieldValueWithSizeAndCode[1] = Integer.valueOf(value.length()).byteValue();

            System.arraycopy(bytesForFieldValue,0,bytesForFieldValueWithSizeAndCode,2,bytesForFieldValue.length);

            bitArray = URLFieldUtils.fetchBitArrayFromByteArray(bytesForFieldValueWithSizeAndCode);
            positionToStartBitStorage = this.previousPositionForStringFields;
            this.previousPositionForStringFields += maxLength;

            /*Also increase the size of the core bit array by this String's size*/
            int[] bitSetDataNew = new int[this.bitSetData.length + maxLength];
            /*Copy contents from original bitSetData to new formed array.*/
            System.arraycopy(this.bitSetData,0,bitSetDataNew,0,this.bitSetData.length);
            this.bitSetData = bitSetDataNew;
        }
        else if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.INTEGER.getCode())
        {
            Integer value = (Integer) fieldValue;
            bitArray = URLFieldUtils.fetchBitArrayForInteger(value,(short)urlField.getUrlFieldProperties().getLength());
        }
        else if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.SHORT.getCode())
        {
            Short value = (Short)fieldValue;
            bitArray = URLFieldUtils.fetchBitArrayForShort(value,(short)urlField.getUrlFieldProperties().getLength());
        }
        else if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.FLOAT.getCode())
        {
            Float value = (Float)fieldValue;
            bitArray = URLFieldUtils.fetchBitArrayForFloat(value,(short)urlField.getUrlFieldProperties().getLength());
        }
        else if(urlField.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.LONG.getCode())
        {
            Long value = (Long)fieldValue;
            bitArray = URLFieldUtils.fetchBitArrayForLong(value,(short)urlField.getUrlFieldProperties().getLength());
        }

        if(null == bitArray)
            throw new URLFieldProcessingException("BitArray could not be prepared for url field code: " +
                                                   urlField.getCode());

        URLFieldUtils.setBitSetToStorage(positionToStartBitStorage,maxLength, bitArray, bitSetData);
    }

    public String generate()
    {
        Base64 base64 = new Base64(0,null,true);
        byte[] bytesToEncode = URLFieldUtils.fetchByteArrayFromBitArrayWithoutShifting(bitSetData);
        this.generatedField = new String(base64.encode(bytesToEncode));
        this.generated = true;
        return this.generatedField;
    }

    public Map<Short,URLField> decodeFields() throws UnsupportedEncodingException
    {
        Map<Short,URLField> fieldMap = new HashMap<Short, URLField>();

        Base64 base64 = new Base64(0,null,true);
        byte[] byteArray = base64.decode(this.generatedField.getBytes());

        int[] decodedBitSetData = URLFieldUtils.fetchBitArrayFromByteArray(byteArray);

        for(URLField element : URLField.values())
        {
            byte[] storageBytes = null;

            if(element.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.STRING.getCode())
            {

                /*Read the next byte to fetch the code for the string value*/
                if(decodedBitSetData.length >= this.secondCategoryFieldPosition + 8)
                    storageBytes = URLFieldUtils.fetchByteArrayFromBitArray(
                                                                            URLFieldUtils.fetchBitArrayFromParentArray
                                                                                    (decodedBitSetData,
                                                                                            this.secondCategoryFieldPosition,
                                                                                            8)
                                                                    );
                else
                    continue;

                short fieldCode = URLFieldUtils.fetchShortFromByteArray(storageBytes);

                /*Read the next byte to fetch how many bits need to be read from bitSetData*/
                if(decodedBitSetData.length >= this.secondCategoryFieldPosition + 8)
                    storageBytes = URLFieldUtils.fetchByteArrayFromBitArray(
                                                                            URLFieldUtils.fetchBitArrayFromParentArray
                                                                                (decodedBitSetData,
                                                                                        this.secondCategoryFieldPosition + 8,
                                                                                        8)
                                                                           );
                else
                    continue;

                int lengthToRead = URLFieldUtils.fetchIntegerFromByteArray(storageBytes) * 8;

                /*Following means field is not set or is of zero length.*/
                if(lengthToRead <= 0)
                    continue;

                this.secondCategoryFieldPosition += 16;
                if(decodedBitSetData.length >= this.secondCategoryFieldPosition + lengthToRead)
                    storageBytes = URLFieldUtils.fetchByteArrayFromBitArray(
                                                                        URLFieldUtils.fetchBitArrayFromParentArray
                                                                                (decodedBitSetData,
                                                                                        this.secondCategoryFieldPosition,
                                                                                        lengthToRead)
                                                                       );

                if(null == storageBytes)
                    continue;

                this.secondCategoryFieldPosition += storageBytes.length * 8;

                String fieldValue = new String(storageBytes);
                URLField urlField = URLField.getEnum(fieldCode);
                if(null == urlField)
                    continue;

                urlField.getUrlFieldProperties().setFieldValue(fieldValue);
                fieldMap.put(fieldCode,urlField);
            }

            else if(decodedBitSetData.length >= this.fieldCodeWithPositionMap.get(element.getCode()) + element.getUrlFieldProperties().getLength())
                    storageBytes = URLFieldUtils.fetchByteArrayFromBitArray(
                                                                            URLFieldUtils.fetchBitArrayFromParentArray
                                                                                (       decodedBitSetData,
                                                                                        this.fieldCodeWithPositionMap.get(element.getCode()),
                                                                                        element.getUrlFieldProperties().getLength()
                                                                                )
                                                                       );

            if(null == storageBytes)
                continue;

            if(element.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.FLOAT.getCode())
            {
                float fieldValue = Float.intBitsToFloat(URLFieldUtils.fetchIntegerFromByteArray(storageBytes));
                URLField urlField = URLField.getEnum(element.getCode());
                urlField.getUrlFieldProperties().setFieldValue(fieldValue);
                fieldMap.put(element.getCode(),urlField);
            }

            else if(element.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.INTEGER.getCode())
            {
                int fieldValue = URLFieldUtils.fetchIntegerFromByteArray(storageBytes);
                URLField urlField = URLField.getEnum(element.getCode());
                urlField.getUrlFieldProperties().setFieldValue(fieldValue);
                fieldMap.put(element.getCode(),urlField);
            }

            else if(element.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.SHORT.getCode())
            {
                short fieldValue = URLFieldUtils.fetchShortFromByteArray(storageBytes);
                URLField urlField = URLField.getEnum(element.getCode());
                urlField.getUrlFieldProperties().setFieldValue(fieldValue);
                fieldMap.put(element.getCode(),urlField);
            }

            else if(element.getUrlFieldProperties().getUrlFieldType().getCode() == URLFieldType.LONG.getCode())
            {
                long fieldValue = URLFieldUtils.fetchLongFromByteArray(storageBytes);
                URLField urlField = URLField.getEnum(element.getCode());
                urlField.getUrlFieldProperties().setFieldValue(fieldValue);
                fieldMap.put(element.getCode(),urlField);
            }
        }

        return fieldMap;
    }



    public static void main(String s[]) throws Exception
    {
        URLFieldFactory urlFieldFactory = new URLFieldFactory();

        URLField userId = URLField.KRITTER_USER_ID;
        userId.getUrlFieldProperties().setFieldValue("euid:3:45a1ed1cb98373cc9d4e14230487bb76");
        urlFieldFactory.stackFieldForStorage(userId);

        URLField excUserId = URLField.EXCHANGE_USER_ID;
        excUserId.getUrlFieldProperties().setFieldValue("euid:3:45a1ed1cb98373cc9d4e14230487bb79");
        urlFieldFactory.stackFieldForStorage(excUserId);

        URLField extSiteId = URLField.EXTERNAL_SITE_ID;
        extSiteId.getUrlFieldProperties().setFieldValue("010a4137-6a5c-6201-5383-cfde7d0000000000000");
        urlFieldFactory.stackFieldForStorage(extSiteId);

        URLField bid = URLField.BID_FLOOR;
        float bidValue = 0.3123f;
        bid.getUrlFieldProperties().setFieldValue(bidValue);
        urlFieldFactory.stackFieldForStorage(bid);

        URLField state = URLField.STATE_ID;
        state.getUrlFieldProperties().setFieldValue(1234);
        urlFieldFactory.stackFieldForStorage(state);

        URLField city = URLField.CITY_ID;
        city.getUrlFieldProperties().setFieldValue(911);
        urlFieldFactory.stackFieldForStorage(city);

        URLField adp = URLField.AD_POSITION;
        adp.getUrlFieldProperties().setFieldValue(99999999);
        urlFieldFactory.stackFieldForStorage(adp);

        URLField channel = URLField.CHANNEL_ID;
        channel.getUrlFieldProperties().setFieldValue(78123);
        urlFieldFactory.stackFieldForStorage(channel);

        URLField mark = URLField.MARKETPLACE;
        mark.getUrlFieldProperties().setFieldValue((short)3);
        urlFieldFactory.stackFieldForStorage(mark);

        String generatedField = urlFieldFactory.generate();
        System.out.println(generatedField);

        URLFieldFactory urlFieldFactory1 = new URLFieldFactory(generatedField);

        Map<Short,URLField> map = urlFieldFactory1.decodeFields();
        System.out.println(map.size());

        if(null != map.get(URLField.BID_FLOOR.getCode()))
            System.out.println("bidfloor: " + map.get(URLField.BID_FLOOR.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.KRITTER_USER_ID.getCode()))
            System.out.println("kritter user id " + map.get(URLField.KRITTER_USER_ID.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.EXTERNAL_SITE_ID.getCode()))
            System.out.println("siteid " + map.get(URLField.EXTERNAL_SITE_ID.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.EXCHANGE_USER_ID.getCode()))
            System.out.println("exchange id: " + map.get(URLField.EXCHANGE_USER_ID.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.ID_FOR_ADVERTISER.getCode()))
            System.out.println("ifa: " + map.get(URLField.ID_FOR_ADVERTISER.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.DEVICE_PLATFORM_ID_MD5.getCode()))
            System.out.println("dpidmd5: " + map.get(URLField.DEVICE_PLATFORM_ID_MD5.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.DEVICE_PLATFORM_ID_SHA1.getCode()))
            System.out.println("dpidsha1: " + map.get(URLField.DEVICE_PLATFORM_ID_SHA1.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.MAC_ADDRESS_MD5.getCode()))
            System.out.println("macmd5: " + map.get(URLField.MAC_ADDRESS_MD5.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.MAC_ADDRESS_SHA1.getCode()))
            System.out.println("macsha1: " + map.get(URLField.MAC_ADDRESS_SHA1.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.STATE_ID.getCode()))
            System.out.println("state: " + map.get(URLField.STATE_ID.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.CITY_ID.getCode()))
            System.out.println("city: " + map.get(URLField.CITY_ID.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.AD_POSITION.getCode()))
            System.out.println("adposi: " + map.get(URLField.AD_POSITION.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.CHANNEL_ID.getCode()))
            System.out.println("channel: " + map.get(URLField.CHANNEL_ID.getCode()).getUrlFieldProperties().getFieldValue());

        if(null != map.get(URLField.MARKETPLACE.getCode()))
            System.out.println("mark: " + map.get(URLField.MARKETPLACE.getCode()).getUrlFieldProperties().getFieldValue());
    }
}
