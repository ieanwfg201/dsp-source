package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This object describes the publisher of the media in which the ad will be displayed. The publisher is typically 
 * the seller in an OpenRTB transaction.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestPublisherDTO {

	/**
	 * Exchange-specific publisher ID.
	 */
    @Setter@Getter
	private String id;

	/**
	 * Publisher name (may be aliased at the publisher’s request).
	 */
    @Setter@Getter
	private String name;

	/**
	 * Array of IAB content categories that describe the publisher. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] cat;

	/**
	 * Highest level domain of the publisher (e.g., “publisher.com”).
	 */
    @Setter@Getter
	private String domain;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;

}
