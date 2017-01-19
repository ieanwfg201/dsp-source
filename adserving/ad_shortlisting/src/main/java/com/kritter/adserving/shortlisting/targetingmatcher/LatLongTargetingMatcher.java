package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.LatLonRadiusUnit;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class LatLongTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_LAT_LONG;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public LatLongTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                   String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsBasedOnLatLongTargeting of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsBasedOnLatLongTargeting of AdTargetingMatcher...");

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
            LatLonRadiusUnit llRUnit=LatLonRadiusUnit.getEnum(targetingProfile.getLat_lon_radius_unit());
            if(llRUnit==null){
            	llRUnit = LatLonRadiusUnit.MILES;
            }
            TargetingProfile.LatitudeLongitudeRadius[] latitudeLongitudeRadiusArray =
                    targetingProfile.getLatitudeLongitudeRadiusArray();

            if( null == latitudeLongitudeRadiusArray || latitudeLongitudeRadiusArray.length ==0 )
            {
                ReqLog.debugWithDebugNew(logger, request, "The adguid {} is not lat long targeted so passing the filter....", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            }

            else
            {

                if(null == request.getRequestingLatitudeValue() ||
                        null == request.getRequestingLongitudeValue())
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

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
                    
                    double radius= entry.getRadius();
                    if(llRUnit==LatLonRadiusUnit.KM){
                    	/*Converting to miles*/
                    	radius = radius*0.621371;
                    }
                    if(distanceFromRequestingPosition <= radius)
                    {
                        ReqLog.debugWithDebugNew(logger, request, "The ad is latlong targeted and passes the check, adGuId: {}",adEntity.getAdGuid());
                        shortlistedAdIdSet.add(adId);
                        break;
                    }
                    else
                    {
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                this.adNoFillReasonMapKey, context);

                        ReqLog.debugWithDebugNew(logger, request, "The ad is latlong targeted and fails the check,adId {}",adEntity.getAdGuid());
                    }
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
