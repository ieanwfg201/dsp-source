package com.kritter.kritterui.example.uiclient.campaign;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class ListAllExpiredCampaignOfAccount {
	/**
	 * Input 
	 * com.kritter.api.entity.campaign.CampaignListEntity  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.campaign.CampaignList Json String
	 */
    public static CampaignList getAllExpiredCampaignOsAccount(String apiUrl) throws Exception{
    	CampaignListEntity cle = new CampaignListEntity();
    	cle.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");
    	/**
    	 * Optional
    	cle.setPage_no(page_no);
    	cle.setPage_size(page_size);
    	 */
    	/**
    	 * Not Required
    	cle.setCampaignQueryEnum(campaignQueryEnum);
    	cle.setId_list(id_list);
    	cle.setStatusIdEnum(statusIdEnum);
    	cle.setCampaign_id(52); 
    	 */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, cle.toJson());
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
        CampaignList cl = CampaignList.getObject(response.toString());
        System.out.println(cl.toJson());
        return cl;
    }
    
    public static void main(String args[]) throws Exception{
        ListAllExpiredCampaignOfAccount.getAllExpiredCampaignOsAccount(ExampleConstant.url_prefix+ExampleConstant.expiredlist_campaign);
    }
}
