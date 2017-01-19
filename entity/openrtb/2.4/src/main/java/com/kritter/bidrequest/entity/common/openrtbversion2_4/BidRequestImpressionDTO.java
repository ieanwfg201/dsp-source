package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
This object describes an ad placement or impression being auctioned. A single bid request can include 
multiple Imp objects, a use case for which might be an exchange that supports selling all ad positions 
on a given page. Each Imp object has a required ID so that bids can reference them individually.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionDTO {

	/**
	 A unique identifier for this impression within the context of the bid request 
	 (typically, starts with 1 and increments.
	 */
	@Setter@Getter
	private String id;

	/**
	 A Banner object (Section 3.2.3); required if this impression is offered as a banner ad opportunity.
	 */
	@Setter@Getter
	private BidRequestImpressionBannerObjectDTO banner;

	/**
	 A Video object (Section 3.2.4); required if this impression is offered as a video ad opportunity.
	 */
	@Setter@Getter
	private BidRequestImpressionVideoObjectDTO video;

	/**
	 * An Audio object (Section 3.2.5); required if this impression is offered as an audio ad opportunity.
	 */
	@Setter@Getter
	private BidRequestImpressionAudioObjectDTO audio;

	/**
	 * A Native object (Section 3.2.6); required if this impression is offered as a native ad opportunity.
	 */
	@JsonProperty("native")
	@Setter@Getter
	private BidRequestImpressionNativeObjectDTO bidRequestImpressionNativeObjectDTO;

	/**
	 * A Pmp object (Section 3.2.19) containing any private marketplace deals in effect for this impression.
	 */
	@Setter@Getter
	private BidRequestPMPDTO pmp;

	/**
	 Name of ad mediation partner, SDK technology, or player responsible for rendering ad 
	 (typically video or mobile). Used by some ad servers to customize ad code by partner. 
	 Recommended for video and/or apps.
	 */
	@Setter@Getter
	private String displaymanager;

	/**
	 Version of ad mediation partner, SDK technology, or player responsible for rendering ad 
	 (typically video or mobile). Used by some ad servers to customize ad code by partner. 
	 Recommended for video and/or apps.
	 */
	@JsonProperty("")
	@Setter@Getter
	private String displaymanagerver;

	/**
	 1 = the ad is interstitial or full screen, 0 = not interstitial.
	 */
	@Setter@Getter
	private Integer instl ;

	/**
	 Identifier for specific ad placement or ad tag that was used to initiate the auction. 
	 This can be useful for debugging of any issues, or for optimization by the buyer.
	 */
	@Setter@Getter
	private String tagid;

	/**
	 * Minimum bid for this impression expressed in CPM.
	 */
	@Setter@Getter
	private Double bidfloor=0.0;

	/**
	 Currency specified using ISO-4217 alpha codes. 
	 This may be different from bid currency returned by bidder if this is allowed by the exchange.
	 */
	@Setter@Getter
	private String bidfloorcur="USD";

	/**
	 * Indicates the type of browser opened upon clicking the creative in an app, 
	 * where 0 = embedded, 1 = native. Note that the Safari View Controller in iOS 9.x devices is
	 *  considered a native browser for purposes of this attribute.
	 */
	@Setter@Getter
	private Integer clickbrowser;

	/**Flag to indicate if the impression requires secure HTTPS URL creative assets and markup, 
	 * where 0 = non-secure, 1 = secure. If omitted, the secure state is unknown, 
	 * but non-secure HTTP support can be assumed.*/
	@Setter@Getter
	private Integer secure;

	/**
	 Array of exchange-specific names of supported iframe busters.
	 */
	@Setter@Getter
	private String[] iframebuster;

	/**
	Advisory as to the number of seconds that may elapse between the auction and the actual impression..
	 */
	@Setter@Getter
	private Integer exp;

	/**
	 * extension object.
	 */
	@JsonProperty("ext")
	@Setter@Getter
	private Object extensionObject;
}