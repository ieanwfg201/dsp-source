package com.kritter.utild.ucloud_upload.ufile.sender;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.log4j.Logger;

import com.kritter.utild.ucloud_upload.ufile.UFileClient;
import com.kritter.utild.ucloud_upload.ufile.UFileRequest;
import com.kritter.utild.ucloud_upload.ufile.UFileResponse;

public class PutSender implements Sender {
    private static Logger m_logger = Logger.getLogger(PutSender.class);
	public PutSender() {
		
	}

	@Override
	public void makeAuth(UFileClient client, UFileRequest request) {		
		String httpMethod = "PUT";
		String contentMD5 = request.getContentMD5();
		String contentType = request.getContentType();	
		String date = request.getDate();
		String canonicalUCloudHeaders = client.spliceCanonicalHeaders(request);
		String canonicalResource = "/" + request.getBucketName() + "/" + request.getKey();
		String stringToSign =  httpMethod + "\n" + contentMD5 + "\n" + contentType + "\n" + date + "\n" +
				canonicalUCloudHeaders + canonicalResource;
		client.makeAuth(stringToSign, request);
	}

	@Override
	public UFileResponse send(UFileClient ufileClient, UFileRequest request) {
		String uri = "http://" + request.getBucketName() + ufileClient.getProxySuffix() + "/" + request.getKey();
		HttpPut putMethod = new HttpPut(uri);
		
		HttpEntity resEntity = null;
		try {
			Map<String, String> headers = request.getHeaders();
			if (headers != null) {
				for (Entry<String, String> entry : headers.entrySet()) {
		            putMethod.setHeader(entry.getKey(), entry.getValue());
		        }
			}
			long  len = request.getContentLength();
			if (len < 0) {
				throw new Exception("Missing Content-Length Exception");
			}
			InputStreamEntity reqEntity = new InputStreamEntity(request.getInputStream(), len);	
			
			putMethod.setEntity(reqEntity);
			UFileResponse ufileResponse = new UFileResponse();
			HttpResponse httpResponse = ufileClient.getHttpClient().execute(putMethod);
			
			resEntity = httpResponse.getEntity();
		
			ufileResponse.setStatusLine(httpResponse.getStatusLine());
			ufileResponse.setHeaders(httpResponse.getAllHeaders());
			
			if (resEntity != null) {
				ufileResponse.setContentLength(resEntity.getContentLength());
				ufileResponse.setContent(resEntity.getContent());
			}

			return ufileResponse;
		} catch (Exception e) {
			m_logger.error(e.getMessage(),e);
			try {
				if (resEntity != null && resEntity.getContent() != null) {
					resEntity.getContent().close();
				}
			} catch (IOException e1) {
			    m_logger.error(e1.getMessage(),e1);
			}
		}
		return null;
	}
}
