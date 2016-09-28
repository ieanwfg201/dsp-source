package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.LatLonRadiusUnit;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.geo.common.entity.reader.FileBasedLatLonDetectionCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class FileBasedLatLongTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_LAT_LONG;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private FileBasedLatLonDetectionCache fileBasedLatLonDetectionCache;
    private String adNoFillReasonMapKey;

    public FileBasedLatLongTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
    		FileBasedLatLonDetectionCache fileBasedLatLonDetectionCache,
                                   String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.fileBasedLatLonDetectionCache = fileBasedLatLonDetectionCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsBasedOnFileLatLongTargeting of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsBasedOnFileLatLongTargeting of AdTargetingMatcher...");

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
            String[] latlonFileArray = targetingProfile.getLatLonFileIdArray();
            LatLonRadiusUnit llRUnit=LatLonRadiusUnit.getEnum(targetingProfile.getLat_lon_radius_unit());
            if(llRUnit==null){
            	llRUnit = LatLonRadiusUnit.MILES;
            }


            if( null == latlonFileArray || latlonFileArray.length ==0 )
            {
                ReqLog.debugWithDebugNew(logger, request, "The ad {} is not custom lat lon file targeted so passing the filter....", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            }

            else
            {if(fileBasedLatLonDetectionCache.doesLatLonExistsinLatLonFiles(
                            request.getRequestingLatitudeValue(),
                            request.getRequestingLongitudeValue(),
                            latlonFileArray, llRUnit
                    )
                    )
            {
                ReqLog.debugWithDebugNew(logger, request, "The ad {} is custom latlon targeted and passes the check...", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
            }
            else
            {
                ReqLog.debugWithDebugNew(logger, request, "The ad {} is custom lat lon targeted and fails the check...", adEntity.getAdGuid());

                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }}
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
