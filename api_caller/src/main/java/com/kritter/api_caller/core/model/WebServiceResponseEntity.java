package com.kritter.api_caller.core.model;

import com.kritter.utils.http_client.entity.HttpResponse;

/**
 * This class is used to capture output from WebserviceApiAction.
 */
public class WebServiceResponseEntity extends HttpResponse implements ApiResponseEntity
{
    public WebServiceResponseEntity(int responseStatusCode,String responsePayload,String responseContentType)
    {
        super(responseStatusCode,responsePayload,responseContentType);
    }
}
