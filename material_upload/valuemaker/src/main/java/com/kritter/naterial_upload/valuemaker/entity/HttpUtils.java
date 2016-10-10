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

import java.io.IOException;

/**
 * Created by moon on 16/9/6.
 */
public class HttpUtils {

    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static Integer post(String url, Object params,String username,String password) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).setConnectionRequestTimeout(1000).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.setHeader("Authorization", "Basic " + authStringEnc(username,password));
        Header[] hs=httpPost.getAllHeaders();
        for (Header h : hs) {
            System.out.println(h.getName() + ":" + h.getValue());
        }
        System.out.println(JSON.toJSONString(params));
        httpPost.setEntity(new StringEntity(JSON.toJSONString(params), Consts.UTF_8));

        CloseableHttpResponse response = httpClient.execute(httpPost);

        int responseStatus = response.getStatusLine().getStatusCode();
        System.out.println(responseStatus);

        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            System.out.println(h.getName() + ":" + h.getValue());
        }

        HttpEntity responseEntity = response.getEntity();
        String result = EntityUtils.toString(responseEntity, Consts.UTF_8);
        System.out.println(result);
        response.close();
        return responseStatus;
    }

    public static String get(String url,String username,String password) throws IOException {

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).setConnectionRequestTimeout(1000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        httpGet.setHeader("Content-Type", "application/json;charset=utf-8");
        httpGet.setHeader("Authorization", "Basic " + authStringEnc(username,password));

        CloseableHttpResponse response = httpClient.execute(httpGet);

        int responseStatus = response.getStatusLine().getStatusCode();
        System.out.println(responseStatus);

        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            System.out.println(h.getName() + ":" + h.getValue());
        }

        HttpEntity responseEntity = response.getEntity();
        String result = EntityUtils.toString(responseEntity, Consts.UTF_8);
        System.out.println(result);
        response.close();
        return result;
    }

    public static String authStringEnc(String username,String password) {
        String nameandpassword = username + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(nameandpassword.getBytes());
        String authStringEnc = new String(authEncBytes);
        System.out.println(authStringEnc);
        return authStringEnc;
    }
}
