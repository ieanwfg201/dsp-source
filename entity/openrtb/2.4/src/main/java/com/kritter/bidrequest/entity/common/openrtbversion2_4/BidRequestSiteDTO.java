package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
This object should be included if the ad supported content is a website as opposed to a non-browser application. 
A bid request must not contain both a Site and an App object. At a minimum, it is useful to provide a site ID or page URL, 
but this is not strictly required.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestSiteDTO {

	/**
	 * Exchange-specific site ID.
	 */
    @Setter@Getter
	private String id;

	/**
	 * Site name (may be aliased at the publisher’s request).
	 */
    @Setter@Getter
	private String name;

	/**
	 * Domain of the site (e.g., “mysite.foo.com”).
	 */
    @Setter@Getter
	private String domain;

	/**
	 * Array of IAB content categories of the site. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] cat;

	/**
	 * Array of IAB content categories that describe the current section of the site. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] sectioncat;

	/**
	 * Array of IAB content categories that describe the current page or view of the site. Refer to List 5.1
	 */
    @Setter@Getter
	private String[] pagecat;

	/**
	 * URL of the page where the impression will be shown.
	 */
    @Setter@Getter
	private String page;

	/**
	 * Referrer URL that caused navigation to the current page.
	 */
    @Setter@Getter
	private String ref;

	/**
	 * Search string that caused navigation to the current page.
	 */
    @Setter@Getter
	private String search;

    /**
     * Indicates if the site has been programmed to optimize layout when viewed on mobile devices, where 0 = no, 1 = yes.
     */
    @Setter@Getter
    private Integer mobile;

	/**
	 * Indicates if the site has a privacy policy, where 0 = no, 1 = yes.
	 */
    @Setter@Getter
	private Integer privacypolicy;

	/**
	 * See Publisher Object
	 */
    @Setter@Getter
    private BidRequestPublisherDTO publisher;

	/**
	 * See Content Object
	 */
    @Setter@Getter
    private BidRequestContentDTO content;

	/**
	 * Comma separated list of keywords about the site.
	 */
    @Setter@Getter
	private String keywords;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;
}