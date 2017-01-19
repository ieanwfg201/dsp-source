package com.kritter.kritterui.example.uiclient.ad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class EditAd {
	/**
	 * Input 
	 * com.kritter.api.entity.ad.Ad  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"69"}
	 */
    public static Message createAd(String apiUrl) throws Exception{
    	/*Get Ad first*/
    	Ad ad = GetAdById.getAdById(ExampleConstant.url_prefix+ExampleConstant.get_ad);
    	ad.setName("Changingadname");
    	URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, ad.toJson());
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
        EditAd.createAd(ExampleConstant.url_prefix+ExampleConstant.edit_ad);
    }
}
