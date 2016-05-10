package com.kritter.exchange.formatting;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespNative;
import com.kritter.constants.ExchangeConstants;
import com.kritter.entity.winner.WinEntity;

public class FormatDspResponse {

    public static String formatResponse(WinEntity winEntity, BidRequestParentNodeDTO bidRequestParent,
            String internalFieldURI, String internalWinUrl, boolean isNative, Logger logger){
        
        if(winEntity == null || bidRequestParent == null 
                || internalFieldURI == null || internalWinUrl == null) {return null;}
        BidResponseEntity bidResponseEntity = winEntity.getWinnerBidResponse();
        if(bidResponseEntity == null) {return null;}
        BidResponseSeatBidEntity bidResponseSeatBidEntity[] = bidResponseEntity.getBidResponseSeatBid();
        if(bidResponseSeatBidEntity == null || bidResponseSeatBidEntity.length<1) {return null;}
        BidResponseBidEntity bidResponseBidEntityArray[] =bidResponseSeatBidEntity[0].getBidResponseBidEntities();
        if(bidResponseBidEntityArray == null || bidResponseBidEntityArray.length<1) {return null;}
        BidResponseBidEntity bidResponseBidEntity = bidResponseBidEntityArray[0];
        StringBuffer sBuff = new StringBuffer("");
        String winUrl = bidResponseBidEntity.getWinNotificationUrl();
        if(bidRequestParent.getBidRequestId() != null){
            winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionId, bidRequestParent.getBidRequestId());
        }
        if(bidResponseEntity.getBidRequestId() != null){
            winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionBidId, bidResponseEntity.getBidRequestId());
        }
        if(bidResponseBidEntity.getRequestImpressionId() != null){
            winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionImpId, bidResponseBidEntity.getRequestImpressionId());
        }
        if(bidResponseSeatBidEntity[0].getBidderSeatId() != null){
            winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionSeatId, bidResponseSeatBidEntity[0].getBidderSeatId());
        }
        if(bidResponseBidEntity.getAdId() != null){
            winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionAdId, bidResponseBidEntity.getAdId());
        }
        winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionPrice, winEntity.getWin_price()+"");
        winUrl = winUrl.replaceAll(ExchangeConstants.winMacroAuctionCurrency,  bidRequestParent.getAllowedCurrencies()[0]);
        if(isNative){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
            try{
                RespNative entity = objectMapper.readValue(bidResponseBidEntity.getAdMarkup(), RespNative.class);
                String[] imptracker = entity.getImpTracker();
                String newImpTracker[] = new String[imptracker.length+2];
                int i=0;
                for(String str:imptracker){
                    newImpTracker[i] = str;
                    i++;
                }
                newImpTracker[i] = winUrl;
                i++;
                newImpTracker[i]=internalWinUrl+internalFieldURI+"?wp="+winEntity.getWin_price();
                entity.setImptrackers(newImpTracker);
                JsonNode jsonNode = objectMapper.valueToTree(entity);
                return jsonNode.toString();
            }catch(Exception e){
                logger.error(e.getMessage(),e);
            }
            
        }else{
            sBuff.append(bidResponseBidEntity.getAdMarkup());
            sBuff.append("<img src=\"");
            sBuff.append(winUrl);
            sBuff.append("\" style=\"display: none;\" />");
            sBuff.append("<img src=\"");
            sBuff.append(internalWinUrl+internalFieldURI);
            sBuff.append("?wp=");
            sBuff.append(winEntity.getWin_price());
            sBuff.append("\" style=\"display: none;\" />");
        }
        return sBuff.toString();
    }
}
