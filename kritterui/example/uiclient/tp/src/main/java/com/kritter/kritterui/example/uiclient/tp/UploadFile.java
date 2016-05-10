package com.kritter.kritterui.example.uiclient.tp;

import java.io.File;
import java.io.InputStream;

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

public class UploadFile {
    public static void main(String[] args) throws Exception { 
        String filePath = "/home/rohan/a.txt";
        String accountGuid = "0122000b-0409-0d01-4757-91f92b000007";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:9000/api/v1/ipfileupload/"+accountGuid);
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

         
} 
}
