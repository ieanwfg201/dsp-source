package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ConvertErrorEnum {
    HEALTHY_CONVERT(0,"HEALTHY_CONVERT"),
    ADSERVING_REQ_NULL(1,"ADSERVING_REQ_NULL"),
    BIDREQ_NULL(2,"BIDREQ_NULL"),
    REQID_NULL(3,"REQID_NULL"),
    REQID_FR_IMP_NULL(4,"REQID_FR_IMP_NULL"),
    BID_IMP_NULL(5,"BID_IMP_NULL"),
    REQ_WIDTH_NF(6,"REQ_WIDTH_NF"),
    REQ_HEIGHT_NF(7,"REQ_HEIGHT_NF"),
    REQ_SITE_NF(8,"REQ_SITE_NF"),
    REQ_SITE_GUID_NF(9,"REQ_SITE_GUID_NF"),
    REQ_PUB_GUID_NF(10,"REQ_PUB_GUID_NF"),
    REQ_APP_NF(11,"REQ_APP_NF"),
    REQ_APP_GUID_NF(12,"REQ_APP_GUID_NF"),
    REQ_UA_NF(13,"REQ_UA_NF"),
    REQ_IP_NF(14,"REQ_IP_NF"),
    REQ_NOT_SITE(15,"REQ_NOT_SITE"),
    REQ_NOT_APP(16,"REQ_NOT_APP"),
    ACCOUNT_ENTITY_NULL(17,"ACCOUNT_ENTITY_NULL"),
    RES_INPUT_NULL(18,"RES_INPUT_NULL"),
    RES_INPUT_EMPTY(19,"RES_INPUT_EMPTY"),
    RES_CONVERT_EXCEPTION(20,"RES_CONVERT_EXCEPTION"),
    REQ_NATIVE_PROP_NF(21,"REQ_NATIVE_PROP_NF");
    private int code;
    private String name;
    private static Map<Integer, ConvertErrorEnum> map = new HashMap<Integer, ConvertErrorEnum>();
    static {
        for (ConvertErrorEnum val : ConvertErrorEnum.values()) {
            map.put(val.code, val);
        }
    }

    
    private ConvertErrorEnum(int code,String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getCode()
    {
        return this.code;
    }
    
    public static ConvertErrorEnum getEnum(int i)
    {
        return map.get(i);
    }

}
