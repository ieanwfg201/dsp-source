package com.kritter.auction_strategies.validation;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

public class ValidateSecondBestPrice {
    public static boolean validate(BidResponseEntity entity, float secondPrice){
        if(entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice() > secondPrice){
            return true;
        }
        return false;
    }
}
