package com.kritter.fanoutinfra.apiclient.common;

import lombok.Getter;
import lombok.Setter;

/**
 * To capture DSP/External-demand-partner response.
 */
public class KHttpResponse
{
    @Getter @Setter
    private int responseStatusCode;
    @Getter @Setter
    private String responsePayload;
    @Getter @Setter
    private String responseContentType;
}
