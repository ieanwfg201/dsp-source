package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.FreqEventType;
import com.kritter.entity.freqcap_entity.FreqCap;
import com.kritter.entity.freqcap_entity.FreqDef;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.recenthistory.RecentHistoryProvider;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.user.thrift.struct.ImpressionEvent;
import com.kritter.user.thrift.struct.RecentImpressionHistory;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;

public class FrequencyCapFilter implements TargetingMatcher {
    private static final long HOUR_TO_MILLISECONDS_UNIT = 60 * 60 * 1000;
    private static final NoFillReason noFillReason = NoFillReason.FREQUENCY_CAP;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private RecentHistoryProvider recentHistoryProvider;
    private String adNoFillReasonMapKey;

    public FrequencyCapFilter(String name,
                              String loggerName,
                              AdEntityCache adEntityCache,
                              RecentHistoryProvider recentHistoryProvider,
                              String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.recentHistoryProvider = recentHistoryProvider;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    /**
     * Given a set of ads which don't have frequency capping enabled
     * @param adIdSet Set of ids of all ads
     * @return Set of ad ids which don't have frequency capping
     */
    private Set<Integer> getNonFrequencyCappedAds(Set<Integer> adIdSet, Request request) {
        if(adIdSet == null || adIdSet.size() == 0)
            return adIdSet;

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);

            int[] frequencyCapDetails = getFrequencyCapDetailsForAd(adEntity, request);
            if(frequencyCapDetails == null) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Given a set of ads which don't have frequency capping enabled
     * @param adIdSet Set of ids of all ads
     * @return Set of ad ids which don't have frequency capping
     */
    private Set<Integer> getFrequencyCappedAds(Set<Integer> adIdSet, Request request) {
        if(adIdSet == null || adIdSet.size() == 0)
            return new HashSet<Integer>();

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);

            int[] frequencyCapDetails = getFrequencyCapDetailsForAd(adEntity, request);
            if(frequencyCapDetails != null) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Converts the impression event list into a map. The map contains the mapping from ad id to timestamps. The
     * different times (corresponding timestamp) at which the ad was shown to the user are contained in the list.
     * This has been done so that we don't traverse the entire history for each ad and only look at the relevant
     * timestamps, i.e., * the timestamps for the ad in question.
     * @param recentImpressionHistory Recent impression history for user
     * @return map containing ad id to timestamps mapping
     */
    private Map<Integer, List<Long>> getAdToTimestampMapFromRecentHistory(
            RecentImpressionHistory recentImpressionHistory) {
        if(recentImpressionHistory == null || recentImpressionHistory.getCircularList() == null ||
                recentImpressionHistory.getCircularList().size() == 0) {
            return null;
        }

        Map<Integer, List<Long>> adToTimestampMap = new HashMap<Integer, List<Long>>();
        List<ImpressionEvent> impressionEvents = recentImpressionHistory.getCircularList();
        for(ImpressionEvent impressionEvent : impressionEvents) {
            int adId = impressionEvent.getAdId();
            long timestamp = impressionEvent.getTimestamp();

            List<Long> timestamps = adToTimestampMap.get(adId);
            if(timestamps == null) {
                timestamps = new ArrayList<Long>();
                adToTimestampMap.put(adId, timestamps);
            }

            timestamps.add(timestamp);
        }
        return adToTimestampMap;
    }

    private int[] getFrequencyCapDetailsForAd(AdEntity adEntity, Request request) {
        int maxCap = -1;
        int timeWindowInHour = -1;
        FreqCap freqCap = adEntity.getFrequencyCap();
        if(freqCap != null && freqCap.getFDef() != null) {
            if(!adEntity.getFrequencyCap().getFDef().containsKey(FreqEventType.IMP)) {
                ReqLog.debugWithDebugNew(logger, request, "Ad id : {} has empty frequency cap object");
                return null;
            }
            Set<FreqDef> freqDefs = freqCap.getFDef().get(FreqEventType.IMP);
            for(FreqDef def : freqDefs) {
                if(def.getDuration() == FreqDuration.BYHOUR) {
                    maxCap = def.getCount();
                    timeWindowInHour = def.getHour();
                } else if(def.getDuration() == FreqDuration.BYDAY) {
                    maxCap = def.getCount();
                    timeWindowInHour = def.getHour() * 24;
                }
            }
        }

        if(maxCap == -1) {
            ReqLog.debugWithDebugNew(logger, request, "Ad id : {} specifies frequency cap but not impression cap.");
            return null;
        }

        int[] result = new int[2];
        result[0] = maxCap;
        result[1] = timeWindowInHour;
        ReqLog.debugWithDebugNew(logger, request, "Ad id : {} specifies impression cap, maximum cap = {} and time " +
                "window = {}.", adEntity.getAdIncId() ,maxCap, timeWindowInHour);
        return result;
    }

    /**
     * Function that gets user's history in a map from ad id to list of impression times and returns whether the ad is
     * eligible for serving.
     * The ad is eligible if it is :
     *     a) not frequency capped
     *     b) if frequency capped but has not reached the cap in the specified time window
     * @param adId id of the ad to check
     * @param adToTimestampMap Recent impression history for user
     * @return if the ad is eligible to be shown to the user
     */
    private boolean isFreqCappedAdEligibleForUser(int adId, Map<Integer, List<Long>> adToTimestampMap,
                                                  Request request) {
        AdEntity adEntity = this.adEntityCache.query(adId);

        if (null == adEntity) {
            logger.error("AdEntity could not be found in cache for adId:{} ", adId);
            return false;
        }


        if(adToTimestampMap == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();

        int maxCap = -1;
        int timeWindowInHour = -1;
        int[] frequencyCapDetails = getFrequencyCapDetailsForAd(adEntity, request);
        if(frequencyCapDetails != null) {
            maxCap = frequencyCapDetails[0];
            timeWindowInHour = frequencyCapDetails[1];
        }

        if(maxCap == -1) {
            ReqLog.debugWithDebugNew(logger, request, "Ad id : {} is not frequency capped. Passing this ad", adId);
            return true;
        }

        long beginTime = currentTime - (timeWindowInHour * HOUR_TO_MILLISECONDS_UNIT);

        List<Long> adImpressionTimeList = adToTimestampMap.get(adId);
        if(adImpressionTimeList == null) {
            // Ad has never been shown to the user. Can be shown irrespective of cap and duration
            return true;
        }

        int shownCount = 0;
        for(long timestamp : adImpressionTimeList) {
            if(timestamp >= beginTime) {
                ++shownCount;
            }
        }
        if(shownCount >= maxCap) {
            logger.debug("MaxCap : {} is hit for adId :{} for timeWindow(hours):{} , cannot show it more...",
                    maxCap, adId, timeWindowInHour);
            return false;
        }

        logger.debug("MaxCap : {} is not yet hit for adId : {} for timeWindow(hours) : {} , number of times shown : {}",
                maxCap, adId, timeWindowInHour, shownCount);
        return true;
    }

    /**
     * This method takes input as frequency capped ad id set,requesting kritter user id
     * and returns whether the ad is eligible for ad serving.
     * Caution: This function must take input of adIdSet that are frequency capped.
     * @param adIdSet set of ad ids of shortlisted ads
     * @param request request object corresponding to the current request
     * @param context context object corresponding to the current request
     * @return set of ad ids passing the frequency capping filter
     */
    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        ReqLog.debugWithDebugNew(logger,request, "Inside {} of AdTargetingMatcher...", getName());

        String kritterUserId = request.getUserId();

        // User history cache is unavailable. Drop all the frequency capped ads since we don't have any user
        // related information.
        if(kritterUserId == null || null == recentHistoryProvider) {
            Set<Integer> frequencyCappedAds = getFrequencyCappedAds(adIdSet, request);
            for(Integer adId : frequencyCappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, NoFillReason.USER_ID_ABSENT.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            if(kritterUserId == null) {
                ReqLog.debugWithDebugNew(logger,request, "User info not available for this request. Allowing only non " +
                    "frequency capped ads");
            } else {
                ReqLog.debugWithDebugNew(logger,request, "Recent history cache is not available. So allowing only non " +
                    "frequency capped ads");
            }

            Set<Integer> shortlistedAdIdSet = getNonFrequencyCappedAds(adIdSet, request);
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(NoFillReason.USER_ID_ABSENT);
            return shortlistedAdIdSet;
        }

        if (null == adIdSet || adIdSet.size() <= 0) {
            ReqLog.debugWithDebugNew(logger,request, "No adIdSet supplied to UserRecentImpressionHistoryCache, " +
                    "returning null/empty set...");
            return adIdSet;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        try {
            RecentImpressionHistory recentImpressionHistory =
                    recentHistoryProvider.fetchImpressionHistoryForUser(kritterUserId);

            /*If there is no impression history then user can be shown all ads.*/
            if (null == recentImpressionHistory) {
                ReqLog.debugWithDebugNew(logger,request, "RecentImpressionHistory for userId: {} is null ,allowing " +
                        "all ads", kritterUserId);
                return adIdSet;
            }

            ReqLog.debugWithDebugNew(logger, request, "Fetched user history for user id : {}. History : {}.",
                    kritterUserId, recentImpressionHistory);

            Map<Integer, List<Long>> adToTimestampMap = getAdToTimestampMapFromRecentHistory(recentImpressionHistory);
            // The user history is empty. This case should not occur, but nonetheless we should show all ads
            if(adToTimestampMap == null) {
                logger.debug("ImpressionEvent List for userId: {} is null, allowing adIdSet: {} ",
                        kritterUserId, adIdSet);
                return adIdSet;
            }

            for (Integer adId : adIdSet) {
                if(isFreqCappedAdEligibleForUser(adId, adToTimestampMap, request)) {
                    ReqLog.debugWithDebugNew(logger,request, "Ad id {} eligible to serve on user : {}", adId,
                            kritterUserId);
                    adIdSetToReturn.add(adId);
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);
                }
            }
        } catch (Exception e) {
            logger.error("Exception occur while trying to fetch user history. {}", e);
            ReqLog.debugWithDebugNew(logger,request, "Exception inside FrequencyCapFilter reason: {}  ", e);

            Set<Integer> frequencyCappedAds = getFrequencyCappedAds(adIdSet, request);
            for(Integer adId : frequencyCappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            Set<Integer> shortlistedAdIdSet = getNonFrequencyCappedAds(adIdSet, request);
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(noFillReason);
            return shortlistedAdIdSet;
        }

        if(null == request.getNoFillReason() && adIdSetToReturn.size() <= 0)
            request.setNoFillReason(noFillReason);
        return adIdSetToReturn;
    }
}
