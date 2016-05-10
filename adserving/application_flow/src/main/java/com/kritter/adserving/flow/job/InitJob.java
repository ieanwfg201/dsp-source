package com.kritter.adserving.flow.job;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.request.reader.RequestProcessor;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.user.userid.UserIdProvider;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * This class is the init job for pre-impression workflow.
 * It keeps instance of RequestProcessor and reads + enriches the request object
 * thereby putting it into context object.
 */
public class InitJob implements Job
{
    private Logger logger;
    private String name;
    private String requestObjectKey;
    private RequestProcessor requestProcessor;
    private String isRequestForSystemDebuggingHeaderName;
    private String inventorySourceHeader;
    private UserIdProvider userIdProvider;

    public InitJob(
                   String loggerName,
                   String jobName,
                   String requestObjectKey,
                   RequestProcessor requestProcessor,
                   String isRequestForSystemDebuggingHeaderName,
                   String inventorySourceHeader,
                   UserIdProvider userIdProvider
                  )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.requestProcessor = requestProcessor;
        this.isRequestForSystemDebuggingHeaderName = isRequestForSystemDebuggingHeaderName;
        this.inventorySourceHeader = inventorySourceHeader;
        this.userIdProvider = userIdProvider;
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

            if(this.userIdProvider != null) {
                Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
                if(externalUserIds != null) {
                    // Find internal user id for the supplied external user ids and set the internal user id in the
                    // request
                    String internalUserId = this.userIdProvider.getInternalUserId(externalUserIds);
                    request.setUserId(internalUserId);
                }
            }

            this.logger.debug("Finished getting workflow request object from RequestProcessor,Enrichment complete.");
            context.setValue(this.requestObjectKey, request);

        }
        catch(Exception e)
        {
            this.logger.error("Exception inside execute() of InitJob of PreImpressionWorkflow " , e);
        }

        this.logger.info("End of initJob of adserving workflow.");
    }
}
