package com.kritter.kritterui.example.uiclient.adv;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class EditAdvAccount {
	
	/**
	 *	Input
	 * com.kritter.api.entity.account.Account  (Json String in Post Body)

	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * 
	`	MOTE: Gret account first
	 */
    public static void editAdvertiser(String apiUrl) throws Exception{
        Account getaccount = new Account();
        getaccount.setPassword("mypassword");
        getaccount.setUserid("uniqueuseruid1");
        /**
         * First get the account Object
         */
        AccountMsgPair msgPair=GetAdvAccount.getAdvertiser(ExampleConstant.url_prefix+ExampleConstant.get_adv);
        if(msgPair != null && msgPair.getAccount() != null && msgPair.getMsg() != null
        		&& msgPair.getMsg().getError_code()==0){
        	Account editAccount= msgPair.getAccount() ;
        	editAccount.setName("AdvChangedName");
        	editAccount.setModified_by(1);/*Ask for this ID*/
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            mapper.writeValue(wr, editAccount.toJson());
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

        }else{
        	System.out.println("Acoount Not Found");
        }

    }
    
    public static void main(String args[]) throws Exception{
        EditAdvAccount.editAdvertiser(ExampleConstant.url_prefix+ExampleConstant.edit_adv);
    }


}
