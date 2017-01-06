package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *Segment objects are essentially key-value pairs that convey specific units of data about the user. The parent Data 
 *object is a collection of such values from a given data provider. The specific segment names and value options must be 
 *published by the exchange a priori to its bidders.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestDataSegmentDTO {

	/**
	 * ID of the data segment specific to the data provider
	 */
    @Setter@Getter
	private String id;

	/**
	 * Name of the data segment specific to the data provider.
	 */
    @Setter@Getter
	private String name;

	/**
	 * String representation of the data segment value.
	 */
    @Setter@Getter
	private String value;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;
}
