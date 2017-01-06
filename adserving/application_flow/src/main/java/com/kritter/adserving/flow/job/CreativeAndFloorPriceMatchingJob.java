package com.kritter.adserving.flow.job;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.adserving.shortlisting.core.CreativeAndFloorMatchingRTBExchange;
import com.kritter.adserving.shortlisting.core.CreativeTargetingMatcher;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * This job takes care of creative matching as well as floor price matching
 * among request and ad units.
 */
public class CreativeAndFloorPriceMatchingJob implements Job
{
    private Logger logger;
    private String name;
    private String requestObjectKey;
    private String responseObjectKey;
    private CreativeTargetingMatcher creativeTargetingMatcher;
    private Map<Integer,CreativeAndFloorMatchingRTBExchange> openRTBVersionWithCreativeAndFloorMatchingInstances;

    public CreativeAndFloorPriceMatchingJob(String loggerName,
                                            String name,
                                            String requestObjectKey,
                                            String responseObjectKey,
                                            CreativeTargetingMatcher creativeTargetingMatcher,
                                            Map<Integer,CreativeAndFloorMatchingRTBExchange>
                                                    openRTBVersionWithCreativeAndFloorMatchingInstances)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.creativeTargetingMatcher = creativeTargetingMatcher;
        this.openRTBVersionWithCreativeAndFloorMatchingInstances = openRTBVersionWithCreativeAndFloorMatchingInstances;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void execute(Context context)
    {
        logger.info("Inside execute of CreativeAndFloorPriceMatchingJob ... ");

        Request request = (Request)context.getValue(this.requestObjectKey);
        Response response = (Response)context.getValue(this.responseObjectKey);

        if(null == request || null == response)
        {
            logger.error("Request, Response are null inside CreativeAndFloorPriceMatchingJob");
            return;
        }

        if(request.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode())
        {
            logger.debug("Inventory source is RTB_EXCHANGE and bid request version is:{} ",
                         request.getBidRequest().getOpenRTBVersion().getCode());

            CreativeAndFloorMatchingRTBExchange creativeAndFloorMatchingRTBExchange =
                    openRTBVersionWithCreativeAndFloorMatchingInstances.
                                            get(request.getBidRequest().getOpenRTBVersion().getCode());

            try
            {
                creativeAndFloorMatchingRTBExchange.processAdUnitsForEachBidRequestImpression(request,response, context);
            }
            catch (Exception e)
            {
                logger.error("Exception inside execute() of CreativeAndFloorPriceMatchingJob ",e);
            }
        }
        else
            creativeTargetingMatcher.processAdIdsForCreativeFiltering(request,response,request.getSite(), context);
    }
}
