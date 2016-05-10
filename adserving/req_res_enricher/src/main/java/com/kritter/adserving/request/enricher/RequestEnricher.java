package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface is implemented by all request enrichers.
 * Defines what all is required in enrichment process.
 */

public interface RequestEnricher {

    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception;

}
