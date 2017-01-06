package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This object is the top-level bid response object (i.e., the unnamed outer JSON object). The id attribute is a reflection
 *  of the bid request ID for logging purposes. Similarly, bidid is an optional response tracking ID for bidders. If 
 *  specified, it can be included in the subsequent win notice call if the bidder wins. At least one seatbid object is 
 *  required, which contains at least one bid for an impression. Other attributes are optional.
To express a “no-bid”, the options are to return an empty response with HTTP 204. Alternately if the bidder wishes to 
convey to the exchange a reason for not bidding, just a BidResponse object is returned with a reason code in the nbr 
attribute.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseEntity
{

	/**ID of the bid request to which this is a response.*/
    @Setter@Getter
    private String id;

    /**Array of seatbid objects; 1+ required if a bid is to be made.*/
    @Setter@Getter
    private BidResponseSeatBidEntity[] seatbid;

    /**
     * Bidder generated response ID to assist with logging/tracking.
     */
    @Setter@Getter
    private String bidid;

    /**Bid currency using ISO-4217 alpha codes.*/
    @Setter@Getter
    private String cur;

    /**Optional feature to allow a bidder to set data in the exchange’s cookie. The string must be in base85 
     * cookie safe characters and be in any format. Proper JSON encoding must be used to include “escaped” quotation marks*/
    @Setter@Getter
    private String customdata;

    /**
     * Reason for not bidding. Refer to List 5.22.
     */
    @Setter@Getter
    private Integer nbr;

    @Setter@Getter
    private Object ext;

}
