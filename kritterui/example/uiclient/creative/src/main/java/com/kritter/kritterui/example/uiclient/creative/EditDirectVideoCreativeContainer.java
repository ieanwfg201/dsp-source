package com.kritter.kritterui.example.uiclient.creative;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class EditDirectVideoCreativeContainer {
	/**
	 * Input 
	 * com.kritter.api.entity.creative_container.Creative_container  (Json String)
	 * Output
	 * com.kritter.api.entity.response.msg.Message (Json String)
	 */
    public static Message createDirectVideoCC(String apiUrl) throws Exception{
    	/*Get Creative Container first*/
    	Creative_container cc = GetCreativeContainer.geCC(ExampleConstant.url_prefix+ExampleConstant.get_creativecontainer);
    	cc.setLabel("ChangingLabel");
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, cc.toJson());
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
        EditDirectVideoCreativeContainer.createDirectVideoCC(ExampleConstant.url_prefix+ExampleConstant.edit_creativecontainer);
    }
}
