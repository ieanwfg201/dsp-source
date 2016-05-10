package com.kritter.fanoutinfra.benchmark.common;

import java.net.URI;

/**
 * Src -> https://svn.apache.org/repos/asf/httpcomponents/benchmark/httpclient/trunk
 * @author rohan
 */
public interface HttpAgent {
    void init() throws Exception;

    void shutdown() throws Exception;

    String getClientName();

    Stats get(URI target, int n, int c) throws Exception;
    Stats post(URI target, byte[] content, int n, int c) throws Exception;
}
