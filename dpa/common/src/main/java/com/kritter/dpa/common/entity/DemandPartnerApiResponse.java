package com.kritter.dpa.common.entity;

import lombok.Getter;

/**
 * This class contains parameters or attributes required to model
 * a demand partner api response.
 */
public class DemandPartnerApiResponse
{
    @Getter
    private String responsePayload;
    @Getter
    private int responseStatusCode;
    @Getter
    private String responseContentType;
    @Getter
    private boolean isNoFill;
    @Getter
    private Double ecpm;

    public DemandPartnerApiResponse(
                                    int responseStatusCode,
                                    String responsePayload,
                                    String responseContentType,
                                    boolean isNoFill
                                   )
    {
        this.responseStatusCode = responseStatusCode;
        this.responsePayload = responsePayload;
        this.responseContentType = responseContentType;
        this.isNoFill = isNoFill;
    }
}
