package com.kritter.adserving.shortlisting.core;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;

/**
 * This is the interface for to be implemented by an ad-exchange
 * targeting matcher, depending upon versions of open rtb and probably
 * on specific parameters/attributes matching would change and would
 * require different implementations.
 */
public interface CreativeAndFloorMatchingRTBExchange
{
    public void processAdUnitsForEachBidRequestImpression(
                                                          Request request,
                                                          Response response
                                                         ) throws Exception;
}
