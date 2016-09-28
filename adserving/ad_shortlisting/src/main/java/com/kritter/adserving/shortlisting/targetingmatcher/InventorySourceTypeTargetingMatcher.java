package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.SupplySourceTypeEnum;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class InventorySourceTypeTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_INV_SRC_TYPE;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public InventorySourceTypeTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                               String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdsOnInventorySourceType of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdsOnInventorySourceType of AdTargetingMatcher...");

        Site site = request.getSite();
        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger,request, "AdEntity not found in cache id : {}" , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            if
                    (
                    targetingProfile.getSupplySourceType() == SupplySourceTypeEnum.APP_WAP.getCode() ||

                            (
                                    targetingProfile.getSupplySourceType() == SupplySourceTypeEnum.APP.getCode() &&
                                            site.getSitePlatform() == SITE_PLATFORM.APP.getPlatform()
                            )                                                                                 ||

                            (
                                    targetingProfile.getSupplySourceType() == SupplySourceTypeEnum.WAP.getCode() &&
                                            site.getSitePlatform() == SITE_PLATFORM.WAP.getPlatform()
                            )
                    )
                shortlistedAdIdSet.add(adId);
            else
            {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);

                ReqLog.debugWithDebugNew(logger,request, "SupplySourceType defined for adGuId:{} is {} ,does not qualify for site's platform type: {} ", 
                        adEntity.getAdGuid(),SupplySourceTypeEnum.getEnum(targetingProfile.getSupplySourceType()).getName(),
                        SITE_PLATFORM.getEnum(site.getSitePlatform()).getDescription());
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
