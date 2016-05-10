package com.kritter.adserving.shortlisting.core;

import com.kritter.entity.reqres.entity.AdExchangeInfo;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestParentNodeDTO;
import com.kritter.common.caches.slot_size_cache.CreativeSlotSizeCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.CreativeFormat;
import com.kritter.serving.demand.cache.*;
import com.kritter.serving.demand.entity.*;
import com.kritter.utils.common.SetUtils;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This is the base class for shortlisting ads after the run of
 * common AdTargetingMatcher, this will have ads being filtered
 * out on creative basis, price floor, etc.
 * If some exchange requires very specific implementation on
 * shortlisting or any other logic then this class can be
 * extended to override specific functions.
 */
public class CreativeAndFloorMatchingRTBExchangeTwoDotOne implements CreativeAndFloorMatchingRTBExchange
{
    private Logger logger;
    private CreativeBannerCache creativeBannerCache;
    private CreativeCache creativeCache;
    private AdEntityCache adEntityCache;
    private CreativeSlotCache creativeSlotCache;
    private CampaignCache campaignCache;
    private CreativeSlotSizeCache creativeSlotSizeCache;
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    private Comparator<CreativeBanner> comparator = null;
    private static final short BANNER_CREATIVE_TYPE = (short)2;
    private static final short RICHMEDIA_CREATIVE_TYPE = (short)3;

    public CreativeAndFloorMatchingRTBExchangeTwoDotOne(
                                                        String loggerName,
                                                        CreativeBannerCache creativeBannerCache,
                                                        CreativeCache creativeCache,
                                                        AdEntityCache adEntityCache,
                                                        CreativeSlotCache creativeSlotCache,
                                                        CampaignCache campaignCache,
                                                        CreativeSlotSizeCache creativeSlotSizeCache
                                                       )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.creativeBannerCache = creativeBannerCache;
        this.creativeCache = creativeCache;
        this.adEntityCache = adEntityCache;
        this.creativeSlotCache = creativeSlotCache;
        this.campaignCache = campaignCache;
        this.creativeSlotSizeCache = creativeSlotSizeCache;
        this.comparator = new BannerSizeComparator();
    }

    /**
     * This function takes input shortlisted ad ids,and try to match them with incoming
     * bid request impressions in context of height,width,creative attributes,creative
     * types,bidfloor.
     * Only banner ads and richmedia ads are supported for now.Video ads would come
     * later on.
     * @param request
     * @param response
     */
    public void processAdUnitsForEachBidRequestImpression(
                                                          Request request,
                                                          Response response
                                                         ) throws Exception
    {
        logger.debug("Inside processAdUnitsForEachBidRequestImpression of CreativeAndFloorMatchingRTBExchangeTwoDotOne");

        if(null == response.getResponseAdInfo() || response.getResponseAdInfo().size() == 0)
        {
            logger.debug("No ResponseAdInfo inside CreativeAndFloorMatchingRTBExchangeTwoDotOne...");
            return;
        }

        if(null == request.getBidRequest() || null==request.getBidRequest().getBidRequestParentNodeDTO())
        {
            logger.error("BidRequest or BidRequestParentNode is null inside CreativeAndFloorMatchingRTBExchangeTwoDotOne ");
            return;
        }


        BidRequestParentNodeDTO bidRequestParentNodeDTO = (BidRequestParentNodeDTO)request.
                                                                    getBidRequest().getBidRequestParentNodeDTO();
        BidRequestImpressionDTO[] bidRequestImpressionDTOs = bidRequestParentNodeDTO.getBidRequestImpressionArray();

        /**
         * For each impression find the matching ad.
         * Ad can not be text ad.As of now only banner impressions are allowed.
         * TODO : Video ads will come later on.
         **/

        List<Short> requestedSlotIdList = new ArrayList<Short>();
        List<Integer> requestedSlotWidths = new ArrayList<Integer>();
        List<Integer> requestedSlotHeights = new ArrayList<Integer>();


        boolean creativeAttributesMatchAtleastOnce = false;
        boolean creativeFoundForRequestedSlot = false;
        boolean floorPriceMet = false;

        for(BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs)
        {
            BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO =
                                                    bidRequestImpressionDTO.getBidRequestImpressionBannerObject();

            if(null == bidRequestImpressionBannerObjectDTO)
            {
                logger.error("BidRequestImpressionBannerObjectDTO is null inside " +
                             "AdShortlistingRTBExchangeTwoDotOne, cannot process " +
                             "request for this impressionId: {} ",
                             bidRequestImpressionDTO.getBidRequestImpressionId());
                continue;
            }

            Integer width = bidRequestImpressionBannerObjectDTO.getBannerWidthInPixels();
            Integer height = bidRequestImpressionBannerObjectDTO.getBannerHeightInPixels();

            //use width and height to find requested slot id.
            short requestedSlotId = -1;
            if(null != width && null != height)
            {
                requestedSlotId = this.creativeSlotSizeCache.fetchClosestSlotIdForSize(width,height);
                if(-1 != requestedSlotId){
                    requestedSlotIdList.add(requestedSlotId);
                    requestedSlotWidths.add(width);
                    requestedSlotHeights.add(height);
                }
                    
            }

            //if blocked creative types contain banner type ad then dont serve anything.
            Short[] blockedCreativeTypes = bidRequestImpressionBannerObjectDTO.getBlockedCreativeTypes();

            Set<Short> blockedCreativeTypeSet = null;
            if(null != blockedCreativeTypes)
                blockedCreativeTypeSet = new HashSet<Short>(Arrays.asList(blockedCreativeTypes));

            boolean isBannerAllowed = true;
            boolean isRichmediaAllowed = true;

            if(
               null != blockedCreativeTypeSet     &&
               blockedCreativeTypeSet.contains(BANNER_CREATIVE_TYPE)
              )
            {
                ReqLog.debugWithDebug(logger, request, "Banner is not allowed for this impression.Creative types block it.");
                isBannerAllowed = false;
            }

            if(!request.getSite().isRichMediaAllowed())
            {
                ReqLog.debugWithDebug(logger, request, "Richmedia is not allowed for this impression.Site does not allow it.");
                isRichmediaAllowed = false;
            }

            if(!request.getHandsetMasterData().isDeviceJavascriptCompatible())
            {
                ReqLog.debugWithDebug(logger, request, "Richmedia is not allowed for this impression.Requesting handset is not javascript compatible.");
                isRichmediaAllowed = false;
            }

            if(
               null != blockedCreativeTypeSet     &&
               blockedCreativeTypeSet.contains(RICHMEDIA_CREATIVE_TYPE)
              )
            {
                ReqLog.debugWithDebug(logger, request, "Richmedia is not allowed for this impression.Creative types do not allow richmedia.");
                isRichmediaAllowed = false;
            }

            if(!isBannerAllowed && !isRichmediaAllowed)
            {
                ReqLog.errorWithDebug(logger, request, "BidRequestImpression does not allow banner ads or richmedia inside" +
                        "AdShortlistingRTBExchangeTwoDotOne, cannot process " +
                        "request for this impressionId: {} ",
                        bidRequestImpressionDTO.getBidRequestImpressionId());
                continue;
            }

            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo())
            {
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

                if(null == adEntity)
                {
                    logger.error("AdEntity not found in cache,FATAL error!!! for adId: {} " +
                                 responseAdInfo.getAdId());
                    continue;
                }

                Creative creative = creativeCache.query(adEntity.getCreativeId());

                if(null == creative)
                {
                    logger.error("Creative null in cache,FATAL error!!! for creative id: " + adEntity.getCreativeId());
                    continue;
                }

                //if not banner and neither richmedia then skip adunit.
                if(
                   !creative.getCreativeFormat().equals(CreativeFormat.BANNER)    &&
                   !creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)
                  )
                {
                    logger.error("Creative format is not banner or richmedia inside AdShortlistingRTBExchangeTwoDotOne, " +
                                 "skipping adId: {} ", adEntity.getAdGuid());
                    continue;
                }

                /******************Check if the incoming impression allows creative attributes************************/

                Set<Short> impressionCreativeAttributes =
                       (
                        null == bidRequestImpressionBannerObjectDTO.getBlockedCreativeAttributes() ?
                        new HashSet<Short>() :
                        new HashSet<Short>
                                (Arrays.asList(bidRequestImpressionBannerObjectDTO.getBlockedCreativeAttributes()))
                       );
                if(impressionCreativeAttributes != null){
                    request.getSite().setCreativeAttributesForInclusionExclusion(bidRequestImpressionBannerObjectDTO.getBlockedCreativeAttributes());
                }
                Set<Short> adCreativeAttributes =
                       (
                        null == creative.getCreativeAttributes() ?
                        new HashSet<Short>() :
                        new HashSet<Short>
                                (Arrays.asList(creative.getCreativeAttributes()))
                       );

                Set<Short> resultingIntersection = SetUtils.intersectNSets(impressionCreativeAttributes,
                                                                           adCreativeAttributes);

                logger.debug("Impression banner object's creative attributes and ad creative attributes intersection" +
                             " size: {} , if size non-zero then impression does not allow this ad to be served.",
                             resultingIntersection.size());

                if(null != resultingIntersection && resultingIntersection.size() > 0)
                {
                    //the creative is not appropriate for the requesting impression.
                    ReqLog.debugWithDebug(logger, request, "Creative id: {} does not qualify for creative attributes demanded " +
                                 "by the impression: {}",
                                 adEntity.getCreativeId(),
                                 bidRequestImpressionDTO.getBidRequestImpressionId());
                    continue;
                }
                else
                    creativeAttributesMatchAtleastOnce = true;

                /************************Creative attributes check ends here******************************************/

                CreativeBanner creativeBannerToUse = null;
                //first sort banner uri ids on size and then use.
                List<CreativeBanner> creativeBannerList = new ArrayList<CreativeBanner>();
                Integer bannerUriIds[] = null;

                if(null != creative.getBannerUriIds())
                {
                    for(Integer bannerId : creative.getBannerUriIds())
                    {
                        CreativeBanner creativeBanner = creativeBannerCache.query(bannerId);
                        if(null == creativeBanner)
                        {
                            logger.error("Creative banner is null(not found in cache) for banner id: " + bannerId);
                            break;
                        }

                        creativeBannerList.add(creativeBanner);
                    }

                    Collections.sort(creativeBannerList,comparator);
                    //done sorting banner uri ids on size.

                    bannerUriIds = new Integer[creative.getBannerUriIds().length];
                    for(int i=0 ;i<creative.getBannerUriIds().length;i++)
                    {
                        bannerUriIds[i] = creativeBannerList.get(i).getId();
                    }
                }

                /**
                 * Size check required only if bid request has size and if ad is not richmedia.
                 * Also if request has available width and height arrays then use them for size
                 * check and also use interstitial flag if set to check for minimum size required.
                 */
                int[] widthArrayForImpressionId = request.fetchRequiredWidthArrayForImpressionId
                                                                 (bidRequestImpressionDTO.getBidRequestImpressionId());

                int[] heightArrayForImpressionId = request.fetchRequiredHeightArrayForImpressionId
                                                                 (bidRequestImpressionDTO.getBidRequestImpressionId());

                int[] minimumWidthArrayForInterstitial = request.fetchMinimumInterstitialWidthArrayForImpressionId
                                                                 (bidRequestImpressionDTO.getBidRequestImpressionId());

                int[] minimumHeightArrayForInterstitial = request.fetchMinimumInterstitialHeightArrayForImpressionId
                                                                 (bidRequestImpressionDTO.getBidRequestImpressionId());

                boolean requestSpecifiesSize =
                                             ((null != width && null != height) ||
                                             (null != widthArrayForImpressionId && null != heightArrayForImpressionId));

                if(
                   isBannerAllowed                                 &&
                   null != bannerUriIds                            &&
                   bannerUriIds.length > 0                         &&
                   requestSpecifiesSize                            &&
                   !creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)
                  )
                {
                    logger.debug("Requesting width:{}, height:{}, creative's banners size:{} ",
                                  width,height,creative.getBannerUriIds().length);

                    boolean sizeCheckForBanner = false;

                    for(Integer bannerId : bannerUriIds)
                    {
                        CreativeBanner creativeBanner = creativeBannerCache.query(bannerId);
                        if(null == creativeBanner)
                        {
                            logger.error("Creative banner is null(not found in cache) for banner id: " + bannerId);
                            break;
                        }

                        CreativeSlot creativeSlot = creativeSlotCache.query(creativeBanner.getSlotId());

                        if(null == creativeSlot)
                        {
                            logger.error("Creative slot is null(not found in cache) for slot id: " +
                                         creativeBanner.getSlotId());
                            break;
                        }

                        boolean strictBannerSize = request.fetchStrictBannerSizeForImpressionId
                                                                (bidRequestImpressionDTO.getBidRequestImpressionId());

                        /*use widthArray and heightArray if available*/
                        boolean sizeFoundUsingRequestWidthHeightArray = false;

                        if(null != widthArrayForImpressionId && null != heightArrayForImpressionId)
                        {
                            int counter = 0;

                            for(int widthFromRequest : widthArrayForImpressionId)
                            {
                                int heightFromRequest = heightArrayForImpressionId[counter];
                                Integer interstitialWidthRequest = null;
                                if(null != minimumWidthArrayForInterstitial && minimumWidthArrayForInterstitial.length > 0)
                                    interstitialWidthRequest = minimumWidthArrayForInterstitial[counter];

                                Integer interstitialHeightRequest = null;
                                if(null != minimumHeightArrayForInterstitial && minimumHeightArrayForInterstitial.length > 0)
                                    interstitialHeightRequest = minimumHeightArrayForInterstitial[counter];

                                if(strictBannerSize && isCreativeSlotEqualsRequestingSlot(creativeSlot,widthFromRequest,heightFromRequest))
                                {
                                    sizeFoundUsingRequestWidthHeightArray = true;
                                    responseAdInfo.setRequestingWidthForWhichCreativeFound(widthFromRequest);
                                    responseAdInfo.setRequestingHeightForWhichCreativeFound(heightFromRequest);
                                    break;
                                }
                                else if(
                                        !strictBannerSize                   &&
                                        !request.isInterstitialBidRequest() &&
                                        isCreativeSlotFitForRequestingSlot(creativeSlot,widthFromRequest,heightFromRequest)
                                       )
                                {
                                    sizeFoundUsingRequestWidthHeightArray = true;
                                    responseAdInfo.setRequestingWidthForWhichCreativeFound(widthFromRequest);
                                    responseAdInfo.setRequestingHeightForWhichCreativeFound(heightFromRequest);
                                    break;
                                }
                                else if(
                                        !strictBannerSize                  &&
                                        request.isInterstitialBidRequest() &&
                                        isCreativeSlotFitForRequestingInterstitialSlot(creativeSlot,
                                                                                       widthFromRequest,
                                                                                       heightFromRequest,
                                                                                       interstitialWidthRequest,
                                                                                       interstitialHeightRequest)
                                       )
                                {
                                    sizeFoundUsingRequestWidthHeightArray = true;
                                    responseAdInfo.setRequestingWidthForWhichCreativeFound(widthFromRequest);
                                    responseAdInfo.setRequestingHeightForWhichCreativeFound(heightFromRequest);
                                    break;
                                }

                                counter ++;
                            }
                        }

                        if(sizeFoundUsingRequestWidthHeightArray)
                        {
                            logger.debug("Creative Banner id:{} found for this impressionId:{} using sizeFoundUsingRequestWidthHeightArray" ,
                                    bannerId,bidRequestImpressionDTO.getBidRequestImpressionId());

                            creativeBannerToUse = creativeBanner;
                            sizeCheckForBanner = true;
                            creativeFoundForRequestedSlot = true;
                            break;
                        }
                        else if(strictBannerSize && isCreativeSlotEqualsRequestingSlot(creativeSlot,width,height))
                        {
                            logger.debug("Creative Banner id:{} found for this impressionId:{} as strictBannerSize " ,
                                         bannerId,bidRequestImpressionDTO.getBidRequestImpressionId());

                            creativeBannerToUse = creativeBanner;
                            sizeCheckForBanner = true;
                            creativeFoundForRequestedSlot = true;
                            responseAdInfo.setRequestingWidthForWhichCreativeFound(width);
                            responseAdInfo.setRequestingHeightForWhichCreativeFound(height);
                            break;
                        }
                        else if(!strictBannerSize                   &&
                                !request.isInterstitialBidRequest() &&
                                isCreativeSlotFitForRequestingSlot(creativeSlot,width,height))
                        {
                            logger.debug("Creative Banner id:{} found for this impressionId:{} " ,
                                         bannerId,bidRequestImpressionDTO.getBidRequestImpressionId());

                            creativeBannerToUse = creativeBanner;
                            sizeCheckForBanner = true;
                            creativeFoundForRequestedSlot = true;
                            responseAdInfo.setRequestingWidthForWhichCreativeFound(width);
                            responseAdInfo.setRequestingHeightForWhichCreativeFound(height);
                            break;
                        }
                        else
                        {
                            logger.debug("BannerId:{} width: {},height:{} , request width:{},height:{},doesnot qualify",
                                          bannerId,creativeSlot.getCreativeSlotWidth(),
                                          creativeSlot.getCreativeSlotHeight(),width,height);
                        }


                        ReqLog.debugWithDebug(logger, request, "StrictBannerSize: {} , request interstitial : {} , " +
                                         "isCreativeEqualToRequestSize: {}, isCreativeSizeLessThanEqualRequestSize:{} ",
                                         strictBannerSize,request.isInterstitialBidRequest(),
                                         isCreativeSlotEqualsRequestingSlot(creativeSlot,width,height),
                                         isCreativeSlotFitForRequestingSlot(creativeSlot,width,height)
                                        );
                    }

                    if(!sizeCheckForBanner)
                    {
                        ReqLog.errorWithDebug(logger, request, "We could not find any creative supporting the requesting sizes of (width,height) " +
                                     "combinations: " +
                                     fetchRequestedWidthAndHeightPairForDebug(width,height) +
                                     " for/by creativeId: " + creative.getId());
                    }
                }
                //if creative is richmedia then allow the creative if flow comes till here.
                else if(isRichmediaAllowed && creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA))
                {
                    if(request.isExternalResouceURLRequired() && creative.getExternalResourceURL() == null) {
                        ReqLog.debugWithDebug(logger, request, "External resource URL required by the supply. Not set for the ad. " +
                                "Skipping adunit:{}", adEntity.getAdGuid());
                        creativeFoundForRequestedSlot = false;
                    } else {
                        creativeFoundForRequestedSlot = true;
                    }
                }
                //if request did not have any size specified and is not richmedia creative then just
                //pass the creative with any creative banner.
                else if(isBannerAllowed && null != bannerUriIds && bannerUriIds.length > 0)
                {
                    creativeBannerToUse = creativeBannerCache.query(bannerUriIds[0]);
                    creativeFoundForRequestedSlot = true;
                }

                if(!creativeFoundForRequestedSlot)
                {
                    ReqLog.errorWithDebug(logger, request, "No creative could be found for impression id of this bidrequest.Skipping adunit:{} ",
                                 adEntity.getAdGuid());
                    continue;
                }

                Campaign campaign = campaignCache.query(adEntity.getCampaignIncId());

                StringBuffer errorMessage = new StringBuffer();
                if(null == campaign)
                {
                    errorMessage.setLength(0);
                    errorMessage.append("FATAL!!! campaign not found for adid: ");
                    errorMessage.append(adEntity.getId());
                    ReqLog.errorWithDebug(logger, request, errorMessage.toString());
                    continue;
                }

                //lastly use bidfloor value of impression to see if ad qualifies.
                Double bidFloorForImpression = bidRequestImpressionDTO.getBidFloorPrice();

                ReqLog.requestDebug(request, " Ecpm floor value asked by exchange is : "+bidFloorForImpression);

                //for the case of creative being banner.
                if
                (
                   isBannerAllowed                                                      &&
                   (null == bidFloorForImpression                                       ||
                       (
                        null != bidFloorForImpression &&
                        bidFloorForImpression.compareTo(responseAdInfo.getEcpmValue()) <= 0
                       )
                   )                                                                    &&
                   null != creativeBannerToUse                                          &&
                   doesImpressionHasPMPDealIdForAdUnit(
                                                       bidRequestImpressionDTO.getBidRequestImpressionId(),
                                                       request.getSite(),
                                                       adEntity,
                                                       request,
                                                       responseAdInfo
                                                      )
                )
                {
                    ReqLog.debugWithDebug(logger, request, "Qualifying BannerId: {} with qualifying ecpm value: {} found for " +
                                 "impressionId: {} with bidFloor as: {} ",
                                 creativeBannerToUse.getId() , responseAdInfo.getEcpmValue(),
                                 bidRequestImpressionDTO.getBidRequestImpressionId(),bidFloorForImpression);

                    responseAdInfo.setCreativeBanner(creativeBannerToUse);
                    if(null != creativeBannerToUse)
                        responseAdInfo.setSlotId(creativeBannerToUse.getSlotId());

                    response.addResponseAdInfoAgainstBidRequestImpressionId
                    (
                        bidRequestImpressionDTO.getBidRequestImpressionId(),
                        responseAdInfo
                    );
                    floorPriceMet = true;
                }
                else if
                (
                    isRichmediaAllowed                                                         &&
                    (
                        null == bidFloorForImpression                                          ||
                        (
                            null != bidFloorForImpression &&
                            bidFloorForImpression.compareTo(responseAdInfo.getEcpmValue()) <= 0
                        )
                    )                                                                          &&
                    creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)
                )
                {
                    ReqLog.debugWithDebug(logger, request, "Qualifying richmedia ad with qualifying ecpm value: {} found for " +
                                 "impressionId: {} with bidFloor as: {} ",
                                 responseAdInfo.getEcpmValue(),
                                 bidRequestImpressionDTO.getBidRequestImpressionId(),bidFloorForImpression);

                    responseAdInfo.setRichMediaAdIsCompatibleForAdserving(true);
                    responseAdInfo.setRichMediaPayLoadFromCreative(creative.getHtmlContent());
                    response.addResponseAdInfoAgainstBidRequestImpressionId
                    (
                        bidRequestImpressionDTO.getBidRequestImpressionId(),
                        responseAdInfo
                    );

                    floorPriceMet = true;
                }
            }
        }

        if(!creativeAttributesMatchAtleastOnce && null == request.getNoFillReason())
        {
            request.setNoFillReason(Request.NO_FILL_REASON.CREATIVE_ATTR);
            ReqLog.debugWithDebug(logger, request, "NoFill found as creative attributes inside CreativeAndFloorMatchingRTBExchangeTwoDotOne");
        }

        if(!creativeFoundForRequestedSlot && null == request.getNoFillReason())
        {
            request.setNoFillReason(Request.NO_FILL_REASON.CREATIVE_SIZE);
            ReqLog.debugWithDebug(logger, request, "NoFill found as creative size inside CreativeAndFloorMatchingRTBExchangeTwoDotOne");
        }

        if(!floorPriceMet && null == request.getNoFillReason())
        {
            request.setNoFillReason(Request.NO_FILL_REASON.ECPM_FLOOR_UNMET);
            ReqLog.debugWithDebug(logger, request, "NoFill found as ecpm floor unmet inside CreativeAndFloorMatchingRTBExchangeTwoDotOne");
        }

        //set to request all the requesting slotids.
        request.setRequestedSlotIdList(requestedSlotIdList);
        Integer[] requestedSlotHeightsIntegers = requestedSlotHeights.toArray(new Integer[requestedSlotHeights.size()]);
        int[] requestedSlotHeightsPrimitives = ArrayUtils.toPrimitive(requestedSlotHeightsIntegers);
        request.setRequestedSlotHeights(requestedSlotHeightsPrimitives);
        Integer[] requestedSlotWidthsIntegers = requestedSlotWidths.toArray(new Integer[requestedSlotWidths.size()]);
        int[] requestedSlotWidthsPrimitives = ArrayUtils.toPrimitive(requestedSlotWidthsIntegers);
        request.setRequestedSlotWidths(requestedSlotWidthsPrimitives);
    }

    private boolean isCreativeSlotEqualsRequestingSlot(
                                                       CreativeSlot creativeSlot,
                                                       Integer requestedWidth,
                                                       Integer requestedHeight
                                                      )
    {
        if(null == requestedHeight || null == requestedHeight)
            return true;

        if(
           requestedWidth.intValue()  == creativeSlot.getCreativeSlotWidth()  &&
           requestedHeight.intValue() == creativeSlot.getCreativeSlotHeight()
          )
        {
            return true;
        }

        return false;
    }

    private boolean isCreativeSlotFitForRequestingSlot(
                                                       CreativeSlot creativeSlot,
                                                       Integer requestedWidth,
                                                       Integer requestedHeight
                                                      )
    {

        if(null == requestedHeight || null == requestedHeight)
            return true;

        if(
           requestedWidth.intValue()  >= creativeSlot.getCreativeSlotWidth()  &&
           requestedHeight.intValue() >= creativeSlot.getCreativeSlotHeight()
          )
        {
            return true;
        }

        return false;
    }

    private boolean isCreativeSlotFitForRequestingInterstitialSlot(
                                                                    CreativeSlot creativeSlot,
                                                                    Integer requestedWidth,
                                                                    Integer requestedHeight,
                                                                    Integer interstitialMinimumWidth,
                                                                    Integer interstitialMinimumHeight
                                                                  )
    {

        logger.debug("Inside checking for interstitial size, requesting width:{} ,requesting height:{} ," +
                     "requesting minimum width:{} , requesting minimum height:{} , creative width: {}, " +
                     "creative height: {}",
                     requestedWidth,requestedHeight,
                     interstitialMinimumWidth,
                     interstitialMinimumHeight,
                     creativeSlot.getCreativeSlotWidth(),
                     creativeSlot.getCreativeSlotHeight());



        if(null == requestedHeight || null == requestedHeight)
            return true;

        if(null == interstitialMinimumWidth || null == interstitialMinimumHeight)
        {
            if(
                requestedWidth.intValue()  >= creativeSlot.getCreativeSlotWidth()           &&
                requestedHeight.intValue() >= creativeSlot.getCreativeSlotHeight()
              )
                return true;
        }

        if(
              requestedWidth.intValue()  >= creativeSlot.getCreativeSlotWidth()             &&
              requestedHeight.intValue() >= creativeSlot.getCreativeSlotHeight()            &&
              interstitialMinimumWidth.intValue() <= creativeSlot.getCreativeSlotWidth()   &&
              interstitialMinimumHeight.intValue() <= creativeSlot.getCreativeSlotHeight()
          )
        {
            logger.debug("Inside checking for interstitial size, check holds true!");
            return true;
        }

        return false;
    }

    private String fetchRequestedWidthAndHeightPairForDebug(int requestedWidth,int requestedHeight)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(requestedWidth);
        sb.append(COMMA);
        sb.append(requestedHeight);
        sb.append(SEMICOLON);
        return sb.toString();
    }

    private class BannerSizeComparator implements Comparator<CreativeBanner>
    {
        @Override
        public int compare(
                           CreativeBanner creativeBannerFirst,
                           CreativeBanner creativeBannerSecond
                          )
        {
            CreativeSlot creativeSlotFirst = creativeSlotCache.query(creativeBannerFirst.getSlotId());
            CreativeSlot creativeSlotSecond = creativeSlotCache.query(creativeBannerSecond.getSlotId());

            if(null == creativeSlotFirst || null == creativeSlotSecond)
                return 0;

            if(creativeSlotFirst.getCreativeSlotWidth().shortValue() >
               creativeSlotSecond.getCreativeSlotWidth().shortValue())
                return -1;

            if(
               creativeSlotFirst.getCreativeSlotWidth().shortValue() ==
               creativeSlotSecond.getCreativeSlotWidth().shortValue() &&
               creativeSlotFirst.getCreativeSlotHeight().shortValue() >
               creativeSlotSecond.getCreativeSlotHeight().shortValue()
              )
                return -1;

            return 1;
        }
    }

    /**
     * if ad is deal id targeted then run only on that deal id,
     * if impression has deal id then only that deal id targeted
     * ad can be selected.
     * @param site
     * @param adEntity
     * @param request
     * @return
     */
    public boolean doesImpressionHasPMPDealIdForAdUnit(
                                                       String impressionId,
                                                       Site site,
                                                       AdEntity adEntity,
                                                       Request request,
                                                       ResponseAdInfo responseAdInfo
                                                      )
    {
        ReqLog.infoWithDebug(logger, request, "Inside doesImpressionHasPMPDealIdForAdUnit of CreativeAndFloorMatchingRTBExchangeTwoDotOne ...");

        Set<AdExchangeInfo.PrivateDealInfo> privateDealInfoSet =
                                                    request.fetchPrivateDealInfoSetForImpressionId(impressionId);

        boolean impressionNeedsPMPAds = ((null != privateDealInfoSet) && privateDealInfoSet.size() > 0);

        String publisherId = site.getPublisherId();
        Map<String,String[]> adUnitPMPDealIdInfoMap = adEntity.getTargetingProfile().getPmpDealIdInfoMap();

        boolean adTargetsDealId = (null != adUnitPMPDealIdInfoMap && adUnitPMPDealIdInfoMap.size() > 0);

        logger.debug("Ad: {} targets pmp deals : {} and impression has pmp info: {} of size ",
                      adEntity.getAdGuid(), adTargetsDealId,impressionNeedsPMPAds,
                      (null == privateDealInfoSet) ? 0: privateDealInfoSet.size());

        String[] dealIdArrayForThisPublisher = null;

        logger.debug("Getting deal map for publisher: {} ", publisherId);

        if(null != adUnitPMPDealIdInfoMap)
            dealIdArrayForThisPublisher = adUnitPMPDealIdInfoMap.get(publisherId);

        logger.debug("Deal id array for publisher by this ad , length: {} ",
                      null == dealIdArrayForThisPublisher ? 0 : dealIdArrayForThisPublisher.length);

        if( impressionNeedsPMPAds && adTargetsDealId && null != dealIdArrayForThisPublisher &&
            dealIdArrayForThisPublisher.length > 0
          )
        {
            ReqLog.debugWithDebug(logger, request, "Impression Id:{} has deal id specified, looking if ad:{} is targeting deal id set: {} ",
                          impressionId,adEntity.getAdGuid(),fetchDealIdString(privateDealInfoSet));

            for(AdExchangeInfo.PrivateDealInfo privateDealInfo : privateDealInfoSet)
            {
                logger.debug("Private deal info from exchange : {} ", privateDealInfo.getDealId());

                for(String dealIdByAd : dealIdArrayForThisPublisher)
                {
                    logger.debug("Private deal info from ad : {} ", dealIdByAd);

                    if(
                       privateDealInfo.getDealId().equalsIgnoreCase(dealIdByAd) &&
                       (null == privateDealInfo.getBidFloor() ||
                        (
                            null != privateDealInfo.getBidFloor() &&
                            privateDealInfo.getBidFloor().compareTo(responseAdInfo.getEcpmValue()) <= 0
                        )
                       )
                      )
                    {
                        ReqLog.debugWithDebug(logger, request, "DealIdByAd: {} matches and fits deal id in impression:{} ",
                                      dealIdByAd,privateDealInfo.getDealId());
                        responseAdInfo.setDealId(dealIdByAd);
                        return true;
                    }
                }
            }
        }

        if(!impressionNeedsPMPAds && !adTargetsDealId)
        {
            return true;
        }

        return false;
    }

    private String fetchDealIdString(Set<AdExchangeInfo.PrivateDealInfo> privateDealInfoSet)
    {
        if(null == privateDealInfoSet || privateDealInfoSet.size() <= 0)
            return "";

        StringBuffer sb = new StringBuffer();

        for(AdExchangeInfo.PrivateDealInfo privateDealInfo : privateDealInfoSet)
        {
            sb.append(privateDealInfo.getDealId());
            sb.append(" , ");
        }

        return sb.toString();
    }
}
