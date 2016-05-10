package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.MidpValue;
import com.kritter.core.workflow.Context;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MidpVersionTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    private String contextHandsetMasterDataKey;

    public MidpVersionTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                       String contextHandsetMasterDataKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.contextHandsetMasterDataKey = contextHandsetMasterDataKey;
    }

    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsBasedOnMidpVersion of AdTargetingMatcher ...");

        ReqLog.requestDebug(request,"Inside filterAdIdsBasedOnMidpVersion of AdTargetingMatcher ...");

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        HandsetMasterData handsetMasterData = (HandsetMasterData) context.getValue(contextHandsetMasterDataKey);


        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            MidpValue midpValueTargeted = targetingProfile.getMidpTargetingValue();

            Boolean requestingHandsetIsMidp1 = handsetMasterData.getHandsetCapabilityObject().getMidp1();
            Boolean requestingHandsetIsMidp2 = handsetMasterData.getHandsetCapabilityObject().getMidp2();

            if(
                    (midpValueTargeted.getCode() == MidpValue.MIDP_1.getCode()) &&
                            (requestingHandsetIsMidp1 || requestingHandsetIsMidp2)
                    )
            {
                shortlistedAdIdSet.add(adId);
            }
            else if(
                    (midpValueTargeted.getCode() == MidpValue.MIDP_2.getCode()) &&
                            requestingHandsetIsMidp2
                    )
            {
                shortlistedAdIdSet.add(adId);
            }
            else if(midpValueTargeted.getCode() == MidpValue.ALL.getCode())
            {
                shortlistedAdIdSet.add(adId);
            }
            else
            {
                ReqLog.debugWithDebug(logger, request,"Requesting handset is midp1 enabled: {} Requesting handset is midp2 enabled: {} , midp value targeted by ad, not under os: so failing the ad to pass,adId: {} ", requestingHandsetIsMidp1,requestingHandsetIsMidp2,midpValueTargeted.getDescription(),adEntity.getAdGuid());
            }
        }

        return shortlistedAdIdSet;

    }
}
