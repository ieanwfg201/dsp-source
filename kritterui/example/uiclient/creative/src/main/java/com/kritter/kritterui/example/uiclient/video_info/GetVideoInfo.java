package com.kritter.kritterui.example.uiclient.video_info;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.video_info.VideoInfoList;
import com.kritter.api.entity.video_info.VideoInfoListEntity;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetVideoInfo {
	/**
	 * Input 
	 * com.kritter.api.entity.video_info.VideoInfoListEntity
	 * Output
	 * com.kritter.api.entity.video_info.VideoInfoList
	 * 
	 * 
	 */
    public static VideoInfo getVideoInfo(String apiUrl) throws Exception{

    	VideoInfoListEntity vile = new VideoInfoListEntity();
    	vile.setId(27); /*Id of video Info*/
    	/*
    	 * Not Required
    	vile.setId_list(id_list);
    	vile.setAccount_guid(account_guid);
		vile.setVideoenum(videoenum);
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
        if(vil != null && vil.getCblist() != null && vil.getCblist().size()>0){
        	return vil.getCblist().get(0);
        }
        System.out.println("VideoInfo Notfound");
        return null;
    }
    
    public static void main(String args[]) throws Exception{
        GetVideoInfo.getVideoInfo(ExampleConstant.url_prefix+ExampleConstant.get_videoinfo);
    }
}
