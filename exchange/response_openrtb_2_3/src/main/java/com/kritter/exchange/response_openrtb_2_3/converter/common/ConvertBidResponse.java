package com.kritter.exchange.response_openrtb_2_3.converter.common;


import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;

public class ConvertBidResponse {
     public static ConvertEntity convert(String str, ObjectMapper objectMapper,Logger logger){
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
}
