package com.kritter.constants;

import lombok.Getter;

/**
 * Enum signifying the different user id types in the system. Could be device id, exchange consumer id, cookie id,
 * etc.
 */
public enum ExternalUserIdType {
    SHA1_DEVICE_ID("sdid", false),
    MD5_DEVICE_ID("mdid", false),
    SHA1_DEVICE_PLATFORM_ID("sdpid", false),
    MD5_DEVICE_PLATFORM_ID("mdpid", false),
    COOKIE_ID("cid", false),
    EXCHANGE_CONSUMER_ID("euid", true),
    BUYER_USER_ID("buid", true),
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
