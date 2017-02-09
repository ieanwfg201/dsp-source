    package com.kritter.bidreqres.response_creator.inmobi2_3;

import com.kritter.bidreqres.entity.inmobi2_3.*;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.DefaultCurrency;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.ex_int.banner_admarkup.common.BannerAdMarkUp;
import com.kritter.ex_int.native_admarkup.NativeAdMarkUp;
import com.kritter.ex_int.utils.comparator.EcpmValueComparator;
import com.kritter.ex_int.utils.comparator.advdomain.FetchAdvertiserDomain;
import com.kritter.ex_int.utils.comparator.common.ShortArrayToIntegerArray;
import com.kritter.ex_int.utils.picker.RandomPicker;
import com.kritter.ex_int.utils.richmedia.RichMediaAdMarkUp;
import com.kritter.ex_int.utils.richmedia.markuphelper.MarkUpHelper;
import com.kritter.ex_int.video_admarkup.VideoAdMarkUp;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ServerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.*;

/**
 * This class creates and validates response to inmobi two dot three ' bid request.
 */
public class BidRequestResponseCreatorInmobi implements IBidResponseCreator
{
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
    private String macroPostImpressionBaseClickUrl;
    private String trackingEventUrl;
    private ObjectMapper objectMapper;
    private String seatId;

    //template for formatting.
    private static final String CURRENCY = DefaultCurrency.defaultCurrency.getName();
    private static final Random randomPicker = new Random();

    public BidRequestResponseCreatorInmobi(
                                              String loggerName,
                                              ServerConfig serverConfig,
                                              String secretKey,
                                              int urlVersion,
                                              String notificationUrlSuffix,
                                              String notificationUrlBidderBidPriceMacro,
                                              AdEntityCache adEntityCache,
                                              String seatId
                                             )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
        this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
        this.postImpressionBaseWinApiUrl = serverConfig.getValueForKey(ServerConfig.WIN_API_URL_PREFIX);
        this.cdnBaseImageUrl = serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
        this.notificationUrlSuffix = notificationUrlSuffix;
        this.notificationUrlBidderBidPriceMacro = notificationUrlBidderBidPriceMacro;
        this.adEntityCache = adEntityCache;
        this.macroPostImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
        this.trackingEventUrl = serverConfig.getValueForKey(ServerConfig.trackingEventUrl_PREFIX);
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        this.seatId = seatId;
    }


    @Override
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException
    {
        if(null == dspObjects || dspObjects.length != 2)
            throw new BidResponseException("Inside BidRequestResponseCreatorInmobi, arguments passed " +
                                           "for formatting are null or not Request and Response objects...");

        Object requestObject = dspObjects[0];
        Object responseObject = dspObjects[1];

        if(!(requestObject instanceof Request) || !(responseObject instanceof Response))
        {
            logger.debug("Request response objects are null, case of nofill inside : {} ", getClass().getName());
            return null;
        }

        Request request = (Request)requestObject;
        Response response = (Response)responseObject;

        if(null == request.getBidRequest() || !(request.getBidRequest() instanceof BidRequestInmobi))
            throw new BidResponseException("Inside BidRequestResponseCreatorInmobi, IBidRequest " +
                                           "is invalid/null, cannot format response...");

        BidRequestInmobi bidRequestInmboi = (BidRequestInmobi)request.getBidRequest();

        /*For each impression id find response ad info and format response.*/
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if(null == impressionIdsToRespondFor)
        {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorInmobi");
            return null;
        }

        Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();

        BidResponseInmobiDTO bidResponseInmobiDTO = new BidResponseInmobiDTO();
        bidResponseInmobiDTO.setBidderGeneratedUniqueId(bidRequestInmboi.getUniqueInternalRequestId());
        bidResponseInmobiDTO.setBidRequestId(bidRequestInmboi.getUniqueBidRequestIdentifierForAuctioneer());
        bidResponseInmobiDTO.setCurrency(CURRENCY);

        //only one seat bid object...
        BidResponseSeatBidInmobiDTO[] bidResponseSeatBidInmobiArray = new BidResponseSeatBidInmobiDTO[1];

        BidResponseSeatBidInmobiDTO bidResponseSeatBidInmobiDTO = new BidResponseSeatBidInmobiDTO();

        BidResponseBidInmobiDTO[] bidResponseBidinmobiArray = new BidResponseBidInmobiDTO[impressionIdsToRespondFor.size()];
        int counter = 0;
        boolean atleastOneBidAsResponse = false;

        for(String impressionId : impressionIdsToRespondFor)
        {
            Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfoSetForBidRequestImpressionId(impressionId);

            //sort and pick the one with highest ecpm value.
            List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
            list.addAll(responseAdInfos);
            Collections.sort(list,comparator);

            ResponseAdInfo responseAdInfoToUse = list.get(0);
            responseAdInfoToUse = RandomPicker.pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues
                    (responseAdInfoToUse,list, randomPicker);

            BidResponseBidInmobiDTO bidResponseBidInmobiDTO =
                    prepareBidResponseSeatBidInmobi(
                                                        responseAdInfoToUse,
                                                        request,
                                                        response,
                                                        impressionId
                                                      );

            if(null == bidResponseBidInmobiDTO)
            {
                logger.error("BidResponseBidInmobi could not be prepared for bidRequestImpressionId:{}",
                             impressionId);
                continue;
            }

            bidResponseBidinmobiArray[counter] = bidResponseBidInmobiDTO;
            response.addResponseAdInfoAsFinalForImpressionId(impressionId,responseAdInfoToUse);
            counter++;
            atleastOneBidAsResponse = true;
        }

        if(!atleastOneBidAsResponse)
            return null;

        bidResponseSeatBidInmobiDTO.setBidResponseBidEntities(bidResponseBidinmobiArray);
        if(this.seatId != null){
        	bidResponseSeatBidInmobiDTO.setBidderSeatId(this.seatId);
        }
        bidResponseSeatBidInmobiArray[0] = bidResponseSeatBidInmobiDTO;

        bidResponseInmobiDTO.setBidResponseSeatBid(bidResponseSeatBidInmobiArray);

        String payLoad = null;

        try
        {
            payLoad = objectMapper.writeValueAsString(bidResponseInmobiDTO);
        }
        catch (IOException ioe)
        {
            throw new BidResponseException("IOException inside BidRequestResponseCreatorInmobi", ioe);
        }

        BidResponseInmobi bidResponseInmobiToReturn =
                                                   new BidResponseInmobi(
                                                                            bidResponseInmobiDTO.getBidRequestId(),
                                                                            bidResponseInmobiDTO.getBidderGeneratedUniqueId(),
                                                                            payLoad
                                                                           );

        return bidResponseInmobiToReturn;
    }

    @Override
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException
    {

    }

    private BidResponseBidInmobiDTO prepareBidResponseSeatBidInmobi
                                                                       (
                                                                        ResponseAdInfo responseAdInfo,
                                                                        Request request,
                                                                        Response response,
                                                                        String bidRequestImpressionId
                                                                       ) throws BidResponseException
    {
        AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

        if(null == adEntity)
        {
            logger.error("AdEntity not found in cache inside BidRequestResponseCreatorInmobi adId:{} ",
                          responseAdInfo.getAdId());
            return null;
        }


        /********************Prepare bid object and set it into bid response object.********************************/
        StringBuffer winNotificationURLBuffer = new StringBuffer();
        BidResponseBidInmobiDTO bidResponseBidInmobiDTO = new BidResponseBidInmobiDTO();
        bidResponseBidInmobiDTO.setAdId(String.valueOf(responseAdInfo.getAdId()));
        Creative creative = responseAdInfo.getCreative();

        if(creative.getCreativeFormat().equals(CreativeFormat.BANNER)) {
            String adMarkup = prepareBannerHTMLAdMarkup(
                    request,
                    responseAdInfo,
                    response, adEntity.getExtTracker(),
                    winNotificationURLBuffer
            );
            bidResponseBidInmobiDTO.setAdMarkup(appendExternalClickTracking(request,response,responseAdInfo,adEntity.getExtTracker(),adMarkup));
        }
        else if(creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA))
            bidResponseBidInmobiDTO.setAdMarkup(
                    prepareRichmediaAdMarkup(
                            request,
                            responseAdInfo,
                            response,
                            winNotificationURLBuffer,
                            creative.getCreative_macro(),
                            adEntity.getExtTracker()
                    )
            );
        else if(creative.getCreativeFormat().equals(CreativeFormat.Native))
            bidResponseBidInmobiDTO.setAdMarkup(
                    prepareNativeAdMarkup(
                            request,
                            responseAdInfo,
                            response,
                            winNotificationURLBuffer
                    )
            );
        else if(creative.getCreativeFormat().equals(CreativeFormat.VIDEO))
            bidResponseBidInmobiDTO.setAdMarkup(
                    prepareVideoAdMarkup(
                            request,
                            responseAdInfo,
                            response,
                            winNotificationURLBuffer
                    )
            );

        String advertiserDomain[] = null;
        if(null != adEntity.getAdvertiserDomains() && adEntity.getAdvertiserDomains().length > 0)
            advertiserDomain = adEntity.getAdvertiserDomains();
        else
        {
            advertiserDomain = new String[1];
            advertiserDomain[0] = FetchAdvertiserDomain.fetchAdvertiserDomain(adEntity.getLandingUrl());
        }

        if(null == advertiserDomain[0])
        {
            logger.error("Advertiser domain could not be found using adId:{} and landingURL:{} ",
                    adEntity.getAdIncId(),adEntity.getLandingUrl());
            return null;
        }
        bidResponseBidInmobiDTO.setAdvertiserDomains(advertiserDomain);
        bidResponseBidInmobiDTO.setBidId(responseAdInfo.getImpressionId());
        bidResponseBidInmobiDTO.setRequestImpressionId(bidRequestImpressionId);
        bidResponseBidInmobiDTO.setCampaignId(String.valueOf(adEntity.getCampaignIncId()));

        Integer[] creativeAttributes = ShortArrayToIntegerArray.fetchIntegerArrayFromShortArray(creative.getCreativeAttributes());
        if(null == creativeAttributes)
        {
            logger.error("Creative Attributes could not be found using adId:{} and landingURL:{} ",
                    adEntity.getAdIncId(),adEntity.getLandingUrl());
            return null;
        }

        bidResponseBidInmobiDTO.setCreativeAttributes(creativeAttributes);
        bidResponseBidInmobiDTO.setCreativeId(creative.getCreativeGuid());
        StringBuffer creativeImageUrl = new StringBuffer(this.cdnBaseImageUrl);

        /*If the creative is banner then set its resource URL to the bid response*/
        if(null != responseAdInfo.getCreativeBanner())
        {
            creativeImageUrl.append(responseAdInfo.getCreativeBanner().getResourceURI());
            bidResponseBidInmobiDTO.setSampleImageUrl(creativeImageUrl.toString());
        }

        bidResponseBidInmobiDTO.setPrice(responseAdInfo.getEcpmValue().floatValue());
        //prepare nurl ,win notification url.
        bidResponseBidInmobiDTO.setWinNotificationUrl(winNotificationURLBuffer.toString());
        /*******************************Done preparing bid response bid object.********************************/

        return bidResponseBidInmobiDTO;
    }

    private String appendExternalClickTracking(Request request, Response response, ResponseAdInfo responseAdInfo, ExtTracker extTracker, String adMarkup) {
        StringBuilder stringBuilder = new StringBuilder(adMarkup).insert(2, " onclick=\"fireClickTracker()\" target=\"_blank\" ");
        for (int i = 0; i < extTracker.getClickTracker().size(); i++) {
                stringBuilder.append("<img id=\"click" + i + "\" width=\"1\" height=\"1\" />");
        }
        stringBuilder.append("<script type=\"text/javascript\">function fireClickTracker() {");
        for (int i = 0; i < extTracker.getClickTracker().size(); i++) {
            stringBuilder.append("document.getElementById('click" + i + "').src = '"+ MarkUpHelper.adTagMacroReplace(extTracker.getClickTracker().get(i), request, responseAdInfo, response,
                    "", this.macroPostImpressionBaseClickUrl.toString(), extTracker.getClickMacro(), extTracker.getClickMacroQuote(), "")+"';");
        }
        stringBuilder.append(" return false;}</script>");
        return stringBuilder.toString();
    }

    private String prepareRichmediaAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer,
            CreativeMacro creativeMacro,
            ExtTracker extTracker
    ) throws BidResponseException
    {
        return RichMediaAdMarkUp.prepareRichmediaAdMarkup(request, responseAdInfo, response,
                winNotificationURLBuffer, logger, urlVersion, secretKey, postImpressionBaseClickUrl,
                postImpressionBaseCSCUrl, postImpressionBaseWinApiUrl, notificationUrlSuffix,
                notificationUrlBidderBidPriceMacro, null, null, creativeMacro, this.macroPostImpressionBaseClickUrl,
                extTracker);
    }

    private String prepareBannerHTMLAdMarkup(
                                             Request request,
                                             ResponseAdInfo responseAdInfo,
                                             Response response,
                                             ExtTracker extTracker,
                                             StringBuffer winNotificationURLBuffer
                                            ) throws BidResponseException
    {
        return BannerAdMarkUp.prepare(logger, request, response, responseAdInfo, urlVersion, secretKey,
                macroPostImpressionBaseClickUrl, postImpressionBaseWinApiUrl, notificationUrlSuffix,
                notificationUrlBidderBidPriceMacro, postImpressionBaseCSCUrl, cdnBaseImageUrl, false, extTracker,
                winNotificationURLBuffer, null, this.macroPostImpressionBaseClickUrl);
    }
    private String prepareNativeAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer
    ) throws BidResponseException
    {
        return NativeAdMarkUp.prepare(request, responseAdInfo, response, winNotificationURLBuffer,
                this.logger, this.urlVersion, this.secretKey, this.macroPostImpressionBaseClickUrl,
                this.postImpressionBaseWinApiUrl, this.notificationUrlSuffix,
                this.notificationUrlBidderBidPriceMacro, this.postImpressionBaseCSCUrl,
                this.cdnBaseImageUrl);
    }

    private String prepareVideoAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer
    ) throws BidResponseException
    {
        return VideoAdMarkUp.prepare(request, responseAdInfo, response, winNotificationURLBuffer,
                logger, urlVersion, secretKey, macroPostImpressionBaseClickUrl, postImpressionBaseWinApiUrl,
                notificationUrlSuffix, notificationUrlBidderBidPriceMacro, postImpressionBaseCSCUrl,
                cdnBaseImageUrl, trackingEventUrl, null, null,macroPostImpressionBaseClickUrl);
    }
}
