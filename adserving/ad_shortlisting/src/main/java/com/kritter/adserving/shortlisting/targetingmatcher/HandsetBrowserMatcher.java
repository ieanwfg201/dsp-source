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
import com.kritter.utils.entity.Version;
import com.kritter.utils.entity.VersionRange;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HandsetBrowserMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_BROWSER;

    private static final String DASH = "-";
    private static final String ALL_VERSIONS = "All";

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String contextHandsetMasterDataKey;
    private String adNoFillReasonMapKey;

    public HandsetBrowserMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                 String contextHandsetMasterDataKey, String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.contextHandsetMasterDataKey = contextHandsetMasterDataKey;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForHandsetBrowser of AdTargetingMatcher");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForHandsetBrowser of AdTargetingMatcher");

        HandsetMasterData handsetMasterData = null;
        if(null != context.getValue(contextHandsetMasterDataKey))
            handsetMasterData = (HandsetMasterData) context.getValue(contextHandsetMasterDataKey);

        Set<Integer> filteredAdIds = new HashSet<Integer>();
        try
        {
            for(Integer adId : adIdSet)
            {
                AdEntity adEntity = this.adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache,FATAL error!!! for adId: {} " , adId);
                    continue;
                }

                TargetingProfile targetingProfile = adEntity.getTargetingProfile();

                ReqLog.debugWithDebugNew(logger, request, "TargetedBrowserJson is {} for ad id : {}", targetingProfile.getTargetedBrowserJson(), adEntity.getAdGuid());

                //if no browser targeting specified then browser json has to be null.
                if(null == targetingProfile.getTargetedBrowserJson())
                {
                    ReqLog.debugWithDebugNew(logger, request, "The ad id is not browser targeted,so passing the adId: {} ",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                Map<String,String> browserMap = targetingProfile.getTargetedBrowserJsonMap();

                String handsetBrowserId = null;
                if(handsetMasterData != null && 
                		null != handsetMasterData.getDeviceBrowserId())
                    handsetBrowserId = String.valueOf(handsetMasterData.getDeviceBrowserId());

                ReqLog.debugWithDebugNew(logger, request, "Requesting browserId is: {} ", handsetBrowserId);

                if(null == browserMap || browserMap.size() == 0)
                {
                    ReqLog.debugWithDebugNew(logger, request, "The ad id is not browser targeted,so passing the adId: {} ",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                if(null == handsetBrowserId)
                {
                    ReqLog.debugWithDebugNew(logger, request, "The requesting browser is null ,ad targets browser ,skipping adId: {} ",adEntity.getAdGuid());
                    continue;
                }

                if(!browserMap.containsKey(handsetBrowserId))
                {
                    ReqLog.debugWithDebugNew(logger, request, "The requesting handset does not comply with any targeted browser in ad, skipping adId: {} ",adEntity.getAdGuid());
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);
                    continue;
                }

                String browserVersions[] = browserMap.get(handsetBrowserId).split(DASH);

                if(
                   browserVersions[0].equalsIgnoreCase(ALL_VERSIONS) ||
                   browserVersions[1].equalsIgnoreCase(ALL_VERSIONS)
                  )
                {
                    ReqLog.debugWithDebugNew(logger, request, "The ad id is targeted to all browser versions so passing the adId: {} ",adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                    continue;
                }

                String minVersion = browserVersions[0];
                String maxVersion = browserVersions[1];
                String handsetBrowserVersion = handsetMasterData.getDeviceBrowserVersion();

                Version minVersionValue = new Version(minVersion);
                Version maxVersionValue = new Version(maxVersion);
                VersionRange versionRange = new VersionRange(minVersionValue,maxVersionValue);

                if(versionRange.checkIfVersionIsWithin(new Version(handsetBrowserVersion)))
                {
                    ReqLog.debugWithDebugNew(logger, request, "The browserversion: {} is within targeted version range. For adid : {}, passing it...", handsetBrowserVersion, adEntity.getAdGuid());
                    filteredAdIds.add(adId);
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    logger.debug("The browserversion: {} is outside targeted version range. For adid : {}, skipping " +
                            "it...", handsetBrowserVersion, adId);
                    request.addDebugMessageForTestRequest("The browserversion: ");
                    request.addDebugMessageForTestRequest(handsetBrowserVersion);
                    request.addDebugMessageForTestRequest("is outside targeted version range. For adid : ");
                    request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                    request.addDebugMessageForTestRequest(" skipping it.");
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception inside AdTargetingMatcher , returning empty set of adid ",e);
            return Collections.<Integer>emptySet();
        }

        if(null == request.getNoFillReason() && filteredAdIds.size() <= 0)
            request.setNoFillReason(noFillReason);

        return filteredAdIds;
    }
}
