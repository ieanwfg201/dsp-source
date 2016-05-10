package com.kritter.bidrequest.entity.common.openrtbversion2_3;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The data and segment objects together allow data about the user to be passed
 * to bidders in the bid request. Segment objects convey specific units of
 * information from the provider identified in the parent data object. The
 * segment object itself and all of its parameters are optional, so default
 * values are not provided; if an optional parameter is not specified, it should
 * be considered unknown.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestUserDataSegmentDTO {

	/**
	 * ID of a data provider’s segment applicable to the user
	 */
	@JsonProperty("id")
    @Setter
	private String segmentId;

    @JsonIgnore
    public String getSegmentId(){
        return segmentId;
    }

	/**
	 * Name of a data provider’s segment applicable to the user
	 */
	@JsonProperty("name")
    @Setter
	private String segmentName;

    @JsonIgnore
    public String getSegmentName(){
        return segmentName;
    }

	/**
	 * String representing the value of the segment. The method for transmitting
	 * this data should be negotiated offline with the data provider. For
	 * example for gender, “male”, or “female”, for age, “30-40”)
	 */
	@JsonProperty("value")
    @Setter
	private String segmentValue;

    @JsonIgnore
    public String getSegmentValue(){
        return segmentValue;
    }

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
