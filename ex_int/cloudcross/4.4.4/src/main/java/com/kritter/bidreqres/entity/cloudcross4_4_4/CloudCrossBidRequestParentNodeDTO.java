package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCrossBidRequestParentNodeDTO extends BidRequestParentNodeDTO {
    @JsonProperty("app")
    @Getter
    @Setter
    private CloudCrossBidRequestAppDTO cloudCrossBidRequestAppDTO;

    @JsonProperty("site")
    @Getter
    @Setter
    private CloudCrossBidRequestSiteDTO cloudCrossBidRequestSiteDTO;

    @JsonProperty("device")
    @Getter
    @Setter
    private CloudCrossBidRequestDeviceDTO cloudCrossBidRequestDeviceDTO;

    @JsonProperty("imp")
    @Getter
    @Setter
    private CloudCrossBidRequestImpressionDTO[] cloudCrossBidRequestImpressionDTOs;

    @JsonProperty("wseat")
    @Setter
    private String bidderSeatIds;

    @Override
    public String[] getBidderSeatIds() {
        String[] strings = new String[1];
        strings[0] = bidderSeatIds;
        return strings;
    }

    @JsonProperty("user")
    @Getter
    @Setter
    private CloudCrossBidRequestUserDTO cloudCrossBidRequestUserDTO;

    public BidRequestAppDTO getBidRequestApp() {
        return cloudCrossBidRequestAppDTO;
    }

    public BidRequestSiteDTO getBidRequestSite() {
        return cloudCrossBidRequestSiteDTO;
    }

    public BidRequestDeviceDTO getBidRequestDevice() {
        return cloudCrossBidRequestDeviceDTO;
    }

    public BidRequestImpressionDTO[] getBidRequestImpressionArray() {
        return cloudCrossBidRequestImpressionDTOs;
    }

    public BidRequestUserDTO getBidRequestUser() {
        return cloudCrossBidRequestUserDTO;
    }
}