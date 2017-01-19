package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.geo.common.entity.reader.CustomIPFileDetectionCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class CustomIPTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_CUSTOM_IP;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private CustomIPFileDetectionCache customIPFileDetectionCache;
    private String adNoFillReasonMapKey;

    public CustomIPTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                    CustomIPFileDetectionCache customIPFileDetectionCache,
                                    String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.customIPFileDetectionCache = customIPFileDetectionCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForCustomIpTargetingIfExist of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForCustomIpTargetingIfExist of AdTargetingMatcher...");

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache id : {}" , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            String[] customIpFileArray = targetingProfile.getCustomIpFileIdArray();

            if( null == customIpFileArray || customIpFileArray.length ==0 )
            {
                ReqLog.debugWithDebugNew(logger, request, "The ad {} is not custom ip targeted so passing the filter....", adEntity.getAdGuid());
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
                    ReqLog.debugWithDebugNew(logger, request, "The ad {} is custom ip targeted and passes the check...", adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                }
                else
                {
                    ReqLog.debugWithDebugNew(logger, request, "The ad {} is custom ip targeted and fails the check...", adEntity.getAdGuid());

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
