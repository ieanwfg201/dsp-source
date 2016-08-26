package com.kritter.utils.common.url;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum keeps all the fields that might be used to be passed as part of URL.
 *
 * IMPORTANT:
 * There are two categories of fields one is any number which could be Integer,
 * Short,Long,Float. These types of fields will have fixed length or say number
 * of bits for usage, when these values are used their whole length bits are
 * used for storage.
 * Second category is String fields, where length is unknown beforehand, so we
 * add these fields to final bit set after the fixed length fields.
 * Each String field contains a byte in the start that determines the length of
 * it in bytes.
 *
 * The new field must be added in the end/in-sequence to maintain the order
 * under each category of field.
 */
public enum URLField
{
    /*First category variables, these are all numeric ones.*/
    BID_FLOOR((short)0,new URLFieldProperties(32,URLFieldType.FLOAT)),
    STATE_ID((short)1, new URLFieldProperties(32,URLFieldType.INTEGER)),
    CITY_ID((short)2, new URLFieldProperties(32,URLFieldType.INTEGER)),
    AD_POSITION((short)3, new URLFieldProperties(32,URLFieldType.INTEGER)),
    CHANNEL_ID((short)4, new URLFieldProperties(32,URLFieldType.INTEGER)),
    MARKETPLACE((short)5, new URLFieldProperties(16,URLFieldType.SHORT)),

    /*Second category of fields, these will only be string parameters, the string variables must be
    * added here below and numeric ones in first category. Start code has been kept from 50 so as to
    * enable space for first category.*/
    KRITTER_USER_ID((short)50,new URLFieldProperties(400,URLFieldType.STRING)),
    EXTERNAL_SITE_ID((short)51,new URLFieldProperties(400,URLFieldType.STRING)),
    EXCHANGE_USER_ID((short)52,new URLFieldProperties(400,URLFieldType.STRING)),
    ID_FOR_ADVERTISER((short)53,new URLFieldProperties(400,URLFieldType.STRING)),
    DEVICE_PLATFORM_ID_SHA1((short)54,new URLFieldProperties(400,URLFieldType.STRING)),
    DEVICE_PLATFORM_ID_MD5((short)55,new URLFieldProperties(400,URLFieldType.STRING)),
    MAC_ADDRESS_SHA1((short)56,new URLFieldProperties(400,URLFieldType.STRING)),
    MAC_ADDRESS_MD5((short)57,new URLFieldProperties(400,URLFieldType.STRING));

    @Getter
    private short code;
    @Getter
    private URLFieldProperties urlFieldProperties;
    private static Map<Short, URLField> map = new HashMap<Short, URLField>();
    static {
        for (URLField val : URLField.values()) {
            map.put(val.code, val);
        }
    }

    private URLField(short code,URLFieldProperties urlFieldProperties)
    {
        this.code = code;
        this.urlFieldProperties = urlFieldProperties;
    }

    public static URLField getEnum(short i)
    {
        return map.get(i);
    }

}
