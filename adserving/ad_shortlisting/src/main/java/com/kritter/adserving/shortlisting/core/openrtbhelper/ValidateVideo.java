package com.kritter.adserving.shortlisting.core.openrtbhelper;

import com.kritter.adserving.thrift.struct.NoFillReason;
import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.common.caches.advinfo_upload_cache.AdvInfoUploadCache;
import com.kritter.common.caches.advinfo_upload_cache.entity.AdvInfoUploadCacheEntity;
import com.kritter.common.caches.video_info_cache.VideoInfoCache;
import com.kritter.common.caches.video_info_cache.entity.VideoInfoCacheEntity;
import com.kritter.common.caches.video_upload_cache.VideoUploadCache;
import com.kritter.common.caches.video_upload_cache.entity.VideoUploadCacheEntity;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.CreativeFormat;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadVideo;

public class ValidateVideo {
    private static final String CTRL_A = String.valueOf((char)1);

    public static void checkVideo(BidRequestImpressionDTO[] bidRequestImpressionDTOs,Site site,
            Request request, Logger logger, Response response, AdEntityCache adEntityCache, 
            CreativeCache creativeCache, VideoInfoCache videoInfoCache,
            AdxBasedExchangesMetadata adxBased,
            VideoUploadCache videoUploadCache, AdvInfoUploadCache advInfoUploadCache
            ){
        boolean isNFR = true;
        if(bidRequestImpressionDTOs == null){
                logger.error("bidRequestImpressionDTOs is null inside video of AdShortlistingRTBExchangeTwoDotThree, cannot process request for this impressionId: {} ");
                return;
        }

        NoFillReason nfrReason = NoFillReason.FILL;
        NoFillReason nfrReasonPriority = null;

        for(BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs){
            if(bidRequestImpressionDTO == null){
                continue;
            }
            BidRequestImpressionVideoObjectDTO  videoObj = bidRequestImpressionDTO.getBidRequestImpressionVideoObject();
            if(videoObj == null){
                logger.debug("VideoObj null");
                continue;
            }
            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo()){
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());
                if(null == adEntity){
                    logger.error("AdEntity not found in cache,FATAL error!!! for adId: {} ",
                            responseAdInfo.getAdId());
                    continue;
                }
                if(adxBased != null && adxBased.isAdvertiser_upload()){
                	int advId = adEntity.getAccountId();
            		if(advInfoUploadCache == null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as advInfoUploadCache null", advId);
            			continue;
            		}
            		AdvInfoUploadCacheEntity bue = advInfoUploadCache.query(site.getPublisherIncId()+CTRL_A+advId);
                	if(bue ==null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as AdvInfoUploadCacheEntity  null", advId);
            			continue;
                	}
                	if(bue.getMua() == null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  null", advId);
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMua().getAdxbasedexhangesstatus()){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  not Approved", advId);
            			continue;                        		
                	}
                }

                Creative creative = creativeCache.query(adEntity.getCreativeId());
                if(null == creative) {
                    ReqLog.debugWithDebugNew(logger, request, "Creative null in cache,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(creative.getCreativeFormat() != CreativeFormat.VIDEO){
                    nfrReason = NoFillReason.CREATIVE_NOT_VIDEO;
                    ReqLog.debugWithDebugNew(logger, request, "Creative Not Video,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(!ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(bidRequestImpressionDTO.getBidRequestImpressionId(), site, adEntity, request, responseAdInfo, logger)){
                    nfrReason = NoFillReason.DEAL_ID_MISMATCH;
                    ReqLog.debugWithDebugNew(logger, request, "DealID check not satisfied creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(bidRequestImpressionDTO.getBidFloorPrice() != null &&  bidRequestImpressionDTO.getBidFloorPrice()>responseAdInfo.getEcpmValue()){
                    nfrReason = NoFillReason.BIDDER_FLOOR_UNMET;
                    ReqLog.debugWithDebugNew(logger, request, "Floor price unmet creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                VideoProps videoProps  = creative.getVideoProps();
                if(videoProps == null){
                    nfrReason = NoFillReason.VIDEO_PROPS_NULL;
                    ReqLog.debugWithDebugNew(logger, request, "Video Props Null,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                VideoInfo videoInfo=null;
                if(videoProps != null && videoProps.getVideo_info() != null){
                    logger.debug("video props is not null : {} ", videoProps.toJson());
                    for(String videoId:videoProps.getVideo_info()){
                        logger.debug("Video id is : {} ", videoId);
                        VideoInfoCacheEntity videoInfoCacheEntity = videoInfoCache.query(Integer.parseInt(videoId));
                        if(videoInfoCacheEntity != null){
                            logger.debug("video info is : {} ", videoInfoCacheEntity.toString());
                            videoInfo = videoInfoCacheEntity.getVideoInfo();
                        }
                    }
                }
            	if(videoInfo != null && adxBased != null && adxBased.isVideo_upload()){
            		if(videoUploadCache == null){
            			ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as videoUploadCache  null", videoInfo.getId());
            			continue;
            		}
            		VideoUploadCacheEntity bue = videoUploadCache.query(site.getPublisherIncId()+CTRL_A+videoInfo.getId());
                	if(bue ==null){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as videoUploadCacheEntity  null", videoInfo.getId());
            			continue;
                	}
                	if(bue.getMuv() == null){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as MaterailVideoUploadEntity  null", videoInfo.getId());
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMuv().getAdxbasedexhangesstatus()){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as MaterailVideoUploadEntity  not Approved", videoInfo.getId());
            			continue;                        		
                	}
            	}

                nfrReasonPriority = ValidateVideoHelper.validate(logger, videoProps, videoObj);
                ReqLog.debugWithDebugNew(logger, request, "NFR reason is :{} under ValidateVideo", nfrReason);
                if(nfrReason ==  NoFillReason.FILL && nfrReasonPriority == NoFillReason.FILL){
                    try {
                        responseAdInfo.setVideoProps(videoProps);
                        responseAdInfo.setVideoInfo(videoInfo);
                        response.addResponseAdInfoAgainstBidRequestImpressionId(
                                bidRequestImpressionDTO.getBidRequestImpressionId(),
                                responseAdInfo
                        );
                        isNFR = false;
                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
        if(isNFR)
        {
            /*In case priority nfr reason which is from ValidateVideoHelper.*/
            if(null != nfrReasonPriority && nfrReasonPriority != NoFillReason.FILL){
                nfrReason = nfrReasonPriority;
            }
            if(nfrReason==NoFillReason.FILL){
            	nfrReason=NoFillReason.VIDEO_MISMATCH;
            }
            request.setNoFillReason(nfrReason);
            ReqLog.debugWithDebugNew(logger, request, "Validate Video NFR: {}", nfrReason);
        }
    }

    public static void checkVideo(com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO[] bidRequestImpressionDTOs,
            Site site, Request request, Logger logger, Response response, AdEntityCache adEntityCache, 
            CreativeCache creativeCache, VideoInfoCache videoInfoCache, AdxBasedExchangesMetadata adxBased,
            VideoUploadCache videoUploadCache, AdvInfoUploadCache advInfoUploadCache
            ){
        boolean isNFR = true;
        if(bidRequestImpressionDTOs == null){
                logger.error("bidRequestImpressionDTOs is null inside video of AdShortlistingRTBExchangeTwoDotThree, cannot process request for this impressionId: {} ");
                return;
        }
        NoFillReason nfrReason = NoFillReason.FILL;
        for(com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs){
            if(bidRequestImpressionDTO == null){
                continue;
            }
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionVideoObjectDTO  videoObj = bidRequestImpressionDTO.getBidRequestImpressionVideoObject();
            if(videoObj == null){
                logger.debug("VideoObj null");
                continue;
            }
            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo()){
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());
                if(null == adEntity){
                    logger.debug("AdEntity not found in cache,FATAL error!!! for adId: {} ",
                            responseAdInfo.getAdId());
                    continue;
                }
                if(adxBased != null && adxBased.isAdvertiser_upload()){
                	int advId = adEntity.getAccountId();
            		if(advInfoUploadCache == null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as advInfoUploadCache null", advId);
            			continue;
            		}
            		AdvInfoUploadCacheEntity bue = advInfoUploadCache.query(site.getPublisherIncId()+CTRL_A+advId);
                	if(bue ==null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as AdvInfoUploadCacheEntity  null", advId);
            			continue;
                	}
                	if(bue.getMua() == null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  null", advId);
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMua().getAdxbasedexhangesstatus()){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
                		ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  not Approved", advId);
            			continue;                        		
                	}
                }

                Creative creative = creativeCache.query(adEntity.getCreativeId());
                if(null == creative) {
                    ReqLog.debugWithDebugNew(logger, request, "Creative null in cache,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(creative.getCreativeFormat() != CreativeFormat.VIDEO){
                    ReqLog.debugWithDebugNew(logger, request, "Creative Not Video,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(!ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(bidRequestImpressionDTO.getBidRequestImpressionId(), site, adEntity, request, responseAdInfo, logger)){
                    ReqLog.debugWithDebugNew(logger, request, "DealID check not satisfied creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(bidRequestImpressionDTO.getBidFloorPrice() != null &&  bidRequestImpressionDTO.getBidFloorPrice()>responseAdInfo.getEcpmValue()){
                    ReqLog.debugWithDebugNew(logger, request, "Floor price unmet creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                VideoProps videoProps  = creative.getVideoProps();
                if(videoProps == null){
                    ReqLog.debugWithDebugNew(logger, request, "Video Props Null,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                VideoInfo videoInfo=null;
                if(videoProps != null && videoProps.getVideo_info() != null){
                    for(String videoId:videoProps.getVideo_info()){
                        VideoInfoCacheEntity videoInfoCacheEntity = videoInfoCache.query(Integer.parseInt(videoId));
                        if(videoInfoCacheEntity != null){
                            videoInfo = videoInfoCacheEntity.getVideoInfo();
                        }
                    }
                }
            	if(videoInfo != null && adxBased != null && adxBased.isVideo_upload()){
            		if(videoUploadCache == null){
            			ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as videoUploadCache  null", videoInfo.getId());
            			continue;
            		}
            		VideoUploadCacheEntity bue = videoUploadCache.query(site.getPublisherIncId()+CTRL_A+videoInfo.getId());
                	if(bue ==null){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as videoUploadCacheEntity  null", videoInfo.getId());
            			continue;
                	}
                	if(bue.getMuv() == null){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as MaterailVideoUploadEntity  null", videoInfo.getId());
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMuv().getAdxbasedexhangesstatus()){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as MaterailVideoUploadEntity  not Approved", videoInfo.getId());
            			continue;                        		
                	}
            	}


                nfrReason = ValidateVideoHelper.validate(logger, videoProps, videoObj);
                if(nfrReason ==  NoFillReason.FILL){
                    try {
                        responseAdInfo.setVideoProps(videoProps);
                        responseAdInfo.setVideoInfo(videoInfo);
                        response.addResponseAdInfoAgainstBidRequestImpressionId(
                                bidRequestImpressionDTO.getBidRequestImpressionId(),
                                responseAdInfo
                        );
                        isNFR = false;
                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
        if(isNFR)
        {
            if(nfrReason==NoFillReason.FILL){
            	nfrReason=NoFillReason.VIDEO_MISMATCH;
            }
            request.setNoFillReason(nfrReason);
            ReqLog.debugWithDebugNew(logger, request, "Validate Video NFR: {}", nfrReason);
        }

    }
    
    public static void checkVideo(com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionDTO[] bidRequestImpressionDTOs,Site site,
            Request request, Logger logger, Response response, AdEntityCache adEntityCache, 
            CreativeCache creativeCache, VideoInfoCache videoInfoCache,
            AdxBasedExchangesMetadata adxBased,
            VideoUploadCache videoUploadCache, AdvInfoUploadCache advInfoUploadCache
            ){
        boolean isNFR = true;
        if(bidRequestImpressionDTOs == null){
                logger.error("bidRequestImpressionDTOs is null inside video of AdShortlistingRTBExchangeTwoDotFour, cannot process request for this impressionId: {} ");
                return;
        }

        NoFillReason nfrReason = NoFillReason.FILL;
        NoFillReason nfrReasonPriority = null;

        for(com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionDTO bidRequestImpressionDTO : bidRequestImpressionDTOs){
            if(bidRequestImpressionDTO == null){
                continue;
            }
            com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionVideoObjectDTO  videoObj = bidRequestImpressionDTO.getVideo();
            if(videoObj == null){
                logger.debug("VideoObj null");
                continue;
            }
            for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo()){
                AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());
                if(null == adEntity){
                    logger.error("AdEntity not found in cache,FATAL error!!! for adId: {} ",
                            responseAdInfo.getAdId());
                    continue;
                }
                if(adxBased != null && adxBased.isAdvertiser_upload()){
                	int advId = adEntity.getAccountId();
            		if(advInfoUploadCache == null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as advInfoUploadCache null", advId);
            			continue;
            		}
            		AdvInfoUploadCacheEntity bue = advInfoUploadCache.query(site.getPublisherIncId()+CTRL_A+advId);
                	if(bue ==null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as AdvInfoUploadCacheEntity  null", advId);
            			continue;
                	}
                	if(bue.getMua() == null){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  null", advId);
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMua().getAdxbasedexhangesstatus()){
            			nfrReason = NoFillReason.ADVINFOMISMATCH;
            			ReqLog.debugWithDebugNew(logger, request, "AdvId {} does not qualify as MaterailAdvInoUploadEntity  not Approved", advId);
            			continue;                        		
                	}
                }

                Creative creative = creativeCache.query(adEntity.getCreativeId());
                if(null == creative) {
                    ReqLog.debugWithDebugNew(logger, request, "Creative null in cache,!!! for creative id:{}", adEntity.getCreativeId());
                    continue;
                }
                if(creative.getCreativeFormat() != CreativeFormat.VIDEO){
                    nfrReason = NoFillReason.CREATIVE_NOT_VIDEO;
                    ReqLog.debugWithDebugNew(logger, request, "Creative Not Video,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                if(!ValidatePmp.doesImpressionHasPMPDealIdForAdUnit(bidRequestImpressionDTO.getId(), site, adEntity, request, responseAdInfo, logger)){
                    nfrReason = NoFillReason.DEAL_ID_MISMATCH;
                    ReqLog.debugWithDebugNew(logger, request, "DealID check not satisfied creativeId {}", adEntity.getCreativeId());
                    continue;
                }
                if(bidRequestImpressionDTO.getBidfloor() != null &&  bidRequestImpressionDTO.getBidfloor()>responseAdInfo.getEcpmValue()){
                    nfrReason = NoFillReason.BIDDER_FLOOR_UNMET;
                    ReqLog.debugWithDebugNew(logger, request, "Floor price unmet {}", adEntity.getCreativeId());
                    continue;
                }
                VideoProps videoProps  = creative.getVideoProps();
                if(videoProps == null){
                    nfrReason = NoFillReason.VIDEO_PROPS_NULL;
        			ReqLog.debugWithDebugNew(logger, request, "Video Props Null,!!! for creative id:{} ", adEntity.getCreativeId());
                    continue;
                }
                VideoInfo videoInfo=null;
                if(videoProps != null && videoProps.getVideo_info() != null){
                    logger.debug("video props is not null : {} ", videoProps.toJson());
                    for(String videoId:videoProps.getVideo_info()){
                        logger.debug("Video id is : {} ", videoId);
                        VideoInfoCacheEntity videoInfoCacheEntity = videoInfoCache.query(Integer.parseInt(videoId));
                        if(videoInfoCacheEntity != null){
                            logger.debug("video info is : {} ", videoInfoCacheEntity.toString());
                            videoInfo = videoInfoCacheEntity.getVideoInfo();
                        }
                    }
                }
            	if(videoInfo != null && adxBased != null && adxBased.isVideo_upload()){
            		if(videoUploadCache == null){
            			ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as videoUploadCache  null", videoInfo.getId());
            			continue;
            		}
            		VideoUploadCacheEntity bue = videoUploadCache.query(site.getPublisherIncId()+CTRL_A+videoInfo.getId());
                	if(bue ==null){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as videoUploadCacheEntity  null", videoInfo.getId());
            			continue;
                	}
                	if(bue.getMuv() == null){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as MaterailVideoUploadEntity  null", videoInfo.getId());
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMuv().getAdxbasedexhangesstatus()){
                		ReqLog.debugWithDebugNew(logger, request, "VideoId {} does not qualify as MaterailVideoUploadEntity  not Approved", videoInfo.getId());
            			continue;                        		
                	}
            	}

                nfrReasonPriority = ValidateVideoHelper.validate(logger, videoProps, videoObj);
                ReqLog.debugWithDebugNew(logger, request, "NFR reason is :{} under ValidateVideo", nfrReason);
                if(nfrReason ==  NoFillReason.FILL && nfrReasonPriority == NoFillReason.FILL){
                    try {
                        responseAdInfo.setVideoProps(videoProps);
                        responseAdInfo.setVideoInfo(videoInfo);
                        response.addResponseAdInfoAgainstBidRequestImpressionId(
                                bidRequestImpressionDTO.getId(),
                                responseAdInfo
                        );
                        isNFR = false;
                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
        if(isNFR)
        {
            /*In case priority nfr reason which is from ValidateVideoHelper.*/
            if(null != nfrReasonPriority && nfrReasonPriority != NoFillReason.FILL){
                nfrReason = nfrReasonPriority;
            }
            if(nfrReason==NoFillReason.FILL){
            	nfrReason=NoFillReason.VIDEO_MISMATCH;
            }
            request.setNoFillReason(nfrReason);
            ReqLog.debugWithDebugNew(logger, request, "Validate Video NFR: {}", nfrReason);
        }
    }



}
