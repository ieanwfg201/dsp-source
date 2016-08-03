package com.kritter.exchange.response_openrtb2_2.converter.v1;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.exchange.response_openrtb2_2.converter.common.ConvertBidResponse;
import com.kritter.exchange.response_openrtb2_2.converter.common.ConvertEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
