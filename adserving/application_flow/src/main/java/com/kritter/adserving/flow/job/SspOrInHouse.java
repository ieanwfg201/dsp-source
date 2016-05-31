package com.kritter.adserving.flow.job;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.ssp_rules.SspGlobalRulesCache;
import com.kritter.common.caches.ssp_rules.entity.SspGlobalRulesEntity;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.DemandPreference;
import com.kritter.constants.DemandType;
import com.kritter.constants.SSPEnum;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.dpa.common.DemandPartnerApi;
import com.kritter.dpa.common.entity.DemandPartnerApiResponse;
import com.kritter.entity.ssp_rules.SSPGlobalRuleDef;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.index.AdGuidIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This job fetches ads from external Demand Partners or DSP's configured against the requesting
 * site or corresponding publisher or at a global level. Look for the priority in selection of these ads.
 */
public class SspOrInHouse implements Job
{
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private String ruleSetVariableNameForKritterWorkflow;
    private Map<String,DemandPartnerApi> demandPartnerApiMap;
    private SspGlobalRulesCache sspGlobalRulesCache;
    private AdEntityCache adEntityCache;
    private CreativeCache creativeCache;
    private AccountCache accountCache;

    public SspOrInHouse(
            String loggerName,
            String jobName,
            String requestObjectKey,
            String responseObjectKey,
            String ruleSetVariableNameForKritterWorkflow,
            Map<String,DemandPartnerApi> demandPartnerApiMap,
            SspGlobalRulesCache sspGlobalRulesCache,
            AdEntityCache adEntityCache,
            CreativeCache creativeCache,
            AccountCache accountCache
    )
    {

        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.ruleSetVariableNameForKritterWorkflow = ruleSetVariableNameForKritterWorkflow;
        this.demandPartnerApiMap = demandPartnerApiMap;
        this.sspGlobalRulesCache = sspGlobalRulesCache;
        this.adEntityCache = adEntityCache;
        this.creativeCache = creativeCache;
        this.accountCache = accountCache;
    }

    @Override
    public String getName()
    {
        return this.jobName;
    }
    private int defaultToDirectWithError(String msg, Context context, Response response){
        logger.error(msg);
        context.setValue(this.ruleSetVariableNameForKritterWorkflow,1);
        if(response != null){
            response.setResponseAdInfo(new HashSet<ResponseAdInfo>());
            response.setShortlistedAdIdSet(new HashSet<String>());
        }

        return 1;
    }
    private int defaultToDirectWithDebug(String msg, Context context,Response response){
        logger.error(msg);
        context.setValue(this.ruleSetVariableNameForKritterWorkflow,2);
        if(response != null){
            response.setResponseAdInfo(new HashSet<ResponseAdInfo>());
            response.setShortlistedAdIdSet(new HashSet<String>());
        }
        return 2;
    }
    private int fetchAdsFromDemandPartnerApi(String demandPartnerAccountId, Map<String, Set<String>> advShortlistedAd,
                                             Request request,Response response, int timeoutInMilis){
        if(demandPartnerAccountId == null || advShortlistedAd == null){
            return -1;
        }
        Set<String> adSet = advShortlistedAd.get(demandPartnerAccountId);
        ReqLog.requestDebug(request, "Inside SSPOrInHouse:fetchAdsFromDemandPartnerApi fetching ad for "+demandPartnerAccountId);
        if(adSet== null){
            ReqLog.requestDebug(request, "Inside SSPOrInHouse:fetchAdsFromDemandPartnerApi adSet not Found for "+demandPartnerAccountId);
            return -1;
        }
        for(String adId : adSet){
            DemandPartnerApi demandPartnerApi = demandPartnerApiMap.get(demandPartnerAccountId);
            demandPartnerApi.setReadTimeOut(timeoutInMilis);
            demandPartnerApi.setConnectTimeOut(timeoutInMilis);
            if(null != demandPartnerApi){
                logger.debug("DemandPartnerApi found for accountId: {} ,calling external network to fetch ads...", demandPartnerAccountId);
                DemandPartnerApiResponse demandPartnerApiResponse =
                        demandPartnerApi.fetchDemandPartnerApiResponse(request);

                //in case we get a fill from the demand partner, skip further workflow here
                //and set appropriate variable for response formatting.
                if(demandPartnerApiResponse != null && !demandPartnerApiResponse.isNoFill() &&
                        demandPartnerApiResponse.getResponseStatusCode() == 200  &&
                        null != demandPartnerApiResponse.getResponsePayload()
                        ){
                    AdEntity adEntity = null;
                    try{
                        Set<Integer> adIncIdSet = this.adEntityCache.query(new AdGuidIndex(adId));
                        if(null != adIncIdSet && adIncIdSet.size() > 0){
                            for(Integer adIncId : adIncIdSet){
                                adEntity = this.adEntityCache.query(adIncId);
                                break;
                            }
                        }
                    }catch (UnSupportedOperationException unsoe){
                        logger.error("UnSupportedOperationException inside SspOrInHouse, skipping adId: {} ", adId);
                    }

                    if(null == adEntity){
                        logger.error("AdEntity with id: {} could not be found inside SspOrInHouse",adId);
                        continue;
                    }

                    Creative creative = this.creativeCache.query(adEntity.getCreativeId());
                    if(null == creative){
                        logger.error("AdEntity with id: {} does not have any creative with id : {}  associated to it inside SspOrInHouse",adId,
                                adEntity.getCreativeGuid());
                        continue;
                    }
                    /*prepare a new richmedia creative for response writing*/
                    Creative creativeToUse =
                            new Creative.CreativeEntityBuilder(
                                    creative.getCreativeIncId(),
                                    creative.getCreativeGuid(),
                                    creative.getAccountGuid(),
                                    CreativeFormat.RICHMEDIA,
                                    creative.getCreativeAttributes(),
                                    false,
                                    System.currentTimeMillis(),
                                    null, null, null
                            )
                                    .setHtmlContent(demandPartnerApiResponse.getResponsePayload())
                                    .setText(null)
                                    .setBannerUriIds(null)
                                    .setExternalResourceURL(null)
                                    .build();

                    ReqLog.debugWithDebug(logger, request, "DemandPartner Response, status: {} , no-fill: {} ,payload: {} ",
                                demandPartnerApiResponse.getResponseStatusCode(),
                                demandPartnerApiResponse.isNoFill(),
                                demandPartnerApiResponse.getResponsePayload()
                        );
                    ResponseAdInfo responseAdInfo = response.getResponseAdInfoAgainstAdGuidMap().get(adId);
                    responseAdInfo.setCreative(creativeToUse);
                    responseAdInfo.setRichMediaAdIsCompatibleForAdserving(true);

                    //reset set of response adinfo to use in dpa workflow for formatting.
                    Set<ResponseAdInfo> dpaResponseAdInfoSet = new HashSet<ResponseAdInfo>();
                    dpaResponseAdInfoSet.add(responseAdInfo);
                    response.setResponseAdInfo(dpaResponseAdInfoSet);
                    return 0;
                }
            }else{
                ReqLog.requestDebug(request, "Inside SSPOrInHouse:fetchAdsFromDemandPartnerApi demandPartnerApi not found for "+demandPartnerAccountId);
            }
        }
        return -1;
    }
    @Override
    public void execute(Context context)
    {
        logger.info("Inside SspOrInHouse job , will use demand partner ruleset defined for requesting site...");
        Request request = (Request)context.getValue(this.requestObjectKey);
        Response response = (Response)context.getValue(this.responseObjectKey);
        int takeFlowToKritterDemand = 1;
        if(null == request || null == response) {
            takeFlowToKritterDemand = defaultToDirectWithError("Either request or response is null inside SspOrInHouse," +
                    "skipping workflow, going to kritter's default workflow ... ", context, null);
            return;
        }

        Set<String> shortlistedAdIdSetForRequest = response.getShortlistedAdIdSet();
        if(
                null == response.getResponseAdInfo() || response.getResponseAdInfo().size() <= 0 ||
                        null == shortlistedAdIdSetForRequest || shortlistedAdIdSetForRequest.size() <= 0
                ) {
            takeFlowToKritterDemand = defaultToDirectWithDebug("No ads available inside SspOrInHouse", context, null);
            return;
        }

        if(null == request.getSite()){
            takeFlowToKritterDemand= defaultToDirectWithError("Site is unavailable inside SspOrInHouse,aborting !", context,null);
            return;
        }
        String pubGuid = request.getSite().getPublisherId();
        if( pubGuid == null){
            takeFlowToKritterDemand = defaultToDirectWithError("PubId is unavailable inside SspOrInHouse", context,null);
            return;
        }
        AccountEntity accountEntity = this.accountCache.query(request.getSite().getPublisherId());
        if(accountEntity == null){
            takeFlowToKritterDemand = defaultToDirectWithError("PubId is unavailable inside accountCache", context,null);
            return;
        }

         /*changed to accomodate demand preference at site level*/
        DemandPreference demandPreference = request.getSite().getDemandPreference();
        if(null == demandPreference)
            demandPreference = accountEntity.getDemandPreference();

        if(demandPreference == DemandPreference.OnlyDSP){
            context.setValue(this.ruleSetVariableNameForKritterWorkflow,3);
            return;
        }
        if(demandPreference == DemandPreference.DIRECT || demandPreference == DemandPreference.DIRECTthenMediation || 
                demandPreference == DemandPreference.OnlyMediation || demandPreference == DemandPreference.DirectThenDSP ){
            Set<ResponseAdInfo> responseAdInfoSet = response.getResponseAdInfo();
            Set<ResponseAdInfo> directresponseAdnfo = new HashSet<ResponseAdInfo>();
            Set<ResponseAdInfo> sspdspresponseAdnfo = new HashSet<ResponseAdInfo>();
            Set<String> directShortlistedAd = new HashSet<String>();
            Map<String,Set<String>> sspShortlistedAd = new HashMap<String, Set<String>>();
            Set<String> totalAPIShortlistedAd = new HashSet<String>();
            boolean directDemandFound = false;
            int directDemandType = DemandType.DIRECT.getCode();
            for(ResponseAdInfo responseAdInfo:responseAdInfoSet){
                if(responseAdInfo.getDemandtype() == directDemandType){
                    directresponseAdnfo.add(responseAdInfo);
                    directShortlistedAd.add(responseAdInfo.getGuid());
                    directDemandFound = true;
                }else{
                    sspdspresponseAdnfo.add(responseAdInfo);
                    totalAPIShortlistedAd.add(responseAdInfo.getGuid());
                    if(sspShortlistedAd.get(responseAdInfo.getAdvertiserGuid()) == null){
                        Set<String> adSet = new HashSet<String>();
                        adSet.add(responseAdInfo.getGuid());
                        sspShortlistedAd.put(responseAdInfo.getAdvertiserGuid(), adSet);
                    }else{
                        sspShortlistedAd.get(responseAdInfo.getAdvertiserGuid()).add(responseAdInfo.getGuid());
                    }
                }
            }
            if(directDemandFound){
                ReqLog.requestDebug(request, "Inside SSPOrInHouse Direct Demand Found");
                takeFlowToKritterDemand = defaultToDirectWithDebug("Direct Demand Found in SspOrInHouse", context,null);
                response.setResponseAdInfo(directresponseAdnfo);
                response.setShortlistedAdIdSet(directShortlistedAd);;
                return;
            }
            if(demandPreference == DemandPreference.DIRECT){
                ReqLog.requestDebug(request, "Inside SSPOrInHouse Direct Preference is Direct");
                takeFlowToKritterDemand = defaultToDirectWithDebug("Demand Preference Direct", context, null);
                response.setResponseAdInfo(directresponseAdnfo);
                response.setShortlistedAdIdSet(directShortlistedAd);;
                return;
            }
            logger.debug("Direct Demand Not Found");
            response.setResponseAdInfo(sspdspresponseAdnfo);
            if(demandPreference == DemandPreference.DirectThenDSP){
                context.setValue(this.ruleSetVariableNameForKritterWorkflow,3);
                return;
            }

            int globalRuleEntityCount = this.sspGlobalRulesCache.getEntityCount();
            if(globalRuleEntityCount != 1){
                ReqLog.requestDebug(request, "Inside SSPOrInHouse this.sspGlobalRulesCache.getEntityCount() not1 ");
                takeFlowToKritterDemand = defaultToDirectWithError("Global Entity count != 1", context,response );
                return;
            }
            SspGlobalRulesEntity sspGlobalRuleEntity = this.sspGlobalRulesCache.query(SSPEnum.INSERT_ID.getCode());
            SSPGlobalRuleDef sspGlobalRuleDef = sspGlobalRuleEntity.getSspGlobalRuleDef();
            if(sspGlobalRuleDef == null){
                ReqLog.requestDebug(request, "Inside SSPOrInHouse sspGlobalRuleDef == null ");
                takeFlowToKritterDemand = defaultToDirectWithError("NO NON Direct Demand Found Found", context, response);
                return;
            }
            List<Map.Entry<String, Double>> apiAdvertiserList = sspGlobalRuleEntity.getAvertiserInOrder();
            if(apiAdvertiserList == null || apiAdvertiserList.size() <1){
                ReqLog.requestDebug(request, "Inside SSPOrInHouse apiAdvertiserList not found ");
                takeFlowToKritterDemand = defaultToDirectWithError("NO NON Direct Demand Found Found", context, response);
                return;
            }
            int timeoutInMilis = SSPEnum.READ_TIMEOUT.getCode();
            if(accountEntity != null){
                timeoutInMilis = accountEntity.getTimeout();
            }
            boolean externaldemandFound = false;
            long startTime = System.currentTimeMillis();
            ReqLog.requestDebug(request, "Inside SSPOrInHouse Timeout "+timeoutInMilis );
            ReqLog.requestDebug(request, "Inside SSPOrInHouse Start time "+startTime );
            for(Map.Entry<String, Double> apiAdsertiser:apiAdvertiserList){
                long currentTime = System.currentTimeMillis();
                ReqLog.requestDebug(request, "Inside SSPOrInHouse EndTime"+currentTime);
                int diff = (int)(currentTime - startTime);
                if(diff > timeoutInMilis){
                    ReqLog.requestDebug(request, "Inside SSPOrInHouse timedout");
                    takeFlowToKritterDemand = defaultToDirectWithError("SSP AD Fetch Timesout", context, response);
                    return;
                }
                int returnCode = fetchAdsFromDemandPartnerApi(apiAdsertiser.getKey(),sspShortlistedAd, request, response, timeoutInMilis-diff);
                if(returnCode == 0){
                    externaldemandFound = true;
                    takeFlowToKritterDemand = 2;
                    context.setValue(this.ruleSetVariableNameForKritterWorkflow,takeFlowToKritterDemand);

                    logger.debug("Ad Found from demand partner id: {} , going to use it for serving",
                            apiAdsertiser.getKey());
                    break;
                }
            }
            if(!externaldemandFound){
                takeFlowToKritterDemand = defaultToDirectWithError("NO NON Direct Demand Found Found", context, response);
                return;
            }
        }else{
            takeFlowToKritterDemand = defaultToDirectWithError("Demand Preference Not  Found", context, null);
            return;
        }
    }
}
