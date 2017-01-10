package com.kritter.bidreqres.entity.inmobi2_3;

import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;

import lombok.Getter;
import lombok.Setter;

public class BidRequestDeviceDTOInmobi extends BidRequestDeviceDTO{
    @JsonProperty("ext")
    @Setter@Getter
    private BidRequestDeviceDTOInmobiExt extensionObject;


}
