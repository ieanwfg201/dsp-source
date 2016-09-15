package com.kritter.material_upload.youkuvideouploader;

/**
 * Created by oneal on 16/8/2.
 */
public class YoukuConfig {

	// TODO should read from config file
	private String refreshToken = "3f2f8405a24eacc2c1c39d2c098e190a";

	// TODO should read from config file
	private String clientId = "10a56fb7e9c3bd9c";

	// TODO should read from config file
	private String clientSecret = "b4ef90f3450b190ad3d95cc0b1c0549a";

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientSecret() {
		return clientSecret;
	}

}
