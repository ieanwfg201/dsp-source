package com.kritter.utild.ucloud_upload.ufile;

import java.util.Date;

public class DownloadUrl {
	
	public static final String UCLOUD_PROXY_SUFFIX = ".ufile.ucloud.cn";
	
	public DownloadUrl() {
		
	}
	
	public String getUrl(UFileClient uFileClient, UFileRequest request, int ttl, boolean isPrivate) {
		if (!isPrivate) {
			return "http://" + request.getBucketName() + uFileClient.getDownloadProxySuffix() + "/" + request.getKey();
		}

		return makeDownloadUrl(uFileClient, request, ttl);
	}
	
	public String makeSignature(UFileClient client, UFileRequest request, long expires) {
		String httpMethod = "GET";
		String contentMD5 = request.getContentMD5();
		String contentType = request.getContentType();	
		String canonicalUCloudHeaders = client.spliceCanonicalHeaders(request);
		String canonicalResource = "/" + request.getBucketName() + "/" + request.getKey();
		String expire_str = "";
		if (expires > 0) {
			expire_str = String.valueOf(expires);
		}
		String stringToSign =  httpMethod + "\n" + contentMD5 + "\n" + contentType + "\n" + expire_str + "\n" +
				canonicalUCloudHeaders + canonicalResource;

		String signature = new HmacSHA1().sign(client.getUCloudPrivateKey(), stringToSign);
		return signature;
	}

	public String makeDownloadUrl(UFileClient ufileClient,
			UFileRequest request, int ttl) {
		String expire_str = "";
		long expires = 0;
		if (ttl > 0) {
			expires = (new Date().getTime() / 1000) + ttl;
			expire_str = String.valueOf(expires);
		}
		String signature = makeSignature(ufileClient, request, expires);
		String url = "http://" + request.getBucketName() + ufileClient.getDownloadProxySuffix() + "/" + request.getKey()
				+ "?UCloudPublicKey=" + ufileClient.getUCloudPublicKey()
				+ "&Expires=" + expire_str
				+ "&Signature=" + signature;
		return url;
	}

}
