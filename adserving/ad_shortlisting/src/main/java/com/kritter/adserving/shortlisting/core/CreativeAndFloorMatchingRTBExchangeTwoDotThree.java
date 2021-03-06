package com.kritter.adserving.shortlisting.core;

import com.kritter.adserving.shortlisting.utils.CreativeUtils;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.constants.OpenRTBParameters;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.entity.reqres.entity.AdExchangeInfo;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.core.openrtbhelper.*;
import com.kritter.common.caches.advinfo_upload_cache.AdvInfoUploadCache;
import com.kritter.common.caches.advinfo_upload_cache.entity.AdvInfoUploadCacheEntity;
import com.kritter.common.caches.adxbasedexchanges_metadata_cache.AdxBasedExchangesMetadataCache;
import com.kritter.common.caches.adxbasedexchanges_metadata_cache.entity.AdxBasedMetadataCacheEntity;
import com.kritter.common.caches.banner_upload_cache.BannerUploadCache;
import com.kritter.common.caches.native_icon_cache.NativeIconCache;
import com.kritter.common.caches.native_screenshot_cache.NativeScreenshotCache;
import com.kritter.common.caches.slot_size_cache.CreativeSlotSizeCache;
import com.kritter.common.caches.video_info_cache.VideoInfoCache;
import com.kritter.common.caches.video_upload_cache.VideoUploadCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.CreativeFormat;
import com.kritter.serving.demand.cache.*;
import com.kritter.serving.demand.entity.*;

import com.kritter.utils.common.url.URLField;
import com.kritter.utils.common.url.URLFieldProcessingException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

/**
 * This is the base class for shortlisting ads after the run of
 * common AdTargetingMatcher, this will have ads being filtered
 * out on creative basis, price floor, etc.
 * If some exchange requires very specific implementation on
 * shortlisting or any other logic then this class can be
 * extended to override specific functions.
 */

public class CreativeAndFloorMatchingRTBExchangeTwoDotThree implements CreativeAndFloorMatchingRTBExchange
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
    private NativeIconCache nativeIconCache;
    private NativeScreenshotCache nativeScreenshotCache;
    private String adNoFillReasonMapKey;
    private VideoInfoCache videoInfoCache;
    private List<Integer> openRTBBidRequestParameterCodeList;
    private BannerUploadCache bannerUploadCache;
    private VideoUploadCache videoUploadCache;
    private AdvInfoUploadCache advInfoUploadCache;
    private AdxBasedExchangesMetadataCache adxBasedExchangeMetadataCache;
    private static final String CTRL_A = String.valueOf((char)1);



    public CreativeAndFloorMatchingRTBExchangeTwoDotThree(
                                                          String loggerName,
                                                          CreativeBannerCache creativeBannerCache,
                                                          CreativeCache creativeCache,
                                                          AdEntityCache adEntityCache,
                                                          CreativeSlotCache creativeSlotCache,
                                                          CampaignCache campaignCache,
                                                          CreativeSlotSizeCache creativeSlotSizeCache,
                                                          NativeIconCache nativeIconCache,
                                                          NativeScreenshotCache nativeScreenshotCache,
                                                          String adNoFillReasonMapKey,
                                                          VideoInfoCache videoInfoCache,
                                                          List<Integer> openRTBBidRequestParameterCodeList,
                                                          BannerUploadCache bannerUploadCache,
                                                          VideoUploadCache videoUploadCache,
                                                          AdvInfoUploadCache advInfoUploadCache,
                                                          AdxBasedExchangesMetadataCache adxBasedExchangeMetadataCache
                                                         )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.creativeBannerCache = creativeBannerCache;
        this.creativeCache = creativeCache;
        this.adEntityCache = adEntityCache;
        this.creativeSlotCache = creativeSlotCache;
        this.campaignCache = campaignCache;
        this.creativeSlotSizeCache = creativeSlotSizeCache;
        this.comparator = new CreativeUtils.BannerSizeComparator(creativeSlotCache);
        this.nativeIconCache = nativeIconCache;
        this.nativeScreenshotCache = nativeScreenshotCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
        this.videoInfoCache = videoInfoCache;
        this.openRTBBidRequestParameterCodeList = openRTBBidRequestParameterCodeList;
        this.bannerUploadCache = bannerUploadCache;
        this.videoUploadCache = videoUploadCache;
        this.advInfoUploadCache = advInfoUploadCache;
        this.adxBasedExchangeMetadataCache = adxBasedExchangeMetadataCache;

    }

    /**
     * This function takes input shortlisted ad ids,and try to match them with incoming
     * bid request impressions in context of height,width,creative attributes,creative
     * types,bidfloor,
     * Only banner ads are supported for now.
     * @param request
     * @param response
     */
    public void processAdUnitsForEachBidRequestImpression(
                                                          Request request,
                                                          Response response,
                                                          Context context
                                                         ) throws Exception
    {
        logger.debug("Inside processAdUnitsForEachBidRequestImpression of CreativeAndFloorMatchingRTBExchangeTwoDotThree");

        if(null == response.getResponseAdInfo() || response.getResponseAdInfo().size() == 0)
        {
            logger.debug("No ResponseAdInfo inside CreativeAndFloorMatchingRTBExchangeTwoDotThree...");
            return;
        }

        if(null == request.getBidRequest() || null==request.getBidRequest().getBidRequestParentNodeDTO())
        {
            logger.error("BidRequest or BidRequestParentNode is null inside CreativeAndFloorMatchingRTBExchangeTwoDotThree ");
            return;
        }

        Site site = request.getSite();
        
        BidRequestParentNodeDTO bidRequestParentNodeDTO = (BidRequestParentNodeDTO)request.
                                                                       getBidRequest().getBidRequestParentNodeDTO();
        
        BidRequestImpressionDTO[] bidRequestImpressionDTOs = bidRequestParentNodeDTO.getBidRequestImpressionArray();

        /*Set parameters from bid request to url fields required for postimpression*/
        try
        {
            setURLFieldsFromBidRequest(bidRequestParentNodeDTO, request);
        }
        catch (Exception e)
        {
            logger.error("Exception inside processAdUnitsForEachBidRequestImpression of " +
                         "CreativeAndFloorMatchingRTBExchangeTwoDotThree while setting url fields ",e);
        }

        /**
         * For each impression find the matching ad.
         * Ad can not be text ad.As of now banner and impressions are allowed.
         * 
         **/

        List<Short> requestedSlotIdList = new ArrayList<Short>();
        List<Integer> requestedSlotWidths = new ArrayList<Integer>();
        List<Integer> requestedSlotHeights = new ArrayList<Integer>();


        boolean creativeAttributesMatchAtleastOnce = false;
        boolean creativeFoundForRequestedSlot = false;
        boolean floorPriceMet = false;

        Float averageFloorPrice = 0.0f;
        int averageFloorPriceCounter = 0;
        boolean bannerOrRichMediaRequired = false;
        int pubIncId = request.getSite().getPublisherIncId();
        AdxBasedExchangesMetadata adxBased = null;
        if(this.adxBasedExchangeMetadataCache != null){
        	AdxBasedMetadataCacheEntity ace = this.adxBasedExchangeMetadataCache.query(pubIncId+"");
        	if(ace != null){
        		adxBased = ace.getAbem();
        	}
        }

        for(BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs)
        {
            /*Populate pmp information to our request object.*/
            BidRequestPMPDTO bidRequestPMPDTO = bidRequestImpressionDTO.getBidRequestPMPDTO();
            setPrivateDealInfoInRequest(bidRequestPMPDTO,request,bidRequestImpressionDTO.getBidRequestImpressionId());

            if(bidRequestImpressionDTO.getBidRequestImpressionNativeObjectDTO() != null){
                ValidateNative.checkNative(bidRequestImpressionDTOs, site, request, logger, response, adEntityCache, creativeCache, nativeIconCache,
                        nativeScreenshotCache);
                return;
            }
            if(bidRequestImpressionDTO.getBidRequestImpressionVideoObject()!= null){
                ValidateVideo.checkVideo(bidRequestImpressionDTOs, site, request, logger, response, adEntityCache, 
                		creativeCache, videoInfoCache, adxBased, this.videoUploadCache, this.advInfoUploadCache);
                return;
            }
            BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO =
                    bidRequestImpressionDTO.getBidRequestImpressionBannerObject();
            
            if(null == bidRequestImpressionBannerObjectDTO)
            {
                logger.error("BidRequestImpressionBannerObjectDTO is null inside AdShortlistingRTBExchangeTwoDotThree, cannot process request for this impressionId: {} ",
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

            isBannerAllowed = ValidateBlockedCreativeType.validate(request, blockedCreativeTypeSet, logger, BANNER_CREATIVE_TYPE);
            isRichmediaAllowed = ValidateRichMediaType.validate(request, logger);
            if(isRichmediaAllowed == true){
                isRichmediaAllowed = ValidateRichMediaType.validateDeviceHandset(request, logger);
            }
            if(isRichmediaAllowed == true){
                isRichmediaAllowed = ValidateRichMediaType.validateBCAT(request, logger, blockedCreativeTypeSet, RICHMEDIA_CREATIVE_TYPE);
            }

            if(!isBannerAllowed && !isRichmediaAllowed)
            {
                ReqLog.errorWithDebugNew(logger, request, "BidRequestImpression does not allow banner ads or richmedia inside AdShortlistingRTBExchangeTwoDotThree, cannot process request for this impressionId: {} ",
                        bidRequestImpressionDTO.getBidRequestImpressionId());

                continue;
            }

            bannerOrRichMediaRequired = true;

            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo())
            {
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());
                if(null == adEntity)
                {
                    logger.error("AdEntity not found in cache,FATAL error!!! for adId: {} " +
                            responseAdInfo.getAdId());
                    continue;
                }
                if(adxBased != null && adxBased.isAdvertiser_upload()){
                	int advId = adEntity.getAccountId();
            		if(this.advInfoUploadCache == null){
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as advInfoUploadCache null", advId);
            			continue;
            		}
            		AdvInfoUploadCacheEntity bue = this.advInfoUploadCache.query(pubIncId+CTRL_A+advId);
                	if(bue ==null){
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as AdvInfoUploadCacheEntity  null", advId);
            			continue;
                	}
                	if(bue.getMua() == null){
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  null", advId);
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMua().getAdxbasedexhangesstatus()){
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  not Approved", advId);
            			continue;                        		
                	}
                }

                int adId = adEntity.getAdIncId();

                Creative creative = creativeCache.query(adEntity.getCreativeId());
                if(null == creative)
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                            NoFillReason.CREATIVE_FORMAT_ERROR.getValue(), this.adNoFillReasonMapKey, context);

                    logger.error("Creative null in cache,FATAL error!!! for creative id: " + adEntity.getCreativeId());
                    continue;
                }

                //if not banner or richmedia creative skip adunit.
                if(
                   !creative.getCreativeFormat().equals(CreativeFormat.BANNER) &&
                   !creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA)
                  )
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                            NoFillReason.CREATIVE_FORMAT_ERROR.getValue(), this.adNoFillReasonMapKey, context);

                    logger.debug("Creative format is not banner or richmedia inside AdShortlistingRTBExchangeTwoDotThree, skipping adId: {} ", adEntity.getAdGuid());
                    continue;
                }

                /******************Check if the incoming impression allows creative attributes************************/
                if(ValidateCreativeAttribute.validate(logger, request, bidRequestImpressionBannerObjectDTO, creative, adEntity, bidRequestImpressionDTO)){
                    creativeAttributesMatchAtleastOnce = true;
                }else{
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                            NoFillReason.CREATIVE_ATTR.getValue(), this.adNoFillReasonMapKey, context);

                    continue;
                }
                /************************Creative attributes check ends here******************************************/

                CreativeBanner creativeBannerToUse = null;
                //first sort banner uri ids on size and then use.
                List<CreativeBanner> creativeBannerList = new ArrayList<CreativeBanner>();
                Integer bannerUriIds[] = ValidateCreativeSize.fetchBannerUids(logger, creative, 
                        creativeBannerCache, creativeBannerList, comparator,
                        request, adxBased, this.bannerUploadCache,responseAdInfo);
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
                            AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                                    NoFillReason.CREATIVE_FORMAT_ERROR.getValue(), this.adNoFillReasonMapKey,
                                    context);

                            logger.error("Creative banner is null(not found in cache) for banner id: {}" , bannerId);
                            break;
                        }

                        CreativeSlot creativeSlot = creativeSlotCache.query(creativeBanner.getSlotId());

                        if(null == creativeSlot)
                        {
                            AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                                    NoFillReason.CREATIVE_FORMAT_ERROR.getValue(), this.adNoFillReasonMapKey,
                                    context);

                            logger.error("Creative slot is null(not found in cache) for slot id:{} ", creativeBanner.getSlotId());
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

                        logger.debug("StrictBannerSize: {} , request interstitial : {} , isCreativeEqualToRequestSize: {}, isCreativeSizeLessThanEqualRequestSize:{} ",
                                      strictBannerSize,request.isInterstitialBidRequest(),
                                      isCreativeSlotEqualsRequestingSlot(creativeSlot,width,height),
                                      isCreativeSlotFitForRequestingSlot(creativeSlot,width,height));

                        if(request.isRequestForSystemDebugging())
                        {
                            request.addDebugMessageForTestRequest("StrictBannerSize: "+strictBannerSize+" , "
                                    + "request interstitial : "+request.isInterstitialBidRequest()+" , "
                                            + "isCreativeEqualToRequestSize: "+isCreativeSlotEqualsRequestingSlot(creativeSlot,width,height)+
                                            ", isCreativeSizeLessThanEqualRequestSize: "+isCreativeSlotFitForRequestingSlot(creativeSlot,width,height));
                        }
                    }

                    if(!sizeCheckForBanner)
                    {
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                                NoFillReason.CREATIVE_SIZE.getValue(), this.adNoFillReasonMapKey, context);
                        
                        logger.debug("We could not find any creative supporting the requesting sizes of (width,height) combinations:{} for/by creativeId: {}" ,
                                fetchRequestedWidthAndHeightPairForDebug(width,height) , creative.getId());

                        if(request.isRequestForSystemDebugging())
                        {
                        	request.addDebugMessageForTestRequest("We could not find any "
                        			+ "creative supporting the requesting sizes of (width,height) combinations:"+
                                       fetchRequestedWidthAndHeightPairForDebug(width,height) +" for/by creativeId: " +creative.getId());
                        }
                    }
                }
                //if creative is richmedia then allow the creative if flow comes till here.
                else if(creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA))
                {
                    logger.debug("Richmedia creative type being up for selection in CreativeAndFloorMatchingRTBExchangeTwoDotThree");

                    // Rich media not allowed, skip this ad.
                    if(!isRichmediaAllowed) {
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                                NoFillReason.CREATIVE_ATTR.getValue(), this.adNoFillReasonMapKey, context);

                        String debugMessage = "Ad id : %d is rich media but request does not allow it. Failing.";
                        request.addDebugMessageForTestRequest(String.format(debugMessage, adId));
                        continue;
                    }

                    // Rich media is allowed, check further.
                    if(request.isExternalResouceURLRequired() && creative.getExternalResourceURL() == null) {
                        ReqLog.debugWithDebugNew(logger, request, "External resource URL required by the supply. Not set for the ad. Skipping adunit:{}", adEntity.getAdGuid());
                        creativeFoundForRequestedSlot = false;
                    } else {
                        creativeFoundForRequestedSlot = true;
                    }

                    /* Set height and width for which richmedia ad found, since there is no check for
                     * richmedia size against requesting width and height, so any value would do.
                     * Use values from request.
                     */
                    logger.debug("Requesting width Array is null : {} and requesting height array is null : {} from" +
                                 " bid request ",
                                  (null == widthArrayForImpressionId) , (null == heightArrayForImpressionId));

                    if(null != widthArrayForImpressionId && widthArrayForImpressionId.length > 0)
                        responseAdInfo.setRequestingWidthForWhichCreativeFound(widthArrayForImpressionId[0]);

                    if(null != heightArrayForImpressionId && heightArrayForImpressionId.length > 0)
                        responseAdInfo.setRequestingHeightForWhichCreativeFound(heightArrayForImpressionId[0]);

                    if(null != widthArrayForImpressionId && null != heightArrayForImpressionId &&
                       widthArrayForImpressionId.length > 0 && heightArrayForImpressionId.length > 0)
                    {
                        logger.debug("Width value is: {} and height value is : {} from BidRequest",
                                widthArrayForImpressionId[0],
                                heightArrayForImpressionId[0]);
                    }
                }
                //if request did not have any size specified and is not richmedia creative then just
                //pass the creative with any creative banner.
                else if(null != bannerUriIds && bannerUriIds.length > 0)
                {
                    // Banner is not allowed, skip this ad.
                    if(!isBannerAllowed) {
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                                NoFillReason.CREATIVE_ATTR.getValue(), this.adNoFillReasonMapKey, context);

                        String debugMessage = "Ad id : %d is banner but request does not allow it. Failing.";
                        request.addDebugMessageForTestRequest(String.format(debugMessage, adId));
                        continue;
                    }

                    // Banner allowed. Check further
                    creativeBannerToUse = creativeBannerCache.query(bannerUriIds[0]);
                    CreativeSlot creativeSlot = creativeSlotCache.query(creativeBannerToUse.getSlotId());
                    responseAdInfo.setCreativeSlot(creativeSlot);
                    creativeFoundForRequestedSlot = true;
                }

                if(!creativeFoundForRequestedSlot)
                {
                    logger.debug("No creative could be found for impression id of this bidrequest.Skipping adunit: {} ", adEntity.getAdGuid());
                    if(request.isRequestForSystemDebugging())
                    {
                        ReqLog.errorWithDebugNew(logger, request, "No creative could be found for impression id of this bidrequest.Skipping adunit:{} ",
                                adEntity.getAdGuid());
                    }
                    continue;
                }

                Campaign campaign = campaignCache.query(adEntity.getCampaignIncId());

                if(null == campaign)
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                            NoFillReason.CAMPAIGN_NOT_FOUND.getValue(), this.adNoFillReasonMapKey, context);

                    logger.error("FATAL!!! campaign not found for adid: {} , campaign's flight date and budget have already been checked.", adEntity.getId());
                    continue;
                }
                if(ValidateFloorPrice.validate(logger, request, bidRequestImpressionDTO, isBannerAllowed, 
                                               creativeBannerToUse, adEntity, responseAdInfo, isRichmediaAllowed,
                                               creative, response,creativeSlotCache))
                {
                    floorPriceMet=true;
                }
                else
                {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                            NoFillReason.ECPM_FLOOR_UNMET.getValue(), this.adNoFillReasonMapKey, context);

                    ReqLog.debugWithDebugNew(logger, request, "Ad id : {} has ecpm {} while the floor is {}. The ad " +
                            "doesn't meet ecpm floor or pmp condition, skipping it.", adId, responseAdInfo.getEcpmValue(),
                            bidRequestImpressionDTO.getBidFloorPrice());

                    if(null != bidRequestImpressionDTO.getBidFloorPrice())
                    {
                        averageFloorPrice += bidRequestImpressionDTO.getBidFloorPrice().floatValue();
                        averageFloorPriceCounter ++;
                    }
                }
            }
        }

        /*Set bid floor value, if calculated.*/
        if(averageFloorPriceCounter > 0)
        {
            averageFloorPrice = averageFloorPrice / averageFloorPriceCounter;
            request.setBidFloorForFilledExchangeImpressions(averageFloorPrice);
        }

        if(!bannerOrRichMediaRequired) {
            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo()) {
                int adId = responseAdInfo.getAdId();
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId,
                        NoFillReason.CREATIVE_ATTR.getValue(), this.adNoFillReasonMapKey, context);
            }

            ReqLog.debugWithDebugNew(logger, request, "None of the impressions requested either banner or rich media. " +
                        "Skipping all the ads.");
        }

        if(!creativeAttributesMatchAtleastOnce && null == request.getNoFillReason())
        {
            request.setNoFillReason(NoFillReason.CREATIVE_ATTR);

            ReqLog.debugWithDebugNew(logger, request, "NoFill found as creative attributes inside CreativeAndFloorMatchingRTBExchangeTwoDotThree");
        }

        if(!creativeFoundForRequestedSlot && null == request.getNoFillReason())
        {
            request.setNoFillReason(NoFillReason.CREATIVE_SIZE);
            ReqLog.debugWithDebugNew(logger, request, "NoFill found as creative size inside CreativeAndFloorMatchingRTBExchangeTwoDotThree");
        }

        if(!floorPriceMet && null == request.getNoFillReason())
        {
            request.setNoFillReason(NoFillReason.ECPM_FLOOR_UNMET);
            ReqLog.debugWithDebugNew(logger, request, "NoFill found as ecpm floor unmet inside CreativeAndFloorMatchingRTBExchangeTwoDotThree");
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
            interstitialMinimumWidth.intValue() <= creativeSlot.getCreativeSlotWidth()    &&
            interstitialMinimumHeight.intValue() <= creativeSlot.getCreativeSlotHeight()
          )
        {
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

    /*The following function sets attributes from bid request that need to be passed in the
     *postimpression URLs.*/
    private void setURLFieldsFromBidRequest(BidRequestParentNodeDTO bidRequestParentNodeDTO, Request request)
                                                                                    throws URLFieldProcessingException
    {
        if(null == bidRequestParentNodeDTO)
        {
            logger.debug("BidRequestParentNodeDTO is null inside setURLFieldsFromBidRequest of " +
                         "CreativeAndFloorMatchingRTBExchangeTwoDotThree, cannot set urlfield attributes.");
            return;
        }

        BidRequestDeviceDTO bidRequestDeviceDTO = bidRequestParentNodeDTO.getBidRequestDevice();

        if(null == bidRequestDeviceDTO)
        {
            logger.debug("BidRequestDeviceDTO is null inside setURLFieldsFromBidRequest of " +
                         "CreativeAndFloorMatchingRTBExchangeTwoDotThree,cannot set urlfield attributes.");
            return;
        }

        String dpidMd5 = bidRequestDeviceDTO.getMD5HashedDevicePlatformId();
        String dpidSha1 = bidRequestDeviceDTO.getSHA1HashedDevicePlatformId();
        String macSha1 = bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice();
        String macMd5 = bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice();
        String ifa = bidRequestDeviceDTO.getIfa();

        if(null != this.openRTBBidRequestParameterCodeList)
        {
            for (Integer openRTBBidRequestParameterCode : this.openRTBBidRequestParameterCodeList)
            {
                int codeValue = openRTBBidRequestParameterCode.intValue();

                if(codeValue == OpenRTBParameters.PARAMETER.DEVICE_PLATFORM_ID_MD5.getCode() && null != dpidMd5)
                {
                    URLField urlField = URLField.DEVICE_PLATFORM_ID_MD5;
                    urlField.getUrlFieldProperties().setFieldValue(dpidMd5);
                    request.getUrlFieldFactory().stackFieldForStorage(urlField);
                }

                /*if dpidmd5 is already available then dont log this one*/
                if(codeValue == OpenRTBParameters.PARAMETER.DEVICE_PLATFORM_ID_SHA1.getCode()
                   && null != dpidSha1 && null == dpidMd5)
                {
                    URLField urlField = URLField.DEVICE_PLATFORM_ID_SHA1;
                    urlField.getUrlFieldProperties().setFieldValue(dpidSha1);
                    request.getUrlFieldFactory().stackFieldForStorage(urlField);
                }

                if(codeValue == OpenRTBParameters.PARAMETER.MAC_ADDRESS_MD5.getCode() && null != macMd5)
                {
                    URLField urlField = URLField.MAC_ADDRESS_MD5;
                    urlField.getUrlFieldProperties().setFieldValue(macMd5);
                    request.getUrlFieldFactory().stackFieldForStorage(urlField);
                }

                /*if macmd5 is already available then dont log this one*/
                if(codeValue == OpenRTBParameters.PARAMETER.MAC_ADDRESS_SHA1.getCode()
                   && null != macSha1 && null == macMd5)
                {
                    URLField urlField = URLField.MAC_ADDRESS_SHA1;
                    urlField.getUrlFieldProperties().setFieldValue(macSha1);
                    request.getUrlFieldFactory().stackFieldForStorage(urlField);
                }

                if(codeValue == OpenRTBParameters.PARAMETER.IFA.getCode() && null != ifa)
                {
                    URLField urlField = URLField.ID_FOR_ADVERTISER;
                    urlField.getUrlFieldProperties().setFieldValue(ifa);
                    request.getUrlFieldFactory().stackFieldForStorage(urlField);
                }
            }
        }
    }

    private void setPrivateDealInfoInRequest(
                                              BidRequestPMPDTO bidRequestPMPDTO,
                                              Request request,
                                              String impressionId
                                            )
    {
        if(null == bidRequestPMPDTO)
        {
            logger.debug("BidRequestPMPDTO missing in BidRequest, no deals can be run on this bid request");
            return;
        }

        logger.debug("PrivateMarketplace entity is present inside bid request...");

        if(
           null != bidRequestPMPDTO.getPrivateAuctionDeals() &&
           bidRequestPMPDTO.getPrivateAuctionDeals().length > 0
          )
        {
            logger.debug("Deals are present in bid request and size is {} inside " +
                         "CreativeAndFloorMatchingRTBExchangeTwoDotThree",
                          bidRequestPMPDTO.getPrivateAuctionDeals().length);

            for(BidRequestDealDTO deal : bidRequestPMPDTO.getPrivateAuctionDeals())
            {
                Double floor = null;
                if(null != deal.getBidFloor())
                    floor = deal.getBidFloor().doubleValue();

                AdExchangeInfo.PrivateDealInfo privateDealInfo =
                        new AdExchangeInfo.PrivateDealInfo
                                                         (
                                                          deal.getDealId(),
                                                          floor,
                                                          deal.getBidFloorCurrency(),
                                                          deal.getAuctionType()
                                                         );

                request.addToAdExchangeInfoPrivateDealInfo(impressionId,privateDealInfo);
            }
        }
        else
            logger.debug("No deals to bid on inside CreativeAndFloorMatchingRTBExchangeTwoDotThree,the ads targeting " +
                         "deals will be skipped/filtered.");

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
        ReqLog.infoWithDebugNew(logger, request, "Inside doesImpressionHasPMPDealIdForAdUnit of CreativeAndFloorMatchingRTBExchangeTwoDotThree ...");

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

        if(
            impressionNeedsPMPAds && adTargetsDealId && null != dealIdArrayForThisPublisher &&
            dealIdArrayForThisPublisher.length > 0
          )
        {
            logger.debug("Impression Id:{} has deal id specified, looking if ad:{} is targeting deal id set: {} ",
                          impressionId,adEntity.getAdGuid(),fetchDealIdString(privateDealInfoSet));

            if(request.isRequestForSystemDebugging())
            {
                request.addDebugMessageForTestRequest
                        (
                         "Impression Id:"+impressionId+" has deal id specified, looking if " +
                         "ad:"+adEntity.getAdGuid()+" is targeting deal id set:  "           +
                          fetchDealIdString(privateDealInfoSet)
                        );
            }

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
                        ReqLog.debugWithDebugNew(logger, request, "DealIdByAd: {} matches and fits deal id in impression:{} ",
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