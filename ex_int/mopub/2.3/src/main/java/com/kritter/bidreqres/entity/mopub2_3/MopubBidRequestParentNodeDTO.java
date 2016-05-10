package com.kritter.bidreqres.entity.mopub2_3;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MopubBidRequestParentNodeDTO extends BidRequestParentNodeDTO
{
    @JsonProperty("app")
    @Getter @Setter
    private MopubBidRequestAppDTO mopubBidRequestAppDTO;

    @JsonProperty("site")
    @Getter @Setter
    private MopubBidRequestSiteDTO mopubBidRequestSiteDTO;

    @JsonProperty("device")
    @Getter @Setter
    private MopubBidRequestDeviceDTO mopubBidRequestDeviceDTO;

    @JsonProperty("imp")
    @Getter @Setter
    private MopubBidRequestImpressionDTO[] mopubBidRequestImpressionDTOs;

    @JsonProperty("user")
    @Getter @Setter
    private MopubBidRequestUserDTO mopubBidRequestUserDTO;

    public BidRequestAppDTO getBidRequestApp()
    {
        return mopubBidRequestAppDTO;
    }

    public BidRequestSiteDTO getBidRequestSite()
    {
        return mopubBidRequestSiteDTO;
    }

    public BidRequestDeviceDTO getBidRequestDevice()
    {
        return mopubBidRequestDeviceDTO;
    }

    public BidRequestImpressionDTO[] getBidRequestImpressionArray()
    {
        return mopubBidRequestImpressionDTOs;
    }

    public BidRequestUserDTO getBidRequestUser()
    {
        return mopubBidRequestUserDTO;
    }
}