package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.site.entity.Site;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.SetUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AdCategoryTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public AdCategoryTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForAdCategories of AdTargetingMatcher ...");
        ReqLog.requestDebug(request, "Inside filterAdIdsForAdCategories of AdTargetingMatcher ...");

        if(adIdSet == null || adIdSet.size() == 0) {
            logger.debug("No ads to shortlist from. Returning!");
            return adIdSet;
        }

        Site site = request.getSite();
        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            //if no inc/exc categories specified by site then move on.
            if(null == site.getCategoriesArrayForInclusionExclusion() ||
                    site.getCategoriesArrayForInclusionExclusion().length == 0)
            {
                ReqLog.debugWithDebug(logger, request, "Site {} didnt specify any ad categories to inc/exc, passing adid {}", site.getSiteGuid(), adEntity.getAdGuid());

                shortlistedAdIdSet.add(adId);
                continue;
            }

            Set<Short> contentCategoriesDefinedBySite = (null == site.getCategoriesArrayForInclusionExclusion() ?
                    new HashSet<Short>() :
                    new HashSet<Short>(
                            Arrays.asList(site.
                                    getCategoriesArrayForInclusionExclusion()))
            );

            Set<Short> adEntityCategorySet = (null == adEntity.getCategoriesArray() ?
                    new HashSet<Short>():
                    new HashSet<Short>(Arrays.asList(adEntity.getCategoriesArray())));

            Set<Short> result = SetUtils.intersectNSets(adEntityCategorySet,
                    contentCategoriesDefinedBySite);

            if(site.isCategoryListExcluded())
            {
                if(null != result && result.size() > 0)
                {
                    ReqLog.debugWithDebug(logger, request, "Site {} ,specifies exclusion category list, failing adId: {}", site.getSiteGuid(),adEntity.getAdGuid());
                    continue;
                }
                else
                {
                    ReqLog.debugWithDebug(logger, request, "Site {} specifies exclusion category list, passing adid {}", site.getSiteGuid(), adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                }
            }
            else
            {
                if(null != result && result.size() > 0)
                {
                    ReqLog.debugWithDebug(logger, request, "Site {} specifies inclusion category list, passing adid {}", site.getSiteGuid(), adEntity.getAdGuid());

                    shortlistedAdIdSet.add(adId);
                }
                else
                {
                    
                    ReqLog.requestDebug(request, "Site: "+site.getSiteGuid()+" ,specifies inclusion category list, failing adId: "+adEntity.getAdGuid());
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.SITE_AD_CATEGORY_INC_EXC);

        return shortlistedAdIdSet;
    }
}
