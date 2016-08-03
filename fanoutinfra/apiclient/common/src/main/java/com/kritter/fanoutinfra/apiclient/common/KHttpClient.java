package com.kritter.fanoutinfra.apiclient.common;

import java.net.URI;

public interface KHttpClient {
    void init() throws Exception;

    void shutdown() throws Exception;

    String post(URI target, byte[] content, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) throws Exception;
    String get(URI target, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) throws Exception;
}
