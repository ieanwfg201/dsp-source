package com.kritter.auction_strategies.validation;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

public class ValidateFloor {
    public static boolean validate(BidResponseEntity entity, Request request){
        if(entity != null && request !=null && request.getSite() != null){
            double floor = request.getSite().getEcpmFloorValue();
            if(floor <= 0){
                return true;
            }
            if(entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice() >= floor){
                return true;
            }
        }
        return false;
    }

    public static boolean validate(com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity entity, Request request){
        if(entity != null && request !=null && request.getSite() != null){
            double floor = request.getSite().getEcpmFloorValue();
            if(floor <= 0){
                return true;
            }
            if(entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice() >= floor){
                return true;
            }
        }
        return false;
    }
}
