package com.kritter.naterial_upload.valuemaker.entity;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moon on 16/9/6.
 */
public class HttpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static Map<String, String> post(String url, Object params, String username, String password) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).setConnectionRequestTimeout(1000).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.setHeader("Authorization", "Basic " + authStringEnc(username, password));
        Header[] hs = httpPost.getAllHeaders();
//        for (Header h : hs) {
//            System.out.println(h.getName() + ":" + h.getValue());
//        }
        httpPost.setEntity(new StringEntity(JSON.toJSONString(params), Consts.UTF_8));

        CloseableHttpResponse response = httpClient.execute(httpPost);

        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            LOG.debug(h.getName() + ":" + h.getValue());
        }

        int responseStatus = response.getStatusLine().getStatusCode();

        HttpEntity responseEntity = response.getEntity();
        String result = EntityUtils.toString(responseEntity, Consts.UTF_8);
        response.close();

        Map<String, String> res = new HashMap<String, String>();
        res.put("StatusCode", responseStatus + "");
        res.put("ResponseStr", result);

        return res;
    }

    public static Map<String, String> get(String url, String username, String password) throws IOException {

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).setConnectionRequestTimeout(1000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        httpGet.setHeader("Content-Type", "application/json;charset=utf-8");
        httpGet.setHeader("Authorization", "Basic " + authStringEnc(username, password));

        CloseableHttpResponse response = httpClient.execute(httpGet);

        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            LOG.debug(h.getName() + ":" + h.getValue());
        }

        int responseStatus = response.getStatusLine().getStatusCode();

        HttpEntity responseEntity = response.getEntity();
        String result = EntityUtils.toString(responseEntity, Consts.UTF_8);
        response.close();

        Map<String, String> res = new HashMap<String, String>();
        res.put("StatusCode", responseStatus + "");
        res.put("ResponseStr", result);

        return res;
    }

    private static String authStringEnc(String username, String password) {
        String np = username + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(np.getBytes());
        String authStringEnc = new String(authEncBytes);
        System.out.println(authStringEnc);
        return authStringEnc;
    }
}
