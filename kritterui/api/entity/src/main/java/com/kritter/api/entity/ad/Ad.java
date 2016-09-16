package com.kritter.api.entity.ad;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.BidType;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.HygieneCategory;
import com.kritter.constants.MarketPlace;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.UserConstant;
import com.kritter.constants.tracking_partner.TrackingPartner;

public class Ad {
    /** mandatory - id of ad - auto generated at the time of creation*/
    private int id = -1 ;
    /** mandatory - guid of ad - auto generated at the time of creation*/
    private String guid = null;
    /** mandatory */
    private String name = null;
    /** mandatory - id of creative container @see com.kritter.api.entity.creative_container.Creative_container */
    private int creative_id = -1; 
    /** mandatory - guid of creative container @see com.kritter.api.entity.creative_container.Creative_container */
    private String creative_guid = null;
    /** mandatory */
    private String landing_url = null; 
    /** mandatory - guid of Targeting profile @see com.kritter.api.entity.targeting_profile.Targeting_profile */
    private String targeting_guid = null;
    /** mandatory - id of campaign @see com.kritter.api.entity.campaign.Campaign */
    private int campaign_id = -1;
    /** mandatory - guid of campaign @see com.kritter.api.entity.campaign.Campaign */
    private String campaign_guid = null;
    /** optional - json array of tier1 category */
    private String categories_tier_1_list = "[]";
    /** optional - json array of tier2 category */
    private String categories_tier_2_list = "[]";
    /** mandatory - json array of hygiene @see com.kritter.constants.HygieneCategory */
    private String hygiene_list = "["+HygieneCategory.FAMILY_SAFE.getCode()+"]";
    /** mandatory - @see com.kritter.constants.StatusIdEnum */
    private StatusIdEnum status_id = StatusIdEnum.Pending;
    /** mandatory - @see com.kritter.constants.MarketPlace */
    private MarketPlace marketplace_id = MarketPlace.CPM;
    /** mandatory - set qual to advertiser bid if not specified manually */
    private double internal_max_bid = 0.005 ;
    /** mandatory */
    private double advertiser_bid = 0.005;
    /** placeholder */
    private String allocation_ids = "";
    /** auto generated */
    private long created_on = 0;
    /** auto generated */
    private long last_modified = 0;
    /** mandatory id of account modifying the entity - @see com.kritter.api.entity.account.Account */
    private int modified_by = -1;
    /** optional */
    private String comment = null;
    /** mandatory - @see com.kritter.constants.tracking_partner.TrackingPartner */
    private TrackingPartner tracking_partner = TrackingPartner.NONE;
    /** placeholder */
    private String targeting_profile_name = "";
    /** optional */
    private double cpa_goal = 0.0 ;
    /** optional */
    private String adv_domain = "" ;
    /** user */
    /** flag to signify whether user is frequency capped or not */
    private boolean is_frequency_capped = false;
    /** # of time an ad should be shown to a user */
    private int frequency_cap = UserConstant.frequency_cap_default;
    /** the time window for which frequency cap should be honored */
    private int time_window = UserConstant.frequency_cap_time_window_default;
    private int bidtype = BidType.AUTO.getCode();
    private String external_imp_tracker = "";
    private String impMacro = "[]";
    private int impMacroQuote = 0;
    private String external_click_tracker = "";
    private String clickMacro = "[]";
    private int clickMacroQuote = 0;
    private int extclickType = 1;
	/** optional - json array of mma tier1 category */
    private String mma_tier_1_list = "[]";
    /** optional - json array of mma_tier2 category */
    private String mma_tier_2_list = "[]";
    
    private boolean click_freq_cap = false;
    private int click_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int click_freq_cap_count = UserConstant.frequency_cap_default;
    private int click_freq_time_window = UserConstant.frequency_cap_time_window_default;
    private boolean imp_freq_cap = false;
    private int imp_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int imp_freq_cap_count = UserConstant.frequency_cap_default;
    private int imp_freq_time_window = UserConstant.frequency_cap_time_window_default;
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adv_domain == null) ? 0 : adv_domain.hashCode());
		long temp;
		temp = Double.doubleToLongBits(advertiser_bid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((allocation_ids == null) ? 0 : allocation_ids.hashCode());
		result = prime * result + bidtype;
		result = prime * result + ((campaign_guid == null) ? 0 : campaign_guid.hashCode());
		result = prime * result + campaign_id;
		result = prime * result + ((categories_tier_1_list == null) ? 0 : categories_tier_1_list.hashCode());
		result = prime * result + ((categories_tier_2_list == null) ? 0 : categories_tier_2_list.hashCode());
		result = prime * result + ((clickMacro == null) ? 0 : clickMacro.hashCode());
		result = prime * result + clickMacroQuote;
		result = prime * result + (click_freq_cap ? 1231 : 1237);
		result = prime * result + click_freq_cap_count;
		result = prime * result + click_freq_cap_type;
		result = prime * result + click_freq_time_window;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		temp = Double.doubleToLongBits(cpa_goal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (created_on ^ (created_on >>> 32));
		result = prime * result + ((creative_guid == null) ? 0 : creative_guid.hashCode());
		result = prime * result + creative_id;
		result = prime * result + ((external_click_tracker == null) ? 0 : external_click_tracker.hashCode());
		result = prime * result + ((external_imp_tracker == null) ? 0 : external_imp_tracker.hashCode());
		result = prime * result + frequency_cap;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((hygiene_list == null) ? 0 : hygiene_list.hashCode());
		result = prime * result + id;
		result = prime * result + ((impMacro == null) ? 0 : impMacro.hashCode());
		result = prime * result + impMacroQuote;
		result = prime * result + (imp_freq_cap ? 1231 : 1237);
		result = prime * result + imp_freq_cap_count;
		result = prime * result + imp_freq_cap_type;
		result = prime * result + imp_freq_time_window;
		temp = Double.doubleToLongBits(internal_max_bid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (is_frequency_capped ? 1231 : 1237);
		result = prime * result + ((landing_url == null) ? 0 : landing_url.hashCode());
		result = prime * result + (int) (last_modified ^ (last_modified >>> 32));
		result = prime * result + ((marketplace_id == null) ? 0 : marketplace_id.hashCode());
		result = prime * result + ((mma_tier_1_list == null) ? 0 : mma_tier_1_list.hashCode());
		result = prime * result + ((mma_tier_2_list == null) ? 0 : mma_tier_2_list.hashCode());
		result = prime * result + modified_by;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status_id == null) ? 0 : status_id.hashCode());
		result = prime * result + ((targeting_guid == null) ? 0 : targeting_guid.hashCode());
		result = prime * result + ((targeting_profile_name == null) ? 0 : targeting_profile_name.hashCode());
		result = prime * result + time_window;
		result = prime * result + ((tracking_partner == null) ? 0 : tracking_partner.hashCode());
		return result;
	}
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ad other = (Ad) obj;
		if (adv_domain == null) {
			if (other.adv_domain != null)
				return false;
		} else if (!adv_domain.equals(other.adv_domain))
			return false;
		if (Double.doubleToLongBits(advertiser_bid) != Double.doubleToLongBits(other.advertiser_bid))
			return false;
		if (allocation_ids == null) {
			if (other.allocation_ids != null)
				return false;
		} else if (!allocation_ids.equals(other.allocation_ids))
			return false;
		if (bidtype != other.bidtype)
			return false;
		if (campaign_guid == null) {
			if (other.campaign_guid != null)
				return false;
		} else if (!campaign_guid.equals(other.campaign_guid))
			return false;
		if (campaign_id != other.campaign_id)
			return false;
		if (categories_tier_1_list == null) {
			if (other.categories_tier_1_list != null)
				return false;
		} else if (!categories_tier_1_list.equals(other.categories_tier_1_list))
			return false;
		if (categories_tier_2_list == null) {
			if (other.categories_tier_2_list != null)
				return false;
		} else if (!categories_tier_2_list.equals(other.categories_tier_2_list))
			return false;
		if (clickMacro == null) {
			if (other.clickMacro != null)
				return false;
		} else if (!clickMacro.equals(other.clickMacro))
			return false;
		if (clickMacroQuote != other.clickMacroQuote)
			return false;
		if (click_freq_cap != other.click_freq_cap)
			return false;
		if (click_freq_cap_count != other.click_freq_cap_count)
			return false;
		if (click_freq_cap_type != other.click_freq_cap_type)
			return false;
		if (click_freq_time_window != other.click_freq_time_window)
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (Double.doubleToLongBits(cpa_goal) != Double.doubleToLongBits(other.cpa_goal))
			return false;
		if (created_on != other.created_on)
			return false;
		if (creative_guid == null) {
			if (other.creative_guid != null)
				return false;
		} else if (!creative_guid.equals(other.creative_guid))
			return false;
		if (creative_id != other.creative_id)
			return false;
		if (external_click_tracker == null) {
			if (other.external_click_tracker != null)
				return false;
		} else if (!external_click_tracker.equals(other.external_click_tracker))
			return false;
		if (external_imp_tracker == null) {
			if (other.external_imp_tracker != null)
				return false;
		} else if (!external_imp_tracker.equals(other.external_imp_tracker))
			return false;
		if (frequency_cap != other.frequency_cap)
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (hygiene_list == null) {
			if (other.hygiene_list != null)
				return false;
		} else if (!hygiene_list.equals(other.hygiene_list))
			return false;
		if (id != other.id)
			return false;
		if (impMacro == null) {
			if (other.impMacro != null)
				return false;
		} else if (!impMacro.equals(other.impMacro))
			return false;
		if (impMacroQuote != other.impMacroQuote)
			return false;
		if (imp_freq_cap != other.imp_freq_cap)
			return false;
		if (imp_freq_cap_count != other.imp_freq_cap_count)
			return false;
		if (imp_freq_cap_type != other.imp_freq_cap_type)
			return false;
		if (imp_freq_time_window != other.imp_freq_time_window)
			return false;
		if (Double.doubleToLongBits(internal_max_bid) != Double.doubleToLongBits(other.internal_max_bid))
			return false;
		if (is_frequency_capped != other.is_frequency_capped)
			return false;
		if (landing_url == null) {
			if (other.landing_url != null)
				return false;
		} else if (!landing_url.equals(other.landing_url))
			return false;
		if (last_modified != other.last_modified)
			return false;
		if (marketplace_id != other.marketplace_id)
			return false;
		if (mma_tier_1_list == null) {
			if (other.mma_tier_1_list != null)
				return false;
		} else if (!mma_tier_1_list.equals(other.mma_tier_1_list))
			return false;
		if (mma_tier_2_list == null) {
			if (other.mma_tier_2_list != null)
				return false;
		} else if (!mma_tier_2_list.equals(other.mma_tier_2_list))
			return false;
		if (modified_by != other.modified_by)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status_id != other.status_id)
			return false;
		if (targeting_guid == null) {
			if (other.targeting_guid != null)
				return false;
		} else if (!targeting_guid.equals(other.targeting_guid))
			return false;
		if (targeting_profile_name == null) {
			if (other.targeting_profile_name != null)
				return false;
		} else if (!targeting_profile_name.equals(other.targeting_profile_name))
			return false;
		if (time_window != other.time_window)
			return false;
		if (tracking_partner != other.tracking_partner)
			return false;
		return true;
	}
	public String getImpMacro() {
		return impMacro;
	}
	public void setImpMacro(String impMacro) {
		this.impMacro = impMacro;
	}
	public int getImpMacroQuote() {
		return impMacroQuote;
	}
	public void setImpMacroQuote(int impMacroQuote) {
		this.impMacroQuote = impMacroQuote;
	}
	public String getClickMacro() {
		return clickMacro;
	}
	public void setClickMacro(String clickMacro) {
		this.clickMacro = clickMacro;
	}
	public int getClickMacroQuote() {
		return clickMacroQuote;
	}
	public void setClickMacroQuote(int clickMacroQuote) {
		this.clickMacroQuote = clickMacroQuote;
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
        this.landing_url = landing_url;
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
    public TrackingPartner getTracking_partner() {
        return tracking_partner;
    }
    public void setTracking_partner(TrackingPartner tracking_partner) {
        this.tracking_partner = tracking_partner;
    }
    public String getTargeting_profile_name() {
        return targeting_profile_name;
    }
    public void setTargeting_profile_name(String targeting_profile_name) {
        this.targeting_profile_name = targeting_profile_name;
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
    public int getExtclickType() {
		return extclickType;
	}
	public void setExtclickType(int extclickType) {
		this.extclickType = extclickType;
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Ad getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        Ad entity = objectMapper.readValue(str, Ad.class);
        return entity;

    }
}
