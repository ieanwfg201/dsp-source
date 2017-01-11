package com.kritter.bidreqres.entity.inmobi2_3;

import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;

import lombok.Getter;
import lombok.Setter;

public class BidRequestUserDTOInmobi extends BidRequestUserDTO{

    @JsonProperty("ext")
    @Setter@Getter
    private BidRequestUserDTOInmobiExt extensionObject;

}
