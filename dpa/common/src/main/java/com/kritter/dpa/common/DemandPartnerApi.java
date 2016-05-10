package com.kritter.dpa.common;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.dpa.common.entity.DemandPartnerApiResponse;

/**
 * This class defines and performs common actions required by a demand partner api.
 */
public interface DemandPartnerApi
{
    /**
     * Individual/Specific demand partner APIs implements this function as per their
     * API implementation requirements.
     **/
    public DemandPartnerApiResponse fetchDemandPartnerApiResponse(Request request);
    
    public void setReadTimeOut(int readTimeOut);
    
    public void setConnectTimeOut(int connectTimeOut);

}
