package com.kritter.bidreqres.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseBidExtYoukuEntity {
	/**
	 * Click URL. This is used in place of clickm. Please ensure that
	 * only one of the clickm and ldp is completed. If both of them
	 * are completed, we will get the value of ldp preferentially.
	 */
	@JsonProperty("ldp")
    @Setter
	private String ldp;
    @JsonIgnore
    public String getLdp(){
        return ldp;
    }
    /**
     * Impression tracking URL. Multiple URLs are supported. Type:
	 *  JSON array.
     */
	@JsonProperty("pm")
    @Setter
	private String[] pm;
    @JsonIgnore
    public String[] getPm(){
        return pm;
    }
    /**
     * Optional field, impression tracking URL, which will be
	 * triggered after ad completes. Multiple URLs are supported.
	 * Type: JSON array.
     */
    @JsonProperty("em")
    @Setter
	private String[] em;
    @JsonIgnore
    public String[] getEm(){
        return em;
    }
    /**
     * Click tracking URL. Multiple URLs are supported. Type: JSON
     * array.
     */
    @JsonProperty("cm")
    @Setter
	private String[] cm;
    @JsonIgnore
    public String[] getCm(){
        return cm;
    }
    /**
     * Material type. Note: Type would be specified as “c” if the
	 * material is dynamic HTML snippet. Other types of material
	 * are not required to provide the field of type.
     */
    @JsonProperty("type")
    @Setter
	private String type;
    @JsonIgnore
    public String geType(){
        return type;
    }
    /**
     * Interactive API used by specified material.
	 * NULL: Common material;
	 * 1: Interactive flv material in VPAID format;
	 * 2: Non-interactive swf material.
     */
    @JsonProperty("api")
    @Setter
	private String api;
    @JsonIgnore
    public String getApi(){
        return api;
    }
}
