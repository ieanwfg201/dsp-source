package com.kritter.api_caller.core.action;

import com.kritter.api_caller.core.model.ApiRequestEntity;
import com.kritter.api_caller.core.model.ApiResponseEntity;

/**
 * This interface represents a webservice action for an
 * api action.
 */
public interface ApiWebserviceAction extends ApiAction{

    /**
     * This function returns the complete url required to make an api call
     * to third party server.The url would contain placeholders to be replaced
     * by appropriate parameters.
     * E.G: While calling api to create a site the publisher id could be
     * specified in the get url.
     */
    public String getAPIUrl();

    /**
     * This function makes an api call to the third party server and gets the
     * response data to be used for display, storage, download ,etc.
     * ApiRequestEntity would contain json , other parameters to be set to api
     * url,etc.
     */
    public ApiResponseEntity fetchAPIResponseFromServer(
            ApiRequestEntity apiRequestEntity);
}
