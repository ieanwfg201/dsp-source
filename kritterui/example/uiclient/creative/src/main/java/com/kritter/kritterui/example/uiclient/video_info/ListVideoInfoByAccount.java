package com.kritter.kritterui.example.uiclient.video_info;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.video_info.VideoInfoList;
import com.kritter.api.entity.video_info.VideoInfoListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class ListVideoInfoByAccount {
	/**
	 * Input 
	 * com.kritter.api.entity.video_info.VideoInfoListEntity
	 * Output
	 * com.kritter.api.entity.video_info.VideoInfoList
	 * 
	 * 
	 */
    public static VideoInfoList listVideoInfoByAccount(String apiUrl) throws Exception{

    	VideoInfoListEntity vile = new VideoInfoListEntity();
    	vile.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");/*GUID of account - can be obtained by GetAdvAccount*/
    	/*
    	 * Not Required
    	vile.setId();
		vile.setVideoenum(videoenum);
    	vile.setId_list("27,28");
    	 */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, vile.toJson());
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
        VideoInfoList vil = VideoInfoList.getObject(response.toString());
        System.out.println(vil.toJson());
        return vil;
    }
    
    public static void main(String args[]) throws Exception{
        ListVideoInfoByAccount.listVideoInfoByAccount(ExampleConstant.url_prefix+ExampleConstant.listbyaccount_videoinfo);
    }
}
