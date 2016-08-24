package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ADTagMacros {

    AD_ID(1,"{{AD_ID}}","\\{\\{AD_ID\\}\\}"),
    AD_WIDTH(2,"{{AD_WIDTH}}","\\{\\{AD_WIDTH\\}\\}"),
    AD_HEIGHT(3,"{{AD_HEIGHT}}","\\{\\{AD_HEIGHT\\}\\}"),
    APP_BUNDLE(4,"{{APP_BUNDLE}}","\\{\\{APP_BUNDLE\\}\\}"),
    APP_NAME(5,"{{APP_NAME}}","\\{\\{APP_NAME\\}\\}"),
    APP_CATEGORY(6,"{{APP_CATEGORY}}","\\{\\{APP_CATEGORY\\}\\}"),
    APP_STOREURL(7,"{{APP_STOREURL}}","\\{\\{APP_STOREURL\\}\\}"),
    APP_VERSION(8,"{{APP_VERSION}}","\\{\\{APP_VERSION\\}\\}"),
    CLICK_ID(9,"{{CLICK_ID}}","\\{\\{CLICK_ID\\}\\}"),
    CAMPAIGN_ID(10,"{{CAMPAIGN_ID}}","\\{\\{CAMPAIGN_ID\\}\\}"),
    DEVICE_OS(11,"{{DEVICE_OS}}","\\{\\{DEVICE_OS\\}\\}"),
    DEVICE_UA(12,"{{DEVICE_UA}}","\\{\\{DEVICE_UA\\}\\}"),
    DEVICE_IP(13,"{{DEVICE_IP}}","\\{\\{DEVICE_IP\\}\\}"),
    EXCHANGE(14,"{{EXCHANGE}}","\\{\\{EXCHANGE\\}\\}"),
    PAGE_URL(15,"{{PAGE_URL}}","\\{\\{PAGE_URL\\}\\}"),
    SITE_DOMAIN(16,"{{SITE_DOMAIN}}","\\{\\{SITE_DOMAIN\\}\\}"),
    TIMESTAMP(17,"{{TIMESTAMP}}","\\{\\{TIMESTAMP\\}\\}"),
    USER_COUNTRY(18,"{{USER_COUNTRY}}","\\{\\{USER_COUNTRY\\}\\}"),
    USER_GEO_LAT(19,"{{USER_GEO_LAT}}","\\{\\{USER_GEO_LAT\\}\\}"),
    USER_GEO_LNG(20,"{{USER_GEO_LNG}}","\\{\\{USER_GEO_LNG\\}\\}"),
    DEVICE_ID(21,"{{DEVICE_ID}}","\\{\\{DEVICE_ID\\}\\}"),
    CLICK_URL(22,"[CLICK_URL]","\\[CLICK_URL]"),
    SECURE_CLICK_URL(23,"[SECURE_CLICK_URL]","\\[SECURE_CLICK_URL]"),
    RANDOM(24,"RANDOM","RANDOM"),
    CACHEBUSTER(25,"{{CACHEBUSTER}}","\\{\\{CACHEBUSTER\\}\\}"),
    PUB_GUID(26,"{{PUB_GUID}}","\\{\\{PUB_GUID\\}\\}"),
    DO_NOT_TRACK(27,"{{DO_NOT_TRACK}}","\\{\\{DO_NOT_TRACK\\}\\}"),
    REFERER(28,"{{REFERER}}","\\{\\{REFERER\\}\\}"),
    IMEI(29,"{{IMEI}}","\\{\\{IMEI\\}\\}"),
    IMEIMD5(30,"{{IMEIMD5}}","\\{\\{IMEIMD5\\}\\}"),
    MAC(31,"{{MAC}}","\\{\\{MAC\\}\\}"),
    IDFA(32,"{{IDFA}}","\\{\\{IDFA\\}\\}"),
    AAID(33,"{{AAID}}","\\{\\{AAID\\}\\}"),
    OpenUDID(34,"{{OpenUDID}}","\\{\\{OpenUDID\\}\\}"),
    AndroidID(35,"{{AndroidID}}","\\{\\{AndroidID\\}\\}"),
    AndroidIDMD5(36,"{{AndroidIDMD5}}","\\{\\{AndroidIDMD5\\}\\}"),
    UDID(37,"{{UDID}}","\\{\\{UDID\\}\\}")
    ;
    
    private int code;
    private String name;
    private String desc;
    private static Map<Integer, ADTagMacros> map = new HashMap<Integer, ADTagMacros>();
    static {
        for (ADTagMacros val : ADTagMacros.values()) {
            map.put(val.code, val);
        }
    }
    private ADTagMacros(int code,String name, String desc){
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getName(){
        return this.name;
    }
    
    public String getDesc(){
        return this.desc;
    }

    public int getCode(){
        return this.code;
    }
    
    public static ADTagMacros getEnum(int i){
        return map.get(i);
    }
}
