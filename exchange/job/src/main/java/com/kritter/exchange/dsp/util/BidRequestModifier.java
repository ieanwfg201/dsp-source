package com.kritter.exchange.dsp.util;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;

/**
 * This interface defines signature of methods required for enhancing bid requests before
 * sending them to a DSP for bidding.
 *
 * TODO : implementation of this interface must move to some util package in future and
 * TODO : not reside in this exchange module, as implementation are very specific to DSPs
 */
public interface BidRequestModifier<R>
{
    public R modifyBidRequest(R originalOpenRTBBidRequest);

    /**Very generic method to allow modifying of requesting URL
     * to external demand channels*/
    public String modifyRequestURL(String originalRequestURL, Request request,R originalOpenRTBBidRequest);

    public KHttpClient.REQUEST_METHOD getRequestMethod();
}
