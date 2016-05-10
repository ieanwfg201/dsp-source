package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.shortlisting.job.AdTargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class OSTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private boolean useMidpUnderOperatingSystem;
    private HandsetOsTargetingMatcher handsetOsTargetingMatcher;
    private MidpVersionTargetingMatcher midpVersionTargetingMatcher;
    private HandsetOsOrMidpTargetingMatcher handsetOsOrMidpTargetingMatcher;

    public OSTargetingMatcher(String name,
                              String loggerName,
                              AdEntityCache adEntityCache,
                              boolean useMidpUnderOperatingSystem,
                              HandsetOsTargetingMatcher handsetOsTargetingMatcher,
                              MidpVersionTargetingMatcher midpVersionTargetingMatcher,
                              HandsetOsOrMidpTargetingMatcher handsetOsOrMidpTargetingMatcher) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.useMidpUnderOperatingSystem = useMidpUnderOperatingSystem;
        this.handsetOsTargetingMatcher = handsetOsTargetingMatcher;
        this.midpVersionTargetingMatcher = midpVersionTargetingMatcher;
        this.handsetOsOrMidpTargetingMatcher = handsetOsOrMidpTargetingMatcher;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        Set<Integer> finalShortlistedAdIds = null;

        if(!this.useMidpUnderOperatingSystem)
        {
            logger.debug("Running handset OperatingSystem and MIDP as separate filters ");
            ReqLog.requestDebug(request, "Running handset OperatingSystem and MIDP as separate filters ");

            //check if operating system targeting passes.
            finalShortlistedAdIds = handsetOsTargetingMatcher.shortlistAds(adIdSet, request, context);
            logger.debug("After filterAdIdsForHandsetOS , adidset: {}", finalShortlistedAdIds);
            ReqLog.requestDebug(request, "After filterAdIdsForHandsetOS , adidset: ");
            ReqLog.requestDebug(request, AdTargetingMatcher.
                        fetchGuidSetForAdIncIdSet(finalShortlistedAdIds, adEntityCache).toString());
            //filter on midp versions targeted if any.
            finalShortlistedAdIds = midpVersionTargetingMatcher.shortlistAds(finalShortlistedAdIds, request, context);
            logger.debug("After filterAdIdsBasedOnMidpVersion , adidset: {}", finalShortlistedAdIds);
            ReqLog.requestDebug(request, "After filterAdIdsBasedOnMidpVersion , adidset: ");
            ReqLog.requestDebug(request, AdTargetingMatcher.
                        fetchGuidSetForAdIncIdSet(finalShortlistedAdIds, adEntityCache).toString());

            /****************************************Set no fill reason.**********************************************/
            if(null == request.getNoFillReason() && finalShortlistedAdIds.size() <= 0)
                request.setNoFillReason(Request.NO_FILL_REASON.AD_OS_MIDP);
            /*********************************************************************************************************/
        }
        else
        {
            ReqLog.debugWithDebug(logger, request,"Running handset OperatingSystem and MIDP combined as filters ");
            //check if operating system or midp targeting passes, one of them will do...
            finalShortlistedAdIds = handsetOsOrMidpTargetingMatcher.shortlistAds(adIdSet, request, context);
            logger.debug("After checkAdIdsForHandsetOsAndMidpCombined , adidset: {}", finalShortlistedAdIds);
            ReqLog.requestDebug(request, "After checkAdIdsForHandsetOsAndMidpCombined , adidset: ");
            ReqLog.requestDebug(request, AdTargetingMatcher.
                        fetchGuidSetForAdIncIdSet(finalShortlistedAdIds, adEntityCache).toString());

            /****************************************Set no fill reason.**********************************************/
            if(null == request.getNoFillReason() && finalShortlistedAdIds.size() <= 0)
                request.setNoFillReason(Request.NO_FILL_REASON.AD_OS_MIDP);
            /*********************************************************************************************************/
        }

        return finalShortlistedAdIds;
    }
}
