package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
 *This object provides information pertaining to the device through which the user is interacting. 
 *Device information includes its hardware, platform, location, and carrier data. The device can refer to a
 * mobile handset, a desktop computer, set top box, or other digital device.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestDeviceDTO {

	/**
	 * Browser user agent string.
	 */
    @Setter@Getter
	private String ua;
	/**
	 * Location of the device assumed to be the user’s current location defined by a Geo object (Section 3.2.14).
	 */
    @Setter@Getter
    private BidRequestGeoDTO geo;

	/**
	 * Standard “Do Not Track” flag as set in the header by the browser, where 0 = tracking is unrestricted, 
	 * 1 = do not track
	 */
    @Setter@Getter
	private Integer dnt;

    /**
     * “Limit Ad Tracking” signal commercially endorsed (e.g., iOS, Android), where 0 = tracking is unrestricted, 
     * 1 = tracking must be limited per commercial guidelines.
     */
    @Setter@Getter
    private Integer lmt;

	/**
	 * IPv4 address closest to device.
	 */
    @Setter@Getter
	private String ip;

    /**
	 * IP address closest to device as IPv6.
	 */
    @Setter@Getter
	private String ipv6;

	/**
	 * The general type of device. Refer to List 5.19.
	 */
    @Setter@Getter
	private Integer devicetype;

	/**
	 * Device make (e.g., “Apple”).
	 */
    @Setter@Getter
	private String make;

	/**
	 * Device model (e.g., “iPhone”).
	 */
    @Setter@Getter
	private String model;

	/**
	 * Device operating system (e.g., “iOS”).
	 */
    @Setter@Getter
	private String os;

	/**
	 * Device operating system version (e.g., “3.1.2”).
	 */
    @Setter@Getter
	private String osv;

    /**
     * Hardware version of the device (e.g., “5S” for iPhone 5S).
     */
    @Setter@Getter
    private String hwv;

    /**Physical height of the screen in pixels.*/
    @Setter@Getter
    private Integer h;

    /**Physical width of the screen in pixels.*/
    @Setter@Getter
    private Integer w;

    /**Screen size as pixels per linear inch.*/
    @Setter@Getter
    private Integer ppi;

    /**The ratio of physical pixels to device independent pixels.*/
    @Setter@Getter
    private Float pxratio;

    /**
	 * Support for JavaScript, where 0 = no, 1 = yes.
	 */
    @Setter@Getter
	private Integer js;

    /**
	 * Indicates if the geolocation API will be available to JavaScript code running in the banner, where 0 = no, 1 = yes.
	 */
    @Setter@Getter
	private Integer geofetch;
    
	/**
	 * Version of Flash supported by the browser.
	 */
    @Setter@Getter
	private String flashver;

	/**
	 * Browser language using ISO-639-1-alpha-2.
	 */
    @Setter@Getter
	private String language;

	/**
	 * Carrier or ISP (e.g., “VERIZON”). “WIFI” is often used in mobile to indicate high bandwidth
	 *  (e.g., video friendly vs. cellular).
	 */
    @Setter@Getter
	private String carrier;

	/**
	 * Network connection type. Refer to List 5.20.
	 */
    @Setter@Getter
	private Integer connectiontype;

    /**ID sanctioned for advertiser use in the clear (i.e., not hashed).*/
    @Setter@Getter
    private String ifa;

	/**
	 * Hardware device ID (e.g., IMEI); hashed via SHA1.
	 */
    @Setter@Getter
	private String didsha1;

	/**
	 * Hardware device ID (e.g., IMEI); hashed via MD5.
	 */
    @Setter@Getter
    private String didmd5;

	/**
	 * Platform device ID (e.g., Android ID); hashed via SHA1.
	 */
    @Setter@Getter
	private String dpidsha1;

	/**
	 * Platform device ID (e.g., Android ID); hashed via MD5.
	 */
    @Setter@Getter
	private String dpidmd5;

    /**MAC address of the device; hashed via SHA1*/
    @Setter@Getter
    private String macsha1;

    /**MAC address of the device; hashed via MD5*/
    @Setter@Getter
    private String macmd5;

    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;


}