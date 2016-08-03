package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.constants.VideoBidResponseProtocols;

import RTB.Tencent.Request.Impression.Video;

public class ConvertVideo {
    /**
            message Video {
            repeated string mimes = 1; //Supported Content type (e.g. "flv","swf").
            optional uint32 linearity = 2; //No practical effect at present. linear/in-stream(1) or non-linear/overlay(2).
            optional uint32 minduration = 3; //Minimum duration of video ad. Unit: ms.
            optional uint32 maxduration = 4; //Maximum duration of video ad. Unit: ms.
            optional uint32 protocol = 5; //No practical effect at present.Vast 1.0/2.0/3.0/wrapper.
            optional uint32 width = 6; //Width of ad space.
            optional uint32 height = 7; //Height of ad space.
        }
     */

    public static BidRequestImpressionVideoObjectDTO convert(Video video){
        if(video==null){
            return null;
        }
        BidRequestImpressionVideoObjectDTO openrtbvideo = new BidRequestImpressionVideoObjectDTO();
        if(video.getMimesList() != null && video.getMimesList().size()>0){
            String[] mimeArray = video.getMimesList().toArray(new String[0]);
            openrtbvideo.setMimeTypesSupported(mimeArray);
        }
        if(video.hasLinearity()){
            openrtbvideo.setIsVideoLinear(video.getLinearity());
        }
        if(video.hasMinduration()){
            openrtbvideo.setMinimumDurationOfVideo(video.getMinduration()/1000);
        }
        if(video.hasMaxduration()){
            openrtbvideo.setMaxDurationOfVideo(video.getMaxduration()/1000);
        }
        /** Only DIRECT VIDEO as they for now does not support protocol */
        //if(video.hasProtocol()){
            Integer[] videoBidResponseProtocol = new Integer[1];
            videoBidResponseProtocol[0] = VideoBidResponseProtocols.NONVAST.getCode();
            openrtbvideo.setVideoBidResponseProtocol(videoBidResponseProtocol);
        //}
        if(video.hasHeight()){
            openrtbvideo.setHeightVideoPlayerInPixels(video.getHeight());
        }
        if(video.hasWidth()){
            openrtbvideo.setWidthVideoPlayerInPixels(video.getWidth());
        }
        return openrtbvideo;
    }
}
