package com.kritter.bidrequest.entity.common.openrtb2_0;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The producer is useful when content where the ad is shown is syndicated, and
 * may appear on a completely different publisher. The producer object itself
 * and all of its parameters are optional, so default values are not provided.
 * If an optional parameter is not specified, it should be considered unknown.
 * This object is optional, but useful if the content producer is different from
 * the site publisher.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestContentProducerDTO {

	/**
	 * Content producer or originator ID. Useful if content is syndicated, and
	 * may be posted on a site using embed tags.
	 */
	@JsonProperty("id")
    @Setter
	private String contentProducerId;

	/**
	 * Content producer or originator name (e.g., “Warner Bros”).
	 */
	@JsonProperty("name")
    @Setter
	private String contentProducerName;

	/**
	 * Array of IAB content categories for the content producer. See Table 6.1
	 * Content Categories.(open rtb 2.0)
	 */
	@JsonProperty("cat")
    @Setter
	private String[] producerContentCategories;

	/**
	 * URL of the content producer.
	 */
	@JsonProperty("domain")
    @Setter
	private String producerContentDomain;

    @JsonIgnore
	public String getContentProducerId() {
		return contentProducerId;
	}

    @JsonIgnore
	public String getContentProducerName() {
		return contentProducerName;
	}

    @JsonIgnore
    public String[] getProducerContentCategories() {
		return producerContentCategories;
	}

    @JsonIgnore
	public String getProducerContentDomain() {
		return producerContentDomain;
	}
}