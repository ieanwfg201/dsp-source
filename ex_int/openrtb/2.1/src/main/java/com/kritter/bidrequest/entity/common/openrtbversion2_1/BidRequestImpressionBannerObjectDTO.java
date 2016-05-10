package com.kritter.bidrequest.entity.common.openrtbversion2_1;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A banner object.This class is contained inside the bid request impression
 * object. Required for banner impressions.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionBannerObjectDTO {

	/**
	 * Width of the impression in pixels. Since some ad types are not restricted
	 * by size this field is not required, but it’s highly recommended that this
	 * information be included when possible.
	 */
	@JsonProperty("w")
    @Setter
	private Integer bannerWidthInPixels;

    @JsonIgnore
    public Integer getBannerWidthInPixels(){
        return bannerWidthInPixels;
    }

	/**
	 * Height of the impression in pixels. Since some ad types are not
	 * restricted by size this field is not required, but it’s highly
	 * recommended that this information be included when possible.
	 */
	@JsonProperty("h")
    @Setter
	private Integer bannerHeightInPixels;

    @JsonIgnore
    public Integer getBannerHeightInPixels(){
        return bannerHeightInPixels;
    }

	/**
	 * Unique identifier for this banner object. Useful for tracking multiple
	 * banner objects (e.g., in companion banner array). Usually starts with 1,
	 * increasing with each object. Combination of impression id banner object
	 * should be unique.
	 */
	@JsonProperty("id")
    @Setter
	private String bannerUniqueId;

    @JsonIgnore
    public String getBannerUniqueId(){
        return bannerUniqueId;
    }

	/**
	 * Ad Position.
	 * 
	 * 0 Unknown
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
    @Setter
	private Integer adPosition;

    @JsonIgnore
    public Integer getAdPosition(){
        return adPosition;
    }

	/**
	 * Blocked creative types.
	 * 
	 * 1 XHTML text ad. (usually mobile)
	 * 
	 * 2 XHTML banner ad. (usually mobile)
	 * 
	 * 3 JavaScript ad; must be valid XHTML (i.e., script tags included).
	 * 
	 * 4 Iframe .
	 * 
	 * If blank, assume all types are allowed
	 */
	@JsonProperty("btype")
    @Setter
	private Short[] blockedCreativeTypes;

    @JsonIgnore
    public Short[] getBlockedCreativeTypes(){
        return blockedCreativeTypes;
    }

	/**
	 * Blocked creative attributes
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
	 * 
	 * 15 Has audio on/off button
	 * 
	 * 16 Ad can be skipped (e.g., skip button on preroll video)
	 * 
	 * If blank or null assume all types are allowed.
	 */
	@JsonProperty("battr")
    @Setter
	private Short[] blockedCreativeAttributes;

    @JsonIgnore
    public Short[] getBlockedCreativeAttributes(){
        return blockedCreativeAttributes;
    }

	/**
	 * Whitelist of content MIME types supported. Popular MIME types include,
	 * but are not limited to “image/jpg”, “image/gif” and
	 * “application/x-shockwave-flash”.
	 */
	@JsonProperty("mimes")
    @Setter
	private String[] whitelistedContentMIMEtypes;

    @JsonIgnore
    public String[] getWhitelistedContentMIMEtypes(){
        return whitelistedContentMIMEtypes;
    }

	/**
	 * Specify if the banner is delivered in the top frame or in an iframe. “0”
	 * means it is not in the top frame, and “1” means that it is.
	 */
	@JsonProperty("topframe")
    @Setter
	private Integer bannerDeliveredInTopFrameOrIframe;

    @JsonIgnore
    public Integer getBannerDeliveredInTopFrameOrIframe(){
        return bannerDeliveredInTopFrameOrIframe;
    }

	/**
	 * Specify properties for an expandable ad.Expandable Direction for possible
	 * values.
	 * 
	 * 1 Left
	 * 
	 * 2 Right
	 * 
	 * 3 Up
	 * 
	 * 4 Down
	 */
	@JsonProperty("expdir")
    @Setter
	private Integer[] expandableAdDirections;

    @JsonIgnore
    public Integer[] getExpandableAdDirections(){
        return expandableAdDirections;
    }

	/**
	 * List of supported API frameworks for this banner. If an API is not
	 * explicitly listed it is assumed not to be supported.
	 * 
	 * 1 VPAID 1.0
	 * 
	 * 2 VPAID 2.0
	 * 
	 * Video Player-Ad API definition (VPAID) standardizes the communication
	 * between video players and in-stream video advertising as designed by the
	 * Digital Video Committee of the Interactive Advertising Bureau (IAB). This
	 * standard intends to meet the needs of emerging in-stream ad formats such
	 * as:
	 * 
	 * Non-linear video ads Interactive video ads The goal of the VPAID standard
	 * is to address known interoperability issues between publisher’s video
	 * players and different ads. Today, video ads created using a specific ad
	 * technology can only run on publisher video players that are
	 * pre-integrated with that same technology. This issue is further
	 * exacerbated when the video ad is expressed in innovative formats (such as
	 * non-linear and interactive ads) that require a high level of
	 * communication and interaction between the ad and the video player.
	 */
	@JsonProperty("api")
    @Setter
	private Integer[] apiSupportedVersionsForBannerRendering;

    @JsonIgnore
    public Integer[] getApiSupportedVersionsForBannerRendering(){
        return apiSupportedVersionsForBannerRendering;
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