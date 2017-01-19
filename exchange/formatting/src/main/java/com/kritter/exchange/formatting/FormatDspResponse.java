package com.kritter.exchange.formatting;

import java.util.LinkedList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespNativeFirstLevel;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespNativeParent;
import com.kritter.constants.ExchangeConstants;
import com.kritter.entity.winner.WinEntity;
import com.kritter.vast.vastversion.CheckVastVersion;

public class FormatDspResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    public static String formatResponse2_3(WinEntity winEntity, BidRequestParentNodeDTO bidRequestParent,
            String internalFieldURI, String internalWinUrl, boolean isNative, Logger logger, boolean isVideo){
        
        if(winEntity == null || bidRequestParent == null 
                || internalFieldURI == null || internalWinUrl == null) {
            logger.debug("FormatDspResponse:formatResponse - Param Null Check Failed");
            logger.debug("FormatDspResponse:formatResponse - winEntity {}", winEntity);
            logger.debug("FormatDspResponse:formatResponse - bidRequestParent {}", bidRequestParent);
            logger.debug("FormatDspResponse:formatResponse - internalFieldURI {}", internalFieldURI);
            logger.debug("FormatDspResponse:formatResponse - internalWinUrl {}", internalWinUrl);
            return null;
        }
        BidResponseEntity bidResponseEntity = winEntity.getWinnerBidResponse2_3();
        if(bidResponseEntity == null) {
            logger.debug("FormatDspResponse:formatResponse - bidResponseEntity NUll");
            return null;
        }
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
            try{
                RespNativeParent entityParent = objectMapper.readValue(bidResponseBidEntity.getAdMarkup(), RespNativeParent.class);
                RespNativeFirstLevel entity = entityParent.getRespNativeForstLevel();
                String[] imptracker = entity.getImpTracker();
                if(imptracker == null){
                    String newImpTracker[] = new String[2];
                    newImpTracker[0] = winUrl;
                    newImpTracker[1]=internalWinUrl + internalFieldURI + (internalFieldURI.contains("?") ? "&" : "?") + "wp="+winEntity.getWin_price();
                    entity.setImptrackers(newImpTracker);
                }else{
                    String newImpTracker[] = new String[imptracker.length+2];
                    int i=0;
                    for(String str:imptracker){
                        newImpTracker[i] = str;
                        i++;
                    }
                    newImpTracker[i] = winUrl;
                    i++;
                    newImpTracker[i]=internalWinUrl + internalFieldURI + (internalFieldURI.contains("?") ? "&" : "?") + "wp="+winEntity.getWin_price();
                    entity.setImptrackers(newImpTracker);
                }
                JsonNode jsonNode = objectMapper.valueToTree(entity);
                return jsonNode.toString();
            }catch(Exception e){
                logger.error(e.getMessage(),e);
            }
            
        }else if(isVideo){
        	if(bidResponseBidEntity.getAdMarkup() ==null || bidResponseBidEntity.getAdMarkup().isEmpty()){
        		return "";
        	}
        	LinkedList<String> impTrackers = new LinkedList<String>();
        	impTrackers.add(winUrl);
        	impTrackers.add(internalWinUrl + internalFieldURI + (internalFieldURI.contains("?") ? "&" : "?") + "wp="+winEntity.getWin_price());
        	String s = CheckVastVersion.enhanceVastResponse(bidResponseBidEntity.getAdMarkup(), logger, impTrackers, "kritkjqbsxcdkjqw");
        	if(s==null || s.isEmpty()){
        		return "";
        	}
        	return s;
        	
        }else{
            sBuff.append("<img src=\"");
            sBuff.append(internalWinUrl+internalFieldURI);
            sBuff.append((internalFieldURI.contains("?") ? "&" : "?"));
            sBuff.append("wp=");
            sBuff.append(winEntity.getWin_price());
            sBuff.append("\" style=\"display: none;\" />");
            sBuff.append("<img src=\"");
            sBuff.append(winUrl);
            sBuff.append("\" style=\"display: none;\" />");
            sBuff.append(bidResponseBidEntity.getAdMarkup());
        }
        return sBuff.toString();
    }

    public static String formatResponse2_2(WinEntity winEntity,
                                           com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO bidRequestParent,
                                           String internalFieldURI, String internalWinUrl, boolean isNative, Logger logger, boolean isVideo){

        if(winEntity == null || bidRequestParent == null
                || internalFieldURI == null || internalWinUrl == null) {
            logger.debug("FormatDspResponse:formatResponse - Param Null Check Failed");
            logger.debug("FormatDspResponse:formatResponse - winEntity {}", winEntity);
            logger.debug("FormatDspResponse:formatResponse - bidRequestParent {}", bidRequestParent);
            logger.debug("FormatDspResponse:formatResponse - internalFieldURI {}", internalFieldURI);
            logger.debug("FormatDspResponse:formatResponse - internalWinUrl {}", internalWinUrl);
            return null;
        }
        com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity bidResponseEntity = winEntity.getWinnerBidResponse2_2();
        if(bidResponseEntity == null) {
            logger.debug("FormatDspResponse:formatResponse - bidResponseEntity NUll");
            return null;
        }
        com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseSeatBidEntity bidResponseSeatBidEntity[] = bidResponseEntity.getBidResponseSeatBid();
        if(bidResponseSeatBidEntity == null || bidResponseSeatBidEntity.length<1) {return null;}
        com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseBidEntity bidResponseBidEntityArray[] =bidResponseSeatBidEntity[0].getBidResponseBidEntities();
        if(bidResponseBidEntityArray == null || bidResponseBidEntityArray.length<1) {return null;}
        com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseBidEntity bidResponseBidEntity = bidResponseBidEntityArray[0];
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

            try{
                RespNativeParent entityParent = objectMapper.readValue(bidResponseBidEntity.getAdMarkup(), RespNativeParent.class);
                RespNativeFirstLevel entity = entityParent.getRespNativeForstLevel();
                String[] imptracker = entity.getImpTracker();
                if(imptracker == null){
                    String newImpTracker[] = new String[2];
                    newImpTracker[0] = winUrl;
                    newImpTracker[1]=internalWinUrl + internalFieldURI + (internalFieldURI.contains("?") ? "&" : "?") + "wp="+winEntity.getWin_price();
                    entity.setImptrackers(newImpTracker);
                }else{
                    String newImpTracker[] = new String[imptracker.length+2];
                    int i=0;
                    for(String str:imptracker){
                        newImpTracker[i] = str;
                        i++;
                    }
                    newImpTracker[i] = winUrl;
                    i++;
                    newImpTracker[i]=internalWinUrl + internalFieldURI + (internalFieldURI.contains("?") ? "&" : "?") + "wp="+winEntity.getWin_price();
                    entity.setImptrackers(newImpTracker);
                }
                JsonNode jsonNode = objectMapper.valueToTree(entity);
                return jsonNode.toString();
            }catch(Exception e){
                logger.error(e.getMessage(),e);
            }

        }else if(isVideo){
        	if(bidResponseBidEntity.getAdMarkup() ==null || bidResponseBidEntity.getAdMarkup().isEmpty()){
        		return "";
        	}
        	LinkedList<String> impTrackers = new LinkedList<String>();
        	impTrackers.add(winUrl);
        	impTrackers.add(internalWinUrl + internalFieldURI + (internalFieldURI.contains("?") ? "&" : "?") + "wp="+winEntity.getWin_price());
        	String s = CheckVastVersion.enhanceVastResponse(bidResponseBidEntity.getAdMarkup(), logger, impTrackers, "kritkjqbsxcdkjqw");
        	if(s==null || s.isEmpty()){
        		return "";
        	}
        	return s;
        	
        }else{
            sBuff.append("<img src=\"");
            sBuff.append(internalWinUrl+internalFieldURI);
            sBuff.append((internalFieldURI.contains("?") ? "&" : "?"));
            sBuff.append("wp=");
            sBuff.append(winEntity.getWin_price());
            sBuff.append("\" style=\"display: none;\" />");
            sBuff.append("<img src=\"");
            sBuff.append(winUrl);
            sBuff.append("\" style=\"display: none;\" />");
            sBuff.append(bidResponseBidEntity.getAdMarkup());
        }
        return sBuff.toString();
    }
}
