package com.kritter.exchange.response_openrtb_2_3.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;

import lombok.Getter;
import lombok.Setter;

public class ConvertEntity {
    @Getter@Setter
    private ConvertErrorEnum errorEnum = ConvertErrorEnum.RES_INPUT_NULL;
    @Getter@Setter
    private BidResponseEntity response = null;
       
}
