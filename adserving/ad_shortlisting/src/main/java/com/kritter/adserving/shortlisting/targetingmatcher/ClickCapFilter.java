package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.FreqEventType;
import com.kritter.core.workflow.Context;
import com.kritter.entity.freqcap_entity.FreqCap;
import com.kritter.entity.freqcap_entity.FreqDef;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.recenthistory.RecentClickHistoryProvider;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.user.thrift.struct.ClickEvent;
import com.kritter.user.thrift.struct.RecentClickHistory;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class ClickCapFilter implements TargetingMatcher {
    private static final long HOUR_TO_MILLISECONDS_UNIT = 60 * 60 * 1000;
    private static final NoFillReason noFillReason = NoFillReason.FREQUENCY_CAP;


    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private RecentClickHistoryProvider recentClickHistoryProvider;
    private String adNoFillReasonMapKey;

    public ClickCapFilter(String name,
                          String loggerName,
                          AdEntityCache adEntityCache,
                          RecentClickHistoryProvider recentClickHistoryProvider,
                          String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.recentClickHistoryProvider = recentClickHistoryProvider;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    /**
     * Given a set of ads which don't have frequency capping enabled
     * @param adIdSet Set of ids of all ads
     * @return Set of ad ids which don't have frequency capping
     */
    private Set<Integer> getNonClickCappedAds(Set<Integer> adIdSet, Request request) {
        if(adIdSet == null || adIdSet.size() == 0)
            return adIdSet;

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);

            int[] clickCapDetails = getClickCapDetailsForAd(adEntity, request);
            if(clickCapDetails == null) {
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
    private Set<Integer> getClickCappedAds(Set<Integer> adIdSet, Request request) {
        if(adIdSet == null || adIdSet.size() == 0)
            return new HashSet<Integer>();

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);

            int[] clickCapDetails = getClickCapDetailsForAd(adEntity, request);
            if(clickCapDetails != null) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Converts the click event list into a map. The map contains the mapping from ad id to timestamps. The
     * different times (corresponding timestamp) at which the ad was shown to the user are contained in the list.
     * This has been done so that we don't traverse the entire history for each ad and only look at the relevant
     * timestamps, i.e., * the timestamps for the ad in question.
     * @param recentClickHistory Recent click history for user
     * @return map containing ad id to timestamps mapping
     */
    private Map<Integer, List<Long>> getAdToTimestampMapFromRecentHistory(RecentClickHistory recentClickHistory) {
        if(recentClickHistory == null || recentClickHistory.getClickEventCircularList() == null ||
                recentClickHistory.getClickEventCircularList().size() == 0) {
            return null;
        }

        Map<Integer, List<Long>> adToTimestampMap = new HashMap<Integer, List<Long>>();
        List<ClickEvent> clickEvents = recentClickHistory.getClickEventCircularList();
        for(ClickEvent clickEvent : clickEvents) {
            int adId = clickEvent.getAdId();
            long timestamp = clickEvent.getTimestamp();

            List<Long> timestamps = adToTimestampMap.get(adId);
            if(timestamps == null) {
                timestamps = new ArrayList<Long>();
                adToTimestampMap.put(adId, timestamps);
            }

            timestamps.add(timestamp);
        }
        return adToTimestampMap;
    }

    private int[] getClickCapDetailsForAd(AdEntity adEntity, Request request) {
        int maxCap = -1;
        int timeWindowInHour = -1;
        FreqCap freqCap = adEntity.getFrequencyCap();
        if(freqCap != null && freqCap.getFDef() != null) {
            if(!adEntity.getFrequencyCap().getFDef().containsKey(FreqEventType.CLK)) {
                ReqLog.debugWithDebugNew(logger, request, "Ad id : {} has empty frequency cap object");
                return null;
            }
            Set<FreqDef> freqDefs = freqCap.getFDef().get(FreqEventType.CLK);
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
            ReqLog.debugWithDebugNew(logger, request, "Ad id : {} specifies frequency cap but not click cap.");
            return null;
        }

        int[] result = new int[2];
        result[0] = maxCap;
        result[1] = timeWindowInHour;
        ReqLog.debugWithDebugNew(logger, request, "Ad id : {} specifies click cap, maximum cap = {} and time window = " +
                "{}.", maxCap, timeWindowInHour);
        return result;
    }

    /**
     * Function that gets user's history in a map from ad id to list of click times and returns whether the ad is
     * eligible for serving.
     * The ad is eligible if it is :
     *     a) not click capped
     *     b) if click capped but has not reached the cap in the specified time window
     * @param adId id of the ad to check
     * @param adToTimestampMap Recent click history for user
     * @return if the ad is eligible to be shown to the user
     */
    private boolean isClickCappedAdEligibleForUser(int adId, Map<Integer, List<Long>> adToTimestampMap,
                                                   Request request) {
        AdEntity adEntity = this.adEntityCache.query(adId);

        if (null == adEntity) {
            logger.error("AdEntity could not be found in cache for adId:{} ", adId);
            return false;
        }

        int maxCap = -1;
        int timeWindowInHour = -1;
        int[] clickCapDetails = getClickCapDetailsForAd(adEntity, request);
        if(clickCapDetails != null) {
            maxCap = clickCapDetails[0];
            timeWindowInHour = clickCapDetails[1];
        }

        if(maxCap == -1) {
            ReqLog.debugWithDebugNew(logger, request, "Ad id : {} is not frequency capped on click. Passing this ad",
                    adId);
            return true;
        }

        if(adToTimestampMap == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();

        long beginTime = currentTime - (timeWindowInHour * HOUR_TO_MILLISECONDS_UNIT);

        List<Long> adClickTimeList = adToTimestampMap.get(adId);
        if(adClickTimeList == null) {
            // Ad has never been shown to the user. Can be shown irrespective of cap and duration
            return true;
        }

        int shownCount = 0;
        for(long timestamp : adClickTimeList) {
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
     * This method takes input as click capped ad id set,requesting kritter user id
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
        if(kritterUserId == null || null == recentClickHistoryProvider) {
            Set<Integer> clickCappedAds = getClickCappedAds(adIdSet, request);
            for(Integer adId : clickCappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            if(kritterUserId == null) {
                ReqLog.debugWithDebugNew(logger,request, "User info not available for this request. Allowing only non " +
                        "frequency capped ads");
            } else {
                ReqLog.debugWithDebugNew(logger,request, "Recent history cache is not available. So allowing only non " +
                        "frequency capped ads");
            }

            Set<Integer> shortlistedAdIdSet = getNonClickCappedAds(adIdSet, request);
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(noFillReason);
            return shortlistedAdIdSet;
        }

        if (null == adIdSet || adIdSet.size() <= 0) {
            ReqLog.debugWithDebugNew(logger,request, "No adIdSet supplied to UserRecentClickHistoryCache, " +
                    "returning null/empty set...");
            return adIdSet;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        try {
            RecentClickHistory recentClickHistory =
                    recentClickHistoryProvider.fetchClickHistoryForUser(kritterUserId);

            /*If there is no click history then user can be shown all ads.*/
            if (null == recentClickHistory) {
                ReqLog.debugWithDebugNew(logger,request, "RecentClickHistory for userId: {} is null ,allowing " +
                        "all ads", kritterUserId);
                return adIdSet;
            }

            ReqLog.debugWithDebugNew(logger, request, "Fetched click history for user id : {}. History : {}.",
                    kritterUserId, recentClickHistory);

            Map<Integer, List<Long>> adToTimestampMap = getAdToTimestampMapFromRecentHistory(recentClickHistory);
            // The user history is empty. This case should not occur, but nonetheless we should show all ads
            if(adToTimestampMap == null) {
                logger.debug("Click event List for userId: {} is null, allowing adIdSet: {} ",
                        kritterUserId, adIdSet);
                return adIdSet;
            }

            for (Integer adId : adIdSet) {
                if(isClickCappedAdEligibleForUser(adId, adToTimestampMap, request)) {
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
            ReqLog.debugWithDebugNew(logger,request, "Exception inside ClickCapFilter reason: {}  ", e);

            Set<Integer> clickCappedAds = getClickCappedAds(adIdSet, request);
            for(Integer adId : clickCappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            Set<Integer> shortlistedAdIdSet = getNonClickCappedAds(adIdSet, request);
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(noFillReason);
            return shortlistedAdIdSet;
        }

        if(null == request.getNoFillReason() && adIdSetToReturn.size() <= 0)
            request.setNoFillReason(noFillReason);
        return adIdSetToReturn;
    }
}
