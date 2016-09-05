package com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Refer spec definition - http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespLink {
    @JsonProperty("url")
    @Setter@Getter    
    private String url;
    
    @JsonProperty("clicktrackers")
    @Setter   
    private String[] clicktrackers;
    @JsonIgnore
    private String[] getClicktrackers(){
        return clicktrackers;
    }
    
    @JsonProperty("fallback")
    @Setter   
    private String fallback;
    @JsonIgnore
    private String getFallback(){
        return fallback;
    }
    
    /*Placeholder for exchange-specific extensions to OpenRTB.*/
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
