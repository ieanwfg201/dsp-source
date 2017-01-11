package com.kritter.kritterui.example.uiclient.tp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetTargetingProfileById {
	/**
	 * Input 
	 * com.kritter.api.entity.targeting_profile.TargetingProfileListEntity(Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.targeting_profile.TargetingProfileList Json String
	 */
    public static Targeting_profile getTP(String apiUrl) throws Exception{
    	TargetingProfileListEntity tple = new TargetingProfileListEntity();
    	tple.setId(88);
    	/**
    	 * Not Required
    	tple.setAccount_guid(account_guid);
    	tple.setGuid(guid);
    	tple.setTpEnum(tpEnum);
    	 */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, tple.toJson());
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
        TargetingProfileList tpl = TargetingProfileList.getObject(response.toString());
        System.out.println(tpl.toJson());
        if(tpl != null && tpl.getTplist()!= null && tpl.getTplist().size()>0){
        	return tpl.getTplist().get(0);
        }
        System.out.println("TP Not Found");
        return null;
    }
    
    public static void main(String args[]) throws Exception{
        GetTargetingProfileById.getTP(ExampleConstant.url_prefix+ExampleConstant.get_targetingprofile_byid);
    }
}
