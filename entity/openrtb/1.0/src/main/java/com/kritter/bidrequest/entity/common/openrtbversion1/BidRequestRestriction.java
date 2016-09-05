package com.kritter.bidrequest.entity.common.openrtbversion1;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The “restrictions” object allows certain block lists to be passed on the bid
 * request. This technique is useful in several cases including a) when bidders
 * have not download these lists from the exchange offline, b) when block lists
 * are highly dynamic, or c) when passing critical blocked items to ensure they
 * are not missed in less frequent offline downloads. Per the latter case, if a
 * block list is both passed in this object and synchronized offline, bidders
 * must combine them via union (i.e., one does not supersede the other).
 * If there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestRestriction {

	/**
	 * Array of blocked content categories.
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
	@JsonProperty("bcat")
	private String[] blockedContentCategories;

	/**
	 * Array of blocked advertiser domains.
	 */
	@JsonProperty("badv")
	private String[] blockedAdvertiserDomains;

	public String[] getBlockedContentCategories() {
		return blockedContentCategories;
	}

	public void setBlockedContentCategories(String[] blockedContentCategories) {
		this.blockedContentCategories = blockedContentCategories;
	}

	public String[] getBlockedAdvertiserDomains() {
		return blockedAdvertiserDomains;
	}

	public void setBlockedAdvertiserDomains(String[] blockedAdvertiserDomains) {
		this.blockedAdvertiserDomains = blockedAdvertiserDomains;
	}
}
