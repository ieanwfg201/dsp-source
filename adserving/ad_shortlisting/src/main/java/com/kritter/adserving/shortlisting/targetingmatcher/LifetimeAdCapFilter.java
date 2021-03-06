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
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.user.thrift.struct.LifetimeDemandHistory;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public abstract class LifetimeAdCapFilter implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private LifetimeDemandHistoryProvider lifetimeAdHistoryProvider;
    private String adNoFillReasonMapKey;

    public LifetimeAdCapFilter(String name,
                               String loggerName,
                               AdEntityCache adEntityCache,
                               LifetimeDemandHistoryProvider lifetimeAdImpHistoryProvider,
                               String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.lifetimeAdHistoryProvider = lifetimeAdImpHistoryProvider;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        ReqLog.debugWithDebugNew(logger,request, "Inside {} of AdTargetingMatcher...", getName());

        if(adIdSet == null || adIdSet.size() == 0) {
            ReqLog.debugWithDebugNew(logger,request, "Ad id set is null or empty. Returning.");
            return adIdSet;
        }

        String kritterUserId = request.getUserId();

        // User history cache is unavailable. Drop all the capped ads since we don't have any user
        // related information.
        if(kritterUserId == null || null == lifetimeAdHistoryProvider) {
            Set<Integer> cappedAds = LifetimeCapUtils.getCappedAds(this.adEntityCache, adIdSet, getEventType());
            for(int adId : cappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, NoFillReason.USER_ID_ABSENT.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            if(kritterUserId == null) {
                ReqLog.debugWithDebugNew(logger,request, "User info not available for this request. Allowing only non " +
                        "life time impression capped ads");
            } else {
                ReqLog.debugWithDebugNew(logger,request, "Recent history cache is not available. So allowing only non " +
                        "life time impression capped ads");
            }

            Set<Integer> shortlistedAdIdSet = LifetimeCapUtils.getNonCappedAds(this.adEntityCache, adIdSet,
                    getEventType());
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(NoFillReason.USER_ID_ABSENT);
            return shortlistedAdIdSet;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        try {
            LifetimeDemandHistory history = lifetimeAdHistoryProvider.fetchLifetimeDemandHistoryForUser(
                    kritterUserId);

            /*If there is no history then user can be shown all ads.*/
            if (null == history) {
                ReqLog.debugWithDebugNew(logger,request, "History for userId: {} is null ,allowing " +
                        "all ads", kritterUserId);
                return adIdSet;
            }

            ReqLog.debugWithDebugNew(logger, request, "Fetched user history for user id : {}. History : {}.",
                    kritterUserId, history);

            for(int adId : adIdSet) {
                AdEntity adEntity = this.adEntityCache.query(adId);
                int frequencyCapCount = -1;
                // Get the capped ad and see if the cap has already been hit. If yes, drop the ad
                FreqCap freqCap = adEntity.getFrequencyCap();
                if(freqCap != null && freqCap.getFDef() != null && freqCap.getFDef().containsKey(getEventType())) {
                    Set<FreqDef> frequencyDefinition = freqCap.getFDef().get(getEventType());
                    for(FreqDef def : frequencyDefinition) {
                        if(def.getDuration() == FreqDuration.LIFE) {
                            frequencyCapCount = def.getCount();
                            ReqLog.debugWithDebugNew(logger, request, "Ad id : {} has life time impression cap : {}.",
                                    adId, frequencyCapCount);
                        } else {
                            ReqLog.debugWithDebugNew(logger, request, "Ad id : {} doesn't have life time impression cap.",
                                    adId);
                        }
                    }
                } else {
                    ReqLog.debugWithDebugNew(logger, request, "Ad id : {} doesn't have life time impression cap.", adId);
                }

                if(frequencyCapCount == -1) {
                    // Ad is not capped.
                    ReqLog.debugWithDebugNew(logger, request, "Ad id : {} doesn't have life time impression cap. Adding " +
                            "it to shortlisted ads.", adId);
                    adIdSetToReturn.add(adId);
                } else {
                    if(history.demandEventCount.containsKey(adId)) {
                        long currentCount = history.demandEventCount.get(adId);
                        ReqLog.debugWithDebugNew(logger, request, "Life time impression count for user id : {} and ad id" +
                                " : {} = {}", adId, kritterUserId, currentCount);
                        if(currentCount >= frequencyCapCount) {
                            ReqLog.debugWithDebugNew(logger, request, "Lifetime impression cap has been hit for ad id : " +
                                    "{}. Dropped the ad.", adId);
                            AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, getNoFillReason().getValue(),
                                    this.adNoFillReasonMapKey, context);
                        } else{
                            ReqLog.debugWithDebugNew(logger, request, "Lifetime impression cap has not been hit. Adding " +
                                    "ad id : {} to shortlisted ads", adId);
                            adIdSetToReturn.add(adId);
                        }
                    } else {
                        ReqLog.debugWithDebugNew(logger, request, "Ad id : {} doesn't have life time impression count " +
                                "in user history. Adding it to shortlisted ads.", adId);
                        adIdSetToReturn.add(adId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception occur while trying to fetch user history. {}", e);
            ReqLog.debugWithDebugNew(logger,request, "Exception inside LifetimeAdImpCapFilter reason: {}  ", e);

            Set<Integer> cappedAds = LifetimeCapUtils.getCappedAds(this.adEntityCache, adIdSet, getEventType());
            for(Integer adId : cappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, getNoFillReason().getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            Set<Integer> shortlistedAdIdSet = LifetimeCapUtils.getNonCappedAds(this.adEntityCache, adIdSet,
                    getEventType());
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(getNoFillReason());
            return shortlistedAdIdSet;
        }

        return adIdSetToReturn;
    }

    public abstract FreqEventType getEventType();

    public abstract NoFillReason getNoFillReason();
}
