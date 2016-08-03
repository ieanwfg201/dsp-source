package com.kritter.device.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class contains capabilities of a handset.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HandsetCapabilities {

    /**
    @JsonProperty("is_wireless")
    @Getter @Setter
    private Boolean isDeviceWireless;
    **/

    @JsonProperty("is_tablet")
    @Getter @Setter
    private Boolean isTablet;

    /**
     * values include joystick, stylus, touchscreen, clickwheel, empty string.
     */
    /**
    @JsonProperty("pointing_method")
    @Getter @Setter
    private String pointingMethod;
    **/

    /**
     * Some devices come with a full qwerty keyboard. This may have a say on how
     * forms or other functions are implemented. Virtual keyboard (a-la Palm
     * pilot) are good enough to make this capability tick to true.
     */
    /**
    @JsonProperty("qwerty")
    @Getter @Setter
    private Boolean hasQwertyKeyboard;
    **/

    /**
     * Prefix to initiate a voice call,"none","tel:", "wtai://wp/mc;"
     */
    /**
    @JsonProperty("wml_call_prefix")
    @Getter @Setter
    private String wmlPrefixToMakeCall;
    **/

    /**
     * Prefix to initiate a voice call,"none","tel:", "wtai://wp/mc;"
     */
    /**
    @JsonProperty("chtml_call_prefix")
    @Getter @Setter
    private String chtmlPrefixToMakeCall;
    **/

    /**
     * Prefix to initiate a voice call,"none","tel:", "wtai://wp/mc;"
     */
    /**
    @JsonProperty("xhtml_call_prefix")
    @Getter @Setter
    private String xhtmlPrefixToMakeCall;
    **/

    /**
     * Indicates whether device supports the href="sms:+num" syntax to trigger
     * the SMS client from a link. Syntax may be "smsto:" on some devices or not
     * be supported at all. values: none, sms:,smsto:
     */
    /**
    @JsonProperty("sms_prefix")
    @Getter @Setter
    private String sendSmsString;
    **/

    /**
     * Indicates whether device supports the href="mms:+num" syntax to trigger
     * the MMS client from a link. Syntax may be "mmsto:" on some devices or not
     * be supported at all. values : none, mms:,mmsto:
     */
    /**
    @JsonProperty("mms_prefix")
    @Getter @Setter
    private String sendMmsString;
    **/

    /**
     * Indicates whether the browser honors the type="file" element in forms
     * (users can upload files on their devices to a remote server). On some
     * devices, users may need to copy/move the file from a given directory to a
     * directory visible to the web browser on the device file-system. values:
     * not_supported, supported, supported _user_intervention
     */
    /**
    @JsonProperty("can_upload_files")
    @Getter @Setter
    private Boolean canUploadFiles;
    **/
    /**
     * Indicates whether the browser supports cookies (please observe that the
     * cookie may be missing in case an operator strips it out. Similarly, a
     * device with no cookie support may automatically become cookie enabled if
     * a WAP gateway manages cookies on behalf of the device)
     */
    /**
    @JsonProperty("is_cookie_supported")
    @Getter @Setter
    private Boolean isCookieSupported;
    **/

    /**
     * Indicates whether the phone accepts a cookie set from a pixel in a page
     * of a different domain (assuming device with default settings)
     */
    /**
    @JsonProperty("tp_cookie_accepted")
    @Getter @Setter
    private Boolean isThirdPartyCookiesAccepted;
    **/

    /**
     * Some browsers support embedding of video through the <object> tag. For
     * ex: <object type="video/3gpp" data="rtsp://.../video.3gp" id="player"
     * width="176" height="150" autoplay="true"> </object> This capability will
     * track whether the XHTML browser supports this.
     *
     * none = Inline video playback/streaming not supported
     *
     * plain = Video will play
     *
     * play_and_stop = Video will play and user will have a chance to stop and
     * resume playback.
     */
    /**
    @JsonProperty("embed_video")
    @Getter @Setter
    private String embedVideo;
    **/

    /**
     * A device can be said Javascript enabled only if the following features
     * are reliably supported: alert, confirm, access form elements (dynamically
     * set/modify values), setTimeout, setInterval, document.location. If a
     * device fails one of these tests, mark as false (i.e. crippled javascript
     * is not enough to be marked as javascript-enabled)
     */
    /**
    @JsonProperty("js_enabled")
    @Getter @Setter
    private Boolean isJavascriptEnabled;
    **/

    /**
     * Preferred way to do geolocation through JavaScript
     *
     * values: none, gears, w3c_api
     */
    /**
    @JsonProperty("geo_api")
    @Getter @Setter
    private String preferredGeolocationApi;
    **/

    /**
     * The device features a built-in camera.
     */
    /**
    @JsonProperty("camera")
    @Getter @Setter
    private Boolean builtInCamera;
    **/

    /**
     * Device is a SmartTV (GoogleTV, Boxee Box, AppleTV, etc.).
     */
    /**
    @JsonProperty("is_smart_tv")
    @Getter @Setter
    private Boolean isDeviceSmartTv;
    **/

    /**
     * This field represents the screen width expressed in pixels
     */
    @JsonProperty("resolution_width")
    @Getter @Setter
    private Integer resolutionWidth;

    /**
     * This field represents the screen height expressed in pixels
     */
    @JsonProperty("resolution_height")
    @Getter @Setter
    private Integer resolutionHeight;

    /**
     * Width of the images viewable (usable) width expressed in pixels. This
     * capability refers to the image when used in "mobile mode", i.e. when the
     * page is served as XHTML MP, or it uses meta-tags such as "viewport",
     * "handheldfriendly", "mobileoptimised" to disable "web rendering" and
     * force a mobile user-experience (for example, iPhone 4 can render picture
     * 640 pixel wide, but normally, pages are served assuming a 320 pixel wide
     * screen).
     */
    /**
    @JsonProperty("max_image_width")
    @Getter @Setter
    private Integer maxImageWidth;
    **/

    /**
     * Height of the images viewable (usable) width expressed in pixels. This
     * capability refers to the image when used in "mobile mode" (see
     * explanation for max_image_height)
     */
    /**
    @JsonProperty("max_image_height")
    @Getter @Setter
    private Integer maxImageHeight;
    **/

    /**
     * Some devices may be flipped, i.e. user may change orientation,
     * effectively inverting screen_width and screen_height for mobile web
     * browsing and, possibly, for other functions.
     */
    /**
    @JsonProperty("dual_orientation")
    @Getter @Setter
    private Boolean dualOrientation;
    **/

    /**
     * The IMEI number is accessible.
     */
    /**
    @JsonProperty("phone_number_available")
    @Getter @Setter
    private Boolean isPhoneNumberAvailable;
    **/

    /**
     * Maximum bandwidth reachable by the device. Possible values:
     *
     * HSDPA = 1800 | 3600 | 7200 | 14400 depending on the device UMTS(3G) = 384
     * EGPRS/EDGE = 200 GPRS = 40 HSCSD = 29 CSD = 9
     *
     * value in Kilobits. one kilobit = 1000 bit
     */
    /**
    @JsonProperty("max_data_rate")
    @Getter @Setter
    private Integer maxDataRate;
    **/

    /**
     * Device can access WiFi connections
     */
    /**
    @JsonProperty("wifi")
    @Getter @Setter
    private Boolean canDeviceConnectToWifi;
    **/

    /**
     * true if the phone supports video streaming
     */
    /**
    @JsonProperty("video_streaming_supported")
    @Getter @Setter
    private Boolean videoStreamingSupported;
    **/

    /**
     * Transcoders hide real device information. This capability will be true if
     * a transcoder is detected and may be used to treat this request specially
     */
    /**
    @JsonProperty("is_transcoder")
    @Getter @Setter
    private Boolean isTranscoder;
    **/

    /**
     * Transcoders may be placing the original device UA string in a different
     * header. This capability contains the name of the header where the
     * original device UA string *may* be found.
     */
    /**
    @JsonProperty("transcoder_ua_header")
    @Getter @Setter
    private String transcoderUserAgentHeader;
    **/

    @JsonProperty("j2me_midp_1_0")
    @Getter @Setter
    private Boolean midp1;

    @JsonProperty("j2me_midp_2_0")
    @Getter @Setter
    private Boolean midp2;
}