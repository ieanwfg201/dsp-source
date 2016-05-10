package com.kritter.bidrequest.entity.common.openrtbversion2_2;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *The “imp” object describes the ad position or impression being auctioned. A
 * single bid request can include multiple “imp” objects, a use case for which
 * might be an exchange that supports selling all ad positions on a given page
 * as a bundle. Each “imp” object has a required ID so that bids can reference
 * them individually. An exchange can also conduct private auctions by
 * restricting involvement to specific subsets of seats within bidders. if there
 * are new properties added to json, the parsing wont break.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionDTO {

	/**
	 * A unique identifier for this impression within the context of the bid
	 * request (typically, value starts with 1, and increments up to n for n
	 * impressions).
	 */
	@JsonProperty("id")
    @Setter
	private String bidRequestImpressionId;

    @JsonIgnore
    public String getBidRequestImpressionId(){
        return bidRequestImpressionId;
    }

	/**
	 * A reference to a banner object. Either a banner or video object (or both
	 * if the impression could be either) must be included in an impression
	 * object; both objects may not be included.This means either the object
	 * included will be banner or video, if both are included that would mean
	 * impression could be either video or banner, so bidder can send out any
	 * response(either banner or video.)
	 */
    @JsonProperty("banner")
    @Setter
	private BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObject;

    @JsonIgnore
    public BidRequestImpressionBannerObjectDTO getBidRequestImpressionBannerObject(){
        return bidRequestImpressionBannerObject;
    }

	/**
	 * A reference to a video object. Either a banner or video object (or both
	 * if the impression could be either) must be included in an impression
	 * object; both objects may not be included.
	 */
    @JsonProperty("video")
    @Setter
	private BidRequestImpressionVideoObjectDTO bidRequestImpressionVideoObject;

    @JsonIgnore
    public BidRequestImpressionVideoObjectDTO getBidRequestImpressionVideoObject(){
        return bidRequestImpressionVideoObject;
    }

    /**
	 * Name of ad mediation partner, SDK technology, or native player
	 * responsible for rendering ad (typically video or mobile). Used by some ad
	 * servers to customize ad code by partner.Recommended for video and native
	 * apps
	 */
	@JsonProperty("displaymanager")
    @Setter
	private String displayManagerForAdRendering;

    @JsonIgnore
    public String getDisplayManagerForAdRendering(){
        return displayManagerForAdRendering;
    }

	/**
	 * Version of ad mediation partner, SDK technology, or native player
	 * responsible for rendering ad (typically video or mobile). Used by some ad
	 * servers to customize ad code by partner.Recommended for video and native
	 * apps
	 */
	@JsonProperty("displaymanagerver")
    @Setter
	private String displayManagerVersion;

    @JsonIgnore
    public String getDisplayManagerVersion(){
        return displayManagerVersion;
    }

	/**
	 * 1 if the ad is interstitial or full screen; else 0 (i.e., no).
	 */
	@JsonProperty("instl")
    @Setter
	private Integer isAdInterstitial;

    @JsonIgnore
    public Integer getIsAdInterstitial(){
        return isAdInterstitial;
    }

	/**
	 * Identifier for specific ad placement or ad tag that was used to initiate
	 * the auction. This can be useful for debugging of any issues, or for
	 * optimization by the buyer.
	 */
	@JsonProperty("tagid")
    @Setter
	private String adTagOrPlacementId;

    @JsonIgnore
    public String getAdTagOrPlacementId(){
        return adTagOrPlacementId;
    }

	/**
	 * Bid floor for this impression (in CPM of bidFloorCurrency).
	 */
	@JsonProperty("bidfloor")
    @Setter
	private Double bidFloorPrice;

    @JsonIgnore
    public Double getBidFloorPrice(){
        return bidFloorPrice;
    }

	/**
	 * If bid floor is specified and multiple currencies supported per bid
	 * request, then currency should be specified here using ISO-4217 alphabetic
	 * codes. Note, this may be different from bid currency returned by bidder,
	 * if this is allowed on an exchange.
	 */
	@JsonProperty("bidfloorcur")
    @Setter
	private String bidFloorCurrency;

    @JsonIgnore
    public String getBidFloorCurrency(){
        return bidFloorCurrency;
    }

	/**
	 *Array of names for supported iframe busters. Exchange specific.
	 */
	@JsonProperty("iframebuster")
    @Setter
	private String[] iframeBusterNames;

    @JsonIgnore
    public String[] getIframeBusterNames(){
        return iframeBusterNames;
    }

    /**
     * A Pmp object (Section 3.2.17) containing any private
     * marketplace deals in effect for this impression.
     */
    @JsonProperty("pmp")
    @Setter
    private BidRequestPMPDTO bidRequestPMPDTO;

    @JsonIgnore
    public BidRequestPMPDTO getBidRequestPMPDTO(){
        return bidRequestPMPDTO;
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