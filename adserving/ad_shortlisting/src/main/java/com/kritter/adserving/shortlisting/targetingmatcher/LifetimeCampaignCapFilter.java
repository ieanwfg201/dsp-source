package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.shortlisting.utils.LifetimeCapUtils;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.FreqEventType;
import com.kritter.core.workflow.Context;
import com.kritter.entity.freqcap_entity.FreqCap;
import com.kritter.entity.freqcap_entity.FreqDef;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.recenthistory.LifetimeDemandHistoryProvider;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Campaign;
import com.kritter.user.thrift.struct.LifetimeDemandHistory;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class LifetimeCampaignCapFilter implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private CampaignCache campaignCache;
    private LifetimeDemandHistoryProvider lifetimeDemandHistoryProvider;
    private String adNoFillReasonMapKey;

    public LifetimeCampaignCapFilter(String name,
                                     String loggerName,
                                     AdEntityCache adEntityCache,
                                     CampaignCache campaignCache,
                                     LifetimeDemandHistoryProvider lifetimeDemandHistoryProvider,
                                     String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.campaignCache = campaignCache;
        this.lifetimeDemandHistoryProvider = lifetimeDemandHistoryProvider;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }


    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        ReqLog.debugWithDebug(logger,request, "Inside {} of AdTargetingMatcher...", getName());

        if(adIdSet == null || adIdSet.size() == 0) {
            ReqLog.debugWithDebug(logger,request, "Ad id set is null or empty. Returning.");
            return adIdSet;
        }

        String kritterUserId = request.getUserId();

        // User history cache is unavailable. Drop all the capped ads since we don't have any user
        // related information.
        if(kritterUserId == null || null == lifetimeDemandHistoryProvider) {
            Set<Integer> cappedAds = LifetimeCapUtils.getCappedCampaigns(adEntityCache, campaignCache, adIdSet,
                    getEventType());
            for(int adId : cappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, getNoFillReason().getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            if(kritterUserId == null) {
                ReqLog.debugWithDebug(logger,request, "User info not available for this request. Allowing only non " +
                        "life time impression capped ads");
            } else {
                ReqLog.debugWithDebug(logger,request, "Recent history cache is not available. So allowing only non " +
                        "life time impression capped ads");
            }

            Set<Integer> shortlistedAdIdSet = LifetimeCapUtils.getNonCappedCampaigns(this.adEntityCache,
                    this.campaignCache, adIdSet, getEventType());
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(getNoFillReason());
            return shortlistedAdIdSet;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        try {
            LifetimeDemandHistory history = lifetimeDemandHistoryProvider.fetchLifetimeDemandHistoryForUser(
                    kritterUserId);

            /*If there is no history then user can be shown all ads.*/
            if (null == history) {
                ReqLog.debugWithDebug(logger,request, "History for userId: {} is null ,allowing " +
                        "all ads", kritterUserId);
                return adIdSet;
            }


            ReqLog.debugWithDebug(logger, request, "Fetched user history for user id : {}. History : {}.",
                    kritterUserId, history);

            for(int adId : adIdSet) {
                AdEntity adEntity = this.adEntityCache.query(adId);
                int campaignId = adEntity.getCampaignIncId();
                Campaign campaign = campaignCache.query(campaignId);
                int frequencyCapCount = -1;

                // Get the capped ad and see if the cap has already been hit. If yes, drop the ad
                FreqCap freqCap = campaign.getFrequencyCap();
                if(freqCap != null && freqCap.getFDef() != null && freqCap.getFDef().containsKey(getEventType())) {
                    Set<FreqDef> frequencyDefinition = freqCap.getFDef().get(getEventType());
                    for(FreqDef def : frequencyDefinition) {
                        if(def.getDuration() == FreqDuration.LIFE) {
                            frequencyCapCount = def.getCount();
                            ReqLog.debugWithDebug(logger, request, " Ad id : {}, campaign id : {} has life time " +
                                    "impression cap : {}.", adId, campaignId, frequencyCapCount);
                        } else {
                            ReqLog.debugWithDebug(logger, request, "Ad id : {}, campaign id : {} doesn't have life " +
                                    "time impression cap.", adId, campaignId);
                        }
                    }
                } else {
                    ReqLog.debugWithDebug(logger, request, "Ad id : {}, campaign id : {} doesn't have life " +
                            "time impression cap.", adId, campaignId);
                }

                if(frequencyCapCount == -1) {
                    // Ad is not capped.
                    ReqLog.debugWithDebug(logger, request, "Campaign id : {}, for ad id : {} doesn't have life time " +
                            "impression cap. Adding it to shortlisted ads.", campaignId, adId);
                    adIdSetToReturn.add(adId);
                } else {
                    if(history.demandEventCount.containsKey(campaignId)) {
                        long currentCount = history.demandEventCount.get(campaignId);
                        ReqLog.debugWithDebug(logger, request, "Life time impression count for user id : {}, ad id" +
                                " : {}, campaign id : {} = {}", adId, kritterUserId, campaignId, currentCount);
                        if(currentCount > frequencyCapCount) {
                            ReqLog.debugWithDebug(logger, request, "Lifetime impression cap has been hit for ad id : " +
                                    "{}, campaign id : {}. Dropped the ad.", adId, campaignId);
                            AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, getNoFillReason().getValue(),
                                    this.adNoFillReasonMapKey, context);
                        } else{
                            ReqLog.debugWithDebug(logger, request, "Lifetime impression cap has not been hit. Adding " +
                                    "ad id : {}, campaign id : {} to shortlisted ads", adId, campaignId);
                            adIdSetToReturn.add(adId);
                        }
                    } else {
                        ReqLog.debugWithDebug(logger, request, "Ad id : {}, campaign id : {} doesn't have life time " +
                                "impression count in user history. Adding it to shortlisted ads.", adId, campaignId);
                        adIdSetToReturn.add(adId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception occur while trying to fetch user history. {}", e);
            ReqLog.debugWithDebug(logger,request, "Exception inside LifetimeAdImpCapFilter reason: {}  ", e);

            Set<Integer> cappedAds = LifetimeCapUtils.getCappedCampaigns(adEntityCache, campaignCache, adIdSet,
                    getEventType());
            for(int adId : cappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, getNoFillReason().getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            Set<Integer> shortlistedAdIdSet = LifetimeCapUtils.getNonCappedCampaigns(this.adEntityCache,
                    this.campaignCache, adIdSet, getEventType());
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(getNoFillReason());
            return shortlistedAdIdSet;
        }

        return adIdSetToReturn;
    }

    public abstract FreqEventType getEventType();

    public abstract NoFillReason getNoFillReason();
}
