package com.kritter.tencent.reader_v20150313.converter.response;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;

import RTB.Tencent.Response;
import RTB.Tencent.Response.Bid;
import RTB.Tencent.Response.SeatBid;

public class ConvertSeatBid {
/**
    message SeatBid {
    repeated Bid bid = 1; //Corresponds to the impression in request. Multiple or a part of the impressions can be respondedwith the participation of bidding.
    }

 */
    public static SeatBid convert(BidResponseSeatBidEntity openrtbSeatBid){
        if(openrtbSeatBid == null || openrtbSeatBid.getBidResponseBidEntities() == null
                || openrtbSeatBid.getBidResponseBidEntities().length < 1){
            return null;
        }
        Response.SeatBid.Builder builder = Response.SeatBid.newBuilder();
        for(BidResponseBidEntity openrtbBid:openrtbSeatBid.getBidResponseBidEntities()){
            Bid bid = ConvertBid.convert(openrtbBid);
            if(bid != null){
                builder.addBid(bid);
            }
        }
        return builder.build();
    }
}
