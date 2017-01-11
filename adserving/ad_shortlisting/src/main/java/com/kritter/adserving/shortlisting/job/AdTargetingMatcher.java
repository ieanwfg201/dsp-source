package com.kritter.adserving.shortlisting.job;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.common.caches.metrics.cache.MetricsCache;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.geo.common.entity.CountryUserInterfaceId;
import com.kritter.geo.common.entity.CountryUserInterfaceIdSecondaryIndex;
import com.kritter.geo.common.entity.IspUserInterfaceId;
import com.kritter.geo.common.entity.IspUserInterfaceIdSecondaryIndex;
import com.kritter.geo.common.entity.reader.*;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.entity.*;
import com.kritter.serving.demand.index.*;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.SetUtils;
import lombok.Getter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.SQLException;
import java.util.*;

/**
 * This class picks campaign ids and returns adids after ad attributes
 * targeting match and supply side filteration that apply on ad units.
 *
 * Regarding the campaigns for these adids check if flight dates are valid
 * and if budget remaining is acceptable to go ahead.
 */
public class AdTargetingMatcher implements Job
{
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
    private String adNoFillReasonMapKey;
    private CampaignCache campaignCache;
    private MetricsCache metricsCache;

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
                                List<TargetingMatcher> targetingMatchers,
                                String adNoFillReasonMapKey,
                                CampaignCache campaignCache,
                                MetricsCache metricsCache
                             ) throws SQLException
    {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.requestObjectKey = requestObjectKey;
        this.shortlistedAdKey = shortlistedAdKey;
        this.selectedSiteCategoryIdKey = selectedSiteCategoryIdKey;
        this.contextHandsetMasterDataKey = contextHandsetMasterDataKey;
        this.adEntityCache = adEntityCache;
        this.countryUserInterfaceIdCache = countryUserInterfaceIdCache;
        this.ispUserInterfaceIdCache = ispUserInterfaceIdCache;
        this.targetingMatchers = targetingMatchers;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
        this.campaignCache = campaignCache;
        this.metricsCache = metricsCache;
    }

    /**
     * This function picks adids which are good (in terms of targeting profile) for this supply.
     * @param context
     * @return
     */
    @Override
    public void execute(Context context)
    {
        Request request = (Request)context.getValue(requestObjectKey);

        Set<Integer> finalShortlistedAdIds;

        Integer countryIdToQueryAds = (null == request.getCountry() ?
                                       null : request.getCountry().getCountryInternalId());

        Integer carriedIdToQueryAds = (null == request.getInternetServiceProvider() ?
                                       null : request.getInternetServiceProvider().getOperatorInternalId());

        //pick adids for country.
        StringBuilder debugMessage = new StringBuilder();
        logger.debug("CountryId: {} and countryUiId:{} (if available already)",
                      countryIdToQueryAds,request.getCountryUserInterfaceId());

        if(request.isRequestForSystemDebugging())
        {
            debugMessage = new StringBuilder("Going to pick up ads for countryId: ");
            debugMessage.append(String.valueOf(countryIdToQueryAds));
            debugMessage.append(" , or countryUiId(if available already )");
            debugMessage.append(String.valueOf(request.getCountryUserInterfaceId()));
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }

        Set<Integer> adIdsForCountry = null;

        if(null != request.getCountryUserInterfaceId())
            adIdsForCountry = pickAdIdsForCountryUserInterfaceId(request,request.getCountryUserInterfaceId());
        else
            adIdsForCountry = pickAdIdsForCountryId(request,countryIdToQueryAds);

        logger.debug("AdIds shortlisted for countryId: {} , or countryUiId: {} are: {} ",
                      countryIdToQueryAds,request.getCountryUserInterfaceId(),adIdsForCountry);

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append("AdIds shortlisted for countryId: ");
            debugMessage.append(String.valueOf(countryIdToQueryAds));
            debugMessage.append(" , or countryUiId: ");
            debugMessage.append(String.valueOf(request.getCountryUserInterfaceId()));
            debugMessage.append(" are: ");
            debugMessage.append(adIdsForCountry.toString());
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }

        //shortlist on basis of country carrier id.
        logger.debug("Going to pick up ads for carrierId: {} , or carrierUiId: {} (if available already)",
                      carriedIdToQueryAds,request.getCarrierUserInterfaceId());

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append("Going to pick up ads for carrierId: ");
            debugMessage.append(String.valueOf(carriedIdToQueryAds));
            debugMessage.append(" , or carrierUiId(if available already )");
            debugMessage.append(String.valueOf(request.getCarrierUserInterfaceId()));
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }

        Set<Integer> adIdsForCarrier = null;
        if(null != request.getCarrierUserInterfaceId())
            adIdsForCarrier = pickAdIdsForCountryCarrierUserInterfaceId(request,request.getCarrierUserInterfaceId());
        else
            adIdsForCarrier = pickAdIdsForCountryCarrierId(request,carriedIdToQueryAds);

        logger.debug("AdIds shortlisted for Carrier id: {} , or carrierUiId: {} are: {} ",
                      carriedIdToQueryAds,request.getCarrierUserInterfaceId(),adIdsForCarrier);

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append("AdIds shortlisted for Carrier id: ");
            debugMessage.append(String.valueOf(carriedIdToQueryAds));
            debugMessage.append(" , or carrierUiId: ");
            debugMessage.append(String.valueOf(request.getCarrierUserInterfaceId()));
            debugMessage.append(" are: ");
            debugMessage.append(adIdsForCarrier.toString());
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }

        adIdsForCountry = SetUtils.intersectNSets(adIdsForCountry,adIdsForCarrier);

        logger.debug("AdIds shortlisted for Country and Carrier intersection are : {} ", adIdsForCountry);

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append("AdIds shortlisted for Country and Carrier intersection are : ");
            debugMessage.append(adIdsForCountry);
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }
        /****************************************Set no fill reason.**********************************************/
        if(adIdsForCountry.size() <= 0)
            request.setNoFillReason(NoFillReason.NO_ADS_COUNTRY_CARRIER);
        /*********************************************************************************************************/

        //pick adids for handset manufacturer/brand.
        HandsetMasterData handsetMasterData = request.getHandsetMasterData();
        context.setValue(this.contextHandsetMasterDataKey, handsetMasterData);

        Set<Integer> adIdsForHandsetBrand =
             pickAdIdsForHandsetBrand(null == handsetMasterData ? null : handsetMasterData.getManufacturerId(),request);

        logger.debug("AdIds shortlisted for manufacturer id : {} , are: {} ",
                      String.valueOf(null == handsetMasterData ? null : handsetMasterData.getManufacturerId()),
                      adIdsForHandsetBrand);

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append("AdIds shortlisted for brand id : ");
            debugMessage.append(String.valueOf(null == handsetMasterData ? null : handsetMasterData.getManufacturerId()));
            debugMessage.append(" are: ");
            debugMessage.append(adIdsForHandsetBrand.toString());
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }
        /****************************************Set no fill reason.**********************************************/
        if(null == request.getNoFillReason() && adIdsForHandsetBrand.size() <= 0)
            request.setNoFillReason(NoFillReason.NO_ADS_BRAND);
        /*********************************************************************************************************/

        //pick adids for handset model.
        Set<Integer> adIdsForHandsetModel =
                pickAdIdsForHandsetModel(null == handsetMasterData ? null : handsetMasterData.getModelId(),request);

        logger.debug("AdIds shortlisted for model id : {} , are: {} ",
                      String.valueOf(null == handsetMasterData ? null : handsetMasterData.getModelId()),
                      adIdsForHandsetModel);

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append(" AdIds shortlisted for model id : ");
            debugMessage.append(String.valueOf(null == handsetMasterData ? null : handsetMasterData.getModelId()));
            debugMessage.append(" are: ");
            debugMessage.append(adIdsForHandsetModel.toString());
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }
        /****************************************Set no fill reason.**********************************************/
        if(null == request.getNoFillReason() && adIdsForHandsetModel.size() <= 0)
            request.setNoFillReason(NoFillReason.NO_ADS_MODEL);
        /*********************************************************************************************************/

        //now take the intersection for individual shortlisted sets.
        finalShortlistedAdIds = SetUtils.intersectNSets(
                                                        adIdsForCountry,
                                                        adIdsForHandsetBrand,
                                                        adIdsForHandsetModel
                                                       );

        logger.debug("First set of shortlisted adids before filters: {} ", finalShortlistedAdIds);

        if(request.isRequestForSystemDebugging())
        {
            debugMessage.setLength(0);
            debugMessage.append("First set of shortlisted adids before filters : ");
            debugMessage.append(finalShortlistedAdIds);
            request.addDebugMessageForTestRequest(debugMessage.toString());
        }

        // Pick only those ads with valid campaigns
        finalShortlistedAdIds = getAdsWithValidCampaigns(finalShortlistedAdIds, campaignCache, request, context);
        ReqLog.debugWithDebugNew(logger, request, "Second set of shortlisted ads with valid campaigns : ",
                finalShortlistedAdIds);

        try
        {
            int osId = (null == handsetMasterData) ? -1 : handsetMasterData.getDeviceOperatingSystemId();
            String osVersion = (null == handsetMasterData) ? null : handsetMasterData.getDeviceOperatingSystemVersion();

            logger.debug("Os detected: {} ,OSVersion detected : {} ",osId, osVersion);
            if(request.isRequestForSystemDebugging())
            {
                debugMessage.setLength(0);
                debugMessage.append("Os detected ");
                debugMessage.append(osId);
                debugMessage.append(" , OSVersion detected: ");
                debugMessage.append(osVersion);
                request.addDebugMessageForTestRequest(debugMessage.toString());
            }
        }
        catch (RuntimeException e)
        {
            logger.error("RuntimeException inside AdTargetingMatcher " , e);
            if(request.isRequestForSystemDebugging())
            {
                debugMessage.setLength(0);
                debugMessage.append("RuntimeException inside AdTargetingMatcher");
                debugMessage.append(e);
                request.addDebugMessageForTestRequest(debugMessage.toString());
            }
            finalShortlistedAdIds = Collections.<Integer>emptySet();
        }

        for(TargetingMatcher targetingMatcher : targetingMatchers) {
            metricsCache.incrementInvocations(targetingMatcher.getName());
            long beginTime = System.nanoTime();
            finalShortlistedAdIds = targetingMatcher.shortlistAds(finalShortlistedAdIds, request, context);
            long endTime = System.nanoTime();
            metricsCache.incrementLatency(targetingMatcher.getName(), (endTime - beginTime + 500) / 1000);

            logger.debug("Shortlisted ads after targeting-matcher : {}", targetingMatcher.getName());

            if(request.isRequestForSystemDebugging())
            {
                debugMessage.setLength(0);
                debugMessage.append("Shortlisted ads after targeting-matcher : ");
                debugMessage.append(targetingMatcher.getName());
                request.addDebugMessageForTestRequest(debugMessage.toString());
            }

            if(finalShortlistedAdIds == null || finalShortlistedAdIds.size() == 0)
            {
                logger.debug("No ads shortlisted .... there is going to be DSP calls or passback if configured.");
                if(request.isRequestForSystemDebugging())
                {
                    debugMessage.setLength(0);
                    debugMessage.append("No ads shortlisted .... there is going to be passback if configured.");
                    request.addDebugMessageForTestRequest(debugMessage.toString());
                }
            }
            else
            {
                logger.debug("Ads are shortlisted after first level of all targeting matchers as : {} ",
                              finalShortlistedAdIds);
                if(request.isRequestForSystemDebugging())
                {
                    debugMessage.setLength(0);
                    debugMessage.append("Ads are shortlisted after first level of all targeting matchers as : ");
                    debugMessage.append(finalShortlistedAdIds);
                    request.addDebugMessageForTestRequest(debugMessage.toString());
                }
            }
        }

        /****************************************** Filters end here **************************************/

        context.setValue(this.shortlistedAdKey, finalShortlistedAdIds);
        Short selectedSiteCategoryId = findSelectedSiteCategoryId();
        context.setValue(this.selectedSiteCategoryIdKey, selectedSiteCategoryId);
    }

    /**
     * Given a set of ad ids, returns only those ads which have campaigns in campaign cache.
     * @param adIdSet Set of shortlisted ad ids
     * @param campaignCache campaign cache containing all campaigns
     * @param request Adserving request object
     * @param context Workflow context object
     * @return Set containing ads having valid campaigns
     */
    private Set<Integer> getAdsWithValidCampaigns(Set<Integer> adIdSet, CampaignCache campaignCache, Request request,
                                                  Context context) {
        if(adIdSet == null || adIdSet.isEmpty())
            return adIdSet;

        Set<Integer> validAdIdSet = new HashSet<Integer>();

        for(int adId: adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);

            if (null == adEntity) {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache for ad id: {}. Dropping it.",
                        adId);
                continue;
            }

            int campaignId = adEntity.getCampaignIncId();
            Campaign campaign = campaignCache.query(campaignId);

            if(null == campaign) {
                ReqLog.debugWithDebugNew(logger,request, "Campaign not found in cache for campaign id: {}. Dropping " +
                        "ad id : {}.", campaignId, adId);

                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, NoFillReason.CAMPAIGN_NOT_FOUND.getValue(),
                        this.adNoFillReasonMapKey, context);
                continue;
            }

            validAdIdSet.add(adId);
        }

        return validAdIdSet;
    }

    private Set<Integer> pickAdIdsForCountryUserInterfaceId(Request request,Integer countryUserInterfaceId)
    {
        Set<Integer> adIdSet = null;

        try
        {
            adIdSet = adEntityCache.query(new CountryIdIndex(countryUserInterfaceId));
        }
        catch (UnSupportedOperationException e)
        {
            logger.error("Exception inside pickAdIdsForCountryUserInterfaceId() of AdTargetingMatcher ", e);
            if (request.isRequestForSystemDebugging())
            {
                request.addDebugMessageForTestRequest("Exception inside pickAdIdsForCountryUserInterfaceId() ");
                request.addDebugMessageForTestRequest(e.toString());
            }
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

            if(null == countryUiId)
                request.setCountryUserInterfaceId(ApplicationGeneralUtils.DEFAULT_COUNTRY_ID);
            else
                request.setCountryUserInterfaceId(countryUiId);
        }
        catch (UnSupportedOperationException e)
        {
            logger.error("Exception inside pickAdIdsForCountryId() of AdTargetingMatcher ",e);
            if(request.isRequestForSystemDebugging())
            {
                request.addDebugMessageForTestRequest("Exception inside pickAdIdsForCountryId() of AdTargetingMatcher");
                request.addDebugMessageForTestRequest(e.toString());
            }
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
        }
        catch (UnSupportedOperationException e)
        {
            logger.error("Exception inside pickAdIdsForCountryCarrierUserInterfaceId() of AdTargetingMatcher ",e);
            if(request.isRequestForSystemDebugging())
            {
                request.addDebugMessageForTestRequest("Exception inside pickAdIdsForCountryCarrierUserInterfaceId() of AdTargetingMatcher");
                request.addDebugMessageForTestRequest(e.toString());
            }
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

            if(null == ispUiId)
                request.setCarrierUserInterfaceId(ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID);
            else
            {
                request.setCarrierUserInterfaceId(ispUiId);

                /**Set the carrier name as well so as to allow bid request enrichment*/
                IspUserInterfaceId ispUserInterfaceId = this.ispUserInterfaceIdCache.query(ispUiId);
                if(null != ispUserInterfaceId)
                    request.setBidRequestDeviceCarrier(ispUserInterfaceId.getIspUIName());
            }
        }
        catch (UnSupportedOperationException e)
        {
            logger.error("Exception inside pickAdIdsForCountryId() of AdTargetingMatcher ",e);
            if(request.isRequestForSystemDebugging())
            {
                request.addDebugMessageForTestRequest("Exception inside pickAdIdsForCountryCarrierId() of AdTargetingMatcher");
                request.addDebugMessageForTestRequest(e.toString());
            }
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
            logger.error("Exception inside pickAdIdsForHandsetBrand() of AdTargetingMatcher ",e);
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
            logger.error("Exception inside pickAdIdsForHandsetModel() of AdTargetingMatcher ",e);
        }

        return (null==adIdSet) ? Collections.<Integer>emptySet() : adIdSet;
    }


    //TODO perform some logic to find out what should be the site's selected category id.
    public Short findSelectedSiteCategoryId()
    {
        return ApplicationGeneralUtils.DEFAULT_SELECTED_SITE_CATEGORY_ID;
    }
}
