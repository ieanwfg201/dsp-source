package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeDataAssetType {

    sponsored(1,"Sponsored By message where response should contain the brand name of the sponsor","text"),
    desc(2,"Descriptive text associated with the product or service being advertised.","text"),
    rating(3,"Rating of the product being offered to the user. For example an app’s rating in an app store from 0-5.","number formatted as string"),
    likes(4,"Number of social ratings or “likes” of the product being offered to the user.","number formatted as string"),
    downloads(5,"Number downloads/installs of this product","number formatted as string"),
    price(6,"Price for product / app / in-app purchase. Value should include currency symbol in localised format.","number formatted as string"),
    saleprice(7,"Sale price that can be used together with price to indicate a discounted price compared to a regular price. Value should include currency symbol in localised format.","number formatted as string"),
    phone(8,"Phone number","formatted string"),
    address(9,"Address","text"),
    desc2(10,"Additional descriptive text associated with the product or service being advertised","text"),
    displayurl(11,"Display URL for the text ad","text"),
    ctatext(12,"CTA description - descriptive text describing a ‘call to action’ button for the destination URL.","text");
    
    private int code;
    private String name;
    private String type;
    private static Map<Integer, NativeDataAssetType> map = new HashMap<Integer, NativeDataAssetType>();
    static {
        for (NativeDataAssetType val : NativeDataAssetType.values()) {
            map.put(val.code, val);
        }
    }
    private NativeDataAssetType(int code,String name, String type){
        this.code = code;
        this.name = name;
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeDataAssetType getEnum(int i){
        return map.get(i);
    }
}
