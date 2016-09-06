package com.kritter.bidrequest.entity.common.openrtbversion1;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The bid request as defined in Layer-3 contains a top-level object that
 * includes the attributes required for auction mechanics as well as an array of
 * “imp” (i.e., impression) objects on which to bid. This layer adds to this top
 * level object a set of business objects that enable bidders to evaluate the
 * value of these impressions. Unless otherwise, specified, a bid request can
 * contain 0 or 1 of each of these objects.
 * 
 * A “site” object should be included if the ad supported content is part of a
 * mobile website (as opposed to an application). A bid request must not contain
 * both a “site” object and an “app” object.
 * If there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestSite {

	/**
	 * Site ID on the exchange.
	 */
	@JsonProperty("sid")
	private String siteIdOnExchange;

	/**
	 * Site name; may be masked at publisher’s request.
	 */
	@JsonProperty("name")
	private String siteName;

	/**
	 * Domain of the site (e.g., “foo.com”).
	 */
	@JsonProperty("domain")
	private String siteDomain;

	/**
	 * Publisher ID of the site.
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
	 * Array of content categories of the site or page.
	 * 
	 * The following table details the content categories used to describe site,
	 * application, and advertising content. These codes and descriptions are
	 * derived from the IAB and have been previously adopted by OpenRTB. VALUE
	 * DESCRIPTION VALUE DESCRIPTION
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
	private String[] siteContentCategories;

	/**
	 * Comma separated list of keywords related to site content.
	 */
	@JsonProperty("keywords")
	private String siteContentKeywordsCSV;

	/**
	 * URL of the current page.
	 */
	@JsonProperty("page")
	private String currentPageSiteURL;

	/**
	 * Referrer URL that caused navigation to the current page.
	 */
	@JsonProperty("ref")
	private String referrerURL;

	/**
	 * Search string that caused navigation to the current page.
	 */
	@JsonProperty("search")
	private String searchStringForCurrentURL;

	public String getSiteIdOnExchange() {
		return siteIdOnExchange;
	}

	public void setSiteIdOnExchange(String siteIdOnExchange) {
		this.siteIdOnExchange = siteIdOnExchange;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteDomain() {
		return siteDomain;
	}

	public void setSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
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

	public String[] getSiteContentCategories() {
		return siteContentCategories;
	}

	public void setSiteContentCategories(String[] siteContentCategories) {
		this.siteContentCategories = siteContentCategories;
	}

	public String getSiteContentKeywordsCSV() {
		return siteContentKeywordsCSV;
	}

	public void setSiteContentKeywordsCSV(String siteContentKeywordsCSV) {
		this.siteContentKeywordsCSV = siteContentKeywordsCSV;
	}

	public String getCurrentPageSiteURL() {
		return currentPageSiteURL;
	}

	public void setCurrentPageSiteURL(String currentPageSiteURL) {
		this.currentPageSiteURL = currentPageSiteURL;
	}

	public String getReferrerURL() {
		return referrerURL;
	}

	public void setReferrerURL(String referrerURL) {
		this.referrerURL = referrerURL;
	}

	public String getSearchStringForCurrentURL() {
		return searchStringForCurrentURL;
	}

	public void setSearchStringForCurrentURL(String searchStringForCurrentURL) {
		this.searchStringForCurrentURL = searchStringForCurrentURL;
	}
}
