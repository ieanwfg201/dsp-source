package com.kritter.auction_strategies.second_price;

import java.util.Map;

import com.kritter.entity.exchangethrift.CreateExchangeThrift;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.thrift.struct.DspNoFill;
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
    public WinEntity getWinnerOpenRTB2_3(
            Map<String, BidResponseEntity> bidderResponses, Request request,CreateExchangeThrift cet)
                    throws Exception {
        if(bidderResponses == null || request ==null || request.getSite() == null || bidderResponses.size() < 1){
            return null;
        }
        String winKey = null;
        float firstPrice = 0.0f;
        float secondPrice = 0.0f;

        for(String key:bidderResponses.keySet()){
            BidResponseEntity entity = bidderResponses.get(key);

            /*Note down the price offered by the DSP,would be used in thrift log.*/
            if(null != entity && null != entity.getBidResponseSeatBid()[0] && null != entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0])
            {
                float bidPrice = entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice();
                request.addBidPriceOfferedByDSPForExchangeRequest(key,(double)bidPrice);
            }

            if(ValidateBidResponse.validate(entity, request)){
                if(ValidateFloor.validate(entity, request)){
                	cet.updateDemandState(key, DspNoFill.LOWBID);
                	cet.updateDemandBid(key, entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice());
                    if(ValidateBestPrice.validate(entity, firstPrice)){
                        winKey = key;
                        if(firstPrice != 0.0){
                            secondPrice = firstPrice;
                        }
                        firstPrice =  entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice();

                    }else if (ValidateSecondBestPrice.validate(entity, secondPrice)){
                        secondPrice = entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice();
                    }
                }else{
                	cet.updateDemandState(key, DspNoFill.FLOORUNMET);
                }
            }else{
            	cet.updateDemandState(key, DspNoFill.RESPERROR);
            }
        }
        if(winKey != null){
            WinEntity winEntity =  new WinEntity();
            winEntity.setAdvId(winKey);
            winEntity.setWinnerBidResponse2_3(bidderResponses.get(winKey));
            if(bidderResponses.size() == 1)
            {
                float winPrice = (float)request.getSite().getEcpmFloorValue() + ExchangeConstants.offset;
                winEntity.setWin_price(winPrice);
            }
            else
                winEntity.setWin_price(secondPrice+ExchangeConstants.offset);
            return winEntity;
        }
        return null;
    }

    @Override
    public WinEntity getWinnerOpenRTB2_2(
            Map<String, com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity> bidderResponses, Request request,
            CreateExchangeThrift cet)
            throws Exception {
        if(bidderResponses == null || request ==null || request.getSite() == null || bidderResponses.size() < 1){
            return null;
        }
        String winKey = null;
        float firstPrice = 0.0f;
        float secondPrice = 0.0f;
        for(String key:bidderResponses.keySet()){
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity entity = bidderResponses.get(key);

            /*Note down the price offered by the DSP,would be used in thrift log.*/
            if(null != entity && null != entity.getBidResponseSeatBid()[0] && null != entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0])
            {
                float bidPrice = entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice();
                request.addBidPriceOfferedByDSPForExchangeRequest(key,(double)bidPrice);
            }

            if(ValidateBidResponse.validate(entity, request)){
            	cet.updateDemandState(key, DspNoFill.LOWBID);
            	cet.updateDemandBid(key, entity.getBidResponseSeatBid()[0].getBidResponseBidEntities()[0].getPrice());
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
                }else{
                	cet.updateDemandState(key, DspNoFill.FLOORUNMET);
                }
            }else{
            	cet.updateDemandState(key, DspNoFill.RESPERROR);
            }
        }
        if(winKey != null){
            WinEntity winEntity =  new WinEntity();
            winEntity.setAdvId(winKey);
            winEntity.setWinnerBidResponse2_2(bidderResponses.get(winKey));

            if(bidderResponses.size() == 1)
            {
                float winPrice = (float)request.getSite().getEcpmFloorValue() + ExchangeConstants.offset;
                winEntity.setWin_price(winPrice);
            }
            else
                winEntity.setWin_price(secondPrice+ExchangeConstants.offset);

            return winEntity;
        }
        return null;
    }
}
