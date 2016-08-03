package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.*;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestParentNodeDTO extends BidRequestParentNodeDTO
{
    @JsonProperty("app")
    @Getter @Setter
    private BidRequestAppDTO youkuBidRequestAppDTO;

    @JsonProperty("site")
    @Getter @Setter
    private YoukuBidRequestSiteDTO youkuBidRequestSiteDTO;

    @JsonProperty("device")
    @Getter @Setter
    private YoukuBidRequestDeviceDTO youkuBidRequestDeviceDTO;

    @JsonProperty("imp")
    @Getter @Setter
    private YoukuBidRequestImpressionDTO[] youkuBidRequestImpressionDTOs;

    @JsonProperty("user")
    @Getter @Setter
    private YoukuBidRequestUserDTO youkuBidRequestUserDTO;

    public BidRequestAppDTO getBidRequestApp()
    {
        return youkuBidRequestAppDTO;
    }

    public BidRequestSiteDTO getBidRequestSite()
    {
        return youkuBidRequestSiteDTO;
    }

    public BidRequestDeviceDTO getBidRequestDevice()
    {
        return youkuBidRequestDeviceDTO;
    }

    public BidRequestImpressionDTO[] getBidRequestImpressionArray()
    {
        return youkuBidRequestImpressionDTOs;
    }

    public BidRequestUserDTO getBidRequestUser()
    {
        return youkuBidRequestUserDTO;
    }
}
