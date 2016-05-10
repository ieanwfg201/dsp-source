package com.kritter.adserving.shortlisting.core.twodotthreehelper;

import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionNativeObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Asset;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Data;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Image;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Native;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Title;
import com.kritter.common.caches.native_icon_cache.NativeIconCache;
import com.kritter.common.caches.native_icon_cache.entity.NativeIconCacheEntity;
import com.kritter.common.caches.native_screenshot_cache.NativeScreenshotCache;
import com.kritter.common.caches.native_screenshot_cache.entity.NativeScreenshotCacheEntity;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.NativeDataAssetType;
import com.kritter.constants.NativeIconImageSize;
import com.kritter.constants.NativeScreenShotImageSize;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;

public class ValidateNative {
    public static void checkNative(BidRequestImpressionDTO[] bidRequestImpressionDTOs,Site site,
            Request request, Logger logger, Response response, AdEntityCache adEntityCache, CreativeCache creativeCache,
            NativeIconCache nativeIconCache,
            NativeScreenshotCache nativeScreenshotCache){
        boolean isNFR = true;
        if(bidRequestImpressionDTOs == null){
                logger.error("bidRequestImpressionDTOs is null inside native of " +
                             "AdShortlistingRTBExchangeTwoDotThree, cannot process " +
                             "request for this impressionId: {} ");
                return;
        }
        for(BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs){
            if(bidRequestImpressionDTO == null){
                continue;
            }
            BidRequestImpressionNativeObjectDTO bNative = bidRequestImpressionDTO.getBidRequestImpressionNativeObjectDTO();
            Native nativeObj = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                nativeObj  = objectMapper.readValue(bNative.getRequest(), Native.class);
            } catch (Exception e1) {
                logger.error(e1.getMessage(),e1);
                nativeObj = null;
            } 
            if(nativeObj == null){
                logger.error("NativeObj null");
                continue;
            }
            Asset[] assets = nativeObj.getAssets();
            if(assets == null){
                logger.error("NativeObj Assets null");
                continue;
            }
            

            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo()){
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());
                if(null == adEntity){
                    logger.error("AdEntity not found in cache,FATAL error!!! for adId: {} " +
                            responseAdInfo.getAdId());
                    continue;
                }
                Creative creative = creativeCache.query(adEntity.getCreativeId());
                if(null == creative) {
                    logger.error("Creative null in cache,!!! for creative id: " + adEntity.getCreativeId());
                    continue;
                }
                if(creative.getCreativeFormat() != CreativeFormat.Native){
                    logger.error("Creative Not Native,!!! for creative id: " + adEntity.getCreativeId());
                    continue;
                }
                if(!ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(bidRequestImpressionDTO.getBidRequestImpressionId(), site, adEntity, request, responseAdInfo, logger)){
                    logger.error("DealID check not satisfied");
                    continue;
                }
                if(bidRequestImpressionDTO.getBidFloorPrice() != null &&  bidRequestImpressionDTO.getBidFloorPrice()>responseAdInfo.getEcpmValue()){
                    logger.error("Floor price unmet");
                    continue;
                }
                NativeDemandProps nativeDemandPros  = creative.getNative_demand_props();
                if(nativeDemandPros == null){
                    logger.error("Native Demand Props Null,!!! for creative id: " + adEntity.getCreativeId());
                    continue;
                }
                HashMap<String, NativeIcon> nativeIconMap = new HashMap<String, NativeIcon>();
                if(nativeDemandPros.getIcons() != null){
                    for(Integer iconId:nativeDemandPros.getIcons()){
                        NativeIconCacheEntity nativeIconCacheEntity = nativeIconCache.query(iconId);
                        if(nativeIconCacheEntity != null){
                            nativeIconMap.put(NativeIconImageSize.getEnum(nativeIconCacheEntity.getNativeIcon().getIcon_size()).getName(), nativeIconCacheEntity.getNativeIcon());
                        }
                    }
                }
                HashMap<String, NativeScreenshot> nativeScreenshotMap = new HashMap<String, NativeScreenshot>();
                if(nativeDemandPros.getScreenshots() != null){
                    for(Integer screenshotId:nativeDemandPros.getScreenshots()){
                        NativeScreenshotCacheEntity nativeScreenshotCacheEntity = nativeScreenshotCache.query(screenshotId);
                        if(nativeScreenshotCacheEntity != null){
                            nativeScreenshotMap.put(NativeScreenShotImageSize.getEnum(nativeScreenshotCacheEntity.getNativeScreenshot().getSs_size()).getName(), nativeScreenshotCacheEntity.getNativeScreenshot());
                        }
                    }
                }
                boolean allPassed = true;
                NativeDemandProps nativeDemandPropseActual = new NativeDemandProps();
                for(Asset asset:assets){
                    if(!allPassed){
                        break;
                    }
                    boolean pass = true;
                    Title title = asset.getTitle();
                    if(title != null && title.getLen() != null){
                        if(title.getLen() >= nativeDemandPros.getTitle().length()){
                            nativeDemandPropseActual.setTitle(nativeDemandPros.getTitle());
                        }else{
                            pass=false;
                        }
                    }
                    if(!pass){continue;}
                    Image img = asset.getImg();
                    if(img != null && img.getW() != null && img.getH() != null){
                        String s  = img.getW()+"*"+img.getH();
                        if(nativeIconMap.get(s) != null){
                            responseAdInfo.setNativeIcon(nativeIconMap.get(s));
                        }else{
                            if(nativeScreenshotMap.get(s) != null){
                                responseAdInfo.setNativeScreenshot(nativeScreenshotMap.get(s));
                            }else{
                                pass = false;
                            }
                        }
                    }
                    Data data = asset.getData();
                    if(data != null && data.getLen() != null && data.getType()== NativeDataAssetType.desc.getCode()){
                        if(data.getLen() >= nativeDemandPros.getDesc().length()){
                            nativeDemandPropseActual.setDesc(nativeDemandPros.getDesc());
                        }else{
                            pass=false;
                        }
                    }
                    if(!pass){
                        allPassed = false;
                    }
                }
                if(allPassed){
                    try {
                        responseAdInfo.setNativeDemandProps(nativeDemandPropseActual);
                        response.addResponseAdInfoAgainstBidRequestImpressionId(
                                bidRequestImpressionDTO.getBidRequestImpressionId(),
                                responseAdInfo
                        );
                        isNFR = true;
                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
        if(isNFR){
            request.setNoFillReason(Request.NO_FILL_REASON.NATIVE_MISMATCH);
        }
    }
}
