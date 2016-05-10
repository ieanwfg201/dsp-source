package com.kritter.kritterui.example.uiclient.account;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;

public class CreativeContainer {

    public static void getOrVerifyPublisher(String apiUrl) throws Exception{
        CreativeContainerListEntity cli = new CreativeContainerListEntity();
        cli.setAccount_guid("010a4137-6a5c-6201-52d0-6d6574000077");
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, cli.toJson());
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
        CreativeContainerList msg = CreativeContainerList.getObject(response.toString());
        
        System.out.println(msg.toJson());

    }
    
    public static void main(String args[]) throws Exception{
        CreativeContainer.getOrVerifyPublisher("http://52.34.139.130:9000/api/v1/creativecontainer/list");
    }


}
