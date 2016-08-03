package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestContentDTO;

import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestContentDTO extends BidRequestContentDTO
{
    @JsonProperty("ext")
    @Setter
    private YoukuBidRequestContentExtDTO extensionObject;

    @JsonIgnore
    public YoukuBidRequestContentExtDTO getExtensionObject(){
        return extensionObject;
    }

}
