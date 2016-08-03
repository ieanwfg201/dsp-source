package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
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
import java.util.Set;

public class DemandPreferenceFilter implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.ONLY_DIRECTthenDSP_DP;

    @Getter
    private String name;
    private Logger logger;
    private AdEntityCache adEntityCache;
    private AccountCache accountCache;
    private String adNoFillReasonMapKey;

    public DemandPreferenceFilter(String name, String loggerName, 
            AdEntityCache adEntityCache, AccountCache accountCache, String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.accountCache = accountCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsBasedOnDemandPreference of AdTargetingMatcher ...");
        ReqLog.requestDebug(request, "Inside filterAdIdsBasedOnDemandPreference of AdTargetingMatcher ...");

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();
        
        if(null == request.getSite()){
            return adIdSet;
        }
        String pubGuid = request.getSite().getPublisherId();
        if( pubGuid == null){
            return adIdSet;
        }
        AccountEntity accountEntity = this.accountCache.query(request.getSite().getPublisherId());
        if(accountEntity == null){
            return adIdSet;
        }
        DemandPreference demandPreference = request.getSite().getDemandPreference();
        if(null == demandPreference)
            demandPreference = accountEntity.getDemandPreference();

        if(demandPreference == DemandPreference.OnlyMediation){
            int demandTypeCode = DemandType.API.getCode();
            for(Integer adId : adIdSet) {
                AdEntity adEntity = adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}" , adId);
                    continue;
                }
                if( demandTypeCode == adEntity.getDemandtype()){
                    shortlistedAdIdSet.add(adId);
                    ReqLog.debugWithDebug(logger, request, "The adid: {}, passes only mediation preference : ", adEntity.getAdGuid());
                    
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    if(request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("Dropping adId: ");
                        request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" , since it does not pass only mediation " +
                                "demand preference.");
                    }
                }
            }
            if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
                request.setNoFillReason(NoFillReason.ONLY_MEDIATION_DP);
            return shortlistedAdIdSet;
        }else if(demandPreference == DemandPreference.OnlyDSP){
            int demandTypeCode = DemandType.DSP.getCode();
            for(Integer adId : adIdSet){
                AdEntity adEntity = adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}" , adId);
                    continue;
                }
                if( demandTypeCode == adEntity.getDemandtype()) {
                    shortlistedAdIdSet.add(adId);
                    ReqLog.debugWithDebug(logger, request, "The adid: {}, passes only dsp preference : ", adEntity.getAdGuid());
                    
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    if (request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("Dropping adId: ");
                        request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" , since it does not pass demand preference.");
                    }
                }
            }
            
            if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
                request.setNoFillReason(NoFillReason.ONLY_DSP_DP);
            return shortlistedAdIdSet;
        }else if(demandPreference == DemandPreference.DIRECT){
            int demandTypeCode = DemandType.DIRECT.getCode();
            for(Integer adId : adIdSet){
                AdEntity adEntity = adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}" , adId);
                    continue;
                }
                if( demandTypeCode == adEntity.getDemandtype()){
                    shortlistedAdIdSet.add(adId);
                    ReqLog.debugWithDebug(logger, request, "The adid: {}, passes only direct preference : ", adEntity.getAdGuid());
                    
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    if (request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("Dropping adId: ");
                        request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" , since it does not pass demand preference.");
                    }
                }
            }
            
            if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
                request.setNoFillReason(NoFillReason.ONLY_DIRECT_DP);
            return shortlistedAdIdSet;
        }else if(demandPreference == DemandPreference.DIRECTthenMediation){
            int demandTypeCode = DemandType.DIRECT.getCode();
            int demandTypeCode1 = DemandType.API.getCode();
            for(Integer adId : adIdSet){
                AdEntity adEntity = adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : " + adId);
                    continue;
                }
                if( demandTypeCode == adEntity.getDemandtype() || demandTypeCode1 == adEntity.getDemandtype()){
                    shortlistedAdIdSet.add(adId);
                    ReqLog.debugWithDebug(logger, request, "The adid: {}, passes only direct Then Mediation " +
                            "preference : ", adEntity.getAdGuid());
                    
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    if (request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("Dropping adId: ");
                        request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" , since it does not pass demand preference.");
                    }
                }
            }
            
            if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
                request.setNoFillReason(NoFillReason.ONLY_DIRECTthenMed_DP);
            return shortlistedAdIdSet;
        }else if(demandPreference == DemandPreference.DirectThenDSP){
            int demandTypeCode = DemandType.DIRECT.getCode();
            int demandTypeCode1 = DemandType.DSP.getCode();
            for(Integer adId : adIdSet){
                AdEntity adEntity = adEntityCache.query(adId);

                if(null == adEntity)
                {
                    ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}" , adId);
                    continue;
                }
                if( demandTypeCode == adEntity.getDemandtype() || demandTypeCode1 == adEntity.getDemandtype()){
                    shortlistedAdIdSet.add(adId);
                    ReqLog.debugWithDebug(logger, request, "The adid: {}, passes only direct than dsp preference : ", adEntity.getAdGuid());
                    
                } else {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);

                    if(request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("Dropping adId: ");
                        request.addDebugMessageForTestRequest(adEntity.getAdGuid());
                        request.addDebugMessageForTestRequest(" , since it does not pass demand preference.");
                    }
                }
            }
            
            if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
                request.setNoFillReason(NoFillReason.ONLY_DIRECTthenDSP_DP);
            return shortlistedAdIdSet;
        }else{
            return adIdSet;
        }

    }
}
