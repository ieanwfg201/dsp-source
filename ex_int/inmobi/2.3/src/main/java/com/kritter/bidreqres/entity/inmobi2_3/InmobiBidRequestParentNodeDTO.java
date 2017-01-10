package com.kritter.bidreqres.entity.inmobi2_3;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;

import lombok.Getter;
import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InmobiBidRequestParentNodeDTO extends BidRequestParentNodeDTO
{
    @JsonProperty("imp")
    @Setter@Getter
	private BidRequestImpressionDTOInmobi[] bidRequestImpressionArray;
    
    @JsonProperty("app")
    @Setter@Getter
	private BidRequestAppDTOInmobi bidRequestApp;

    @JsonProperty("device")
    @Setter@Getter
	private BidRequestDeviceDTOInmobi bidRequestDevice;
    
    @JsonProperty("user")
    @Setter@Getter
	private BidRequestUserDTOInmobi bidRequestUser;


}
