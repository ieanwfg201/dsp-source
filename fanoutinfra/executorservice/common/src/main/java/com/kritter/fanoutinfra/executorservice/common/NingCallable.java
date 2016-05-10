package com.kritter.fanoutinfra.executorservice.common;

import java.net.URI;
import java.util.concurrent.Callable;

import com.kritter.constants.ExchangeConstants;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;

public class NingCallable implements Callable<String> {

    private int maxConnectionPerHost = 1;
    private int maxConnection = 4;
    private int requestTimeoutMillis = 100;
    private String content = null;
    private KHttpClient httpClient = null;
    private URI uri = null;
    private String key = null;
    
    public NingCallable(URI uri, String content, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection,KHttpClient httpClient, String key){
        this.maxConnectionPerHost = maxConnectionPerHost;
        this.maxConnection = maxConnection;
        this.requestTimeoutMillis = requestTimeoutMillis;
        this.content = content;
        this.httpClient = httpClient;
        this.uri = uri;
        this.key = key;
    }
    
    @Override
    public String call() throws Exception {
        return this.key+ExchangeConstants.callDelimiter+this.httpClient.post(this.uri, 
                this.content.getBytes(), this.requestTimeoutMillis, this.maxConnectionPerHost, this.maxConnection);
    }

}
