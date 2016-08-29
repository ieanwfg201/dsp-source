package com.kritter.bidreqres.response_creator.mopub2_3;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.bidreqres.entity.mopub2_3.*;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.ex_int.banner_admarkup.common.BannerAdMarkUp;
import com.kritter.ex_int.utils.comparator.EcpmValueComparator;
import com.kritter.ex_int.utils.comparator.advdomain.FetchAdvertiserDomain;
import com.kritter.ex_int.utils.comparator.common.ShortArrayToIntegerArray;
import com.kritter.ex_int.utils.picker.RandomPicker;
import com.kritter.ex_int.utils.richmedia.RichMediaAdMarkUp;
import com.kritter.ex_int.video_admarkup.VideoAdMarkUp;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.caches.iab.index.IABIDIndex;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.DefaultCurrency;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
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
 * This class creates and validates response to mopub's bid request.
 */
public class BidRequestResponseCreatorMopub implements IBidResponseCreator
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
    private IABCategoriesCache iabCategoriesCache;
    private String macroPostImpressionBaseClickUrl;
    private String trackingEventUrl;

    //template for formatting.
    private static final String CURRENCY = DefaultCurrency.defaultCurrency.getName();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random randomPicker = new Random();

    public BidRequestResponseCreatorMopub(
                                          String loggerName,
                                          ServerConfig serverConfig,
                                          String secretKey,
                                          int urlVersion,
                                          String notificationUrlSuffix,
                                          String notificationUrlBidderBidPriceMacro,
                                          AdEntityCache adEntityCache,
                                          IABCategoriesCache iabCategoriesCache
                                         )
    {
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
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException
    {
        if(null == dspObjects || dspObjects.length != 2)
            throw new BidResponseException("Inside BidRequestResponseCreatorMopub, arguments passed " +
                                           "for formatting are null or not Request and Response objects...");

        Object requestObject = dspObjects[0];
        Object responseObject = dspObjects[1];

        if(!(requestObject instanceof Request) || !(responseObject instanceof Response))
            throw new BidResponseException("Inside BidRequestResponseCreatorMopub, arguments passed " +
                                           "for formatting are not Request and Response objects...");

        Request request = (Request)requestObject;
        Response response = (Response)responseObject;

        if(null == request.getBidRequest() || !(request.getBidRequest() instanceof BidRequestMopub))
            throw new BidResponseException("Inside BidRequestResponseCreatorMopub, IBidRequest " +
                                           "is invalid/null, cannot format response...");

        BidRequestMopub bidRequestMopub = (BidRequestMopub)request.getBidRequest();

        /*For each impression id find response ad info and format response.*/
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if(null == impressionIdsToRespondFor)
        {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorMopub");
            return null;
        }

        Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();

        BidResponseMopubDTO bidResponseMopubDTO = new BidResponseMopubDTO();
        bidResponseMopubDTO.setBidderGeneratedUniqueId(bidRequestMopub.getUniqueInternalRequestId());
        bidResponseMopubDTO.setBidRequestId(bidRequestMopub.getUniqueBidRequestIdentifierForAuctioneer());
        bidResponseMopubDTO.setCurrency(CURRENCY);

        //only one seat bid object...
        BidResponseSeatBidMopubDTO[] bidResponseSeatBidMopubArray = new BidResponseSeatBidMopubDTO[1];

        BidResponseSeatBidMopubDTO bidResponseSeatBidMopubDTO = new BidResponseSeatBidMopubDTO();

        BidResponseBidMopubDTO[] bidResponseBidMopubArray = new BidResponseBidMopubDTO[impressionIdsToRespondFor.size()];
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

            BidResponseBidMopubDTO bidResponseBidMopubDTO =
                    prepareBidResponseSeatBidMopub(
                                                        responseAdInfoToUse,
                                                        request,
                                                        response,
                                                        impressionId
                                                      );

            if(null == bidResponseBidMopubDTO)
            {
                logger.error("BidResponseBidMopub could not be prepared for bidRequestImpressionId:{}",
                             impressionId);
                continue;
            }

            bidResponseBidMopubArray[counter] = bidResponseBidMopubDTO;
            response.addResponseAdInfoAsFinalForImpressionId(impressionId,responseAdInfoToUse);
            counter++;
            atleastOneBidAsResponse = true;
        }

        if(!atleastOneBidAsResponse)
            return null;

        bidResponseSeatBidMopubDTO.setBidResponseBidEntities(bidResponseBidMopubArray);
        bidResponseSeatBidMopubArray[0] = bidResponseSeatBidMopubDTO;

        bidResponseMopubDTO.setBidResponseSeatBid(bidResponseSeatBidMopubArray);

        String payLoad = null;

        try
        {
            objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            payLoad = objectMapper.writeValueAsString(bidResponseMopubDTO);
        }
        catch (IOException ioe)
        {
            throw new BidResponseException("IOException inside BidRequestResponseCreatorMopub", ioe);
        }

        BidResponseMopub bidResponseMopubToReturn =
                                                   new BidResponseMopub(
                                                                        bidResponseMopubDTO.getBidRequestId(),
                                                                        bidResponseMopubDTO.getBidderGeneratedUniqueId(),
                                                                        payLoad
                                                                       );

        return bidResponseMopubToReturn;
    }

    @Override
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException
    {

    }

    private BidResponseBidMopubDTO prepareBidResponseSeatBidMopub
                                                               (
                                                                ResponseAdInfo responseAdInfo,
                                                                Request request,
                                                                Response response,
                                                                String bidRequestImpressionId
                                                               ) throws BidResponseException
    {
        AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

        if (null == adEntity)
        {
            logger.error("AdEntity not found in cache inside BidRequestResponseCreatorMopub adId:{} ",
                    responseAdInfo.getAdId());
            return null;
        }


        /********************Prepare bid object and set it into bid response object.********************************/
        StringBuffer winNotificationURLBuffer = new StringBuffer();
        BidResponseBidMopubDTO bidResponseBidMopubDTO = new BidResponseBidMopubDTO();
        bidResponseBidMopubDTO.setAdId(String.valueOf(responseAdInfo.getAdId()));
        Creative creative = responseAdInfo.getCreative();

        if (creative.getCreativeFormat().equals(CreativeFormat.BANNER))
            bidResponseBidMopubDTO.setAdMarkup(
                    prepareBannerHTMLAdMarkup(
                            request,
                            responseAdInfo,
                            response, adEntity.getExtTracker(),
                            winNotificationURLBuffer
                    )
            );
        else if (creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA))
            bidResponseBidMopubDTO.setAdMarkup(
                    prepareRichmediaAdMarkup(
                            request,
                            responseAdInfo,
                            response,
                            winNotificationURLBuffer,
                            creative.getCreative_macro(),
                            adEntity.getExtTracker()
                    )
            );
        else if(creative.getCreativeFormat().equals(CreativeFormat.VIDEO))
            bidResponseBidMopubDTO.setAdMarkup(
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
        else
        {
            advertiserDomain = new String[1];
            advertiserDomain[0] = FetchAdvertiserDomain.fetchAdvertiserDomain(adEntity.getLandingUrl());
        }

        if (null == advertiserDomain[0])
        {
            logger.error("Advertiser domain could not be found using adId:{} and landingURL:{} ",
                    adEntity.getAdIncId(), adEntity.getLandingUrl());
            return null;
        }
        bidResponseBidMopubDTO.setAdvertiserDomains(advertiserDomain);
        bidResponseBidMopubDTO.setBidId(responseAdInfo.getImpressionId());
        bidResponseBidMopubDTO.setRequestImpressionId(bidRequestImpressionId);
        bidResponseBidMopubDTO.setCampaignId(String.valueOf(adEntity.getCampaignIncId()));

        Integer[] creativeAttributes = ShortArrayToIntegerArray.fetchIntegerArrayFromShortArray(creative.getCreativeAttributes());
        if (null == creativeAttributes)
        {
            logger.error("Creative Attributes could not be found using adId:{} and landingURL:{} ",
                    adEntity.getAdIncId(), adEntity.getLandingUrl());
            return null;
        }

        bidResponseBidMopubDTO.setCreativeAttributes(creativeAttributes);
        bidResponseBidMopubDTO.setCreativeId(creative.getCreativeGuid());
        String[] iabCategories = null;
        try
        {
            Short[] iabCategoriesInternalCodes = adEntity.getCategoriesArray();
            if (null != iabCategoriesInternalCodes && iabCategoriesInternalCodes.length > 0)
            {
                iabCategories = new String[iabCategoriesInternalCodes.length];
                int counter = 0;
                for (Short categoryShortCode : iabCategoriesInternalCodes)
                {
                    Set<String> result = iabCategoriesCache.query(new IABIDIndex(categoryShortCode));

                    for(String entry : result)
                    {
                        iabCategories[counter++] = entry;
                        break;
                    }
                }
            }
        }
        catch (UnSupportedOperationException e)
        {
            logger.error("UnSupportedOperationException inside BidRequestResponseCreatorMopub ",e);
        }

        if(null != iabCategories)
            bidResponseBidMopubDTO.setCreativeCategories(iabCategories);

        StringBuffer creativeImageUrl = new StringBuffer(this.cdnBaseImageUrl);

        /*If the creative is banner then set its resource URL to the bid response*/
        if(null != responseAdInfo.getCreativeBanner())
        {
            creativeImageUrl.append(responseAdInfo.getCreativeBanner().getResourceURI());
            bidResponseBidMopubDTO.setSampleImageUrl(creativeImageUrl.toString());
        }

        bidResponseBidMopubDTO.setPrice(responseAdInfo.getEcpmValue().floatValue());

        /***prepare nurl ,win notification url.impression tracker****/

        bidResponseBidMopubDTO.setWinNotificationUrl(winNotificationURLBuffer.toString());

        BidResponseBidExtMopubDTO bidResponseBidExtMopubDTO = new BidResponseBidExtMopubDTO();
        int extraTrackingSize=0;
        if(adEntity.getExtTracker() != null){
            if(adEntity.getExtTracker().getImpTracker() != null && adEntity.getExtTracker().getImpTracker().size()>0){
                extraTrackingSize = adEntity.getExtTracker().getImpTracker().size();
            }
        }
        String impTrackers[] = new String[1+extraTrackingSize];
        impTrackers[0] = fetchImpressionTrackerSameAsCSC(request,responseAdInfo,response);
        if(extraTrackingSize>0){
            int count =1;
            for(String str:adEntity.getExtTracker().getImpTracker()){
                impTrackers[count] = str;
                count++;
            }
        }
        bidResponseBidExtMopubDTO.setImptrackers(impTrackers);
        if(creative.getCreativeFormat() != null && CreativeFormat.VIDEO == creative.getCreativeFormat()){
            VideoProps videoProps = creative.getVideoProps();
            if(videoProps != null){
                VideoBidResponseProtocols videoprotocol = VideoBidResponseProtocols.getEnum(videoProps.getProtocol());
                if(videoprotocol != null){
                    if(videoprotocol == VideoBidResponseProtocols.VAST_2_0_WRAPPER){
                        bidResponseBidExtMopubDTO.setCrtype("VAST 2.0"); 
                    }else if(videoprotocol == VideoBidResponseProtocols.VAST_3_0_WRAPPER){
                        bidResponseBidExtMopubDTO.setCrtype("VAST 3.0"); 
                    }
                }
            }
            bidResponseBidExtMopubDTO.setDuration(videoProps.getDuration()+"");
            Integer[] videoCreativeAttribute = new Integer[1];
            videoCreativeAttribute[0]=6;
            bidResponseBidMopubDTO.setCreativeAttributes(videoCreativeAttribute);
        }
        bidResponseBidMopubDTO.setExtensionObject(bidResponseBidExtMopubDTO);
        /*******************************Done preparing bid response bid object.********************************/

        return bidResponseBidMopubDTO;
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

    private String fetchImpressionTrackerSameAsCSC(
                                                    Request request,
                                                    ResponseAdInfo responseAdInfo,
                                                    Response response
                                                  ) throws BidResponseException
    {
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
                    "handset,location,bids,version,etc. inside BidRequestResponseCreatorMopub");


        StringBuffer cscBeaconUrl = new StringBuffer(this.postImpressionBaseCSCUrl);
        cscBeaconUrl.append(clickUri);

        return cscBeaconUrl.toString();
    }

    private String prepareBannerHTMLAdMarkup(
                                             Request request,
                                             ResponseAdInfo responseAdInfo,
                                             Response response, ExtTracker extTracker,
                                             StringBuffer winNotificationURLBuffer
                                            ) throws BidResponseException
    {
        return BannerAdMarkUp.prepare(logger, request, response, responseAdInfo, urlVersion, secretKey, 
                postImpressionBaseClickUrl, postImpressionBaseWinApiUrl, notificationUrlSuffix,
                notificationUrlBidderBidPriceMacro, postImpressionBaseCSCUrl, cdnBaseImageUrl, false, extTracker,
                winNotificationURLBuffer, null, this.macroPostImpressionBaseClickUrl);
    }
    
    private String prepareVideoAdMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer
    ) throws BidResponseException
    {
        return VideoAdMarkUp.prepare(request, responseAdInfo, response, winNotificationURLBuffer,
                logger, urlVersion, secretKey, postImpressionBaseClickUrl, postImpressionBaseWinApiUrl,
                notificationUrlSuffix, notificationUrlBidderBidPriceMacro, postImpressionBaseCSCUrl,
                cdnBaseImageUrl, trackingEventUrl, null, null,macroPostImpressionBaseClickUrl);
    }
}
