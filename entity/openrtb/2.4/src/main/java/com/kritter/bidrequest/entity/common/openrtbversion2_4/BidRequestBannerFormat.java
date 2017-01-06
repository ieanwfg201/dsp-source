package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * This object represents an allowed size (i.e., height and width combination) for a banner impression. 
 * These are typically used in an array for an impression where multiple sizes are permitted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestBannerFormat {
	/**
	 * Width in device independent pixels (DIPS).
	 */
	@Getter@Setter
	private Integer w;
	/**
	 * Height in device independent pixels (DIPS).
	 */
	@Getter@Setter
	private Integer h;
	
	@Getter@Setter
	private Object ext;
}
