package com.kritter.adserving.adexchange.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines how an ad-exchange request be enriched.
 * Any new ad-exchange integration must define this interface and
 * implement how to enrich the request object.
 */
public interface RTBExchangeRequestReader
{
    /**
     * Keep in mind the Content-Type and Charset while reading the post body.
     * Content-Type would be application/json (unless specified) and
     * charset (or data encoding ) should be UTF-8 always.
     * If content type is not received in request then set it on nginx level.
     * @param requestId
     * @param httpServletRequest
     * @param logger
     * @return
     * @throws Exception
     */
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception;
}
