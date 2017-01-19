package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 This object represents the most general type of impression. Although the term “banner” may have very specific meaning 
 in other contexts, here it can be many things including a simple static image, an expandable ad unit, or even in-banner 
 video (refer to the Video object in Section 3.2.4 for the more generalized and full featured video ad units). An array of 
 Banner objects can also appear within the Video to describe optional companion ads defined in the VAST specification.
The presence of a Banner as a subordinate of the Imp object indicates that this impression is offered as a banner type 
impression. At the publisher’s discretion, that same impression may also be offered as video, audio, and/or native by also 
including as Imp subordinates objects of those types. However, any given bid for the impression must conform to one of the 
offered types.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionBannerObjectDTO {

	/**
	 Width in device independent pixels (DIPS). If no format objects are specified, this is an exact width requirement. 
	 Otherwise it is a preferred width.
	 */
    @Setter@Getter
	private Integer w;

	/**
	 Height in device independent pixels (DIPS). If no format objects are specified, this is an exact height requirement. 
	 Otherwise it is a preferred height
	 */
    @Setter@Getter
	private Integer h;

    /**
	 Height in device independent pixels (DIPS). If no format objects are specified, this is an exact height requirement. 
	 Otherwise it is a preferred height
	 */
   @Setter@Getter
	private BidRequestBannerFormat[] format;

    /**
     * NOTE: Deprecated in favor of the format array. Maximum width in device independent pixels (DIPS).
     * .*/
    @Setter@Getter@Deprecated
    private Integer wmax;

    /**
     * NOTE: Deprecated in favor of the format array. Maximum height in device independent pixels (DIPS).
     * */
    @Setter@Getter@Deprecated
    private Integer hmax;

    /**NOTE: Deprecated in favor of the format array. Minimum width in device independent pixels (DIPS).*/
    @Setter@Getter@Deprecated
    private Integer wmin;

    /**NOTE: Deprecated in favor of the format array. Minimum height in device independent pixels (DIPS).*/
    @Setter@Getter@Deprecated
    private Integer hmin;

    /**
	 Unique identifier for this banner object. Recommended when Banner objects are used with a Video object (Section 3.2.4)
	  to represent an array of companion ads. Values usually start at 1 and increase with each object; should be unique 
	  within an impression.
	 */
    @Setter@Getter
	private String id;

	/**
	 Blocked banner ad types. Refer to List 5.2.
	 */
    @Setter@Getter
	private Integer[] btype;

	/**
	Blocked creative attributes. Refer to List 5.3.
	 */
    @Setter@Getter
	private Short[] battr;

	/**
	 Ad position on screen. Refer to List 5.4.
	 */
    @Setter@Getter
	private Integer pos;

	/**
	 Content MIME types supported. Popular MIME types may include “application/x-shockwave-flash”, “image/jpg”, and 
	 “image/gif”.
	 */
    @Setter@Getter
	private String[] mimes;

	/**
	 Indicates if the banner is in the top frame as opposed to an iframe, where 0 = no, 1 = yes.
	 */
    @Setter@Getter
	private Integer topframe;

	/**
	 Directions in which the banner may expand. Refer to List 5.5.
	 */
    @Setter@Getter
	private Integer[] expdir;

	/**
	List of supported API frameworks for this impression. Refer to List 5.6. 
	If an API is not explicitly listed, it is assumed not to be supported.
	 */
    @Setter@Getter
	private Integer[] api;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;
}