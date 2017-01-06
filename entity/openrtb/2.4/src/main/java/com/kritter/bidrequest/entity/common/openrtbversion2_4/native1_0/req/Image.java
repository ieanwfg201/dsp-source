package com.kritter.bidrequest.entity.common.openrtbversion2_4.native1_0.req;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Setter;

/**
 * Refer spec definition - http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {
    @JsonProperty("type")
    @Setter    
    private Integer type;
    @JsonIgnore
    public Integer getType(){
        return type;
    }
    
    @JsonProperty("w")
    @Setter    
    private Integer w;
    @JsonIgnore
    public Integer getW(){
        return w;
    }
    
    @JsonProperty("wmin")
    @Setter    
    private Integer wmin;
    @JsonIgnore
    public Integer getWmin(){
        return wmin;
    }
    
    @JsonProperty("h")
    @Setter    
    private Integer h;
    @JsonIgnore
    public Integer getH(){
        return h;
    }
    
    @JsonProperty("hmin")
    @Setter    
    private Integer hmin;
    @JsonIgnore
    public Integer getHmin(){
        return hmin;
    }
    
    @JsonProperty("mimes")
    @Setter    
    private String[] mimes;
    @JsonIgnore
    public String[] getMimes(){
        return mimes;
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
