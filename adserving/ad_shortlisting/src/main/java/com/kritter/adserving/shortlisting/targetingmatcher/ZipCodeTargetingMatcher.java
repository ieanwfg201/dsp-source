package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.geo.common.entity.CountryUserInterfaceIdCountryCodeSecondaryIndex;
import com.kritter.geo.common.entity.ZipCodeInfoDetected;
import com.kritter.geo.common.entity.reader.CountryUserInterfaceIdCache;
import com.kritter.geo.common.entity.reader.ZipCodeDetectionCache;
import com.kritter.geo.common.entity.reader.ZipCodeFileDataCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZipCodeTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_ZIPCODE;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private ZipCodeFileDataCache zipCodeFileDataCache;
    private ZipCodeDetectionCache zipCodeDetectionCache;
    private CountryUserInterfaceIdCache countryUserInterfaceIdCache;
    private String adNoFillReasonMapKey;

    public ZipCodeTargetingMatcher(
            String name, String loggerName, AdEntityCache adEntityCache,
            ZipCodeFileDataCache zipCodeFileDataCache,
            ZipCodeDetectionCache zipCodeDetectionCache,
            CountryUserInterfaceIdCache countryUserInterfaceIdCache,
            String adNoFillReasonMapKey
    ) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);

        this.adEntityCache = adEntityCache;
        this.zipCodeFileDataCache = zipCodeFileDataCache;
        this.zipCodeDetectionCache = zipCodeDetectionCache;
        this.countryUserInterfaceIdCache = countryUserInterfaceIdCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForZipCodeTargeting of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForZipCodeTargeting of AdTargetingMatcher...");

        Set<Integer> shortListedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            String[] zipCodeFileArray = targetingProfile.getZipCodeFileIdArray();

            if( null == zipCodeFileArray || zipCodeFileArray.length ==0 )
            {
                ReqLog.debugWithDebugNew(logger, request, "The ad is not zip code targeted so passing the filter - adguid{}", adEntity.getAdGuid());
                shortListedAdIdSet.add(adId);
            }

            else
            {
                Set<String> zipCodeDetectedSet =
                        findNearestZipCodeForRequestingLatitudeLongitude(request.getCountryUserInterfaceId(),
                                request.getRequestingLatitudeValue(),
                                request.getRequestingLongitudeValue());

                if(
                        zipCodeFileDataCache.
                                doesRequestingZipCodeSetExistInTargetedFiles
                                        (
                                                zipCodeDetectedSet,
                                                zipCodeFileArray
                                        )
                        )
                {
                    ReqLog.debugWithDebugNew(logger, request, "The ad is zipcode targeted and passes the check, detected adGuid {} ZipCodeSet:{} ",
                            adEntity.getAdGuid(), zipCodeDetectedSet);

                    shortListedAdIdSet.add(adId);
                }
                else
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    ReqLog.debugWithDebugNew(logger, request, "The ad is zipcode targeted and fails the check..aguid {} .", adEntity.getAdGuid());
                }
            }
        }

        if(null == request.getNoFillReason() && shortListedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortListedAdIdSet;
    }

    /*
 * This function finds out the nearest zipcodes for requested latitude , longitude.
 * Later in future we might add constraint on ZipCodeInfoDetected to be at some
 * maximum distance from requesting latitude,longitude and is not just closest for
 * the sake of finding the closest point.
 * Have a check so that the detected ui country in the beginning of admatching matches
 * the detected zipcode country codes, only those countrycode entries should be allowed
 * which matches the ui country id detected here in initial phase.
 **/
    private Set<String> findNearestZipCodeForRequestingLatitudeLongitude(Integer uiCountryIdDetected,
                                                                         Double latitude,
                                                                         Double longitude)
    {
        logger.debug("Inside findNearestZipCodeForRequestingLatitudeLongitude of AdTargetingMatcher...");

        Set<String> zipCodeFinalSet = null;

        if(null == latitude || null == longitude)
            return null;

        zipCodeFinalSet = new HashSet<String>();

        logger.debug("Finding nearest zipcode list for requesting latitude {} and longitude {} ",
                latitude, longitude);

        List<ZipCodeInfoDetected> nearestDetectedZipCodes =
                this.zipCodeDetectionCache.findNearestZipCodeInfo(latitude,longitude);

        logger.debug("Nearest ZipCode Detected:{} ", nearestDetectedZipCodes);

        for(ZipCodeInfoDetected zipCodeInfoDetected : nearestDetectedZipCodes)
        {
            //for each zipcode entry find corresponding ui country id and prepend to it.
            String countryCode = zipCodeInfoDetected.getCountryCode();

            Integer uiCountryId = null;

            try
            {
                Set<Integer> uiCountryIdSetForCountryCode =
                        this.countryUserInterfaceIdCache.
                                query(new CountryUserInterfaceIdCountryCodeSecondaryIndex(countryCode));

                if(null != uiCountryIdSetForCountryCode)
                {
                    for(Integer uiCountryIdEntry : uiCountryIdSetForCountryCode)
                    {
                        uiCountryId = uiCountryIdEntry;
                        break;
                    }
                }
            }
            catch (UnSupportedOperationException unsoe)
            {
                this.logger.error("UnSupportedOperationException inside AdTargetingMatcher ",unsoe);
            }

            //if detected ui country id doesnot match the one here skip it.
            if(null == uiCountryId         ||
                    null == uiCountryIdDetected ||
                    uiCountryId.intValue() != uiCountryIdDetected.intValue() )
            {
                logger.error(" Detected uiCountryId from location detection {} and countryCode from  zipcode detection via lat-long is: {} , detected uiCountryID and zipcode country id doesnot match",
                        uiCountryIdDetected,uiCountryId);
                continue;
            }

            for(String zipCode : zipCodeInfoDetected.getZipCodeSet())
            {
                StringBuffer sb = new StringBuffer();
                sb.append(uiCountryId);
                sb.append(ZipCodeFileDataCache.DELIMITER);
                sb.append(zipCode);

                zipCodeFinalSet.add(sb.toString());
            }
        }

        return zipCodeFinalSet;
    }
}
