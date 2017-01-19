package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This object constitutes a specific deal that was struck a priori between a buyer and a seller. Its presence with the 
 * Pmp collection indicates that this impression is available under the terms of that deal. Refer to Section 7.3 for more 
 * details.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestDealDTO {

    /**A unique identifier for the direct deal.*/
    @Setter@Getter
    private String id;

    /**Minimum bid for this impression expressed in CPM.*/
    @Setter@Getter
    private Float bidfloor;

    /**Currency specified using ISO-4217 alpha codes. This may be different from bid currency returned by bidder if 
     * this is allowed by the exchange.*/
    @Setter@Getter
    private String bidfloorcur;

    /**Optional override of the overall auction type of the bid request, where 1 = First Price, 2 = Second Price Plus, 
     * 3 = the value passed in bidfloor is the agreed upon deal price. Additional auction types can be defined by the 
     * exchange.
    */
    @Setter@Getter
    private Integer at;

    /**
     * Whitelist of buyer seats (e.g., advertisers, agencies) allowed to bid on this deal. IDs of seats and knowledge of 
     * the buyer’s customers to which they refer must be coordinated between bidders and the exchange a priori. 
     * Omission implies no seat restrictions.
     */
    @Setter@Getter
    private String[] wseat;

    /**
     * Array of advertiser domains (e.g., advertiser.com) allowed to bid on this deal. Omission implies no advertiser 
     * restrictions.
     */
    @Setter@Getter
    private String[] wadomain;

    /**
     * Placeholder for exchange-specific extensions to OpenRTB.
     */
    @Setter@Getter
    private Object ext;

}
