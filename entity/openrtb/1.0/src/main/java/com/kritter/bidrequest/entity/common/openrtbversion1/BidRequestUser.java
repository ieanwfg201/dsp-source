package com.kritter.bidrequest.entity.common.openrtbversion1;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The “user” object contains information known or derived about the human user
 * of the device. Note that the user ID is an exchange artifact (refer to the
 * “device” object for hardware or platform derived IDs) and may be subject to
 * rotation policies. However, this user ID must be stable long enough to serve
 * reasonably as the basis for frequency capping.
 * If there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestUser {

	/**
	 * Unique consumer ID of this user on the exchange.
	 */
	@JsonProperty("uid")
	private String userIdOnExchange;

	/**
	 * Year of birth as a 4-digit integer.
	 */
	@JsonProperty("yob")
	private Integer yearOfBirth;

	/**
	 * Gender as “M” male, “F” female, “O” other.
	 */
	@JsonProperty("gender")
	private String gender;

	/**
	 * Home zip code if USA; else postal code.
	 */
	@JsonProperty("zip")
	private String zipCode;

	/**
	 * Home country; using ISO-3166-1 Alpha-3.
	 */
	@JsonProperty("country")
	private String homeCountry;

	/**
	 * Comma separated list of keywords of consumer interests or intent.
	 */
	@JsonProperty("keywords")
	private String consumerInterestsKeywordCSV;

	public String getUserIdOnExchange() {
		return userIdOnExchange;
	}

	public void setUserIdOnExchange(String userIdOnExchange) {
		this.userIdOnExchange = userIdOnExchange;
	}

	public Integer getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getHomeCountry() {
		return homeCountry;
	}

	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
	}

	public String getConsumerInterestsKeywordCSV() {
		return consumerInterestsKeywordCSV;
	}

	public void setConsumerInterestsKeywordCSV(
			String consumerInterestsKeywordCSV) {
		this.consumerInterestsKeywordCSV = consumerInterestsKeywordCSV;
	}
}
