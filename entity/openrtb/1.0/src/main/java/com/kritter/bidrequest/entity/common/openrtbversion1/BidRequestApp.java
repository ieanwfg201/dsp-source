package com.kritter.bidrequest.entity.common.openrtbversion1;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * An “app” object should be included if the ad supported content is part of a
 * mobile application (as opposed to a mobile website). A bid request must not
 * contain both an “app” object and a “site” object.
 * if there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestApp {

	/**
	 * Application ID on the exchange.
	 */
	@JsonProperty("aid")
	private String applicationId;

	/**
	 * Application name; may be masked at publisher’s request.
	 */
	@JsonProperty("name")
	private String applicationName;

	/**
	 * Domain of the application (e.g., “mygame.foo.com”).
	 */
	@JsonProperty("domain")
	private String applicationDomain;

	/**
	 * Publisher ID of the application.
	 */
	@JsonProperty("pid")
	private String publisherId;

	/**
	 * Publisher name; may be masked at publisher’s request.
	 */
	@JsonProperty("pub")
	private String publisherName;

	/**
	 * Domain of the publisher (e.g., “foopub.com”).
	 */
	@JsonProperty("pdomain")
	private String publisherDomain;

	/**
	 * Array of content categories of the application.
	 * 
	 * IAB1 Arts & Entertainment IAB14 Society
	 * 
	 * IAB2 Automotive IAB15 Science
	 * 
	 * IAB3 Business IAB16 Pets
	 * 
	 * IAB4 Careers IAB17 Sports
	 * 
	 * IAB5 Education IAB18 Style & Fashion
	 * 
	 * IAB6 Family & Parenting IAB19 Technology & Computing
	 * 
	 * IAB7 Health & Fitness IAB20 Travel
	 * 
	 * IAB8 Food & Drink IAB21 Real Estate
	 * 
	 * IAB9 Hobbies & Interests IAB22 Shopping
	 * 
	 * IAB10 Home & Garden IAB23 Religion & Spirituality
	 * 
	 * IAB11 Law Government & Politics IAB24 Uncategorized
	 * 
	 * IAB12 News IAB25 Non-Standard Content
	 * 
	 * IAB13 Personal Finance IAB26 Illegal Content
	 */
	@JsonProperty("cat")
	private String[] contentCategories;

	/**
	 * Comma separated list of keywords related to application content.
	 */
	@JsonProperty("keywords")
	private String contentKeywordsCSV;

	/**
	 * Application version.
	 */
	@JsonProperty("ver")
	private String applicationVersion;

	/**
	 * Application bundle (e.g., com.foo.mygame).
	 */
	@JsonProperty("bundle")
	private String applicationBundle;

	/**
	 * “1” if the application is a paid version; else “0” (i.e., free).
	 */
	@JsonProperty("paid")
	private Integer isApplicationPaidVersion;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationDomain() {
		return applicationDomain;
	}

	public void setApplicationDomain(String applicationDomain) {
		this.applicationDomain = applicationDomain;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPublisherDomain() {
		return publisherDomain;
	}

	public void setPublisherDomain(String publisherDomain) {
		this.publisherDomain = publisherDomain;
	}

	public String[] getContentCategories() {
		return contentCategories;
	}

	public void setContentCategories(String[] contentCategories) {
		this.contentCategories = contentCategories;
	}

	public String getContentKeywordsCSV() {
		return contentKeywordsCSV;
	}

	public void setContentKeywordsCSV(String contentKeywordsCSV) {
		this.contentKeywordsCSV = contentKeywordsCSV;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getApplicationBundle() {
		return applicationBundle;
	}

	public void setApplicationBundle(String applicationBundle) {
		this.applicationBundle = applicationBundle;
	}

	public Integer getIsApplicationPaidVersion() {
		return isApplicationPaidVersion;
	}

	public void setIsApplicationPaidVersion(Integer isApplicationPaidVersion) {
		this.isApplicationPaidVersion = isApplicationPaidVersion;
	}

}
