package com.kritter.adserving.flow.job;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.request.reader.RequestProcessor;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is the init job for pre-impression workflow.
 * It keeps instance of RequestProcessor and reads + enriches the request object
 * thereby putting it into context object.
 */
public class InitJob implements Job
{
    public static String AD_STATS_LOGGING_KEY = "ad-stats-logging-enabled";

    private Logger logger;
    private String name;
    private String requestObjectKey;
    private RequestProcessor requestProcessor;
    private String isRequestForSystemDebuggingHeaderName;
    private String inventorySourceHeader;
    private String sslRequestHeader;
    private Boolean adStatsLoggingEnabled = null;

    public InitJob(
                   String loggerName,
                   String jobName,
                   String requestObjectKey,
                   RequestProcessor requestProcessor,
                   String isRequestForSystemDebuggingHeaderName,
                   String inventorySourceHeader,
                   String sslRequestHeader
                  )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.requestProcessor = requestProcessor;
        this.isRequestForSystemDebuggingHeaderName = isRequestForSystemDebuggingHeaderName;
        this.inventorySourceHeader = inventorySourceHeader;
        this.sslRequestHeader = sslRequestHeader;
    }

    public InitJob(
            String loggerName,
            String jobName,
            String requestObjectKey,
            RequestProcessor requestProcessor,
            String isRequestForSystemDebuggingHeaderName,
            String inventorySourceHeader,
            String sslRequestHeader,
            boolean adStatsLoggingEnabled
    )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.requestProcessor = requestProcessor;
        this.isRequestForSystemDebuggingHeaderName = isRequestForSystemDebuggingHeaderName;
        this.inventorySourceHeader = inventorySourceHeader;
        this.sslRequestHeader = sslRequestHeader;
        this.adStatsLoggingEnabled = adStatsLoggingEnabled;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void execute(Context context)
    {
        this.logger.info("Inside init job of PreImpressionWorkflow.");
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

        this.logger.debug("Fetched servlet-request object inside InitJob.");

        if(this.adStatsLoggingEnabled != null) {
            this.logger.debug("Setting ad stats logging enabled to {} in context.", this.adStatsLoggingEnabled);
            context.setValue(AD_STATS_LOGGING_KEY, this.adStatsLoggingEnabled);
        } else {
            this.logger.debug("Ad stats logging enabled is null. Not setting in context.");
        }

        try
        {
            //set inventory source code here as well inside the context object
            //would be used in response formatting in case there is any exception
            //in the workflow due to bad request or any other internal error.
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
            context.setValue(this.inventorySourceHeader,inventorySourceCode);
            /*************************Finished setting inventory source header**************************************/

            this.logger.debug("Going to call RequestProcessor module.");
            Request request = this.requestProcessor.processRequest(context.getUuid().toString(),httpServletRequest);

            String debugSystemForSupplyDemandMatching = httpServletRequest.getHeader(isRequestForSystemDebuggingHeaderName);

            this.logger.debug("DebugSystemHeaderValue: {} ",debugSystemForSupplyDemandMatching);

            if(null != debugSystemForSupplyDemandMatching)
            {
                try
                {
                    request.setRequestAsDebugSystemForSupplyDemandMatching
                            (Boolean.valueOf(debugSystemForSupplyDemandMatching));
                }
                catch (RuntimeException rte)
                {
                    request.setRequestAsDebugSystemForSupplyDemandMatching(false);
                }
            }

            String sslRequest = httpServletRequest.getHeader(this.sslRequestHeader);
            if(null != sslRequest) {
                this.logger.debug("Value for secure request header : {} is {}", this.sslRequestHeader, sslRequest);
                request.setSecure(Boolean.valueOf(sslRequest));
            }

            this.logger.debug("Finished getting workflow request object from RequestProcessor, Enrichment " +
                    "complete.");
            context.setValue(this.requestObjectKey, request);
        }
        catch(Exception e)
        {
            this.logger.error("Exception inside execute() of InitJob of PreImpressionWorkflow " , e);
        }

        this.logger.info("End of initJob of adserving workflow.");
    }
}
