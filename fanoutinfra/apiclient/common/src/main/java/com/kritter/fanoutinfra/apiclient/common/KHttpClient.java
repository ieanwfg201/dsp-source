package com.kritter.fanoutinfra.apiclient.common;

import lombok.Getter;

import java.net.URI;

public interface KHttpClient {
    void init() throws Exception;

    void shutdown() throws Exception;

    String post(URI target, byte[] content, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) throws Exception;
    String get(URI target, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) throws Exception;
    void postAsync(URI target, byte[] content, int requestTimeoutMillis, int maxConnectionPerHost, int maxConnection)
            throws Exception;
    void getAsync(URI target, int requestTimeoutMillis, int maxConnectionPerHost, int maxConnection) throws Exception;

    KHttpResponse queryNonBlocking(URI target, byte[] content, int requestTimeoutMillis,
                                   int maxConnectionPerHost, int maxConnection,
                                   REQUEST_METHOD requestMethod) throws Exception;

    enum REQUEST_METHOD
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
}
