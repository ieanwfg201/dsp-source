package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 *This object represents an audio type impression. Many of the fields are non-essential for minimally viable transactions,
 * but are included to offer fine control when needed. Audio in OpenRTB generally assumes compliance with the DAAST 
 * standard. As such, the notion of companion ads is supported by optionally including an array of Banner objects 
 * (refer to the Banner object in Section 3.2.3) that define these companion ads.
The presence of a Audio as a subordinate of the Imp object indicates that this impression is offered as an audio type
 impression. At the publisher’s discretion, that same impression may also be offered as banner, video, and/or native by 
 also including as Imp subordinates objects of those types. However, any given bid for the impression must conform 
 to one of the offered types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionAudioObjectDTO {

	/**
	 * Content MIME types supported (e.g., “audio/mp4”).
	 */
	@Getter@Setter
	private Integer[] mimes;
	/**
	 * Minimum audio ad duration in seconds
	 */
	@Getter@Setter
	private Integer minduration;
	
	/**
	 * Minimum audio ad duration in seconds
	 */
	@Getter@Setter
	private Integer maxduration;
	/**
	 * Array of supported audio protocols. Refer to List 5.8.
	 */
	@Getter@Setter
	private Integer[] protocols;
	
	/**
	 * Indicates the start delay in seconds for pre-roll, mid-roll, or post-roll ad placements. Refer to List 5.10.
	 */
	@Getter@Setter
	private Integer startdelay;
	
	/**
	 * If multiple ad impressions are offered in the same bid request, 
	 * the sequence number will allow for the coordinated delivery of multiple creatives.
	 */
	@Getter@Setter
	private Integer sequence;
	/**
	 * Blocked creative attributes. Refer to List 5.3.
	 */
	@Getter@Setter
	private Integer[] battr;
	/**
	 * Maximum extended ad duration if extension is allowed. If blank or 0, extension is not allowed. If -1, 
	 * extension is allowed, and there is no time limit imposed. If greater than 0, then the value represents the number of 
	 * seconds of extended play supported beyond the maxduration value.
	 */
	@Getter@Setter
	private Integer maxextended;
	/**
	 * Minimum bit rate in Kbps.
	 */
	@Getter@Setter
	private Integer minbitrate;
	/**
	 * Maximum bit rate in Kbps.
	 */
	@Getter@Setter
	private Integer maxbitrate;
	/**
	 * Supported delivery methods (e.g., streaming, progressive). If none specified, assume all are supported. 
	 * Refer to List 5.13.
	 */
	@Getter@Setter
	private Integer[] delivery;
	/**
	 * Supported delivery methods (e.g., streaming, progressive). If none specified, assume all are supported. Refer to
		List 5.13.
	 */
	@Getter@Setter
	private BidRequestImpressionBannerObjectDTO[] companionad;
	
	/**
	 *List of supported API frameworks for this impression. Refer to List 5.6. If an API is not explicitly listed, 
	 *it is assumed not to be supported.
	 */
	@Getter@Setter
	private Integer[] api;
	/**
	 * Supported DAAST companion ad types. Refer to List 5.12. Recommended if companion Banner objects are included 
	 * via the companionad array.
	 */
	@Getter@Setter
	private Integer companiontype;
	/**
	 * The maximum number of ads that can be played in an ad pod.
	 */
	@Getter@Setter
	private Integer maxseq;
	/**
	 * Type of audio feed. Refer to List 5.14.
	 */
	@Getter@Setter
	private Integer feed;
	/**
	 * Indicates if the ad is stitched with audio content or delivered independently, where 0 = no, 1 = yes.
	 */
	@Getter@Setter
	private Integer stitched;
	/**
	 * Volume normalization mode. Refer to List 5.15.
	 */
	@Getter@Setter
	private Integer nvol;
    @Setter@Getter
    private Object ext;

}
