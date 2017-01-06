package com.kritter.kritterui.example.uiclient.campaign;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateCampaign {
	/**
	 * Input 
	 * com.kritter.api.entity.campaign.Campaign  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"52"}   {Where id is the unique internal id of campaign }
	 * 
	 * NOTE: Campaign Budget should always be created when a campaign is created
	 */
    public static Message createCampaignUnderAdvertiser(String apiUrl) throws Exception{
        Campaign campaign = new Campaign();
        /** Required */
    	campaign.setName("RelevantCampaignNamee");
    	campaign.setModified_by(1); /*Ask for this*/
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date startDate = (Date)formatter.parse("2016-10-26 00:00:00"); 	
    	campaign.setStart_date(startDate.getTime());/*long form of date. Take care of TZ*/
    	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date endDate = (Date)formatter1.parse("2016-11-26 00:00:00"); 	
    	campaign.setEnd_date(endDate.getTime());/*long form of date. Take care of TZ*/
    	campaign.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001"); /*GUID of account - can be obtained by GetAdvAccount*/
    	campaign.setAccount_id(71); /*Id of account - can be obtained by GetAdvAccount*/
    	/**
    	 * Optional 
    	 * if freq cap enabled then set
    	campaign.setIs_frequency_capped(true);
    	Below if click freq cap
    	campaign.setClick_freq_cap(true);
    	campaign.setClick_freq_cap_count(10);
    	campaign.setClick_freq_cap_type(FreqDuration.BYHOUR.getCode());
        Above can also be obtained by /api/v1/meta/firstlevel/freqduration  
    	campaign.setClick_freq_time_window(24); If above BYHOUR then 1-48, BYDAY then 24, LIFE then -1
    	Below if imp freq cap
    	campaign.setImp_freq_cap(true);
    	campaign.setImp_freq_cap_count(10);
    	campaign.setImp_freq_cap_type(FreqDuration.BYHOUR.getCode());
        Above can also be obtained by /api/v1/meta/firstlevel/freqduration  
    	campaign.setImp_freq_time_window(10); If above BYHOUR then 1-48, BYDAY then 24, LIFE then -1
    	 */
    	/**
    	 * Not Required
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
        CreateCampaign.createCampaignUnderAdvertiser(ExampleConstant.url_prefix+ExampleConstant.create_campaign);
    }
}
