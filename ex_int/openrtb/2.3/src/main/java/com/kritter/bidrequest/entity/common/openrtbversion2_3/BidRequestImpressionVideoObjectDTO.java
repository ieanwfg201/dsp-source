package com.kritter.bidrequest.entity.common.openrtbversion2_3;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A video object.This class is contained inside an impression of the bid
 * request.Required for video impressions.
 * 
 * The “video” object must be included directly in the impression object if the
 * impression offered for auction is an in-stream video ad opportunity. Note
 * that for the video object, many of the fields are non-essential for a
 * minimally viable exchange interfaces. These parameters do not necessarily
 * need to be specified to the bidder, if they are always the same for all
 * impression, of if the exchange chooses not to supply the additional
 * information to the bidder.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionVideoObjectDTO {

	/**
	 * Content MIME types supported. Popular MIME types include, but are not
	 * limited to “video/x-ms-wmv” for Windows Media, and “video/x-flv” for
	 * Flash Video.
	 */
	@JsonProperty("mimes")
    @Setter
	private String[] mimeTypesSupported;

    /**
	 * Indicates whether the ad impression is linear or non-linear.Linear would
	 * mean no interaction in the video, non linear means video is interactive
	 * and there would be embedded objects in video which can be interacted
	 * with.
	 * 
	 * 1 Linear/In-stream
	 * 
	 * 2 Non-Linear/Overlay
	 */
	@JsonProperty("linearity")
    @Setter
	private Integer IsVideoLinear;

	/**
	 * Minimum video ad duration in seconds.
	 */
	@JsonProperty("minduration")
    @Setter
	private Integer minimumDurationOfVideo;

	/**
	 * Maximum video ad duration in seconds.
	 */
	@JsonProperty("maxduration")
    @Setter
	private Integer maxDurationOfVideo;

	/**
	 * Video bid response protocols.
	 * 
	 * The IAB’s Video Ad Serving Template (VAST) specification is a universal
	 * XML schema for serving ads to digital video players, and describes
	 * expected video player behavior when executing VAST-­formatted ad
	 * responses. VAST 3.0 adds critical functionality that opens up the
	 * in-stream digital video advertising marketplace, reducing expensive
	 * technical barriers and encouraging advertisers to increase video ad
	 * spend.
	 * 
	 * 1 VAST 1.0
	 * 
	 * 2 VAST 2.0
	 * 
	 * 3 VAST 3.0
	 * 
	 * 4 VAST 1.0 Wrapper
	 * 
	 * 5 VAST 2.0 Wrapper
	 * 
	 * 6 VAST 3.0 Wrapper
	 */
	@JsonProperty("protocols")
    @Setter
	private Integer[] videoBidResponseProtocol;

	/**
	 * Width of the player in pixels. This field is not required, but it’s
	 * highly recommended that this information be included.
	 */
	@JsonProperty("w")
    @Setter
	private Integer widthVideoPlayerInPixels;

	/**
	 * Height of the player in pixels. This field is not required, but it’s
	 * highly recommended that this information be included.
	 */
	@JsonProperty("h")
    @Setter
	private Integer heightVideoPlayerInPixels;

	/**
	 * Indicates the start delay in seconds for preroll, midroll, or postroll ad
	 * placement. The following table lists the various options for the video
	 * start delay. If the start delay value is greater than 0 then the position
	 * is mid-roll, and the value represents the number of seconds into the
	 * content that the ad will be displayed. If the start delay time is not
	 * available, the exchange can report the position of the ad in general
	 * terms using this table of negative numbers.
	 * 
	 * 0 Pre-roll
	 * 
	 * -1(negative) Generic mid-roll
	 * 
	 * -2(negative) Generic Post-roll
	 * 
	 * Meaning, if its 0 the ad is preroll, plays before the actual video plays,
	 * -1,-2 means generic mid roll and post roll respectively, if the value is
	 * greater than 0 then it represent the time in seconds for which ad will be
	 * seen in the content and would be mid roll.
	 */
	@JsonProperty("startdelay")
    @Setter
	private Integer startDelayInSeconds;

	/**
	 * If multiple ad impressions are offered in the same bid request, the
	 * sequence number will allow for the coordinated delivery of multiple
	 * creatives.
	 */
	@JsonProperty("sequence")
    @Setter
	private Integer sequenceCreativeInMultiImpressionsForSameBidRequest;

	/**
	 * Blocked creative attributes. If blank assume all types are allowed. 1
	 * Audio Ad (Auto Play)
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
	 */
	@JsonProperty("battr")
    @Setter
	private Integer[] blockedCreativeAttributes;

	/**
	 * Maximum extended video ad duration, if extension is allowed. If blank or
	 * 0, extension is not allowed. If -1, extension is allowed, and there is no
	 * time limit imposed. If greater than 0, then the value represents the
	 * number of seconds of extended play supported beyond the maxduration
	 * value.
	 */
	@JsonProperty("maxextended")
    @Setter
	private Integer maximumExtendedVideoAdDuration;

	/**
	 * Minimum bit rate in Kbps. Exchange may set this dynamically, or
	 * universally across their set of publishers.
	 */
	@JsonProperty("minbitrate")
    @Setter
	private Integer minimumBitRateVideoInKbps;

	/**
	 * Maximum bit rate in Kbps. Exchange may set this dynamically, or
	 * universally across their set of publishers.
	 */
	@JsonProperty("maxbitrate")
    @Setter
	private Integer maximumBitRateVideoInKbps;

	/**
	 * If exchange publisher has rules preventing letter boxing of 4x3 content
	 * to play in a 16x9 window, then this should be set to false. Default
	 * setting is true, which assumes that boxing of content to fit into a
	 * window is allowed. “1” indicates boxing is allowed. “0” indicates it is
	 * not allowed.
	 */
	@JsonProperty("boxingallowed")
    @Setter
	private Integer contentBoxingAllowed;

	/**
	 * List of allowed playback methods. If blank, assume that all are allowed.
	 * 
	 * 1 Auto-play sound on
	 * 
	 * 2 Auto-play sound off
	 * 
	 * 3 Click-to-play
	 * 
	 * 4 Mouse-over
	 */
	@JsonProperty("playbackmethod")
    @Setter
	private Integer[] playbackMethods;

	/**
	 * List of supported delivery methods (streaming, progressive). If blank,
	 * assume all are supported.
	 * 
	 * 1 Streaming
	 * 
	 * 2 Progressive
	 */
	@JsonProperty("delivery")
    @Setter
	private Integer[] supportedDeliveryMethods;

	/**
	 * Ad Position The following table specifies the position of the ad as a
	 * relative measure of visibility or prominence. Note to the reader: This
	 * OpenRTB table has values derived from the IAB Quality Assurance
	 * Guidelines (QAG). Users of OpenRTB should keep in synch with updates to
	 * the QAG values as published on IAB.net.
	 * 
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

	/**
	 * If companion ads are available, they can be listed as an array of banner
	 * objects. See Banner Object.
	 */
	@JsonProperty("companionad")
    @Setter
	private BidRequestImpressionBannerObjectDTO[] companionBannerObjects;

	/**
	 * List of supported API frameworks for this banner. If an API is not
	 * explicitly listed it is assumed not to be supported.
	 * 
	 * 1 VPAID 1.0
	 * 
	 * 2 VPAID 2.0
	 * 
	 * 3 MRAID
	 * 
	 * 4 ORMMA
	 */
	@JsonProperty("api")
    @Setter
	private Integer[] supportedAPIFrameworkList;

    /*Supported VAST companion ad types. Refer to List 5.12.
      Recommended if companion Banner objects are included via
      the companionad array.
     */
    @JsonProperty("companiontype")
    @Setter
    private Integer[] companionTypes;

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
	public String[] getMimeTypesSupported() {
		return mimeTypesSupported;
	}

    @JsonIgnore
	public Integer getIsVideoLinear() {
		return IsVideoLinear;
	}

    @JsonIgnore
	public Integer getMinimumDurationOfVideo() {
		return minimumDurationOfVideo;
	}

    @JsonIgnore
	public Integer getMaxDurationOfVideo() {
		return maxDurationOfVideo;
	}

    @JsonIgnore
	public Integer[] getVideoBidResponseProtocol() {
		return videoBidResponseProtocol;
	}

    @JsonIgnore
	public Integer getWidthVideoPlayerInPixels() {
		return widthVideoPlayerInPixels;
	}

    @JsonIgnore
	public Integer getHeightVideoPlayerInPixels() {
		return heightVideoPlayerInPixels;
	}

    @JsonIgnore
	public Integer getStartDelayInSeconds() {
		return startDelayInSeconds;
	}

    @JsonIgnore
	public Integer getSequenceCreativeInMultiImpressionsForSameBidRequest() {
		return sequenceCreativeInMultiImpressionsForSameBidRequest;
	}

    @JsonIgnore
	public Integer[] getBlockedCreativeAttributes() {
		return blockedCreativeAttributes;
	}

    @JsonIgnore
	public Integer getMaximumExtendedVideoAdDuration() {
		return maximumExtendedVideoAdDuration;
	}

    @JsonIgnore
	public Integer getMinimumBitRateVideoInKbps() {
		return minimumBitRateVideoInKbps;
	}

    @JsonIgnore
	public Integer getMaximumBitRateVideoInKbps() {
		return maximumBitRateVideoInKbps;
	}

    @JsonIgnore
	public Integer getContentBoxingAllowed() {
		return contentBoxingAllowed;
	}

    @JsonIgnore
	public Integer[] getPlaybackMethods() {
		return playbackMethods;
	}

    @JsonIgnore
	public Integer[] getSupportedDeliveryMethods() {
		return supportedDeliveryMethods;
	}

    @JsonIgnore
	public Integer getAdPosition() {
		return adPosition;
	}

    @JsonIgnore
	public BidRequestImpressionBannerObjectDTO[] getCompanionBannerObjects() {
		return companionBannerObjects;
	}

    @JsonIgnore
	public Integer[] getSupportedAPIFrameworkList() {
		return supportedAPIFrameworkList;
	}

    @JsonIgnore
    public Integer[] getCompanionTypes() {
        return companionTypes;
    }

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
