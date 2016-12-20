package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class models bid response seat bid entity for CloudCross.
 */
public class BidResponseSeatBidCloudCrossDTO extends BidResponseSeatBidEntity {
    @JsonProperty("bid")
    @Setter
    private BidResponseBidEntity bidResponseBidEntities;

}
