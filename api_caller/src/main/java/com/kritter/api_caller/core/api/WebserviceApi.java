package com.kritter.api_caller.core.api;

import com.kritter.api_caller.core.action.WebserviceApiAction;
import com.kritter.api_caller.core.model.WebServiceRequestEntity;
import com.kritter.api_caller.core.model.WebServiceResponseEntity;

/**
 * This class implements a webservice api, the role of this class is to
 * query a remote server over http protocol for some resource and fetch
 * contents with http status.
 *
 * The request could be POST or GET depending upon usage.
 */
public class WebserviceApi implements Api<WebServiceResponseEntity,WebServiceRequestEntity>
{
    private WebserviceApiAction webserviceApiAction;
    private String webserviceApiSignature;

    @Override
    public String getApiSignature()
    {
        return this.webserviceApiSignature;
    }

    @Override
    public WebServiceResponseEntity processApiRequest(WebServiceRequestEntity input)
    {
        return this.webserviceApiAction.fetchApiResponseFromRemoteWebServer(input);
    }
}
