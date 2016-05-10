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
public class Video {
    @JsonProperty("mimes")
    @Setter@Getter    
    private String[] mimes;
    
    @JsonProperty("minduration")
    @Setter@Getter    
    private Integer minduration;
    
    @JsonProperty("maxduration")
    @Setter@Getter    
    private Integer maxduration;
    
    @JsonProperty("protocols")
    @Setter@Getter    
    private Integer[] protocols;
    
    /*Placeholder for exchange-specific extensions to OpenRTB.*/
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
