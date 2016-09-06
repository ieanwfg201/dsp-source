package com.kritter.bidrequest.entity.common.openrtbversion1;

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
 * NOTE: This top level bid object contains sub objects which are open rtb 1.0
 * compliant, the getters of all attributes of this object and subobjects return
 * what has been set into them, as per open rtb if the parameter is not received
 * in bid request json then defaults are given, which would be returned in case
 * no value received from json.
 * 
 * The Default values indicates how optional parameters should be interpreted if
 * explicit values are not provided.
 * 
 * If an exchange's bid request object need to modify any sub object it does so
 * in its package and extends the classes present in common package.
 * 
 * Extensions may be provided, if anything additional is provided by the
 * exchange then it could be inside any object.
 * 
 * The strictness on mandatory parameters is checked elsewhere.
 * 
 * If there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestParentNode {

	/**
	 * Unique ID of the bid request, provided by the exchange.
	 */
	@JsonProperty("id")
	private String bidRequestId;

	/**
	 * Array of impression objects. Multiple impression auctions may be
	 * specified in a single bid request. At least one impression is required
	 * for a valid bid request.
	 */
	private BidRequestImpression[] bidRequestImpressionArray;

	/**
	 * BidRequest site object.In case request is from a wap site.
	 */
	private BidRequestSite bidRequestSite;

	/**
	 * BidRequest app object.In case of request is from an app.
	 */
	private BidRequestApp bidRequestApp;

	/**
	 * BidRequest device object.
	 */
	private BidRequestDevice bidRequestDevice;

	/**
	 * BidRequest user object.
	 */
	private BidRequestUser bidRequestUser;

	/**
	 * Auction Type. If “1”, then first price auction. If “2”, then second price
	 * auction. Additional auction types can be defined as per the exchange’s
	 * business rules. Exchange specific rules should be numbered over 500.
	 */
	@JsonProperty("at")
	private Integer auctionType;

	/**
	 * Maximum amount of time in milliseconds to submit a bid (e.g., 120 means
	 * the bidder has 120ms to submit a bid before the auction is complete). If
	 * this value never changes across an exchange, then the exchange can supply
	 * this information offline.
	 */
	@JsonProperty("tmax")
	private Integer maxTimeoutForBidSubmission;

	/**
	 * Bid request Restrictions.
	 */
	private BidRequestRestriction bidRequestRestriction;

	public String getBidRequestId() {
		return bidRequestId;
	}

	public void setBidRequestId(String bidRequestId) {
		this.bidRequestId = bidRequestId;
	}

	public Integer getAuctionType() {
		return auctionType;
	}

	public void setAuctionType(Integer auctionType) {
		this.auctionType = auctionType;
	}

	public Integer getMaxTimeoutForBidSubmission() {
		return maxTimeoutForBidSubmission;
	}

	public void setMaxTimeoutForBidSubmission(Integer maxTimeoutForBidSubmission) {
		this.maxTimeoutForBidSubmission = maxTimeoutForBidSubmission;
	}
}
