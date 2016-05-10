package com.kritter.bidrequest.entity.common.openrtbversion2_3;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * An “app” object should be included if the ad supported content is part of a
 * mobile application (as opposed to a mobile website). A bid request must not
 * contain both an “app” object and a “site” object. The app object itself and
 * all of its parameters are optional, so default values are not provided. If an
 * optional parameter is not specified, it should be considered unknown. . At a
 * minimum, it’s useful to provide an App ID or bundle, but this is not strictly
 * required.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestAppDTO {

	/**
	 * Application ID on the exchange.
	 */
	@JsonProperty("id")
    @Setter
	private String applicationIdOnExchange;

    @JsonIgnore
    public String getApplicationIdOnExchange(){
        return applicationIdOnExchange;
    }

    /**
	 * Application name (may be masked at publisher’s request).
	 */
	@JsonProperty("name")
    @Setter
	private String applicationName;

    @JsonIgnore
    public String getApplicationName(){
        return applicationName;
    }

    /**
	 * Domain of the application (e.g., “mygame.foo.com”).
	 */
    @JsonProperty("domain")
    @Setter
	private String applicationDomain;

    @JsonIgnore
    public String getApplicationDomain(){
        return applicationDomain;
    }

	/**
	 * Array of IAB content categories for the overall application. See Table
	 * 6.1 Content Categories.
	 */
	@JsonProperty("cat")
    @Setter
	private String[] contentCategoriesApplication;

    @JsonIgnore
    public String[] getContentCategoriesApplication(){
        return contentCategoriesApplication;
    }

    /**
	 * Array of IAB content categories for the current subsection of the app.
	 * See Table 6.1 Content Categories.
	 */
	@JsonProperty("sectioncat")
    @Setter
	private String[] contentCategoriesSubSectionApplication;

    @JsonIgnore
    public String[] getContentCategoriesSubSectionApplication(){
        return contentCategoriesSubSectionApplication;
    }

    /**
	 * Array of IAB content categories for the current page/view of the app. See
	 * Table 6.1 Content Categories.
	 */
    @JsonProperty("pagecat")
    @Setter
	private String[] currentPageContentCategoriesApplication;

    @JsonIgnore
    public String[] getCurrentPageContentCategoriesApplication(){
        return currentPageContentCategoriesApplication;
    }

	/**
	 * Application version.
	 */
	@JsonProperty("ver")
    @Setter
	private String applicationVersion;

    @JsonIgnore
    public String getApplicationVersion(){
        return applicationVersion;
    }

	/**
	 * Application bundle or package name (e.g., com.foo.mygame). This is
	 * intended to be a unique ID across multiple exchanges.
	 */
	@JsonProperty("bundle")
    @Setter
	private String applicationBundleName;

    @JsonIgnore
    public String getApplicationBundleName(){
        return applicationBundleName;
    }

	/**
	 * Specifies whether the app has a privacy policy. “1” means there is a
	 * policy and “0” means there is not.
	 */
	@JsonProperty("privacypolicy")
    @Setter
	private Integer privacyPolicy;

    @JsonIgnore
    public Integer getPrivacyPolicy(){
        return privacyPolicy;
    }

	/**
	 * “1” if the application is a paid version; else “0” (i.e., free).
	 */
	@JsonProperty("paid")
    @Setter
	private Integer isApplicationPaidVersion;

    @JsonIgnore
	public Integer getIsApplicationPaidVersion(){
        return isApplicationPaidVersion;
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
	 * List of keywords describing this app in a comma separated string
	 */
	@JsonProperty("keywords")
    @Setter
	private String appKeywordsCSV;

    @JsonIgnore
    public String getAppKeywordsCSV(){
        return appKeywordsCSV;
    }

    /**
     * For QAG 1.5 compliance, an app store URL for
     * an installed app should be passed in the bid request.
     */
    @JsonProperty("storeurl")
    @Setter
    private String applicationStoreUrl;

    @JsonIgnore
    public String getApplicationStoreUrl(){
        return applicationStoreUrl;
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
