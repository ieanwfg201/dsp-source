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
public class RespAsset {
    @JsonProperty("id")
    @Setter@Getter    
    private Integer id;
    
    @JsonProperty("required")
    @Setter@Getter    
    private Integer required=0;
    
    @JsonProperty("title")
    @Setter
    private RespTitle title;
    @JsonIgnore
    public RespTitle getTitle(){
        return title;
    }
    
    @JsonProperty("img")
    @Setter @Getter
    private RespImage img;
    @JsonIgnore
    public RespImage getImg(){
        return img;
    }
    
    @JsonProperty("video")
    @Setter    
    private RespVideo video;
    @JsonIgnore
    public RespVideo getVideo(){
        return video;
    }

    @JsonProperty("data")
    @Setter @Getter
    private RespData data;
    @JsonIgnore
    public RespData getData(){
        return data;
    }
    
    @JsonProperty("link")
    @Setter @Getter
    private RespLink link;
    @JsonIgnore
    public RespLink getLink(){
        return link;
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
