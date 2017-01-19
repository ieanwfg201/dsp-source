package com.kritter.kritterui.example.uiclient.tp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class EditTargetingProfile {
	/**
	 * Input 
	 * com.kritter.api.entity.targeting_profile.Targeting_profile (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 */
    public static Message editTP(String apiUrl) throws Exception{
    	/*Get TP first*/
    	Targeting_profile tp = GetTargetingProfileById.getTP(ExampleConstant.url_prefix+ExampleConstant.get_targetingprofile_byid);;
    	tp.setName("Changing Name");
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
        EditTargetingProfile.editTP(ExampleConstant.url_prefix+ExampleConstant.edit_targetingprofile);
    }
}
