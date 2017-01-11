package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.ConnectionType;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class ConnectionTypeTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.NO_ADS_CONNECTION_TYPE;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public ConnectionTypeTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                          String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForConnectionTypeTargeting of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForConnectionTypeTargeting of AdTargetingMatcher...");

        if(adIdSet == null || adIdSet.size() == 0) {
            logger.debug("No ads to shortlist from. Returning!");
            return adIdSet;
        }

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);
            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger,request, "AdEntity not found in cache id : {}" , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            Short[] targetedConnectionTypes = targetingProfile.getTargetedConnectionTypes();
            if(targetedConnectionTypes == null || targetedConnectionTypes.length == 0) {
                ReqLog.debugWithDebugNew(logger,request, "The ad id {} is not targeted to any connection type so passing the filter....", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            } else {
                ConnectionType connectionType = request.getConnectionType();
                if(ArrayUtils.contains(targetedConnectionTypes, connectionType.getId())) {
                    ReqLog.debugWithDebugNew(logger,request, "The ad id {} is connection type targeted and passes the check, detected ConnectionType:{} ", adEntity.getAdGuid(), connectionType);
                    shortlistedAdIdSet.add(adId);
                } else {
                    // Fails the filter
                    ReqLog.debugWithDebugNew(logger,request, "The ad id {} is connection type targeted and fails the check. Detected ConnectionType:{}", adEntity.getAdGuid(), connectionType);

                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
