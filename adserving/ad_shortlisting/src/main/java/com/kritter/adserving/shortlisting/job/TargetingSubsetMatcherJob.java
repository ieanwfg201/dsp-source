package com.kritter.adserving.shortlisting.job;

import com.kritter.adserving.shortlisting.TargetingMatcher;
// import com.kritter.common.caches.metrics.cache.MetricsCache;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Set;

public class TargetingSubsetMatcherJob implements Job {
    @Getter
    private String name;
    private final Logger logger;
    private String requestObjectKey;
    private String shortlistedAdKey;
    private List<TargetingMatcher> targetingMatchers;
    // private MetricsCache metricsCache;

    public TargetingSubsetMatcherJob(String name,
                                     String loggerName,
                                     String requestObjectKey,
                                     String shortlistedAdKey,
                                     List<TargetingMatcher> targetingMatchers) {
                                     // MetricsCache metricsCache)
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.requestObjectKey = requestObjectKey;
        this.shortlistedAdKey = shortlistedAdKey;
        this.targetingMatchers = targetingMatchers;
        // this.metricsCache = metricsCache;
    }

    @Override
    public void execute(Context context) {
        Request request = (Request)context.getValue(requestObjectKey);

        ReqLog.debugWithDebugNew(logger, request, "Inside execute of {}", getName());

        if(request == null) {
            logger.debug("Request object null in {}.", getName());
            return;
        }

        Set<Integer> shortlistedAdIds = (Set<Integer>) context.getValue(this.shortlistedAdKey);

        for(TargetingMatcher targetingMatcher : targetingMatchers) {
            // metricsCache.incrementInvocations(targetingMatcher.getName());
            long beginTime = System.nanoTime();
            shortlistedAdIds = targetingMatcher.shortlistAds(shortlistedAdIds, request, context);
            long endTime = System.nanoTime();
            // metricsCache.incrementLatency(targetingMatcher.getName(), (endTime - beginTime + 500) / 1000);

            logger.debug("Shortlisted ads after targeting-matcher : {}", targetingMatcher.getName());

            ReqLog.debugWithDebugNew(logger, request, "Shortlisted ads after targeting-matcher : {}",
                    targetingMatcher.getName());

            if(shortlistedAdIds == null || shortlistedAdIds.size() == 0)
            {
                ReqLog.debugWithDebugNew(logger, request, "No ads shortlisted .... there is going to be " +
                        "DSP calls or passback if configured.");
            }
            else
            {
                if(logger.isDebugEnabled() || request.isRequestForSystemDebugging()) {
                    for(int adId : shortlistedAdIds) {
                        ReqLog.debugWithDebugNew(logger, request, "\tAd id : {}", adId);
                    }
                }
            }
        }

        context.setValue(this.shortlistedAdKey, shortlistedAdIds);
    }
}
