package com.kritter.valuemaker.reader_v20160817.response_creator;


import RTB.VamRealtimeBidding;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.ex_int.utils.comparator.EcpmValueComparator;
import com.kritter.ex_int.utils.picker.RandomPicker;
import com.kritter.ex_int.utils.richmedia.markuphelper.MarkUpHelper;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.valuemaker.reader_v20160817.converter.response.ConvertResponse;
import com.kritter.valuemaker.reader_v20160817.entity.BidRequestVam;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class VamBidResponseCreator implements IBidResponseCreator {
    private Logger logger;
    private ObjectMapper objectMapper;
    private static final Random randomPicker = new Random();
    private String secretKey;
    private int urlVersion;
    private AdEntityCache adEntityCache;

//    private String postImpressionBaseClickUrl;
//    private String postImpressionBaseCSCUrl;
//    private String postImpressionBaseWinApiUrl;
//    private String macroPostImpressionBaseClickUrl;
//    private String trackingEventUrl;

    private String notificationUrlSuffix;
    private String notificationUrlBidderBidPriceMacro;
    private IABCategoriesCache iabCategoriesCache;
    private ServerConfig serverConfig;


    public VamBidResponseCreator(
            String loggerName,
            ServerConfig serverConfig,
            String secretKey,
            int urlVersion,
            String notificationUrlSuffix,
            String notificationUrlBidderBidPriceMacro,
            AdEntityCache adEntityCache,
            IABCategoriesCache iabCategoriesCache
    ) {
        this.logger = LogManager.getLogger(loggerName);
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
        this.adEntityCache = adEntityCache;
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//        this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
//        this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
//        this.postImpressionBaseWinApiUrl = serverConfig.getValueForKey(ServerConfig.WIN_API_URL_PREFIX);
//        this.macroPostImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
//        this.trackingEventUrl = serverConfig.getValueForKey(ServerConfig.trackingEventUrl_PREFIX);

        this.notificationUrlSuffix = notificationUrlSuffix;
        this.notificationUrlBidderBidPriceMacro = notificationUrlBidderBidPriceMacro;
        this.iabCategoriesCache = iabCategoriesCache;
        this.serverConfig = serverConfig;
    }

    private void writeEmptyResponse(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(204);
        httpServletResponse.getOutputStream().close();
    }

    private void populateVamBidResponseUsingValidPayloadAndWriteToExchange(
            Request request,
            Response response,
            HttpServletResponse httpServletResponse,
            int totalProcessingTime
    ) throws IOException, BidResponseException {

        try {
            BidRequestVam bidRequestVam = (BidRequestVam) request.getBidRequest();
            VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = (VamBidRequestParentNodeDTO) bidRequestVam.getBidRequestParentNodeDTO();
            VamRealtimeBidding.VamRequest vamRequest = (VamRealtimeBidding.VamRequest) vamBidRequestParentNodeDTO.getExtensionObject();
            int secure = vamRequest.getSecure();
            String postImpressionBaseClickUrl  = null;
            String postImpressionBaseCSCUrl = null;
            String postImpressionBaseWinApiUrl = null;
            String macroPostImpressionBaseClickUrl = null;
            String trackingEventUrl;
            if(secure == 1) {
                postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX, secure);
                postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX, secure);
                postImpressionBaseWinApiUrl = serverConfig.getValueForKey(ServerConfig.WIN_API_URL_PREFIX, secure);
                macroPostImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX, secure);
                trackingEventUrl = serverConfig.getValueForKey(ServerConfig.trackingEventUrl_PREFIX, secure);
                request.setSecure(true);
            }
            else{
                postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
                postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
                postImpressionBaseWinApiUrl = serverConfig.getValueForKey(ServerConfig.WIN_API_URL_PREFIX);
                macroPostImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
                trackingEventUrl = serverConfig.getValueForKey(ServerConfig.trackingEventUrl_PREFIX);
            }

            Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();
            if (null == impressionIdsToRespondFor) {
                logger.debug("There is no impression ids to respond for inside VamBidResponseCreator");
            }

            Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();

            //只取一个
            for (String impressionId : impressionIdsToRespondFor) {
                Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfoSetForBidRequestImpressionId(impressionId);
                if (null == responseAdInfos || responseAdInfos.size() <= 0) {
                    logger.debug("ResponseAdInfo Size is 0 inside VamBidResponseCreator , writing nofill...");
                    continue;
                }
                //sort and pick the one with highest ecpm value.
                List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
                for (ResponseAdInfo responseAdInfoTemp : responseAdInfos) {
                    if (responseAdInfoTemp.getCreative().getCreativeFormat().equals(CreativeFormat.BANNER)) {
                        list.add(responseAdInfoTemp);
                    } else if (responseAdInfoTemp.getCreative().getCreativeFormat().equals(CreativeFormat.VIDEO)) {
                        list.add(responseAdInfoTemp);
                    }
                }

                if (null == list || list.size() <= 0) {
                    logger.debug("There is no banner to be served inside VamBidResponseCreator , writing nofill...");
                    continue;
                }

                Collections.sort(list, comparator);

                ResponseAdInfo responseAdInfoToUse = list.get(0);
                responseAdInfoToUse = RandomPicker.pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues(responseAdInfoToUse, list, randomPicker);
                Creative creative = responseAdInfoToUse.getCreative();

                Double p = responseAdInfoToUse.getEcpmValue() * 100;
                int maxPrice = p == null ? 0 : p.intValue();
                response.addResponseAdInfoAsFinalForImpressionId(impressionId, responseAdInfoToUse);
                String creativeId = creative.getCreativeGuid();
                if (null != responseAdInfoToUse.getCreativeBanner()) {
                    creativeId = responseAdInfoToUse.getCreativeBanner().getGuid();
                }

                String clickUri = CreativeFormatterUtils.prepareClickUri
                        (
                                logger,
                                request,
                                responseAdInfoToUse,
                                response.getBidderModelId(),
                                urlVersion,
                                request.getInventorySource(),
                                response.getSelectedSiteCategoryId(),
                                secretKey
                        );

                //win_url
                StringBuffer winNotificationURLBuffer = new StringBuffer(postImpressionBaseWinApiUrl);
                winNotificationURLBuffer.append(clickUri);

                String suffixToAdd = notificationUrlSuffix;
                suffixToAdd = suffixToAdd.replace(
                        notificationUrlBidderBidPriceMacro,
                        String.valueOf(responseAdInfoToUse.getEcpmValue())
                );
                winNotificationURLBuffer.append(suffixToAdd);

                String win_url = winNotificationURLBuffer.toString();

                //clk_url
                StringBuffer clickUrl = new StringBuffer(postImpressionBaseClickUrl);
                clickUrl.append(clickUri);
                String clk_url = clickUrl.toString();

                //show_url
                StringBuffer cscBeaconUrl = new StringBuffer(postImpressionBaseCSCUrl);
                cscBeaconUrl.append(clickUri);
                String show_url = cscBeaconUrl.toString();

                AdEntity adEntity = adEntityCache.query(responseAdInfoToUse.getAdId());
                if (null == adEntity) {
                    logger.error("AdEntity not found in cache inside BidRequestResponseCreatorCloudCross adId:{} ", responseAdInfoToUse.getAdId());
                    continue;
                }

                StringBuffer macroClickUrl = new StringBuffer(macroPostImpressionBaseClickUrl);
                macroClickUrl.append(clickUri);

                ExtTracker extTracker = adEntity.getExtTracker();
                List<String> clkTracker = new ArrayList<String>();
                List<String> impTracker = new ArrayList<String>();

                if (extTracker != null && extTracker.getClickTracker() != null) {
                    List<String> extClickTracker = extTracker.getClickTracker();
                    for (String str : extClickTracker) {
                        clkTracker.add(MarkUpHelper.adTagMacroReplace(str, request, responseAdInfoToUse, response, "", macroClickUrl.toString(), extTracker.getClickMacro(), extTracker.getClickMacroQuote(), ""));
                    }
                }

                if (extTracker != null && extTracker.getImpTracker() != null) {
                    List<String> extImpTracker = extTracker.getImpTracker();
                    for (String str : extImpTracker) {
                        impTracker.add(MarkUpHelper.adTagMacroReplace(str, request, responseAdInfoToUse, response, "", macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(), ""));
                    }
                }

                VamRealtimeBidding.VamResponse vamBidResponse = ConvertResponse.convert(
                        vamRequest,
                        creativeId,
                        maxPrice,
                        win_url,
                        clk_url,
                        show_url,
                        clkTracker,
                        impTracker
                );

                ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
                servletOutputStream.write(vamBidResponse.toByteArray());
                servletOutputStream.close();
                break;
            }
        } catch (Exception e) {
            logger.error("IOException inside VamBidResponseCreator ", e);
            writeEmptyResponse(httpServletResponse);
        }

    }


    @Override
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException {
        if (null == dspObjects || dspObjects.length != 3) {
            throw new BidResponseException("Inside VamBidResponseCreator, arguments passed for formatting are null or not Request and Response objects...");
        }

        Object requestObject = dspObjects[0];
        Object responseObject = dspObjects[1];
        Object contextObject = dspObjects[2];

        Request request = (Request) requestObject;
        Response response = (Response) responseObject;
        Context context = (Context) contextObject;

        HttpServletResponse httpServletResponse = (HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY);

        if (!(requestObject instanceof Request) || !(responseObject instanceof Response) || !(contextObject instanceof Context)) {
            logger.error("Request/Response objects are not passed inside VamBidResponseCreator,writing no-fill");
            try {
                writeEmptyResponse(httpServletResponse);
            } catch (IOException ioe) {
                logger.error("IOException inside VamBidResponseCreator ", ioe);
            }
            return null;
        }

        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if (null == impressionIdsToRespondFor) {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorVam");
            try {
                writeEmptyResponse(httpServletResponse);
            } catch (IOException ioe) {
                logger.error("IOException inside VamBidResponseCreator ", ioe);
            }
            return null;
        }

        try {
            populateVamBidResponseUsingValidPayloadAndWriteToExchange(request, response, httpServletResponse, 2);
        } catch (IOException ioe) {
            logger.error("IOException inside VamBidResponseCreator ", ioe);
        }

        return null;
    }

    @Override
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException {
    }

    private String preparExt(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response
    ) throws BidResponseException, IOException {
        String clickUri = CreativeFormatterUtils.prepareClickUri
                (
                        this.logger,
                        request,
                        responseAdInfo,
                        response.getBidderModelId(),
                        urlVersion,
                        request.getInventorySource(),
                        response.getSelectedSiteCategoryId(),
                        this.secretKey, true
                );

        if (null == clickUri)
            throw new BidResponseException("Click URI could not be formed using different attributes like " +
                    "handset,location,bids,version,etc. inside VamBidResponseCreator");


        Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
        String exchangeUserId = null;
        if (null != externalUserIdSet) {
            for (ExternalUserId externalUserId : externalUserIdSet) {
                if (externalUserId.getIdType().equals(ExternalUserIdType.EXCHANGE_CONSUMER_ID))
                    exchangeUserId = externalUserId.toString();
            }
        }
        /**
         * Reusing modify cscurl
         */
        clickUri = new StringBuffer(ApplicationGeneralUtils.modifyCSCURLForUserIds(
                exchangeUserId,
                request.getUserId(),
                clickUri
        )).toString();

        logger.debug("click url is modified to contain exchange and kritter UserId, after modification url:{} ",
                clickUri.toString());

        Base64 base64 = new Base64(0);
        logger.debug("LENGTH OF EXT ############## {} #########", clickUri.getBytes().length);
        return base64.encodeToString(clickUri.getBytes());
    }

}
