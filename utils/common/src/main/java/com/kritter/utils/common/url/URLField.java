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
 * it in bytes, for convenience the codes for such fields are kept as negative.
 *
 * The new field must be added in the end/in-sequence to maintain the order
 * under each category of field.
 */
public enum URLField
{
    /***************************************************************************/
     /*
      * First category of fields which could be Integer, Short, Long, Float.
      */
    /***************************************************************************/

    BID_FLOOR((short)0,new URLFieldProperties(32,URLFieldType.FLOAT)),

    /***************************************************************************/
     /*
      * Second category of fields which are all string.
      * Important: The code values must be negative.
      */
    /***************************************************************************/
    KRITTER_USER_ID((short)1,new URLFieldProperties(400,URLFieldType.STRING)),
    EXTERNAL_SITE_ID((short)2,new URLFieldProperties(400,URLFieldType.STRING)),
    EXCHANGE_USER_ID((short)3,new URLFieldProperties(400,URLFieldType.STRING)),
    ID_FOR_ADVERTISER((short)4,new URLFieldProperties(400,URLFieldType.STRING)),
    DEVICE_PLATFORM_ID_SHA1((short)5,new URLFieldProperties(400,URLFieldType.STRING)),
    DEVICE_PLATFORM_ID_MD5((short)6,new URLFieldProperties(400,URLFieldType.STRING)),
    MAC_ADDRESS_SHA1((short)7,new URLFieldProperties(400,URLFieldType.STRING)),
    MAC_ADDRESS_MD5((short)8,new URLFieldProperties(400,URLFieldType.STRING));

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
