package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionVideoObjectDTO;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.APIFrameworks;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.AdPositionsOpenRTB;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoBoxing;
import com.kritter.constants.VideoMaxExtended;
import com.kritter.constants.VideoPlaybackMethods;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.video_supply_props.VideoSupplyProps;

public class ConvertBidRequestImpVideo {
    public static ConvertErrorEnum convert(Request request, BidRequestImpressionDTO bidRequestImpressionDTO, int version){
        Site site = request.getSite();
        if(site == null){
            return ConvertErrorEnum.REQ_SITE_NF;
        }
        if(!site.isVideo()){
            return ConvertErrorEnum.HEALTHY_CONVERT;
        }
        VideoSupplyProps supplyVideo = site.getVideoSupplyProps();
        if(supplyVideo == null){
        	return ConvertErrorEnum.REQ_VIDEO_PROP_NF;
        }
        BidRequestImpressionVideoObjectDTO video = new BidRequestImpressionVideoObjectDTO();
        if(supplyVideo.getApi() != null){
        	video.setSupportedAPIFrameworkList(VideoSupplyProps.toIntArray(supplyVideo.getApi() ,APIFrameworks.Unknown.getCode()));
        }
        if(supplyVideo.getBoxingallowed() !=null && VideoBoxing.Unknown.getCode() != supplyVideo.getBoxingallowed()){
        	video.setContentBoxingAllowed(supplyVideo.getBoxingallowed());
        }
        if(supplyVideo.getCompaniontype() != null){
        	video.setCompanionTypes(VideoSupplyProps.toIntArray(supplyVideo.getCompaniontype(),VASTCompanionTypes.Unknown.getCode()));
        }
        if(supplyVideo.getDelivery() != null){
        	video.setSupportedDeliveryMethods(VideoSupplyProps.toIntArray(supplyVideo.getDelivery(),ContentDeliveryMethods.Unknown.getCode()));
        }
        if(supplyVideo.getHeightPixel() != null && supplyVideo.getHeightPixel()>0){
        	video.setHeightVideoPlayerInPixels(supplyVideo.getHeightPixel());
        }
        if(supplyVideo.getWidthPixel() != null && supplyVideo.getWidthPixel()>0){
        	video.setWidthVideoPlayerInPixels(supplyVideo.getWidthPixel());
        }
        if(supplyVideo.getMaxBitrate() != null && supplyVideo.getMaxBitrate()>0){
        	video.setMaximumBitRateVideoInKbps(supplyVideo.getMaxBitrate());
        }
        if(supplyVideo.getMinBitrate() != null && supplyVideo.getMinBitrate()>0){
        	video.setMinimumBitRateVideoInKbps(supplyVideo.getMinBitrate());
        }
        if(supplyVideo.getMaxDurationSec() != null && supplyVideo.getMaxDurationSec()>0){
        	video.setMaxDurationOfVideo(supplyVideo.getMaxDurationSec());
        }
        if(supplyVideo.getMinDurationSec() != null && supplyVideo.getMinDurationSec()>0){
        	video.setMinimumDurationOfVideo(supplyVideo.getMinDurationSec());
        }
        if(supplyVideo.getStartDelay() != null && supplyVideo.getStartDelay() > -3){
        	video.setStartDelayInSeconds(supplyVideo.getStartDelay());
        }
        if(supplyVideo.getPos() != null && AdPositionsOpenRTB.Unknown.getCode() != supplyVideo.getPos()){
        	video.setAdPosition(supplyVideo.getPos() );
        }
        if(supplyVideo.getPlaybackmethod() != null){
        	video.setPlaybackMethods(VideoSupplyProps.toIntArray(supplyVideo.getPlaybackmethod(),VideoPlaybackMethods.Unknown.getCode()));
        }
        if(supplyVideo.getMaxextended() != null && supplyVideo.getMaxextended() != VideoMaxExtended.Unknown.getCode()){
        	video.setMaximumExtendedVideoAdDuration(supplyVideo.getMaxextended());
        }
        if(supplyVideo.getLinearity() != null){
        	video.setIsVideoLinear(supplyVideo.getLinearity() );
        }
        if(supplyVideo.getPlaybackmethod() != null){
        	video.setPlaybackMethods(VideoSupplyProps.toIntArray(supplyVideo.getPlaybackmethod(),VideoPlaybackMethods.Unknown.getCode()));
        }
        if(supplyVideo.getProtocols() != null){
        	video.setVideoBidResponseProtocol(VideoSupplyProps.toIntArray(supplyVideo.getProtocols(),VideoBidResponseProtocols.NONVAST.getCode()));
        }
        if(supplyVideo.getMimes() != null){
        	video.setMimeTypesSupported(VideoSupplyProps.toMimes(supplyVideo.getMimes() ));
        }

        bidRequestImpressionDTO.setBidRequestImpressionVideoObject(video);
        return ConvertErrorEnum.HEALTHY_CONVERT;
        
    }
}
