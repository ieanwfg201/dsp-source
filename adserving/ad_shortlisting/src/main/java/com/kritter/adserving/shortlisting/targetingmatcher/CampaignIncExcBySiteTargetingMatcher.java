package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.DemandPreference;
import com.kritter.constants.DemandType;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CampaignIncExcBySiteTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.SITE_INC_EXC_ADV_CMPGN;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private AccountCache account_cache;
    private String adNoFillReasonMapKey;

    public CampaignIncExcBySiteTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache,
            AccountCache account_cache, String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.account_cache = account_cache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsForCampaignInclusionExclusionBySite of AdTargetingMatcher.Site specifies exclusion: {} ",
                request.getSite().isAdvertiserIdListExcluded());

        ReqLog.requestDebug(request, "Inside filterAdIdsForCampaignInclusionExclusionBySite of AdTargetingMatcher...");

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();
        Site site = request.getSite();

        Map<Integer,Set<Integer>> campaignInclusionExclusionSchemaMap = site.getCampaignInclusionExclusionSchemaMap();

        //if no advertiser specified for inc/exc then just pass all adids.
        if(null == campaignInclusionExclusionSchemaMap || campaignInclusionExclusionSchemaMap.size() == 0)
        {
            ReqLog.debugWithDebug(logger,request, "CampaignInclusionExclusion does not matter for site {}   passing all adids...",
                    site.getSiteGuid()  );

            return adIdSet;
        }
        AccountEntity accountEntity = account_cache.query(request.getSite().getPublisherId());
        //if campaign inclusion exclusion specified by the site then check for each corresponding advertiser.
        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger,request, "AdEntity not found in cache id : {} " , adId);
                continue;
            }

            Integer advertiserId = adEntity.getAccountId();
            Integer campaignId = adEntity.getCampaignIncId();

            Set<Integer> campaignIdList = campaignInclusionExclusionSchemaMap.get(advertiserId);

            if(null != campaignIdList && campaignIdList.size() <= 0)
                campaignIdList = null;

            DemandPreference demandPreference = request.getSite().getDemandPreference();
            if(null == demandPreference && null != accountEntity)
                demandPreference = accountEntity.getDemandPreference();

            if(adEntity.getDemandtype() == DemandType.API.getCode() && accountEntity != null 
                    && (demandPreference == DemandPreference.DIRECTthenMediation
                    || demandPreference == DemandPreference.OnlyMediation)){
                ReqLog.requestDebug(request, "Demand Type API. Including "+advertiserId);
                shortlistedAdIdSet.add(adId);
                continue;
            }
            if(adEntity.getDemandtype() == DemandType.DSP.getCode() && accountEntity != null 
                    && (demandPreference == DemandPreference.DirectThenDSP
                            || demandPreference == DemandPreference.OnlyDSP)){
                ReqLog.requestDebug(request, "Demand Type DSP. Including "+advertiserId);
                shortlistedAdIdSet.add(adId);
                continue;
            }

            /*if advertiser and campaign id list included*/
            if(!site.isAdvertiserIdListExcluded())
            {
                /*advertiser not included.*/
                if(!campaignInclusionExclusionSchemaMap.containsKey(advertiserId))
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion inclusion specified with advertiserId: {} not contained in targeting.Failing",advertiserId);

                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    continue;
                }

                /*map contains advertiser id to include but no campaign list specified, means all included.*/
                if(campaignInclusionExclusionSchemaMap.containsKey(advertiserId) && null == campaignIdList)
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion inclusion specified with advertiserId: {} contained and no campaigns specified .Passing",advertiserId);

                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                /*if advertiser included and campaign list specified but this ad's campaign id not included, fail*/
                if(campaignInclusionExclusionSchemaMap.containsKey(advertiserId) &&
                        null != campaignIdList && !campaignIdList.contains(campaignId))
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion inclusion specified with advertiserId: {} contained and campaign {} not included .Failing",
                            advertiserId,campaignId);

                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    continue;
                }

                /*if advertiser included and campaign included as well.*/
                if(campaignInclusionExclusionSchemaMap.containsKey(advertiserId) &&
                        null != campaignIdList && campaignIdList.contains(campaignId))
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion inclusion specified with advertiserId: {} contained and campaign {} included .Passing",
                            advertiserId,campaignId);
                    shortlistedAdIdSet.add(adId);
                    continue;
                }
            }
            else if(site.isAdvertiserIdListExcluded())
            {
                /*if advertiser not excluded.*/
                if(!campaignInclusionExclusionSchemaMap.containsKey(advertiserId))
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion exclusion specified with advertiserId: {} not contained .Passing",
                            advertiserId);
                    shortlistedAdIdSet.add(adId);
                    continue;
                }

                /*if advertiser excluded with no campaigns listed then all campaigns are excluded*/
                if(campaignInclusionExclusionSchemaMap.containsKey(advertiserId) && null == campaignIdList)
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion exclusion specified with advertiserId: {} contained and campaign list not specified, means all excluded.Failing",
                            advertiserId);
                    continue;
                }

                /*if advertiser excluded with campaigns specified and this campaign excluded*/
                if(campaignInclusionExclusionSchemaMap.containsKey(advertiserId) && null != campaignIdList && campaignIdList.contains(campaignId))
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion exclusion specified with advertiserId: {} contained and campaign {} included .Failing",
                            advertiserId,campaignId);

                    continue;
                }

                /*if advertiser excluded with campaigns specified and this campaign not excluded*/
                if(campaignInclusionExclusionSchemaMap.containsKey(advertiserId) && null != campaignIdList && !campaignIdList.contains(campaignId))
                {
                    ReqLog.debugWithDebug(logger,request, "CampaignAdvertiserInclusionExclusion exclusion specified with advertiserId: {} contained and campaign {} not included .Passing",
                            advertiserId,campaignId);

                    shortlistedAdIdSet.add(adId);
                    continue;
                }
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}
