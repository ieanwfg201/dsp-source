package com.kritter.api_caller.core.api;

import com.kritter.api_caller.core.action.ApiAction;

/**
 * This interface is used to define an API.
 *
 */
public interface Api {

    /**
     * This function returns the api signature associated with
     * this api for instance storage and referencing.
     */
    public String getApiSignature();

    /**
     * The implementations of this interface has api action class
     * as part of them to perform api action.
     */
    public ApiAction getApiActionInstance();

    /**
     * This function processes an incoming request for this api.
     * The input could be in any form and output can be anything.
     * Its the job of the implementations to take care of both.
     */
    public Object processApiRequest(Object input);

}
