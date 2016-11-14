package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCrossBidRequestImpressionBannerObjectDTO extends BidRequestImpressionBannerObjectDTO {
    @JsonProperty("mimes")
    @Setter
    private String whitelistedContentMIMEtypes;

    @Override
    public String[] getWhitelistedContentMIMEtypes() {
        String[] strings = new String[1];
        strings[0] = whitelistedContentMIMEtypes;
        return strings;
    }
}