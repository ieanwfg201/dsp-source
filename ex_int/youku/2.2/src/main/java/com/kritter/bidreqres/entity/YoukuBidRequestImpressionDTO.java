package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionVideoObjectDTO;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestImpressionDTO extends BidRequestImpressionDTO
{
    @JsonProperty("secure")
    @Setter
    private Integer requiresSecureAssets;

    @JsonIgnore
    public Integer getRequiresSecureAssets()
    {
        return requiresSecureAssets;
    }
    
    @JsonProperty("video")
    @Setter
	private YoukuBidRequestVideo bidRequestImpressionVideoObject;
    
    @JsonIgnore
    public BidRequestImpressionVideoObjectDTO getBidRequestImpressionVideoObject(){
        return bidRequestImpressionVideoObject;
    }

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private YoukuBidRequestImpressionExtDTO extensionObject;

    @JsonIgnore
    public YoukuBidRequestImpressionExtDTO getExtensionObject(){
        return extensionObject;
    }

}
