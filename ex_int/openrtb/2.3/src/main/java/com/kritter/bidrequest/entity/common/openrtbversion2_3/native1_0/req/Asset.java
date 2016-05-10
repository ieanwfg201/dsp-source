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
public class Asset {

    /**
     * Unique asset ID, assigned by exchange. Typically a counter for the array.
     */
    @JsonProperty("id")
    @Setter@Getter    
    private Integer id;
    
    /**
     * Set to 1 if asset is required (exchange will not accept a bid without it)
     */
    @JsonProperty("required")
    @Setter    
    private Integer required = 0;
    @JsonIgnore
    public Integer getRequired(){
        return required;
    }
    
    @JsonProperty("title")
    @Setter    
    private Title title;
    @JsonIgnore
    public Title getTitle(){
        return title;
    }
    
    @JsonProperty("img")
    @Setter    
    private Image img;
    @JsonIgnore
    public Image getImg(){
        return img;
    }
    
    @JsonProperty("video")
    @Setter    
    private Video video;
    @JsonIgnore
    public Video getVideo(){
        return video;
    }
    
    @JsonProperty("data")
    @Setter    
    private Data data;
    @JsonIgnore
    public Data getData(){
        return data;
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
