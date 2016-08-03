package com.kritter.constants;

import lombok.Getter;

/**
 * Enum signifying the different user id types in the system. Could be device id, exchange consumer id, cookie id,
 * etc.
 */
public enum ExternalUserIdType {
    SHA1_DEVICE_ID("didsha1", false),
    MD5_DEVICE_ID("didmd5", false),
    SHA1_DEVICE_PLATFORM_ID("dpidsha1", false),
    MD5_DEVICE_PLATFORM_ID("dpidmd5", false),
    IFA_USER_ID("ifa", false),
    MAC_SHA1_DEVICE_ID("macsha1", false),
    MAC_MD5_DEVICE_ID("macmd5", false),
    COOKIE_ID("cid", false),
    EXCHANGE_CONSUMER_ID("id", true),
    BUYER_USER_ID("buyeruid", true),
    AGGREGATOR_USER_ID("auid", true);

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
}
