package com.kritter.bidrequest.entity.common.openrtbversion2_4.native1_0.req;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Refer spec definition - http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Native {

    @JsonProperty("ver")
    @Setter
    private String ver="1";
    @JsonIgnore
    public String getVer(){
        return ver;
    }
    
    /**
     * The Layout ID of the native ad unit. See the table of Native Layout IDs below.
     */
    @JsonProperty("layout")
    @Setter    
    private Integer layout;
    @JsonIgnore
    public Integer getLayout(){
        return layout;
    }
    
    /**
     * The Ad unit ID of the native ad unit. See the Table of Native Ad Unit IDs below for a list of 
     * supported core ad units.
     */
    @JsonProperty("adunit")
    @Setter    
    private Integer adunit;
    @JsonIgnore
    public Integer getAdunit(){
        return adunit;
    }
    /**
     * The number of identical placements in this Layout. Refer to Section 8.1 Multi Placement Bid Requests.
     */
    @JsonProperty("plcmtcnt")
    @Setter    
    private Integer plcmtcnt = 1;
    @JsonIgnore
    public Integer getPlcmtcnt(){
        return plcmtcnt;
    }
    
    /**
     * xx (see the IAB Core Six layout types). 0 for the first ad, 1 for the second ad,
     *  and so on. This is not the sequence number of the content in the stream.
     */
    @JsonProperty("seq")
    @Setter    
    private Integer seq = 0;
    @JsonIgnore
    public Integer getSeq(){
        return seq;
    }
    
    @JsonProperty("assets")
    @Setter
    private Asset[] assets;

    @JsonIgnore
    public Asset[] getAssets(){
        return assets;
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