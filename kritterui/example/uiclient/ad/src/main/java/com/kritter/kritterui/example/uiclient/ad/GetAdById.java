package com.kritter.kritterui.example.uiclient.ad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetAdById {
	/**
	 * Input 
	 * ccom.kritter.api.entity.ad.AdListEntity  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.ad.AdList Json String
	 */
    public static Ad getAdById(String apiUrl) throws Exception{
    	AdListEntity ale = new AdListEntity();
    	ale.setId(69); /*Ad Id*/
    	/**
    	 * Not Required
    	ale.setAdenum(adenum);
    	ale.setCampaign_id(campaign_id);
    	ale.setId_list(id_list);
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
        AdList adList = AdList.getObject(response.toString());
        System.out.println(adList.toJson());
        if(adList != null && adList.getAdlist() != null && adList.getAdlist().size()>0){
        	return adList.getAdlist().get(0);
        }else{
        	System.out.println("Ad Not Found");
        }
        return null;
    }
    
    public static void main(String args[]) throws Exception{
        GetAdById.getAdById(ExampleConstant.url_prefix+ExampleConstant.get_ad);
    }
}
