package com.kritter.fanoutinfra.benchmark.common;

import java.net.URI;
/**
 * Src -> https://svn.apache.org/repos/asf/httpcomponents/benchmark/httpclient/trunk
 * @author rohan
 */

public final class Config {

    private URI uri;
    private int requests;
    private int concurrency;
    private boolean keepAlive;
    private int contentLength;

    public Config() {
        super();
        this.requests = 1;
        this.concurrency = 1;
        this.contentLength = 2048;
        this.keepAlive = false;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(final URI uri) {
        this.uri = uri;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(final int requests) {
        this.requests = requests;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(final int concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(final boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

}