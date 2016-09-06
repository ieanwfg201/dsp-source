package com.kritter.bidrequest.entity.common.openrtb2_0;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The “user” object contains information known or derived about the human user
 * of the device. Note that the user ID is an exchange artifact (refer to the
 * “device” object for hardware or platform derived IDs) and may be subject to
 * rotation policies. However, this user ID must be stable long enough to serve
 * reasonably as the basis for frequency capping.
 * 
 * The user object itself and all of its parameters are optional, so default
 * values are not provided. If an optional parameter is not specified, it should
 * be considered unknown.
 * 
 * <b>If device ID is used as a proxy for unique user ID, use the device
 * object.</b>
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestUserDTO {

	/**
	 * Unique consumer ID of this user on the exchange.
	 */
	@JsonProperty("id")
    @Setter
	private String uniqueConsumerIdOnExchange;

    @JsonIgnore
    public String getUniqueConsumerIdOnExchange(){
        return uniqueConsumerIdOnExchange;
    }

	/**
	 * Buyer’s user ID for this user as mapped by exchange for the buyer.
	 */
	@JsonProperty("buyeruid")
    @Setter
	private String uniqueConsumerIdMappedForBuyer;

    @JsonIgnore
    public String getUniqueConsumerIdMappedForBuyer(){
        return uniqueConsumerIdMappedForBuyer;
    }

	/**
	 * Year of birth as a 4-digit integer.
	 */
	@JsonProperty("yob")
    @Setter
	private Integer yearOfBirth;

    @JsonIgnore
    public Integer getYearOfBirth(){
        return yearOfBirth;
    }

	/**
	 * Gender as “M” male, “F” female, “O” Other. (Null indicates unknown)
	 */
	@JsonProperty("gender")
    @Setter
	private String gender;

    @JsonIgnore
    public String getGender(){
        return gender;
    }

	/**
	 * Comma separated list of keywords of consumer interests or intent.
	 */
	@JsonProperty("keywords")
    @Setter
	private String consumerInterestsCSV;

    @JsonIgnore
    public String getConsumerInterestsCSV(){
        return consumerInterestsCSV;
    }

	/**
	 * If supported by the exchange, this is custom data that the bidder had
	 * stored in the exchange’s cookie. The string may be in base85 cookie safe
	 * characters, and be in any format. This may useful for storing user
	 * features. Note: Proper JSON encoding must be used to include “escaped”
	 * quotation marks.
	 */
	@JsonProperty("customdata")
    @Setter
	private String consumerCustomData;

    @JsonIgnore
    public String getConsumerCustomData(){
        return consumerCustomData;
    }

	/**
	 * If supported by the exchange, this is custom data that the bidder had
	 * stored in the exchange’s cookie. The string may be in base85 cookie safe
	 * characters, and be in any format. This may useful for storing user
	 * features. Note: Proper JSON encoding must be used to include “escaped”
	 * quotation marks.
	 */
    @JsonProperty("geo")
    @Setter
	private BidRequestGeoDTO bidRequestGeo;

    @JsonIgnore
    public BidRequestGeoDTO getBidRequestGeo(){
        return bidRequestGeo;
    }

	/**
	 * See Data Object
	 */
    @JsonProperty("data")
    @Setter
    private BidRequestUserDataDTO[] bidRequestUserData;

    @JsonIgnore
    public BidRequestUserDataDTO[] getBidRequestUserData(){
        return bidRequestUserData;
    }
}