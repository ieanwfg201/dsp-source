package com.kritter.exchange.request_openrtb_2_3.converter.common;

import java.util.ArrayList;
import java.util.Set;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.caches.iab.index.IABIDIndex;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.SITE_PLATFORM;

public class ConvertBidRequestSite {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version,
            IABCategoriesCache iabCategoryCache){
        if(request.getSite() == null){
            return ConvertErrorEnum.REQ_SITE_NF;
        }
        if(request.getSite().getSiteGuid() == null){
            return ConvertErrorEnum.REQ_SITE_GUID_NF;
        }
        if(request.getSite().getSitePlatform() != SITE_PLATFORM.WAP.getPlatform()){
            return ConvertErrorEnum.REQ_NOT_SITE; 
        }
        
        BidRequestSiteDTO site = new BidRequestSiteDTO();
        site.setSiteIdOnExchange(request.getSite().getSiteGuid());
        site.setSiteName(request.getSite().getName());
        site.setSiteDomain(request.getSite().getSiteUrl());
        ConvertErrorEnum convertErrorEnum = ConvertErrorEnum.HEALTHY_CONVERT;
        convertErrorEnum = ConvertBidRequestPub.convert(request, site, version);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        
        convertErrorEnum = ConvertBidRequestContent.convert(request, site, version);
        if(convertErrorEnum != ConvertErrorEnum.HEALTHY_CONVERT){
            return convertErrorEnum;
        }
        if(request.getSite().getCategoriesArray() != null && request.getSite().getCategoriesArray().length>0){
            ArrayList<String> array = new ArrayList<String>();
            for(short s:request.getSite().getCategoriesArray()){
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
            site.setContentCategoriesForSite( array.toArray(new String[array.size()]));
        }else{
            site.setContentCategoriesForSite(new String[]{});
        }

        bidRequest.setBidRequestSite(site);
        
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
