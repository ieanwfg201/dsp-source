package com.kritter.adserving.flow.job;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class sets a variable in request context so as to
 * divide traffic to following
 * EcpmBidCalculatorNotUsingBidder and
 * EcpmBidCalculatorUsingBidder jobs.
 * The decision is based on if it is exchange traffic or
 * non-exchange traffic.
 */
public class TrafficAssignerToEcpmBidCalculator implements Job
{
    private Logger logger;
    private String name;
    private String requestObjectKey;
    private String variableName;

    public TrafficAssignerToEcpmBidCalculator(
                                              String loggerName,
                                              String jobName,
                                              String requestObjectKey,
                                              String variableName
                                             )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.variableName = variableName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void execute(Context context)
    {
        Request request = (Request)context.getValue(this.requestObjectKey);

        if(null == request)
        {
            logger.error("Request is null inside TrafficAssignerToEcpmBidCalculator");
            return;
        }

        context.setValue(this.variableName,(int)request.getInventorySource());
    }
}
