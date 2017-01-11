package com.kritter.kritterui.example.uiclient.video_info;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class EditVideoInfo {
	/**
	 * Input 
	 * com.kritter.entity.video_props.VideoInfo (Json String)
	 * Output
	 * com.kritter.api.entity.response.msg.Message (Json String)
	 * E.g
	 */
    public static Message editVideoInfo(String apiUrl) throws Exception{

    	/*Get VideoInfo First*/
    	VideoInfo vi = GetVideoInfo.getVideoInfo(ExampleConstant.url_prefix+ExampleConstant.get_videoinfo);
    	vi.setModified_by(1);/*Ask for this*/
    	vi.setVideo_size(2);;/*Ask for this*/
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, vi.toJson());
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
        EditVideoInfo.editVideoInfo(ExampleConstant.url_prefix+ExampleConstant.edit_videoinfo);
    }
}
