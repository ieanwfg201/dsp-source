package com.kritter.kritterui.example.uiclient.account;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.account.AccountList;
import com.kritter.api.entity.account.ListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class ListAccountByEmail {
	
	/**
	 * Input
	 * com.kritter.api.entity.account.ListEntity  (Json String in Post Body)
	 * Output
	 * com.kritter.api.entity.account.AccountList Json String
	 * 
	 * @param apiUrl
	 * @throws Exception
	 */
    public static AccountList listAccountbyEmail(String apiUrl) throws Exception{
    	ListEntity entity = new ListEntity();
    	entity.setEmail("chaharv@gmail.com");
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, entity.toJson());
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
        AccountList msg = AccountList.getObject(response.toString());
        
        System.out.println(msg.toJson());
        return msg;
    }
    
    public static void main(String args[]) throws Exception{
        ListAccountByEmail.listAccountbyEmail(ExampleConstant.url_prefix+ExampleConstant.list_account_by_email);
    }


}
