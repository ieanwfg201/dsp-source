package com.kritter.exchange.request_openrtb_2_2.converter.v1;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.exchange.request_openrtb_2_2.converter.common.ConvertBidRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Convert {
    private Logger logger;
    
    public Convert(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    public BidRequestParentNodeDTO convert(Request request,  int version,
            AccountEntity accountEntity, IABCategoriesCache iabCategoryCache){
        if(request == null){
            this.logger.debug("BidRequestParentNodeDTO: Request object null");
            return null;
        }
        
        BidRequestParentNodeDTO bidRequest = new BidRequestParentNodeDTO();
        ConvertErrorEnum errorEnum = ConvertBidRequest.convert(request, bidRequest, version,
                accountEntity, iabCategoryCache);
        if(ConvertErrorEnum.HEALTHY_CONVERT == errorEnum){
            return bidRequest;
        }
        return null;
    }
}