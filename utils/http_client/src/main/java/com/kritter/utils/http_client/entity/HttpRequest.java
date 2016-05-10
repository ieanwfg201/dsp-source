package com.kritter.utils.http_client.entity;

import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class keeps the parameters required for calling a third party server.
 */
public class HttpRequest
{
    @Getter
    private String serverURL;
    @Getter
    private int connectTimeOut;
    @Getter
    private int readTimeOut;
    @Getter
    private Map<String,String> headerValuesToSet;
    @Getter
    private String postBodyPayload;
    @Getter
    private REQUEST_METHOD requestMethod;

    private static final String EQUAL_TO = "=";
    private static final String PARAM_DELIM = "&";
    private static final String ENCODING = "UTF-8";

    public enum REQUEST_METHOD
    {
        POST_METHOD("POST"),
        GET_METHOD("GET"),
        DELETE_METHOD("DELETE");

        @Getter
        private String method;

        REQUEST_METHOD(String method)
        {
            this.method = method;
        }
    }

    public HttpRequest(String serverURL,
                       int connectTimeOut,
                       int readTimeOut,
                       REQUEST_METHOD requestMethod,
                       Map<String,String> headerValuesToSet,
                       String postBodyPayload)
    {
        this.serverURL = serverURL;
        this.connectTimeOut = connectTimeOut;
        this.readTimeOut = readTimeOut;
        this.requestMethod = requestMethod;
        this.headerValuesToSet = headerValuesToSet;
        this.postBodyPayload = postBodyPayload;
    }

    public static String preparePostBodyFromRequestParameters(Map<String,Object> postBodyRequestParameters)
                                                                                    throws UnsupportedEncodingException
    {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : postBodyRequestParameters.entrySet())
        {
            if (postData.length() != 0) postData.append(PARAM_DELIM);
            postData.append(URLEncoder.encode(param.getKey(), ENCODING));
            postData.append(EQUAL_TO);
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), ENCODING));
        }

        byte[] postDataBytes = postData.toString().getBytes(ENCODING);

        return new String(postDataBytes);
    }
}