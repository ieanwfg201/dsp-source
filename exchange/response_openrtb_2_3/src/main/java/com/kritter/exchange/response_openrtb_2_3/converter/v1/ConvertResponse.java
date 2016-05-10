package com.kritter.exchange.response_openrtb_2_3.converter.v1;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.exchange.response_openrtb_2_3.converter.common.ConvertBidResponse;
import com.kritter.exchange.response_openrtb_2_3.converter.common.ConvertEntity;

public class ConvertResponse {
    private Logger logger;
    
    public ConvertResponse(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
    }
    
    public BidResponseEntity convert(String str){
        ObjectMapper objectMapper = new ObjectMapper();
        ConvertEntity convertEntity = ConvertBidResponse.convert(str, objectMapper, logger);
        if(convertEntity.getErrorEnum() == ConvertErrorEnum.HEALTHY_CONVERT){
            return convertEntity.getResponse();
        }
        return null;
    }
}
