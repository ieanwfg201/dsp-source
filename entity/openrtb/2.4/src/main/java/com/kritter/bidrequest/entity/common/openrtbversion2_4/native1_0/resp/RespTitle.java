package com.kritter.bidrequest.entity.common.openrtbversion2_4.native1_0.resp;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Refer spec definition - http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespTitle {
    @JsonProperty("text")
    @Setter@Getter    
    private String text;
    
    /*Placeholder for exchange-specific extensions to OpenRTB.*/
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
