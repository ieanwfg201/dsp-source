package com.kritter.bidreqres.entity.inmobi2_3;

import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestAppDTO;

import lombok.Getter;
import lombok.Setter;

public class BidRequestAppDTOInmobi extends BidRequestAppDTO{
    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter@Getter
    private BidRequestAppDTOInmobiExt extensionObject;

}
