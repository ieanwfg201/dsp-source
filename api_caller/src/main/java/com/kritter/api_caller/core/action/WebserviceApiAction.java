package com.kritter.api_caller.core.action;

import com.kritter.api_caller.core.model.WebServiceRequestEntity;
import com.kritter.api_caller.core.model.WebServiceResponseEntity;

/**
 * This interface represents methods used by a webservice API action implementation.
 */
public interface WebserviceApiAction extends ApiAction
{
    /**
     * This method takes input as an entity that would have url,headers,postbody,content type
     * etc. used to make web call to remote server and fetch content.
     * @param webServiceRequestEntity
     * @return WebServiceResponseEntity
     */
    public WebServiceResponseEntity fetchApiResponseFromRemoteWebServer(WebServiceRequestEntity webServiceRequestEntity);

}
