package com.kritter.bidrequest.entity.common.openrtb2_0;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The data and segment objects together allow data about the user to be passed
 * to bidders in the bid request. This data may be from multiple sources (e.g.,
 * the exchange itself, third party providers) as specified by the data object
 * ID field. A bid request can mix data objects from multiple providers. The
 * data object itself and all of its parameters are optional, so default values
 * are not provided. If an optional parameter is not specified, it should be
 * considered unknown
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestUserDataDTO {

	/**
	 * Exchange specific ID for the data provider
	 */
	@JsonProperty("id")
    @Setter
	private String dataProviderIdOnExchange;

    @JsonIgnore
    public String getDataProviderIdOnExchange(){
        return dataProviderIdOnExchange;
    }

	/**
	 * Data provider name
	 */
	@JsonProperty("name")
    @Setter
	private String dataProviderName;

    @JsonIgnore
    public String getDataProviderName(){
        return dataProviderName;
    }

	/**
	 * Array of segment objects
	 */
    @JsonProperty("segment")
    @Setter
	private BidRequestUserDataSegmentDTO[] bidRequestUserDataSegments;

    @JsonIgnore
    public BidRequestUserDataSegmentDTO[] getBidRequestUserDataSegments(){
        return bidRequestUserDataSegments;
    }
}