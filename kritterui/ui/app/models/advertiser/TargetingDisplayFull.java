package models.advertiser;

import java.util.LinkedList;
import java.util.List;

import scala.Option;
import services.MetadataAPI;
import services.TPMetadataAPI;

import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.Geo_Targeting_type;
import com.kritter.constants.InclusionExclusionType;
import com.kritter.constants.LatLonRadiusUnit;
import com.kritter.constants.MetadataType;

import controllers.routes;

public class TargetingDisplayFull extends TargetingDisplay{

    public TargetingDisplayFull(Targeting_profile tp){
        super(tp);
    }

    public String getGuid(){
        return tp.getGuid();
    }

    public String getStatusIdName(){
        return tp.getStatus_id().getName();
    }
    public String getHourList(){
        return tp.getHours_list();
    }

    public String getGeo_targeting_type_name(){
        return tp.getGeo_targeting_type().getName();
    }
    public List<String> getConnectionType(){
        return TPMetadataAPI.getConnectionTypeValues(tp.getConnection_type_targeting_json());
    }
    public List<String> getCountryValues(){
        return TPMetadataAPI.getCountryValues(tp.getCountry_json());
    }
    public List<String> getStateValues(){
        return TPMetadataAPI.getStateValues(tp.getState_json());
    }
    public List<String> getCityValues(){
        return TPMetadataAPI.getCityValues(tp.getCity_json());
    }
    public List<String> getCarrierValues(){
        return TPMetadataAPI.getCarrierValues(tp.getCarrier_json());
    }
    public String getCustomIpSetString(){
        if(tp.getGeo_targeting_type()==Geo_Targeting_type.IP){
            return routes.StaticFileController.download(Option.apply(tp.getCustom_ip_file_id_set())).url();
        }else{
            return "";
        }
    }
    public String getCustomZipCodeSetString(){
        if(tp.getGeo_targeting_type()==Geo_Targeting_type.ZIPCODE){
            return routes.StaticFileController.download(Option.apply(tp.getZipcode_file_id_set())).url();
        }else{
            return "";
        }
    }
    public List<String> getBrowserValues(){
        return TPMetadataAPI.getBrowserValues(tp.getBrowser_json());
    }
    public List<String> getOsValues(){
        return TPMetadataAPI.getOsValues(tp.getOs_json());
    }
    public List<String> getDeviceType(){
        return TPMetadataAPI.getDeviceTypeValues(tp.getDevice_type());
    }
    public List<String> getBrandValues(){
        return TPMetadataAPI.getBrandValues(tp.getBrand_list());
    }
    public List<String> getModelValues(){
        return TPMetadataAPI.getModelValues(tp.getModel_list());
    }
    public String getSite_list_excluded(){
        if(tp.isIs_site_list_excluded()){
            return "Exclusion";
        }
        return "Inclusion";
    }
    public List<String> getSiteValues(){
        String str = tp.getSite_list().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
        String strSplit[] = str.split(",");
        boolean isFirst=true;
        StringBuffer sbuuf = new StringBuffer("");
        for(String element:strSplit){
            String elementSplit[] = element.split("\\|");
            if(elementSplit.length>1){
                if(isFirst){
                    isFirst=false;
                }else{
                    sbuuf.append(",");
                }
                sbuuf.append(elementSplit[1]);
            }
        }

        return TPMetadataAPI.getSiteValues(sbuuf.toString());
    }
    public List<String> getExtSiteValues(){
        return TPMetadataAPI.getExtSiteValues(tp.getExt_supply_attributes());
    }
    public List<String> getPubValues(){
        return TPMetadataAPI.getPubValues(tp.getPub_list());
    }
    public List<String> getExchangeValues(){
        return TPMetadataAPI.getPubValues(tp.getExchange_list());
    }
    public String getSupply_source_type_name(){
        return tp.getSupply_source_type().getName();
    }
    public String getSupply_source_name(){
        return tp.getSupply_source().getName();
    }
    public String getCategory_list_excluded(){
        if(tp.isIs_category_list_excluded()){
            return "Exclusion";
        }
        return "Inclusion";
    }
    public List<String> getCategories_tier_1_list(){ 
        return MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, tp.getCategories_tier_1_list());
    }
    public List<String> getCategories_tier_2_list(){ 
        return MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, tp.getCategories_tier_2_list());
    }
    public List<String> getLatLong(){
        List<String> ll = new LinkedList<String>();
        String[] split = tp.getLat_long().split("\n");
        for(String str:split){
            ll.add(str);
        }
        return ll;
    }
    public String getTablet_targeting(){
        return ""+tp.isTablet_targeting();
    }
    public List<String> getRetargetingSegment(){ 
        return TPMetadataAPI.getRetargetingSegmentValues(tp.getRetargeting());
    }
    public List<String> getPmpExchangeValues(){
        return TPMetadataAPI.getPubGuidValues(tp.getPmp_exchange());
    }
    public String getPmpDeal(){
        return tp.getPmp_dealid();
    }
    public String mmaIncExc(){
    	if(tp.isMma_inc_exc()){
    		return "Inclusion";
    	}
    	return "Exclusion";
    }
	public List<String> getMMATier1(){ 
		return MetadataAPI.getValues(MetadataType.MMA_CATEGORY_BY_IDS, tp.getMma_tier_1_list());
	}
    public List<String> getMMATier2(){ 
        return MetadataAPI.getValues(MetadataType.MMA_CATEGORY_BY_IDS, tp.getMma_tier_2_list());
    }
    public String adposIncExc(){
    	if(tp.isAdposition_inc_exc()){
    		return "Inclusion";
    	}
    	return "Exclusion";
    }
	public List<String> getAdpos(){ 
		return MetadataAPI.getValues(MetadataType.ADPOS_BY_IDS, tp.getAdposition_list());
	}
    public String channelIncExc(){
    	if(tp.isChannel_inc_exc()){
    		return "Inclusion";
    	}
    	return "Exclusion";
    }
	public List<String> getChannelTier1(){ 
		return MetadataAPI.getValues(MetadataType.CHANNEL_BY_IDS, tp.getChannel_tier_1_list());
	}
	public List<String> getChannelTier2(){ 
		return MetadataAPI.getValues(MetadataType.CHANNEL_BY_IDS, tp.getChannel_tier_2_list());
	}
    public String getLatLonRadiusFile(){
    	String s = tp.getLat_lon_radius_file();
    	if(s != null){
    		String sTrim = s.trim();
    		if(!"".equals(sTrim)){
    			return routes.StaticFileController.download(Option.apply(tp.getLat_lon_radius_file())).url();
    		}
    	}
    	return "";
    }
    public String deviceIdIncExc(){
    	InclusionExclusionType ie = InclusionExclusionType.getEnum(tp.getUser_id_inc_exc());
    	if(ie!=null){
    		return ie.name();
    	}
    	return "None";
    }

    public String getDeviceidfile(){
    	String s = tp.getDeviceid_file();
    	if(s != null){
    		String sTrim = s.trim();
    		if(!"".equals(sTrim)){
    			return routes.StaticFileController.download(Option.apply(s)).url();
    		}
    	}
    	return "";
    }
    public String getLatLonRadiusUnit(){
    	LatLonRadiusUnit llru=LatLonRadiusUnit.getEnum(tp.getLat_lon_radius_unit());
    	if(llru != null){
    		return llru.getName();
    	}
    	return "";
    }
}
