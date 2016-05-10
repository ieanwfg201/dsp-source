package com.kritter.adserving.request.enricher;

import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * This class enriches requests that come from ad-exchanges.
 */

public class RTBExchangeRequestEnricher implements RequestEnricher
{
    private Logger logger;

    //This map contains request uri identifiers and corresponding instances
    //to take care of the request enrichment for an exchange bid request.
    private Map<String,RTBExchangeRequestReader> rtbExchangeRequestReaderMap;

    public RTBExchangeRequestEnricher(String loggerName,
                                      Map<String,RTBExchangeRequestReader> rtbExchangeRequestReaderMap)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.rtbExchangeRequestReaderMap = rtbExchangeRequestReaderMap;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception
    {
        this.logger.info("Inside validateAndEnrichRequest() of RTBExchangeRequestEnricher");

        logger.debug("going to fetch request uri");
        String requestURI = httpServletRequest.getRequestURI();

        logger.debug("request uri is null from request" + (null == requestURI));

        logger.debug("Request URI inside RTBExchangeRequestEnricher is {} ", requestURI);

        String requestURIParts[] = requestURI.split(ApplicationGeneralUtils.URI_PATH_SEPARATOR);

        String adExchangeIdentifier = null;

        if(requestURIParts.length < 2)
            throw new Exception("Invalid Exchange Request, requestURI being: " + requestURI + " , should have " +
                                "been e.g: /b209484c01754ef08c0e476b796fc081/a179484c01754ef08c0e476b796fc081 , " +
                                "where b209484c01754ef08c0e476b796fc081 is exchange " +
                                "identifier(publisher id) and last param is its site id.");

        //pick second last as the exchange identifier.
        adExchangeIdentifier = requestURIParts[requestURIParts.length-2];

        RTBExchangeRequestReader rtbExchangeRequestReader = rtbExchangeRequestReaderMap.get(adExchangeIdentifier);

        if(null == rtbExchangeRequestReader)
            throw new Exception("No valid Request Enricher could be found for exchange identifier: " +
                                adExchangeIdentifier);

        return rtbExchangeRequestReader.validateAndEnrichRequest(requestId,httpServletRequest,logger);
    }
}
