package com.kritter.kritterui.example.uiclient.pub;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetPubAccount {
	
	/**
	 * Input
	 * com.kritter.api.entity.account.Account  (Json String in Post Body)
	 * Output
	 * com.kritter.api.entity.account.AccountMsgPair Json String
	 * 
	 * @param apiUrl
	 * @throws Exception
	 */
    public static AccountMsgPair getPublisher(String apiUrl) throws Exception{
        Account account = new Account();
        account.setPassword("mypassword");
        account.setUserid("uniqueuseruid1");
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, account.toJson());
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
        AccountMsgPair msg = AccountMsgPair.getObject(response.toString());
        
        System.out.println(msg.toJson());
        return msg;
    }
    
    public static void main(String args[]) throws Exception{
        GetPubAccount.getPublisher(ExampleConstant.url_prefix+ExampleConstant.get_pub);
    }


}
