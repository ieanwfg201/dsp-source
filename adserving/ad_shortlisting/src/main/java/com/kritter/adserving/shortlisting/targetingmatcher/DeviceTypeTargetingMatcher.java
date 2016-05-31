package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.DeviceType;
import com.kritter.core.workflow.Context;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class performs device type targeting.
 */
public class DeviceTypeTargetingMatcher implements TargetingMatcher
{
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public DeviceTypeTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache)
    {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context)
    {
        logger.info("Inside filterAdIdsForDeviceTypeTargeting of AdTargetingMatcher ...");
        ReqLog.requestDebug(request, "Inside filterAdIdsForDeviceTypeTargeting of AdTargetingMatcher ...");

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
                ReqLog.errorWithDebug(logger,request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            HandsetMasterData handsetMasterData = request.getHandsetMasterData();

            if(null == handsetMasterData)
            {
                logger.error("DeviceTypeTargetingMatcher could not be applied on ads, handset master data missing.");
                return shortlistedAdIdSet;
            }

            Short[] deviceTypeTargetedArray = adEntity.getTargetingProfile().getDeviceTypeArray();
            DeviceType deviceType = handsetMasterData.getDeviceType();

            boolean adDoesNotTargetDeviceType = (null == deviceTypeTargetedArray || deviceTypeTargetedArray.length <= 0);

            //no device type detected and ad does not target any device type.
            if(null == deviceType && adDoesNotTargetDeviceType)
            {
                ReqLog.debugWithDebug(logger,request, "Ad:{} does not target any device type (desktop/mobile etc)...passing it and no device type detected",adEntity.getAdGuid());

                shortlistedAdIdSet.add(adId);
                continue;
            }

            short requestingDeviceTypeCode = (short)deviceType.getCode();
            logger.debug("DeviceTypeid from request: {} ", requestingDeviceTypeCode);

            if(adDoesNotTargetDeviceType)
            {
                ReqLog.debugWithDebug(logger,request, "Ad:{} does not target any device type (desktop/mobile etc)...passing it",
                              adEntity.getAdGuid());

                shortlistedAdIdSet.add(adId);
                continue;
            }

            else
            {
                for(Short deviceTypeId : deviceTypeTargetedArray)
                {
                    if(requestingDeviceTypeCode == deviceTypeId)
                    {
                        ReqLog.debugWithDebug(logger,request, "Ad:{}  targets device type (desktop/mobile etc)...passing it",
                        adEntity.getAdGuid());
                        shortlistedAdIdSet.add(adId);
                        break;
                    }
                }
            }
        }

        return shortlistedAdIdSet;
    }
}
