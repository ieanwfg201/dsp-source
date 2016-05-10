package com.kritter.utils.http_client.entity;

import lombok.Getter;

/**
 * This class holds parameters that are required for an http response.
 */
public class HttpResponse
{
    @Getter
    private int responseStatusCode;
    @Getter
    private String responsePayload;
    @Getter
    private String responseContentType;

    public HttpResponse(int responseStatusCode,String responsePayload,String responseContentType)
    {
        this.responseStatusCode = responseStatusCode;
        this.responsePayload = responsePayload;
        this.responseContentType = responseContentType;
    }
}
