package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This object defines the producer of the content in which the ad will be shown. This is particularly useful 
 * when the content is syndicated and may be distributed through different publishers and thus when the producer and 
 * publisher are not necessarily the same entity.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestContentProducerDTO {

	/**
	 * Content producer or originator ID. Useful if content is syndicated and may be posted on a site using embed tags.
	 */
    @Setter@Getter
	private String id;

	/**
	 * Content producer or originator name (e.g., “Warner Bros”).
	 */
    @Setter@Getter
	private String name;

	/**
	 * AArray of IAB content categories that describe the content producer. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] cat;

	/**
	 * Highest level domain of the content producer (e.g., “producer.com”).
	 */
    @Setter@Getter
	private String domain;

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter@Getter
    private Object extensionObject;

}