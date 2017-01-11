package com.kritter.kritterui.example.uiclient.tp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateTargetingProfile {
	/**
	 * Input 
	 * com.kritter.api.entity.targeting_profile.Targeting_profile (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"88"}  
	 */
    public static Message createTP(String apiUrl) throws Exception{
    	/*Creating simple TP with only Site/Pub Inclusion Exclusion*/
    	Targeting_profile tp = new Targeting_profile();
    	tp.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001"); /*Account Guid*/
    	tp.setName("targetingname");
    	tp.setIs_site_list_excluded(true); /*true if excluded*/
    	tp.setPub_list("[]"); /*Json Array - Ids to be populated*/
        /*Above can be obtained by /api/v1/meta/firstlevel/directpublishers  */
    	tp.setSite_list("[]");/*Json Array - Ids to be populated*/
        /*Above can be obtained by /api/v1/meta/secondlevel/targeting_directsitesByPublishers/1,2   where 1,2 aur pub ids selected
         * in pub list*/

    	tp.setModified_by(1);/*Ask for This*/
    	tp.setStatus_id(StatusIdEnum.Active);
        /*Above can also be obtained by /api/v1/meta/firstlevel/statusids  */
    	/**Not Requiredd Auto Popularted
    	tp.setGuid(guid);
    	tp.setId(id);
    	tp.setLast_modified(last_modified);
    	tp.setCreated_on(created_on);
    	 */
    	/**Not Requiredd
    	 *     	tp.setDirect_supply_inc_exc(direct_supply_inc_exc);
    	tp.setExchange_list(exchange_list);
    	tp.setExchange_supply_inc_exc(exchange_supply_inc_exc);
    	tp.setSupplyInclusionExclusion(supplyInclusionExclusion);
    	tp.setIs_category_list_excluded(is_category_list_excluded);
    	tp.setHours_list(hours_list);
    	tp.setFile_prefix_path(file_prefix_path);
    	tp.setGeo_targeting_type(geo_targeting_type);
    	tp.setExt_supply_attributes(ext_supply_attributes);
    	tp.setCustom_ip_file_id_set(custom_ip_file_id_set);
    	tp.setDevice_type(device_type);
    	tp.setDeviceid_file(deviceid_file);
    	tp.setAdposition_inc_exc(adposition_inc_exc);
    	tp.setAdposition_list(adposition_list);
    	tp.setBrand_list(brand_list);
    	tp.setBrowser_json(browser_json);
    	tp.setCarrier_json(carrier_json);
    	tp.setCategories_tier_1_list(categories_tier_1_list);
    	tp.setChannel_inc_exc(channel_inc_exc);
    	tp.setChannel_tier_1_list(channel_tier_1_list);
    	tp.setChannel_tier_2_list(channel_tier_2_list);
    	tp.setCity_json(city_json);
    	tp.setConnection_type_targeting_json(connection_type_targeting_json);
    	tp.setCountry_json(country_json);
    	tp.setLat_lon_radius_file(lat_lon_radius_file);
    	tp.setLat_lon_radius_unit(lat_lon_radius_unit);
    	tp.setLat_long(lat_long);
    	tp.setMidp(midp);
    	tp.setMma_inc_exc(mma_inc_exc);
    	tp.setMma_tier_1_list(mma_tier_1_list);
    	tp.setMma_tier_2_list(mma_tier_2_list);
    	tp.setModel_list(model_list);
    	tp.setOs_json(os_json);
    	tp.setPmp_deal_json(pmp_deal_json);
    	tp.setPmp_dealid(pmp_dealid);
    	tp.setPmp_exchange(pmp_exchange);
    	tp.setRetargeting(retargeting);
    	tp.setState_json(state_json);
    	tp.setSupply_source_type(supply_source_type);
    	tp.setSupply_source(supply_source);
    	tp.setTablet_targeting(tablet_targeting);
    	tp.setUser_id_inc_exc(user_id_inc_exc);
    	tp.setZipcode_file_id_set(zipcode_file_id_set);
    	 * 
    	 */
    	
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, tp.toJson());
        wr.flush();
        wr.close();
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Message msg = Message.getObject(response.toString());
        
        System.out.println(msg.toJson());
        return msg;
    }
    
    public static void main(String args[]) throws Exception{
        CreateTargetingProfile.createTP(ExampleConstant.url_prefix+ExampleConstant.create_targetingprofile);
    }
}
