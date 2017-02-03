package com.kritter.material_upload.youkuvideouploader;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenNonWeb {
    private static final Logger LOG = LogManager.getLogger(TokenNonWeb.class);
    @Getter@Setter
    private String accessToken;

    public void refreshToken(String client_id,String client_secret,String refresh_token,
    		String token_url) {
        String grant_type = "refresh_token";

        CloseableHttpClient client = null;
        try {
            client = HttpClients.createDefault();

            HttpPost refreshPost = new HttpPost(token_url+"?");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("client_id", client_id));
            nvps.add(new BasicNameValuePair("client_secret", client_secret));
            nvps.add(new BasicNameValuePair("grant_type", grant_type));
            nvps.add(new BasicNameValuePair("refresh_token", refresh_token));
            refreshPost.setEntity(new UrlEncodedFormEntity(nvps));

            CloseableHttpResponse resp = client.execute(refreshPost);
            JSONObject jsonResult = new JSONObject(EntityUtils.toString(resp.getEntity()));
            LOG.info("Refresh result: {}" , jsonResult.toString());
            accessToken = (String)jsonResult.get("access_token");

            resp.close();

        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

}


