package com.kritter.auction_strategies.validation;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

public class ValidateBidResponse {
    public static boolean validate(BidResponseEntity entity, Request request){
        if(entity != null && request !=null && request.getSite() != null){
            if(entity.getBidResponseSeatBid() != null && entity.getBidResponseSeatBid().length>0 
                    && entity.getBidResponseSeatBid()[0].getBidResponseBidEntities() != null 
                    && entity.getBidResponseSeatBid()[0].getBidResponseBidEntities().length>0){
                    return true;
            }
        }
        return false;
    }
}
