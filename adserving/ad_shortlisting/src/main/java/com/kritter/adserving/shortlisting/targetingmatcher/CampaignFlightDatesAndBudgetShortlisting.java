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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.campaignCache = campaignCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context)
    {
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
                // If campaign's payout currently exceeds the total burn, drop it.
                // TODO : Remove once we have a proper solution in place
                if(campaign.getCampaignDailyBudget() != null)
                {
                    double dailyBudgetRemaining = campaign.getDailyBudgetRemaining() == null ? 0.0 :
                            campaign.getDailyBudgetRemaining();
                    double campaignBurn = campaign.getCampaignDailyBudget() - dailyBudgetRemaining;
                    double totalPotentialPayout = campaign.getCampaignPayout() + campaign.getCurrentPayout();

                    ReqLog.debugWithDebugNew(logger, request, "For campaign id : {}, daily budget remaining : {}, " +
                            "current campaign payout : {}, daily budget remaining : {}.", campaign.getId(),
                            dailyBudgetRemaining, campaign.getCurrentPayout(), campaign.getCampaignDailyBudget());

                    if(campaign.getAbsolutePayoutThreshold() != null && campaign.getAbsolutePayoutThreshold() > 0) {
                        if(campaignBurn + campaign.getAbsolutePayoutThreshold() < totalPotentialPayout) {
                            ReqLog.debugWithDebugNew(logger, request, "For campaign id : {}, current payout {} " +
                                    "exceeds burn {} by absolute threshold {}. Will get started again after cache " +
                                    "refresh. Dropping it.", campaignId, totalPotentialPayout, campaignBurn,
                                    campaign.getAbsolutePayoutThreshold());

                            AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                    this.adNoFillReasonMapKey, context);

                            continue;
                        }
                    }

                    if(campaign.getPercentPayoutThreshold() != null && campaign.getPercentPayoutThreshold() > 0) {
                        double percentThreshold =
                                campaign.getPercentPayoutThreshold() * campaign.getCampaignDailyBudget() / 100;
                        if (campaignBurn + percentThreshold < totalPotentialPayout) {
                            ReqLog.debugWithDebugNew(logger, request, "For campaign id : {}, current payout {} " +
                                    "exceeds burn {} by percent threshold {}. Will get started again after cache " +
                                    "refresh. Dropping it.", campaignId, totalPotentialPayout, campaignBurn,
                                    campaign.getPercentPayoutThreshold());

                            AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                    this.adNoFillReasonMapKey, context);

                            continue;
                        }
                    }
                }
                else
                {
                    ReqLog.debugWithDebugNew(logger, request, "For campaign id : {}, daily budget is null.",
                                             campaign.getId());
                }

                ReqLog.debugWithDebugNew(logger,request,"The campaign is valid to go ahead in terms of flight dates " +
                                         "and budget values,CampaignId: {} ",campaign.getCampaignGuid());

                activeAdIds.add(adId);
            }
            else
            {
                logger.debug("The campaign FAILS flight dates and budget checks,CampaignId: {} ",
                              campaign.getCampaignGuid());

                if(request.isRequestForSystemDebugging())
                {
                    request.addDebugMessageForTestRequest("The campaign FAILS flight dates and budget checks,CampaignId: ");
                    request.addDebugMessageForTestRequest(campaign.getCampaignGuid());
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
