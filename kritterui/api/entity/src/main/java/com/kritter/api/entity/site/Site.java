package com.kritter.api.entity.site;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.HygieneCategory;
import com.kritter.constants.Payout;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.AdPositionsOpenRTB;
import com.kritter.constants.VideoLinearity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class Site {
    /** mandatory id (auto populated at the time of creation )*/
    private int id = -1;
    /** mandatory guid (auto populated at the time of creation )*/
    private String guid = null;
    /** mandatory */
    private String name = null;
    /** mandatory id of account  @see com.kritter.api.entity.account.Account */
    private int pub_id = -1;
    /** mandatory guid of account  @see com.kritter.api.entity.account.Account */
    private String pub_guid = null;
    /** mandatory when site_platform_id is choses as WAP */
    private String site_url = null;
    /** mandatory when site_platform_id is choses as APP */
    private String app_id = null;
    /** mandatory when site_platform_id is choses as APP @see com.kritter.constants.APP_STORE_ID */
    private int app_store_id = -1; 
    /** optional json array populated with tier 1 category id  */
    private String categories_tier_1_list = "[]";
    /** optional json array populated with tier 2 category id  */
    private String categories_tier_2_list = "[]";
    /** optional json array populated with tier 1 category id  for inclusion/ exclusion  */
    private String category_list_inc_exc_tier_1 = "[]";
    /** optional json array populated with tier 2 category id  for inclusion/ exclusion  */
    private String category_list_inc_exc_tier_2 = "[]";
    /** optional true if categories are to be excluded  */
    private boolean is_category_list_excluded = true;
    /** mandatory @see com.kritter.constants.HygieneCategory  */
    private String hygiene_list = ""+HygieneCategory.FAMILY_SAFE.getCode();
    /** optional @see com.kritter.constants.HygieneCategory  specifies hygiene which sites wants to opt in */
    private String opt_in_hygiene_list = null;
    /** optional json array of creative attributes id*/
    private String creative_attr_inc_exc = null;
    /** optional true if creative attributes are to be excluded  */
    private boolean is_creative_attr_exc = true;
    /** mandatory @see com.kritter.constants.SITE_PLATFORM */
    private SITE_PLATFORM site_platform_id = null;
    /** mandatory @see com.kritter.constants.StatusIdEnum */
    private int status_id = 5;
    /** mandatory auto populated */
    private long created_on = 0;
    /** mandatory auto populated */
    private long last_modified = 0;
    /** mandatory is of account which modifes this entity */
    private int modified_by = -1;
    /** mandatory @see com.kritter.constants.Payout */
    private String billing_rules_json = Payout.default_payout_percent_str;
    /**optional comma seperated urls for exclusion*/
    private String url_exclusion = null;
    /**optional whether house ads are allowed*/
    private boolean allow_house_ads = true;
    /** optional */
    private String comments = null;
    /** optional - 0 to 100 */
    private double floor = 0.0;
    /** flag specifying whether advertiser is excluded */
    private boolean is_advertiser_excluded = true;
    /** Optional json array of advertiser id  */
    private String advertiser_json_array = "[]";
    /** Optional json array of advertiserid|campaignid  */
    private String campaign_json_array = "[]";
    /** placeholder used internally */
    private String campaign_inc_exc_schema = "[]";
    /** optional @see com.kritter.constants.SITE_PASSBACK_TYPE */
    private int passback_type = -1;
    /** optional temporary placeholder @see com.kritter.constants.SITE_PASSBACK_CONTENT_TYPE */
    private int passback_content_type = -1;
    /** optional populates when passback_type is populated */
    private String nofill_backup_content ="";
    /** optional allow richmedia in direct sites and aggreagtors*/
    private boolean is_richmedia_allowed = false;
    private boolean is_native = false;
    @Getter@Setter
    private Integer native_layout;
    @Getter@Setter
    private String native_title_keyname;
    @Getter@Setter
    private Integer native_title_maxchars;
    @Getter@Setter
    private String native_screenshot_keyname;
    @Getter@Setter
    private Integer native_screenshot_imagesize;
    @Getter@Setter
    private String native_landingurl_keyname;
    @Getter@Setter
    private String native_call_to_action_keyname;
    @Getter@Setter
    private String native_icon_keyname;
    @Getter@Setter
    private Integer native_icon_imagesize;
    @Getter@Setter
    private String native_description_keyname;
    @Getter@Setter
    private Integer native_description_maxchars;
    @Getter@Setter
    private String native_rating_count_keyname;
    /**Video Properties Begin*/
    private boolean is_video = false;
    /** mimes format [1,2] - com.kritter.constants.VideoMimeTypes*/
    @Getter@Setter
    private String strmimes; 
    /** protocol format [1,2] - com.kritter.constants.VideoBidResponseProtocols*/
    @Getter@Setter
    private String strprotocols;
	/**com.kritter.constants.VideoLinearity*/
	@Getter@Setter
	private Integer linearity = VideoLinearity.LINEAR_IN_STREAM.getCode();
	/**com.kritter.constants.VideoStartDelay*/
	@Getter@Setter
	private Integer startDelay;
	/**format [1,2] -com.kritter.constants.VideoPlaybackMethods*/
	@Getter@Setter
	private String strplaybackmethod;
    @Getter@Setter
    private Integer minDurationSec; 
    @Getter@Setter
    private Integer maxDurationSec; 
	@Getter@Setter
	private Integer widthPixel;
	@Getter@Setter
	private Integer heightPixel;
	/**format [1,2] com.kritter.constants.ContentDeliveryMethods*/
	@Getter@Setter
	private String strdelivery;
	/**com.kritter.constants.AdPositionsOpenRTB*/
	@Getter@Setter
	private Integer pos = AdPositionsOpenRTB.Unknown.getCode();
	/**format [1,2] - com.kritter.constants.APIFrameworks*/
	@Getter@Setter
	private String strapi;
	/**format [1,2] - com.kritter.constants.VASTCompanionTypes*/
	@Getter@Setter
	private String strcompaniontype;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
      
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPub_id() {
        return pub_id;
    }
    public void setPub_id(int pub_id) {
        this.pub_id = pub_id;
    }
    public String getPub_guid() {
        return pub_guid;
    }
    public void setPub_guid(String pub_guid) {
        this.pub_guid = pub_guid;
    }
    public String getSite_url() {
        return site_url;
    }
    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }
    public String getApp_id() {
        return app_id;
    }
    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }
    public int getApp_store_id() {
        return app_store_id;
    }
    public void setApp_store_id(int app_store_id) {
        this.app_store_id = app_store_id;
    }
    public boolean isIs_category_list_excluded() {
        return is_category_list_excluded;
    }
    public void setIs_category_list_excluded(boolean is_category_list_excluded) {
        this.is_category_list_excluded = is_category_list_excluded;
    }
    public String getHygiene_list() {
        return hygiene_list;
    }
    public void setHygiene_list(String hygiene_list) {
        this.hygiene_list = hygiene_list;
    }
    public String getOpt_in_hygiene_list() {
        return opt_in_hygiene_list;
    }
    public void setOpt_in_hygiene_list(String opt_in_hygiene_list) {
        this.opt_in_hygiene_list = opt_in_hygiene_list;
    }
    public String getCreative_attr_inc_exc() {
        return creative_attr_inc_exc;
    }
    public void setCreative_attr_inc_exc(String creative_attr_inc_exc) {
        this.creative_attr_inc_exc = creative_attr_inc_exc;
    }
    public boolean isIs_creative_attr_exc() {
        return is_creative_attr_exc;
    }
    public void setIs_creative_attr_exc(boolean is_creative_attr_exc) {
        this.is_creative_attr_exc = is_creative_attr_exc;
    }
    public SITE_PLATFORM getSite_platform_id() {
        return site_platform_id;
    }
    public void setSite_platform_id(SITE_PLATFORM site_platform_id) {
        this.site_platform_id = site_platform_id;
    }
    public int getStatus_id() {
        return status_id;
    }
    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }
    public String getBilling_rules_json() {
        return billing_rules_json;
    }
    public void setBilling_rules_json(String billing_rules_json) {
        this.billing_rules_json = billing_rules_json;
    }
    public String getUrl_exclusion() {
        return url_exclusion;
    }
    public void setUrl_exclusion(String url_exclusion) {
        this.url_exclusion = url_exclusion;
    }
    public boolean isAllow_house_ads() {
        return allow_house_ads;
    }
    public void setAllow_house_ads(boolean allow_house_ads) {
        this.allow_house_ads = allow_house_ads;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getCategories_tier_1_list() {
        return categories_tier_1_list;
    }
    public void setCategories_tier_1_list(String categories_tier_1_list) {
        if(!(categories_tier_1_list==null || "".equals(categories_tier_1_list))){
            this.categories_tier_1_list = categories_tier_1_list;
        }
    }
    public String getCategories_tier_2_list() {
        return categories_tier_2_list;
    }
    public void setCategories_tier_2_list(String categories_tier_2_list) {
        if(!(categories_tier_2_list==null || "".equals(categories_tier_2_list))){
            this.categories_tier_2_list = categories_tier_2_list;
        }
    }
    public String getCategory_list_inc_exc_tier_1() {
        return category_list_inc_exc_tier_1;
    }
    public void setCategory_list_inc_exc_tier_1(String category_list_inc_exc_tier_1) {
        if(!(category_list_inc_exc_tier_1==null || "".equals(category_list_inc_exc_tier_1))){
            this.category_list_inc_exc_tier_1 = category_list_inc_exc_tier_1;
        }
    }
    public String getCategory_list_inc_exc_tier_2() {
        return category_list_inc_exc_tier_2;
    }
    public void setCategory_list_inc_exc_tier_2(String category_list_inc_exc_tier_2) {
        if(!(category_list_inc_exc_tier_2==null || "".equals(category_list_inc_exc_tier_2))){
            this.category_list_inc_exc_tier_2 = category_list_inc_exc_tier_2;
        }
    }
    public double getFloor() {
        return floor;
    }
    public void setFloor(double floor) {
        this.floor = floor;
    }
    public boolean isIs_advertiser_excluded() {
        return is_advertiser_excluded;
    }
    public void setIs_advertiser_excluded(boolean is_advertiser_excluded) {
        this.is_advertiser_excluded = is_advertiser_excluded;
    }
    public String getAdvertiser_json_array() {
        return advertiser_json_array;
    }
    public void setAdvertiser_json_array(String advertiser_json_array) {
        this.advertiser_json_array = advertiser_json_array;
    }
    public String getCampaign_json_array() {
        return campaign_json_array;
    }
    public void setCampaign_json_array(String campaign_json_array) {
        this.campaign_json_array = campaign_json_array;
    }
    public String getCampaign_inc_exc_schema() {
        return campaign_inc_exc_schema;
    }
    public void setCampaign_inc_exc_schema(String campaign_inc_exc_schema) {
        this.campaign_inc_exc_schema = campaign_inc_exc_schema;
    }
    public int getPassback_type() {
        return passback_type;
    }
    public void setPassback_type(int passback_type) {
        this.passback_type = passback_type;
    }
    public String getNofill_backup_content() {
        return nofill_backup_content;
    }
    public void setNofill_backup_content(String nofill_backup_content) {
        this.nofill_backup_content = nofill_backup_content;
    }
    public int getPassback_content_type() {
        return passback_content_type;
    }
    public void setPassback_content_type(int passback_content_type) {
        this.passback_content_type = passback_content_type;
    }
    public boolean isIs_richmedia_allowed() {
        return is_richmedia_allowed;
    }
    public void setIs_richmedia_allowed(boolean is_richmedia_allowed) {
        this.is_richmedia_allowed = is_richmedia_allowed;
    }
    public boolean isIs_native() {
        return is_native;
    }
    public void setIs_native(boolean is_native) {
        this.is_native = is_native;
    }
    public boolean isIs_video() {
        return is_video;
    }
    public void setIs_video(boolean is_video) {
        this.is_video = is_video;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public JsonNode toJsonIgnoreNull(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Site getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Site entity = objectMapper.readValue(str, Site.class);
        return entity;
    }
    public static Site getObjectIgnoreNull(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        Site entity = objectMapper.readValue(str, Site.class);
        return entity;
    }
    
}
