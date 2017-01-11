package com.kritter.exchange.request_openrtb_2_3.converter.v1;

import com.kritter.serving.demand.entity.AdEntity;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.exchange.request_openrtb_2_3.converter.common.ConvertBidRequest;

public class Convert {
    private Logger logger;
    
    public Convert(String loggerName){
        this.logger = LogManager.getLogger(loggerName);
    }

    public BidRequestParentNodeDTO convert(Request request, int version, AccountEntity publisherAccountEntity,
                                           IABCategoriesCache iabCategoryCache, AccountEntity dspEntity) {
        if(request == null) {
            this.logger.debug("BidRequestParentNodeDTO: Request object null");
            return null;
        }
        
        BidRequestParentNodeDTO bidRequest = new BidRequestParentNodeDTO();
        ConvertErrorEnum errorEnum = ConvertBidRequest.convert(request, bidRequest, version, publisherAccountEntity,
                iabCategoryCache, dspEntity);
        if(ConvertErrorEnum.HEALTHY_CONVERT == errorEnum) {
            return bidRequest;
        }
        return null;
    }
}
