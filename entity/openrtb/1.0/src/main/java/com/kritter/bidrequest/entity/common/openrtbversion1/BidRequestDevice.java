package com.kritter.bidrequest.entity.common.openrtbversion1;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 *The “device” object provides information pertaining to the mobile device
 * including its hardware, platform, location, and carrier.
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
 * if there are new properties added to json, the parsing wont break.
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
public class BidRequestDevice {

	/**
	 * SHA1 hashed device ID; IMEI when available, else MEID or ESN.
	 */
	@JsonProperty("did")
	private String deviceId;

	/**
	 * SHA1 hashed platform-specific ID (e.g., Android ID or UDID for iOS).
	 */
	@JsonProperty("dpid")
	private String devicePlatformId;

	/**
	 * IP address closest to device (typically a carrier IP).
	 */
	@JsonProperty("ip")
	private String ipAddress;

	/**
	 * Country derived from the IP address using ISO-3166-1 Alpha-3.
	 * http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
	 */
	@JsonProperty("country")
	private String country;

	/**
	 * Carrier or ISP derived from the IP address.
	 */
	@JsonProperty("carrier")
	private String carrier;

	/**
	 * Device user agent string.
	 */
	@JsonProperty("ua")
	private String userAgent;

	/**
	 * Device make (e.g., “Apple”).
	 */
	@JsonProperty("make")
	private String manufacturer;

	/**
	 * Device model (e.g., “iPhone”).
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * Device operating system (e.g., “iOS”).
	 */
	@JsonProperty("os")
	private String operatingSystem;

	/**
	 * Device operating system version (e.g., “3.1.2”).
	 */
	@JsonProperty("osv")
	private String operatingSystemVersion;

	/**
	 * “1” if the device supports JavaScript; else “0”.
	 */
	@JsonProperty("js")
	private String doesDeviceSupportJavascript;

	/**
	 * Lat/Long as “-999.99,-999.99” (i.e., south and west are negative).
	 */
	@JsonProperty("loc")
	private String locationLatLong;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDevicePlatformId() {
		return devicePlatformId;
	}

	public void setDevicePlatformId(String devicePlatformId) {
		this.devicePlatformId = devicePlatformId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getOperatingSystemVersion() {
		return operatingSystemVersion;
	}

	public void setOperatingSystemVersion(String operatingSystemVersion) {
		this.operatingSystemVersion = operatingSystemVersion;
	}

	public String getDoesDeviceSupportJavascript() {
		return doesDeviceSupportJavascript;
	}

	public void setDoesDeviceSupportJavascript(
			String doesDeviceSupportJavascript) {
		this.doesDeviceSupportJavascript = doesDeviceSupportJavascript;
	}

	public String getLocationLatLong() {
		return locationLatLong;
	}

	public void setLocationLatLong(String locationLatLong) {
		this.locationLatLong = locationLatLong;
	}

}
