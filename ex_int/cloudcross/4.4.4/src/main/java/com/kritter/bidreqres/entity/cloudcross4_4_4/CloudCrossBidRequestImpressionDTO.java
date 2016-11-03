package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCrossBidRequestImpressionDTO extends BidRequestImpressionDTO {
    @JsonProperty("banner")
    @Setter
    private CloudCrossBidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObject;

    @Override
    public CloudCrossBidRequestImpressionBannerObjectDTO getBidRequestImpressionBannerObject() {
        return bidRequestImpressionBannerObject;
    }
}