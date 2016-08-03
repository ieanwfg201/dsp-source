package com.kritter.api_caller.core.api;

/**
 * This interface is used to define an API.
 * Concept is that an API lets you perform some action which could be
 * database operation or fetching some data from remote servers over
 * internet.
 */
public interface Api<O,I>
{
    /**
     * This function returns the api signature associated with
     * this api for instance-storage and referencing inside
     * ApiPool, so if we wish to store api instances and
     * maintain them via usage of ApiPool, the signature value
     * is required to store and fetch the corresponding Api
     * instance.
     * Must be unique across different API instances.
     */
    public String getApiSignature();

    /**
     * This function processes an incoming request for this api.
     * The input could be in any form and output can be anything.
     * Its the job of the implementations to take care of both.
     */
    public O processApiRequest(I input);
}
