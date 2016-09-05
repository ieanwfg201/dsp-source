package com.kritter.bidrequest.entity.common.openrtbversion2_3;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The publisher object itself and all of its parameters are optional, so
 * default values are not provided. If an optional parameter is not specified,
 * it should be considered unknown.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestPublisherDTO {

	/**
	 * Publisher ID on the exchange.
	 */
	@JsonProperty("id")
    @Setter
	private String publisherIdOnExchange;

    @JsonIgnore
    public String getPublisherIdOnExchange(){
        return publisherIdOnExchange;
    }

	/**
	 * Publisher name (may be masked at publisher’s request).
	 */
	@JsonProperty("name")
    @Setter
	private String publisherName;

    @JsonIgnore
    public String getPublisherName(){
        return publisherName;
    }

	/**
	 * Array of IAB content categories for the publisher. See Table 6.1 Content
	 * Categories.(open rtb 2.0)
	 */
	@JsonProperty("cat")
    @Setter
	private String[] publisherContentCategories;

    @JsonIgnore
    public String[] getPublisherContentCategories(){
        return publisherContentCategories;
    }

	/**
	 * Publisher’s highest level domain name, for example “foopub.com”.
	 */
	@JsonProperty("domain")
    @Setter
	private String publisherDomain;

    @JsonIgnore
    public String getPublisherDomain(){
        return publisherDomain;
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
