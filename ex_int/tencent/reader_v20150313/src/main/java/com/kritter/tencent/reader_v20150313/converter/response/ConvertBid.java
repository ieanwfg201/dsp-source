package com.kritter.tencent.reader_v20150313.converter.response;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;

import RTB.Tencent.Response;
import RTB.Tencent.Response.Bid;

public class ConvertBid {
/**
 *     message Bid {
        optional string id = 1; //response id per impression(Impression ID identified by DSP).
        optional string impid = 2; //Required. Identify the impression ID. (set to corresponding Request.Impression.id)
        optional float price = 3; //Bid price, unit: fen/CPM.
        optional string adid = 4; //Ad ID or order ID, in common with the dsp_order_id in material uploading API.
        optional string nurl = 5; //It is not recommended to use this field for now. Win notice url.
        optional string adm = 6; //Expired, not in use at present.
        optional string ext = 7; //Replace the macro ${EXT2} in the impression & click tracking. (base64 encoded preferred，no more than 512 bytes)
        optional string adm_para = 8; //String of the parameter list that is passed to adm.Such as http://creative.dsp.mediav.com/aa.html?cid=xxxx&pid=xxxx. This field will be filled in the part of xxxx.
        optional string deprecated_respinfo = 9; //Ignore it. Useless field.
        optional string ext2 = 10; //Replace the macro ${EXT2} in the impression tracking. (base64 encoded preferred，no more than 512 bytes)
    }
 */
    public static Bid convert(String bidid, String impid, float bidPrice, String adId, String creativeId,
            String ext, String ext2){
        Response.Bid.Builder bidBuilder =  Response.Bid.newBuilder();
        if(bidid != null){
            bidBuilder.setId(bidid);
        }
        if(impid != null){
            bidBuilder.setImpid(impid);
        }
        bidBuilder.setPrice(bidPrice);
        if(adId != null){
            bidBuilder.setAdid(adId+":"+creativeId);
        }
        /** Not Recommended
        if(openrtbBid.getWinNotificationUrl() != null){
        }
         */
        /** Not in Use
        if(openrtbBid.getAdMarkup() != null){
        }
        */
        if(ext != null){
            bidBuilder.setExt(ext);
        }
        if(ext2 != null){
            bidBuilder.setExt2(ext2);
        }
        return bidBuilder.build();
    }
    public static Bid convert(BidResponseBidEntity openrtbBid){
        if(openrtbBid == null){
            return null;
        }
        /** Not Recommended
        if(openrtbBid.getWinNotificationUrl() != null){
        }
         */
        /** Not in Use
        if(openrtbBid.getAdMarkup() != null){
        }
        */
        /**
         * fill ext
         * and ext2
         */
        return convert(openrtbBid.getBidId(), openrtbBid.getRequestImpressionId(), openrtbBid.getPrice(),
               openrtbBid.getAdId(), openrtbBid.getCreativeId(), null, null);
    }
}
