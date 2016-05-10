package com.kritter.kritterui.example.uiclient.account;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Account_Type;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.Payment_type;
import com.kritter.constants.StatusIdEnum;

public class CreatePubAccount {
    public static void createPublisher(String apiUrl) throws Exception{
        Account account = new Account();
        account.setAddress("Address");
        account.setCity("CITY");
        account.setCompany_name("CompanyName");
        account.setCountry("India");
        account.setEmail("a@n.com");
        account.setIm("im");
        account.setInventory_source(INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode());
        account.setModified_by(1);
        account.setName("name");
        account.setPassword(BCrypt.hashpw("lalala", BCrypt.gensalt()));
        account.setPayment_type(Payment_type.NothingSelected);
        account.setPhone("99999");
        account.setStatus(StatusIdEnum.Pending);
        account.setType_id(Account_Type.directpublisher);
        account.setUserid("userid");
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

    }
    
    public static void main(String args[]) throws Exception{
        CreatePubAccount.createPublisher("http://104.130.4.73:9000/api/v1/account/pub/create");
    }
}
