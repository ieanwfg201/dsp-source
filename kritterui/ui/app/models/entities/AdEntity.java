package models.entities;

import models.validators.AtleastOne;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;

import com.kritter.api.entity.ad.Ad;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.HygieneCategory;
import com.kritter.constants.MarketPlace;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.UserConstant;
import com.kritter.constants.tracking_partner.TrackingPartner;

public class AdEntity extends Entity{
    private int id = -1 ;
    private String guid = null;
    @Required
    private String name = null;
    
    @Required
    private String creative = null;
    
    
    private int creative_id = -1; 
    
    private String creative_guid = null;
    @Required @URL
    private String landing_url = null; 
    @Required
    private String targeting_guid = null;
    private int campaign_id = -1;
    private String campaign_guid = null;
    private String categories_tier_1_list = "[]";
    private String categories_tier_2_list = "[]";
    @AtleastOne
    private String hygiene_list = "["+HygieneCategory.FAMILY_SAFE.getCode()+"]";
    private StatusIdEnum status_id = StatusIdEnum.Pending;
    private MarketPlace marketplace_id = MarketPlace.CPM;
    private TrackingPartner tracking_partner = TrackingPartner.NONE;
    @Required
    private double internal_max_bid = 0.005 ;
    @Required
    private double advertiser_bid = 0.005;
    
    private String account_guid = "";
    
    private String allocation_ids = "";
    private long created_on = 0;
    private long last_modified = 0;
    private int modified_by = -1;
    private String comment = null;
    private double cpa_goal = 0.0;
    private String adv_domain = "";
    private boolean is_frequency_capped = false;
    private int frequency_cap = UserConstant.frequency_cap_default;
    private int time_window = UserConstant.frequency_cap_time_window_default;
    private int bidtype = 0;
    private String external_imp_tracker = "";
    private String external_click_tracker = "";
    private String mma_tier_1_list = "[]";
    private String mma_tier_2_list = "[]";
    private boolean click_freq_cap = false;
    private int click_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int click_freq_cap_count = UserConstant.frequency_cap_default;
    private int click_freq_time_window = UserConstant.frequency_cap_time_window_default;
    private boolean imp_freq_cap = false;
    private int imp_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int imp_freq_cap_count = UserConstant.frequency_cap_default;
    private int imp_freq_time_window = UserConstant.frequency_cap_time_window_default;


    
    public String getCreative(){ 
    	return creative_id+":"+ creative_guid;
    }
    
    public void setCreative(String creative){
    	String tmp[] = null;
    	if(creative != null && !"".equals(creative)){
    		tmp = creative.split(":");
    		if(tmp.length ==2){
    			setCreative_guid(tmp[1]);
        		setCreative_id(Integer.parseInt(tmp[0].trim()));
    		}   		
    	} 
    	this.creative = creative;
    }
    
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
    public int getCreative_id() {
        return creative_id;
    }
    public void setCreative_id(int creative_id) {
        this.creative_id = creative_id;
    }
    public String getCreative_guid() {
        return creative_guid;
    }
    public void setCreative_guid(String creative_guid) {
        this.creative_guid = creative_guid;
    }
    public String getLanding_url() {
        return landing_url;
    }
    public void setLanding_url(String landing_url) {
        this.landing_url = landing_url.trim();
    }
    public String getTargeting_guid() {
        return targeting_guid;
    }
    public void setTargeting_guid(String targeting_guid) {
        this.targeting_guid = targeting_guid;
    }
    public int getCampaign_id() {
        return campaign_id;
    }
    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }
    public String getCampaign_guid() {
        return campaign_guid;
    }
    public void setCampaign_guid(String campaign_guid) {
        this.campaign_guid = campaign_guid;
    }
    public String getHygiene_list() {
        return hygiene_list;
    }
    public void setHygiene_list(String hygiene_list) {
        this.hygiene_list = hygiene_list;
    }
    public StatusIdEnum getStatus_id() {
        return status_id;
    }
    public void setStatus_id(StatusIdEnum status_id) {
        this.status_id = status_id;
    }
    public MarketPlace getMarketplace_id() {
        return marketplace_id;
    }
    public void setMarketplace_id(MarketPlace marketplace_id) {
        this.marketplace_id = marketplace_id;
    }
    public double getInternal_max_bid() {
        return internal_max_bid;
    }
    public void setInternal_max_bid(double internal_max_bid) {
        this.internal_max_bid = internal_max_bid;
    }
    public double getAdvertiser_bid() {
        return advertiser_bid;
    }
    public void setAdvertiser_bid(double advertiser_bid) {
        this.advertiser_bid = advertiser_bid;
    }
    public String getAllocation_ids() {
        return allocation_ids;
    }
    public void setAllocation_ids(String allocation_ids) {
        this.allocation_ids = allocation_ids;
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
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }   
    
	public String getAccount_guid() {
		return account_guid;
	}

	public void setAccount_guid(String account_guid) {
		this.account_guid = account_guid;
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

    public TrackingPartner getTracking_partner() {
        return tracking_partner;
    }

    public void setTracking_partner(TrackingPartner tracking_partner) {
        this.tracking_partner = tracking_partner;
    }
    
    public double getCpa_goal() {
        return cpa_goal;
    }
    public void setCpa_goal(double cpa_goal) {
        this.cpa_goal = cpa_goal;
    }
    
    public String getAdv_domain() {
        return adv_domain;
    }

    public void setAdv_domain(String adv_domain) {
        this.adv_domain = adv_domain;
    }

    public boolean isIs_frequency_capped() {
        return is_frequency_capped;
    }

    public void setIs_frequency_capped(boolean is_frequency_capped) {
        this.is_frequency_capped = is_frequency_capped;
    }

    public int getFrequency_cap() {
        return frequency_cap;
    }

    public void setFrequency_cap(int frequency_cap) {
        this.frequency_cap = frequency_cap;
    }

    public int getTime_window() {
        return time_window;
    }

    public void setTime_window(int time_window) {
        this.time_window = time_window;
    }

    public int getBidtype() {
        return bidtype;
    }

    public void setBidtype(int bidtype) {
        this.bidtype = bidtype;
    }
    public String getExternal_imp_tracker() {
		return external_imp_tracker;
	}
	public void setExternal_imp_tracker(String external_imp_tracker) {
		this.external_imp_tracker = external_imp_tracker;
	}
	public String getExternal_click_tracker() {
		return external_click_tracker;
	}
	public void setExternal_click_tracker(String external_click_tracker) {
		this.external_click_tracker = external_click_tracker;
	}
	public String getMma_tier_1_list() {
		return mma_tier_1_list;
	}
	public void setMma_tier_1_list(String mma_tier_1_list) {
		this.mma_tier_1_list = mma_tier_1_list;
	}
	public String getMma_tier_2_list() {
		return mma_tier_2_list;
	}
	public void setMma_tier_2_list(String mma_tier_2_list) {
		this.mma_tier_2_list = mma_tier_2_list;
	}
    public boolean isClick_freq_cap() {
		return click_freq_cap;
	}
	public void setClick_freq_cap(boolean click_freq_cap) {
		this.click_freq_cap = click_freq_cap;
	}
	public int getClick_freq_cap_type() {
		return click_freq_cap_type;
	}
	public void setClick_freq_cap_type(int click_freq_cap_type) {
		this.click_freq_cap_type = click_freq_cap_type;
	}
	public int getClick_freq_cap_count() {
		return click_freq_cap_count;
	}
	public void setClick_freq_cap_count(int click_freq_cap_count) {
		this.click_freq_cap_count = click_freq_cap_count;
	}
	public int getClick_freq_time_window() {
		return click_freq_time_window;
	}
	public void setClick_freq_time_window(int click_freq_time_window) {
		this.click_freq_time_window = click_freq_time_window;
	}
	public boolean isImp_freq_cap() {
		return imp_freq_cap;
	}
	public void setImp_freq_cap(boolean imp_freq_cap) {
		this.imp_freq_cap = imp_freq_cap;
	}
	public int getImp_freq_cap_type() {
		return imp_freq_cap_type;
	}
	public void setImp_freq_cap_type(int imp_freq_cap_type) {
		this.imp_freq_cap_type = imp_freq_cap_type;
	}
	public int getImp_freq_cap_count() {
		return imp_freq_cap_count;
	}
	public void setImp_freq_cap_count(int imp_freq_cap_count) {
		this.imp_freq_cap_count = imp_freq_cap_count;
	}
	public int getImp_freq_time_window() {
		return imp_freq_time_window;
	}
	public void setImp_freq_time_window(int imp_freq_time_window) {
		this.imp_freq_time_window = imp_freq_time_window;
	}

    public Ad getEntity(){
    	Ad ad = new Ad();
    	BeanUtils.copyProperties(this, ad);
    	return ad;
    }
}
