package com.kritter.kritterui.example.uiclient.video_info;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.creative_banner.ImageUploadResponse;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;
import com.kritter.kritterui.example.uiclient.creative.CreateDirectVideoCreativeContainer;

public class UploadDirectVideoCreateVideoInfo {

	/**
	 * */
	public static void uploadDirectVideo(String apiUrl, String directVideoUploadUrl) throws Exception{
	       String filePath = "/home/rohan/testvideos/300_50.mp4";
	        String accountGuid = "011c3e84-ed0b-cb01-57fa-c1f271000001";/*GUID of account - can be obtained by GetAdvAccount*/
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(directVideoUploadUrl+accountGuid);
	        FileBody bin = new FileBody(new File(filePath));

	        MultipartEntity reqEntity = new MultipartEntity();
	        reqEntity.addPart("file", bin);
	        httppost.setEntity(reqEntity);


	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity resEntity = response.getEntity();
	        // Get the HTTP Status Code
	        int statusCode = response.getStatusLine().getStatusCode();

	        // Get the contents of the response
	        InputStream input = resEntity.getContent();
	        String responseBody = IOUtils.toString(input);
	        input.close();

	        // Print the response code and message body
	        System.out.println("HTTP Status Code: "+statusCode);
	        System.out.println(responseBody);
	        ImageUploadResponse imr = ImageUploadResponse.getObject(responseBody);
	        createVideoInfo(apiUrl,imr.getBannerUrl());
	}
	/**
	 * Input 
	 * com.kritter.entity.video_props.VideoInfo (Json String)
	 * Output
	 * com.kritter.api.entity.response.msg.Message (Json String)
	 * E.g
	 * {"msg":"OK","error_code":0,"id":"27"}
	*/
	 public static Message createVideoInfo(String apiUrl,String bannerUrl) throws Exception{
		 	VideoInfo vi = new VideoInfo();
		 	vi.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");/*GUID of account - can be obtained by GetAdvAccount*/
		 	vi.setResource_uri(bannerUrl);
		 	vi.setVideo_size(1);/*Set default 1 as this is not honored*/
		 	vi.setModified_by(1); /*Ask for tghis*/
		 	/**
		 	 * Not Required Auto Populated
		 	vi.setLast_modified(last_modified);
		 	vi.setGuid(guid);
		 	vi.setId(id);
		 	vi.setCreated_on(created_on);
		 	 */
		 	/**
		 	 * Not Required
		 	vi.setExt(ext);
		 	 */
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
	        UploadDirectVideoCreateVideoInfo.uploadDirectVideo(ExampleConstant.url_prefix+ExampleConstant.create_videoinfo,
	        		ExampleConstant.url_prefix+ExampleConstant.video_upload_url);
	    }

}
