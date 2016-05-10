package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.ConnectionType;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ConnectionTypeTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public ConnectionTypeTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForConnectionTypeTargeting of AdTargetingMatcher...");
        ReqLog.requestDebug(request, "Inside filterAdIdsForConnectionTypeTargeting of AdTargetingMatcher...");

        if(adIdSet == null || adIdSet.size() == 0) {
            logger.debug("No ads to shortlist from. Returning!");
            return adIdSet;
        }

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);
            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger,request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            Short[] targetedConnectionTypes = targetingProfile.getTargetedConnectionTypes();
            if(targetedConnectionTypes == null || targetedConnectionTypes.length == 0) {
                ReqLog.debugWithDebug(logger,request, "The ad id {} is not targeted to any connection type so passing the filter....", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            } else {
                ConnectionType connectionType = request.getConnectionType();
                if(ArrayUtils.contains(targetedConnectionTypes, connectionType.getId())) {
                    ReqLog.debugWithDebug(logger,request, "The ad id {} is connection type targeted and passes the check, detected ConnectionType:{} ", adEntity.getAdGuid(), connectionType);
                    shortlistedAdIdSet.add(adId);
                } else {
                    // Fails the filter
                    ReqLog.debugWithDebug(logger,request, "The ad id {} is connection type targeted and fails the check. Detected ConnectionType:{}", adEntity.getAdGuid(), connectionType);
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.NO_ADS_CONNECTION_TYPE);

        return shortlistedAdIdSet;
    }
}
