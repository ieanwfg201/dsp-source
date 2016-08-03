package com.kritter.fanoutinfra.executorservice.common;

import java.net.URI;
import java.util.concurrent.Callable;

import com.kritter.fanoutinfra.apiclient.common.KHttpClient;

public class NingGetUrlCallable implements Callable<String> {

    private int maxConnectionPerHost = 1;
    private int maxConnection = 4;
    private int requestTimeoutMillis = 100;
    private KHttpClient httpClient = null;
    private URI uri = null;
    
    public NingGetUrlCallable(URI uri, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection,KHttpClient httpClient){
        this.maxConnectionPerHost = maxConnectionPerHost;
        this.maxConnection = maxConnection;
        this.requestTimeoutMillis = requestTimeoutMillis;
        this.httpClient = httpClient;
        this.uri = uri;
    }
    
    @Override
    public String call() throws Exception {
        return this.httpClient.get(this.uri,this.requestTimeoutMillis, 
        		this.maxConnectionPerHost, this.maxConnection);
    }

}
