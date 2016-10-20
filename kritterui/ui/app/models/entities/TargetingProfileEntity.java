package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;

import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.Geo_Targeting_type;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.MidpValue;
import com.kritter.constants.SupplySourceEnum;
import com.kritter.constants.SupplySourceTypeEnum;

public class TargetingProfileEntity extends Entity{

    private String guid = null;
    @Required
    private String name = null;
    private String account_guid = null;
    private StatusIdEnum status_id = StatusIdEnum.Active;
    private String brand_list = "[]";
    private String model_list = "[]";
    private String os_json = "{}";
    private String browser_json = "{}";
    private String country_json = "[]";
    private String carrier_json = "[]";
    private String state_json = "[]";
    private String city_json = "[]";
    private String zipcode_file_id_set = null;
    private String site_list = "[]";
    private boolean is_site_list_excluded = false;
    private String  categories_tier_1_list = "[]";
    private String  categories_tier_2_list = "[]";
    private boolean is_category_list_excluded = false;
    private String custom_ip_file_id_set = null;
    private int modified_by = -1;
    private long created_on = 0;
    private long last_modified = 0;
    private SupplySourceTypeEnum supply_source_type = SupplySourceTypeEnum.APP_WAP;
    private SupplySourceEnum supply_source = SupplySourceEnum.EXCHANGE_NETWORK;
    private Geo_Targeting_type geo_targeting_type = Geo_Targeting_type.COUNTRY_CARRIER;
    private MidpValue midp = MidpValue.ALL;
    private String hours_list = "[]";
    private String pub_list = "[]";
    private String exchange_list = "[]";
    private String lat_long = "";
    private String ext_supply_attributes = "[]";
    private String connection_type_targeting_json = "[]";
    private boolean tablet_targeting=false;
    private String retargeting="[]";
    private String pmp_exchange ="";
    private String pmp_dealid ="";
    private String device_type = "[]";
    private boolean mma_inc_exc=true;
    /** optional - json array of mma tier1 category */
    private String mma_tier_1_list = "[]";
    /** optional - json array of mma_tier2 category */
    private String mma_tier_2_list = "[]";
    private boolean adposition_inc_exc=true;
    private String adposition_list = "[]";
    private boolean channel_inc_exc=true;
    /** optional - json array of channel tier1 category */
    private String channel_tier_1_list = "[]";
    /** optional - json array of channel_tier2 category */
    private String channel_tier_2_list = "[]";
    private String lat_lon_radius_file = null;
    private String deviceid_file = null;
    private String file_prefix_path = null;
    private int id;
    private int lat_lon_radius_unit = 0;
    private int user_id_inc_exc=0;

	public int getUser_id_inc_exc() {
		return user_id_inc_exc;
	}
	public void setUser_id_inc_exc(int user_id_inc_exc) {
		this.user_id_inc_exc = user_id_inc_exc;
	}
	public MidpValue getMidp() {
        return midp;
    }
    public void setMidp(MidpValue midp) {
        this.midp = midp;
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
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public StatusIdEnum getStatus_id() {
        return status_id;
    }
    public void setStatus_id(StatusIdEnum status_id) {
        this.status_id = status_id;
    }
    public String getBrand_list() {
        return brand_list;
    }
    public void setBrand_list(String brand_list) {
        this.brand_list = brand_list;
    }
    public String getModel_list() {
        return model_list;
    }
    public void setModel_list(String model_list) {
        this.model_list = model_list;
    }
    public String getOs_json() {
        return os_json;
    }
    public void setOs_json(String os_json) {
        this.os_json = os_json;
    }
    public String getBrowser_json() {
        return browser_json;
    }
    public void setBrowser_json(String browser_json) {
        this.browser_json = browser_json;
    }
    public String getZipcode_file_id_set() {
        return zipcode_file_id_set;
    }
    public void setZipcode_file_id_set(String zipcode_file_id_set) {
        this.zipcode_file_id_set = zipcode_file_id_set;
    }
    public String getSite_list() {
        return site_list;
    }
    public void setSite_list(String site_list) {
        this.site_list = site_list;
    }
    public boolean isIs_site_list_excluded() {
        return is_site_list_excluded;
    }
    public void setIs_site_list_excluded(boolean is_site_list_excluded) {
        this.is_site_list_excluded = is_site_list_excluded;
    }
    public boolean isIs_category_list_excluded() {
        return is_category_list_excluded;
    }
    public void setIs_category_list_excluded(boolean is_category_list_excluded) {
        this.is_category_list_excluded = is_category_list_excluded;
    }
    public String getCustom_ip_file_id_set() {
        return custom_ip_file_id_set;
    }
    public void setCustom_ip_file_id_set(String custom_ip_file_id_set) {
        this.custom_ip_file_id_set = custom_ip_file_id_set;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
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
    public SupplySourceTypeEnum getSupply_source_type() {
        return supply_source_type;
    }
    public void setSupply_source_type(SupplySourceTypeEnum supply_source_type) {
        this.supply_source_type = supply_source_type;
    }
    public SupplySourceEnum getSupply_source() {
        return supply_source;
    }
    public void setSupply_source(SupplySourceEnum supply_source) {
        this.supply_source = supply_source;
    }
    public String getCountry_json() {
        return country_json;
    }
    public void setCountry_json(String country_json) {
        this.country_json = country_json;
    }
    public String getCarrier_json() {
        return carrier_json;
    }
    public void setCarrier_json(String carrier_json) {
        this.carrier_json = carrier_json;
    }
    public String getState_json() {
        return state_json;
    }
    public void setState_json(String state_json) {
        this.state_json = state_json;
    }
    public String getCity_json() {
        return city_json;
    }
    public void setCity_json(String city_json) {
        this.city_json = city_json;
    }
    
    public Geo_Targeting_type getGeo_targeting_type() {
        return geo_targeting_type;
    }
    public void setGeo_targeting_type(Geo_Targeting_type geo_targeting_type) {
        this.geo_targeting_type = geo_targeting_type;
    }
    
    public String getHours_list() {
		return hours_list;
	}
	public void setHours_list(String hours_list) {
		this.hours_list = hours_list;
	}
	public String getRetargeting() {
        return retargeting;
    }
    public void setRetargeting(String retargeting) {
        this.retargeting = retargeting;
    }
    public String getDevice_type() {
        return device_type;
    }
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
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
    public String getPub_list() {
        return pub_list;
    }
    public void setPub_list(String pub_list) {
        this.pub_list = pub_list;
    }
    public String getExt_supply_attributes() {
        return ext_supply_attributes;
    }
    public void setExt_supply_attributes(String ext_supply_attributes) {
        this.ext_supply_attributes = ext_supply_attributes;
    }
    public String getLat_long() {
        return lat_long;
    }
    public void setLat_long(String lat_long) {
        this.lat_long = lat_long;
    }
    public String getExchange_list() {
        return exchange_list;
    }
    public void setExchange_list(String exchange_list) {
        this.exchange_list = exchange_list;
    }
    public String getConnection_type_targeting_json() {
        return connection_type_targeting_json;
    }
    public void setConnection_type_targeting_json(
            String connection_type_targeting_json) {
        this.connection_type_targeting_json = connection_type_targeting_json;
    }
    public boolean isTablet_targeting() {
        return tablet_targeting;
    }
    public void setTablet_targeting(boolean tablet_targeting) {
        this.tablet_targeting = tablet_targeting;
    }
    public String getPmp_exchange() {
        return pmp_exchange;
    }
    public void setPmp_exchange(String pmp_exchange) {
        this.pmp_exchange = pmp_exchange;
    }
    public String getPmp_dealid() {
        return pmp_dealid;
    }
    public void setPmp_dealid(String pmp_dealid) {
        this.pmp_dealid = pmp_dealid;
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
	public boolean isMma_inc_exc() {
		return mma_inc_exc;
	}
	public void setMma_inc_exc(boolean mma_inc_exc) {
		this.mma_inc_exc = mma_inc_exc;
	}

    public String getAdposition_list() {
		return adposition_list;
	}
	public void setAdposition_list(String adposition_list) {
		this.adposition_list = adposition_list;
	}
	public boolean isAdposition_inc_exc() {
		return adposition_inc_exc;
	}
	public void setAdposition_inc_exc(boolean adposition_inc_exc) {
		this.adposition_inc_exc = adposition_inc_exc;
	}
	public boolean isChannel_inc_exc() {
		return channel_inc_exc;
	}
	public void setChannel_inc_exc(boolean channel_inc_exc) {
		this.channel_inc_exc = channel_inc_exc;
	}
	public String getChannel_tier_1_list() {
		return channel_tier_1_list;
	}
	public void setChannel_tier_1_list(String channel_tier_1_list) {
		this.channel_tier_1_list = channel_tier_1_list;
	}
	public String getChannel_tier_2_list() {
		return channel_tier_2_list;
	}
	public void setChannel_tier_2_list(String channel_tier_2_list) {
		this.channel_tier_2_list = channel_tier_2_list;
	}
	public String getLat_lon_radius_file() {
		return lat_lon_radius_file;
	}
	public void setLat_lon_radius_file(String lat_lon_radius_file) {
		this.lat_lon_radius_file = lat_lon_radius_file;
	}
    public String getDeviceid_file() {
		return deviceid_file;
	}
	public void setDeviceid_file(String deviceid_file) {
		this.deviceid_file = deviceid_file;
	}
    public String getFile_prefix_path() {
		return file_prefix_path;
	}
	public void setFile_prefix_path(String file_prefix_path) {
		this.file_prefix_path = file_prefix_path;
	}
    public int getLat_lon_radius_unit() {
		return lat_lon_radius_unit;
	}
	public void setLat_lon_radius_unit(int lat_lon_radius_unit) {
		this.lat_lon_radius_unit = lat_lon_radius_unit;
	}
	public Targeting_profile getEntity(){
    	Targeting_profile tp = new Targeting_profile();
    	BeanUtils.copyProperties(this, tp);
    	return tp;
    }
}
