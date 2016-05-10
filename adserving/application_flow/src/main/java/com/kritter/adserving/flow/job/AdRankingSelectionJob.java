package com.kritter.adserving.flow.job;

import com.kritter.adserving.adrankselect.common.AdRankingSelection;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This class uses implementation of AdRankingSelection to rank
 * and sort ads in their decreasing priority of ranks.
 *
 * This class can be utilized to use different implementations
 * of ad ranking and granted traffic appropriately by means of
 * jobsets.
 */
public class AdRankingSelectionJob implements Job
{
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private AdRankingSelection adRankingSelection;

    public AdRankingSelectionJob(
                                 String loggerName,
                                 String jobName,
                                 String requestObjectKey,
                                 String responseObjectKey,
                                 AdRankingSelection adRankingSelection
                                )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.adRankingSelection = adRankingSelection;
    }

    @Override
    public String getName()
    {
        return this.jobName;
    }

    /**
     * This method sets ,finally selected ad units after ranking, into response object.
     * @param context
     */
    @Override
    public void execute(Context context)
    {
        Request request = (Request)context.getValue(this.requestObjectKey);
        Response response = (Response)context.getValue(this.responseObjectKey);

        if(null == request || null == response)
        {
            logger.error("Request, Response are null inside AdRankingSelectionJob");
            return;
        }

        ResponseAdInfo[] selectedAdInfos = this.adRankingSelection.rankAdUnitsInDecreasingOrder(request,response);

        response.setResponseAdInfo(new HashSet<ResponseAdInfo>(Arrays.asList(selectedAdInfos)));
    }
}
