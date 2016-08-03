package com.kritter.adserving.flow.job;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.constants.Budget;
import com.kritter.constants.MarketPlace;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.utils.common.AdNoFillStatsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class calculates ecpm bid value for each ad unit shortlisted.
 * The ecpm bid value is required for:
 * a. ranking
 *
 * This class uses ctr and cpa value in doing so.
 */

public class EcpmBidCalculatorNotUsingBidder implements Job
{
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private String adNoFillReasonMapKey;

    public EcpmBidCalculatorNotUsingBidder(
                                           String loggerName,
                                           String name,
                                           String requestObjectKey,
                                           String responseObjectKey,
                                           String adNoFillReasonMapKey
                                          )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = name;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public String getName()
    {
        return jobName;
    }

    /**
     * This function uses ResponseAdInfo object collection and sets ecpm bid calculated
     * using ctr and cpa informations.
     * @param context Request context
     */
    @Override
    public void execute(Context context)
    {
        logger.info("Inside execute of EcpmBidCalculatorNotUsingBidder");

        Request request = (Request)context.getValue(requestObjectKey);
        Response response = (Response)context.getValue(responseObjectKey);

        if(null == request || null == response)
        {
            logger.error("Request, Response are null inside EcpmBidCalculatorNotUsingBidder");
            return;
        }

        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();
        Set<ResponseAdInfo> finalResponseAdInfo = new HashSet<ResponseAdInfo>(responseAdInfos);

        for(ResponseAdInfo responseAdInfo : responseAdInfos)
        {
            double clickProbabilityOfEntityForThisImpression = responseAdInfo.getCtrValue();
            double cpaProbabilityForEntityForThisImpression  = responseAdInfo.getCpaValue();

            double optimizedBidValueOfAdForThisImpression = responseAdInfo.getHardBid();

            logger.debug("The internal bid value is : {} for ad id: {}",
                         optimizedBidValueOfAdForThisImpression, responseAdInfo.getAdId());

            //in case of cpm ads, the ecpm bid value remains as the same price per 1000.

            if(responseAdInfo.getMarketPlace().equals(MarketPlace.CPC))
            {
                optimizedBidValueOfAdForThisImpression = (
                                                          optimizedBidValueOfAdForThisImpression    *
                                                          clickProbabilityOfEntityForThisImpression *
                                                          1000
                                                         );
                logger.debug("Inside EcpmBidCalculatorNotUsingBidder for adId:{} , CTR used as : {} " ,
                             responseAdInfo.getAdId(),clickProbabilityOfEntityForThisImpression);
            }
            else if(responseAdInfo.getMarketPlace().equals(MarketPlace.CPD))
            {
                optimizedBidValueOfAdForThisImpression = (
                                                          optimizedBidValueOfAdForThisImpression    *
                                                          clickProbabilityOfEntityForThisImpression *
                                                          cpaProbabilityForEntityForThisImpression  *
                                                          1000
                                                         );

                logger.debug("Inside EcpmBidCalculatorNotUsingBidder for adId:{} , CTR used as : {} ,CPA used as:{} " ,
                             responseAdInfo.getAdId(),
                             clickProbabilityOfEntityForThisImpression,cpaProbabilityForEntityForThisImpression);
            }

            responseAdInfo.setEcpmValue(optimizedBidValueOfAdForThisImpression);

            ReqLog.debugWithDebug(logger, request, "Ecpm bid value calculated inside EcpmBidCalculatorNotUsingBidder as : {} for adunit id: {}",
                         optimizedBidValueOfAdForThisImpression, responseAdInfo.getAdId());

            //check for budget remaining of the entity, if less , then ad cant participate in final selection.

            Double budgetRemainingForeseen = responseAdInfo.getDailyRemainingBudget() -
                                             responseAdInfo.getAdvertiserBid();

            if( budgetRemainingForeseen < Budget.min_budget )
            {
                ReqLog.errorWithDebug(logger, request, "Adunit's budget : {} is less than minimum value inside EcpmBidCalculatorNotUsingBidder for adid : {} , removing this ad from final list... ",
                             budgetRemainingForeseen ,responseAdInfo.getAdId());
                finalResponseAdInfo.remove(responseAdInfo);
                continue;
            }

            //check for if ecpm value meets site floor ...
            if(optimizedBidValueOfAdForThisImpression <  request.getSite().getEcpmFloorValue())
            {
                ReqLog.errorWithDebug(logger, request, "Adunit's ecpm value: {} ,is less than floor value: {} ,for the site {} ,for adid : {}",
                             optimizedBidValueOfAdForThisImpression,
                             request.getSite().getEcpmFloorValue(),request.getSite().getId(),
                             responseAdInfo.getAdId());
                finalResponseAdInfo.remove(responseAdInfo);
                AdNoFillStatsUtils.updateContextForNoFillOfAd(responseAdInfo.getAdId(),
                        NoFillReason.ECPM_FLOOR_UNMET.getValue(), this.adNoFillReasonMapKey, context);
            }
        }

        if(finalResponseAdInfo.size() <= 0 && null == request.getNoFillReason())
            request.setNoFillReason(NoFillReason.ECPM_FLOOR_UNMET);

        //set final response-ad-info set into response.
        response.setResponseAdInfo(finalResponseAdInfo);
    }
}
