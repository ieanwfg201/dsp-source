package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
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
 *NOTE: This top level bid object contains sub objects which are open rtb 2.0=4
 * compliant, the getters of all attributes of this object and subobjects return
 * what has been set into them, as per open rtb if the parameter is not received
 * in bid request json then defaults are given, which would be returned in case
 * no value received from json.
 * 
 * <b>The Default values indicates how optional parameters should be interpreted
 * if explicit values are not provided.The getters return default values
 * wherever applicable as per open rtb 2.4.</b>
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
    @Setter@Getter
	private String id;


	/**
	 * Array of impression objects. Multiple impression auctions may be
	 * specified in a single bid request. At least one impression is required
	 * for a valid bid request.
	 */
    @Setter@Getter
	private BidRequestImpressionDTO[] imp;


	/**
	 * BidRequest site object.In case request is from a wap site.
	 */
    @Setter@Getter
	private BidRequestSiteDTO site;

	/**
	 * BidRequest app object.In case of request is from an app.
	 */
    @JsonProperty("app")
    @Setter@Getter
	private BidRequestAppDTO app;


	/**
	 * BidRequest device object.
	 */
    @Setter@Getter
	private BidRequestDeviceDTO device;

	/**
	 * BidRequest user object.
	 */
    @Setter@Getter
	private BidRequestUserDTO user;

    /*Indicator of test mode in which auctions are not
      billable,where 0 = live mode, 1 = test mode.*/
    @Setter@Getter
    private Integer test;

    /**
     * Auction Type. If “1”, then first price auction. If “2”, then second price
     * auction. Additional auction types can be defined as per the exchange’s
     * business rules. Exchange specific rules should be numbered over 500.
     */
    @Setter@Getter
	private Integer at;

	/**
	 Maximum time in milliseconds to submit a bid to avoid timeout. This value is commonly communicated offline.
	 */
    @Setter@Getter
	private Integer tmax;

	/**
	Whitelist of buyer seats (e.g., advertisers, agencies) allowed to bid on this impression.
	 IDs of seats and knowledge of the buyer’s customers to which they refer must be coordinated between bidders and the exchange a priori. Omission implies no seat restrictions.
	 */
    @Setter@Getter
	private String[] wseat;


	/**
	Flag to indicate if Exchange can verify that the impressions offered represent 
	all of the impressions available in context (e.g., all on the web page, all video spots 
	such as pre/mid/post roll) to support road-blocking. 0 = no or unknown, 1 = yes, the impressions 
	offered represent all that are available.
	 */
	@JsonProperty("allimps")
    @Setter@Getter
	private Integer allimps;

	/**
	 Array of allowed currencies for bids on this bid request using ISO-4217 alpha codes. 
	 Recommended only if the exchange accepts multiple currencies.
	 */
	@JsonProperty("cur")
    @Setter@Getter
	private String[] cur;

	/**
	 Blocked advertiser categories using the IAB content categories. Refer to List 5.1 of doc
	 */
    @Setter@Getter
	private String[] bcat;

	/**
	 Block list of advertisers by their domains (e.g., “ford.com”).
	 */
	@JsonProperty("")
    @Setter@Getter
	private String[] badv;

	/**
	 Block list of applications by their platform-specific exchange-independent application identifiers. 
	 On Android, these should be bundle or package names (e.g., com.foo.mygame). 
	 On iOS, these are numeric IDs.
	 */
   @Setter@Getter
	private String[] bapp;

   /**
    * A Regs object (Section 3.2.18) that specifies any industry, legal, or governmental regulations 
    * in force for this request.
    */
    @Setter@Getter
    private BidRequestRegsDTO regs;

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter@Getter
    private Object extensionObject;
 
}