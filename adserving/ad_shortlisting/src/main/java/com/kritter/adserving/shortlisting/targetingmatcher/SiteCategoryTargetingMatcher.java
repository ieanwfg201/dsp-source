package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.site.entity.Site;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.SetUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SiteCategoryTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_SITE_CATEGORY_INC_EXC;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public SiteCategoryTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                        String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForSiteCategories of AdTargetingMatcher ...");
        ReqLog.requestDebugNew( request, "Inside filterAdIdsForSiteCategories of AdTargetingMatcher ...");

        Site site = request.getSite();

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache adId : {} ",adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            //if site categories targeting does not matter, move ahead.
            if(null == targetingProfile.getCategoriesExclusionList() &&
                    null == targetingProfile.getCategoriesInclusionList() )
            {
                ReqLog.debugWithDebugNew(logger, request, "For adid : {}, site category inc/exc does not matter.", adEntity.getAdGuid());
                shortlistedAdIdSet.add(adId);
                continue;
            }

            //otherwise check for both inclusion and exclusion list for the ad whichever is applicable.
            Set<Short> siteCategorySet = (null == site.getCategoriesArray() ?
                    new HashSet<Short>() :
                    new HashSet<Short>(Arrays.asList(site.getCategoriesArray())));

            if(null != targetingProfile.getCategoriesExclusionList())
            {
                ReqLog.debugWithDebugNew(logger, request, "For adid: {}, sitecategory excl is defined...", adEntity.getAdGuid());
                Set<Short> exclusionSet = new HashSet<Short>(
                        Arrays.asList(targetingProfile.getCategoriesExclusionList()));

                //if ad's targeting does not exclude any of the site category add it.
                Set<Short> excludedResult = SetUtils.intersectNSets(exclusionSet, siteCategorySet);

                if(null == excludedResult || excludedResult.size() == 0 )
                {
                    ReqLog.debugWithDebugNew(logger, request, "For adid: {}, sitecategory excl passes...", adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);
                }
            }
            else if(null != targetingProfile.getCategoriesInclusionList())
            {
                ReqLog.debugWithDebugNew(logger, request, "For adid: {}, sitecategory incl is defined...", adEntity.getAdGuid());

                Set<Short> inclusionSet = new HashSet<Short>(
                        Arrays.asList(targetingProfile.getCategoriesInclusionList()));

                //if ad's targeting matches any one category of the site categories. pass it.
                Set<Short> includedResult = SetUtils.intersectNSets(inclusionSet,siteCategorySet);

                if(null != includedResult && includedResult.size() > 0 )
                {
                    ReqLog.debugWithDebugNew(logger, request, "For adid: {}, sitecategory incl passes...", adEntity.getAdGuid());
                    shortlistedAdIdSet.add(adId);
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
