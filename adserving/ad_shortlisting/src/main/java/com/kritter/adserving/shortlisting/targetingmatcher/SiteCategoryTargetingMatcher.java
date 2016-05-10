package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.site.entity.Site;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.SetUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SiteCategoryTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public SiteCategoryTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForSiteCategories of AdTargetingMatcher ...");
        ReqLog.requestDebug( request, "Inside filterAdIdsForSiteCategories of AdTargetingMatcher ...");

        Site site = request.getSite();

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache adId : {} ",adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            //if site categories targeting does not matter, move ahead.
            if(null == targetingProfile.getCategoriesExclusionList() &&
                    null == targetingProfile.getCategoriesInclusionList() )
            {
                ReqLog.debugWithDebug(logger, request, "For adid : {}, site category inc/exc does not matter.", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
                continue;
            }

            //otherwise check for both inclusion and exclusion list for the ad whichever is applicable.
            Set<Short> siteCategorySet = (null == site.getCategoriesArray() ?
                    new HashSet<Short>() :
                    new HashSet<Short>(Arrays.asList(site.getCategoriesArray())));

            if(null != targetingProfile.getCategoriesExclusionList())
            {
                ReqLog.debugWithDebug(logger, request, "For adid: {}, sitecategory excl is defined...", adEntity.getAdGuid());
                Set<Short> exclusionSet = new HashSet<Short>(
                        Arrays.asList(targetingProfile.getCategoriesExclusionList()));

                //if ad's targeting does not exclude any of the site category add it.
                Set<Short> excludedResult = SetUtils.intersectNSets(exclusionSet, siteCategorySet);

                if(null == excludedResult || excludedResult.size() == 0 )
                {
                    ReqLog.debugWithDebug(logger, request, "For adid: {}, sitecategory excl passes...", adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                }
            }
            else if(null != targetingProfile.getCategoriesInclusionList())
            {
                ReqLog.debugWithDebug(logger, request, "For adid: {}, sitecategory incl is defined...", adEntity.getAdGuid());

                Set<Short> inclusionSet = new HashSet<Short>(
                        Arrays.asList(targetingProfile.getCategoriesInclusionList()));

                //if ad's targeting matches any one category of the site categories. pass it.
                Set<Short> includedResult = SetUtils.intersectNSets(inclusionSet,siteCategorySet);

                if(null != includedResult && includedResult.size() > 0 )
                {
                    ReqLog.debugWithDebug(logger, request, "For adid: {}, sitecategory incl passes...", adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.AD_SITE_CATEGORY_INC_EXC);

        return shortlistedAdIdSet;
    }
}
