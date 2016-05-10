package com.kritter.bidrequest.entity.common.openrtb2_0;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * The “device” object provides information pertaining to the device including
 * its hardware, platform, location, and carrier. This device can refer to a
 * mobile handset, a desktop computer, set top box or other digital device. The
 * device object itself and all of its parameters are optional, so default
 * values are not provided. If an optional parameter is not specified, it should
 * be considered unknown. In general, the most essential fields are either the
 * IP address (to enable geo-lookup for the bidder), or providing geo
 * information directly in the geo object.
 * 
 * 
 * BEST PRACTICE: There are currently no prominent open source lists for device
 * makes, models, operating systems, or carriers. Exchanges typically use
 * commercial products or other proprietary lists for these attributes. Until
 * suitable open standards are available, exchanges are highly encouraged to
 * publish lists of their device make, model, operating system, and carrier
 * values to bidders.
 * 
 * BEST PRACTICE: Proper device IP detection in mobile is not straightforward.
 * Typically it involves starting at the left of the x-forwarded-for header,
 * skipping private carrier networks (e.g., 10.x.x.x or 192.x.x.x), and possibly
 * scanning for known carrier IP ranges. Exchanges are urged to research and
 * implement this feature carefully when presenting device IP values to bidders.
 * 
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestDeviceDTO {

	/**
	 * If “0”, then do not track Is set to false, if “1”, then do no track is
	 * set to true in browser. This case applies where OS level id is not passed
	 * but some exchange specific or any other id is passed as user do not wish
	 * to allow sharing udid or odin1 ,etc.
	 */
	@JsonProperty("dnt")
    @Setter
	private Integer doNotTrackDevice;

    @JsonIgnore
    public Integer getDoNotTrackDevice(){
        return doNotTrackDevice;
    }

	/**
	 * Browser user agent string.
	 */
	@JsonProperty("ua")
    @Setter
	private String deviceUserAgent;

    @JsonIgnore
    public String getDeviceUserAgent(){
        return deviceUserAgent;
    }

	/**
	 * IPv4 address closest to device.
	 */
	@JsonProperty("ip")
    @Setter
	private String ipV4AddressClosestToDevice;

    @JsonIgnore
    public String getIpV4AddressClosestToDevice(){
        return ipV4AddressClosestToDevice;
    }

	/**
	 * Geography as derived from the device’s location services (e.g., cell
	 * tower triangulation, GPS) or IP address. See Geo Object.
	 */
    @JsonProperty("geo")
    @Setter
    private BidRequestGeoDTO geoObject;

    @JsonIgnore
    public BidRequestGeoDTO getGeoObject(){
        return geoObject;
    }

	/**
	 * SHA1 hashed device ID; IMEI when available, else MEID or ESN. OpenRTB’s
	 * preferred method for device ID hashing is SHA1.
	 */
    @JsonProperty("didsha1")
    @Setter
	private String SHA1HashedDeviceId;

    @JsonIgnore
    public String getSHA1HashedDeviceId(){
        return SHA1HashedDeviceId;
    }
	/**
	 * MD5 hashed device ID; IMEI when available, else MEID or ESN. Should be
	 * interpreted as case insensitive.
	 */
	@JsonProperty("didmd5")
    @Setter
    private String MD5HashedDeviceId;

    @JsonIgnore
    public String getMD5HashedDeviceId(){
        return MD5HashedDeviceId;
    }

	/**
	 * SHA1 hashed platform-specific ID (e.g., Android ID or UDID for iOS).
	 * OpenRTB’s preferred method for device ID hash is SHA1.
	 */
	@JsonProperty("dpidsha1")
    @Setter
	private String SHA1HashedDevicePlatformId;

    @JsonIgnore
    public String getSHA1HashedDevicePlatformId(){
        return SHA1HashedDevicePlatformId;
    }

	/**
	 * MD5 hashed platform-specific ID (e.g., Android ID or UDID for iOS).
	 * Should be interpreted as case insensitive.
	 */
	@JsonProperty("dpidmd5")
    @Setter
	private String MD5HashedDevicePlatformId;

    @JsonIgnore
    public String getMD5HashedDevicePlatformId(){
        return MD5HashedDevicePlatformId;
    }

	/**
	 * IP address in IPv6.
	 */
	@JsonProperty("ipv6")
    @Setter
	private String ipV6Address;

    @JsonIgnore
    public String getIpV6Address(){
        return ipV6Address;
    }

	/**
	 * Carrier or ISP derived from the IP address. Should be specified using
	 * Mobile Network Code (MNC)
	 * 
	 * http://en.wikipedia.org/wiki/Mobile_Network_ Code
	 */
    @JsonProperty("carrier")
    @Setter
	private String carrier_MNC_Code;

    @JsonIgnore
    public String getCarrier_MNC_Code(){
        return carrier_MNC_Code;
    }

	/**
	 * Browser language; use alpha-2/ISO 639-1 codes.
	 * 
	 * http://en.wikipedia.org/wiki/ISO_639-1
	 */
	@JsonProperty("language")
    @Setter
	private String browserLanguage;

    @JsonIgnore
    public String getBrowserLanguage(){
        return browserLanguage;
    }

	/**
	 * Device make (e.g., “Apple”).
	 */
	@JsonProperty("make")
    @Setter
	private String deviceManufacturer;

    @JsonIgnore
    public String getDeviceManufacturer(){
        return deviceManufacturer;
    }

	/**
	 * Device model (e.g., “iPhone”).
	 */
	@JsonProperty("model")
    @Setter
	private String deviceModel;

    @JsonIgnore
    public String getDeviceModel(){
        return deviceModel;
    }

	/**
	 * Device operating system (e.g., “iOS”).
	 */
	@JsonProperty("os")
    @Setter
	private String deviceOperatingSystem;

    @JsonIgnore
    public String getDeviceOperatingSystem(){
        return deviceOperatingSystem;
    }

	/**
	 * Device operating system version (e.g., “3.1.2”).
	 */
	@JsonProperty("osv")
    @Setter
	private String deviceOperatingSystemVersion;

    @JsonIgnore
    public String getDeviceOperatingSystemVersion(){
        return deviceOperatingSystemVersion;
    }

	/**
	 * “1” if the device supports JavaScript; else “0”.
	 */
	@JsonProperty("js")
    @Setter
	private Integer doesDeviceSupportJavascript;

    @JsonIgnore
    public Integer getDoesDeviceSupportJavascript(){
        return doesDeviceSupportJavascript;
    }

	/**
	 * Return the detected data connection type for the device.
	 * 
	 * 0 Unknown
	 * 
	 * 1 Ethernet
	 * 
	 * 2 Wifi
	 * 
	 * 3 Cellular data – Unknown Generation
	 * 
	 * 4 Cellular data – 2G
	 * 
	 * 5 Cellular data – 3G
	 * 
	 * 6 Cellular data – 4G
	 */
	@JsonProperty("connectiontype")
    @Setter
	private Integer connectionType;

    @JsonIgnore
    public Integer getConnectionType(){
        return connectionType;
    }

	/**
	 * Return the device type being used.
	 * 
	 * 1 Mobile/Tablet
	 * 
	 * 2 Personal Computer
	 * 
	 * 3 Connected TV
	 */
	@JsonProperty("devicetype")
    @Setter
	private Integer deviceType;

    @JsonIgnore
    public Integer getDeviceType(){
        return deviceType;
    }

	/**
	 * Return the Flash version detected.
	 */
	@JsonProperty("flashver")
    @Setter
	private String flashVersionDetected;

    @JsonIgnore
    public String getFlashVersionDetected(){
        return flashVersionDetected;
    }
}