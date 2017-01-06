package com.kritter.kritterui.example.uiclient.campaign;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class PauseCampaign {
	/**
	 * Input 
	 * com.kritter.api.entity.campaign.Campaign  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"52"}   {Where id is the unique internal id of campaign }
	 * 
	 * NOTE: Get Campaign First
	 */
    public static Message pauseCampaign(String apiUrl) throws Exception{
        Campaign campaign = GetCampaignById.getCampaignById(ExampleConstant.url_prefix+ExampleConstant.get_campaign);
        /** Required */
    	campaign.setModified_by(1); /*Ask for this*/
    	/**
    	 *NOT REQUIRED
    	campaign.setStart_date(startDate.getTime());long form of date. Take care of TZ
    	campaign.setEnd_date(endDate.getTime()); long form of date. Take care of TZ
    	campaign.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001"); GUID of account - can be obtained by GetAdvAccount
    	campaign.setAccount_id(71); Id of account - can be obtained by GetAdvAccount
    	campaign.setIs_frequency_capped(true);
    	campaign.setClick_freq_cap(true);
    	campaign.setClick_freq_cap_count(10);
    	campaign.setClick_freq_cap_type(FreqDuration.BYHOUR.getCode());
    	campaign.setClick_freq_time_window(24); If above BYHOUR then 1-48, BYDAY then 24, LIFE then -1
    	campaign.setImp_freq_cap(true);
    	campaign.setImp_freq_cap_count(10);
    	campaign.setImp_freq_cap_type(FreqDuration.BYHOUR.getCode());
    	campaign.setImp_freq_time_window(10); If above BYHOUR then 1-48, BYDAY then 24, LIFE then -1
    	campaign.setLast_modified(last_modified);
    	campaign.setGuid(guid);
    	campaign.setId(id);
    	campaign.setCreated_on(created_on);
    	campaign.setStatus_id(status_id);
    	 */
    	
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, campaign.toJson());
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
        PauseCampaign.pauseCampaign(ExampleConstant.url_prefix+ExampleConstant.pause_campaign);
    }
}
