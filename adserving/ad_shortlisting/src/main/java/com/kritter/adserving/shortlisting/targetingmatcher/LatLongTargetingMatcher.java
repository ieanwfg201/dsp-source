package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class LatLongTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public LatLongTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsBasedOnLatLongTargeting of AdTargetingMatcher...");
        ReqLog.requestDebug(request, "Inside filterAdIdsBasedOnLatLongTargeting of AdTargetingMatcher...");

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

            TargetingProfile.LatitudeLongitudeRadius[] latitudeLongitudeRadiusArray =
                    targetingProfile.getLatitudeLongitudeRadiusArray();

            if( null == latitudeLongitudeRadiusArray || latitudeLongitudeRadiusArray.length ==0 )
            {
                ReqLog.debugWithDebug(logger, request, "The adguid {} is not lat long targeted so passing the filter....", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            }

            else
            {

                if(null == request.getRequestingLatitudeValue() ||
                        null == request.getRequestingLongitudeValue())
                {
                    logger.debug("Request does not have latitude longitude value and ad is lat-long targeted" +
                            ", so skipping adid: {} ",adEntity.getAdGuid());
                    continue;
                }

                for(TargetingProfile.LatitudeLongitudeRadius entry : latitudeLongitudeRadiusArray)
                {
                    double distanceFromRequestingPosition =
                            GeoCommonUtils.haversineDistanceInMiles(entry.getLatitude(), entry.getLongitude(),
                                    request.getRequestingLatitudeValue(),
                                    request.getRequestingLongitudeValue());

                    if(distanceFromRequestingPosition <= entry.getRadius())
                    {
                        ReqLog.debugWithDebug(logger, request, "The ad is latlong targeted and passes the check, adGuId: {}",adEntity.getAdGuid());
                        shortlistedAdIdSet.add(adId);
                        break;
                    }
                    else
                    {
                        ReqLog.debugWithDebug(logger, request, "The ad is latlong targeted and fails the check,adId {}",adEntity.getAdGuid());
                    }
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.AD_LAT_LONG);

        return shortlistedAdIdSet;
    }
}
