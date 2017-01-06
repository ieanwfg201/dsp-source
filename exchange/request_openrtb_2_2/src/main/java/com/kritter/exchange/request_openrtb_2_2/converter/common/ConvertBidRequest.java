package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.caches.iab.index.IABIDIndex;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.ExchangeConstants;
import com.kritter.constants.SupportedCurrencies;
import com.kritter.entity.reqres.entity.Request;

import java.util.ArrayList;
import java.util.Set;

public class ConvertBidRequest {

    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version,
                                           AccountEntity accountEntity, IABCategoriesCache iabCategoryCache,
                                           AccountEntity dspEntity) {
        if(request == null){
            return ConvertErrorEnum.ADSERVING_REQ_NULL; 
        }
        if(bidRequest == null){
            return ConvertErrorEnum.BIDREQ_NULL; 
        }
        if(request.getRequestId() == null){
            return ConvertErrorEnum.REQID_NULL; 
        }
        if(accountEntity== null){
            return ConvertErrorEnum.ACCOUNT_ENTITY_NULL; 
        }
        ConvertErrorEnum convertErrorEnum = ConvertErrorEnum.HEALTHY_CONVERT;
        bidRequest.setBidRequestId(request.getRequestId());
        
        convertErrorEnum = ConvertBidRequestImp.convert(request, bidRequest, version, accountEntity);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        
        convertErrorEnum = ConvertBidRequestSite.convert(request, bidRequest, version, iabCategoryCache);

        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT && convertErrorEnum != ConvertErrorEnum.REQ_NOT_SITE){
            return convertErrorEnum;
        }
        if(convertErrorEnum == ConvertErrorEnum.REQ_NOT_SITE){
            convertErrorEnum = ConvertBidRequestApp.convert(request, bidRequest, version, iabCategoryCache);
        }
        
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        /** Below block to be executed immediately and only after Site and App Conversion has happened*/
        /** BLOCK BEGIN*/
        if(request.getSite().getAdDomainsToExclude() != null && request.getSite().getAdDomainsToExclude().length> 1){
            bidRequest.setBlockedAdvertiserDomainsForBidRequest(request.getSite().getAdDomainsToExclude());
        }
        
        if(request.getSite().isCategoryListExcluded() && 
                request.getSite().getCategoriesArrayForInclusionExclusion() != null && 
                request.getSite().getCategoriesArrayForInclusionExclusion().length>0){
            ArrayList<String> array = new ArrayList<String>();
            for(short s:request.getSite().getCategoriesArrayForInclusionExclusion()){
                IABIDIndex i = new IABIDIndex(s);
                try {
                    Set<String> set = iabCategoryCache.query(i);
                    if(set.size()>0){
                        array.add(set.iterator().next());
                    }
                } catch (UnSupportedOperationException e) {
                    e.printStackTrace();
                }
            }
            bidRequest.setBlockedAdvertiserCategoriesForBidRequest( array.toArray(new String[array.size()]));
        }else{
            bidRequest.setBlockedAdvertiserCategoriesForBidRequest(new String[]{});
        }
        /** BLOCK END */
        convertErrorEnum = ConvertBidRequestDevice.convert(request, bidRequest, version);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        convertErrorEnum = ConvertBidRequestUser.convert(request, bidRequest, version, dspEntity);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }

        bidRequest.setAuctionType(ExchangeConstants.req_auc_type);
        bidRequest.setMaxTimeoutForBidSubmission(accountEntity.getTimeout());
        
        bidRequest.setAllImpressions(ExchangeConstants.req_allimps);
        ;
        String[] allowedCurrencies = new String[1];
        allowedCurrencies[0]= SupportedCurrencies.getEnum(accountEntity.getCurrency()).getName();
        bidRequest.setAllowedCurrencies(allowedCurrencies);
        /*TODO populate bcat*/
        convertErrorEnum = ConvertBidRequestRegs.convert(request, bidRequest, version);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }

        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
