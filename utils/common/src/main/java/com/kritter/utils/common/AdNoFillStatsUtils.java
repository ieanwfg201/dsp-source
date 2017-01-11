package com.kritter.utils.common;

import com.kritter.core.workflow.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions for ad stats population and update
 */
public class AdNoFillStatsUtils {
    public static String AD_STATS_LOGGING_KEY = "ad-stats-logging-enabled";

    /**
     * Updates the context with the no fill reason for this ad.
     * @param adId Ad id for the ad that has been dropped
     * @param noFillReasonId no fill reason id
     * @param noFillTargetingMapKey key to the ad->nofillreason->count map. There should not be anything else stored
     *                              against this key in the context
     * @param context Context object flowing through the request
     */
    public static void updateContextForNoFillOfAd(int adId, int noFillReasonId, String noFillTargetingMapKey,
                                                  Context context) {
        Boolean adStatsLoggingEnabled = (Boolean) context.getValue(AD_STATS_LOGGING_KEY);
        if(adStatsLoggingEnabled == null || !adStatsLoggingEnabled) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> noFillTargetingMap = (Map<Integer, Integer>) context.getValue(noFillTargetingMapKey);
        if(noFillTargetingMap == null) {
            noFillTargetingMap = new HashMap<Integer, Integer>();
            context.setValue(noFillTargetingMapKey, noFillTargetingMap);
        }

        noFillTargetingMap.put(adId, noFillReasonId);
    }
}