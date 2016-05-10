package com.kritter.auction_strategies.second_price;

import java.util.Map;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.auction_strategies.common.KAuction;
import com.kritter.auction_strategies.validation.ValidateBestPrice;
import com.kritter.auction_strategies.validation.ValidateBidResponse;
import com.kritter.auction_strategies.validation.ValidateFloor;
import com.kritter.auction_strategies.validation.ValidateSecondBestPrice;
import com.kritter.entity.winner.WinEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.constants.ExchangeConstants;

public class SecondPriceAuction implements KAuction {

    @Override
    public WinEntity getWinner(
            Map<String, BidResponseEntity> bidderResponses, Request request)
                    throws Exception {
        if(bidderResponses == null || request ==null || request.getSite() == null || bidderResponses.size() < 1){
            return null;
        }
        String winKey = null;
        float firstPrice = 0.0f;
        float secondPrice = 0.0f;
        for(String key:bidderResponses.keySet()){
            BidResponseEntity entity = bidderResponses.get(key);
            if(ValidateBidResponse.validate(entity, request)){
                if(ValidateFloor.validate(entity, request)){
                    if(ValidateBestPrice.validate(entity, firstPrice)){
                        winKey = key;
                        if(firstPrice != 0.0){
                            secondPrice = firstPrice;
                        }
                        firstPrice =  entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice();

                    }else if (ValidateSecondBestPrice.validate(entity, secondPrice)){
                        secondPrice = entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice();
                    }
                }
            }
        }
        if(winKey != null){
            WinEntity winEntity =  new WinEntity();
            winEntity.setAdvId(winKey);
            winEntity.setWinnerBidResponse(bidderResponses.get(winKey));
            winEntity.setWin_price(secondPrice+ExchangeConstants.offset);
            return winEntity;
        }
        return null;
    }

}
