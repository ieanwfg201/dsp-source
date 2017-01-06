package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.geo.common.entity.City;
import com.kritter.geo.common.entity.CityUserInterfaceId;
import com.kritter.geo.common.entity.CityUserInterfaceIdSecondaryIndex;
import com.kritter.geo.common.entity.reader.CityDetectionCache;
import com.kritter.geo.common.entity.reader.CityUserInterfaceIdCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.url.URLField;
import com.kritter.utils.common.url.URLFieldProcessingException;
import com.kritter.utils.entity.TargetingProfileLocationEntity;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

/**
 * This class performs detection and targeting matching for cities targeted by an ad.
 **/
public class CityTargetingMatcher implements TargetingMatcher
{
    private static NoFillReason noFillReason = NoFillReason.NO_ADS_CITY;
    private String name;
    private Logger logger;
    private String adNoFillReasonMapKey;
    private AdEntityCache adEntityCache;
    private CityDetectionCache cityDetectionCache;
    private CityUserInterfaceIdCache cityUserInterfaceIdCache;

    public CityTargetingMatcher(
                                String name,
                                String loggerName,
                                String adNoFillReasonMapKey,
                                AdEntityCache adEntityCache,
                                CityDetectionCache cityDetectionCache,
                                CityUserInterfaceIdCache cityUserInterfaceIdCache
                               )
    {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
        this.adEntityCache = adEntityCache;
        this.cityDetectionCache = cityDetectionCache;
        this.cityUserInterfaceIdCache = cityUserInterfaceIdCache;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside CityTargetingMatcher of AdTargetingMatcher ...");
        ReqLog.requestDebugNew(request, "Inside CityTargetingMatcher of AdTargetingMatcher ...");

        if (adIdSet == null || adIdSet.size() == 0) {
            logger.debug("No ads to shortlist from inside CityTargetingMatcher. Returning!");
            ReqLog.requestDebugNew(request, "No ads to shortlist from inside CityTargetingMatcher. Returning!");
            return adIdSet;
        }

        String ipAddress = request.getIpAddressUsedForDetection();
        City city = null;
        CityUserInterfaceId cityUserInterfaceId = null;

        try
        {
            city = cityDetectionCache.findCityForIpAddress(ipAddress);

            if (null != city)
            {
                logger.debug("City detected id is: {} " , city.getCityId());
                ReqLog.requestDebugNew(request,"City detected id is: " + city.getCityId());
                request.setDataSourceNameUsedForCityDetection(city.getDataSourceName());

                Set<Integer> uiIdSetCity = cityUserInterfaceIdCache.query(
                        new CityUserInterfaceIdSecondaryIndex(city.getCityId()));
                if (null != uiIdSetCity)
                {
                    for (Integer cityUiId : uiIdSetCity)
                    {
                        cityUserInterfaceId = cityUserInterfaceIdCache.query(cityUiId);
                        break;
                    }
                }
            }
        } catch (Exception e)
        {
            logger.error("Exception inside CityTargetingMatcher in detecting city for ip address: {} ",
                    ipAddress, e);
            logger.error("City cannot be detected, some error happened, only ads with no city targeting " +
                    "will be shortlisted.");
            ReqLog.requestDebugNew(request, "City cannot be detected, some error happened, only ads with " +
                    "no city targeting will be shortlisted,  " + e.getMessage());
        }

        if (null != cityUserInterfaceId)
        {
            request.setCityUserInterfaceId(cityUserInterfaceId.getCityUserInterfaceId());

            try
            {
                URLField urlField = URLField.CITY_ID;
                urlField.getUrlFieldProperties().setFieldValue(cityUserInterfaceId.getCityUserInterfaceId());
                request.getUrlFieldFactory().stackFieldForStorage(urlField);
            }
            catch (URLFieldProcessingException urfpe)
            {
                logger.error("URLFieldProcessingException inside CityTargetingMatcher ", urfpe);
            }

            logger.debug("City user interface id detected is: {} ", cityUserInterfaceId.getCityUserInterfaceId());
            ReqLog.requestDebugNew(request,"City user interface id detected is: " +
                                cityUserInterfaceId.getCityUserInterfaceId());
        }
        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if (null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache id : {}", adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            TargetingProfileLocationEntity cityTargetingEntity = targetingProfile.getTargetedCities();

            Integer[] targetedCityUIIdSet = null;

            if(null != cityTargetingEntity)
                targetedCityUIIdSet = cityTargetingEntity.fetchAllKeyArrayFromDataMap();

            if (null == cityTargetingEntity || null == targetedCityUIIdSet)
            {
                logger.debug("AdId: {} does not target any city , so passing it... ", adId);
                ReqLog.requestDebugNew(request,"AdId:" + adId + " does not target any city , so passing it...");
                shortlistedAdIdSet.add(adId);
                continue;
            }

            if(null == cityUserInterfaceId)
            {
                logger.error("City is null, only ads with no city targeting will pass...");
                ReqLog.requestDebugNew(request,"City is null, only ads with no city targeting will pass...");
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                                              this.adNoFillReasonMapKey, context);
                continue;
            }

            boolean targetingFound = false;
            for(Integer cityIdTargeted : targetedCityUIIdSet)
            {
                if(cityIdTargeted.intValue() == cityUserInterfaceId.getCityUserInterfaceId().intValue())
                {
                    logger.debug("CityUserInterfaceId: {} targeted by ad: {} and is detected in request,passing ad.",
                                  cityUserInterfaceId.getCityUserInterfaceId(),adId);

                    ReqLog.requestDebugNew(request,"CityUserInterfaceId: " +
                                        cityUserInterfaceId.getCityUserInterfaceId() +
                                        " targeted by ad: " + adId +
                                        " and is detected in request,passing ad.");

                    shortlistedAdIdSet.add(adId);
                    targetingFound = true;
                    break;
                }
            }
            if(!targetingFound)
            {
                logger.debug("CityUserInterfaceId: {} targeted by ad: {} and is not detected in request,failing ad.",
                              cityUserInterfaceId.getCityUserInterfaceId(),adId);
                ReqLog.requestDebugNew(request,"CityUserInterfaceId : " + cityUserInterfaceId.getCityUserInterfaceId() +
                                    " targeted by ad: " + adId + " and is not detected in request,failing ad.");
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                                              this.adNoFillReasonMapKey, context);
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}