package com.kritter.valuemaker.reader_v20160817.converter.response;

import RTB.VamRealtimeBidding.VamResponse;
import RTB.VamRealtimeBidding.VamResponse.Bid;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;


public class ConvertResponse {
    public static VamResponse convert(BidResponseEntity openrtbResponse){
        if(openrtbResponse == null){
            return null;
        }
        VamResponse.Builder builder = VamResponse.newBuilder();
        if(openrtbResponse.getBidRequestId() != null){
            builder.setId(openrtbResponse.getBidRequestId());
        }
        builder.setCur("CNY");
        if(openrtbResponse.getBidResponseSeatBid() != null){
            for(BidResponseSeatBidEntity openrtbSeatbid:openrtbResponse.getBidResponseSeatBid() ){
                for(BidResponseBidEntity openrtbBid:openrtbSeatbid.getBidResponseBidEntities()){
                    Bid bid = ConvertBid.convert(openrtbBid);
                    if(bid != null){
                        builder.addBid(bid);
                    }
                }
            }
        }
        return builder.build();
    }
}
