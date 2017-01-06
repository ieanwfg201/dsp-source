package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This object is the private marketplace container for direct deals between buyers and sellers that may pertain to this 
 * impression. The actual deals are represented as a collection of Deal objects. Refer to Section 7.3 for more details.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestPMPDTO {

    /**
     * Indicator of auction eligibility to seats named in the Direct Deals object, where 0 = all bids are accepted, 
     * 1 = bids are restricted to the deals specified and the terms thereof.
     */
    @Setter@Getter
    private Integer private_auction=0;

    /**
     * Array of Deal (Section 3.2.20) objects that convey the specific deals applicable to this impression
     */
    @Setter@Getter
    private BidRequestDealDTO[] deals;

    /**Placeholder for exchange-specific extensions to OpenRTB.*/
    @Setter@Getter
    private Object ext;

}
