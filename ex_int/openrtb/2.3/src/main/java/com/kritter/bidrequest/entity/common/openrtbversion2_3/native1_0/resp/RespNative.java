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
public class RespNative {
    
    @JsonProperty("ver")
    @Setter@Getter    
    private Integer ver=1;
    
    
    @JsonProperty("assets")
    @Setter@Getter    
    private RespAsset[] assets;
    
    @JsonProperty("link")
    @Setter@Getter    
    private Object link=1;
    
    @JsonProperty("imptrackers")
    @Setter    
    private String[] imptrackers;
    @JsonIgnore
    private String[] getImptrackers(){
        return imptrackers;
    }
    
    @JsonProperty("jstracker")
    @Setter    
    private String jstracker;
    @JsonIgnore
    private String getJstracker(){
        return jstracker;
    }
    
    /*Placeholder for exchange-specific extensions to OpenRTB.*/
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
    @JsonIgnore
    public String[] getImpTracker(){
        return imptrackers;
    }
}
