package com.kritter.exchange.response_openrtb2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity;
import com.kritter.constants.ConvertErrorEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ConvertEntity {
    @Getter@Setter
    private ConvertErrorEnum errorEnum = ConvertErrorEnum.RES_INPUT_NULL;
    @Getter@Setter
    private BidResponseEntity response = null;
       
}
