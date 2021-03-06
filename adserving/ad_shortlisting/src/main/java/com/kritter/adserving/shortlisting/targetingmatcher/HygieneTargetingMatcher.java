package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.HygieneCategory;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.SetUtils;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class HygieneTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AD_SITE_HYGIENE;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public HygieneTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                   String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside matchUpHygieneCriteriaBetweenAdAndSite of AdTargetingMatcher...");

        ReqLog.requestDebugNew(request, "Inside matchUpHygieneCriteriaBetweenAdAndSite of AdTargetingMatcher...");

        Site site = request.getSite();

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache id : " + adId);
                continue;
            }

            boolean matchFound = false;

            if(null == site.getHygieneList() || null == adEntity.getHygieneArray())
            {
                ReqLog.errorWithDebugNew(logger,request,"Site id:{} or adguid: {} , do not have hygiene lists defined, no connection can be made...skipping ad..." , 
                        site.getSiteGuid() , adEntity.getAdGuid() );

                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);

                if(request.isRequestForSystemDebugging())
                {
                    request.addDebugMessageForTestRequest("Site id: ");
                    request.addDebugMessageForTestRequest(site.getSiteGuid());
                    request.addDebugMessageForTestRequest(" or adid: ");
                    request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                    request.addDebugMessageForTestRequest(" ,do not have hygiene lists defined, no connection can " +
                            "be made...skipping ad...");
                }

                continue;
            }

            Set<HygieneCategory> siteHygieneSet = null;
            if(null != site.getOptInHygieneList())
                siteHygieneSet = HygieneCategory.fetchHygieneCategoryInstance(site.getOptInHygieneList());

            Set<HygieneCategory> adHygieneSet = null;
            if(null != adEntity.getHygieneArray())
                adHygieneSet = HygieneCategory.fetchHygieneCategoryInstance(adEntity.getHygieneArray());

            //use default site's hygiene in case opt in hygiene is not set.
            if(null == siteHygieneSet || siteHygieneSet.size() == 0)
                siteHygieneSet = HygieneCategory.fetchHygieneCategoryInstance(site.getHygieneList());

            if(
                    canSourcePageHygieneOptForAdHygiene
                            (
                                    siteHygieneSet,
                                    adHygieneSet
                            )
                    )
            {
                shortlistedAdIdSet.add(adId);
                matchFound = true;
            }

            if(matchFound)
            {
                ReqLog.debugWithDebugNew(logger,request,"Hygiene match found for site: {} and ad: {}", site.getSiteGuid(), adEntity.getAdGuid());
            }
            else
            {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);

                ReqLog.debugWithDebugNew(logger,request,"Hygiene match not found for site: {} and ad: {}", site.getSiteGuid(), adEntity.getAdGuid());
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }

    private boolean canSourcePageHygieneOptForAdHygiene(Set<HygieneCategory> sourcePageOptInHygiene,
                                                        Set<HygieneCategory> adHygiene)
    {
        //if hygienes of any of supply or demand is unspecified then match them.
        if(null == sourcePageOptInHygiene     || null == adHygiene ||
                sourcePageOptInHygiene.size() == 0 || adHygiene.size() == 0)
            return false;

        if(adHygiene.contains(HygieneCategory.MATURE) && !sourcePageOptInHygiene.contains(HygieneCategory.MATURE))
            return false;

        if(adHygiene.contains(HygieneCategory.MATURE) && sourcePageOptInHygiene.contains(HygieneCategory.MATURE))
            return true;

        Set<HygieneCategory> result = SetUtils.intersectSets(sourcePageOptInHygiene, adHygiene);

        if(null != result && result.size() > 0 )
            return true;

        return false;
    }
}
