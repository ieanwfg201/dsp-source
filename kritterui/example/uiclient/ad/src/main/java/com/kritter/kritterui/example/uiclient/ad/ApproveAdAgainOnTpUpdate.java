package com.kritter.kritterui.example.uiclient.ad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class ApproveAdAgainOnTpUpdate {
	/**
	 * Input 
	 * ccom.kritter.api.entity.ad.AdListEntity  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 */
    public static Message approve_ad_again_on_tp_update(String apiUrl) throws Exception{
    	AdListEntity ale = new AdListEntity();
    	ale.setId_list("011c3e84-ed0b-cb01-580a-a46953000008"); /*Targeting Guid */
    	/**
    	 * Not Required
    	ale.setAdenum(adenum);
    	ale.setCampaign_id(campaign_id);
    	ale.setId(69);
    	ale.setPage_no(page_no);
    	ale.setPage_size(page_size);
    	ale.setStatudIdEnum(statudIdEnum);
    	*/
    	URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, ale.toJson());
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
        ApproveAdAgainOnTpUpdate.approve_ad_again_on_tp_update(ExampleConstant.url_prefix+ExampleConstant.sendadforapproval);
    }
}
