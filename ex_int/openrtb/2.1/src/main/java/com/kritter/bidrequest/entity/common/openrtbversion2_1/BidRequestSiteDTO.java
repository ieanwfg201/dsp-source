package com.kritter.bidrequest.entity.common.openrtbversion2_1;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A site object should be included if the ad supported content is part of a
 * website (as opposed to an application). A bid request must not contain both a
 * site object and an app object. The site object itself and all of its
 * parameters are optional, so default values are not provided. If an optional
 * parameter is not specified, it should be considered unknown. At a minimum,
 * it’s useful to provide a page URL or a site ID, but this is not strictly
 * required.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestSiteDTO {

	/**
	 * Site ID on the exchange.
	 */
	@JsonProperty("id")
    @Setter
	private String siteIdOnExchange;

    @JsonIgnore
    public String getSiteIdOnExchange(){
        return siteIdOnExchange;
    }

	/**
	 * Site name (may be masked at publisher’s request).
	 */
	@JsonProperty("name")
    @Setter
	private String siteName;

    @JsonIgnore
    public String getSiteName(){
        return siteName;
    }

	/**
	 * Domain of the site, used for advertiser side blocking. For example,
	 * “foo.com”.
	 */
	@JsonProperty("domain")
    @Setter
	private String siteDomain;

    @JsonIgnore
    public String getSiteDomain(){
        return siteDomain;
    }

	/**
	 * Array of IAB content categories for the overall site. See Table 6.1
	 * Content Categories(open rtb 2.0)
	 */
	@JsonProperty("cat")
    @Setter
	private String[] contentCategoriesForSite;

    @JsonIgnore
    public String[] getContentCategoriesForSite(){
        return contentCategoriesForSite;
    }

	/**
	 * Array of IAB content categories for the current subsection of the
	 * site.See Table 6.1 Content Categories(open rtb 2.0)
	 */
	@JsonProperty("sectioncat")
    @Setter
	private String[] contentCategoriesSubsectionSite;

    @JsonIgnore
    public String[] getContentCategoriesSubsectionSite(){
        return contentCategoriesSubsectionSite;
    }

	/**
	 * Array of IAB content categories for the current page.See Table 6.1
	 * Content Categories(open rtb 2.0)
	 */
	@JsonProperty("pagecat")
    @Setter
	private String[] pageContentCategories;

    @JsonIgnore
    public String[] getPageContentCategories(){
        return pageContentCategories;
    }

	/**
	 * URL of the page where the impression will be shown.
	 */
	@JsonProperty("page")
    @Setter
	private String sitePageURL;

    @JsonIgnore
    public String getSitePageURL(){
        return sitePageURL;
    }

	/**
	 * Specifies whether the site has a privacy policy. “1” means there is a
	 * policy. “0” means there is not.
	 */
	@JsonProperty("privacypolicy")
    @Setter
	private Integer privacyPolicy;

    @JsonIgnore
    public Integer getPrivacyPolicy(){
        return privacyPolicy;
    }

	/**
	 * Referrer URL that caused navigation to the current page.
	 */
	@JsonProperty("ref")
    @Setter
	private String refererURL;

    @JsonIgnore
    public String getRefererURL(){
        return refererURL;
    }

	/**
	 * Search string that caused navigation to the current page.
	 */
	@JsonProperty("search")
    @Setter
	private String searchStringCausingNavigation;

    @JsonIgnore
    public String getSearchStringCausingNavigation(){
        return searchStringCausingNavigation;
    }

	/**
	 * See Publisher Object
	 */
    @JsonProperty("publisher")
    @Setter
    private BidRequestPublisherDTO bidRequestPublisher;

    @JsonIgnore
    public BidRequestPublisherDTO getBidRequestPublisher(){
        return bidRequestPublisher;
    }

	/**
	 * See Content Object
	 */
    @JsonProperty("content")
    @Setter
    private BidRequestContentDTO bidRequestContent;

    @JsonIgnore
    public BidRequestContentDTO getBidRequestContent(){
        return bidRequestContent;
    }

	/**
	 * List of keywords describing this site in a comma separated string.
	 */
	@JsonProperty("keywords")
    @Setter
	private String siteKeywordsCSV;

    @JsonIgnore
    public String getSiteKeywordsCSV(){
        return siteKeywordsCSV;
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