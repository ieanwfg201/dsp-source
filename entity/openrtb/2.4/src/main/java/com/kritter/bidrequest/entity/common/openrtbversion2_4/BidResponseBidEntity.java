package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A SeatBid object contains one or more Bid objects, each of which relates to a specific impression in the bid 
 * request via the impid attribute and constitutes an offer to buy that impression for a given price.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseBidEntity
{
	/**
	 * Bidder generated bid ID to assist with logging/tracking.
	 */
    @Setter@Getter
    private String id;
    /**ID of the Imp object in the related bid request.*/
    @Setter@Getter
    private String impid;

    /**
     * Bid price expressed as CPM although the actual transaction is for a unit impression only. Note that while the type 
     * indicates float, integer math is highly recommended when handling currencies (e.g., BigDecimal in Java).
     */
    @Setter@Getter
    private float price;
    
    /**ID of a preloaded ad to be served if the bid wins.*/
    @Setter@Getter
    private String adid;

    /**Win notice URL called by the exchange if the bid wins (not necessarily indicative of a delivered, viewed, or 
     * billable ad); optional means of serving ad markup.*/
    @Setter@Getter
    private String nurl;


    /**
     * Optional means of conveying ad markup in case the bid wins; supersedes the win notice if markup is included in both.
     */
    @Setter@Getter
    private String adm;

    /**Advertiser domain for block list checking (e.g., “ford.com”). This can be an array of for the case of rotating 
     * creatives. Exchanges can mandate that only one domain is allowed.*/
    @Setter@Getter
    private String[] adomain;

    /**
     * A platform-specific application identifier intended to be unique to the app and independent of the exchange. On 
     * Android, this should be a bundle or package name (e.g., com.foo.mygame). On iOS, it is a numeric ID.
     */
    @Setter@Getter
    private String bundle;

    /**URL without cache-busting to an image that is representative of the content of the campaign for ad 
     * quality/safety checking.*/
    @Setter@Getter
    private String iurl;

    /**Campaign ID to assist with ad quality checking; the collection of creatives for which iurl should be representative.*/
    @Setter@Getter
    private String cid;

    /**Creative ID to assist with ad quality checking*/
    @Setter@Getter
    private String crid;

    /**IAB content categories of the creative. Refer to List 5.1*/
    @Setter@Getter
    private String[] cat;
    
    /**Set of attributes describing the creative. Refer to List 5.3.*/
    @Setter@Getter
    private Integer[] attr ;

    /**API required by the markup if applicable. Refer to List 5.6.*/
    @Setter@Getter
    private Integer api ;

    /**Video response protocol of the markup if applicable. Refer to List 5.8.*/
    @Setter@Getter
    private Integer protocol ;

    /**Creative media rating per IQG guidelines. Refer to List 5.17.*/
    @Setter@Getter
    private Integer qagmediarating ;
    /**
     * Reference to the deal.id from the bid request if this bid pertains to a private marketplace direct deal.
     */
    @Setter@Getter
    private String dealid ;

    /**
     * Width of the creative in device independent pixels (DIPS).
     */
    @Setter@Getter
    private Integer w ;

    /**
     * Height of the creative in device independent pixels (DIPS).
     */
    @Setter@Getter
    private Integer h ;

    /**
     * Advisory as to the number of seconds the bidder is willing to wait between the auction and the actual impression.
     */
    @Setter@Getter
    private Integer exp ;

    @Setter@Getter
    private Object ext;

}
