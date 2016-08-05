package models.entities;

import org.springframework.beans.BeanUtils;
import play.data.validation.Constraints.Required;
import com.kritter.api.entity.site.Site;
import com.kritter.constants.HygieneCategory;
import com.kritter.constants.Payout;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.VideoAdPos;
import com.kritter.constants.VideoLinearity;

import lombok.Getter;
import lombok.Setter;

public class SiteEntity extends Entity{
    
    private int id = -1;
    private String guid = null;
    @Required
    private String name = null;
    
    private int pub_id = -1;
    private String pub_guid = null;
    private String site_url = null;
    
    @Required
    private SITE_PLATFORM site_platform_id = null;
    
    private String app_id = null;
    private int app_store_id = -1; 
    
    
    @Required
    private String categories_tier_1_list = null;
    private String categories_tier_2_list = null;
    private String category_list_inc_exc_tier_1 = null;
    private String category_list_inc_exc_tier_2 = null;
    private boolean is_category_list_excluded = true;
    
    private String hygiene_list =  ""+HygieneCategory.FAMILY_SAFE.getCode();
    private String opt_in_hygiene_list = "[]";
    private String creative_attr_inc_exc = null;
    private boolean is_creative_attr_exc = true;
    
    private int status_id = 5;
    private long created_on = 0;
    private long last_modified = 0;
    private int modified_by = -1;
    private String billing_rules_json = Payout.default_payout_percent_str;
    private String url_exclusion = null;
    private boolean allow_house_ads = true;
    private String comments = null;
    private double floor = 0.0;
    private boolean is_advertiser_excluded = true;
    private String advertiser_json_array = "[]";
    private String campaign_json_array = "[]";
    private int passback_type = -1;
    private int passback_content_type = -1;
    private String nofill_backup_content ="";
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
    @Getter@Setter
    private String strmimes; 
    @Getter@Setter
    private String strprotocols;
	@Getter@Setter
	private Integer linearity = VideoLinearity.LINEAR_IN_STREAM.getCode();
	@Getter@Setter
	private Integer startDelay;
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
	@Getter@Setter
	private String strdelivery;
	@Getter@Setter
	private Integer pos = VideoAdPos.Unknown.getCode();
	@Getter@Setter
	private String strapi;
	@Getter@Setter
	private String strcompaniontype;
	/** Video Properties End */
    
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
        if(SITE_PLATFORM.APP==site_platform_id){
            this.setSite_url("");
        }
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
        this.categories_tier_1_list = categories_tier_1_list;
    }
    public String getCategories_tier_2_list() {
        return categories_tier_2_list;
    }
    public void setCategories_tier_2_list(String categories_tier_2_list) {
        this.categories_tier_2_list = categories_tier_2_list;
    }
    public String getCategory_list_inc_exc_tier_1() {
        return category_list_inc_exc_tier_1;
    }
    public void setCategory_list_inc_exc_tier_1(String category_list_inc_exc_tier_1) {
        this.category_list_inc_exc_tier_1 = category_list_inc_exc_tier_1;
    }
    public String getCategory_list_inc_exc_tier_2() {
        return category_list_inc_exc_tier_2;
    }
    public void setCategory_list_inc_exc_tier_2(String category_list_inc_exc_tier_2) {
        this.category_list_inc_exc_tier_2 = category_list_inc_exc_tier_2;
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

    public Site getEntity(){
    	Site site = new Site();
    	BeanUtils.copyProperties(this, site);
    	return site;
    }
    
     
    
}
