package com.kritter.kritterui.example.uiclient.campaign_budget;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.campaign.GetCampaignById;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateCampaignBudget {
	/**
	 * Input 
	 * com.kritter.api.entity.campaign_budget.Campaign_budget  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"52"}   {Where id is the unique internal id of campaign }
	 * 
	 * NOTE: Campaign Budget should always be created when a campaign is created\
	 * also campaign budget should not be created again if campaign budget has already been created for a
	 * campaign (under such a scenario campoaign budget should only be edited)
	 * 
	 */
    public static Message createCampaignBudget(String apiUrl) throws Exception{
        Campaign campaign = GetCampaignById.getCampaignById(ExampleConstant.url_prefix+ExampleConstant.get_campaign);

    	Campaign_budget cb = new Campaign_budget();
    	cb.setCampaign_guid(campaign.getGuid());
    	cb.setCampaign_id(campaign.getId());
    	cb.setAdv_daily_budget(30);
    	cb.setAdv_daily_burn(0);
    	cb.setAdv_total_budget(100);
    	cb.setAdv_total_burn(0);
    	cb.setInternal_daily_budget(30);
    	cb.setInternal_daily_burn(0);
    	cb.setInternal_total_budget(100);
    	cb.setInternal_total_burn(0);
    	cb.setModified_by(1); /*Ask for this */
    	/**
    	 * Not Requires Auto populated
    	cb.setLast_modified(last_modified);
    	cb.setCreated_on(created_on);
    	 */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, cb.toJson());
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
        CreateCampaignBudget.createCampaignBudget(ExampleConstant.url_prefix+ExampleConstant.create_campaignbudget);
    }
}
