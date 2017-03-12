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
    private int user_id_inc_exc=0;
    private String deviceid_file = null;
    private String file_prefix_path = null;
    private int id ;
    private int lat_lon_radius_unit =0 ;
    private boolean audience_targeting=false;
	private boolean audience_genderincl=false;
    private String audience_gender = "[]";
	private boolean audience_agerangeinc=false;
    private String audience_agerange = "[]";
	private boolean audience_catinc=false;
    private String audience_tier1_cat = "[]";
    private String audience_tier2_cat = "[]";
    private String audience_tier3_cat = "[]";
    private String audience_tier4_cat = "[]";
    private String audience_tier5_cat = "[]";
	private String audience_tags;
	private String audience_inc;
	private String audience_exc;
	private String audience_package;

    public String getAudience_inc() {
        return audience_inc;
    }

    public void setAudience_inc(String audience_inc) {
        this.audience_inc = audience_inc;
    }

    public String getAudience_exc() {
        return audience_exc;
    }

    public void setAudience_exc(String audience_exc) {
        this.audience_exc = audience_exc;
    }

    public String getAudience_package() {
        return audience_package;
    }

    public void setAudience_package(String audience_package) {
        this.audience_package = audience_package;
    }

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Targeting_profile that = (Targeting_profile) o;

		if (is_site_list_excluded != that.is_site_list_excluded) return false;
		if (is_category_list_excluded != that.is_category_list_excluded) return false;
		if (modified_by != that.modified_by) return false;
		if (created_on != that.created_on) return false;
		if (last_modified != that.last_modified) return false;
		if (tablet_targeting != that.tablet_targeting) return false;
		if (mma_inc_exc != that.mma_inc_exc) return false;
		if (adposition_inc_exc != that.adposition_inc_exc) return false;
		if (channel_inc_exc != that.channel_inc_exc) return false;
		if (user_id_inc_exc != that.user_id_inc_exc) return false;
		if (id != that.id) return false;
		if (lat_lon_radius_unit != that.lat_lon_radius_unit) return false;
		if (audience_targeting != that.audience_targeting) return false;
		if (audience_genderincl != that.audience_genderincl) return false;
		if (audience_agerangeinc != that.audience_agerangeinc) return false;
		if (audience_catinc != that.audience_catinc) return false;
		if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (account_guid != null ? !account_guid.equals(that.account_guid) : that.account_guid != null) return false;
		if (status_id != that.status_id) return false;
		if (brand_list != null ? !brand_list.equals(that.brand_list) : that.brand_list != null) return false;
		if (model_list != null ? !model_list.equals(that.model_list) : that.model_list != null) return false;
		if (os_json != null ? !os_json.equals(that.os_json) : that.os_json != null) return false;
		if (browser_json != null ? !browser_json.equals(that.browser_json) : that.browser_json != null) return false;
		if (country_json != null ? !country_json.equals(that.country_json) : that.country_json != null) return false;
		if (carrier_json != null ? !carrier_json.equals(that.carrier_json) : that.carrier_json != null) return false;
		if (state_json != null ? !state_json.equals(that.state_json) : that.state_json != null) return false;
		if (city_json != null ? !city_json.equals(that.city_json) : that.city_json != null) return false;
		if (zipcode_file_id_set != null ? !zipcode_file_id_set.equals(that.zipcode_file_id_set) : that.zipcode_file_id_set != null)
			return false;
		if (categories_tier_1_list != null ? !categories_tier_1_list.equals(that.categories_tier_1_list) : that.categories_tier_1_list != null)
			return false;
		if (categories_tier_2_list != null ? !categories_tier_2_list.equals(that.categories_tier_2_list) : that.categories_tier_2_list != null)
			return false;
		if (custom_ip_file_id_set != null ? !custom_ip_file_id_set.equals(that.custom_ip_file_id_set) : that.custom_ip_file_id_set != null)
			return false;
		if (supply_source_type != that.supply_source_type) return false;
		if (supply_source != that.supply_source) return false;
		if (geo_targeting_type != that.geo_targeting_type) return false;
		if (hours_list != null ? !hours_list.equals(that.hours_list) : that.hours_list != null) return false;
		if (midp != that.midp) return false;
		if (lat_long != null ? !lat_long.equals(that.lat_long) : that.lat_long != null) return false;
		if (pub_list != null ? !pub_list.equals(that.pub_list) : that.pub_list != null) return false;
		if (exchange_list != null ? !exchange_list.equals(that.exchange_list) : that.exchange_list != null)
			return false;
		if (site_list != null ? !site_list.equals(that.site_list) : that.site_list != null) return false;
		if (ext_supply_attributes != null ? !ext_supply_attributes.equals(that.ext_supply_attributes) : that.ext_supply_attributes != null)
			return false;
		if (direct_supply_inc_exc != null ? !direct_supply_inc_exc.equals(that.direct_supply_inc_exc) : that.direct_supply_inc_exc != null)
			return false;
		if (exchange_supply_inc_exc != null ? !exchange_supply_inc_exc.equals(that.exchange_supply_inc_exc) : that.exchange_supply_inc_exc != null)
			return false;
		if (connection_type_targeting_json != null ? !connection_type_targeting_json.equals(that.connection_type_targeting_json) : that.connection_type_targeting_json != null)
			return false;
		if (retargeting != null ? !retargeting.equals(that.retargeting) : that.retargeting != null) return false;
		if (supplyInclusionExclusion != null ? !supplyInclusionExclusion.equals(that.supplyInclusionExclusion) : that.supplyInclusionExclusion != null)
			return false;
		if (pmp_exchange != null ? !pmp_exchange.equals(that.pmp_exchange) : that.pmp_exchange != null) return false;
		if (pmp_dealid != null ? !pmp_dealid.equals(that.pmp_dealid) : that.pmp_dealid != null) return false;
		if (pmp_deal_json != null ? !pmp_deal_json.equals(that.pmp_deal_json) : that.pmp_deal_json != null)
			return false;
		if (device_type != null ? !device_type.equals(that.device_type) : that.device_type != null) return false;
		if (mma_tier_1_list != null ? !mma_tier_1_list.equals(that.mma_tier_1_list) : that.mma_tier_1_list != null)
			return false;
		if (mma_tier_2_list != null ? !mma_tier_2_list.equals(that.mma_tier_2_list) : that.mma_tier_2_list != null)
			return false;
		if (adposition_list != null ? !adposition_list.equals(that.adposition_list) : that.adposition_list != null)
			return false;
		if (channel_tier_1_list != null ? !channel_tier_1_list.equals(that.channel_tier_1_list) : that.channel_tier_1_list != null)
			return false;
		if (channel_tier_2_list != null ? !channel_tier_2_list.equals(that.channel_tier_2_list) : that.channel_tier_2_list != null)
			return false;
		if (lat_lon_radius_file != null ? !lat_lon_radius_file.equals(that.lat_lon_radius_file) : that.lat_lon_radius_file != null)
			return false;
		if (deviceid_file != null ? !deviceid_file.equals(that.deviceid_file) : that.deviceid_file != null)
			return false;
		if (file_prefix_path != null ? !file_prefix_path.equals(that.file_prefix_path) : that.file_prefix_path != null)
			return false;
		if (audience_gender != null ? !audience_gender.equals(that.audience_gender) : that.audience_gender != null)
			return false;
		if (audience_agerange != null ? !audience_agerange.equals(that.audience_agerange) : that.audience_agerange != null)
			return false;
		if (audience_tier1_cat != null ? !audience_tier1_cat.equals(that.audience_tier1_cat) : that.audience_tier1_cat != null)
			return false;
		if (audience_tier2_cat != null ? !audience_tier2_cat.equals(that.audience_tier2_cat) : that.audience_tier2_cat != null)
			return false;
		if (audience_tier3_cat != null ? !audience_tier3_cat.equals(that.audience_tier3_cat) : that.audience_tier3_cat != null)
			return false;
		if (audience_tier4_cat != null ? !audience_tier4_cat.equals(that.audience_tier4_cat) : that.audience_tier4_cat != null)
			return false;
		if (audience_tier5_cat != null ? !audience_tier5_cat.equals(that.audience_tier5_cat) : that.audience_tier5_cat != null)
			return false;
		return audience_tags != null ? audience_tags.equals(that.audience_tags) : that.audience_tags == null;
	}

	@Override
	public int hashCode() {
		int result = guid != null ? guid.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (account_guid != null ? account_guid.hashCode() : 0);
		result = 31 * result + (status_id != null ? status_id.hashCode() : 0);
		result = 31 * result + (brand_list != null ? brand_list.hashCode() : 0);
		result = 31 * result + (model_list != null ? model_list.hashCode() : 0);
		result = 31 * result + (os_json != null ? os_json.hashCode() : 0);
		result = 31 * result + (browser_json != null ? browser_json.hashCode() : 0);
		result = 31 * result + (country_json != null ? country_json.hashCode() : 0);
		result = 31 * result + (carrier_json != null ? carrier_json.hashCode() : 0);
		result = 31 * result + (state_json != null ? state_json.hashCode() : 0);
		result = 31 * result + (city_json != null ? city_json.hashCode() : 0);
		result = 31 * result + (zipcode_file_id_set != null ? zipcode_file_id_set.hashCode() : 0);
		result = 31 * result + (is_site_list_excluded ? 1 : 0);
		result = 31 * result + (categories_tier_1_list != null ? categories_tier_1_list.hashCode() : 0);
		result = 31 * result + (categories_tier_2_list != null ? categories_tier_2_list.hashCode() : 0);
		result = 31 * result + (is_category_list_excluded ? 1 : 0);
		result = 31 * result + (custom_ip_file_id_set != null ? custom_ip_file_id_set.hashCode() : 0);
		result = 31 * result + modified_by;
		result = 31 * result + (int) (created_on ^ (created_on >>> 32));
		result = 31 * result + (int) (last_modified ^ (last_modified >>> 32));
		result = 31 * result + (supply_source_type != null ? supply_source_type.hashCode() : 0);
		result = 31 * result + (supply_source != null ? supply_source.hashCode() : 0);
		result = 31 * result + (geo_targeting_type != null ? geo_targeting_type.hashCode() : 0);
		result = 31 * result + (hours_list != null ? hours_list.hashCode() : 0);
		result = 31 * result + (midp != null ? midp.hashCode() : 0);
		result = 31 * result + (lat_long != null ? lat_long.hashCode() : 0);
		result = 31 * result + (pub_list != null ? pub_list.hashCode() : 0);
		result = 31 * result + (exchange_list != null ? exchange_list.hashCode() : 0);
		result = 31 * result + (site_list != null ? site_list.hashCode() : 0);
		result = 31 * result + (ext_supply_attributes != null ? ext_supply_attributes.hashCode() : 0);
		result = 31 * result + (direct_supply_inc_exc != null ? direct_supply_inc_exc.hashCode() : 0);
		result = 31 * result + (exchange_supply_inc_exc != null ? exchange_supply_inc_exc.hashCode() : 0);
		result = 31 * result + (connection_type_targeting_json != null ? connection_type_targeting_json.hashCode() : 0);
		result = 31 * result + (tablet_targeting ? 1 : 0);
		result = 31 * result + (retargeting != null ? retargeting.hashCode() : 0);
		result = 31 * result + (supplyInclusionExclusion != null ? supplyInclusionExclusion.hashCode() : 0);
		result = 31 * result + (pmp_exchange != null ? pmp_exchange.hashCode() : 0);
		result = 31 * result + (pmp_dealid != null ? pmp_dealid.hashCode() : 0);
		result = 31 * result + (pmp_deal_json != null ? pmp_deal_json.hashCode() : 0);
		result = 31 * result + (device_type != null ? device_type.hashCode() : 0);
		result = 31 * result + (mma_inc_exc ? 1 : 0);
		result = 31 * result + (mma_tier_1_list != null ? mma_tier_1_list.hashCode() : 0);
		result = 31 * result + (mma_tier_2_list != null ? mma_tier_2_list.hashCode() : 0);
		result = 31 * result + (adposition_inc_exc ? 1 : 0);
		result = 31 * result + (adposition_list != null ? adposition_list.hashCode() : 0);
		result = 31 * result + (channel_inc_exc ? 1 : 0);
		result = 31 * result + (channel_tier_1_list != null ? channel_tier_1_list.hashCode() : 0);
		result = 31 * result + (channel_tier_2_list != null ? channel_tier_2_list.hashCode() : 0);
		result = 31 * result + (lat_lon_radius_file != null ? lat_lon_radius_file.hashCode() : 0);
		result = 31 * result + user_id_inc_exc;
		result = 31 * result + (deviceid_file != null ? deviceid_file.hashCode() : 0);
		result = 31 * result + (file_prefix_path != null ? file_prefix_path.hashCode() : 0);
		result = 31 * result + id;
		result = 31 * result + lat_lon_radius_unit;
		result = 31 * result + (audience_targeting ? 1 : 0);
		result = 31 * result + (audience_genderincl ? 1 : 0);
		result = 31 * result + (audience_gender != null ? audience_gender.hashCode() : 0);
		result = 31 * result + (audience_agerangeinc ? 1 : 0);
		result = 31 * result + (audience_agerange != null ? audience_agerange.hashCode() : 0);
		result = 31 * result + (audience_catinc ? 1 : 0);
		result = 31 * result + (audience_tier1_cat != null ? audience_tier1_cat.hashCode() : 0);
		result = 31 * result + (audience_tier2_cat != null ? audience_tier2_cat.hashCode() : 0);
		result = 31 * result + (audience_tier3_cat != null ? audience_tier3_cat.hashCode() : 0);
		result = 31 * result + (audience_tier4_cat != null ? audience_tier4_cat.hashCode() : 0);
		result = 31 * result + (audience_tier5_cat != null ? audience_tier5_cat.hashCode() : 0);
		result = 31 * result + (audience_tags != null ? audience_tags.hashCode() : 0);
		return result;
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

	public int getUser_id_inc_exc() {
		return user_id_inc_exc;
	}
	public void setUser_id_inc_exc(int user_id_inc_exc) {
		this.user_id_inc_exc = user_id_inc_exc;
	}
	public boolean isAudience_targeting() {
		return audience_targeting;
	}
	public void setAudience_targeting(boolean audience_targeting) {
		this.audience_targeting = audience_targeting;
	}
	public boolean isAudience_genderincl() {
		return audience_genderincl;
	}
	public void setAudience_genderincl(boolean audience_genderincl) {
		this.audience_genderincl = audience_genderincl;
	}
	public String getAudience_gender() {
		return audience_gender;
	}
	public void setAudience_gender(String audience_gender) {
		this.audience_gender = audience_gender;
	}
	public boolean isAudience_agerangeinc() {
		return audience_agerangeinc;
	}
	public void setAudience_agerangeinc(boolean audience_agerangeinc) {
		this.audience_agerangeinc = audience_agerangeinc;
	}
	public String getAudience_agerange() {
		return audience_agerange;
	}
	public void setAudience_agerange(String audience_agerange) {
		this.audience_agerange = audience_agerange;
	}
	public boolean isAudience_catinc() {
		return audience_catinc;
	}
	public void setAudience_catinc(boolean audience_catinc) {
		this.audience_catinc = audience_catinc;
	}
	public String getAudience_tier1_cat() {
		return audience_tier1_cat;
	}
	public void setAudience_tier1_cat(String audience_tier1_cat) {
		this.audience_tier1_cat = audience_tier1_cat;
	}
	public String getAudience_tier2_cat() {
		return audience_tier2_cat;
	}
	public void setAudience_tier2_cat(String audience_tier2_cat) {
		this.audience_tier2_cat = audience_tier2_cat;
	}
	public String getAudience_tier3_cat() {
		return audience_tier3_cat;
	}
	public void setAudience_tier3_cat(String audience_tier3_cat) {
		this.audience_tier3_cat = audience_tier3_cat;
	}
	public String getAudience_tier4_cat() {
		return audience_tier4_cat;
	}
	public void setAudience_tier4_cat(String audience_tier4_cat) {
		this.audience_tier4_cat = audience_tier4_cat;
	}
	public String getAudience_tier5_cat() {
		return audience_tier5_cat;
	}
	public void setAudience_tier5_cat(String audience_tier5_cat) {
		this.audience_tier5_cat = audience_tier5_cat;
	}
	public String getAudience_tags() {return this.audience_tags;}
	public void setAudience_tags(String audience_tags) {this.audience_tags = audience_tags;}

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
