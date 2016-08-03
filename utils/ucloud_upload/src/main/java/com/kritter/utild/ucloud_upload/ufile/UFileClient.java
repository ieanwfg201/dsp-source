package com.kritter.utild.ucloud_upload.ufile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.client.HttpClient;



import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class UFileClient {
    private static Logger m_logger = Logger.getLogger(UFileClient.class);
	private static final String CANONICAL_PREFIX = "X-UCloud";
	
	/*
	 * public key
	 * private key
	 * domain suffix 
	 */
	private String ucloudPublicKey;
	private String ucloudPrivateKey;
	private String proxySuffix;
	private String downloadProxySuffix;
	private HttpClient httpClient;
	
	
	public UFileClient() {
		this.ucloudPublicKey = "";
		this.ucloudPrivateKey = "";
		this.proxySuffix = "";
		this.downloadProxySuffix = "";
		httpClient = new DefaultHttpClient();
		this.setHttpClient(httpClient);
	}


	public void loadConfig(String ucloudPublicKey,String ucloudPrivateKey, String proxySuffix,
            String downloadProxySuffix) {
        this.ucloudPublicKey = ucloudPublicKey;
        this.ucloudPrivateKey = ucloudPrivateKey;
        this.proxySuffix = proxySuffix;
        this.downloadProxySuffix = downloadProxySuffix;
	}
	
	public String getUCloudPublicKey() {
		return ucloudPublicKey;
	}


	public void setUCloudPublicKey(String ucloudPublicKey) {
		this.ucloudPublicKey = ucloudPublicKey;
	}


	public String getUCloudPrivateKey() {
		return ucloudPrivateKey;
	}


	public void setUCloudPrivateKey(String ucloudPrivateKey) {
		this.ucloudPrivateKey = ucloudPrivateKey;
	}


	public String getProxySuffix() {
		return proxySuffix;
	}


	public void setProxySuffix(String proxySuffix) {
		this.proxySuffix = proxySuffix;
	}


	public String getDownloadProxySuffix() {
		return downloadProxySuffix;
	}


	public void setDownloadProxySuffix(String downloadProxySuffix) {
		this.downloadProxySuffix = downloadProxySuffix;
	}


	public void makeAuth(String stringToSign, UFileRequest request
	        ) {
		String signature = new HmacSHA1().sign(this.ucloudPrivateKey, stringToSign);
		String authorization = "UCloud" + " " + this.ucloudPublicKey + ":" + signature;
		request.setAuthorization(authorization);
	}


	public String spliceCanonicalHeaders(UFileRequest request) {
		Map<String, String> headers = request.getHeaders();
	    Map<String, String> sortedMap = new TreeMap<String, String>();
		
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				if (entry.getKey().startsWith(CANONICAL_PREFIX)) {
					sortedMap.put(entry.getKey().toLowerCase(), entry.getValue());
				}
	        }
			String result = "";
			for (Entry<String, String> entry : sortedMap.entrySet()) {
				result += entry.getKey() + ":" +  entry.getValue() + "\n";
	        }
			return result;
		} else {
			return "";
		}
	}


	public void closeErrorResponse(UFileResponse res) {
	    try{
		InputStream inputStream = res.getContent();
		if (inputStream != null) {
			Reader reader = new InputStreamReader(inputStream);
			StringBuffer sBUff = new StringBuffer("");
			int c;
			while ((c=reader.read())!= -1) {
			    sBUff.append((char)c);
	         }
			m_logger.debug(sBUff.toString());
			/*Gson gson = new Gson();
			ErrorBody body = gson.fromJson(reader, ErrorBody.class);
			String bodyJson = gson.toJson(body);
			System.out.println(bodyJson);
		*/
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					m_logger.error(e.getMessage(),e);
				}
			}
		}
	    }catch(Exception e){
	        m_logger.error(e.getMessage(),e);
		}
	}


	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}


	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void shutdown() {
		httpClient.getConnectionManager().shutdown();
	}
}
