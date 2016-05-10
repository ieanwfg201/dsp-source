package com.kritter.kritterui.example.uiclient.banner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.creative_banner.CreativeBannerList;
import com.kritter.api.entity.creative_banner.CreativeBannerListEntity;
import com.kritter.constants.CreativeBannerAPIEnum;

public class GetBanner {
    public static void getBanner(String apiUrl) throws Exception{
        CreativeBannerListEntity cble = new CreativeBannerListEntity();
        cble.setAccount_guid("0122000b-0409-0d01-4757-91f92b000007");
        cble.setCbenum(CreativeBannerAPIEnum.list_creative_banner_by_account);
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, cble.toJson());
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
        System.out.println(response.toString());
        CreativeBannerList msg = CreativeBannerList.getObject(response.toString());
        
        System.out.println(msg.toJson());

    }
    
    public static void main(String args[]) throws Exception{
        GetBanner.getBanner("http://localhost:9000/api/v1/creativebanner/listbyaccount");
    }
}
