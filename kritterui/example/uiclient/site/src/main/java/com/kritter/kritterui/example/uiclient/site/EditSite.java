package com.kritter.kritterui.example.uiclient.site;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.site.Site;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class EditSite {
	/**
	 * Input  
	 * com.kritter.api.entity.site.Site  (Json String in Post Body)
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"63"}   {Where id is the unique internal id of site }
	 */
    public static Message editSite(String apiUrl) throws Exception{
    	/**
    	 * First Get Site
    	 */
    	Site site = GetSiteById.getSiteById(ExampleConstant.url_prefix+ExampleConstant.get_site_by_id);
    	
        if(site != null){
        	site.setName("ChangingSiteName");
        	URL url = new URL(apiUrl);
        	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        	conn.setRequestMethod("POST");
        	conn.setRequestProperty("Content-Type", "application/json");
        	conn.setDoOutput(true);
        	ObjectMapper mapper = new ObjectMapper();
        	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        	mapper.writeValue(wr, site.toJson());
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
        }else{
        	System.out.println("Site Not Found");
        	return null;
        }
    }
    
    public static void main(String args[]) throws Exception{
        EditSite.editSite(ExampleConstant.url_prefix+ExampleConstant.edit_site);
    }
}
