package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.common.caches.adposition_cache.AdPositionCache;
import com.kritter.common.caches.adposition_cache.entity.AdPositionCacheEntity;
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

public class AdPositionTargetingMatcher implements TargetingMatcher {
	private static NoFillReason noFillReason = NoFillReason.ADPOSITION_MISMATCH;
	private static final String CTRL_A = String.valueOf((char)1);

	@Getter
	private String name;
	private Logger logger;
	private AdEntityCache adEntityCache;
	private String adNoFillReasonMapKey;
	private AdPositionCache adPositionCache;	

	public AdPositionTargetingMatcher(String name, String loggerName, 
			AdEntityCache adEntityCache, String adNoFillReasonMapKey, AdPositionCache adPositionCache) {
		this.name = name;
		this.logger = LoggerFactory.getLogger(loggerName);
		this.adEntityCache = adEntityCache;
		this.adNoFillReasonMapKey = adNoFillReasonMapKey;
		this.adPositionCache = adPositionCache;
	}

	@Override
	public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
		logger.info("Inside filterAdIdsBasedOnDemandPreference of AdPositionTargetingMatcher ...");
		ReqLog.requestDebugNew(request, "Inside filterAdIdsBasedOnDemandPreference of AdPositionTargetingMatcher ...");

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
			ReqLog.errorWithDebugNew(logger, request, "Public not found AdPositionTargetingMatcher : {}" , pubIncId);
			return adIdSet;
		}
		/**/

		String adPosition = request.getSite().getAdPosition();
		
		AdPositionCacheEntity adPositionEntity = adPositionCache.query(pubIncId+CTRL_A+adPosition);
		Integer adPositionUiId = null;
		if(adPositionEntity != null){
			adPositionUiId = adPositionEntity.getInternalid();
		}
		request.getSite().setAdPositionUiId(adPositionUiId);
		for(Integer adId : adIdSet) {
			AdEntity adEntity = adEntityCache.query(adId);
			if(null == adEntity) {
				ReqLog.errorWithDebugNew(logger, request, "AdEntity not found in cache id : {}" , adId);
				continue;
			}
			TPExt tpExt = null;
			if(adEntity.getTargetingProfile() != null){
				tpExt = adEntity.getTargetingProfile().getTpExt();
			}
			if(tpExt != null && tpExt.getAdposids() != null && tpExt.getAdposids().size()>0){
				if(adPositionUiId==null && tpExt.isAdposids_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				if(!tpExt.getAdposids().contains(adPositionUiId) && tpExt.isAdposids_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				if(tpExt.getAdposids().contains(adPositionUiId) && !tpExt.isAdposids_inc()){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
			}
			shortlistedAdIdSet.add(adId);
			ReqLog.debugWithDebugNew(logger, request, "The adid: {}, passes AdPositionTargetingMatcher : ", adEntity.getAdGuid());
		}
		if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
			request.setNoFillReason(NoFillReason.ADPOSITION_MISMATCH);
		return shortlistedAdIdSet;
	}
}
