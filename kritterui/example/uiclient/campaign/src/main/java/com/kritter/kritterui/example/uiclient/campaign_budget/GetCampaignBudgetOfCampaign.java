package com.kritter.kritterui.example.uiclient.campaign_budget;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign_budget.CampaignBudgetList;
import com.kritter.api.entity.campaign_budget.CampaignBudgetListEntity;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.kritterui.example.uiclient.campaign.GetCampaignById;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetCampaignBudgetOfCampaign {
	/**
	 * Input 
	 * com.kritter.api.entity.campaign_budget.CampaignBudgetListEntity  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.campaign_budget.CampaignBudgetList Json String
	 * 
	 */
    public static Campaign_budget getCamapignBudgetOfCampaign(String apiUrl) throws Exception{
        Campaign campaign = GetCampaignById.getCampaignById(ExampleConstant.url_prefix+ExampleConstant.get_campaign);

        CampaignBudgetListEntity cb = new CampaignBudgetListEntity();
    	cb.setCampaign_id(campaign.getId());
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
        CampaignBudgetList cl = CampaignBudgetList.getObject(response.toString());
        System.out.println(cl.toJson());
        if(cl != null && cl.getCampaig_budget_list()!=null && cl.getCampaig_budget_list().size()>0){
        	return cl.getCampaig_budget_list().get(0);
        }
        return null;
    }
    
    public static void main(String args[]) throws Exception{
        GetCampaignBudgetOfCampaign.getCamapignBudgetOfCampaign(ExampleConstant.url_prefix+ExampleConstant.get_campaignbudget);
    }
}
