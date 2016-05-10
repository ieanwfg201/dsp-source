package com.kritter.bidrequest.entity.common.openrtbversion2_2;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This object is the private marketplace container for direct deals between buyers and sellers that may
 pertain to this impression. The actual deals are represented as a collection of Deal objects. Refer to
 Section 7.2 for more details.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestPMPDTO {

    /**
     * Indicator of auction eligibility to seats named in the Direct
     Deals object, where 0 = all bids are accepted, 1 = bids are
     restricted to the deals specified and the terms thereof.
     */
    @JsonProperty("private_auction")
    @Setter
    private Integer privateAuction;

    @JsonIgnore
    public Integer getPrivateAuction(){
        return privateAuction;
    }

    /**
     * Array of Deal (Section 3.2.18) objects that convey the specific
       deals applicable to this impression.
     */
    @JsonProperty("deals")
    @Setter
    private BidRequestDealDTO[] privateAuctionDeals;

    @JsonIgnore
    public BidRequestDealDTO[] getPrivateAuctionDeals(){
        return privateAuctionDeals;
    }

    /*Placeholder for exchange-specific extensions to OpenRTB.*/
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
