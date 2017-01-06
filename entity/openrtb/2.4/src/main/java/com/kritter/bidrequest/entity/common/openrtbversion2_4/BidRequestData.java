package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
The data and segment objects together allow additional data about the user to be specified. This data may be from 
multiple sources whether from the exchange itself or third party providers as specified by the id field. A bid 
request can mix data objects from multiple providers. The specific data providers in use should be published by the 
exchange a priori to its bidders. 
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestData {

	/**
	 * Exchange-specific ID for the data provider
	 */
    @Setter@Getter
	private String id;

	/**
	 * Data provider name
	 */
    @Setter@Getter
	private String name;

	/**
	 * Array of Segment (Section 3.2.17) objects that contain the actual data values.
	 */
    @Setter@Getter
	private BidRequestDataSegmentDTO[] segment;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;

}
