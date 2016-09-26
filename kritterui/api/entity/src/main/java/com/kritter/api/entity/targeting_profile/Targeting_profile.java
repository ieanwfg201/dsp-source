package com.kritter.api.entity.targeting_profile;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.Geo_Targeting_type;
import com.kritter.constants.MidpValue;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.SupplySourceEnum;
import com.kritter.constants.SupplySourceTypeEnum;

public class Targeting_profile {
    
    /** mandatory - auto generated at the time of creation */
    private String guid = null;
    /** mandatory */
    private String name = null;
    /** mandatory - guid of account @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory - @see com.kritter.constants.StatusIdEnum */
    private StatusIdEnum status_id = StatusIdEnum.Active;
    /** optional - @see json array of brand/ manufacturer ids */
    private String brand_list = "[]";
    /** optional - @see json array of model ids */
    private String model_list = "[]";
    /** optional - json map of os id and version string e.g. {"4":"2.0-4.2"} */
    private String os_json = "{}";
    /** optional - json map of browser id and version string e.g. {"4":"2.0-4.2"} */
    private String browser_json = "{}";
    /** optional - @see json array of country ids */
    private String country_json = "[]";
    /** optional - @see json array of carrier ids */
    private String carrier_json = "[]";
    /** placeholder */
    private String state_json = "[]";
    /** placeholder */
    private String city_json = "[]";
    /** placeholder */
    private String zipcode_file_id_set = null;
    /** flag to specify whether pub/site is included or excluded */
    private boolean is_site_list_excluded = false;
    /** optional - json array of tier 1 category */
    private String categories_tier_1_list = "[]";
    /** optional - json array of tier 2 category */
    private String categories_tier_2_list = "[]";
    /** flag to specify whether category is included or excluded */
    private boolean is_category_list_excluded = false;
	/** optional - jason array of ip range files */
    private String custom_ip_file_id_set = null;
    /** mandatory - id of Account which is modifying this entity @see com.kritter.api.entity.account.Account */
    private int modified_by = -1;
    /** auto populated */
    private long created_on = 0;
    /** auto populated */
    private long last_modified = 0;
    /** mandatory - @see com.kritter.constants.SupplySourceTypeEnum */
    private SupplySourceTypeEnum supply_source_type = SupplySourceTypeEnum.APP_WAP;
    /** mandatory - @see com.kritter.constants.SupplySourceEnum */
    private SupplySourceEnum supply_source = SupplySourceEnum.EXCHANGE_NETWORK;
    /** mandatory - @see com.kritter.constants.Geo_Targeting_type */
    private Geo_Targeting_type geo_targeting_type = Geo_Targeting_type.COUNTRY_CARRIER;
    /** optional - json array of hour of day*/
    private String hours_list = "[]";
    /** placeholder */
    private MidpValue midp = MidpValue.ALL;
    /** optional - json array of map e.g [{"lat":2374.28175,"lon":-823.2,"r":2562.87213}]*/
    /** @deprecated */
    private String lat_long = "";
    /** @deprecated */
    private String pub_list = "[]";
    /** @deprecated */
    private String exchange_list = "[]";
    /** @deprecated */
    private String site_list = "[]";
    /** @deprecated */
    private String ext_supply_attributes = "[]";
    /** optional - json map of pubid to site id array e.g. {"15":[25]} */
    private String direct_supply_inc_exc = "[]";
    /** optional - json map of exchangeid to site id map of ext site internal id e.g.  {"29":{"29":[{"intid":237512}]}}*/
    private String  exchange_supply_inc_exc= "[]";
    /** optional - json array of connection type @see com.kritter.constants.ConnectionType */
    private String connection_type_targeting_json = "[]";
    /** optional target only tablet */
    private boolean tablet_targeting=false;
    /** optional when retargeting segment is selected */
    private String retargeting ="[]";
    /** supply inclusion exclusion if required ,not used in ui workflow, for limited parameters.**/
    private String supplyInclusionExclusion;
    /** optional for pmp deal ids  */
    private String pmp_exchange ="";
    private String pmp_dealid ="";
    private String pmp_deal_json = "{}";
    private String device_type = "[]";
    private boolean mma_inc_exc=true;
    /** optional - json array of mma tier1 category */
    private String mma_tier_1_list = "[]";
    /** optional - json array of mma_tier2 category */
    private String mma_tier_2_list = "[]";
    private boolean adposition_inc_exc=true;
    /** optional - json array of adpositions/ adspaces*/
    private String adposition_list = "[]";
    private boolean channel_inc_exc=true;
    /** optional - json array of channel tier1 category */
    private String channel_tier_1_list = "[]";
    /** optional - json array of channel_tier2 category */
    private String channel_tier_2_list = "[]";
    /** optional - jason array of lat lon radius files */
    private String lat_lon_radius_file = null;
    /** optional - jason array of deviceid files */
    private boolean user_id_inc_exc=true;
    private String deviceid_file = null;
    private String file_prefix_path = null;
    private int id ;
    private int lat_lon_radius_unit =0 ;
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account_guid == null) ? 0 : account_guid.hashCode());
		result = prime * result + (adposition_inc_exc ? 1231 : 1237);
		result = prime * result + ((adposition_list == null) ? 0 : adposition_list.hashCode());
		result = prime * result + ((brand_list == null) ? 0 : brand_list.hashCode());
		result = prime * result + ((browser_json == null) ? 0 : browser_json.hashCode());
		result = prime * result + ((carrier_json == null) ? 0 : carrier_json.hashCode());
		result = prime * result + ((categories_tier_1_list == null) ? 0 : categories_tier_1_list.hashCode());
		result = prime * result + ((categories_tier_2_list == null) ? 0 : categories_tier_2_list.hashCode());
		result = prime * result + (channel_inc_exc ? 1231 : 1237);
		result = prime * result + ((channel_tier_1_list == null) ? 0 : channel_tier_1_list.hashCode());
		result = prime * result + ((channel_tier_2_list == null) ? 0 : channel_tier_2_list.hashCode());
		result = prime * result + ((city_json == null) ? 0 : city_json.hashCode());
		result = prime * result
				+ ((connection_type_targeting_json == null) ? 0 : connection_type_targeting_json.hashCode());
		result = prime * result + ((country_json == null) ? 0 : country_json.hashCode());
		result = prime * result + (int) (created_on ^ (created_on >>> 32));
		result = prime * result + ((custom_ip_file_id_set == null) ? 0 : custom_ip_file_id_set.hashCode());
		result = prime * result + ((device_type == null) ? 0 : device_type.hashCode());
		result = prime * result + ((deviceid_file == null) ? 0 : deviceid_file.hashCode());
		result = prime * result + ((direct_supply_inc_exc == null) ? 0 : direct_supply_inc_exc.hashCode());
		result = prime * result + ((exchange_list == null) ? 0 : exchange_list.hashCode());
		result = prime * result + ((exchange_supply_inc_exc == null) ? 0 : exchange_supply_inc_exc.hashCode());
		result = prime * result + ((ext_supply_attributes == null) ? 0 : ext_supply_attributes.hashCode());
		result = prime * result + ((geo_targeting_type == null) ? 0 : geo_targeting_type.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((hours_list == null) ? 0 : hours_list.hashCode());
		result = prime * result + (is_category_list_excluded ? 1231 : 1237);
		result = prime * result + (is_site_list_excluded ? 1231 : 1237);
		result = prime * result + (int) (last_modified ^ (last_modified >>> 32));
		result = prime * result + ((lat_lon_radius_file == null) ? 0 : lat_lon_radius_file.hashCode());
		result = prime * result + ((lat_long == null) ? 0 : lat_long.hashCode());
		result = prime * result + ((midp == null) ? 0 : midp.hashCode());
		result = prime * result + (mma_inc_exc ? 1231 : 1237);
		result = prime * result + ((mma_tier_1_list == null) ? 0 : mma_tier_1_list.hashCode());
		result = prime * result + ((mma_tier_2_list == null) ? 0 : mma_tier_2_list.hashCode());
		result = prime * result + ((model_list == null) ? 0 : model_list.hashCode());
		result = prime * result + modified_by;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((os_json == null) ? 0 : os_json.hashCode());
		result = prime * result + ((pmp_deal_json == null) ? 0 : pmp_deal_json.hashCode());
		result = prime * result + ((pmp_dealid == null) ? 0 : pmp_dealid.hashCode());
		result = prime * result + ((pmp_exchange == null) ? 0 : pmp_exchange.hashCode());
		result = prime * result + ((pub_list == null) ? 0 : pub_list.hashCode());
		result = prime * result + ((retargeting == null) ? 0 : retargeting.hashCode());
		result = prime * result + ((site_list == null) ? 0 : site_list.hashCode());
		result = prime * result + ((state_json == null) ? 0 : state_json.hashCode());
		result = prime * result + ((status_id == null) ? 0 : status_id.hashCode());
		result = prime * result + ((supplyInclusionExclusion == null) ? 0 : supplyInclusionExclusion.hashCode());
		result = prime * result + ((supply_source == null) ? 0 : supply_source.hashCode());
		result = prime * result + ((supply_source_type == null) ? 0 : supply_source_type.hashCode());
		result = prime * result + (tablet_targeting ? 1231 : 1237);
		result = prime * result + ((zipcode_file_id_set == null) ? 0 : zipcode_file_id_set.hashCode());
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
		Targeting_profile other = (Targeting_profile) obj;
		if (account_guid == null) {
			if (other.account_guid != null)
				return false;
		} else if (!account_guid.equals(other.account_guid))
			return false;
		if (adposition_inc_exc != other.adposition_inc_exc)
			return false;
		if (adposition_list == null) {
			if (other.adposition_list != null)
				return false;
		} else if (!adposition_list.equals(other.adposition_list))
			return false;
		if (brand_list == null) {
			if (other.brand_list != null)
				return false;
		} else if (!brand_list.equals(other.brand_list))
			return false;
		if (browser_json == null) {
			if (other.browser_json != null)
				return false;
		} else if (!browser_json.equals(other.browser_json))
			return false;
		if (carrier_json == null) {
			if (other.carrier_json != null)
				return false;
		} else if (!carrier_json.equals(other.carrier_json))
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
		if (channel_inc_exc != other.channel_inc_exc)
			return false;
		if (channel_tier_1_list == null) {
			if (other.channel_tier_1_list != null)
				return false;
		} else if (!channel_tier_1_list.equals(other.channel_tier_1_list))
			return false;
		if (channel_tier_2_list == null) {
			if (other.channel_tier_2_list != null)
				return false;
		} else if (!channel_tier_2_list.equals(other.channel_tier_2_list))
			return false;
		if (city_json == null) {
			if (other.city_json != null)
				return false;
		} else if (!city_json.equals(other.city_json))
			return false;
		if (connection_type_targeting_json == null) {
			if (other.connection_type_targeting_json != null)
				return false;
		} else if (!connection_type_targeting_json.equals(other.connection_type_targeting_json))
			return false;
		if (country_json == null) {
			if (other.country_json != null)
				return false;
		} else if (!country_json.equals(other.country_json))
			return false;
		if (created_on != other.created_on)
			return false;
		if (custom_ip_file_id_set == null) {
			if (other.custom_ip_file_id_set != null)
				return false;
		} else if (!custom_ip_file_id_set.equals(other.custom_ip_file_id_set))
			return false;
		if (device_type == null) {
			if (other.device_type != null)
				return false;
		} else if (!device_type.equals(other.device_type))
			return false;
		if (deviceid_file == null) {
			if (other.deviceid_file != null)
				return false;
		} else if (!deviceid_file.equals(other.deviceid_file))
			return false;
		if (direct_supply_inc_exc == null) {
			if (other.direct_supply_inc_exc != null)
				return false;
		} else if (!direct_supply_inc_exc.equals(other.direct_supply_inc_exc))
			return false;
		if (exchange_list == null) {
			if (other.exchange_list != null)
				return false;
		} else if (!exchange_list.equals(other.exchange_list))
			return false;
		if (exchange_supply_inc_exc == null) {
			if (other.exchange_supply_inc_exc != null)
				return false;
		} else if (!exchange_supply_inc_exc.equals(other.exchange_supply_inc_exc))
			return false;
		if (ext_supply_attributes == null) {
			if (other.ext_supply_attributes != null)
				return false;
		} else if (!ext_supply_attributes.equals(other.ext_supply_attributes))
			return false;
		if (geo_targeting_type != other.geo_targeting_type)
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (hours_list == null) {
			if (other.hours_list != null)
				return false;
		} else if (!hours_list.equals(other.hours_list))
			return false;
		if (is_category_list_excluded != other.is_category_list_excluded)
			return false;
		if (is_site_list_excluded != other.is_site_list_excluded)
			return false;
		if (last_modified != other.last_modified)
			return false;
		if (lat_lon_radius_file == null) {
			if (other.lat_lon_radius_file != null)
				return false;
		} else if (!lat_lon_radius_file.equals(other.lat_lon_radius_file))
			return false;
		if (lat_long == null) {
			if (other.lat_long != null)
				return false;
		} else if (!lat_long.equals(other.lat_long))
			return false;
		if (midp != other.midp)
			return false;
		if (mma_inc_exc != other.mma_inc_exc)
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
		if (model_list == null) {
			if (other.model_list != null)
				return false;
		} else if (!model_list.equals(other.model_list))
			return false;
		if (modified_by != other.modified_by)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (os_json == null) {
			if (other.os_json != null)
				return false;
		} else if (!os_json.equals(other.os_json))
			return false;
		if (pmp_deal_json == null) {
			if (other.pmp_deal_json != null)
				return false;
		} else if (!pmp_deal_json.equals(other.pmp_deal_json))
			return false;
		if (pmp_dealid == null) {
			if (other.pmp_dealid != null)
				return false;
		} else if (!pmp_dealid.equals(other.pmp_dealid))
			return false;
		if (pmp_exchange == null) {
			if (other.pmp_exchange != null)
				return false;
		} else if (!pmp_exchange.equals(other.pmp_exchange))
			return false;
		if (pub_list == null) {
			if (other.pub_list != null)
				return false;
		} else if (!pub_list.equals(other.pub_list))
			return false;
		if (retargeting == null) {
			if (other.retargeting != null)
				return false;
		} else if (!retargeting.equals(other.retargeting))
			return false;
		if (site_list == null) {
			if (other.site_list != null)
				return false;
		} else if (!site_list.equals(other.site_list))
			return false;
		if (state_json == null) {
			if (other.state_json != null)
				return false;
		} else if (!state_json.equals(other.state_json))
			return false;
		if (status_id != other.status_id)
			return false;
		if (supplyInclusionExclusion == null) {
			if (other.supplyInclusionExclusion != null)
				return false;
		} else if (!supplyInclusionExclusion.equals(other.supplyInclusionExclusion))
			return false;
		if (supply_source != other.supply_source)
			return false;
		if (supply_source_type != other.supply_source_type)
			return false;
		if (tablet_targeting != other.tablet_targeting)
			return false;
		if (zipcode_file_id_set == null) {
			if (other.zipcode_file_id_set != null)
				return false;
		} else if (!zipcode_file_id_set.equals(other.zipcode_file_id_set))
			return false;
		return true;
	}
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFile_prefix_path() {
		return file_prefix_path;
	}
	public void setFile_prefix_path(String file_prefix_path) {
		this.file_prefix_path = file_prefix_path;
	}

    public String getDeviceid_file() {
		return deviceid_file;
	}
	public void setDeviceid_file(String deviceid_file) {
		this.deviceid_file = deviceid_file;
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
    public MidpValue getMidp() {
        return midp;
    }
    public void setMidp(MidpValue midp) {
        this.midp = midp;
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
    public void setPub_list(String pub_list) {
        this.pub_list = pub_list;
    }
    public String getPub_list() {
        return pub_list;
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
    public String getDirect_supply_inc_exc() {
        return direct_supply_inc_exc;
    }
    public void setDirect_supply_inc_exc(String direct_supply_inc_exc) {
        this.direct_supply_inc_exc = direct_supply_inc_exc;
    }
    public String getExchange_supply_inc_exc() {
        return exchange_supply_inc_exc;
    }
    public void setExchange_supply_inc_exc(String exchange_supply_inc_exc) {
        this.exchange_supply_inc_exc = exchange_supply_inc_exc;
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
    public String getRetargeting() {
        return retargeting;
    }
    public void setRetargeting(String retargeting) {
        this.retargeting = retargeting;
    }
    public String getSupplyInclusionExclusion(){
        return supplyInclusionExclusion;
    }
    public void setSupplyInclusionExclusion(String supplyInclusionExclusion){
        this.supplyInclusionExclusion = supplyInclusionExclusion;
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
    public String getPmp_deal_json() {
        return pmp_deal_json;
    }
    public void setPmp_deal_json(String pmp_deal_json) {
        this.pmp_deal_json = pmp_deal_json;
    }
    public String getDevice_type() {
        return device_type;
    }
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
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
    
	public int getLat_lon_radius_unit() {
		return lat_lon_radius_unit;
	}
	public void setLat_lon_radius_unit(int lat_lon_radius_unit) {
		this.lat_lon_radius_unit = lat_lon_radius_unit;
	}
    public boolean isUser_id_inc_exc() {
		return user_id_inc_exc;
	}
	public void setUser_id_inc_exc(boolean user_id_inc_exc) {
		this.user_id_inc_exc = user_id_inc_exc;
	}

	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Targeting_profile getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        Targeting_profile entity = objectMapper.readValue(str, Targeting_profile.class);
        return entity;

    }
    
}
