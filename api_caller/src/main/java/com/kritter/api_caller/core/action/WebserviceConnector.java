package com.kritter.api_caller.core.action;

import java.util.Map;

/**
 * This interface takes care of making an api call to the thirdparty server.
 *
 * The class implementing this interface would require some sort of http
 * request firing capability.
 *
 * The request would then be submitted with a post json body or probably
 * without any json body.
 *
 * The result from thirdparty could be a simple json or some other form of data
 * with success , failure message or action result etc.It could also be a file
 * link to download as in the case of fetching reports.
 */

public interface WebserviceConnector {

    public String fetchResponseFromApiCall(String apiURL, Map<String,String> headerValuesToSet, String postBody);

    public void releaseResources();

}
