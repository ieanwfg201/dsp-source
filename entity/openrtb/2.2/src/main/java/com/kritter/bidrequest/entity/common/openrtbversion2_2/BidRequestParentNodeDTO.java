package com.kritter.bidrequest.entity.common.openrtbversion2_2;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class is the parent node for a bid request from the exchange. All the
 * members to be accessed from this class or from its subclasses have to have
 * direct methods inside the BidRequest object for the exchange. Reason being
 * that its one handle where to get all parameters from also if some exchange's
 * bid request has to add some other parameter it could do so in its bid request
 * object, rather than changing the common objects or overriding them.
 * 
 *NOTE: This top level bid object contains sub objects which are open rtb 2.0
 * compliant, the getters of all attributes of this object and subobjects return
 * what has been set into them, as per open rtb if the parameter is not received
 * in bid request json then defaults are given, which would be returned in case
 * no value received from json.
 * 
 * <b>The Default values indicates how optional parameters should be interpreted
 * if explicit values are not provided.The getters return default values
 * wherever applicable as per open rtb 2.0.</b>
 * 
 * If an exchange's bid request object need to modify any sub object it does so
 * in its package and extends the classes present in common package.
 * 
 * If there are new properties added to json, the parsing wont break.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestParentNodeDTO {

	/**
	 * Unique ID of the bid request, provided by the exchange.
	 */
	@JsonProperty("id")
    @Setter
	private String bidRequestId;

    @JsonIgnore
    public String getBidRequestId(){
        return bidRequestId;
    }

	/**
	 * Array of impression objects. Multiple impression auctions may be
	 * specified in a single bid request. At least one impression is required
	 * for a valid bid request.
	 */
    @JsonProperty("imp")
    @Setter
	private BidRequestImpressionDTO[] bidRequestImpressionArray;

    @JsonIgnore
    public BidRequestImpressionDTO[] getBidRequestImpressionArray(){
        return bidRequestImpressionArray;
    }

	/**
	 * BidRequest site object.In case request is from a wap site.
	 */
    @JsonProperty("site")
    @Setter
	private BidRequestSiteDTO bidRequestSite;

    @JsonIgnore
    public BidRequestSiteDTO getBidRequestSite(){
        return bidRequestSite;
    }

	/**
	 * BidRequest app object.In case of request is from an app.
	 */
    @JsonProperty("app")
    @Setter
	private BidRequestAppDTO bidRequestApp;

    @JsonIgnore
    public BidRequestAppDTO getBidRequestApp(){
        return bidRequestApp;
    }

	/**
	 * BidRequest device object.
	 */
    @JsonProperty("device")
    @Setter
	private BidRequestDeviceDTO bidRequestDevice;

    @JsonIgnore
    public BidRequestDeviceDTO getBidRequestDevice(){
        return bidRequestDevice;
    }

	/**
	 * BidRequest user object.
	 */
    @JsonProperty("user")
    @Setter
	private BidRequestUserDTO bidRequestUser;

    @JsonIgnore
    public BidRequestUserDTO getBidRequestUser(){
        return bidRequestUser;
    }

    /**
     * Auction Type. If “1”, then first price auction. If “2”, then second price
     * auction. Additional auction types can be defined as per the exchange’s
     * business rules. Exchange specific rules should be numbered over 500.
     */
	@JsonProperty("at")
    @Setter
	private Integer auctionType;

    @JsonIgnore
    public Integer getAuctionType(){
        return auctionType;
    }

	/**
	 * Maximum amount of time in milliseconds to submit a bid (e.g., 120 means
	 * the bidder has 120ms to submit a bid before the auction is complete). If
	 * this value never changes across an exchange, then the exchange can supply
	 * this information offline.
	 */
	@JsonProperty("tmax")
    @Setter
	private Integer maxTimeoutForBidSubmission;

    @JsonIgnore
    public Integer getMaxTimeoutForBidSubmission(){
        return maxTimeoutForBidSubmission;
    }

	/**
	 * Array of buyer seats allowed to bid on this auction. Seats are an
	 * optional feature of exchange. For example, “4”,”34”,”82”,”A45” indicates
	 * that only advertisers using these exchange seats are allowed to bid on
	 * the impressions in this auction.
	 */
	@JsonProperty("wseat")
    @Setter
	private String[] bidderSeatIds;

    @JsonIgnore
    public String[] getBidderSeatIds(){
        return bidderSeatIds;
    }

	/**
	 * Flag to indicate whether Exchange can verify that all impressions offered
	 * represent all of the impressions available in context (e.g., all
	 * impressions available on the web page; all impressions available for a
	 * video [pre,mid and postroll spots], etc.) to support road-blocking. A
	 * true value should only be passed if the exchange is aware of all
	 * impressions in context for the publisher. “0” means the exchange cannot
	 * verify, and “1” means that all impressions represent all impressions
	 * available.
	 */
	@JsonProperty("allimps")
    @Setter
	private Integer allImpressions;

    @JsonIgnore
    public Integer getAllImpressions(){
        return allImpressions;
    }

	/**
	 * Array of allowed currencies for bids on this bid request using ISO-4217
	 * alphabetic codes. If only one currency is used by the exchange, this
	 * parameter is not required.
	 */
	@JsonProperty("cur")
    @Setter
	private String[] allowedCurrencies;

    @JsonIgnore
    public String[] getAllowedCurrencies(){
        return allowedCurrencies;
    }

	/**
	 * Blocked Advertiser Categories. Note that there is no existing
	 * categorization / taxonomy of advertiser industries. However, as a
	 * substitute exchanges may decide to use IAB categories as an
	 * approximation. Content categories could be found in IAB specification
	 * which could be used as approximation.
	 */
	@JsonProperty("bcat")
    @Setter
	private String[] blockedAdvertiserCategoriesForBidRequest;

    @JsonIgnore
    public String[] getBlockedAdvertiserCategoriesForBidRequest(){
        return blockedAdvertiserCategoriesForBidRequest;
    }

	/**
	 * Array of strings of blocked top- level domains of advertisers. For
	 * example, ,“company1.com”, “company2.com”-.
	 */
	@JsonProperty("badv")
    @Setter
	private String[] blockedAdvertiserDomainsForBidRequest;

    @JsonIgnore
    public String[] getBlockedAdvertiserDomainsForBidRequest(){
        return blockedAdvertiserDomainsForBidRequest;
    }

    @JsonProperty("regs")
    @Setter
    private BidRequestRegsDTO bidRequestRegsDTO;

    @JsonIgnore
    public BidRequestRegsDTO getBidRequestRegsDTO(){
        return bidRequestRegsDTO;
    }

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}