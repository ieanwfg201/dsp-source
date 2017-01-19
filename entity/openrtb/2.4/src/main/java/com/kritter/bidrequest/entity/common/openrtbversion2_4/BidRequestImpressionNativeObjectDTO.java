package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionNativeObjectDTO {

	/** 
	 * Request payload complying with the Native Ad Specification
	 */
    @Setter@Getter
	private String request;

	/**
	 * Version of the Dynamic Native Ads API to which request complies; highly recommended for efficient parsing.
	 */
    @Setter@Getter
    private String ver;
    /**
     * List of supported API frameworks for this impression. Refer to List 5.6. If an API is not explicitly listed, 
     * it is assumed not to be supported
     */
    @Setter@Getter
    private Integer[] api;
    
    /**
     * Blocked creative attributes. Refer to List 5.3.
     */
    @Setter@Getter
    private Integer[] battr;

    @Setter@Getter
    private Object ext;
}
