package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.geo.common.entity.reader.CustomIPFileDetectionCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class CustomIPTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private CustomIPFileDetectionCache customIPFileDetectionCache;

    public CustomIPTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                    CustomIPFileDetectionCache customIPFileDetectionCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.customIPFileDetectionCache = customIPFileDetectionCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForCustomIpTargetingIfExist of AdTargetingMatcher...");
        ReqLog.requestDebug(request, "Inside filterAdIdsForCustomIpTargetingIfExist of AdTargetingMatcher...");

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            String[] customIpFileArray = targetingProfile.getCustomIpFileIdArray();

            if( null == customIpFileArray || customIpFileArray.length ==0 )
            {
                ReqLog.debugWithDebug(logger, request, "The ad {} is not custom ip targeted so passing the filter....", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            }

            else
            {
                if(customIPFileDetectionCache.
                        doesIPExistInCustomIPFiles(
                                request.getIpAddressUsedForDetection(),
                                customIpFileArray
                        )
                        )
                {
                    ReqLog.debugWithDebug(logger, request, "The ad {} is custom ip targeted and passes the check...", adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                }
                else
                {
                    ReqLog.debugWithDebug(logger, request, "The ad {} is custom ip targeted and fails the check...", adEntity.getAdGuid());
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.AD_CUSTOM_IP);

        return shortlistedAdIdSet;
    }
}
