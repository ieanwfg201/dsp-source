package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class TabletTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.NO_ADS_TABLET_TARGETING;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String contextHandsetMasterDataKey;
    private String adNoFillReasonMapKey;

    public TabletTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                  String contextHandsetMasterDataKey,
                                  String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.contextHandsetMasterDataKey = contextHandsetMasterDataKey;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside TabletTargetingMatcher of AdTargetingMatcher");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForHandsetBrowser of AdTargetingMatcher");

        if(adIdSet == null || adIdSet.size() == 0) {
            return adIdSet;
        }

        HandsetMasterData handsetMasterData = (HandsetMasterData) context.getValue(contextHandsetMasterDataKey);
        Set<Integer> filteredAds = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);

            if(null == adEntity) {
                ReqLog.debugWithDebugNew(logger,request, "AdEntity not found in cache,FATAL error!!! for adId: {} " , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            boolean isTabletTargeting = targetingProfile.isTabletTargeting();

            if(!isTabletTargeting) {
                ReqLog.debugWithDebugNew(logger,request, "Ad with adId : {} is not tablet targeted, passing!", adId);
                filteredAds.add(adId);
            } else {
                if(handsetMasterData.getHandsetCapabilityObject().getIsTablet()) {
                    ReqLog.debugWithDebugNew(logger,request, "Ad with adId : {} is tablet targeted, the request is from tablet, hence passing",
                            adId);
                    filteredAds.add(adId);
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    ReqLog.debugWithDebugNew(logger,request, "Ad with adId : {} is tablet targeted. Request is not from tablet, hence dropping" +
                            "the ad", adId);
                }
            }
        }

        if(null == request.getNoFillReason() && filteredAds.size() <= 0)
            request.setNoFillReason(noFillReason);

        return filteredAds;
    }
}
