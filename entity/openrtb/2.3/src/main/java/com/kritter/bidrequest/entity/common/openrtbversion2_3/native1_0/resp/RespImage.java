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
public class RespImage {
    @JsonProperty("url")
    @Setter@Getter    
    private String url;

    
    @JsonProperty("w")
    @Setter    
    private Integer w;
    @JsonIgnore
    public Integer getW(){
        return w;
    }
    
    @JsonProperty("h")
    @Setter    
    private Integer h;
    @JsonIgnore
    public Integer getH(){
        return h;
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
