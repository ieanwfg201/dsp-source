package com.kritter.adserving.request.reader;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.request.enricher.AggregatorRequestEnricher;
import com.kritter.adserving.request.enricher.DirectPublisherRequestEnricher;
import com.kritter.adserving.request.enricher.RTBExchangeRequestEnricher;
import com.kritter.adserving.request.enricher.RequestEnricher;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import com.kritter.constants.INVENTORY_SOURCE;

import java.util.List;

/**
 * This class reads an incoming request and identifies the inventory source
 * type, also puts in the http-request data into request object along with
 * calling the appropriate enricher.
 *
 * The inventory source identifier is set by nginx/loadbalancer, its not
 * entirely possible to receive id param from request so keep the endpoint
 * separate for different inventory sources and set the appropriate header.
 */
public class RequestProcessor
{
    private static final String REQUEST_PARAMETER_NAME_FOR_ENRICHER_OVERRIDE="enricher_override";
    private final Logger logger;
    private final String inventorySourceHeader;
    private final DirectPublisherRequestEnricher directPublisherRequestEnricher;
    private final RequestEnricher aggregatorRequestEnricher;
    private final RTBExchangeRequestEnricher rtbExchangeRequestEnricher;
    private final List<? extends RequestEnricher> alternativeRequestEnrichers;

    public RequestProcessor(
                            String loggerName,
                            String inventorySourceHeader,
                            DirectPublisherRequestEnricher directPublisherRequestEnricher,
                            RequestEnricher aggregatorRequestEnricher,
                            RTBExchangeRequestEnricher rtbExchangeRequestEnricher,
                            List<? extends RequestEnricher> alternativeRequestEnrichers
                           )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.inventorySourceHeader = inventorySourceHeader;
        this.directPublisherRequestEnricher = directPublisherRequestEnricher;
        this.aggregatorRequestEnricher = aggregatorRequestEnricher;
        this.rtbExchangeRequestEnricher = rtbExchangeRequestEnricher;
        this.alternativeRequestEnrichers = alternativeRequestEnrichers;
    }

    public Request processRequest(String requestId, HttpServletRequest httpServletRequest) throws Exception
    {
        this.logger.info("Inside processRequest() method of RequestProcessor");
        String inventorySource = httpServletRequest.getHeader(this.inventorySourceHeader);
        short inventorySourceCode = -1;

        if(null==inventorySource)
            throw new Exception("Inventory source header is not set.Cannot proceed.");

        try
        {
            inventorySourceCode = Short.parseShort(inventorySource);
        }
        catch(NumberFormatException e)
        {
            throw new Exception("Inventory source header value is not an integer, cannot proceed." ,e);
        }

        ApplicationGeneralUtils.logDebug(this.logger," The inventory source code is : ", String.valueOf(inventorySourceCode));

        RequestEnricher requestEnricher = deturminePrimaryRequestEnricher(inventorySourceCode);
        RequestEnricher alternativeRequestEnricher = deturmineAlternativeRequestEnricher(httpServletRequest);

        /**
         *      Both are null               => return null
         *      Only alternate is not null  => use alternate
         *      Neither are null            => use alternate
         *      Only primary is not null    => use primary
         */

        if(alternativeRequestEnricher == null && requestEnricher != null){
            this.logger.debug("Inside " + requestEnricher.getClass().getSimpleName() +
                    " scope, calling enricher " +
                    requestEnricher.getClass().getSimpleName());
            return requestEnricher.validateAndEnrichRequest(requestId,httpServletRequest,this.logger);
        }
        if(alternativeRequestEnricher == null && requestEnricher == null){
            this.logger.debug("Unknown integration scope");
            return null;
        }
        if(alternativeRequestEnricher != null && requestEnricher == null){
            this.logger.debug("Unknown integration scope, but identified alternative enricher, calling " +
                    alternativeRequestEnricher.getClass().getSimpleName());
            return alternativeRequestEnricher.validateAndEnrichRequest(requestId,httpServletRequest,this.logger);
        }
        if(alternativeRequestEnricher != null && requestEnricher != null){
            this.logger.debug("Inside " + requestEnricher.getClass().getSimpleName() +
                    " scope, but calling alternative enricher " +
                    alternativeRequestEnricher.getClass().getSimpleName());
            return alternativeRequestEnricher.validateAndEnrichRequest(requestId,httpServletRequest,this.logger);
        }


        this.logger.debug("Inside " + requestEnricher.getClass().getSimpleName() + " integration scope, " +
                "calling " + requestEnricher.getClass().getSimpleName() + ". ");
        return requestEnricher.validateAndEnrichRequest(requestId,httpServletRequest,logger);
    }

    private RequestEnricher deturminePrimaryRequestEnricher(int inventorySourceCode){
        if(inventorySourceCode == INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode()){
            return this.directPublisherRequestEnricher;
        }
        if(inventorySourceCode == INVENTORY_SOURCE.AGGREGATOR.getCode()){
            return this.aggregatorRequestEnricher;
        }
        if(inventorySourceCode == INVENTORY_SOURCE.RTB_EXCHANGE.getCode()){
            return this.rtbExchangeRequestEnricher;
        }
        return null;
    }
    private RequestEnricher deturmineAlternativeRequestEnricher(HttpServletRequest request){

        if(null == this.alternativeRequestEnrichers || this.alternativeRequestEnrichers.size() == 0)
            return null;

        String overrideClassName = request.getParameter(RequestProcessor.REQUEST_PARAMETER_NAME_FOR_ENRICHER_OVERRIDE);
        if(StringUtils.isBlank(overrideClassName)){
            overrideClassName = request.getHeader(RequestProcessor.REQUEST_PARAMETER_NAME_FOR_ENRICHER_OVERRIDE);
        }
        overrideClassName = StringUtils.trimToNull(overrideClassName);
        if(StringUtils.isBlank(overrideClassName)){
            return null;
        }
        for(RequestEnricher requestEnricher : this.alternativeRequestEnrichers){
            if(StringUtils.equals(requestEnricher.getClass().getSimpleName(),overrideClassName)){
                return requestEnricher;
            }
        }

        return null;
    }
}

