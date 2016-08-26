package com.kritter.bidreqres.response_creator.cloudcross4_4_4;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.bidreqres.entity.cloudcross4_4_4.*;
import com.kritter.bidrequest.entity.IBidResponse;
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
        bidResponseCloudCrossDTO.setBidRequestId(bidRequestCloudCross.getUniqueBidRequestIdentifierForAuctioneer());
        bidResponseCloudCrossDTO.setCurrency(CURRENCY);

        //only one seat bid object...
        BidResponseSeatBidCloudCrossDTO[] bidResponseSeatBidCloudCrossArray = new BidResponseSeatBidCloudCrossDTO[1];

        BidResponseSeatBidCloudCrossDTO bidResponseSeatBidCloudCrossDTO = new BidResponseSeatBidCloudCrossDTO();

        BidResponseBidCloudCrossDTO[] bidResponseBidCloudCrossArray = new BidResponseBidCloudCrossDTO[impressionIdsToRespondFor.size()];
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

            BidResponseBidCloudCrossDTO bidResponseBidCloudCrossDTO =
                    prepareBidResponseSeatBidCloudCross(
                            responseAdInfoToUse,
                            request,
                            response,
                            impressionId
                    );

            if (null == bidResponseBidCloudCrossDTO) {
                logger.error("BidResponseBidCloudCross could not be prepared for bidRequestImpressionId:{}",
                        impressionId);
                continue;
            }

            bidResponseBidCloudCrossArray[counter] = bidResponseBidCloudCrossDTO;
            response.addResponseAdInfoAsFinalForImpressionId(impressionId, responseAdInfoToUse);
            counter++;
            atleastOneBidAsResponse = true;
        }

        if (!atleastOneBidAsResponse)
            return null;

        bidResponseSeatBidCloudCrossDTO.setBidResponseBidEntities(bidResponseBidCloudCrossArray);
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


        /********************Prepare bid object and set it into bid response object.********************************/
        StringBuffer winNotificationURLBuffer = new StringBuffer();
        BidResponseBidCloudCrossDTO bidResponseBidCloudCrossDTO = new BidResponseBidCloudCrossDTO();
        bidResponseBidCloudCrossDTO.setAdId(String.valueOf(responseAdInfo.getAdId()));
        Creative creative = responseAdInfo.getCreative();

        if (creative.getCreativeFormat().equals(CreativeFormat.BANNER))
            bidResponseBidCloudCrossDTO.setAdMarkup(
                    prepareBannerHTMLAdMarkup(
                            request,
                            responseAdInfo,
                            response, adEntity.getExtTracker(),
                            winNotificationURLBuffer
                    )
            );
        else if (creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA))
            bidResponseBidCloudCrossDTO.setAdMarkup(
                    prepareRichmediaAdMarkup(
                            request,
                            responseAdInfo,
                            response,
                            winNotificationURLBuffer,
                            creative.getCreative_macro(),
                            adEntity.getExtTracker()
                    )
            );
        else if (creative.getCreativeFormat().equals(CreativeFormat.VIDEO))
            bidResponseBidCloudCrossDTO.setAdMarkup(
                    prepareVideoAdMarkup(
                            request,
                            responseAdInfo,
                            response,
                            winNotificationURLBuffer
                    )
            );


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
        bidResponseBidCloudCrossDTO.setAdvertiserDomains(advertiserDomain);
        bidResponseBidCloudCrossDTO.setBidId(responseAdInfo.getImpressionId());
        bidResponseBidCloudCrossDTO.setRequestImpressionId(bidRequestImpressionId);
        bidResponseBidCloudCrossDTO.setCampaignId(String.valueOf(adEntity.getCampaignIncId()));

        Integer[] creativeAttributes = fetchIntegerArrayFromShortArray(creative.getCreativeAttributes());
        if (null == creativeAttributes) {
            logger.error("Creative Attributes could not be found using adId:{} and landingURL:{} ",
                    adEntity.getAdIncId(), adEntity.getLandingUrl());
            return null;
        }

        bidResponseBidCloudCrossDTO.setCreativeAttributes(creativeAttributes);
        bidResponseBidCloudCrossDTO.setCreativeId(creative.getCreativeGuid());
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

        StringBuffer creativeImageUrl = new StringBuffer(this.cdnBaseImageUrl);

        /*If the creative is banner then set its resource URL to the bid response*/
        if (null != responseAdInfo.getCreativeBanner()) {
            creativeImageUrl.append(responseAdInfo.getCreativeBanner().getResourceURI());
            bidResponseBidCloudCrossDTO.setSampleImageUrl(creativeImageUrl.toString());
        }

        bidResponseBidCloudCrossDTO.setPrice(responseAdInfo.getEcpmValue().floatValue());

        /***prepare nurl ,win notification url.impression tracker****/

        bidResponseBidCloudCrossDTO.setWinNotificationUrl(winNotificationURLBuffer.toString());

        BidResponseBidExtCloudCrossDTO bidResponseBidExtCloudCrossDTO = new BidResponseBidExtCloudCrossDTO();
        int extraTrackingSize = 0;
        if (adEntity.getExtTracker() != null) {
            if (adEntity.getExtTracker().getImpTracker() != null && adEntity.getExtTracker().getImpTracker().size() > 0) {
                extraTrackingSize = adEntity.getExtTracker().getImpTracker().size();
            }
        }
        String impTrackers[] = new String[1 + extraTrackingSize];
        impTrackers[0] = fetchImpressionTrackerSameAsCSC(request, responseAdInfo, response);
        if (extraTrackingSize > 0) {
            int count = 1;
            for (String str : adEntity.getExtTracker().getImpTracker()) {
                impTrackers[count] = str;
                count++;
            }
        }
        bidResponseBidExtCloudCrossDTO.setImptrackers(impTrackers);
        if (creative.getCreativeFormat() != null && CreativeFormat.VIDEO == creative.getCreativeFormat()) {
            VideoProps videoProps = creative.getVideoProps();
            if (videoProps != null) {
                VideoBidResponseProtocols videoprotocol = VideoBidResponseProtocols.getEnum(videoProps.getProtocol());
                if (videoprotocol != null) {
                    if (videoprotocol == VideoBidResponseProtocols.VAST_2_0_WRAPPER) {
                        bidResponseBidExtCloudCrossDTO.setCrtype("VAST 2.0");
                    } else if (videoprotocol == VideoBidResponseProtocols.VAST_3_0_WRAPPER) {
                        bidResponseBidExtCloudCrossDTO.setCrtype("VAST 3.0");
                    }
                }
            }
            bidResponseBidExtCloudCrossDTO.setDuration(videoProps.getDuration() + "");
            Integer[] videoCreativeAttribute = new Integer[1];
            videoCreativeAttribute[0] = 6;
            bidResponseBidCloudCrossDTO.setCreativeAttributes(videoCreativeAttribute);
        }
        bidResponseBidCloudCrossDTO.setExtensionObject(bidResponseBidExtCloudCrossDTO);
        /*******************************Done preparing bid response bid object.********************************/

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
                winNotificationURLBuffer, null);
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
