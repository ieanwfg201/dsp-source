package com.kritter.exchange.job;

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
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.constants.DemandPreference;
import com.kritter.constants.ExchangeConstants;
import com.kritter.constants.SITE_PASSBACK_TYPE;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

/**
 * This job fetches ads from DSP's.
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
            
            AccountEntity pubEntity = this.accountCache.query(request.getSite().getPublisherId());

            /*changed to accomodate demand preference at site level*/
            DemandPreference demandPreference = request.getSite().getDemandPreference();
            if(null == demandPreference)
                demandPreference = pubEntity.getDemandPreference();

            if(demandPreference == DemandPreference.OnlyDSP){
                defaultToKritter = false;
            }
            int requestTimeoutMillis = pubEntity.getTimeout();
            Convert convertRequest = new Convert(this.loggerName);
            BidRequestParentNodeDTO bidRequestParentNodeDto = convertRequest.convert(request, this.requestConvertversion, pubEntity, this.iabCategoriesCache);
            if(bidRequestParentNodeDto == null){
                logger.info("ExchangeJob: Request to bidRequestParentNodeDto conversion failed");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: Request to bidRequestParentNodeDto conversion failed");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_REQ_CONVERT);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
            JsonNode jsonNode = objectMapper.valueToTree(bidRequestParentNodeDto);
            if(jsonNode == null){
                logger.info("ExchangeJob: bidRequestParentNodeDto to Json Conversion failed");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: bidRequestParentNodeDto to Json Conversion failed");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_REQ_SER_NULL);
                return;
            }
            if(request.isRequestForSystemDebugging()){
                request.addDebugMessageForTestRequest("BidRequest");
                request.addDebugMessageForTestRequest(jsonNode.toString());
                logger.debug(jsonNode.toString());
            }
         
            Map<String,URI> dspGuidUrlMap = new HashMap<String, URI>();
            Map<String,ResponseAdInfo> dspGuidResponseAdInfoMap = new HashMap<String, ResponseAdInfo>();
            for(ResponseAdInfo responseAdInfo: responseAdInfoSet){
                AccountEntity advEntity = this.accountCache.query(responseAdInfo.getAdvertiserGuid());
                if(advEntity != null){
                    try{
                        URI uri = new URI(advEntity.getDemand_url());
                        dspGuidUrlMap.put(responseAdInfo.getAdvertiserGuid(), uri);
                        dspGuidResponseAdInfoMap.put(responseAdInfo.getAdvertiserGuid(), responseAdInfo);
                    }catch(Exception e){
                        logger.info("ExchangeJob: Incorrect URI "+advEntity.getDemand_url() +" for adv "+responseAdInfo.getAdvertiserGuid());
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
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_URL_MAP_EMP);
                return;
            }
            
            KExecutor kexecutor = KExecutor.getKExecutor(loggerName);
            if(kexecutor == null){
                logger.info("ExchangeJob: kexecutor is null ");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: kexecutor is null ");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_EXEC_NULL);
                return;
            }
            Map<String,String> advResponseMap= null;
            if(ExchangeConstants.ningApiClient.equals(httpClientName)){
                KHttpClient k = new NingClient(loggerName);
                advResponseMap = kexecutor.call(dspGuidUrlMap, k, jsonNode.toString(), 
                            requestTimeoutMillis, this.maxConnectionPerHost, this.maxConnection);
            }else{
                logger.info("ExchangeJob: httpClientName NF "+httpClientName);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: httpClientName NF "+httpClientName);
                }
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_API_INCORRECT);
                emptyExchangeAds = true;
                return;
            }
            if(advResponseMap == null || advResponseMap.size() < 1){
                logger.info("ExchangeJob: advResponseMap == null || advResponseMap.size() < 1");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: advResponseMap == null || advResponseMap.size() < 1");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_RESP_EMPTY);
                return;
            }
            Map<String,BidResponseEntity> advBidResponseMap = new HashMap<String, BidResponseEntity>();
            ConvertResponse convertResponse = new ConvertResponse(loggerName);
            for(String key:advResponseMap.keySet()){
                BidResponseEntity bidResponseEntity = convertResponse.convert(advResponseMap.get(key));
                if(bidResponseEntity == null){
                    logger.info("ExchangeJob: bidResponseEntity for adv "+key);
                    if(request.isRequestForSystemDebugging()){
                        request.addDebugMessageForTestRequest("ExchangeJob: bidResponseEntity for adv "+key);
                    } 
                }else{
                    advBidResponseMap.put(key, bidResponseEntity);
                }
            }
            if(advBidResponseMap.size()<1){
                logger.info("ExchangeJob: advBidResponseMap.size()<1");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: advBidResponseMap.size()<1");
                }
                emptyExchangeAds = true;
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_BID_RESP_EMPTY);
                return;
            }
            WinEntity winEntity = null;
            if(ExchangeConstants.auction_strategy_second_price.equals(auctionStrategy)){
                KAuction auction = new SecondPriceAuction();
                winEntity = auction.getWinner(advBidResponseMap, request);
            }else{
                logger.info("ExchangeJob: auctionStrategy NF "+auctionStrategy);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: auctionStrategy NF "+auctionStrategy);
                }
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_AP_INCORRECT);
                emptyExchangeAds = true;
                return;
            }
            if(winEntity == null){
                logger.info("ExchangeJob: winEntity == null");
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("ExchangeJob: winEntity == null");
                }
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_WIN_NULL);
                emptyExchangeAds = true;
                return;
            }
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
            if(request.isRequestForSystemDebugging() && winEntity !=null && winEntity.getWinnerBidResponse() != null){
                request.addDebugMessageForTestRequest("ExchangeJob BIDRESPONSE");
                ObjectMapper objectMapper1 = new ObjectMapper();
                objectMapper1.setSerializationInclusion(Inclusion.NON_NULL);
                JsonNode jsonNode1 = objectMapper1.valueToTree(winEntity.getWinnerBidResponse());
                request.addDebugMessageForTestRequest(jsonNode1.toString());
            }
            formattedResponse = FormatDspResponse.formatResponse(winEntity, bidRequestParentNodeDto, clickUri, exchangeInternalWinUrl, isNative, logger);
            if(request.isRequestForSystemDebugging()){
                request.addDebugMessageForTestRequest("ExchangeJob formattedResponse");
                request.addDebugMessageForTestRequest(formattedResponse);
            }
            responseCode = HttpServletResponse.SC_OK;
            fill = true;
        }catch(Exception e ){
            if(request != null){
                request.setNoFillReason(Request.NO_FILL_REASON.EX_OD_EXCEPTION);
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
}
