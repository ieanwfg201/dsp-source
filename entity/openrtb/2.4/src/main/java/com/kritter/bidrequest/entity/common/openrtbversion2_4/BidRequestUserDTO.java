package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *This object contains information known or derived about the human user of the device 
 *(i.e., the audience for advertising). The user id is an exchange artifact and may be subject to rotation or 
 *other privacy policies. However, this user ID must be stable long enough to serve reasonably as the basis for 
 *frequency capping and retargeting.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestUserDTO {

	/**
	 * Exchange-specific ID for the user. At least one of id or buyeruid is recommended.
	 */
    @Setter@Getter
	private String id;

	/**
	 * Buyer-specific ID for the user as mapped by the exchange for the buyer. At least one of buyeruid or id is recommended.
	 */
    @Setter@Getter
	private String buyeruid;

	/**
	 * Year of birth as a 4-digit integer.
	 */
    @Setter@Getter
	private Integer yob;

	/**
	 * Gender, where “M” = male, “F” = female, “O” = known to be other (i.e., omitted is unknown).
	 */
    @Setter@Getter
	private String gender;

	/**
	 * Comma separated list of keywords, interests, or intent.
	 */
    @Setter@Getter
	private String keywords;

	/**
	 * Optional feature to pass bidder data that was set in the exchange’s cookie. The string must be in base85 
	 * cookie safe characters and be in any format. Proper JSON encoding must be used to include “escaped” 
	 * quotation marks.
	 */
    @Setter@Getter
	private String customdata;

	/**
	 * Location of the user’s home base defined by a Geo object (Section 3.2.14). This is not necessarily their 
	 * current location
	 */
    @Setter@Getter
	private BidRequestGeoDTO geo;

	/**
	 * See Data Object
	 */
    @Setter@Getter
    private BidRequestData[] data;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;

}