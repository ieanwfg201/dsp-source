package com.kritter.kritterui.example.uiclient.reporting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetDashBoard {
	/**
	 * 
	 * Output
	 * JSON String of dimension and metrics {Similar to Morris table/chart format}
	 */
    public static String getDashBoard(String apiUrl) throws Exception{
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String str = response.toString();
        System.out.println(str);
        return str;
    }
    
    public static void main(String args[]) throws Exception{
    	GetDashBoard.getDashBoard(ExampleConstant.url_prefix+ExampleConstant.dashboardPub+"pubguid");
    }
}
