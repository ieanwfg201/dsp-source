package com.kritter.tencent.reader_v20150313.converter.response;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;

import RTB.Tencent.Response;
import RTB.Tencent.Response.SeatBid;

public class ConvertResponse {
/**
 * message Response {
    optional string id = 1; //bid request id, be equal to ID Request.id. Required.
    optional string bidid = 2; //bid response id, not be actually used.
    repeated SeatBid seatbid = 3; //Return the bidding information. Currently only Setting the result in seatbid[0] is supported.
}   
 */
    public static Response convert(BidResponseEntity openrtbResponse){
        if(openrtbResponse == null){
            return null;
        }
        Response.Builder builder = Response.newBuilder();
        if(openrtbResponse.getBidRequestId() != null){
            builder.setId(openrtbResponse.getBidRequestId());
        }
        if(openrtbResponse.getBidderGeneratedUniqueId() != null){
            builder.setBidid(openrtbResponse.getBidderGeneratedUniqueId());
        }
        if(openrtbResponse.getBidResponseSeatBid() != null){
            for(BidResponseSeatBidEntity openrtbSeatbid:openrtbResponse.getBidResponseSeatBid() ){
                SeatBid seatbid = ConvertSeatBid.convert(openrtbSeatbid);
                if(seatbid != null){
                    builder.addSeatbid(seatbid);
                }
            }
        }
        return builder.build();
    }
}
