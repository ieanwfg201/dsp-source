package com.kritter.kritterui.example.uiclient.tp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.Targeting_profile;

public class TPCreate {
    public static void cerateTP(String apiUrl) throws Exception{
        /* {"name":"chandan","guid":null,"status_id":"Active","modified_by":9,"created_on":0,"last_modified":0,"supply_source_type":"APP_WAP","account_guid":"010a4137-6a5c-6201-4cc2-415c26000044","categories_tier_1_list":"[1,46]","categories_tier_2_list":"[2,6]","brand_list":"[1020,705,1020,705]","model_list":"[10684,10684,17455]","os_json":"{\"1\":\"all-all\",\"15\":\"all-all\",\"7\":\"all-all\"}","browser_json":"{\"50\":\"all-all\",\"9\":\"all-all\",\"27\":\"all-all\",\"4\":\"all-all\"}","zipcode_file_id_set":null,"site_list":"2|1","is_site_list_excluded":false,"is_category_list_excluded":false,"custom_ip_file_id_set":"","supply_source":"NETWORK","country_json":"[5,173,208]","carrier_json":"[224,224,224,704,704,432,432,432,821,821,821]","state_json":"[]","city_json":"[]","geo_targeting_type":"COUNTRY_CARRIER","hours_list":"[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23]","midp":"ALL","pub_list":"[2]","ext_supply_attributes":"[]","lat_long":"[{}]","direct_supply_inc_exc":"[]","exchange_supply_inc_exc":"[]","exchange_list":"[]","connection_type_targeting_json":"[1]","tablet_targeting":false,"retargeting":"[]","supplyInclusionExclusion":null,"pmp_exchange":"","pmp_dealid":"","pmp_deal_json":"{}","device_type":"[]"}
*/
        Targeting_profile tp = new Targeting_profile();
        tp.setName("XXX11X");
        tp.setAccount_guid("010a4137-6a5c-6201-4cc2-415c26000044");
        tp.setModified_by(1);
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

    }
    
    public static void main(String args[]) throws Exception{
        TPCreate.cerateTP("http://52.34.139.130:9000/api/v1/targetingprofile/create");
    }


}
