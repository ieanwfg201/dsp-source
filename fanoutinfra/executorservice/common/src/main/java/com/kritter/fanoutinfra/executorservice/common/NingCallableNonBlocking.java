package com.kritter.fanoutinfra.executorservice.common;

import com.kritter.constants.ExchangeConstants;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;
import com.kritter.fanoutinfra.apiclient.common.KHttpResponse;

import java.net.URI;
import java.util.concurrent.Callable;

/**
 * This class implements callable interface to fetch responses from external demand partners.
 */
public class NingCallableNonBlocking implements Callable<KHttpResponse> {

    private int maxConnectionPerHost = 10;
    private int maxConnection = 100;
    private int requestTimeoutMillis = 400;
    private String content = null;
    private KHttpClient httpClient = null;
    private URI uri = null;
    private String key = null;
    private KHttpClient.REQUEST_METHOD requestMethod;

    public NingCallableNonBlocking(URI uri, String content, int requestTimeoutMillis,
                                   int maxConnectionPerHost, int maxConnection, KHttpClient httpClient, String key,
                                   KHttpClient.REQUEST_METHOD requestMethod)
    {
        this.maxConnectionPerHost = maxConnectionPerHost;
        this.maxConnection = maxConnection;
        this.requestTimeoutMillis = requestTimeoutMillis;
        this.content = content;
        this.httpClient = httpClient;
        this.uri = uri;
        this.key = key;
        this.requestMethod = requestMethod;
    }

    @Override
    public KHttpResponse call() throws Exception
    {
        KHttpResponse kHttpResponse =
                this.httpClient.queryNonBlocking(this.uri,this.content.getBytes(), this.requestTimeoutMillis,
                                                this.maxConnectionPerHost, this.maxConnection,requestMethod);
        kHttpResponse.setResponsePayload(this.key + ExchangeConstants.callDelimiter + kHttpResponse.getResponsePayload());
        return kHttpResponse;
    }
}
