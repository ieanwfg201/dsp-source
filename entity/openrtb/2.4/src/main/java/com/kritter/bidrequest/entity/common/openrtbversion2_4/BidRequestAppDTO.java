package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
This object should be included if the ad supported content is a non-browser application 
(typically in mobile) as opposed to a website. A bid request must not contain both an App and a Site object. 
At a minimum, it is useful to provide an App ID or bundle, but this is not strictly required.

 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestAppDTO {

	/**
	 * Application ID on the exchange.
	 */
    @Setter@Getter
	private String id;

    /**
	 * App name (may be aliased at the publisher’s request).
	 */
    @Setter@Getter
	private String name;

	/**
	 * A platform-specific application identifier intended to be unique to the app and independent of the exchange. 
	 * On Android, this should be a bundle or package name (e.g., com.foo.mygame). On iOS, it is a numeric ID.
	 */
    @Setter@Getter
	private String bundle;

    /**
	 * Domain of the app (e.g., “mygame.foo.com”).
	 */
    @Setter@Getter
	private String domain;

    /**
     * App store URL for an installed app; for IQG 2.1 compliance
     */
    @Setter@Getter
    private String storeurl;

	/**
	 * Array of IAB content categories of the app. Refer to List 5.1
	 */
    @Setter@Getter
	private String[] cat;

    /**
	 * Array of IAB content categories that describe the current section of the app. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] sectioncat;

    /**
	 * Array of IAB content categories that describe the current page or view of the app. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] pagecat;

	/**
	 * Application version.
	 */
    @Setter@Getter
	private String ver;

	/**
	 * Indicates if the app has a privacy policy, where 0 = no, 1 = yes.
	 */
    @Setter@Getter
	private Integer privacypolicy;

	/**
	 * 0 = app is free, 1 = the app is a paid version.
	 */
    @Setter@Getter
	private Integer paid;

    /**
	 * Details about the Publisher (Section 3.2.10) of the app.
	 */
    @Setter@Getter
	private BidRequestPublisherDTO publisher;

	/**
	 * Details about the Content (Section 3.2.11) within the app.
	 */
    @Setter@Getter
	private BidRequestContentDTO content;

	/**
	 * Comma separated list of keywords about the app.
	 */
    @Setter@Getter
	private String keywords;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;
}
