package com.kritter.bidrequest.entity.common.openrtb2_0;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The geo object itself and all of its parameters are optional, so default
 * values are not provided. If an optional parameter is not specified, it should
 * be considered unknown. Note that the Geo Object may appear in one or both the
 * Device Object and the User Object. This is intentional, since the information
 * may be derived from either a device-oriented source (such as IP geo lookup),
 * or by user registration information (for example provided to a publisher
 * through a user registration). If the information is in conflict, it’s up to
 * the bidder to determine which information to use.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestGeoDTO {

	/**
	 * Latitude from -90 to 90. South is negative. This should only be passed if
	 * known to be accurate (For example, not the centroid of a postal code).
	 */
	@JsonProperty("lat")
    @Setter
	private Float geoLatitude;

    @JsonIgnore
    public Float getGeoLatitude(){
        return geoLatitude;
    }

	/**
	 * Longitude from -180 to 180. West is negative. This should only be passed
	 * if known to be accurate.
	 */
	@JsonProperty("lon")
    @Setter
	private Float geoLongitude;

    @JsonIgnore
    public Float getGeoLongitude(){
        return geoLongitude;
    }

	/**
	 * Country using ISO-3166-1 Alpha-3.
	 * 
	 * http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
	 */
	@JsonProperty("country")
    @Setter
	private String country;

    @JsonIgnore
    public String getCountry(){
        return country;
    }

	/**
	 * Region using ISO 3166-2
	 * 
	 * http://en.wikipedia.org/wiki/ISO_3166-2
	 */
	@JsonProperty("region")
    @Setter
	private String region;

    @JsonIgnore
    public String getRegion(){
        return region;
    }

	/**
	 * Region of a country using fips 10-4 notation (alternative to ISO 3166-2)
	 * 
	 * http://en.wikipedia.org/wiki/FIPS_10-4
	 */
	@JsonProperty("regionfips104")
    @Setter
	private String regionFIPS104;

    @JsonIgnore
    public String getRegionFIPS104(){
        return regionFIPS104;
    }

	/**
	 * Pass the metro code (see
	 * 
	 * http://code.google.com/apis/adwords/docs/appe ndix/metrocodes.html
	 * 
	 * ). Metro codes are similar to but not exactly the same as Nielsen DMAs.
	 */
	@JsonProperty("metro")
    @Setter
	private String metro;

    @JsonIgnore
    public String getMetro(){
        return metro;
    }

	/**
	 * City using United Nations Code for Trade and Transport Locations
	 * 
	 * http://www.unece.org/cefact/locode/service/loc ation.htm
	 */
	@JsonProperty("city")
    @Setter
	private String city;

    @JsonIgnore
    public String getCity(){
        return city;
    }

	/**
	 *Zip/postal code
	 */
	@JsonProperty("zip")
    @Setter
	private String zipCode;

    @JsonIgnore
    public String getZipCode(){
        return zipCode;
    }

	/**
	 * Indicate the source of the geo data (GPS, IP address, user provided). See
	 * Table 6.15 Location Type for a list of potential values. Type should be
	 * provided when lat/lon is provided.
	 * 
	 * 1 GPS/Location Services
	 * 
	 * 2 IP Address
	 * 
	 * 3 User provided (e.g., registration data)
	 */
	@JsonProperty("type")
    @Setter
	private Integer locationType;

    @JsonIgnore
    public Integer getLocationType(){
        return locationType;
    }
}