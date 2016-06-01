package com.kritter.utils.common.url;

import org.apache.commons.codec.binary.Base64;

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
    private int spaceRequiredForBits;
    private int[] bitSetData;
    private boolean generated;
    private Map<Short,Integer> fieldCodeWithPositionMap;

    public void init()
    {
        this.spaceRequiredForBits = 0;
        this.bitSetData = null;
        this.generated = false;
        this.fieldCodeWithPositionMap = new HashMap<Short, Integer>();

        int totalSize = 0;

        int previousFieldLength = 0;
        for(URLFieldLength urlFieldLength : URLFieldLength.values())
        {
            totalSize += urlFieldLength.getLength();
            short code = urlFieldLength.getCode();

            /* position = code * previous_length */
            int position = code * previousFieldLength;
            this.fieldCodeWithPositionMap.put(code, position);
            previousFieldLength = urlFieldLength.getLength();
        }

        this.bitSetData = new int[totalSize];
    }

    /**
     * This method takes input
     * @param field
     * @param urlFieldLength
     * @throws URLFieldProcessingException
     */
    public void stackFieldForStorage(Object field,URLFieldLength urlFieldLength) throws URLFieldProcessingException
    {
        if(generated)
            throw new URLFieldProcessingException("The id has been generated, no more fields can be added.");

        if(null == field || null == urlFieldLength)
            throw new URLFieldProcessingException("Field provided or field's length provided are null");

        /*get position from using code*/
        int position = this.fieldCodeWithPositionMap.get(urlFieldLength.getCode());
        int[] bitArray = null;

        if(field instanceof String)
        {
            String value = (String)field;
            bitArray = URLFieldUtils.fetchBitArrayFromByteArray(value.getBytes());
        }
        else if(field instanceof Integer)
        {
            Integer value = (Integer) field;
            bitArray = URLFieldUtils.fetchBitArrayForInteger(value,(short)urlFieldLength.getLength());
        }
        else if(field instanceof Short)
        {
            Short value = (Short)field;
            bitArray = URLFieldUtils.fetchBitArrayForShort(value,(short)urlFieldLength.getLength());
        }
        else if(field instanceof Float)
        {
            Float value = (Float)field;
            bitArray = URLFieldUtils.fetchBitArrayForFloat(value,(short)urlFieldLength.getLength());
        }
        else if(field instanceof Long)
        {
            Long value = (Long)field;
            bitArray = URLFieldUtils.fetchBitArrayForLong(value,(short)urlFieldLength.getLength());
        }

        if(null == bitArray)
            throw new URLFieldProcessingException("BitArray could not be prepared for url field code: " +
                                                   urlFieldLength.getCode());

        URLFieldUtils.setBitSetToStorage(position,urlFieldLength.getLength(), bitArray, bitSetData);
    }

    public String generate()
    {
        Base64 base64 = new Base64(0,null,true);
        byte[] bytesToEncode = URLFieldUtils.fetchByteArrayFromBitArrayWithoutShifting(bitSetData);
        return new String(base64.encode(bytesToEncode));
    }

    public static void main(String s[]) throws Exception
    {
        URLFieldFactory urlFieldFactory = new URLFieldFactory();
        urlFieldFactory.init();
        urlFieldFactory.stackFieldForStorage("kritter-user-id",URLFieldLength.KRITTER_USER_ID);
    }
}
