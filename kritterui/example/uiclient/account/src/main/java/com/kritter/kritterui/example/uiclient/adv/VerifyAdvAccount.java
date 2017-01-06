package com.kritter.kritterui.example.uiclient.adv;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class VerifyAdvAccount {
	/**
	 * Input 
	 * com.kritter.api.entity.account.Account  (Json String in Post Body)

	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"Email Id Already Present","error_code":61,"id":null}
	 * Not Present E.G
	 * {"msg":"OK","error_code":0,"id":null}
	 * @param apiUrl
	 * @throws Exception
	 */
    public static Message verifyAdvAccountByUserIdOrEmail(String apiUrl) throws Exception{
        Account account = new Account();
        /**
         * Below Required
         */
        account.setEmail("uniqueadvemail1@email.com");
        account.setUserid("uniqueadvid1");
        
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
        Message msg = Message.getObject(response.toString());
        
        System.out.println(msg.toJson());
        return msg;
    }
    
    public static void main(String args[]) throws Exception{
        VerifyAdvAccount.verifyAdvAccountByUserIdOrEmail(ExampleConstant.url_prefix+ExampleConstant.verify_adv);
    }
}
