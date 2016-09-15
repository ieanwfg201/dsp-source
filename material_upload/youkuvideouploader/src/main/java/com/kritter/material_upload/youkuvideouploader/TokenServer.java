package com.kritter.material_upload.youkuvideouploader;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by oneal on 16/8/2.
 */
public class TokenServer {

    static {
        init();
    }

    private static Timer timer = null;
    private static String accessToken;
    public static String getAccessToken() {
        return accessToken;
    }

    private static void init() {
        refreshToken();
        int refreshInterval = 3600; // TODO should be configurable
        timer = new Timer();
        timer.schedule(new AccessTokenRefreshTask(), 0, refreshInterval);
    }

    public static void shutDown() {
        timer.cancel();
    }

    private static void refreshToken() {
        YoukuConfig config = new YoukuConfig();
        String client_id = config.getClientId();
        String client_secret = config.getClientSecret();
        String refresh_token = config.getRefreshToken();
        String grant_type = "refresh_token";

        CloseableHttpClient client = null;
        try {
            client = HttpClients.createDefault();

            HttpPost refreshPost = new HttpPost("https://openapi.youku.com/v2/oauth2/token?");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("client_id", client_id));
            nvps.add(new BasicNameValuePair("client_secret", client_secret));
            nvps.add(new BasicNameValuePair("grant_type", grant_type));
            nvps.add(new BasicNameValuePair("refresh_token", refresh_token));
            refreshPost.setEntity(new UrlEncodedFormEntity(nvps));

            CloseableHttpResponse resp = client.execute(refreshPost);
            JSONObject jsonResult = new JSONObject(EntityUtils.toString(resp.getEntity()));
            System.out.println("Refresh result: " + jsonResult.toString());
            accessToken = (String)jsonResult.get("access_token");

            resp.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class AccessTokenRefreshTask extends TimerTask {

        @Override
        public void run() {
            refreshToken();
        }
    }


    public static void main(String[] args) {
        TokenServer.refreshToken();
    }

}


