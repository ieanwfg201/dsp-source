package com.kritter.bidrequest.entity.common.openrtbversion1;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *The “imp” object describes the ad position or impression being auctioned. A
 * single bid request can include multiple “imp” objects, a use case for which
 * might be an exchange that supports selling all ad positions on a given page
 * as a bundle. Each “imp” object has a required ID so that bids can reference
 * them individually. An exchange can also conduct private auctions by
 * restricting involvement to specific subsets of seats within bidders. Version
 * 1 of openrtb may not support videos. The impression object itself comprises
 * of properties that would hold valid for banner,text and interstitials.
 * If there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpression {

	/**
	 * A unique identifier for this impression within the context of the bid
	 * request (typically, value starts with 1, and increments up to n for n
	 * impressions).
	 */
	@JsonProperty("impid")
	private String bidRequestImpressionId;

	/**
	 * Array of allowed bidder seats; default is unrestricted.
	 * 
	 */
	@JsonProperty("wseat")
	private String[] allowedBidderSeats;

	/**
	 * Height in pixels.
	 */
	@JsonProperty("h")
	private Integer heightAdUnitInPixels;

	/**
	 * Width in pixels.
	 */
	@JsonProperty("w")
	private Integer widthAdUnitInPixels;

	/**
	 * Position of the impression on the page.The following table specifies the
	 * position of the ad as a relative measure of visibility or prominence. The
	 * default is “0” indicating a prominent position such as a header. VALUE
	 * DESCRIPTION
	 * 
	 * 1 Definitely visible without scrolling (i.e., “above the fold”).
	 * 
	 * 2 May or may not be immediately visible depending on screen size and
	 * resolution.
	 * 
	 * 3 High likelihood that the ad will initially appear off-screen (i.e.,
	 * “below the fold”).
	 */
	@JsonProperty("pos")
	private Integer adUnitPositionOnPage;

	/**
	 * 1 if the ad is interstitial or full screen; else 0 (i.e., no).
	 */
	@JsonProperty("instl")
	private Integer isAdInterstitial;

	/**
	 * Array of blocked ad types.
	 * 
	 * The following table indicates the types of ads that can be accepted by
	 * the exchange unless restricted by publisher site settings. VALUE
	 * DESCRIPTION
	 * 
	 * 1 XHTML text ad.
	 * 
	 * 2 XHTML banner ad.
	 * 
	 * 3 JavaScript ad; must be valid XHTML (i.e., script tags included).
	 */
	@JsonProperty("btype")
	private String[] blockedAdTypes;

	/**
	 * Array of blocked creative attributes
	 * 
	 * The following table specifies a standard list of creative attributes that
	 * can describe an ad being served or serve as restrictions of thereof.
	 * VALUE DESCRIPTION
	 * 
	 * 1 Audio Ad (Auto Play)
	 * 
	 * 2 Audio Ad (User Initiated)
	 * 
	 * 3 Expandable (Automatic)
	 * 
	 * 4 Expandable (User Initiated - Click)
	 * 
	 * 5 Expandable (User Initiated - Rollover)
	 * 
	 * 6 In-Banner Video Ad (Auto Play)
	 * 
	 * 7 In-Banner Video Ad (User Initiated)
	 * 
	 * 8 Pop (e.g., Over, Under, or upon Exit)
	 * 
	 * 9 Provocative or Suggestive Imagery
	 * 
	 * 10 Shaky, Flashing, Flickering, Extreme Animation, Smileys
	 * 
	 * 11 Surveys
	 * 
	 * 12 Text Only
	 * 
	 * 13 User Interactive (e.g., Embedded Games)
	 * 
	 * 14 Windows Dialog or Alert Style
	 */
	@JsonProperty("battr")
	private String[] blockedCreativeAttributes;

	public String getBidRequestImpressionId() {
		return bidRequestImpressionId;
	}

	public void setBidRequestImpressionId(String bidRequestImpressionId) {
		this.bidRequestImpressionId = bidRequestImpressionId;
	}

	public String[] getAllowedBidderSeats() {
		return allowedBidderSeats;
	}

	public void setAllowedBidderSeats(String[] allowedBidderSeats) {
		this.allowedBidderSeats = allowedBidderSeats;
	}

	public Integer getHeightAdUnitInPixels() {
		return heightAdUnitInPixels;
	}

	public void setHeightAdUnitInPixels(Integer heightAdUnitInPixels) {
		this.heightAdUnitInPixels = heightAdUnitInPixels;
	}

	public Integer getWidthAdUnitInPixels() {
		return widthAdUnitInPixels;
	}

	public void setWidthAdUnitInPixels(Integer widthAdUnitInPixels) {
		this.widthAdUnitInPixels = widthAdUnitInPixels;
	}

	public Integer getAdUnitPositionOnPage() {
		return adUnitPositionOnPage;
	}

	public void setAdUnitPositionOnPage(Integer adUnitPositionOnPage) {
		this.adUnitPositionOnPage = adUnitPositionOnPage;
	}

	public Integer getIsAdInterstitial() {
		return isAdInterstitial;
	}

	public void setIsAdInterstitial(Integer isAdInterstitial) {
		this.isAdInterstitial = isAdInterstitial;
	}

	public String[] getBlockedAdTypes() {
		return blockedAdTypes;
	}

	public void setBlockedAdTypes(String[] blockedAdTypes) {
		this.blockedAdTypes = blockedAdTypes;
	}

	public String[] getBlockedCreativeAttributes() {
		return blockedCreativeAttributes;
	}

	public void setBlockedCreativeAttributes(String[] blockedCreativeAttributes) {
		this.blockedCreativeAttributes = blockedCreativeAttributes;
	}
}
