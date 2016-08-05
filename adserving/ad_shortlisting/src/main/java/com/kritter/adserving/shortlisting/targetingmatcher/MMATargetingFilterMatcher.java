package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.ad_ext.AdExt;
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

public class MMATargetingFilterMatcher implements TargetingMatcher {
	private static NoFillReason noFillReason = NoFillReason.MMA_MISMATCH;

	@Getter
	private String name;
	private Logger logger;
	private AdEntityCache adEntityCache;
	private String adNoFillReasonMapKey;

	public MMATargetingFilterMatcher(String name, String loggerName, 
			AdEntityCache adEntityCache, String adNoFillReasonMapKey) {
		this.name = name;
		this.logger = LoggerFactory.getLogger(loggerName);
		this.adEntityCache = adEntityCache;
		this.adNoFillReasonMapKey = adNoFillReasonMapKey;
	}

	@Override
	public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
		logger.info("Inside filterAdIdsBasedOnDemandPreference of MMATargetingFilterMatcher ...");
		ReqLog.requestDebug(request, "Inside filterAdIdsBasedOnDemandPreference of MMATargetingFilterMatcher ...");

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
			ReqLog.errorWithDebug(logger, request, "Public not found MMATargetingFilterMatcher : {}" , pubIncId);
			return adIdSet;
		}
		/**/

		Integer[] mmaCatgories = request.getSite().getMmaCatgories();
		boolean mmaCatgoriesExclude = request.getSite().isMmaCatgoriesExclude();
		Integer[] mmaCatgoriesForExcInc = request.getSite().getMmaCatgoriesForExcInc();
		Integer[] mmaindustryCode = request.getSite().getMmaindustryCode();
		boolean mmaindustrCodeExclude = request.getSite().isMmaCatgoriesExclude();
		Integer[] mmaindustrCodeExcInc = request.getSite().getMmaindustryCode();

		for(Integer adId : adIdSet) {
			AdEntity adEntity = adEntityCache.query(adId);
			if(null == adEntity) {
				ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}" , adId);
				continue;
			}
			AdExt adExt = adEntity.getAdExt();
			TPExt tpExt = null;
			if(adEntity.getTargetingProfile() != null){
				tpExt = adEntity.getTargetingProfile().getTpExt();
			}
			
			HashSet<Integer> admma_tier1Filter = null;
			HashSet<Integer> admma_tier2Filter = null;
			if(adExt != null){
				admma_tier1Filter = adExt.getMma_tier1();
				admma_tier2Filter = adExt.getMma_tier2();;
			}
			boolean mma_cat_Exclude = true;
			HashSet<Integer> mma_tier1targeting = null;
				HashSet<Integer> mma_tier2targeting = null;
			if(tpExt != null){
				mma_cat_Exclude = !tpExt.isInc();
				mma_tier1targeting = tpExt.getMma_tier1();
				mma_tier2targeting = tpExt.getMma_tier2();
			}

			/** Apply supply side industry code inclusion exclusion filter */
			if(mmaindustrCodeExcInc != null && mmaindustrCodeExcInc.length>0){
				if((admma_tier1Filter == null || admma_tier1Filter.size()<1) &&  (admma_tier2Filter == null || admma_tier2Filter.size()<1)){
				}else{
					boolean breakBool =false;
					boolean inclusionFound =false;
					for(Integer i:mmaindustrCodeExcInc){
						boolean present =false;
						if(admma_tier1Filter != null && admma_tier1Filter.contains(i)){
							present=true;
							inclusionFound = true;
						}else if(admma_tier2Filter != null && admma_tier2Filter.contains(i)){
							present=true;
							inclusionFound = true;
						}
						if(present && mmaindustrCodeExclude){
							AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
									this.adNoFillReasonMapKey, context);
							breakBool=true;
							break;
						}
					}
					if(breakBool){
						continue;
					}
					if(!mmaindustrCodeExclude && inclusionFound){
						AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
								this.adNoFillReasonMapKey, context);
						continue;
					}
				}
			}
			/** Apply mma category targeting */
			if((mma_tier1targeting != null && mma_tier1targeting.size()>0)||
					(mma_tier2targeting != null && mma_tier2targeting.size()>0)){
				if(mmaCatgories == null || mmaCatgories.length<1){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
				boolean breakBool =false;
				boolean inclusionFound =false;
				for(Integer i:mmaCatgories){
					boolean present =false;
					if(mma_tier1targeting != null && mma_tier1targeting.contains(i)){
						present=true;
						inclusionFound = true;
					}else if(mma_tier2targeting != null && mma_tier2targeting.contains(i)){
						present=true;
						inclusionFound = true;
					}
					if(present && mma_cat_Exclude){
						AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
								this.adNoFillReasonMapKey, context);
						breakBool=true;
						break;
					}
				}
				if(breakBool){
					continue;
				}
				if(!mma_cat_Exclude && inclusionFound){
					AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
							this.adNoFillReasonMapKey, context);
					continue;
				}
			}
			shortlistedAdIdSet.add(adId);
			
			ReqLog.debugWithDebug(logger, request, "The adid: {}, passes MMATargetingFilterMatcher : ", adEntity.getAdGuid());
			//AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
			//	this.adNoFillReasonMapKey, context);
		}
		if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
			request.setNoFillReason(NoFillReason.MMA_MISMATCH);
		return shortlistedAdIdSet;
	}
}
