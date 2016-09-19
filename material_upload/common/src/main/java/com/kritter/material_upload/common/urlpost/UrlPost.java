package com.kritter.material_upload.common.urlpost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlPost {
	private static final Logger LOG = LoggerFactory.getLogger(UrlPost.class);
	public String urlpost(String urlString,String postBody) throws Exception{
		StringBuffer sbuff = new StringBuffer("");
        URL url;
        BufferedReader br = null;
        HttpURLConnection conn=null;
        OutputStream os=null;
		try{
            url = new URL(urlString);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            os = conn.getOutputStream();
            os.write(postBody.getBytes());
            os.flush();

            // open the stream and put it into BufferedReader
            br= new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sbuff.append(inputLine);
            }
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			throw new Exception(e);
		}finally{
            if(br!= null){
                try {
                    br.close();
                } catch (IOException e) {
                	LOG.error(e.getMessage(),e);
                }
            }
            if(conn != null){
            	conn.disconnect();
            }
            if(os != null){
            	try {
					os.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(),e);
				}
            }

		}
		return sbuff.toString();
	}
}
