package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.caches.ext_supply_attr_cache.ExternalSupplyAttributesCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.SupplySourceEnum;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.ExternalSupplyAttributes;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class SupplyInclusionExclusionTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_SUPPLY_INC_EXC;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private ExternalSupplyAttributesCache externalSupplyAttributesCache;
    private String adNoFillReasonMapKey;

    public SupplyInclusionExclusionTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                                    ExternalSupplyAttributesCache externalSupplyAttributesCache,
                                                    String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.externalSupplyAttributesCache = externalSupplyAttributesCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context)
    {
        Site site = request.getSite();

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

         /*Irrespective of what ads target, find the internal id for the external supply attributes of exchange
          *request, and populate in request object.*/
        Integer internalIdForExternalSupplyAttributes =
            externalSupplyAttributesCache.fetchExternalSupplyAttributesInternalId(site.getSiteIncId(),
                site.getExternalSupplyId());

        ReqLog.debugWithDebugNew(logger, request, "Internal id for external Supply attributes found is: {}",
                      internalIdForExternalSupplyAttributes);

        //put default to internal id for external supply attributes.
        if(null == internalIdForExternalSupplyAttributes)
        {
            internalIdForExternalSupplyAttributes = ApplicationGeneralUtils.DEFAULT_INTERNAL_ID_FOR_EXTERNAL_SUPPLY_ATTRIBUTES;
        }

        request.setExternalSupplyAttributesInternalId(internalIdForExternalSupplyAttributes);

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache id : {}" , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            Map<Integer,Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>>>  supplyInclusionExclusionMap =
                    targetingProfile.getSupplyInclusionExclusionMap();

            Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>> publisherSiteListMap = null;
            Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>> publisherSiteListMapExchange = null;
            Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>> publisherSiteListMapNetwork = null;

            /*Get both the exchange and network supply attributes defined.*/
            if(null != supplyInclusionExclusionMap && supplyInclusionExclusionMap.size() > 0)
            {
                publisherSiteListMap = new HashMap<Integer, Map<Integer, List<ExternalSupplyAttributes>>>();
                publisherSiteListMapExchange = supplyInclusionExclusionMap.get(SupplySourceEnum.EXCHANGE.getCode());
                publisherSiteListMapNetwork = supplyInclusionExclusionMap.get(SupplySourceEnum.NETWORK.getCode());
            }

            /*accumulate both the exchange and network supply attributes in a single map for usage in filtering*/
            if(null != publisherSiteListMapExchange && publisherSiteListMapExchange.size() > 0)
            {
                for(Map.Entry<Integer,Map<Integer,List<ExternalSupplyAttributes>>> entry : publisherSiteListMapExchange.entrySet())
                {
                    publisherSiteListMap.put(entry.getKey(),entry.getValue());
                }
            }

            if(null != publisherSiteListMapNetwork && publisherSiteListMapNetwork.size() > 0)
            {
                for(Map.Entry<Integer,Map<Integer,List<ExternalSupplyAttributes>>> entry : publisherSiteListMapNetwork.entrySet())
                {
                    Map<Integer,List<ExternalSupplyAttributes>> alreadyPresentSiteListMap =
                            publisherSiteListMap.get(entry.getKey());

                    if(null != alreadyPresentSiteListMap && null != entry.getValue())
                    {
                        for(Map.Entry<Integer,List<ExternalSupplyAttributes>> entryNew : entry.getValue().entrySet())
                        {
                            alreadyPresentSiteListMap.put(entryNew.getKey(),entryNew.getValue());
                        }
                    }
                    else
                    {
                        alreadyPresentSiteListMap = entry.getValue();
                    }

                    publisherSiteListMap.put(entry.getKey(),alreadyPresentSiteListMap);
                }
            }

            /*Keep a map of site id and its external supply attributes' list...*/
            Map<Integer,List<ExternalSupplyAttributes>> siteListMap = null;

            if(null != publisherSiteListMap && publisherSiteListMap.size() <= 0)
                publisherSiteListMap = null;
            else if(null != publisherSiteListMap && publisherSiteListMap.size() > 0)
                siteListMap = publisherSiteListMap.get(site.getPublisherIncId());

            /*corner case where if sitelistmap is non null and size 0 then assign null*/
            if(null != siteListMap && siteListMap.size() <=0)
                siteListMap = null;

            logger.debug("Looking up external_supply's internal id for siteIncId:{} and externalSupplyId:{} ",
                    site.getSiteIncId(),site.getExternalSupplyId());

            boolean isSupplyExcluded = targetingProfile.isSiteListExcluded();

            /*If supply is included then check if requesting site is included or not*/
            if(!isSupplyExcluded)
            {
                if(null == publisherSiteListMap || publisherSiteListMap.size() <= 0)
                {
                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are not targeted by ad: {} , passing it .",adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                // Publisher site list map doesn't contain the publisher, i.e., the publisher is not included.
                // Skip this ad.
                if(!publisherSiteListMap.containsKey(site.getPublisherIncId())) {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    logger.debug("SupplyAttributes are inclusion targeted by ad: {} with this publisher:{} not " +
                            "specified. Failing it .", adId, site.getPublisherIncId());

                    if(request.isRequestForSystemDebugging())
                    {
                        request.addDebugMessageForTestRequest("SupplyAttributes are inclusion targeted by ad: ");
                        request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" ,with this publisher not specified: ");
                        request.addDebugMessageForTestRequest(String.valueOf(site.getPublisherIncId()));
                        request.addDebugMessageForTestRequest(", failing adId.");
                    }

                    continue;
                }

                // This publisher has been included. Check for site inclusion now
                /*supply attributes are targeted by the ad.Publisher included but no sites specified...*/
                if(null == siteListMap) {

                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are inclusion targeted by ad: {} with no site specified for this publisher id: {} ,passing it .",adEntity.getAdGuid(),site.getPublisherIncId());

                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                /*specific sites are targeted by ad and publisher included but requesting site not included*/
                if(!siteListMap.containsKey(site.getSiteIncId())) {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are inclusion targeted by ad: {} with this site:{} not specified for this publisher id: {} ,failing it .",adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId());
                    continue;
                }

                /*specific sites are targeted by ad and publisher included and requesting site included*/
                if(siteListMap.containsKey(site.getSiteIncId())) {
                    List<ExternalSupplyAttributes> externalSupplyAttributesList = siteListMap.get(site.getSiteIncId());

                    if(null != externalSupplyAttributesList && externalSupplyAttributesList.size() <=0 )
                        externalSupplyAttributesList = null;

                    if(null == externalSupplyAttributesList)
                    {
                        ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are inclusion targeted by ad: {} with this site:{} specified but ext attr not specified for this publisher id: {} , passing it .",adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId());

                        shortlistedAdIdSet.add(adId);
                        continue;
                    }

                    boolean found = false;

                    for(ExternalSupplyAttributes externalSupplyAttributes : externalSupplyAttributesList)
                    {
                        if( null == externalSupplyAttributes.getInternalSupplyId())
                            continue;

                        if(externalSupplyAttributes.getInternalSupplyId().intValue() == internalIdForExternalSupplyAttributes.intValue())
                        {
                            found = true;
                            break;
                        }
                    }

                    if(found)
                    {
                        ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are inclusion targeted by ad: {} with this site:{} specified for this publisher id: {} ,and the internalId for external supply attributes: {} is contained by targeting passing it .",
                                adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId(),internalIdForExternalSupplyAttributes);

                        shortlistedAdIdSet.add(adId);
                        continue;
                    } else {
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                this.adNoFillReasonMapKey, context);

                        if(request.isRequestForSystemDebugging())
                        {
                            request.addDebugMessageForTestRequest("SupplyAttributes are inclusion targeted by ad: ");
                            request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                            request.addDebugMessageForTestRequest(" ,with this site specified: ");
                            request.addDebugMessageForTestRequest(String.valueOf(site.getSiteIncId()));
                            request.addDebugMessageForTestRequest(", for publisher id: ");
                            request.addDebugMessageForTestRequest(String.valueOf(site.getPublisherIncId()));
                            request.addDebugMessageForTestRequest(", and the internal id for external supply attributes: ");
                            request.addDebugMessageForTestRequest(String.valueOf(internalIdForExternalSupplyAttributes));
                            request.addDebugMessageForTestRequest(", is not contained by the targeting passing it.");
                        }
                    }
                }
            }

            //exclusion.
            else if(isSupplyExcluded)
            {
                if(null == publisherSiteListMap || publisherSiteListMap.size() <= 0)
                {
                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are not targeted by ad: {} , passing it .",adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                /*Requesting publisher is not excluded by the ad.*/
                if(!publisherSiteListMap.containsKey(site.getPublisherIncId()))
                {
                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are exclusion targeted by ad: {} with this publisher not specified: {} , passing it. {} ",adEntity.getAdGuid(),site.getPublisherIncId());

                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                /*If exclusion specifies the requesting publisher.*/
                if(null == siteListMap)
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are exclusion targeted by ad: {} with no site specified for this publisher id: {} ,failing it .",adEntity.getAdGuid(),site.getPublisherIncId());

                    continue;
                }

                /*requesting publisher excluded but requesting site not excluded*/
                if(!siteListMap.containsKey(site.getSiteIncId())) {

                    ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are exclusion targeted by ad: {} with this site:{} not specified for this publisher id: {} ,passing it .",adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId());

                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                /*requesting publisher excluded and requesting site excluded, so look for external supply attributes*/
                if(siteListMap.containsKey(site.getSiteIncId())) {
                    List<ExternalSupplyAttributes> externalSupplyAttributesList = siteListMap.get(site.getSiteIncId());

                    if(null != externalSupplyAttributesList && externalSupplyAttributesList.size() <=0 )
                        externalSupplyAttributesList = null;

                    if(null == externalSupplyAttributesList)
                    {
                        ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are exclusion targeted by ad: {} with this site:{} specified but ext attr not specified for this publisher id: {}, this means that no external attributes are excluded, passing it .",
                                adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId());

                        shortlistedAdIdSet.add(adId);
                        continue;
                    }

                    boolean found = false;

                    for(ExternalSupplyAttributes externalSupplyAttributes : externalSupplyAttributesList)
                    {
                        if( null == externalSupplyAttributes.getInternalSupplyId())
                            continue;

                        if(externalSupplyAttributes.getInternalSupplyId().intValue() == internalIdForExternalSupplyAttributes.intValue())
                        {
                            found = true;
                            break;
                        }
                    }

                    if(found)
                    {
                        ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are exclusion targeted by ad: {} with this site:{} specified for this publisher id: {} ,and the internalId for external supply attributes: {} is contained by targeting failing it .",
                                adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId(),internalIdForExternalSupplyAttributes);

                        continue;
                    }
                    else
                    {
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                this.adNoFillReasonMapKey, context);

                        ReqLog.debugWithDebugNew(logger, request, "SupplyAttributes are exclusion targeted by ad: {} with this site:{} specified for this publisher id: {} ,and the internalId for external supply attributes: {} is not contained by targeting passing it .",
                                adEntity.getAdGuid(),site.getSiteIncId(),site.getPublisherIncId(),internalIdForExternalSupplyAttributes);

                        shortlistedAdIdSet.add(adId);
                        continue;
                    }
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
