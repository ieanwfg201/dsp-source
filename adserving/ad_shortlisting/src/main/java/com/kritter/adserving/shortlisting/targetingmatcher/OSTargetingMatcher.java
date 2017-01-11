package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.shortlisting.job.AdTargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class OSTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_OS_MIDP;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private boolean useMidpUnderOperatingSystem;
    private HandsetOsTargetingMatcher handsetOsTargetingMatcher;
    private MidpVersionTargetingMatcher midpVersionTargetingMatcher;
    private HandsetOsOrMidpTargetingMatcher handsetOsOrMidpTargetingMatcher;
    private String adNoFillReasonMapKey;

    public OSTargetingMatcher(String name,
                              String loggerName,
                              AdEntityCache adEntityCache,
                              boolean useMidpUnderOperatingSystem,
                              HandsetOsTargetingMatcher handsetOsTargetingMatcher,
                              MidpVersionTargetingMatcher midpVersionTargetingMatcher,
                              HandsetOsOrMidpTargetingMatcher handsetOsOrMidpTargetingMatcher,
                              String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.useMidpUnderOperatingSystem = useMidpUnderOperatingSystem;
        this.handsetOsTargetingMatcher = handsetOsTargetingMatcher;
        this.midpVersionTargetingMatcher = midpVersionTargetingMatcher;
        this.handsetOsOrMidpTargetingMatcher = handsetOsOrMidpTargetingMatcher;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        Set<Integer> finalShortlistedAdIds = null;

        if(!this.useMidpUnderOperatingSystem)
        {
            logger.debug("Running handset OperatingSystem and MIDP as separate filters ");
            ReqLog.requestDebugNew(request, "Running handset OperatingSystem and MIDP as separate filters ");

            //check if operating system targeting passes.
            finalShortlistedAdIds = handsetOsTargetingMatcher.shortlistAds(adIdSet, request, context);
            logger.debug("After filterAdIdsForHandsetOS , adidset: {}", finalShortlistedAdIds);
            ReqLog.requestDebugNew(request, "After filterAdIdsForHandsetOS , adidset: ");
            if(request.isRequestForSystemDebugging()){
            	request.addDebugMessageForTestRequest(finalShortlistedAdIds.toString());
            }
            //filter on midp versions targeted if any.
            finalShortlistedAdIds = midpVersionTargetingMatcher.shortlistAds(finalShortlistedAdIds, request, context);
            logger.debug("After filterAdIdsBasedOnMidpVersion , adidset: {}", finalShortlistedAdIds);
            ReqLog.requestDebugNew(request, "After filterAdIdsBasedOnMidpVersion , adidset: ");
            if(request.isRequestForSystemDebugging()){
            	request.addDebugMessageForTestRequest(finalShortlistedAdIds.toString());
            }
        }
        else
        {
            ReqLog.debugWithDebugNew(logger, request,"Running handset OperatingSystem and MIDP combined as filters ");
            //check if operating system or midp targeting passes, one of them will do...
            finalShortlistedAdIds = handsetOsOrMidpTargetingMatcher.shortlistAds(adIdSet, request, context);
            logger.debug("After checkAdIdsForHandsetOsAndMidpCombined , adidset: {}", finalShortlistedAdIds);
            ReqLog.requestDebugNew(request, "After checkAdIdsForHandsetOsAndMidpCombined , adidset: ");
            if(request.isRequestForSystemDebugging()){
            	request.addDebugMessageForTestRequest(finalShortlistedAdIds.toString());
            }
        }

        if(finalShortlistedAdIds == null)
            finalShortlistedAdIds = new HashSet<Integer>();

        for(Integer adId : adIdSet) {
            if(!finalShortlistedAdIds.contains(adId)) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }
        }

        if(null == request.getNoFillReason() && finalShortlistedAdIds.size() <= 0)
            request.setNoFillReason(noFillReason);

        return finalShortlistedAdIds;
    }
}
