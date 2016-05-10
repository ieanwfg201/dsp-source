package com.kritter.adserving.shortlisting.core;

import com.kritter.entity.native_props.NativeProps;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Title;
import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.native_icon_cache.NativeIconCache;
import com.kritter.common.caches.native_icon_cache.entity.NativeIconCacheEntity;
import com.kritter.common.caches.native_screenshot_cache.NativeScreenshotCache;
import com.kritter.common.caches.native_screenshot_cache.entity.NativeScreenshotCacheEntity;
import com.kritter.common.caches.slot_size_cache.CreativeSlotSizeCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.RICHMEDIA_TYPE;
import com.kritter.serving.demand.cache.*;
import com.kritter.serving.demand.entity.*;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.DemandPreference;
import com.kritter.constants.DemandType;
import com.kritter.utils.common.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class takes input as adids and performs creative level
 * publisher filtering and creative attributes matching with
 * request and returns set of ad ids for formatting.
 *
 * e.g. filtering could be based on
 * 1. creative attributes like richmedia,text,banner,video,etc.
 * 2. slot size.
 */

public class CreativeTargetingMatcher
{
    private Logger logger;
    private CreativeBannerCache creativeBannerCache;
    private CreativeCache creativeCache;
    private AdEntityCache adEntityCache;
    private CreativeSlotCache creativeSlotCache;
    private CreativeSlotSizeCache creativeSlotSizeCache;
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    private Comparator<CreativeBanner> comparator = null;
    private NativeIconCache nativeIconCache;
    private NativeScreenshotCache nativeScreenshotCache;
    private AccountCache accountCache;
    
    public CreativeTargetingMatcher(String loggerName,
                                    CreativeBannerCache creativeBannerCache,
                                    CreativeCache creativeCache,
                                    AdEntityCache adEntityCache,
                                    CreativeSlotCache creativeSlotCache,
                                    CreativeSlotSizeCache creativeSlotSizeCache,
                                    NativeIconCache nativeIconCache,
                                    NativeScreenshotCache nativeScreenshotCache,
                                    AccountCache accountCache)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.creativeBannerCache = creativeBannerCache;
        this.creativeCache = creativeCache;
        this.adEntityCache = adEntityCache;
        this.creativeSlotCache = creativeSlotCache;
        this.creativeSlotSizeCache = creativeSlotSizeCache;
        this.comparator = new BannerSizeComparator();
        this.nativeIconCache = nativeIconCache;
        this.nativeScreenshotCache = nativeScreenshotCache;
        this.accountCache = accountCache;
    }


    /**
     * This method is responsible to check for each adid if:

     * 1. The associated creative is allowed by the requesting site.
     * 2. If the dimensions of the requesting slot allows the creative
     *    banner if creative is banner.
     * @param request
     * @return Set<ResponseAdInfo>
     */
    public void processAdIdsForCreativeFiltering(
                                                 Request request,
                                                 Response response,
                                                 Site site
                                                )
    {
        if(null==response.getResponseAdInfo() || response.getResponseAdInfo().size() == 0)
            return;
        
        int requestedWidths[] = request.getRequestedSlotWidths();
        int requestedHeights[] = request.getRequestedSlotHeights();

        if(null != requestedWidths && null != requestedHeights)
        {
            if(requestedWidths.length != requestedHeights.length)
            {
                logger.error("The requested slot heights and widths array are not of same length,no ad can be selected.");
                return;
            }
        }

        int width = -1;
        int height = -1;
        List<Short> requestedSlotIdList = new ArrayList<Short>();
        if(!site.isNative()){
            if(null != requestedWidths && requestedWidths.length == 1)
                width = requestedWidths[0];
            if(null != requestedHeights && requestedHeights.length == 1)
                height = requestedHeights[0];
        }
        if(width != -1 && height != -1)
        {
            short requestedSlotId = this.creativeSlotSizeCache.fetchClosestSlotIdForSize(width,height);
            if(-1 != requestedSlotId)
                 requestedSlotIdList.add(requestedSlotId);
        }
        request.setRequestedSlotIdList(requestedSlotIdList);

        boolean creativeAttributesMatchAtleastOnce = false;

        //Below variables to be used for no-fill reason.
        boolean creativeFoundForRequestedSlot = false;
        boolean richMediaAdFound = false;

        //first check if site's creative preference matches with ad's creative
        //then check for size if required.
        Set<Short> siteCreativeAttributes = (null == site.getCreativeAttributesForInclusionExclusion() ?
                                             new HashSet<Short>() :
                                             new HashSet<Short>(
                                                    Arrays.asList(site.getCreativeAttributesForInclusionExclusion()))
                                            );

        //this means dont care inc or exc for creative attributes.
        if(siteCreativeAttributes.size() <= 0)
            creativeAttributesMatchAtleastOnce = true;

        Set<ResponseAdInfo> responseAdInfoSetForUse = new HashSet<ResponseAdInfo>();

        for(ResponseAdInfo responseAdInfo: response.getResponseAdInfo())
        {
            AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

            if(null == adEntity)
            {
                logger.error("AdEntity not found in cache,FATAL error!!! for adId: {} ",
                             responseAdInfo.getAdId());
                continue;
            }

            Creative creative = creativeCache.query(adEntity.getCreativeId());

            if(null == creative)
            {
                logger.error("Creative null in cache,FATAL error!!! for creative id: " + adEntity.getCreativeId());
                continue;
            }

            Set<Short> adCreativeAttributes = (null == creative.getCreativeAttributes() ?
                                               new HashSet<Short>() :
                                               new HashSet<Short>(
                                                       Arrays.asList(creative.getCreativeAttributes()))
                                                                 );

            Set<Short> resultingIntersection = SetUtils.intersectNSets(siteCreativeAttributes,adCreativeAttributes);

            logger.debug("Site creative attributes and ad creative attributes intersection size: {} and site's " +
                         "policy for these creative attributes is exclusion? : {}",
                         resultingIntersection.size(), site.isCreativeAttributesForExclusion());

            if(     siteCreativeAttributes.size() > 0                                       &&
                    (
                     site.isCreativeAttributesForExclusion() &&
                     null != resultingIntersection &&
                     resultingIntersection.size() > 0
                    )                                                                       ||
                    (
                     !site.isCreativeAttributesForExclusion() &&
                     (null == resultingIntersection || resultingIntersection.size() == 0 )
                    )
              )
            {
                //the creative is not appropriate for the requesting site.
                ReqLog.debugWithDebug(logger, request, "Creative id: {} does not qualify for creative attributes demanded by the siteid: {}",
                             adEntity.getCreativeId(), site.getId());
                continue;
            }
            else
                creativeAttributesMatchAtleastOnce = true;
            
            AccountEntity accountEntity = this.accountCache.query(request.getSite().getPublisherId());
            DemandPreference demandPreference = request.getSite().getDemandPreference();
            if(accountEntity != null){
                if(null == demandPreference)
                    demandPreference = accountEntity.getDemandPreference();
            }
            int demandTypeCode = adEntity.getDemandtype();
            if(demandTypeCode==DemandType.DSP.getCode() && demandPreference != null && 
                    (demandPreference==DemandPreference.OnlyDSP || demandPreference==DemandPreference.DirectThenDSP)){
                responseAdInfoSetForUse.add(responseAdInfo);
                continue;
            }

            if(site.isNative() && !creative.getCreativeFormat().equals(CreativeFormat.Native)){
                ReqLog.debugWithDebug(logger, request, "Site is Native but Creative not native " , creative.getId());
                continue;
            }
            //if creative type is banner then check for size otherwise just allow.
            if(creative.getCreativeFormat().equals(CreativeFormat.BANNER))
            {
                boolean sizeCheckForBanner = false;

                //first sort banner uri ids on size and then use.
                List<CreativeBanner> creativeBannerList = new ArrayList<CreativeBanner>();
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

                Integer bannerUriIds[] = new Integer[creative.getBannerUriIds().length];

                for(int i=0 ; i < creative.getBannerUriIds().length ; i++)
                {
                    bannerUriIds[i] = creativeBannerList.get(i).getId();
                }

                //done sorting banner uri ids on size.
                for(Integer bannerId : bannerUriIds)
                {
                    CreativeBanner creativeBanner = creativeBannerCache.query(bannerId);
                    if(null == creativeBanner)
                    {
                        logger.error("Creative banner is null(not found in cache) for banner id: " + bannerId);
                        continue;
                    }

                    CreativeSlot creativeSlot = creativeSlotCache.query(creativeBanner.getSlotId());

                    if(null == creativeSlot)
                    {
                        logger.error("Creative slot is null(not found in cache) for slot id: " +
                                      creativeBanner.getSlotId());
                        continue;
                    }

                    ReqLog.debugWithDebug(logger, request, "Creative Slot width and height: {} , : {} , requestedWidth: {} , requestedHeight:{} ",
                                 creativeSlot.getCreativeSlotWidth(),creativeSlot.getCreativeSlotHeight(),
                                 requestedWidths[0],requestedHeights[0]);

                    if(isCreativeSlotFitForRequestingSlot(creativeSlot,requestedWidths,requestedHeights))
                    {
                        sizeCheckForBanner = true;

                        /********************************************************************************************/
                        //even if one such creative banner found for request then nofill reason wont be creative_size.
                        creativeFoundForRequestedSlot = true;
                        /********************************************************************************************/

                        responseAdInfo.setCreativeBanner(creativeBanner);
                        responseAdInfo.setSlotId(creativeBanner.getSlotId());
                        responseAdInfoSetForUse.add(responseAdInfo);
                        break;
                    }
                }

                if(!sizeCheckForBanner)
                {
                    ReqLog.errorWithDebug(logger, request, "We could not find any creative supporting the requesting sizes of (width,height) " +
                            "combinations: " +
                            fetchRequestedWidthAndHeightPairForDebug(requestedWidths,requestedHeights) +
                            " for/by creativeid: " + creative.getId());
                }
            } 
            else if(creative.getCreativeFormat().equals(CreativeFormat.Native)) {
                ReqLog.debugWithDebug(logger, request, "checking for native creative id " , creative.getId());
                if(!site.isNative()){
                    logger.debug("Site is not native: {} ", site.getSiteGuid());
                    continue;
                }
                NativeProps nativeSiteProps = site.getNativeProps();
                if(nativeSiteProps == null){
                    logger.debug("Site's native props are null, site guid: {} ", site.getSiteGuid());
                    continue;
                }
                NativeDemandProps nativeDemandProps = creative.getNative_demand_props();
                if(nativeDemandProps == null){
                    logger.debug("Native Demand props is null for creative id: {} ", creative.getId());
                    continue;
                }
                NativeDemandProps nativeDemandPropsActual = new NativeDemandProps();
                if(nativeSiteProps.getTitle_maxchars() != null){
                    if(nativeDemandProps.getTitle() != null){
                        if(nativeDemandProps.getTitle().length() <= nativeSiteProps.getTitle_maxchars()){
                            nativeDemandPropsActual.setTitle(nativeDemandProps.getTitle());
                        }
                    }
                }
                if(nativeSiteProps.getDescription_maxchars() != null){
                    if(nativeDemandProps.getDesc() != null){
                        if(nativeDemandProps.getDesc().length() <= nativeSiteProps.getDescription_maxchars()){
                            nativeDemandPropsActual.setDesc(nativeDemandProps.getDesc());
                        }
                    }
                }
                boolean iconCheckPassed = false;
                NativeIcon nativeIcon = null;
                if(nativeSiteProps.getIcon_imagesize() != null){
                    List<Integer> iconIdList = nativeDemandProps.getIcons();
                    for(Integer iconId:iconIdList){
                        if(iconCheckPassed){
                            break;
                        }
                        NativeIconCacheEntity nativeIconCacheEntity = nativeIconCache.query(iconId);
                        if(nativeIconCacheEntity != null){
                            nativeIcon = nativeIconCacheEntity.getNativeIcon();
                            if(nativeIcon != null){
                                if(nativeIcon.getIcon_size() == nativeSiteProps.getIcon_imagesize()){
                                    iconCheckPassed = true;
                                }
                            }
                        }
                    }
                }else{
                    iconCheckPassed = true;
                }
                if(!iconCheckPassed){
                    logger.debug("Icon check failed for creative id : {} " , creative.getId());
                    continue;
                }
                boolean screenshotCheckPassed = false;
                NativeScreenshot nativeScreenshot = null;
                if(nativeSiteProps.getScreenshot_imagesize() != null){
                    List<Integer> screenShotIdList = nativeDemandProps.getScreenshots();
                    for(Integer screenshotId:screenShotIdList){
                        if(screenshotCheckPassed){
                            break;
                        }
                        NativeScreenshotCacheEntity nativeScreenshotCacheEntity = nativeScreenshotCache.query(screenshotId);
                        if(nativeScreenshotCache != null){
                            nativeScreenshot = nativeScreenshotCacheEntity.getNativeScreenshot();
                            if(nativeScreenshot != null){
                                if(nativeScreenshot.getSs_size() == nativeSiteProps.getScreenshot_imagesize()){
                                    screenshotCheckPassed = true;
                                }
                            }
                        }
                    }
                }else{
                    screenshotCheckPassed = true;
                }
                if(!screenshotCheckPassed){
                    logger.debug("Screenshot check failed for creative id : {} " , creative.getId());
                    continue;
                }
                ReqLog.debugWithDebug(logger, request, "Match found for native creative id" + creative.getId());
                creativeFoundForRequestedSlot = true;
                responseAdInfo.setNativeIcon(nativeIcon);
                responseAdInfo.setNativeScreenshot(nativeScreenshot);
                responseAdInfo.setNativeDemandProps(nativeDemandPropsActual);
                responseAdInfoSetForUse.add(responseAdInfo);
            }
            //if the creative type is richmedia then check for if handset allows javascript
            //and if the site allows richmedia ads or if richmedia flag set in request use that.
            else if(creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA))
            {
                boolean isRichmediaAllowed = false;

                if(site.isRichMediaAllowed())
                    isRichmediaAllowed = true;

                //in case request has richmedia flag set ,use it and override site preference.
                if(null != request.getRichMediaParameterValue() &&
                   RICHMEDIA_TYPE.RICHMEDIA_INTERSTITIAL.getCode() != request.getRichMediaParameterValue().intValue())
                    isRichmediaAllowed = false;

                //if richmedia allowed, check if handset is compatible with javascript
                if(isRichmediaAllowed && request.getHandsetMasterData().isDeviceJavascriptCompatible())
                {
                    ReqLog.debugWithDebug(logger, request, "Site allows richmedia ads and handset is javascript compatible, allowing ad: {} ",
                                 adEntity.getAdGuid());
                    responseAdInfo.setRichMediaAdIsCompatibleForAdserving(true);
                    richMediaAdFound = true;
                    responseAdInfoSetForUse.add(responseAdInfo);
                }
            }
            /*if text ad then just allow*/
            else if(creative.getCreativeFormat().equals(CreativeFormat.TEXT))
            {
                ReqLog.requestDebug(request, "Text ad found for site..."+site.getSiteGuid()+" and ad id as: "+adEntity.getAdGuid());
                creativeFoundForRequestedSlot = true;
                responseAdInfoSetForUse.add(responseAdInfo);
            }
        }

        response.setResponseAdInfo(responseAdInfoSetForUse);

        if(!richMediaAdFound && !creativeAttributesMatchAtleastOnce && null == request.getNoFillReason())
            request.setNoFillReason(Request.NO_FILL_REASON.CREATIVE_ATTR);

        if(!richMediaAdFound && !creativeFoundForRequestedSlot && null == request.getNoFillReason())
            request.setNoFillReason(Request.NO_FILL_REASON.CREATIVE_SIZE);
        if(!site.isNative() && !creativeFoundForRequestedSlot){
            request.setNoFillReason(Request.NO_FILL_REASON.NATIVE_MISMATCH);
        }
    }

    private String fetchRequestedWidthAndHeightPairForDebug(int[] requestedWidths,int[] requestedHeights)
    {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < requestedWidths.length; i++)
        {
            sb.append(requestedWidths[i]);
            sb.append(COMMA);
            sb.append(requestedHeights[i]);
            sb.append(SEMICOLON);
        }

        return sb.toString();
    }

    private boolean isCreativeSlotFitForRequestingSlot(
                                                       CreativeSlot creativeSlot,
                                                       int requestedWidths[],
                                                       int requestedHeights[]
                                                      )
    {
        for(int i=0; i < requestedWidths.length; i++)
        {
            if(
               requestedWidths[i] >= creativeSlot.getCreativeSlotWidth() &&
               requestedHeights[i] >= creativeSlot.getCreativeSlotHeight()
              )
            {
                return true;
            }
        }

        return false;
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

            if(creativeSlotFirst.getCreativeSlotWidth().shortValue() ==
               creativeSlotSecond.getCreativeSlotWidth().shortValue() &&
               creativeSlotFirst.getCreativeSlotHeight().shortValue() >
               creativeSlotSecond.getCreativeSlotHeight().shortValue()
              )
                return -1;

            return 1;
        }
    }
}
