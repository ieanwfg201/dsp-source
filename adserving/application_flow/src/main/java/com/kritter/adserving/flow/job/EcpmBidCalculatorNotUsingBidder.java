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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
        this.logger = LogManager.getLogger(loggerName);
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

        if(null == responseAdInfos)
        {
            logger.debug("No ads to work on inside EcpmBidCalculatorNotUsingBidder, returning back.");
            return;
        }

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

            ReqLog.debugWithDebugNew(logger, request, "Ecpm bid value calculated inside EcpmBidCalculatorNotUsingBidder as : {} for adunit id: {}",
                         optimizedBidValueOfAdForThisImpression, responseAdInfo.getAdId());

            /* Check for if ecpm value meets site's floor value provided via admin user interface.
             * If realtime bid floor is provided by the requesting site then use that.
             */
            if(null != request.getBidFloorValueForNetworkSupply())
            {
                if(optimizedBidValueOfAdForThisImpression < request.getBidFloorValueForNetworkSupply().doubleValue())
                {
                    ReqLog.errorWithDebugNew(logger, request, "Adunit's ecpm value: {} ,is less than floor value: {} ,provided real time via ad request for the site: {} ,for adid : {}",
                            optimizedBidValueOfAdForThisImpression,
                            request.getBidFloorValueForNetworkSupply().doubleValue(),request.getSite().getId(),
                            responseAdInfo.getAdId());
                    finalResponseAdInfo.remove(responseAdInfo);
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(responseAdInfo.getAdId(),
                            NoFillReason.ECPM_FLOOR_UNMET.getValue(), this.adNoFillReasonMapKey, context);
                }
            }
            else if(optimizedBidValueOfAdForThisImpression <  request.getSite().getEcpmFloorValue())
            {
                ReqLog.errorWithDebugNew(logger, request, "Adunit's ecpm value: {} ,is less than floor value: {} ,for the site {} ,for adid : {}",
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
