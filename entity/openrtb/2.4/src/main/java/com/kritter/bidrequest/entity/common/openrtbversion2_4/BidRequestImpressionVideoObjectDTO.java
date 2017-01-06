package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
This object represents an in-stream video impression. Many of the fields are non-essential for minimally viable 
transactions, but are included to offer fine control when needed. Video in OpenRTB generally assumes compliance with 
the VAST standard. As such, the notion of companion ads is supported by optionally including an array of Banner objects 
(refer to the Banner object in Section 3.2.3) that define these companion ads.
The presence of a Video as a subordinate of the Imp object indicates that this impression is offered as a video type 
impression. At the publisher’s discretion, that same impression may also be offered as banner, audio, and/or native 
by also including as Imp subordinates objects of those types. However, any given bid for the impression must conform 
to one of the offered types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionVideoObjectDTO {

	/**
		Content MIME types supported. Popular MIME types may include “video/x-ms-wmv” for Windows Media and 
		“video/x-flv” for Flash Video.
	 */
	@Getter@Setter
	private String[] mimes;
	/**
	 Minimum video ad duration in seconds.
	 */
    @Setter@Getter
	private Integer minduration;

	/**
	 * Maximum video ad duration in seconds.
	 */
    @Setter@Getter
	private Integer maxduration;

	/**
	Array of supported video protocols. Refer to List 5.8. At least one supported protocol must be specified in either 
	the protocol or protocols attribute.
	 */
    @Setter@Getter
	private Integer[] protocols;
	/**
	 * NOTE: Deprecated in favor of protocols. Supported video protocol. Refer to List 5.8. At least one supported 
	 * protocol must be specified in either the protocol or protocols attribute.
	 */
    @Setter@Getter@Deprecated
	private Integer protocol;
	/**
	 Width of the video player in device independent pixels (DIPS).
	 */
    @Setter@Getter
	private Integer w;

	/**
	Height of the video player in device independent pixels (DIPS).	 
	*/
    @Setter@Getter
	private Integer h;
	/**
	Indicates the start delay in seconds for pre-roll, mid-roll, or post-roll ad placements. 
	Refer to List 5.10 for additional generic values.	 */
    @Setter@Getter
	private Integer startdelay;

    /**
	Indicates if the impression must be linear, nonlinear, etc. If none specified, assume all are allowed. 
	Refer to List 5.7.	 */
    @Setter@Getter
	private Integer linearity;

    /**
	Indicates if the player will allow the video to be skipped, where 0 = no, 1 = yes. 
	If a bidder sends markup/creative that is itself skippable, the Bid object should include the attr array with an
	element of 16 indicating skippable video. Refer to List 5.3.
	*/
    @Getter@Setter
	private Integer skip;

    /**
Videos of total duration greater than this number of seconds can be skippable; only applicable if the ad is skippable.	
*/
    @Getter@Setter
	private Integer skipmin=0;

    /**
Number of seconds a video must play before skipping is enabled; only applicable if the ad is skippable.*/
    @Getter@Setter
	private Integer skipafter=0;


	/**
If multiple ad impressions are offered in the same bid request, 
the sequence number will allow for the coordinated delivery of multiple creatives.
	 */
    @Setter@Getter
	private Integer sequence;

	/**
Blocked creative attributes. Refer to List 5.3.	 */
    @Setter@Getter
	private Integer[] battr;

	/**
Maximum extended ad duration if extension is allowed. If blank or 0, extension is not allowed. 
If -1, extension is allowed, and there is no time limit imposed. If greater than 0, then the value represents the 
number of seconds of extended play supported beyond the maxduration value.
	 */
    @Setter@Getter
	private Integer maxextended;

	/**
	 Minimum bit rate in Kbps.
	 */
    @Setter@Getter
	private Integer minbitrate;

	/**
	 Maximum bit rate in Kbps.
	 */
    @Setter@Getter
	private Integer maxbitrate;

	/**
	 Indicates if letter-boxing of 4:3 content into a 16:9 window is allowed, where 0 = no, 1 = yes.
	 */
    @Setter@Getter
	private Integer boxingallowed=1;

	/**
	 Playback methods that may be in use. If none are specified, any method may be used. 
	 Refer to List 5.9. Only one method is typically used in practice. 
	 As a result, this array may be converted to an integer in a future version of the specification.
	 */
    @Setter@Getter
	private Integer[] playbackmethod;

	/**
	Supported delivery methods (e.g., streaming, progressive). If none specified, assume all are supported. Refer to List 5.13.
	 */
    @Setter@Getter
	private Integer[] delivery;

	/**
	Ad position on screen. Refer to List 5.4.
	 */
    @Setter@Getter
	private Integer pos;

	/**
	 Array of Banner objects (Section 3.2.3) if companion ads are available.
	 */
    @Setter@Getter
	private BidRequestImpressionBannerObjectDTO[] companionad;

	/**
	 List of supported API frameworks for this impression. Refer to List 5.6. 
	 If an API is not explicitly listed, it is assumed not to be supported.
	 */
    @Setter@Getter
	private Integer[] api;

    /**
     * Supported VAST companion ad types. Refer to List 5.12. Recommended if companion Banner objects are included 
     * via the companionad array.
     */
    @Setter@Getter
    private Integer[] companiontype;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;

}
