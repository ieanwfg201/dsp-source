package com.kritter.exchange.job;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.adserving.thrift.struct.DspNoFill;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.adserving.thrift.struct.ReqState;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.common.site.entity.Site;
import com.kritter.common_caches.pmp.*;
import com.kritter.constants.*;
import com.kritter.entity.exchangethrift.CreateExchangeThrift;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.winner.WinEntity;
import com.kritter.exchange.formatting.FormatDspResponse;
import com.kritter.exchange.request_openrtb_2_3.converter.v1.Convert;
import com.kritter.exchange.response_openrtb_2_3.converter.v1.ConvertResponse;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;
import com.kritter.fanoutinfra.apiclient.ning.NingClient;
import com.kritter.fanoutinfra.executorservice.common.KExecutor;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.auction_strategies.common.KAuction;
import com.kritter.auction_strategies.second_price.SecondPriceAuction;
import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;

import com.kritter.utils.common.dsp.BidRequestModifier;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

/**
 * This job fetches ads from third party demand channels, which can be standalone DSPs
 * or a marketplace of DSPs.
 */
public class ExchangeJob implements Job
{
    private String loggerName;
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private String httpClientName;
    private AccountCache accountCache;
    private IABCategoriesCache iabCategoriesCache;
    private int requestConvertversion;
    private int maxConnectionPerHost; 
    private int maxConnection;
    private String auctionStrategy;
    private String exchangeInternalWinUrl;
    private String secretKey;
    private String errorOrEmptyResponse;
    private int urlVersion;
    private String exchange_rule_var;
    private PrivateMarketPlaceDealCache privateMarketPlaceDealCache;
    private ThirdPartyConnectionDSPMappingCache thirdPartyConnectionDSPMappingCache;
    private DSPAndAdvertiserMappingCache dspAndAdvertiserMappingCache;
    private Map<String,BidRequestModifier> bidRequestModifierMap;
    public ExchangeJob(
                        String loggerName,
                        String jobName,
                        String requestObjectKey,
                        String responseObjectKey,
                        int requestConvertversion,
                        int maxConnectionPerHost,
                        int maxConnection,
                        String httpClientName,
                        String auctionStrategy,
                        int urlVersion,
                        String errorOrEmptyResponse,
                        String secretKey,
                        AccountCache accountCache,
                        IABCategoriesCache iabCategoriesCache,
                        ServerConfig serverConfig,
                        String exchange_rule_var
                      )
    {
        this.loggerName = loggerName;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.httpClientName = httpClientName;
        this.urlVersion = urlVersion;
        this.requestConvertversion = requestConvertversion;
        this.errorOrEmptyResponse = errorOrEmptyResponse;
        this.maxConnectionPerHost = maxConnectionPerHost;
        this.maxConnection = maxConnectionPerHost;
        this.auctionStrategy= auctionStrategy;
        this.accountCache = accountCache;
        this.iabCategoriesCache = iabCategoriesCache;
        this.exchangeInternalWinUrl = serverConfig.getValueForKey(ServerConfig.EXCHANGE_INTERNAL_WIN_API_URL_PREFIX);
        this.secretKey = secretKey;
        this.exchange_rule_var = exchange_rule_var;
    }

    public ExchangeJob(
                        String loggerName,
                        String jobName,
                        String requestObjectKey,
                        String responseObjectKey,
                        int requestConvertversion,
                        int maxConnectionPerHost,
                        int maxConnection,
                        String httpClientName,
                        String auctionStrategy,
                        int urlVersion,
                        String errorOrEmptyResponse,
                        String secretKey,
                        AccountCache accountCache,
                        IABCategoriesCache iabCategoriesCache,
                        ServerConfig serverConfig,
                        String exchange_rule_var,
                        PrivateMarketPlaceDealCache privateMarketPlaceDealCache,
                        ThirdPartyConnectionDSPMappingCache thirdPartyConnectionDSPMappingCache,
                        DSPAndAdvertiserMappingCache dspAndAdvertiserMappingCache,
                        Map<String,BidRequestModifier> bidRequestModifierMap
                      )
    {
        this.loggerName = loggerName;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.httpClientName = httpClientName;
        this.urlVersion = urlVersion;
        this.requestConvertversion = requestConvertversion;
        this.errorOrEmptyResponse = errorOrEmptyResponse;
        this.maxConnectionPerHost = maxConnectionPerHost;
        this.maxConnection = maxConnectionPerHost;
        this.auctionStrategy= auctionStrategy;
        this.accountCache = accountCache;
        this.iabCategoriesCache = iabCategoriesCache;
        this.exchangeInternalWinUrl = serverConfig.getValueForKey(ServerConfig.EXCHANGE_INTERNAL_WIN_API_URL_PREFIX);
        this.secretKey = secretKey;
        this.exchange_rule_var = exchange_rule_var;
        this.privateMarketPlaceDealCache = privateMarketPlaceDealCache;
        this.thirdPartyConnectionDSPMappingCache = thirdPartyConnectionDSPMappingCache;
        this.dspAndAdvertiserMappingCache = dspAndAdvertiserMappingCache;
        this.bidRequestModifierMap = bidRequestModifierMap;
    }
    @Override
    public String getName()
    {
        return this.jobName;
    }

    @Override
    public void execute(Context context)
    {
        boolean emptyExchangeAds = false;
        boolean defaultToKritter = true;
        boolean passback=false;
        boolean fill = false;
        logger.info("Inside ExchangeJob");
        Request request = (Request)context.getValue(this.requestObjectKey);
        Response response = (Response)context.getValue(this.responseObjectKey);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest("Inside ExchangeJob");
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse)context.
                getValue(Workflow.CONTEXT_RESPONSE_KEY);
        String formattedResponse = null;
        int responseCode =  HttpServletResponse.SC_NO_CONTENT;
        
        try{
            if(request == null || request.getSite() == null || request.getSite().getPublisherId() == null){
                logger.info("ExchangeJob: Request/Site/PubId null");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: Request/Site/PubId null");
                }
                return;
            }
            if(SITE_PASSBACK_TYPE.DIRECT_PASSBACK == request.getSite().getSitePassbackType()){
                passback=true;
            }
            if(response == null){
                logger.info("ExchangeJob: Response null");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: Response null");
                }
                return;
            }
            
            Set<ResponseAdInfo> responseAdInfoSet = response.getResponseAdInfo();
            if(responseAdInfoSet == null){
                logger.info("ExchangeJob: responseAdInfoSet null");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: responseAdInfoSet null");
                }
                emptyExchangeAds = true;
                return;
            }
            if(responseAdInfoSet.size() < 1){
                logger.info("ExchangeJob: responseAdInfoSet size < 1");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: responseAdInfoSet size < 1");
                }
                emptyExchangeAds = true;
                return;
            }

            /*Pick the account for requesting site.*/
            AccountEntity pubEntity = this.accountCache.query(request.getSite().getPublisherId());

            /*If demand preference found at site level use it, otherwise use demand preference set for publisher.*/
            DemandPreference demandPreference = request.getSite().getDemandPreference();
            if(null == demandPreference)
                demandPreference = pubEntity.getDemandPreference();

            if(demandPreference == DemandPreference.OnlyDSP || demandPreference == DemandPreference.DirectThenDSP){
                defaultToKritter = false;
            }
            int requestTimeoutMillis = pubEntity.getTimeout();
            Convert convertRequest2_3 = new Convert(this.loggerName);
            com.kritter.exchange.request_openrtb_2_2.converter.v1.Convert convertRequest2_2 = new com.kritter.exchange.request_openrtb_2_2.converter.v1.Convert(this.loggerName);

             /*
              * Fetch deals applied on the requesting site.Then check for each ad if the deal is applicable or not
              * by checking if the deal targets that ad or not. If yes then include that deal in the bid request
              * object to be sent to that particular third party demand channel connection.
              */
            Set<PrivateMarketPlaceCacheEntity> dealsForSite =  fetchPMPDealsForRequestingSite(request.getSite());

            /*Prepare bid request parent node that would be used for sending bidrequest payload to external DSPs.
            * Over here we use a map to store bid request payload for each external DSP, as depending on the open
            * rtb version they accept, bid request payload might change, also adding deals to each bid request
            * would depend upon the ad selected. As deals target specific ads for routing to external demand
            * channels.
            * */

            Map<String,String> bidRequestPayloadPerDSP = new HashMap<String, String>();
            Map<String,Object> bidRequestObjectPerDSP = new HashMap<String, Object>();

             /*This map would store bidder URL against the corresponding advertiser guid used by http client for
             * firing bid requests and hence fetching bid response payloads.
             * */
            Map<String,URI> dspGuidUrlMap = new HashMap<String, URI>();

            /*This map would store the ResponseAdInfo for each external DSP identified via advertiser guid */
            Map<String,ResponseAdInfo> dspGuidResponseAdInfoMap = new HashMap<String, ResponseAdInfo>();
            CreateExchangeThrift createExchangeThrift = new CreateExchangeThrift(loggerName);
            createExchangeThrift.setRequestId(request.getRequestId())
            	.setPubincId(request.getSite().getPublisherIncId())
            	.setSiteId(request.getSite().getSiteIncId())
            	.setExtSupplyAttrInternalId(request.getExternalSupplyAttributesInternalId())
            	.setExtSupplyId(request.getSite().getExternalSupplyId())
            	.setFloor(request.getSite().getEcpmFloorValue())
            	.setReqState(ReqState.HEALTHY)
            	.setCountryId(request.getCountryUserInterfaceId())
            	.setTime(request.getTime()/1000);
            if(null!=request.getHandsetMasterData() &&
                    null!=request.getHandsetMasterData().getDeviceOperatingSystemId())
            	createExchangeThrift.setDeviceOsId(request.getHandsetMasterData().getDeviceOperatingSystemId());
            else
            	createExchangeThrift.setDeviceOsId(-1);
            if(request.getSite().isNative()){
            	createExchangeThrift.setFormatId((short)CreativeFormat.Native.getCode());
            }else if(request.getSite().isVideo()){
            	createExchangeThrift.setFormatId((short)CreativeFormat.VIDEO.getCode());
            }else{
                	createExchangeThrift.setFormatId((short)CreativeFormat.BANNER.getCode());
            }
            

            
            for(ResponseAdInfo responseAdInfo: responseAdInfoSet)
            {
                AccountEntity advEntity = this.accountCache.query(responseAdInfo.getAdvertiserGuid());

                /****************************First form bid request as per the version required***********************/
                BidRequestParentNodeDTO bidRequestParentNodeDtoTwoDotThree = null;
                com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO
                                          bidRequestParentNodeDtoTwoDotTwo = null;

                if(null == advEntity.getOpenRTBVersion())
                {
                    logger.error("There is no open rtb version required defined for advid: {} ,skipping calling dsp",
                                  advEntity.getGuid());
                    continue;
                }

                if(null != advEntity && advEntity.getOpenRTBVersion().getCode() == OpenRTBVersion.VERSION_2_3.getCode())
                {
                	if(request.isAggregatorOpenRTB()){
                		bidRequestParentNodeDtoTwoDotThree = request.getOpenrtbObjTwoDotThree();
                	}else{
                		bidRequestParentNodeDtoTwoDotThree = convertRequest2_3.convert(request, this.requestConvertversion, pubEntity, this.iabCategoriesCache,advEntity);
                	}

                    /*Add deal object to parent bid request if applicable.*/
                    addPMPEntityToParentBidRequestVersion2_3(bidRequestParentNodeDtoTwoDotThree,dealsForSite,advEntity,responseAdInfo.getAdId());

                    if (bidRequestParentNodeDtoTwoDotThree == null) {
                        logger.info("ExchangeJob: Request to bidRequestParentNodeDto conversion failed");
                        if (request.isRequestForSystemDebugging()) {
                            request.addDebugMessageForTestRequest("ExchangeJob: Request to bidRequestParentNodeDto conversion failed");
                        }
                        emptyExchangeAds = true;
                        request.setNoFillReason(NoFillReason.EX_OD_REQ_CONVERT);
                        return;
                    }

                    /****************check if bidrequest modifier is present then modify bid request******************/
                    BidRequestModifier<BidRequestParentNodeDTO> bidRequestModifier = null;
                    if(null != this.bidRequestModifierMap)
                        bidRequestModifier = this.bidRequestModifierMap.get(advEntity.getGuid());
                    if(null != bidRequestModifier)
                        bidRequestParentNodeDtoTwoDotThree = bidRequestModifier.modifyBidRequest(bidRequestParentNodeDtoTwoDotThree);
                    /*****************************Bid modification completed**************************************/

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
                    JsonNode jsonNode = objectMapper.valueToTree(bidRequestParentNodeDtoTwoDotThree);

                    if (jsonNode == null) {
                        logger.info("ExchangeJob: bidRequestParentNodeDto to Json Conversion failed");
                        if (request.isRequestForSystemDebugging()) {
                            request.addDebugMessageForTestRequest("ExchangeJob: bidRequestParentNodeDto to Json Conversion failed");
                        }
                        emptyExchangeAds = true;
                        request.setNoFillReason(NoFillReason.EX_OD_REQ_SER_NULL);
                        return;
                    }

                    String bidRequestPayload = jsonNode.toString();

                    if (request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("BidRequest");
                        request.addDebugMessageForTestRequest(bidRequestPayload);
                        logger.debug(bidRequestPayload);
                    }

                    bidRequestPayloadPerDSP.put(responseAdInfo.getAdvertiserGuid(),bidRequestPayload);
                    bidRequestObjectPerDSP.put(responseAdInfo.getAdvertiserGuid(),bidRequestParentNodeDtoTwoDotThree);
                }
                else if(null != advEntity && advEntity.getOpenRTBVersion().getCode() == OpenRTBVersion.VERSION_2_2.getCode())
                {
                    bidRequestParentNodeDtoTwoDotTwo = convertRequest2_2.convert(
                                                                                 request,
                                                                                 this.requestConvertversion,
                                                                                 pubEntity,
                                                                                 this.iabCategoriesCache
                                                                                );

                    /*Add deal object to parent bid request if applicable.*/
                    addPMPEntityToParentBidRequestVersion2_2(bidRequestParentNodeDtoTwoDotTwo,dealsForSite,advEntity,responseAdInfo.getAdId());

                    if (bidRequestParentNodeDtoTwoDotTwo == null) {
                        logger.info("ExchangeJob: Request to bidRequestParentNodeDto conversion failed");
                        if (request.isRequestForSystemDebugging()) {
                            request.addDebugMessageForTestRequest("ExchangeJob: Request to bidRequestParentNodeDto conversion failed");
                        }
                        emptyExchangeAds = true;
                        request.setNoFillReason(NoFillReason.EX_OD_REQ_CONVERT);
                        return;
                    }

                    /****************check if bidrequest modifier is present then modify bid request******************/
                    BidRequestModifier<com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO> bidRequestModifier = null;
                    if(null != this.bidRequestModifierMap)
                        bidRequestModifier = this.bidRequestModifierMap.get(advEntity.getGuid());
                    if(null != bidRequestModifier)
                        bidRequestParentNodeDtoTwoDotTwo = bidRequestModifier.modifyBidRequest(bidRequestParentNodeDtoTwoDotTwo);
                    /*****************************Bid modification completed**************************************/

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
                    JsonNode jsonNode = objectMapper.valueToTree(bidRequestParentNodeDtoTwoDotTwo);

                    if (jsonNode == null) {
                        logger.info("ExchangeJob: bidRequestParentNodeDto to Json Conversion failed");
                        if (request.isRequestForSystemDebugging()) {
                            request.addDebugMessageForTestRequest("ExchangeJob: bidRequestParentNodeDto to Json Conversion failed");
                        }
                        emptyExchangeAds = true;
                        request.setNoFillReason(NoFillReason.EX_OD_REQ_SER_NULL);
                        return;
                    }

                    String bidRequestPayload = jsonNode.toString();

                    if (request.isRequestForSystemDebugging()) {
                        request.addDebugMessageForTestRequest("BidRequest");
                        request.addDebugMessageForTestRequest(bidRequestPayload);
                        logger.debug(bidRequestPayload);
                    }

                    bidRequestPayloadPerDSP.put(responseAdInfo.getAdvertiserGuid(),bidRequestPayload);
                    bidRequestObjectPerDSP.put(responseAdInfo.getAdvertiserGuid(),bidRequestParentNodeDtoTwoDotTwo);
                }
                /*****************************************************************************************************/

                if(advEntity != null){
                    try{
                        URI uri = new URI(advEntity.getDemand_url());
                        dspGuidUrlMap.put(responseAdInfo.getAdvertiserGuid(), uri);
                        dspGuidResponseAdInfoMap.put(responseAdInfo.getAdvertiserGuid(), responseAdInfo);
                        createExchangeThrift.addDemand(responseAdInfo.getAdvertiserGuid(), advEntity.getAccountId(), responseAdInfo.getCampaignId(), responseAdInfo.getAdId());
                    }catch(Exception e){
                        logger.info("ExchangeJob: Incorrect URI {} for adv {}", advEntity.getDemand_url(), responseAdInfo.getAdvertiserGuid());
                        if(request.isRequestForSystemDebugging()){
                            request.addDebugMessageForTestRequest("ExchangeJob: Incorrect URI "+advEntity.getDemand_url() +" for adv "+responseAdInfo.getAdvertiserGuid());
                        }
                    }
                }
            }

            if(dspGuidUrlMap.size() < 1){
                logger.info("ExchangeJob: dspGuidUrlMap size < 1");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: dspGuidUrlMap size < 1");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(NoFillReason.EX_OD_URL_MAP_EMP);
                return;
            }
            
            KExecutor kexecutor = KExecutor.getKExecutor(loggerName);
            if(kexecutor == null){
                logger.info("ExchangeJob: kexecutor is null ");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: kexecutor is null ");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(NoFillReason.EX_OD_EXEC_NULL);
                createExchangeThrift.updateErrorReqState(ReqState.KEXEC_ERR);
                request.setCreateExchangeThrift(createExchangeThrift);
                return;
            }
            Map<String,String> advResponseMap= null;

            /*This is where open rtb bid request payload is sent to external bidder*/
            if(ExchangeConstants.ningApiClient.equals(httpClientName)){
                KHttpClient k = new NingClient(loggerName);
                advResponseMap = kexecutor.call(dspGuidUrlMap, k, bidRequestPayloadPerDSP,
                                                requestTimeoutMillis, this.maxConnectionPerHost,
                                                this.maxConnection);
            }else{
                logger.info("ExchangeJob: httpClientName NF {} ",httpClientName);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: httpClientName NF "+httpClientName);
                }
                request.setNoFillReason(NoFillReason.EX_OD_API_INCORRECT);
                emptyExchangeAds = true;
                createExchangeThrift.updateErrorReqState(ReqState.ASYNC_ERR);
                request.setCreateExchangeThrift(createExchangeThrift);
                return;
            }
            if(advResponseMap == null || advResponseMap.size() < 1){
                logger.info("ExchangeJob: advResponseMap == null || advResponseMap.size() < 1");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: advResponseMap == null || advResponseMap.size() < 1");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(NoFillReason.EX_OD_RESP_EMPTY);
                createExchangeThrift.updateAllTimeOut();
                request.setCreateExchangeThrift(createExchangeThrift);
                return;
            }

            /*Keep map for responses as per the open rtb version.*/
            Map<String,BidResponseEntity> advBidResponseMap2_3 = new HashMap<String, BidResponseEntity>();
            Map<String,com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity>  advBidResponseMap2_2 =
                                            new HashMap<String, com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity>();

            ConvertResponse convertResponse2_3 = new ConvertResponse(loggerName);
            com.kritter.exchange.response_openrtb2_2.converter.v1.ConvertResponse convertResponse2_2 =
                                new com.kritter.exchange.response_openrtb2_2.converter.v1.ConvertResponse(loggerName);

            for(String key:advResponseMap.keySet()){

                AccountEntity advEntity = this.accountCache.query(key);
                if(null != advEntity && advEntity.getOpenRTBVersion().getCode() == OpenRTBVersion.VERSION_2_3.getCode())
                {
                    BidResponseEntity bidResponseEntity2_3 = convertResponse2_3.convert(advResponseMap.get(key));
                    if(bidResponseEntity2_3 == null){
                        logger.info("ExchangeJob: bidResponseEntity for adv {}",key);
                        if(request.isRequestForSystemDebugging()){
                            request.addDebugMessageForTestRequest("ExchangeJob: bidResponseEntity for adv " + key);
                        }
                    }else {
                        advBidResponseMap2_3.put(key, bidResponseEntity2_3);
                    }
                }
                else if(null != advEntity && advEntity.getOpenRTBVersion().getCode() == OpenRTBVersion.VERSION_2_2.getCode())
                {
                    com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity bidResponseEntity2_2 = convertResponse2_2.convert(advResponseMap.get(key));
                    if(bidResponseEntity2_2 == null){
                        logger.info("ExchangeJob: bidResponseEntity for adv {}",key);
                        if(request.isRequestForSystemDebugging()){
                            request.addDebugMessageForTestRequest("ExchangeJob: bidResponseEntity for adv " + key);
                        }
                    }else {
                        advBidResponseMap2_2.put(key, bidResponseEntity2_2);
                    }
                }
            }

            if(advBidResponseMap2_3.size()<1 && advBidResponseMap2_2.size()<1){
                logger.info("ExchangeJob: advBidResponseMap.size()<1");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: advBidResponseMap.size()<1");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(NoFillReason.EX_OD_BID_RESP_EMPTY);
                createExchangeThrift.updateAllTimeOut();
                request.setCreateExchangeThrift(createExchangeThrift);
                return;
            }

            WinEntity winEntity2_3 = null;
            WinEntity winEntity2_2 = null;
            WinEntity winEntity = null;
            createExchangeThrift.updateAllTimeOut();
            if(ExchangeConstants.auction_strategy_second_price.equals(auctionStrategy))
            {
                KAuction auction = new SecondPriceAuction();
                winEntity2_3 = auction.getWinnerOpenRTB2_3(advBidResponseMap2_3, request,createExchangeThrift);
                winEntity2_2 = auction.getWinnerOpenRTB2_2(advBidResponseMap2_2, request,createExchangeThrift);
            }
            else{
                logger.info("ExchangeJob: auctionStrategy NF {}",auctionStrategy);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: auctionStrategy NF "+auctionStrategy);
                }
                request.setNoFillReason(NoFillReason.EX_OD_AP_INCORRECT);
                emptyExchangeAds = true;
                createExchangeThrift.updateErrorReqState(ReqState.AUC_NF);
                request.setCreateExchangeThrift(createExchangeThrift);
                return;
            }

            /*Compare and choose among the different open rtb versions' win entity the best one.*/
            float price2_3 = 0.0f;
            float price2_2 = 0.0f;
            if(null != winEntity2_3)
                price2_3 = winEntity2_3.getWin_price();
            if(null != winEntity2_2)
                price2_2 = winEntity2_2.getWin_price();

            if(price2_3 > price2_2)
                winEntity = winEntity2_3;
            else
                winEntity = winEntity2_2;

            if(winEntity == null){
                logger.info("ExchangeJob: winEntity == null");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: winEntity == null");
                }
                request.setNoFillReason(NoFillReason.EX_OD_WIN_NULL);
                emptyExchangeAds = true;
                request.setCreateExchangeThrift(createExchangeThrift);
                return;
            }
            createExchangeThrift.updateDemandState(winEntity.getAdvId(), DspNoFill.FILL);
            createExchangeThrift.setWinprice(winEntity.getWin_price());

            Set<ResponseAdInfo> newResponseAdInfo  = new HashSet<ResponseAdInfo>();
            ResponseAdInfo newResponse = dspGuidResponseAdInfoMap.get(winEntity.getAdvId());
            newResponse.setEcpmValue((double)winEntity.getWin_price());
            newResponseAdInfo.add(newResponse);
            response.setResponseAdInfo(newResponseAdInfo);
            Set<String> shortlistedAd = new HashSet<String>();
            shortlistedAd.add(newResponse.getGuid());
            response.setShortlistedAdIdSet(shortlistedAd);
            
            String clickUri = CreativeFormatterUtils.prepareClickUri(logger, request, newResponse, response.getBidderModelId(), urlVersion, request.getInventorySource(), response.getSelectedSiteCategoryId(), this.secretKey);
            boolean isNative = false;
            if(request.getSite() != null && request.getSite().isNative()){
                isNative = true;
            }
            boolean isVideo = false;
            if(request.getSite() != null && request.getSite().isVideo()){
            	isVideo = true;
            }
            if(request.isRequestForSystemDebugging() && winEntity != null){
                request.addDebugMessageForTestRequest("ExchangeJob BIDRESPONSE");
                ObjectMapper objectMapper1 = new ObjectMapper();
                objectMapper1.setSerializationInclusion(Inclusion.NON_NULL);
                if(null != winEntity.getWinnerBidResponse2_3())
                {
                    JsonNode jsonNode1 = objectMapper1.valueToTree(winEntity.getWinnerBidResponse2_3());
                    request.addDebugMessageForTestRequest(jsonNode1.toString());
                }
                else if(null != winEntity.getWinnerBidResponse2_2())
                {
                    JsonNode jsonNode1 = objectMapper1.valueToTree(winEntity.getWinnerBidResponse2_2());
                    request.addDebugMessageForTestRequest(jsonNode1.toString());
                }
            }

            /*Use (advertiser_guid <-> bid_request_parent_object) map here to fetch required bid request object*/
            Object bidRequestParentNodeDtoObject = bidRequestObjectPerDSP.get(winEntity.getAdvId());
            if(bidRequestParentNodeDtoObject instanceof BidRequestParentNodeDTO)
                formattedResponse = FormatDspResponse.formatResponse2_3(winEntity, (BidRequestParentNodeDTO)bidRequestParentNodeDtoObject, clickUri, exchangeInternalWinUrl, isNative, logger,isVideo);
            else if(bidRequestParentNodeDtoObject instanceof com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO)
                formattedResponse = FormatDspResponse.formatResponse2_2(winEntity, (com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO)bidRequestParentNodeDtoObject, clickUri, exchangeInternalWinUrl, isNative, logger,isVideo);

            logger.debug("Exchange Job - formattedResponse {} ", formattedResponse);
            if(request.isRequestForSystemDebugging()){
                request.addDebugMessageForTestRequest("ExchangeJob formattedResponse");
                request.addDebugMessageForTestRequest(formattedResponse);
            }
            responseCode = HttpServletResponse.SC_OK;
            fill = true;
            emptyExchangeAds = false;
            request.setCreateExchangeThrift(createExchangeThrift);
        }catch(Exception e ){
            if(request != null){
                request.setNoFillReason(NoFillReason.EX_OD_EXCEPTION);
            }
            emptyExchangeAds = true;
            logger.error(e.getMessage(),e);
            if(request.isRequestForSystemDebugging()){
                request.addDebugMessageForTestRequest("Exception Inside ExchangeJob");
                request.addDebugMessageForTestRequest(e.getMessage());
            }
        }finally{
            try {
                if(emptyExchangeAds){
                    emptyExchangeAds(response);
                }
                if(defaultToKritter || (passback==true && fill==false )){
                    context.setValue(this.exchange_rule_var,1);
                }else{
                    context.setValue(this.exchange_rule_var,2);
                    if(request.isRequestForSystemDebugging()){
                        writeResponseToUser(httpServletResponse,request.getDebugRequestBuffer().toString(),responseCode);
                    }else{
                        writeResponseToUser(httpServletResponse,formattedResponse,responseCode);
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: Exception Inside writeResponseToUser");
                    request.addDebugMessageForTestRequest(e.getMessage());
                }
            };
        }
        
    }
    
    private void emptyExchangeAds(Response response){
        if(response == null) { return; }
        Set<ResponseAdInfo> newResponseAdInfo  = new HashSet<ResponseAdInfo>();
        Set<String> shortlistedAd = new HashSet<String>();
        response.setShortlistedAdIdSet(shortlistedAd);
        response.setResponseAdInfo(newResponseAdInfo);

    }
    
    private void writeResponseToUser(HttpServletResponse httpServletResponse,
            String content,
            int responseCode) throws IOException {

        httpServletResponse.setStatus(responseCode);
        if(responseCode==HttpServletResponse.SC_NO_CONTENT){
            return;
        }
        OutputStream os = httpServletResponse.getOutputStream();
        if(null == content)
            content = errorOrEmptyResponse;
        os.write(content.getBytes());
        os.flush();
        os.close();
    }

    /**
     * This function fetches the deals available and applicable on the requesting site.
     * @param site
     * @return
     */
    private Set<PrivateMarketPlaceCacheEntity> fetchPMPDealsForRequestingSite(Site site)
    {
        if(null == site                                ||
           null == privateMarketPlaceDealCache         ||
           null == thirdPartyConnectionDSPMappingCache ||
           null == dspAndAdvertiserMappingCache)
        {
            logger.debug("Either site is null or the client does not require pmp functionality as caches are null");
            return null;
        }

        try
        {
            Set<String> dealIdSetForSite = privateMarketPlaceDealCache.query
                                                (new PrivateMarketPlaceSiteIncIdSecondaryIndex(site.getSiteIncId()));

            Set<PrivateMarketPlaceCacheEntity> deals = new HashSet<PrivateMarketPlaceCacheEntity>();

            for(String dealId : dealIdSetForSite)
            {
                PrivateMarketPlaceCacheEntity privateMarketPlaceCacheEntity = privateMarketPlaceDealCache.query(dealId);
                deals.add(privateMarketPlaceCacheEntity);
            }

            return deals;
        }
        catch (UnSupportedOperationException e)
        {
            logger.error("UnSupportedOperationException inside fetchPMPDealsForRequestingSite of ExchangeJob",e);
        }

        return null;
    }

    /**
     * This function takes input as bid request parent node for open rtb version 2.2 and
     * set of deals applicable to requesting site, also it takes input as the advertiser
     * account entity for the ad selected. It has input as the ad guid that this deal
     * wants to target, using ad guid selected in the workflow, we must match if the
     * deal targets the ad guid which has been selected in supply demand matching jobs.
     * @param bidRequestParentNodeDTO
     * @param deals
     * @param accountEntity
     * @return
     */
    private void addPMPEntityToParentBidRequestVersion2_2
                                                         (
                                                            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO bidRequestParentNodeDTO,
                                                            Set<PrivateMarketPlaceCacheEntity> deals,
                                                            AccountEntity accountEntity,
                                                            Integer adId
                                                         )
    {
        if(null == deals || deals.size() <= 0)
        {
            logger.debug("There are no deals to be setup in the bid request inside " +
                         "addPMPEntityToParentBidRequestVersion2_2");
            return;
        }

        com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestPMPDTO bidRequestPMPDTO = null;
        Set<com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDealDTO> dealDTOs =
                            new HashSet<com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDealDTO>();

        for(PrivateMarketPlaceCacheEntity deal : deals)
        {
            Integer[] dealAdIdList = deal.getAdIdList();
            List<Integer> dealAdIdSet = new ArrayList<Integer>();
            if(null != dealAdIdList)
                dealAdIdSet = Arrays.asList(dealAdIdList);

            if(!dealAdIdSet.contains(adId))
            {
                logger.debug("The deal id:{} applicable for this site has ad id list: {} , which is not applicable to " +
                        "requesting ad : {}", deal.getId(),dealAdIdSet,adId);
                continue;
            }

            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDealDTO bidRequestDealDTO =
                                    new com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDealDTO();

            bidRequestDealDTO.setDealId(deal.getId());

            bidRequestDealDTO.setAuctionType(Integer.valueOf(deal.getAuctionType()));

            if(null != deal.getDealCPM())
                bidRequestDealDTO.setBidFloor(deal.getDealCPM().floatValue());

            bidRequestDealDTO.setBidFloorCurrency(DefaultCurrency.defaultCurrency.getName());

            String[] whiteListedBuyerSeats = fetchWhitelistedBuyerIds(deal,accountEntity);
            if(null == whiteListedBuyerSeats)
            {
                logger.error("Whitelisted Buyer seat ids could not be generated or is blank, the deal: {} ," +
                        "will not have any meaning since it will target all seats.Skipping it.",deal.getId());
                return;
            }

            bidRequestDealDTO.setWhitelistedBuyerSeats(whiteListedBuyerSeats);

            dealDTOs.add(bidRequestDealDTO);
        }

        if(null != dealDTOs && dealDTOs.size() > 0)
        {
            bidRequestPMPDTO = new com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestPMPDTO();
            bidRequestPMPDTO.setPrivateAuctionDeals(dealDTOs.toArray(new com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDealDTO[dealDTOs.size()]));
            bidRequestPMPDTO.setPrivateAuction(1);

            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO[] bidRequestImpressionDTOs = bidRequestParentNodeDTO.getBidRequestImpressionArray();

            if(null != bidRequestImpressionDTOs)
            {
                /*Since there is only one impression we are sending so pmp is set to only one impression.*/
                for(com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs)
                {
                    bidRequestImpressionDTO.setBidRequestPMPDTO(bidRequestPMPDTO);
                }
            }
        }
    }

    /**
     * This function takes input as bid request parent node for open rtb version 2.3 and
     * set of deals applicable to requesting site, also it takes input as the advertiser
     * account entity for the ad selected. It has input as the ad guid that this deal
     * wants to target, using ad guid selected in the workflow, we must match if the
     * deal targets the ad guid which has been selected in supply demand matching jobs.
     * @param bidRequestParentNodeDTO
     * @param deals
     * @param accountEntity
     * @return
     */
    private void addPMPEntityToParentBidRequestVersion2_3
                                                         (
                                                          BidRequestParentNodeDTO bidRequestParentNodeDTO,
                                                          Set<PrivateMarketPlaceCacheEntity> deals,
                                                          AccountEntity accountEntity,
                                                          Integer adId
                                                         )
    {
        if(null == deals || deals.size() <= 0)
        {
            logger.debug("There are no deals to be setup in the bid request inside " +
                         "addPMPEntityToParentBidRequestVersion2_3");
            return;
        }

        BidRequestPMPDTO bidRequestPMPDTO = null;
        Set<BidRequestDealDTO> dealDTOs = new HashSet<BidRequestDealDTO>();

        for(PrivateMarketPlaceCacheEntity deal : deals)
        {
            Integer[] dealAdIdList = deal.getAdIdList();
            List<Integer> dealAdIdSet = new ArrayList<Integer>();
            if(null != dealAdIdList)
                dealAdIdSet = Arrays.asList(dealAdIdList);

            if(!dealAdIdSet.contains(adId))
            {
                logger.debug("The deal id:{} applicable for this site has ad id list: {} , which is not applicable to " +
                             "requesting ad : {}", deal.getId(),dealAdIdSet,adId);
                continue;
            }

            BidRequestDealDTO bidRequestDealDTO = new BidRequestDealDTO();

            bidRequestDealDTO.setDealId(deal.getId());

            Map<String,String[]> whitelistedAdvertiserDomainsMap = deal.getWhitelistedAdvertiserDomainsMap();
            if(null != whitelistedAdvertiserDomainsMap)
            {
                String[] whitelistedAdvertiserDomains = whitelistedAdvertiserDomainsMap.get(accountEntity.getId());
                if(null != whitelistedAdvertiserDomains)
                    bidRequestDealDTO.setAllowedAdvertiserDomains(whitelistedAdvertiserDomains);
            }

            bidRequestDealDTO.setAuctionType(Integer.valueOf(deal.getAuctionType()));

            if(null != deal.getDealCPM())
                bidRequestDealDTO.setBidFloor(deal.getDealCPM().floatValue());

            bidRequestDealDTO.setBidFloorCurrency(DefaultCurrency.defaultCurrency.getName());

            String[] whiteListedBuyerSeats = fetchWhitelistedBuyerIds(deal,accountEntity);
            if(null == whiteListedBuyerSeats)
            {
                logger.error("Whitelisted Buyer seat ids could not be generated or is blank, the deal: {} ," +
                             "will not have any meaning since it will target all seats.Skipping it.",deal.getId());
                return;
            }

            bidRequestDealDTO.setWhitelistedBuyerSeats(whiteListedBuyerSeats);

            dealDTOs.add(bidRequestDealDTO);
        }

        if(null != dealDTOs && dealDTOs.size() > 0)
        {
            bidRequestPMPDTO = new BidRequestPMPDTO();
            bidRequestPMPDTO.setPrivateAuctionDeals(dealDTOs.toArray(new BidRequestDealDTO[dealDTOs.size()]));
            bidRequestPMPDTO.setPrivateAuction(1);

            BidRequestImpressionDTO[] bidRequestImpressionDTOs = bidRequestParentNodeDTO.getBidRequestImpressionArray();

            if(null != bidRequestImpressionDTOs)
            {
                /*Since there is only one impression we are sending so pmp is set to only one impression.*/
                for(BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs)
                {
                    bidRequestImpressionDTO.setBidRequestPMPDTO(bidRequestPMPDTO);
                }
            }
        }
    }

    private String[] fetchWhitelistedBuyerIds(PrivateMarketPlaceCacheEntity deal,AccountEntity accountEntity)
    {
        Integer[] whiteListedDspIdArray = deal.getDspIdList();
        Integer[] whiteListedAdvIdArray = deal.getAdvertiserIdList();

        logger.debug("Whitelisted dsp id array length is : {} " , ((null == whiteListedDspIdArray) ? 0 : whiteListedDspIdArray.length));
        logger.debug("Whitelisted adv id array length is : {} " ,((null == whiteListedAdvIdArray) ? 0 : whiteListedAdvIdArray.length));

        if(null == accountEntity.getThirdPartyDemandChannel())
            return null;

        int thirdPartyDemandChannelCode = accountEntity.getThirdPartyDemandChannel().getCode();
        logger.debug("ThirdPartyDemandChannel code is {}" ,thirdPartyDemandChannelCode);

        Set<String> whitelistedBuyerIds = null;

        if( ThirdPartyDemandChannel.MARKETPLACE_OF_DSP.getCode() == thirdPartyDemandChannelCode &&
            null != whiteListedDspIdArray )
        {
            logger.debug("Inside marketplace of dsps , finding dsp ids whitelisted guid values");
            whitelistedBuyerIds = new HashSet<String>();

            for(Integer dspId : whiteListedDspIdArray)
            {
                ThirdPartyConnectionDSPMappingEntity thirdPartyConnectionDSPMappingEntity =
                        thirdPartyConnectionDSPMappingCache.query(dspId.intValue());

                logger.debug("Entry found is null: {} ", (null == thirdPartyConnectionDSPMappingEntity));

                if(null != thirdPartyConnectionDSPMappingEntity)
                {
                    logger.debug("Dsp id found for int id: {} as : {}" , dspId,thirdPartyConnectionDSPMappingEntity.getDspId());
                    whitelistedBuyerIds.add(thirdPartyConnectionDSPMappingEntity.getDspId());
                }
            }
        }
        else if( ThirdPartyDemandChannel.STANDALONE_DSP_BIDDER.getCode() == thirdPartyDemandChannelCode &&
                 null != whiteListedAdvIdArray )
        {
            whitelistedBuyerIds = new HashSet<String>();

            for(Integer advId : whiteListedAdvIdArray)
            {
                DSPAndAdvertiserMappingEntity dspAndAdvertiserMappingEntity =
                        dspAndAdvertiserMappingCache.query(advId);

                if(null != dspAndAdvertiserMappingEntity)
                {
                    logger.debug("Adv id found for int id: {} as : {}" , advId,dspAndAdvertiserMappingEntity.getAdvertiserId());
                    whitelistedBuyerIds.add(dspAndAdvertiserMappingEntity.getAdvertiserId());
                }
            }
        }

        if(null != whitelistedBuyerIds && whitelistedBuyerIds.size() > 0)
            return whitelistedBuyerIds.toArray(new String[whitelistedBuyerIds.size()]);

        return null;
    }
}
