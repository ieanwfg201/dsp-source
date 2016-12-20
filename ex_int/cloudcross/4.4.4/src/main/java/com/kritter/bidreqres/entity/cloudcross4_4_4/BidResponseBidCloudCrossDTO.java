package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * This class models bid response bid entity for mopub.
 */
public class BidResponseBidCloudCrossDTO extends BidResponseBidEntity {
    @JsonProperty("curl")
    @Setter
    private List<String> curl;
    @JsonProperty("adurl")
    @Setter
    private String adurl;
    @JsonProperty("casize")
    @Setter
    private String casize;
    @JsonProperty("iurl")
    @Setter
    private List<String> sampleImageUrl;
    @JsonProperty("adomain")
    @Setter
    private String advertiserDomains;
}
