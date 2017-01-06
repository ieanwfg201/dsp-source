package com.kritter.exchange.response_openrtb2_2.converter.common;


import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.DefaultCurrency;

import org.codehaus.jackson.map.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConvertBidResponse {
     public static ConvertEntity convert(String str, ObjectMapper objectMapper, Logger logger){
        ConvertEntity entity = new ConvertEntity();
        if(str == null){
            entity.setErrorEnum(ConvertErrorEnum.RES_INPUT_NULL);
            return entity;
        }
        String tmpStr = str.trim();
        if("".equals(tmpStr)){
            entity.setErrorEnum(ConvertErrorEnum.RES_INPUT_EMPTY);
            return entity;
        }
        try{
            BidResponseEntity bidResponseEntity = objectMapper.readValue(str, BidResponseEntity.class);
            entity.setErrorEnum(ConvertErrorEnum.HEALTHY_CONVERT);
            entity.setResponse(bidResponseEntity);
            return entity;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            entity.setErrorEnum(ConvertErrorEnum.RES_CONVERT_EXCEPTION);
            return entity;
        }
    }
     public static void main(String args[]){
         Logger logger = LogManager.getLogger("bals");
         String str = "{"+
    "\"id\": \"0102f1ff-768a-2d01-557c-bfca1403dcb9\","+
    "\"seatbid\": [{"+
        "\"bid\": [{"+
            "\"id\": \"184415\","+
            "\"impid\": \"0102f1ff-768a-2d01-557c-bfca1403dcb9\","+
            "\"price\": 8.5,"+
            "\"nurl\": \"http://register.powerlinks.com/nurl?bi=5253d30a-553a-442d-a999-c883f4b061e3&si=50422a24-91bd-43fa-a673-cf24d23ecaff&b=cdc4590a-392a-11e6-b760-06c9e824977b&apb=tw3LRzQhfC8=&nurl=&a=${AUCTION_ID}&ab=${AUCTION_BID_ID}&as=${AUCTION_SEAT_ID}&ap=${AUCTION_PRICE}&ac=${AUCTION_CURRENCY}&cid=3339&crid=abe40ecc791eb8253e92338716b3ab3d46af77d4\","+
            "\"adm\": \"{\\\"native\\\":{\\\"assets\\\":[{\\\"id\\\":1,\\\"title\\\":{\\\"text\\\":\\\"This i"
            + "s a test creative\\\"}},{\\\"id\\\":4,\\\"data\\\":{\\\"value\\\":\\\"This is a short description "
            + "for the test creative.\\\"}},{\\\"id\\\":3,\\\"img\\\":{\\\"url\\\":\\\"http://res.cloudinary."
            + "com/powerlinks-media/image/upload/e_sharpen,f_auto,fl_lossy,q_85,c_lpad,w_300,h_250/Creative "
            + "Media/brus6ozerin3niln1blk\\\",\\\"w\\\":300,\\\"h\\\":250}}],\\\"link\\\":{\\\"url\\\":\\\""
            + "https://register.powerlinks.com/clk?bi=5253d30a-553a-442d-a999-c883f4b061e3&si=50422a24-91bd-43fa-a"
            + "673-cf24d23ecaff&b=cdc4590a-392a-11e6-b760-06c9e824977b&rd=http%3A%2F%2Fwww.powerlinks.com&cid=3"
            + "339&crid=abe40ecc791eb8253e92338716b3ab3d46af77d4\\\",\\\"clicktrackers\\\":[\\\"http://www.po"
            + "werlinks.com/\\\"]},\\\"imptrackers\\\":[\\\"https://register.powerlinks.com/imp?bi=525"
            + "3d30a-553a-442d-a999-c883f4b061e3&si=50422a24-91bd-43fa-a673-cf24d23ecaff&b=cdc4590a-392a-11e6-b760-06c9e824977b&apb=tw3LRzQhfC8=&a=${AUCTION_ID}&ab=${AUCTION_BID_ID}&as=${AUCTION_SEAT_ID}&aa=${AUCTION_AD_ID}&ap=${AUCTION_PRICE}&ac=${AUCTION_CURRENCY}&cid=3339&crid=abe40ecc791eb8253e92338716b3ab3d46af77d4\\\"],\\\"ext\\\":{\\\"v\\\":3}}}\","+
            "\"adomain\": [\"powerlinks.com\"],"+
            "\"cid\": \"3339\","+
            "\"crid\": \"abe40ecc791eb8253e92338716b3ab3d46af77d4\","+
            "\"ext\": {"+
                "\"plDspBid\": \"482ab217-910b-47b4-8fa5-201f73ded325\","+
                "\"plBid\": \"482ab217-910b-47b4-8fa5-201f73ded325\","+
                "\"plF\": \"f4240\","+
                "\"plP\": \"81b320\","+
                "\"plUuid\": \"cdc4590a-392a-11e6-b760-06c9e824977b\","+
                "\"plx\": \"5253d30a-553a-442d-a999-c883f4b061e3\""+
            "}"+
        "}],"+
        "\"seat\": \"718\","+
        "\"group\": 0"+
    "}],"+
    "\"cur\": \""+DefaultCurrency.defaultCurrency.getName()+"\""+
"}";
         System.out.println(str);
         ObjectMapper objectMapper = new ObjectMapper();
         ConvertEntity ce = ConvertBidResponse.convert(str, objectMapper, logger);
         System.out.println(ce.toString());
     }
}
