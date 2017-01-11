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
public class RespData {
    @JsonProperty("label")
    @Setter    
    private String label;
    @JsonIgnore
    private String getLabel(){
        return label;
    }
    
    @JsonProperty("value")
    @Setter@Getter    
    private String value;
    
    /*Placeholder for exchange-specific extensions to OpenRTB.*/
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
