package com.kritter.kritterui.example.uiclient.campaign;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetCampaignById {
	/**
	 * Input 
	 * com.kritter.api.entity.campaign.CampaignListEntity  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.campaign.CampaignList Json String
	 */
    public static Campaign getCampaignById(String apiUrl) throws Exception{
    	CampaignListEntity cle = new CampaignListEntity();
    	cle.setCampaign_id(52); /*id of campaign*/
    	
    	/**
    	 * Not Required
    	cle.setCampaignQueryEnum(campaignQueryEnum);
    	cle.setId_list(id_list);
    	cle.setStatusIdEnum(statusIdEnum);
    	cle.setAccount_guid(account_guid);
    	cle.setPage_no(page_no);
    	cle.setPage_size(page_size);
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
        if(cl != null && cl.getCampaign_list() != null && cl.getCampaign_list().size()>0){
        	return cl.getCampaign_list().get(0);
        }else{
        	System.out.println("Campaign Not Found");
        }
        return null;
    }
    
    public static void main(String args[]) throws Exception{
        GetCampaignById.getCampaignById(ExampleConstant.url_prefix+ExampleConstant.get_campaign);
    }
}
