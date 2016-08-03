package com.kritter.kritterui.example.uiclient.banner;

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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.creative_banner.ImageUploadResponse;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.response.msg.Message;

public class UploadBanner {
    public static void main(String[] args) throws Exception { 
        String filePath = "/home/rohan/banners/AT_EN_320x50.jpg";
        String accountGuid = "ffffffff-ffff-ff01-55ec-0868ab000015";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:9000/api/v1/imageupload/"+accountGuid);
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
        insertCreativeBanner(imr);
        		
        

         
} 
    
    public static void insertCreativeBanner(ImageUploadResponse imr ) throws Exception{
    	Creative_banner cli = new Creative_banner();
        cli.setAccount_guid("ffffffff-ffff-ff01-55ec-0868ab000015");
        cli.setModified_by(1);
        cli.setResource_uri(imr.getBannerUrl());
        cli.setSlot_id(12); //NOTE this shoul be driven from slots**
        
        URL url = new URL("http://localhost:9000/api/v1/creativebanner/create");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        System.out.println(cli.toJson());
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
        Message msg = Message.getObject(response.toString());
        
        System.out.println(msg.toJson());

    }

}
