package com.kritter.adserving.flow.job;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.bidder.online.bidcalculator.RealTimeBidder;
import com.kritter.bidder.online.entity.ExchangeBidWithEntityId;
import com.kritter.bidder.online.entity.ServedEntityInfo;
import com.kritter.constants.BidType;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.utils.common.AdNoFillStatsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * This class calculates ecpm bid value for each ad unit shortlisted.
 * The ecpm bid value is required for two purposes:
 * a. ranking
 * b. to bid on exchange.
 *
 * This class uses some real time bidder implementation which
 * internally might be using offline bidder output.
 * Along with it this class uses ctr and cpa value in doing so.
 *
 * This class can be utilized to use different online bidder
 * implementations and appropriately granted traffic.
 */
public class EcpmBidCalculatorUsingBidder implements Job
{
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private RealTimeBidder realTimeBidder;
    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public EcpmBidCalculatorUsingBidder(
                                        String loggerName,
                                        String name,
                                        String requestObjectKey,
                                        String responseObjectKey,
                                        RealTimeBidder realTimeBidder,
                                        AdEntityCache adEntityCache,
                                        String adNoFillReasonMapKey
                                       )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = name;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.realTimeBidder = realTimeBidder;
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public String getName()
    {
        return jobName;
    }

    /**
     * This function uses ResponseAdInfo object collection and sets bid into each ad entity
     * as suggested by the online bidder.
     * @param context Request context
     */
    @Override
    public void execute(Context context)
    {
        logger.info("Inside execute of EcpmBidCalculatorUsingBidder");

        Response response = (Response)context.getValue(responseObjectKey);
        Request request = (Request)context.getValue(requestObjectKey);

        if(null == request || null == response)
        {
            logger.debug("Request, Response are null inside EcpmBidCalculatorUsingBidder");
            return;
        }

        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();
        Set<ResponseAdInfo> finalResponseAdInfoSet = new HashSet<ResponseAdInfo>();
        Set<ServedEntityInfo> servedEntityInfosForBidder = new HashSet<ServedEntityInfo>(responseAdInfos.size());

        for(ResponseAdInfo responseAdInfo : responseAdInfos)
        {
            ServedEntityInfo servedEntityInfo = new ServedEntityInfo(responseAdInfo.getAdId(),
                                                                     responseAdInfo.getHardBid(),
                                                                     responseAdInfo.getDailyRemainingBudget(),
                                                                     responseAdInfo.getCtrValue(),
                                                                     responseAdInfo.getCpaValue(),
                                                                     responseAdInfo.getMarketPlace());
            servedEntityInfosForBidder.add(servedEntityInfo);
        }

        Map<Integer,ExchangeBidWithEntityId> exchangeBidWithEntityIdMap =
                                             realTimeBidder.calculateBidForEntities(servedEntityInfosForBidder);

        for(ResponseAdInfo responseAdInfo : responseAdInfos)
        {
            boolean isBidSet = false;
            if(adEntityCache != null){
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());
                if(adEntity != null && BidType.MANUAL.getCode() == adEntity.getBidtype()){
                    responseAdInfo.setEcpmValue(adEntity.getBid());
                    isBidSet = true;
                }
            }
            
            if(!isBidSet){
                ExchangeBidWithEntityId exchangeBidWithEntityId = exchangeBidWithEntityIdMap.get(responseAdInfo.getAdId());
                if(null == exchangeBidWithEntityId) {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(responseAdInfo.getAdId(),
                            NoFillReason.CAMPAIGN_DATE_BUDGET.getValue(), this.adNoFillReasonMapKey, context);
                    continue;
                }

                double ecpmValue = exchangeBidWithEntityIdMap.get(responseAdInfo.getAdId()).getBidValue();
                responseAdInfo.setEcpmValue(ecpmValue);
            }
            finalResponseAdInfoSet.add(responseAdInfo);
            ReqLog.errorWithDebugNew(logger, request, "Ecpm bid value calculated inside EcpmBidCalculatorUsingBidder as : {} for adunit id: {}",
                    responseAdInfo.getEcpmValue(), responseAdInfo.getAdId());
        }

        response.setResponseAdInfo(finalResponseAdInfoSet);

        if(finalResponseAdInfoSet.size() <= 0 && null == request.getNoFillReason())
        {
            request.setNoFillReason(NoFillReason.BIDDER_BID_NEGATIVE_ZERO);
            ReqLog.errorWithDebugNew(logger, request, "Bidder bid is calculated as negative or zero in this request and no ad could get a positive ecpm value ");
            
        }
    }
}
