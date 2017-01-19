package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.DeviceType;
import com.kritter.core.workflow.Context;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

/**
 * This class performs device type targeting.
 */
public class DeviceTypeTargetingMatcher implements TargetingMatcher
{
    private static NoFillReason noFillReason = NoFillReason.DEVICE_TYPE;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public DeviceTypeTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                      String adNoFillReasonMapKey)
    {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context)
    {
        logger.info("Inside filterAdIdsForDeviceTypeTargeting of AdTargetingMatcher ...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForDeviceTypeTargeting of AdTargetingMatcher ...");

        if(adIdSet == null || adIdSet.size() == 0) {
            logger.debug("No ads to shortlist from. Returning!");
            return adIdSet;
        }

        logger.debug("AdIdSet Size: {}  inside shortlistAds of DeviceTypeTargetingMatcher",adIdSet.size());

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger,request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            HandsetMasterData handsetMasterData = request.getHandsetMasterData();

            Short[] deviceTypeTargetedArray = adEntity.getTargetingProfile().getDeviceTypeArray();
            DeviceType deviceType = null;

            if(null != handsetMasterData)
                deviceType = handsetMasterData.getDeviceType();

            //if ad does not include any device type or includes both desktop and mobile.
            boolean adDoesNotTargetDeviceType = (
                                                 null == deviceTypeTargetedArray     ||
                                                 deviceTypeTargetedArray.length <= 0 ||
                                                 deviceTypeTargetedArray.length == 2
                                                );

            //no device type detected and ad does not target any device type.
            if(null == deviceType && adDoesNotTargetDeviceType)
            {
                ReqLog.debugWithDebugNew(logger,request, "Ad:{} does not target any device type (desktop/mobile etc)...passing it and no device type detected",adEntity.getAdGuid());

                shortlistedAdIdSet.add(adId);
                continue;
            }

            short requestingDeviceTypeCode = DeviceType.UNKNOWN.getCode();

            if(null != deviceType)
                requestingDeviceTypeCode = deviceType.getCode();

            logger.debug("DeviceTypeid from request: {} ", requestingDeviceTypeCode);

            if(adDoesNotTargetDeviceType)
            {
                ReqLog.debugWithDebugNew(logger,request, "Ad:{} does not target any device type (desktop/mobile etc)...passing it",
                              adEntity.getAdGuid());

                shortlistedAdIdSet.add(adId);
                continue;
            }

            else
            {
                boolean foundTargetedDeviceType = false;
                for(Short deviceTypeId : deviceTypeTargetedArray)
                {
                    if(requestingDeviceTypeCode == deviceTypeId)
                    {
                        ReqLog.debugWithDebugNew(logger,request, "Ad:{}  targets device type (desktop/mobile etc)...passing it",
                        adEntity.getAdGuid());
                        shortlistedAdIdSet.add(adId);
                        foundTargetedDeviceType = true;
                        break;
                    }
                }

                if(!foundTargetedDeviceType) {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    if(request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("Ad : " + adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" targets device type (desktop/mobile etc). No matching" +
                                "device type found. Hence dropping it.");
                    }
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
