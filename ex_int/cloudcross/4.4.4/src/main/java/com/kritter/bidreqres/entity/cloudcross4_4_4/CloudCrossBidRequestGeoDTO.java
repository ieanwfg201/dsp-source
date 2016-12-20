package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCrossBidRequestGeoDTO extends BidRequestGeoDTO {
    @JsonProperty("state")
    @Setter
    private String state;

    public String getState() {
        return state;
    }
}