package com.kritter.adserving.request.enricher;

import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.req_logging.ReqLoggingCache;
import com.kritter.req_logging.ReqLoggingCacheEntity;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private Logger bidRequestLogger;
    private ReqLoggingCache reqLoggingCache;

    public RTBExchangeRequestEnricher(String loggerName,
                                      Map<String,RTBExchangeRequestReader> rtbExchangeRequestReaderMap,
                                      String bidRequestLoggerName,
                                      ReqLoggingCache reqLoggingCache)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.rtbExchangeRequestReaderMap = rtbExchangeRequestReaderMap;
        this.bidRequestLogger = LogManager.getLogger(bidRequestLoggerName);
        this.reqLoggingCache = reqLoggingCache;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception
    {
        this.logger.info("Inside validateAndEnrichRequest() of RTBExchangeRequestEnricher");

        String requestURI = httpServletRequest.getRequestURI();

        this.logger.debug("Request URI inside RTBExchangeRequestEnricher is: {} ", requestURI);

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

        /*Fetch whether bid request needs to be logged*/
        String reqLogIdentifier = requestURIParts[requestURIParts.length-1];
        ReqLoggingCacheEntity reqLoggingCacheEntity = reqLoggingCache.query(reqLogIdentifier);
        boolean logBidRequest = false;
        if(null != reqLoggingCacheEntity)
            logBidRequest = reqLoggingCacheEntity.isEnable();

        return rtbExchangeRequestReader.validateAndEnrichRequest
                                                   (requestId,httpServletRequest,logger,this.bidRequestLogger,logBidRequest,adExchangeIdentifier);
    }
}