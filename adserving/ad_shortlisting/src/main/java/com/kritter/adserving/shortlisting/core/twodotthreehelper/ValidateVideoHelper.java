package com.kritter.adserving.shortlisting.core.twodotthreehelper;

import org.slf4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.constants.APIFrameworks;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.VideoBoxing;
import com.kritter.constants.VideoMaxExtended;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.constants.VideoPlaybackMethods;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.video_props.VideoProps;

public class ValidateVideoHelper {
    public static Request.NO_FILL_REASON validateAPI(VideoProps videoProps, Integer[] supportedApiFrameworkList) throws Exception{
        int code = videoProps.getApi();
        if(APIFrameworks.Unknown.getCode() != code){
            if(supportedApiFrameworkList != null && supportedApiFrameworkList.length>0){
                for(Integer i:supportedApiFrameworkList ){
                    if(code==i){
                        return Request.NO_FILL_REASON.FILL;
                    }
                }
                return Request.NO_FILL_REASON.Video_ApiFramework;
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }

    public static Request.NO_FILL_REASON validateMime(VideoProps videoProps, String[] mimeTypesSupported)throws Exception{
        if(mimeTypesSupported != null && mimeTypesSupported.length>0){
            for(String i:mimeTypesSupported ){
                if(i.equals(VideoMimeTypes.getEnum(videoProps.getMime()).getMime())){
                    return Request.NO_FILL_REASON.FILL;
                }
            }
            return Request.NO_FILL_REASON.Video_Mime;
        }
        return Request.NO_FILL_REASON.FILL;
    }

    public static Request.NO_FILL_REASON validateDuration(VideoProps videoProps, Integer minDuration, Integer maxDuration)throws Exception{
        if(videoProps.getDuration() == -1){
            return Request.NO_FILL_REASON.FILL;
        }
        if(maxDuration != null && videoProps.getDuration()> maxDuration){
            return Request.NO_FILL_REASON.Video_Duration;
        }
        if(minDuration != null && videoProps.getDuration()< minDuration){
            return Request.NO_FILL_REASON.Video_Duration;
        }
        return Request.NO_FILL_REASON.FILL;
    }
    
    public static Request.NO_FILL_REASON validateProtocol(VideoProps videoProps, Integer[] videoBidResponseProtocol)throws Exception{
        if(videoBidResponseProtocol != null && videoBidResponseProtocol.length>0){
            for(Integer i:videoBidResponseProtocol){
                if(i==videoProps.getProtocol()){
                    return Request.NO_FILL_REASON.FILL;
                }
            }
            return Request.NO_FILL_REASON.Video_Protocol;
        }
        return Request.NO_FILL_REASON.FILL;
    }
    
    public static Request.NO_FILL_REASON validateStartDelay(VideoProps videoProps, Integer  startDelay)throws Exception{
        if(videoProps.getStartdelay()== -11){
            return Request.NO_FILL_REASON.FILL;
        }
        if(startDelay != null){
            if(startDelay < 1){
                if(startDelay!= videoProps.getStartdelay()){
                    return Request.NO_FILL_REASON.Video_StartDelay;
                }
            }else{
                if(videoProps.getStartdelay() < 1 ){
                    return Request.NO_FILL_REASON.Video_StartDelay;
                }
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }
    
    public static Request.NO_FILL_REASON validateWidth(VideoProps videoProps, Integer widthVideoPlayerInPixels)throws Exception{
        if(videoProps.getWidth() == -1){
            return Request.NO_FILL_REASON.FILL;
        }
        if(widthVideoPlayerInPixels != null && videoProps.getWidth() > widthVideoPlayerInPixels){
            return Request.NO_FILL_REASON.Video_Width;
        }
        return Request.NO_FILL_REASON.FILL;
    }
    
    public static Request.NO_FILL_REASON validateHeight(VideoProps videoProps, Integer heightVideoPlayerInPixels)throws Exception{
        if(videoProps.getHeight() == -1){
            return Request.NO_FILL_REASON.FILL;
        }
        if(heightVideoPlayerInPixels != null && videoProps.getHeight() > heightVideoPlayerInPixels){
            return Request.NO_FILL_REASON.Video_Height;
        }
        return Request.NO_FILL_REASON.FILL;
    }

    public static Request.NO_FILL_REASON validateLinearity(VideoProps videoProps, Integer isVideoLinear)throws Exception{
        int code = videoProps.getLinearity();
            if(isVideoLinear !=  code){
                return Request.NO_FILL_REASON.Video_Linearity;
        }
        return Request.NO_FILL_REASON.FILL;
    }
    public static Request.NO_FILL_REASON validateMaxExtended(VideoProps videoProps, Integer maximumExtendedVideoAdDuration)throws Exception{
        int code = videoProps.getMaxextended();
        if(VideoMaxExtended.Unknown.getCode() != code){
            if(maximumExtendedVideoAdDuration == null && code!=VideoMaxExtended.EXTENSION_NOT_ALLOWED.getCode()){
                return Request.NO_FILL_REASON.Video_MaxExtended;
            }
            if(maximumExtendedVideoAdDuration != null){
                if(maximumExtendedVideoAdDuration == VideoMaxExtended.EXTENSION_NOT_ALLOWED.getCode()
                        && code != VideoMaxExtended.EXTENSION_NOT_ALLOWED.getCode()){
                    return Request.NO_FILL_REASON.Video_MaxExtended;
                }
                if(maximumExtendedVideoAdDuration > 0 && code > maximumExtendedVideoAdDuration){
                    return Request.NO_FILL_REASON.Video_MaxExtended;
                }
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }
    
    public static Request.NO_FILL_REASON validateBitRate(VideoProps videoProps, Integer minimumBitRateVideoInKbps, Integer maximumBitRateVideoInKbps)throws Exception{
        if(videoProps.getBitrate() == -1){
            return Request.NO_FILL_REASON.FILL;
        }
        if(maximumBitRateVideoInKbps != null && videoProps.getBitrate()> maximumBitRateVideoInKbps){
            return Request.NO_FILL_REASON.Video_BitRate;
        }
        if(minimumBitRateVideoInKbps != null && videoProps.getBitrate()< minimumBitRateVideoInKbps){
            return Request.NO_FILL_REASON.Video_BitRate;
        }
        return Request.NO_FILL_REASON.FILL;
    }

    public static Request.NO_FILL_REASON validateBoxing(VideoProps videoProps, Integer contentBoxingAllowed)throws Exception{
        int code = videoProps.getBoxingallowed();
        if(VideoBoxing.Unknown.getCode() != code){
            if(contentBoxingAllowed != null && contentBoxingAllowed != code){
                return Request.NO_FILL_REASON.Video_Boxing;
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }

    public static Request.NO_FILL_REASON validatePlayBack(VideoProps videoProps, Integer[] playbackMethods)throws Exception{
        int code = videoProps.getPlaybackmethod();
        if(VideoPlaybackMethods.Unknown.getCode() != code){
            if(playbackMethods != null && playbackMethods.length>0){
                for(Integer i:playbackMethods){
                    if(i == code){
                        return Request.NO_FILL_REASON.FILL;
                    }
                }
                return Request.NO_FILL_REASON.Video_PlayBack;
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }

    public static Request.NO_FILL_REASON validateDelivery(VideoProps videoProps, Integer[] supportedDeliveryMethods)throws Exception{
        int code = videoProps.getDelivery();
        if(ContentDeliveryMethods.Unknown.getCode() != code){
            if(supportedDeliveryMethods != null && supportedDeliveryMethods.length>0){
                for(Integer i:supportedDeliveryMethods){
                    if(i == code){
                        return Request.NO_FILL_REASON.FILL;
                    }
                }
                return Request.NO_FILL_REASON.Video_Delivery;
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }
    public static Request.NO_FILL_REASON validateCompanionType(VideoProps videoProps, Integer[] companionTypes)throws Exception{
        int code = videoProps.getCompaniontype();
        if(VASTCompanionTypes.Unknown.getCode() != code){
            if(companionTypes != null && companionTypes.length>0){
                for(Integer i:companionTypes){
                    if(i == code){
                        return Request.NO_FILL_REASON.FILL;
                    }
                }
                return Request.NO_FILL_REASON.Video_CompanionType;
            }
        }
        return Request.NO_FILL_REASON.FILL;
    }
    
    public static Request.NO_FILL_REASON validatehelper(Logger logger, VideoProps videoProps, Integer[] supportedApiFrameworkList,
            String[] mimeTypesSupported, Integer minDuration, Integer maxDuration, Integer[] videoBidResponseProtocol, Integer  startDelay,
            Integer widthVideoPlayerInPixels, Integer heightVideoPlayerInPixels, Integer isVideoLinear, Integer maximumExtendedVideoAdDuration,
            Integer minimumBitRateVideoInKbps, Integer maximumBitRateVideoInKbps, Integer contentBoxingAllowed, Integer[] playbackMethods,
            Integer[] supportedDeliveryMethods, Integer[] companionTypes){
        try{
                Request.NO_FILL_REASON nfr = Request.NO_FILL_REASON.FILL;
                nfr = validateAPI(videoProps, supportedApiFrameworkList);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateMime(videoProps, mimeTypesSupported);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateDuration(videoProps, minDuration, maxDuration);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateProtocol(videoProps, videoBidResponseProtocol);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateStartDelay(videoProps, startDelay);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateWidth(videoProps, widthVideoPlayerInPixels);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateHeight(videoProps, heightVideoPlayerInPixels);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateLinearity(videoProps, isVideoLinear);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateMaxExtended(videoProps, maximumExtendedVideoAdDuration);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateBitRate(videoProps, minimumBitRateVideoInKbps, maximumBitRateVideoInKbps);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateBoxing(videoProps, contentBoxingAllowed);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validatePlayBack(videoProps, playbackMethods);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateDelivery(videoProps, supportedDeliveryMethods);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                nfr = validateCompanionType(videoProps, companionTypes);
                if(nfr != Request.NO_FILL_REASON.FILL) {return nfr;}
                
            return Request.NO_FILL_REASON.FILL;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return Request.NO_FILL_REASON.Video_Exception;
        }
    }
    public static Request.NO_FILL_REASON validate(Logger logger, VideoProps videoProps, BidRequestImpressionVideoObjectDTO  videoObj){
        return validatehelper(logger, videoProps, videoObj.getSupportedAPIFrameworkList(), videoObj.getMimeTypesSupported(),
                videoObj.getMinimumDurationOfVideo(), videoObj.getMaxDurationOfVideo(), videoObj.getVideoBidResponseProtocol(),
                videoObj.getStartDelayInSeconds(), videoObj.getWidthVideoPlayerInPixels(), videoObj.getHeightVideoPlayerInPixels(),
                videoObj.getIsVideoLinear(), videoObj.getMaximumExtendedVideoAdDuration(),videoObj.getMinimumBitRateVideoInKbps() ,
                videoObj.getMaximumBitRateVideoInKbps(), videoObj.getContentBoxingAllowed(), videoObj.getPlaybackMethods(), 
                videoObj.getSupportedDeliveryMethods(), videoObj.getCompanionTypes());
    }
    public static Request.NO_FILL_REASON validate(Logger logger, VideoProps videoProps, 
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionVideoObjectDTO  videoObj){
        return validatehelper(logger, videoProps, videoObj.getSupportedAPIFrameworkList(), videoObj.getMimeTypesSupported(),
                videoObj.getMinimumDurationOfVideo(), videoObj.getMaxDurationOfVideo(), videoObj.getVideoBidResponseProtocol(),
                videoObj.getStartDelayInSeconds(), videoObj.getWidthVideoPlayerInPixels(), videoObj.getHeightVideoPlayerInPixels(),
                videoObj.getIsVideoLinear(), videoObj.getMaximumExtendedVideoAdDuration(),videoObj.getMinimumBitRateVideoInKbps() ,
                videoObj.getMaximumBitRateVideoInKbps(), videoObj.getContentBoxingAllowed(), videoObj.getPlaybackMethods(), 
                videoObj.getSupportedDeliveryMethods(), videoObj.getCompanionTypes());
    }
    /*
    @Getter@Setter
    private int companiontype = VASTCompanionTypes.Unknown.getCode();

     */
}
