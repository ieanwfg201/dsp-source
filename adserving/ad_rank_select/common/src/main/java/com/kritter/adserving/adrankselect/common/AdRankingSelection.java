package com.kritter.adserving.adrankselect.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;

/**
 * This interface defines methods to be implemented by any ranking and selection
 * class used in adserving to finally select the ad units for formatting.
 */
public interface AdRankingSelection
{
    /**
     * This method ranks ad units and selects them in decreasing order of their
     * priority.
     * @param request
     * @param response
     * @return
     */
    public ResponseAdInfo[] rankAdUnitsInDecreasingOrder(Request request,Response response);

}
