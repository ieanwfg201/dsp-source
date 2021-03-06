package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.site.entity.Site;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class SiteDomainsExclusionTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.SITE_AD_DOMAIN;

    private static final String DOT = ".";

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String adNoFillReasonMapKey;

    public SiteDomainsExclusionTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
                                                String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForSiteDomainsExclusion of AdTargetingMatcher...");
        ReqLog.requestDebugNew(request, "Inside filterAdIdsForSiteDomainsExclusion of AdTargetingMatcher...");

        Site site = request.getSite();
        boolean excludeDefinedAdDomains = site.isExcludeDefinedAdDomains();

        if(!excludeDefinedAdDomains)
        {
            ReqLog.debugWithDebugNew(logger,request, "Site does not want to exclude any domains ... passing all adids...");

            return adIdSet;
        }

        String[] excludedDomains = site.getAdDomainsToExclude();

        if(null == excludedDomains || excludedDomains.length == 0)
        {
            ReqLog.debugWithDebugNew(logger,request, "Site's excluded domains list is empty ... passing all adids...");
            return adIdSet;
        }

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebugNew(logger,request, "AdEntity not found in cache id : {}" , adId);
                continue;
            }

            String landingUrl = adEntity.getLandingUrl();

            URL url;

            try
            {
                url = new URL(landingUrl);
            }
            catch (MalformedURLException mue)
            {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);

                ReqLog.errorWithDebugNew(logger,request, "The landing url is not well formed,skipping ad unit {}" , adEntity.getAdGuid());
                continue;
            }

            String adDomain = url.getHost();

            boolean isAdAllowed = true;

            for(String blockedDomain : excludedDomains)
            {
                //just in case if ad-domain is sub domain, and defined
                //blocked domain is top level domain.
                if(
                    blockedDomain.equalsIgnoreCase(adDomain) ||
                    blockedDomain.contains(adDomain)
                  )
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    ReqLog.debugWithDebugNew(logger,request, "Addomain is excluded by site ... ,ad-domain {}, blocked domain by site is {}",adDomain,  blockedDomain);
                    isAdAllowed = false;
                    break;
                }
            }

            if(isAdAllowed)
                shortlistedAdIdSet.add(adId);
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
