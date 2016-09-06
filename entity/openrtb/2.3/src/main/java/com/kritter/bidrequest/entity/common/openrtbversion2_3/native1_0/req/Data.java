package com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Refer spec definition - http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    @JsonProperty("type")
    @Setter@Getter    
    private Integer type;
    
    @JsonProperty("len")
    @Setter    
    private Integer len;
    @JsonIgnore
    public Integer getLen(){
        return len;
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
