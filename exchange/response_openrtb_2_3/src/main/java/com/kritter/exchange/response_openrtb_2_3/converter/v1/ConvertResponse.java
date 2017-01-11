package com.kritter.exchange.response_openrtb_2_3.converter.v1;

import org.codehaus.jackson.map.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.exchange.response_openrtb_2_3.converter.common.ConvertBidResponse;
import com.kritter.exchange.response_openrtb_2_3.converter.common.ConvertEntity;

public class ConvertResponse {
    private Logger logger;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ConvertResponse(String loggerName){
        this.logger = LogManager.getLogger(loggerName);
    }
    
    public BidResponseEntity convert(String str){
        ConvertEntity convertEntity = ConvertBidResponse.convert(str, objectMapper, logger);
        if(convertEntity.getErrorEnum() == ConvertErrorEnum.HEALTHY_CONVERT){
            return convertEntity.getResponse();
        }
        return null;
    }
}
