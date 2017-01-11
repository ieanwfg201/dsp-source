package com.kritter.bidreqres.entity.inmobi2_3;

import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;

import lombok.Setter;

public class BidRequestImpressionDTOInmobi extends BidRequestImpressionDTO{
    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private BidRequestImpressionDTOInmobiExt extensionObject;

}
