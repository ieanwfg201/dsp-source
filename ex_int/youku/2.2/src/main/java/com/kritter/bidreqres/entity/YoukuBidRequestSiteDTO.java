package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestSiteDTO;

import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestSiteDTO extends BidRequestSiteDTO
{
    @JsonProperty("content")
    @Setter
    private YoukuBidRequestContentDTO bidRequestContent;

    @JsonIgnore
    public YoukuBidRequestContentDTO getBidRequestContent(){
        return bidRequestContent;
    }

}
