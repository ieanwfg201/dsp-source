package com.kritter.kritterui.example.uiclient.reporting;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;


import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.reporting.ReportingEntity;

public class GetReport {

    public static void getReport(String apiUrl) throws Exception{
        ReportingEntity entity = new ReportingEntity();
        LinkedList<String> advertiserId = new LinkedList<String>();
        advertiserId.add("'0122000b-0409-0d01-4757-91f92b000007'");
        entity.setAdvertiserId(advertiserId);
        entity.setStart_time_str("2014-07-06 00:00");
        entity.setEnd_time_str("2015-07-06 00:00");
        entity.setTotal_request(true);
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
        System.out.println(response.toString());

    }
    
    public static void main(String args[]) throws Exception{
        GetReport.getReport("http://localhost:9000/api/v1/reporting");
    }


}
