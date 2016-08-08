package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.entity.Version;
import com.kritter.utils.entity.VersionRange;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HandsetOsOrMidpTargetingMatcher implements TargetingMatcher {
    private static final String DASH = "-";
    private static final String ALL_VERSIONS = "All";
    private static final String MIDP_1 = "-2";
    private static final String MIDP_2 = "-3";

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String contextHandsetMasterDataKey;

    public HandsetOsOrMidpTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                           String contextHandsetMasterDataKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.contextHandsetMasterDataKey = contextHandsetMasterDataKey;;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside checkAdIdsForHandsetOsOrMidp of AdTargetingMatcher");

        ReqLog.requestDebug(request, "Inside checkAdIdsForHandsetOsOrMidp of AdTargetingMatcher");

        HandsetMasterData handsetMasterData = (HandsetMasterData) context.getValue(contextHandsetMasterDataKey);

        Set<Integer> filteredAdIds = new HashSet<Integer>();

        try
        {
            for(Integer adId : adIdSet)
            {
                AdEntity adEntity = this.adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache,FATAL error!!! for ad id: {} ",adId);
                    continue;
                }

                TargetingProfile targetingProfile = adEntity.getTargetingProfile();


                ReqLog.debugWithDebug(logger, request, "TargetedOsMidpJson is {} for ad id : {}", targetingProfile.getTargetedOSJson(), adEntity.getAdGuid());

                //if no OS targeting specified then OS json has to be null.
                if(null == targetingProfile.getTargetedOSJson())
                {
                    ReqLog.debugWithDebug(logger, request, "The ad id is not OS/Midp targeted,so passing the adId: {} ",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                Map<String,String> osMap = targetingProfile.getTargetedOSJsonMap();

                String handsetOSId = String.valueOf(handsetMasterData.getDeviceOperatingSystemId());

                if(null == osMap || osMap.size() == 0)
                {
                    ReqLog.debugWithDebug(logger, request, "The ad id is not OS/midp targeted,so passing the adId: {} ",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                //check if request is midp and ad targets corresponding midp version.
                Boolean requestingHandsetIsMidp1 = handsetMasterData.getHandsetCapabilityObject().getMidp1();
                Boolean requestingHandsetIsMidp2 = handsetMasterData.getHandsetCapabilityObject().getMidp2();
                if(requestingHandsetIsMidp1 == null){
                	requestingHandsetIsMidp1 = false;
                }
                if(requestingHandsetIsMidp2 == null){
                	requestingHandsetIsMidp2 = false;
                }
                ReqLog.debugWithDebug(logger, request, "Handset's Midp1 value : {} ",requestingHandsetIsMidp1);

                ReqLog.debugWithDebug(logger, request, "Handset's Midp2 value : {} ",requestingHandsetIsMidp2);

                if(
                        (requestingHandsetIsMidp1 && osMap.containsKey(MIDP_1)) || (requestingHandsetIsMidp2 && osMap.containsKey(MIDP_2))
                        )
                {

                    ReqLog.debugWithDebug(logger, request, "The requesting handset is midp enabled, adId {} passes the midp version targeted..",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                if(!osMap.containsKey(handsetOSId))
                {
                    ReqLog.debugWithDebug(logger, request, "The requesting handset does not comply with any targeted os in campaign, skipping adId {} ",adEntity.getAdGuid());
                    continue;
                }

                String osVersions[] = osMap.get(handsetOSId).split(DASH);

                if(osVersions[0].equalsIgnoreCase(ALL_VERSIONS) || osVersions[1].equalsIgnoreCase(ALL_VERSIONS))
                {
                    ReqLog.debugWithDebug(logger, request, "The ad id is targeted to all os, versions so passing the adId {} ",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                String minVersion = osVersions[0];
                String maxVersion = osVersions[1];
                String handsetOsVersion = handsetMasterData.getDeviceOperatingSystemVersion();

                Version minVersionValue = new Version(minVersion);
                Version maxVersionValue = new Version(maxVersion);
                VersionRange versionRange = new VersionRange(minVersionValue,maxVersionValue);

                if(versionRange.checkIfVersionIsWithin(new Version(handsetOsVersion)))
                {
                    ReqLog.debugWithDebug(logger, request, "The osversion: {} is within targeted version range for adId : {}, passing it...", handsetOsVersion, adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception inside AdTargetingMatcher , returning empty set of adid ",e);
            return Collections.<Integer>emptySet();
        }

        return filteredAdIds;
    }
}
