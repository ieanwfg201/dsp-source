package com.kritter.api_caller.core.model;

import com.kritter.utils.http_client.entity.HttpRequest;
import java.util.Map;

/**
 * This class is used as input to WebserviceApiAction implementation to make
 * remote calls over web using http protocol.
 */
public class WebServiceRequestEntity extends HttpRequest implements ApiRequestEntity
{
    public WebServiceRequestEntity(String serverURL,
                                   int connectTimeOut,
                                   int readTimeOut,
                                   REQUEST_METHOD requestMethod,
                                   Map<String,String> headerValuesToSet,
                                   String postBodyPayload)
    {
        super(serverURL,connectTimeOut,readTimeOut,requestMethod,headerValuesToSet,postBodyPayload);
    }
}
