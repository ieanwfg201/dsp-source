package com.kritter.constants;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum signifying the different user id types in the system. Could be device id, exchange consumer id, cookie id,
 * etc.
 */
public enum ExternalUserIdType {
    SHA1_DEVICE_ID("didsha1", false), /*Hardware device ID (e.g., IMEI); hashed via SHA1.*/
    MD5_DEVICE_ID("didmd5", false),   /*Hardware device ID (e.g., IMEI); hashed via MD5.*/
    SHA1_DEVICE_PLATFORM_ID("dpidsha1", false), /*Platform device ID (e.g., Android ID); hashed via SHA1*/
    MD5_DEVICE_PLATFORM_ID("dpidmd5", false),   /*Platform device ID (e.g., Android ID); hashed via MD5.*/
    IFA_USER_ID("ifa", false), /*ID sanctioned for advertiser use in the clear (i.e., not hashed).*/
    MAC_SHA1_DEVICE_ID("macsha1", false), /*MAC address of the device; hashed via SHA1.*/
    MAC_MD5_DEVICE_ID("macmd5", false), /*MAC address of the device; hashed via MD5.*/
    COOKIE_ID("cid", false), /*Kritters Cookie ID*/
    EXCHANGE_CONSUMER_ID("id", true), /*Exchange-specific ID for the user. At least one of id or buyerid is recommended.*/
    BUYER_USER_ID("buyeruid", true), /*Buyer-specific ID for the user as mapped by the exchange for the buyer. At least one of buyerid or id is recommended*/
    AGGREGATOR_USER_ID("auid", true), /*If aggregator gives user*/
    DEVICE_PLATFORM_ID("dpid", false), /*Platform device ID (e.g., Android ID); Clear Text*/
    DEVICE_ID("did", false), /*Hardware device ID (e.g., IMEI);Clear Text*/
    MAC("mac", false), /*MAC address of the device; Clear Text*/
    UDID("udid", false), /*udid*/
    OPENUDID("oudid", false), /*OpenUdid*/
    AAID("aaid", false), /*aaid*/
    AAIDMD5("aaidmd5", false), /*aaid*/
    OpenUDIDMD5("oudidmd5", false), /*aaid*/
    DSPBUYERUID("dspuid", true) /*In case when we're acting as an exchange, gives the userid for the DSP's who're the
    buyers of impression*/;
	IFA_SHA1_USER_ID("ifasha1", false),
	IFA_MD5_USER_ID("ifamd5", false);

    @Getter
    private String typeName;
    /**
     * Specifies if while serializing the user id, we need to store the source as well.
     */
    @Getter
    private boolean sourceRequired;

    private ExternalUserIdType(String typeName, boolean sourceRequired) {
        this.typeName = typeName;
        this.sourceRequired = sourceRequired;
    }

    private static Map<String, ExternalUserIdType> nameTypeMap = new HashMap<String, ExternalUserIdType>();
    static {
        for(ExternalUserIdType userIdType : ExternalUserIdType.values()) {
            nameTypeMap.put(userIdType.typeName, userIdType);
        }
    }

    public static ExternalUserIdType getEnum(String name) {
        return nameTypeMap.get(name);
    }
}
