package com.kritter.tencent.reader_v20150313.converter.request;


import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;

import RTB.Tencent.Request.Impression.Banner;

public class ConvertBanner {

    /**
        message Banner {
            optional uint32 width = 1; //Width of ad space.
            optional uint32 height = 2; //Height of ad space.
            repeated string mimes = 3; //supported material type (e.g. “jpg”, ”swf”).
            optional string extra_style = 4; //Material size that is allowed to be advertised by this ad space in addition to existing ad space specification.Format: width x height. Such as 960*90;1000*90 (separate multiple sizes with “;”).
            optional uint32 visibility = 5; //Screen sequence of ad space. 0: unknown; 1: first screen; 2: non-first screen.
        }
     */
    public static BidRequestImpressionBannerObjectDTO convert(Banner banner){
        if(banner == null){
            return null;
        }
        BidRequestImpressionBannerObjectDTO openrtbBanner =  new BidRequestImpressionBannerObjectDTO();
        if(banner.hasWidth()){
            openrtbBanner.setBannerWidthInPixels(banner.getWidth());
        }
        if(banner.hasHeight()){
            openrtbBanner.setBannerHeightInPixels(banner.getHeight());
        }
        if(banner.getMimesList() != null && banner.getMimesList().size()>0){
            String[] mimeArray = banner.getMimesList().toArray(new String[0]);
            openrtbBanner.setWhitelistedContentMIMEtypes(mimeArray);
        }
        /**
         * TODO extra_style
         */
        
        /**
         * TODO visibility
         */
        return openrtbBanner; 
    }
}
