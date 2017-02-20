package com.kritter.adserving.shortlisting.core.openrtbhelper;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.*;
import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.entity.video_props.VideoProps;

public class ValidateVideoHelper {
    public static NoFillReason validateAPI(VideoProps videoProps, Integer[] supportedApiFrameworkList) throws Exception{
        int code = videoProps.getApi();
        if(APIFrameworks.Unknown.getCode() != code){
            if(supportedApiFrameworkList != null && supportedApiFrameworkList.length>0){
                for(Integer i:supportedApiFrameworkList ){
                    if(code==i){
                        return NoFillReason.FILL;
                    }
                }
                return NoFillReason.Video_ApiFramework;
            }
        }
        return NoFillReason.FILL;
    }

    public static NoFillReason validateMime(VideoProps videoProps, String[] mimeTypesSupported)throws Exception{
        if(mimeTypesSupported != null && mimeTypesSupported.length>0){
            for(String i:mimeTypesSupported ){
                if(i.equals(VideoMimeTypes.getEnum(videoProps.getMime()).getMime())){
                    return NoFillReason.FILL;
                }
            }
            return NoFillReason.Video_Mime;
        }
        return NoFillReason.FILL;
    }

    public static NoFillReason validateDuration(VideoProps videoProps, Integer minDuration, Integer maxDuration)throws Exception{
        if(videoProps.getDuration() == -1){
            return NoFillReason.FILL;
        }
        if(maxDuration != null && videoProps.getDuration()> maxDuration){
            return NoFillReason.Video_Duration;
        }
        if(minDuration != null && videoProps.getDuration()< minDuration){
            return NoFillReason.Video_Duration;
        }
        return NoFillReason.FILL;
    }
    
    public static NoFillReason validateProtocol(VideoProps videoProps, Integer[] videoBidResponseProtocol)throws Exception{
        if(videoBidResponseProtocol != null && videoBidResponseProtocol.length>0){
            for(Integer i:videoBidResponseProtocol){
                if(i==videoProps.getProtocol() || i == VideoBidResponseProtocols.NONVAST.getCode()){
                    return NoFillReason.FILL;
                }
            }
            return NoFillReason.Video_Protocol;
        }
        return NoFillReason.FILL;
    }
    
    public static NoFillReason validateStartDelay(VideoProps videoProps, Integer  startDelay)throws Exception{
        if(videoProps.getStartdelay()== -11){
            return NoFillReason.FILL;
        }
        if(startDelay != null){
            if(startDelay < 1){
                if(startDelay!= videoProps.getStartdelay()){
                    return NoFillReason.Video_StartDelay;
                }
            }else{
                if(videoProps.getStartdelay() < 1 ){
                    return NoFillReason.Video_StartDelay;
                }
            }
        }
        return NoFillReason.FILL;
    }
    
    public static NoFillReason validateWidth(VideoProps videoProps, Integer widthVideoPlayerInPixels)throws Exception{
        if(videoProps.getWidth() == -1){
            return NoFillReason.FILL;
        }
        if(widthVideoPlayerInPixels != null && videoProps.getWidth() > widthVideoPlayerInPixels){
            return NoFillReason.Video_Width;
        }
        return NoFillReason.FILL;
    }
    
    public static NoFillReason validateHeight(VideoProps videoProps, Integer heightVideoPlayerInPixels)throws Exception{
        if(videoProps.getHeight() == -1){
            return NoFillReason.FILL;
        }
        if(heightVideoPlayerInPixels != null && videoProps.getHeight() > heightVideoPlayerInPixels){
            return NoFillReason.Video_Height;
        }
        return NoFillReason.FILL;
    }

    public static NoFillReason validateLinearity(VideoProps videoProps, Integer isVideoLinear)throws Exception{
        int code = videoProps.getLinearity();
            if(isVideoLinear !=  code){
                return NoFillReason.Video_Linearity;
        }
        return NoFillReason.FILL;
    }
    public static NoFillReason validateMaxExtended(VideoProps videoProps, Integer maximumExtendedVideoAdDuration)throws Exception{
        int code = videoProps.getMaxextended();
        if(VideoMaxExtended.Unknown.getCode() != code){
            if(maximumExtendedVideoAdDuration == null && code!=VideoMaxExtended.EXTENSION_NOT_ALLOWED.getCode()){
                return NoFillReason.Video_MaxExtended;
            }
            if(maximumExtendedVideoAdDuration != null){
                if(maximumExtendedVideoAdDuration == VideoMaxExtended.EXTENSION_NOT_ALLOWED.getCode()
                        && code != VideoMaxExtended.EXTENSION_NOT_ALLOWED.getCode()){
                    return NoFillReason.Video_MaxExtended;
                }
                if(maximumExtendedVideoAdDuration > 0 && code > maximumExtendedVideoAdDuration){
                    return NoFillReason.Video_MaxExtended;
                }
            }
        }
        return NoFillReason.FILL;
    }
    
    public static NoFillReason validateBitRate(VideoProps videoProps, Integer minimumBitRateVideoInKbps, Integer maximumBitRateVideoInKbps)throws Exception{
        if(videoProps.getBitrate() == -1){
            return NoFillReason.FILL;
        }
        if(maximumBitRateVideoInKbps != null && videoProps.getBitrate()> maximumBitRateVideoInKbps){
            return NoFillReason.Video_BitRate;
        }
        if(minimumBitRateVideoInKbps != null && videoProps.getBitrate()< minimumBitRateVideoInKbps){
            return NoFillReason.Video_BitRate;
        }
        return NoFillReason.FILL;
    }

    public static NoFillReason validateBoxing(VideoProps videoProps, Integer contentBoxingAllowed)throws Exception{
        int code = videoProps.getBoxingallowed();
        if(VideoBoxing.Unknown.getCode() != code){
            if(contentBoxingAllowed != null && contentBoxingAllowed != code){
                return NoFillReason.Video_Boxing;
            }
        }
        return NoFillReason.FILL;
    }

    public static NoFillReason validatePlayBack(VideoProps videoProps, Integer[] playbackMethods)throws Exception{
        int code = videoProps.getPlaybackmethod();
        if(VideoPlaybackMethods.Unknown.getCode() != code){
            if(playbackMethods != null && playbackMethods.length>0){
                for(Integer i:playbackMethods){
                    if(i == code){
                        return NoFillReason.FILL;
                    }
                }
                return NoFillReason.Video_PlayBack;
            }
        }
        return NoFillReason.FILL;
    }

    public static NoFillReason validateDelivery(VideoProps videoProps, Integer[] supportedDeliveryMethods)throws Exception{
        int code = videoProps.getDelivery();
        if(ContentDeliveryMethods.Unknown.getCode() != code){
            if(supportedDeliveryMethods != null && supportedDeliveryMethods.length>0){
                for(Integer i:supportedDeliveryMethods){
                    if(i == code){
                        return NoFillReason.FILL;
                    }
                }
                return NoFillReason.Video_Delivery;
            }
        }
        return NoFillReason.FILL;
    }
    public static NoFillReason validateCompanionType(VideoProps videoProps, Integer[] companionTypes)throws Exception{
        int code = videoProps.getCompaniontype();
        if(VASTCompanionTypes.Unknown.getCode() != code){
            if(companionTypes != null && companionTypes.length>0){
                for(Integer i:companionTypes){
                    if(i == code){
                        return NoFillReason.FILL;
                    }
                }
                return NoFillReason.Video_CompanionType;
            }
        }
        return NoFillReason.FILL;
    }
    
    public static NoFillReason validatehelper(Logger logger, VideoProps videoProps, Integer[] supportedApiFrameworkList,
            String[] mimeTypesSupported, Integer minDuration, Integer maxDuration, Integer[] videoBidResponseProtocol, Integer  startDelay,
            Integer widthVideoPlayerInPixels, Integer heightVideoPlayerInPixels, Integer isVideoLinear, Integer maximumExtendedVideoAdDuration,
            Integer minimumBitRateVideoInKbps, Integer maximumBitRateVideoInKbps, Integer contentBoxingAllowed, Integer[] playbackMethods,
            Integer[] supportedDeliveryMethods, Integer[] companionTypes){
        try{
                NoFillReason nfr = NoFillReason.FILL;
                nfr = validateAPI(videoProps, supportedApiFrameworkList);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateMime(videoProps, mimeTypesSupported);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateDuration(videoProps, minDuration, maxDuration);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateProtocol(videoProps, videoBidResponseProtocol);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateStartDelay(videoProps, startDelay);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateWidth(videoProps, widthVideoPlayerInPixels);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateHeight(videoProps, heightVideoPlayerInPixels);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateLinearity(videoProps, isVideoLinear);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateMaxExtended(videoProps, maximumExtendedVideoAdDuration);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateBitRate(videoProps, minimumBitRateVideoInKbps, maximumBitRateVideoInKbps);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateBoxing(videoProps, contentBoxingAllowed);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validatePlayBack(videoProps, playbackMethods);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateDelivery(videoProps, supportedDeliveryMethods);
                if(nfr != NoFillReason.FILL) {return nfr;}
                nfr = validateCompanionType(videoProps, companionTypes);
                if(nfr != NoFillReason.FILL) {return nfr;}
                
            return NoFillReason.FILL;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return NoFillReason.Video_Exception;
        }
    }
    public static NoFillReason validate(Logger logger, VideoProps videoProps, BidRequestImpressionVideoObjectDTO  videoObj){
        return validatehelper(logger, videoProps, videoObj.getSupportedAPIFrameworkList(), videoObj.getMimeTypesSupported(),
                videoObj.getMinimumDurationOfVideo(), videoObj.getMaxDurationOfVideo(), videoObj.getVideoBidResponseProtocol(),
                videoObj.getStartDelayInSeconds(), videoObj.getWidthVideoPlayerInPixels(), videoObj.getHeightVideoPlayerInPixels(),
                videoObj.getIsVideoLinear(), videoObj.getMaximumExtendedVideoAdDuration(),videoObj.getMinimumBitRateVideoInKbps() ,
                videoObj.getMaximumBitRateVideoInKbps(), videoObj.getContentBoxingAllowed(), videoObj.getPlaybackMethods(), 
                videoObj.getSupportedDeliveryMethods(), videoObj.getCompanionTypes());
    }
    public static NoFillReason validate(Logger logger, VideoProps videoProps,
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionVideoObjectDTO  videoObj){
        return validatehelper(logger, videoProps, videoObj.getSupportedAPIFrameworkList(), videoObj.getMimeTypesSupported(),
                videoObj.getMinimumDurationOfVideo(), videoObj.getMaxDurationOfVideo(), videoObj.getVideoBidResponseProtocol(),
                videoObj.getStartDelayInSeconds(), videoObj.getWidthVideoPlayerInPixels(), videoObj.getHeightVideoPlayerInPixels(),
                videoObj.getIsVideoLinear(), videoObj.getMaximumExtendedVideoAdDuration(),videoObj.getMinimumBitRateVideoInKbps() ,
                videoObj.getMaximumBitRateVideoInKbps(), videoObj.getContentBoxingAllowed(), videoObj.getPlaybackMethods(), 
                videoObj.getSupportedDeliveryMethods(), videoObj.getCompanionTypes());
    }
    public static NoFillReason validate(Logger logger, VideoProps videoProps,
            com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionVideoObjectDTO  videoObj){
        return validatehelper(logger, videoProps, videoObj.getApi(), videoObj.getMimes(),
                videoObj.getMinduration(), videoObj.getMaxduration(), videoObj.getProtocols(),
                videoObj.getStartdelay(), videoObj.getW(), videoObj.getH(),
                videoObj.getLinearity(), videoObj.getMaxextended(),videoObj.getMinbitrate() ,
                videoObj.getMaxbitrate(), videoObj.getBoxingallowed(), videoObj.getPlaybackmethod(), 
                videoObj.getDelivery(), videoObj.getCompaniontype());
    }
    /*
    @Getter@Setter
    private int companiontype = VASTCompanionTypes.Unknown.getCode();

     */
}
