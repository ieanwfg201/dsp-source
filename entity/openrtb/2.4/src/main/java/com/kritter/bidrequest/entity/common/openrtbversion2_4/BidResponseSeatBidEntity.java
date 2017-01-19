package com.kritter.bidrequest.entity.common.openrtbversion2_4;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 A bid response can contain multiple SeatBid objects, each on behalf of a different bidder seat and each containing 
 one or more individual bids. If multiple impressions are presented in the request, the group attribute can be used 
 to specify if a seat is willing to accept any impressions that it can win (default) or if it is only interested in winning any if it can win them all as a group.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseSeatBidEntity
{
	/**Array of 1+ Bid objects (Section 4.2.3) each related to an impression. Multiple bids can relate to the 
	 * same impression.*/
    @Setter@Getter
    private BidResponseBidEntity[] bid;

    /**ID of the buyer seat (e.g., advertiser, agency) on whose behalf this bid is made.*/
    @Setter@Getter
    private String seat;

    /**
     * 0 = impressions can be won individually; 1 = impressions must be won or lost as a group.
     */
    @Setter@Getter
    private Integer group;

    @Setter@Getter
    private Object ext;

}
