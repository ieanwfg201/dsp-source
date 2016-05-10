package com.kritter.adserving.shortlisting.job;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.geo.common.entity.CountryUserInterfaceIdSecondaryIndex;
import com.kritter.geo.common.entity.IspUserInterfaceIdSecondaryIndex;
import com.kritter.geo.common.entity.reader.*;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.*;
import com.kritter.serving.demand.index.*;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.SetUtils;
import lombok.Getter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * This class picks campaign ids and returns adids after ad attributes
 * targeting match and supply side filteration that apply on ad units.
 *
 * Regarding the campaigns for these adids check if flight dates are valid
 * and if budget remaining is acceptable to go ahead.
 */
public class AdTargetingMatcher implements Job {
    @Getter
    private String name;
    private Logger logger;

    private String requestObjectKey;
    private String shortlistedAdKey;
    private String selectedSiteCategoryIdKey;
    private String contextHandsetMasterDataKey;
    private AdEntityCache adEntityCache;
    private CountryUserInterfaceIdCache countryUserInterfaceIdCache;
    private ISPUserInterfaceIdCache ispUserInterfaceIdCache;
    private List<TargetingMatcher> targetingMatchers;

    public AdTargetingMatcher(
            String name,
            String loggerName,
            String requestObjectKey,
            String shortlistedAdKey,
            String selectedSiteCategoryIdKey,
            String contextHandsetMasterDataKey,
            AdEntityCache adEntityCache,
            CountryUserInterfaceIdCache countryUserInterfaceIdCache,
            ISPUserInterfaceIdCache ispUserInterfaceIdCache,
            List<TargetingMatcher> targetingMatchers) throws SQLException {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.requestObjectKey = requestObjectKey;
        this.shortlistedAdKey = shortlistedAdKey;
        this.selectedSiteCategoryIdKey = selectedSiteCategoryIdKey;
        this.contextHandsetMasterDataKey = contextHandsetMasterDataKey;
        this.adEntityCache = adEntityCache;
        this.countryUserInterfaceIdCache = countryUserInterfaceIdCache;
        this.ispUserInterfaceIdCache = ispUserInterfaceIdCache;
        this.targetingMatchers = targetingMatchers;
    }

    /**
     * This function picks adids which are good (in terms of targeting profile) for this supply.
     * @param context
     * @return
     */
    @Override
    public void execute(Context context) {
        Request request = (Request)context.getValue(requestObjectKey);

        Set<Integer> finalShortlistedAdIds;

        Integer countryIdToQueryAds = (null == request.getCountry() ?
                                       null : request.getCountry().getCountryInternalId());

        Integer carriedIdToQueryAds = (null == request.getInternetServiceProvider() ?
                                       null : request.getInternetServiceProvider().getOperatorInternalId());

        //pick adids for country.
        StringBuilder debugMessage = new StringBuilder("Going to pick up ads for countryId: ");
        debugMessage.append(String.valueOf(countryIdToQueryAds));
        debugMessage.append(" , or countryUiId(if available already )");
        debugMessage.append(String.valueOf(request.getCountryUserInterfaceId()));

        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());

        Set<Integer> adIdsForCountry = null;

        if(null != request.getCountryUserInterfaceId())
            adIdsForCountry = pickAdIdsForCountryUserInterfaceId(request,request.getCountryUserInterfaceId());
        else
            adIdsForCountry = pickAdIdsForCountryId(request,countryIdToQueryAds);

        debugMessage.setLength(0);
        debugMessage.append("AdIds shortlisted for countryId: ");
        debugMessage.append(String.valueOf(countryIdToQueryAds));
        debugMessage.append(" , or countryUiId: ");
        debugMessage.append(String.valueOf(request.getCountryUserInterfaceId()));
        debugMessage.append(" are: ");
        debugMessage.append(adIdsForCountry.toString());
        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());
        ReqLog.requestDebug(request, " and their guid set: ");
        ReqLog.requestDebug(request, fetchGuidSetForAdIncIdSet(adIdsForCountry, adEntityCache).toString());
 
        //shortlist on basis of country carrier id.
        debugMessage.setLength(0);
        debugMessage.append("Going to pick up ads for carrierId: ");
        debugMessage.append(String.valueOf(carriedIdToQueryAds));
        debugMessage.append(" , or carrierUiId(if available already )");
        debugMessage.append(String.valueOf(request.getCarrierUserInterfaceId()));

        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());

        Set<Integer> adIdsForCarrier = null;
        if(null != request.getCarrierUserInterfaceId())
            adIdsForCarrier = pickAdIdsForCountryCarrierUserInterfaceId(request,request.getCarrierUserInterfaceId());
        else
            adIdsForCarrier = pickAdIdsForCountryCarrierId(request,carriedIdToQueryAds);

        debugMessage.setLength(0);
        debugMessage.append("AdIds shortlisted for Carrier id: ");
        debugMessage.append(String.valueOf(carriedIdToQueryAds));
        debugMessage.append(" , or carrierUiId: ");
        debugMessage.append(String.valueOf(request.getCarrierUserInterfaceId()));
        debugMessage.append(" are: ");
        debugMessage.append(adIdsForCarrier.toString());
        
        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());
        ReqLog.requestDebug(request, " and their guid set: ");
        ReqLog.requestDebug(request, fetchGuidSetForAdIncIdSet(adIdsForCarrier, adEntityCache).toString());
        
        adIdsForCountry = SetUtils.intersectNSets(adIdsForCountry,adIdsForCarrier);

        debugMessage.setLength(0);
        debugMessage.append("AdIds shortlisted for Country and Carrier intersection are : ");
        debugMessage.append(adIdsForCountry);
        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());
        ReqLog.requestDebug(request, " and their guid set: ");
        ReqLog.requestDebug(request, fetchGuidSetForAdIncIdSet(adIdsForCountry, adEntityCache).toString());

            /****************************************Set no fill reason.**********************************************/
        if(adIdsForCountry.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.NO_ADS_COUNTRY_CARRIER);
        /*********************************************************************************************************/

        //pick adids for handset manufacturer/brand.
        HandsetMasterData handsetMasterData = request.getHandsetMasterData();
        context.setValue(this.contextHandsetMasterDataKey, handsetMasterData);

        Set<Integer> adIdsForHandsetBrand =
             pickAdIdsForHandsetBrand(null == handsetMasterData ? null : handsetMasterData.getManufacturerId(),request);

        debugMessage.setLength(0);
        debugMessage.append("AdIds shortlisted for brand id : ");
        debugMessage.append(String.valueOf(null == handsetMasterData ? null : handsetMasterData.getManufacturerId()));
        debugMessage.append(" are: ");
        debugMessage.append(adIdsForHandsetBrand.toString());
        
        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());
        ReqLog.requestDebug(request, " and their guid set: ");
        ReqLog.requestDebug(request, fetchGuidSetForAdIncIdSet(adIdsForHandsetBrand, adEntityCache).toString());

        /****************************************Set no fill reason.**********************************************/
        if(null == request.getNoFillReason() && adIdsForHandsetBrand.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.NO_ADS_BRAND);
        /*********************************************************************************************************/

        //pick adids for handset model.
        Set<Integer> adIdsForHandsetModel =
                pickAdIdsForHandsetModel(null == handsetMasterData ? null : handsetMasterData.getModelId(),request);

        debugMessage.setLength(0);
        debugMessage.append(" AdIds shortlisted for model id : ");
        debugMessage.append(String.valueOf(null == handsetMasterData ? null : handsetMasterData.getModelId()));
        debugMessage.append(" are: ");
        debugMessage.append(adIdsForHandsetModel.toString());
        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());
        ReqLog.requestDebug(request, " and their guid set: ");
        ReqLog.requestDebug(request, fetchGuidSetForAdIncIdSet(adIdsForHandsetModel, adEntityCache).toString());

        /****************************************Set no fill reason.**********************************************/
        if(null == request.getNoFillReason() && adIdsForHandsetModel.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.NO_ADS_MODEL);
        /*********************************************************************************************************/

        //now take the intersection for individual shortlisted sets.
        finalShortlistedAdIds = SetUtils.intersectNSets(
                                                        adIdsForCountry,
                                                        adIdsForHandsetBrand,
                                                        adIdsForHandsetModel
                                                       );

        debugMessage.setLength(0);
        debugMessage.append("First set of shortlisted adids before filters : ");
        debugMessage.append(finalShortlistedAdIds);
        ReqLog.debugWithDebug(this.logger, request, debugMessage.toString());
        ReqLog.requestDebug(request, " and their guid set: ");
        ReqLog.requestDebug(request, fetchGuidSetForAdIncIdSet(finalShortlistedAdIds, adEntityCache).toString());

        try
        {
            int osId = (null == handsetMasterData) ? -1 : handsetMasterData.getDeviceOperatingSystemId();
            String osVersion = (null == handsetMasterData) ? null : handsetMasterData.getDeviceOperatingSystemVersion();

            ReqLog.debugWithDebug(logger, request, "Os detected: {} ,OSVersion detected : {} ",osId, osVersion);
        }
        catch (RuntimeException e)
        {
            ReqLog.errorWithDebug(logger, request, "RuntimeException inside AdTargetingMatcher " , e);
            finalShortlistedAdIds = Collections.<Integer>emptySet();
        }

        for(TargetingMatcher targetingMatcher : targetingMatchers) {
            finalShortlistedAdIds = targetingMatcher.shortlistAds(finalShortlistedAdIds, request, context);

            logger.debug("Shortlisted ads after {} = ", targetingMatcher.getName());
            if(finalShortlistedAdIds == null || finalShortlistedAdIds.size() == 0) {
                logger.debug("null");
            } else {
                for(Integer adId : finalShortlistedAdIds) {
                    logger.debug("\tad id : {}", adId);
                }
            }
        }

        /****************************************** Filters end here **************************************/

        context.setValue(this.shortlistedAdKey, finalShortlistedAdIds);
        Short selectedSiteCategoryId = findSelectedSiteCategoryId();
        context.setValue(this.selectedSiteCategoryIdKey, selectedSiteCategoryId);
    }

    private Set<Integer> pickAdIdsForCountryUserInterfaceId(Request request,Integer countryUserInterfaceId) {
        Set<Integer> adIdSet = null;

        try {
            adIdSet = adEntityCache.query(new CountryIdIndex(countryUserInterfaceId));

            ReqLog.debugWithDebug(logger, request, "Picking adIds for countryUiId: {} ",countryUserInterfaceId);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append("Inside pickAdIdsForCountryUserInterfaceId, size of adidset: ");
            logMessage.append(null == adIdSet ? 0 : adIdSet.size());

            ReqLog.debugWithDebug(logger, request, logMessage.toString());
        } catch (UnSupportedOperationException e) {
            ReqLog.errorWithDebug(logger, request, "Exception inside pickAdIdsForCountryUserInterfaceId() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }

    private Set<Integer> pickAdIdsForCountryId(Request request,Integer countryId)
    {
        Set<Integer> adIdSet = null;

        try
        {
            Set<Integer> uiCountryIdSetForDataSourceId =
                    this.countryUserInterfaceIdCache.query(new CountryUserInterfaceIdSecondaryIndex(countryId));

            Integer countryUiId = null;

            if(null != uiCountryIdSetForDataSourceId)
            {
                for(Integer uiCountryIdEntry : uiCountryIdSetForDataSourceId)
                {
                    countryUiId = uiCountryIdEntry;
                    break;
                }
            }

            adIdSet = adEntityCache.query(new CountryIdIndex(countryUiId));

            ReqLog.debugWithDebug(logger, request, "Picking adIds for countryUiId: {} ",countryUiId);

            StringBuffer logMessage = new StringBuffer();
            logMessage.append("Inside pickAdIdsForCountryId, size of adidset: ");
            logMessage.append(null == adIdSet ? 0 : adIdSet.size());

            ReqLog.debugWithDebug(logger, request, logMessage.toString());

            if(null == countryUiId)
                request.setCountryUserInterfaceId(ApplicationGeneralUtils.DEFAULT_COUNTRY_ID);
            else
                request.setCountryUserInterfaceId(countryUiId);
        }
        catch (UnSupportedOperationException e)
        {
            ReqLog.errorWithDebug(logger, request, "Exception inside pickAdIdsForCountryId() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }

    private Set<Integer> pickAdIdsForCountryCarrierUserInterfaceId(
                                                                   Request request,
                                                                   Integer countryCarrierUserInterfaceId
                                                                  )
    {
        Set<Integer> adIdSet = null;

        try
        {
            adIdSet = adEntityCache.query(new CountryCarrierIdIndex(countryCarrierUserInterfaceId));

            ReqLog.debugWithDebug(logger, request, "Picking adIds for ispUiId: {} ", countryCarrierUserInterfaceId);

            StringBuffer logMessage = new StringBuffer();
            logMessage.append("Inside pickAdIdsForCountryCarrierUserInterfaceId, size of adidset: ");
            logMessage.append(null == adIdSet ? 0 : adIdSet.size());

            ReqLog.debugWithDebug(logger, request, logMessage.toString());
        }
        catch (UnSupportedOperationException e)
        {
            ReqLog.errorWithDebug(logger, request, "Exception inside pickAdIdsForCountryId() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }

    private Set<Integer> pickAdIdsForCountryCarrierId(
                                                      Request request,
                                                      Integer countryCarrierId
                                                     )
    {
        Set<Integer> adIdSet = null;

        try
        {
            Set<Integer> uiIspIdSetForDataSourceId =
                    this.ispUserInterfaceIdCache.query(new IspUserInterfaceIdSecondaryIndex(countryCarrierId));

            Integer ispUiId = null;

            if(null != uiIspIdSetForDataSourceId)
            {
                for(Integer uiIspIdEntry : uiIspIdSetForDataSourceId)
                {
                    ispUiId = uiIspIdEntry;
                    break;
                }
            }

            adIdSet = adEntityCache.query(new CountryCarrierIdIndex(ispUiId));

            ReqLog.debugWithDebug(logger, request, "Picking adIds for ispUiId: {} ", ispUiId);

            StringBuffer logMessage = new StringBuffer();
            logMessage.append("Inside pickAdIdsForCountryCarrierId, size of adidset: ");
            logMessage.append(null == adIdSet ? 0 : adIdSet.size());

            ReqLog.debugWithDebug(logger, request, logMessage.toString());

            if(null == ispUiId)
                request.setCarrierUserInterfaceId(ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID);
            else
                request.setCarrierUserInterfaceId(ispUiId);
        }
        catch (UnSupportedOperationException e)
        {
            ReqLog.errorWithDebug(logger, request, "Exception inside pickAdIdsForCountryId() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }

    private Set<Integer> pickAdIdsForHandsetBrand(Integer handsetBrandId,Request request){

        Set<Integer> adIdSet = null;

        try
        {
            adIdSet = adEntityCache.query(new BrandIdIndex(handsetBrandId));
        }
        catch(UnSupportedOperationException e)
        {
            ReqLog.errorWithDebug(logger, request, "Exception inside pickAdIdsForHandsetBrand() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }

    private Set<Integer> pickAdIdsForHandsetModel(Integer handsetModelId,Request request)
    {
        Set<Integer> adIdSet = null;

        try
        {
            adIdSet = adEntityCache.query(new ModelIdIndex(handsetModelId));
        }
        catch(UnSupportedOperationException e)
        {
            ReqLog.errorWithDebug(logger, request, "Exception inside pickAdIdsForHandsetModel() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }

    public static Set<String> fetchGuidSetForAdIncIdSet(Set<Integer> adIdSet, AdEntityCache adEntityCache)
    {
        if(null == adIdSet || adIdSet.size() == 0)
            return new HashSet<String>();

        Set<String> guidSet = new HashSet<String>();

        for(Integer adId: adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null != adEntity)
                guidSet.add(adEntity.getAdGuid());
        }

        return guidSet;
    }

    //TODO perform some logic to find out what should be the site's selected category id.
    public Short findSelectedSiteCategoryId()
    {
        return ApplicationGeneralUtils.DEFAULT_SELECTED_SITE_CATEGORY_ID;
    }
}
