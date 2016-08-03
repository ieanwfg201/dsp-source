package com.kritter.auction_strategies.validation;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

public class ValidateBestPrice {
    public static boolean validate(BidResponseEntity entity, float firstPrice){
        if(entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice() > firstPrice){
            return true;
        }
        return false;
    }

    public static boolean validate(com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity entity, float firstPrice){
        if(entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice() > firstPrice){
            return true;
        }
        return false;
    }
}
