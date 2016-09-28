package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.Budget;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Campaign;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class CampaignFlightDatesAndBudgetShortlisting implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.CAMPAIGN_DATE_BUDGET;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private CampaignCache campaignCache;
    private String adNoFillReasonMapKey;

    public CampaignFlightDatesAndBudgetShortlisting(String name, String loggerName, AdEntityCache adEntityCache,
                                                    CampaignCache campaignCache, String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.campaignCache = campaignCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForCampaignFlightDatesAndBudget of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForCampaignFlightDatesAndBudget of AdTargetingMatcher...");

        Set<Integer> activeAdIds = new HashSet<Integer>();

        for(Integer adId: adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger,request, "AdEntity not found in cache,FATAL error!!! for ad id: {} " , adId);
                continue;
            }

            int campaignId = adEntity.getCampaignIncId();

            Campaign campaign = campaignCache.query(campaignId);

            if(null == campaign)
            {
                ReqLog.debugWithDebugNew(logger,request, "Campaign not found in cache,FATAL error!!! for campaign id: {} ",
                        campaignId);

                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
                continue;
            }

            long currentTime = System.currentTimeMillis();

            StringBuffer logMessage = new StringBuffer();

            if(
                    (campaign.getStartDate().getTime() <= currentTime) &&
                            (campaign.getEndDate().getTime() >= currentTime) &&
                            (campaign.getDailyBudgetRemaining() > Budget.min_budget) &&
                            (campaign.getCampaignTotalBudgetRemaining() > Budget.min_budget) &&
                            (campaign.getAccountTotalBudgetRemaining() > Budget.min_budget) &&
                            (campaign.getAccountAdvertiserTotalBudgetRemaining() > Budget.min_budget) &&
                            (campaign.getCampaignAdvertiserDailyRemainingBudget() > Budget.min_budget) &&
                            (campaign.getCampaignAdvertiserTotalRemainingBudget() > Budget.min_budget)
                    )
            {
                logMessage.append("The campaign is valid to go ahead in terms of flight dates and budget values,CampaignId: ");
                logMessage.append(campaign.getCampaignGuid());

                ReqLog.debugWithDebugNew(logger,request, logMessage.toString());

                activeAdIds.add(adId);
            }
            else
            {
                logMessage.setLength(0);
                logMessage.append("The campaign FAILS flight dates and budget checks,CampaignId: ");
                logMessage.append(campaign.getCampaignGuid());
                
                logger.debug(logMessage.toString());
                if(request.isRequestForSystemDebugging()){
                	request.addDebugMessageForTestRequest(logMessage.toString());
                }

                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }
        }

        if(null == request.getNoFillReason() && activeAdIds.size() <= 0)
            request.setNoFillReason(noFillReason);

        return activeAdIds;
    }
}
