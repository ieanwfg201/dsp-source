package com.kritter.bidrequest.entity.common.openrtbversion2_4.native1_0.resp;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Refer spec definition - http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespVideo {
    @JsonProperty("vasttag")
    @Setter@Getter    
    private Integer vasttag;
    
}
