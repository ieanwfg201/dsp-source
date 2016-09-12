package com.kritter.bidreqres.response_creator.cloudcross4_4_4;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.bidreqres.entity.cloudcross4_4_4.*;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.caches.iab.index.IABIDIndex;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.ex_int.banner_admarkup.common.BannerAdMarkUp;
import com.kritter.ex_int.utils.richmedia.RichMediaAdMarkUp;
import com.kritter.ex_int.utils.richmedia.markuphelper.MarkUpHelper;
import com.kritter.ex_int.video_admarkup.VideoAdMarkUp;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ServerConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * This class creates and validates response to CloudCross's bid request.
 */
public class BidRequestResponseCreatorCloudCross implements IBidResponseCreator {
    private Logger logger;
    private String postImpressionBaseClickUrl;
    private String postImpressionBaseCSCUrl;
    private String postImpressionBaseWinApiUrl;
    private String cdnBaseImageUrl;
    private String secretKey;
    private int urlVersion;
    private String notificationUrlSuffix;
    private String notificationUrlBidderBidPriceMacro;
    private AdEntityCache adEntityCache;
    private IABCategoriesCache iabCategoriesCache;
    private String macroPostImpressionBaseClickUrl;
    private String trackingEventUrl;

    //template for formatting.
    private static final String CURRENCY = "USD";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random randomPicker = new Random();

    public BidRequestResponseCreatorCloudCross(
            String loggerName,
            ServerConfig serverConfig,
            String secretKey,
            int urlVersion,
            String notificationUrlSuffix,
            String notificationUrlBidderBidPriceMacro,
            AdEntityCache adEntityCache,
            IABCategoriesCache iabCategoriesCache
    ) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
        this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
        this.postImpressionBaseWinApiUrl = serverConfig.getValueForKey(ServerConfig.WIN_API_URL_PREFIX);
        this.cdnBaseImageUrl = serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
        this.notificationUrlSuffix = notificationUrlSuffix;
        this.notificationUrlBidderBidPriceMacro = notificationUrlBidderBidPriceMacro;
        this.adEntityCache = adEntityCache;
        this.iabCategoriesCache = iabCategoriesCache;
        this.macroPostImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
        this.trackingEventUrl = serverConfig.getValueForKey(ServerConfig.trackingEventUrl_PREFIX);
    }


    @Override
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException {
        if (null == dspObjects || dspObjects.length != 2)
            throw new BidResponseException("Inside BidRequestResponseCreatorCloudCross, arguments passed " +
                    "for formatting are null or not Request and Response objects...");

        Object requestObject = dspObjects[0];
        Object responseObject = dspObjects[1];

        if (!(requestObject instanceof Request) || !(responseObject instanceof Response))
            throw new BidResponseException("Inside BidRequestResponseCreatorCloudCross, arguments passed " +
                    "for formatting are not Request and Response objects...");

        Request request = (Request) requestObject;
        Response response = (Response) responseObject;

        if (null == request.getBidRequest() || !(request.getBidRequest() instanceof BidRequestCloudCross))
            throw new BidResponseException("Inside BidRequestResponseCreatorCloudCross, IBidRequest " +
                    "is invalid/null, cannot format response...");

        BidRequestCloudCross bidRequestCloudCross = (BidRequestCloudCross) request.getBidRequest();

        /*For each impression id find response ad info and format response.*/
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if (null == impressionIdsToRespondFor) {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorCloudCross");
            return null;
        }

        Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();

        BidResponseCloudCrossDTO bidResponseCloudCrossDTO = new BidResponseCloudCrossDTO();
        bidResponseCloudCrossDTO.setBidderGeneratedUniqueId(bidRequestCloudCross.getUniqueInternalRequestId());
        bidResponseCloudCrossDTO.setBidRequestId(bidRequestCloudCross.getCloudCrossBidRequestParentNodeDTO().getBidRequestId());

        //only one seat bid object...
        BidResponseSeatBidCloudCrossDTO[] bidResponseSeatBidCloudCrossArray = new BidResponseSeatBidCloudCrossDTO[1];

        BidResponseSeatBidCloudCrossDTO bidResponseSeatBidCloudCrossDTO = new BidResponseSeatBidCloudCrossDTO();

        BidResponseBidCloudCrossDTO bidResponseBidCloudCrossDTO = new BidResponseBidCloudCrossDTO();
        int counter = 0;
        boolean atleastOneBidAsResponse = false;

        for (String impressionId : impressionIdsToRespondFor) {
            Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfoSetForBidRequestImpressionId(impressionId);

            //sort and pick the one with highest ecpm value.
            List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
            list.addAll(responseAdInfos);
            Collections.sort(list, comparator);

            ResponseAdInfo responseAdInfoToUse = list.get(0);
            responseAdInfoToUse = pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues
                    (responseAdInfoToUse, list);

            BidResponseBidCloudCrossDTO bidResponseBidCloudCross =
                    prepareBidResponseSeatBidCloudCross(
                            responseAdInfoToUse,
                            request,
                            response,
                            impressionId
                    );

            if (null == bidResponseBidCloudCross) {
                logger.error("BidResponseBidCloudCross could not be prepared for bidRequestImpressionId:{}",
                        impressionId);
                continue;
            } else {
                bidResponseBidCloudCrossDTO = bidResponseBidCloudCross;
                response.addResponseAdInfoAsFinalForImpressionId(impressionId, responseAdInfoToUse);
                counter++;
                atleastOneBidAsResponse = true;
                break;
            }
        }

        if (!atleastOneBidAsResponse)
            return null;

        bidResponseSeatBidCloudCrossDTO.setBidResponseBidEntities(bidResponseBidCloudCrossDTO);
        bidResponseSeatBidCloudCrossArray[0] = bidResponseSeatBidCloudCrossDTO;

        bidResponseCloudCrossDTO.setBidResponseSeatBid(bidResponseSeatBidCloudCrossArray);

        String payLoad = null;

        try {
            objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            payLoad = objectMapper.writeValueAsString(bidResponseCloudCrossDTO);
        } catch (IOException ioe) {
            throw new BidResponseException("IOException inside BidRequestResponseCreatorCloudCross", ioe);
        }

        BidResponseCloudCross bidResponseCloudCrossToReturn =
                new BidResponseCloudCross(
                        bidResponseCloudCrossDTO.getBidRequestId(),
                        bidResponseCloudCrossDTO.getBidderGeneratedUniqueId(),
                        payLoad
                );

        return bidResponseCloudCrossToReturn;
    }

    @Override
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException {

    }

    private BidResponseBidCloudCrossDTO prepareBidResponseSeatBidCloudCross
            (
                    ResponseAdInfo responseAdInfo,
                    Request request,
                    Response response,
                    String bidRequestImpressionId
            ) throws BidResponseException {
        AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

        if (null == adEntity) {
            logger.error("AdEntity not found in cache inside BidRequestResponseCreatorCloudCross adId:{} ",
                    responseAdInfo.getAdId());
            return null;
        }
        String clickUri = CreativeFormatterUtils.prepareClickUri
                (
                        logger,
                        request,
                        responseAdInfo,
                        response.getBidderModelId(),
                        urlVersion,
                        request.getInventorySource(),
                        response.getSelectedSiteCategoryId(),
                        secretKey
                );

        /********************Prepare bid object and set it into bid response object.********************************/
        StringBuffer winNotificationURLBuffer = new StringBuffer();
        BidResponseBidCloudCrossDTO bidResponseBidCloudCrossDTO = new BidResponseBidCloudCrossDTO();
        // id 必须 string 投标对象唯⼀ID(DSPID_广告 ID)
        bidResponseBidCloudCrossDTO.setBidId(responseAdInfo.getImpressionId());
//        impid 必须 string 对应的曝光 ID
        bidResponseBidCloudCrossDTO.setRequestImpressionId(bidRequestImpressionId);
//        price 必须 integer 竞投价格,单位为人民币分
        bidResponseBidCloudCrossDTO.setPrice(responseAdInfo.getEcpmValue().floatValue());
//        curl 可选 [string] 点击监测地址 这里处理第三方的监播地址
        StringBuffer macroClickUrl = new StringBuffer(macroPostImpressionBaseClickUrl);
        macroClickUrl.append(clickUri);
        List<String> clkTracker = new ArrayList<String>();
        ExtTracker extTracker = adEntity.getExtTracker();
        List<String> extClickTracker = null;
        if (extTracker != null)
            extClickTracker = extTracker.getClickTracker();
        if (extTracker != null && extClickTracker != null) {
            for (String str : extClickTracker) {
                clkTracker.add(MarkUpHelper.adTagMacroReplace(str, request, responseAdInfo, response, "",
                        macroClickUrl.toString(), extTracker.getClickMacro(), extTracker.getClickMacroQuote(), ""));
            }
        }
        bidResponseBidCloudCrossDTO.setCurl(clkTracker);
//        点击监播是通过imp的重定向
//        adid 可选 string 在竞价方胜出时引用广告 id
        bidResponseBidCloudCrossDTO.setAdId(String.valueOf(responseAdInfo.getAdId()));
//        nurl 必须 string 竞价成功后通知 url
        winNotificationURLBuffer.append(postImpressionBaseWinApiUrl);
        winNotificationURLBuffer.append(clickUri);
        String suffixToAdd = notificationUrlSuffix;
        suffixToAdd = suffixToAdd.replace(
                notificationUrlBidderBidPriceMacro,
                String.valueOf(responseAdInfo.getEcpmValue())
        );
        winNotificationURLBuffer.append(suffixToAdd);
        bidResponseBidCloudCrossDTO.setWinNotificationUrl(winNotificationURLBuffer.toString());
//        adm 必须 string 广告物料 URL（素材 URL 地址）
        StringBuilder creativeUrl = new StringBuilder();
        creativeUrl = creativeUrl.append(cdnBaseImageUrl).append(responseAdInfo.getCreativeBanner().getResourceURI());
        bidResponseBidCloudCrossDTO.setAdMarkup(creativeUrl.toString());
//        adomain 可选 string 广告主顶层或者主域名
        String advertiserDomain[] = null;
        if (null != adEntity.getAdvertiserDomains() && adEntity.getAdvertiserDomains().length > 0)
            advertiserDomain = adEntity.getAdvertiserDomains();
        else {
            advertiserDomain = new String[1];
            advertiserDomain[0] = fetchAdvertiserDomain(adEntity.getLandingUrl());
        }

        if (null == advertiserDomain[0]) {
            logger.error("Advertiser domain could not be found using adId:{} and landingURL:{} ",
                    adEntity.getAdIncId(), adEntity.getLandingUrl());
            return null;
        }
        bidResponseBidCloudCrossDTO.setAdvertiserDomains(advertiserDomain[0]);
//        iurl 可选 [string] 曝光监测地址
        List<String> impTracker = new ArrayList<String>();
        List<String> extImpTracker = null;
        if (extTracker != null)
            extImpTracker = extTracker.getImpTracker();
        if (extTracker != null && extImpTracker != null) {
            for (String str : extImpTracker) {
                impTracker.add(MarkUpHelper.adTagMacroReplace(str, request, responseAdInfo, response, "",
                        macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(), ""));
            }
        }
        StringBuffer cscBeaconUrl = new StringBuffer(postImpressionBaseCSCUrl);
        cscBeaconUrl.append(clickUri);
        impTracker.add(cscBeaconUrl.toString());
        bidResponseBidCloudCrossDTO.setSampleImageUrl(impTracker);
//        cid 可选 string 投放 id
        bidResponseBidCloudCrossDTO.setCampaignId(String.valueOf(adEntity.getCampaignIncId()));
//        crid 可选 string 创意 id
        Creative creative = responseAdInfo.getCreative();
        bidResponseBidCloudCrossDTO.setCreativeId(creative.getCreativeGuid());
//        adurl 可选 string 广告点击跳转链接，可以支持重定向
        StringBuffer clickUrl = new StringBuffer(postImpressionBaseClickUrl);
        clickUrl.append(clickUri);
        bidResponseBidCloudCrossDTO.setAdurl(clickUrl.toString());
//        cat 可选 int 创意行业分类
        String[] iabCategories = null;
        try {
            Short[] iabCategoriesInternalCodes = adEntity.getCategoriesArray();
            if (null != iabCategoriesInternalCodes && iabCategoriesInternalCodes.length > 0) {
                iabCategories = new String[iabCategoriesInternalCodes.length];
                int counter = 0;
                for (Short categoryShortCode : iabCategoriesInternalCodes) {
                    Set<String> result = iabCategoriesCache.query(new IABIDIndex(categoryShortCode));

                    for (String entry : result) {
                        iabCategories[counter++] = entry;
                        break;
                    }
                }
            }
        } catch (UnSupportedOperationException e) {
            logger.error("UnSupportedOperationException inside BidRequestResponseCreatorCloudCross ", e);
        }

        if (null != iabCategories)
            bidResponseBidCloudCrossDTO.setCreativeCategories(iabCategories);
//        casize 可选 string 广告尺寸
        BidRequestCloudCross bidRequestCloudCross = (BidRequestCloudCross) request.getBidRequest();
        CloudCrossBidRequestImpressionDTO impressionDTO = bidRequestCloudCross.getCloudCrossBidRequestParentNodeDTO().getCloudCrossBidRequestImpressionDTOs()[0];
        BidRequestImpressionBannerObjectDTO bannerObject = impressionDTO.getBidRequestImpressionBannerObject();
        bidResponseBidCloudCrossDTO.setCasize(Integer.toString(bannerObject.getBannerWidthInPixels()) + "*" + Integer.toString(bannerObject.getBannerHeightInPixels()));
        return bidResponseBidCloudCrossDTO;
    }

    private String prepareRichmediaAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer,
            CreativeMacro creativeMacro,
            ExtTracker extTracker
    ) throws BidResponseException {
        return RichMediaAdMarkUp.prepareRichmediaAdMarkup(request, responseAdInfo, response,
                winNotificationURLBuffer, logger, urlVersion, secretKey, postImpressionBaseClickUrl,
                postImpressionBaseCSCUrl, postImpressionBaseWinApiUrl, notificationUrlSuffix,
                notificationUrlBidderBidPriceMacro, null, null, creativeMacro, this.macroPostImpressionBaseClickUrl,
                extTracker);
    }

    private String fetchImpressionTrackerSameAsCSC(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response
    ) throws BidResponseException {
        String clickUri = CreativeFormatterUtils.prepareClickUri
                (
                        this.logger,
                        request,
                        responseAdInfo,
                        response.getBidderModelId(),
                        urlVersion,
                        request.getInventorySource(),
                        response.getSelectedSiteCategoryId(),
                        this.secretKey
                );

        if (null == clickUri)
            throw new BidResponseException("Click URI could not be formed using different attributes like " +
                    "handset,location,bids,version,etc. inside BidRequestResponseCreatorCloudCross");


        StringBuffer cscBeaconUrl = new StringBuffer(this.postImpressionBaseCSCUrl);
        cscBeaconUrl.append(clickUri);

        return cscBeaconUrl.toString();
    }

    private String prepareBannerHTMLAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response, ExtTracker extTracker,
            StringBuffer winNotificationURLBuffer
    ) throws BidResponseException {
        return BannerAdMarkUp.prepare(logger, request, response, responseAdInfo, urlVersion, secretKey,
                postImpressionBaseClickUrl, postImpressionBaseWinApiUrl, notificationUrlSuffix,
                notificationUrlBidderBidPriceMacro, postImpressionBaseCSCUrl, cdnBaseImageUrl, false, extTracker,
                winNotificationURLBuffer, null, macroPostImpressionBaseClickUrl);
    }

    private String prepareVideoAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer
    ) throws BidResponseException {
        return VideoAdMarkUp.prepare(request, responseAdInfo, response, winNotificationURLBuffer,
                logger, urlVersion, secretKey, macroPostImpressionBaseClickUrl, postImpressionBaseWinApiUrl,
                notificationUrlSuffix, notificationUrlBidderBidPriceMacro, postImpressionBaseCSCUrl,
                cdnBaseImageUrl, trackingEventUrl, null, null, macroPostImpressionBaseClickUrl);
    }

    private static class EcpmValueComparator implements Comparator<ResponseAdInfo> {
        @Override
        public int compare(
                ResponseAdInfo responseAdInfoFirst,
                ResponseAdInfo responseAdInfoSecond
        ) {

            if (responseAdInfoFirst.getEcpmValue() > responseAdInfoSecond.getEcpmValue())
                return -1;
            else if (responseAdInfoFirst.getEcpmValue() < responseAdInfoSecond.getEcpmValue())
                return 1;
            else return 0;
        }
    }

    private static String fetchAdvertiserDomain(String landingUrl) {
        if (null == landingUrl)
            return null;

        URL uri = null;

        try {
            uri = new URL(landingUrl);
        } catch (MalformedURLException mue) {
            return null;
        }

        return uri.getHost();
    }

    private Integer[] fetchIntegerArrayFromShortArray(Short[] array) {
        if (null == array || array.length <= 0)
            return null;

        Integer[] dest = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            dest[i] = array[i].intValue();
        }

        return dest;
    }

    private ResponseAdInfo pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues
            (
                    ResponseAdInfo responseAdInfoHighestEcpm,
                    List<ResponseAdInfo> responseAdInfoList
            ) {
        int sameHighestEcpmCount = 1;

        for (int i = 1; i < responseAdInfoList.size(); i++) {
            if (responseAdInfoHighestEcpm.getEcpmValue().compareTo(responseAdInfoList.get(i).getEcpmValue()) == 0) {
                sameHighestEcpmCount += 1;
            }
        }

        int index = randomPicker.nextInt(sameHighestEcpmCount);

        if (index >= 0)
            return responseAdInfoList.get(index);

        return responseAdInfoList.get(0);
    }
}
