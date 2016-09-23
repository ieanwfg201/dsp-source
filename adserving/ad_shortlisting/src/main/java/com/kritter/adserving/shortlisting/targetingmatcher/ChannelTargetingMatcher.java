package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.common.caches.channel_cache.ChannelCache;
import com.kritter.common.caches.channel_cache.entity.ChannelCacheEntity;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.targeting_profile.column.TPExt;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ChannelTargetingMatcher implements TargetingMatcher {
	private static NoFillReason noFillReason = NoFillReason.CHANNEL_MISMATCH;
	private static final String CTRL_A = String.valueOf((char)1);

	@Getter
	private String name;
	private Logger logger;
	private AdEntityCache adEntityCache;
	private String adNoFillReasonMapKey;
	private ChannelCache channelCache;	

	public ChannelTargetingMatcher(String name, String loggerName, 
			AdEntityCache adEntityCache, String adNoFillReasonMapKey, ChannelCache channelCache) {
		this.name = name;
		this.logger = LoggerFactory.getLogger(loggerName);
		this.adEntityCache = adEntityCache;
		this.adNoFillReasonMapKey = adNoFillReasonMapKey;
		this.channelCache = channelCache;
	}

	@Override
	public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
		logger.info("Inside filterAdIdsBasedOnDemandPreference of ChannelTargetingMatcher ...");
		ReqLog.requestDebug(request, "Inside filterAdIdsBasedOnDemandPreference of ChannelTargetingMatcher ...");

		if(adIdSet == null || adIdSet.size() == 0) {
			logger.debug("No ads to shortlist from. Returning!");
			return adIdSet;
		}

		Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

		if(null == request.getSite()){
			return adIdSet;
		}
		String pubGuid = request.getSite().getPublisherId();
		if( pubGuid == null){
			return adIdSet;
		}
		Integer pubIncId = request.getSite().getPublisherIncId();
		if( pubIncId == null){
			ReqLog.errorWithDebug(logger, request, "Public not found ChannelTargetingMatcher : {}" , pubIncId);
			return adIdSet;
		}
		String channelSecondLevelCode = request.getSite().getChannelSecondLevelCode();
		Integer internalSecondLevelId=null;
		if(channelSecondLevelCode !=  null && !channelSecondLevelCode.isEmpty()){
			ChannelCacheEntity channelEntity = channelCache.query(pubIncId+CTRL_A+channelSecondLevelCode);
			if(channelEntity != null){
			internalSecondLevelId = channelEntity.getInternalid();
			if(internalSecondLevelId != null){
				request.getSite().setChannelInternalId(internalSecondLevelId);
				ReqLog.debugWithDebug(logger, request, "Channel Second level found : {}" , internalSecondLevelId);
			}
			}
		}
		String channelFirstLevelCode = request.getSite().getChannelFirstLevelCode();
		Integer internalFirstLevelId=null;
		if(channelFirstLevelCode !=  null && !channelFirstLevelCode.isEmpty()){
			ChannelCacheEntity channelEntity = channelCache.query(pubIncId+CTRL_A+channelFirstLevelCode);
			if(channelEntity != null){
			internalFirstLevelId = channelEntity.getInternalid();
			if(internalFirstLevelId != null){
				request.getSite().setChannelInternalId(internalFirstLevelId);
				ReqLog.debugWithDebug(logger, request, "Channel First Level level found : {}" , internalFirstLevelId);
			}
			}
		}


		for(Integer adId : adIdSet) {
			AdEntity adEntity = adEntityCache.query(adId);
			if(null == adEntity) {
				ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}" , adId);
				continue;
			}
			TPExt tpExt = null;
			if(adEntity.getTargetingProfile() != null){
				tpExt = adEntity.getTargetingProfile().getTpExt();
			}
			boolean foundFirstTier=false;
			boolean channelTargetedFirstTier=false;
			if(tpExt != null && tpExt.getChannel_tier1() != null && tpExt.getChannel_tier1().size()>0){
				channelTargetedFirstTier=true;
				if(internalFirstLevelId != null && tpExt.getChannel_tier1().contains(internalFirstLevelId)){
					foundFirstTier=true;
				}
			}
			boolean foundSecondTier=false;
			boolean channelTargetedSecondTier=false;
			if(tpExt != null && tpExt.getChannel_tier2() != null && tpExt.getChannel_tier2().size()>0){
				channelTargetedSecondTier=true;
				if(internalSecondLevelId != null && tpExt.getChannel_tier2().contains(internalSecondLevelId)){
					foundSecondTier=true;
				}
			}
			if(channelTargetedSecondTier){
				if(!foundSecondTier && tpExt.isChannel_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				if(foundSecondTier && !tpExt.isChannel_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				if(internalSecondLevelId==null && !tpExt.isChannel_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
			}else if(channelTargetedFirstTier){
				if(!foundFirstTier && tpExt.isChannel_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				if(foundFirstTier && !tpExt.isChannel_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				if(internalFirstLevelId==null && !tpExt.isChannel_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
			}
			shortlistedAdIdSet.add(adId);
			ReqLog.debugWithDebug(logger, request, "The adid: {}, passes ChannelTargetingMatcher : ", adEntity.getAdGuid());
		}
		if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0){
			request.setNoFillReason(NoFillReason.CHANNEL_MISMATCH);
			ReqLog.debugWithDebug(logger, request, "ALL ad dropped in Channel");
		}
		return shortlistedAdIdSet;

	}
}
