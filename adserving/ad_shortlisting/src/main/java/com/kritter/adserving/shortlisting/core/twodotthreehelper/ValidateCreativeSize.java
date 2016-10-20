package com.kritter.adserving.shortlisting.core.twodotthreehelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;

import com.kritter.common.caches.banner_upload_cache.BannerUploadCache;
import com.kritter.common.caches.banner_upload_cache.entity.BannerUploadCacheEntity;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.cache.CreativeBannerCache;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.entity.CreativeBanner;

public class ValidateCreativeSize {
	private static final String CTRL_A = String.valueOf((char)1);
    public static Integer[] fetchBannerUids(Logger logger, Creative creative, CreativeBannerCache creativeBannerCache,
            List<CreativeBanner> creativeBannerList, Comparator<CreativeBanner> comparator,Request request,
            AdxBasedExchangesMetadata adxBased, BannerUploadCache bannerUploadCache){
        Integer bannerUriIds[] = null;
        if(null != creative.getBannerUriIds())
        {
            for(Integer bannerId : creative.getBannerUriIds())
            {
            	if(adxBased != null && adxBased.isBanner_upload()){
            		if(bannerUploadCache == null){
            			ReqLog.debugWithDebugNew(logger, request, "BannerId {} does not qualify as bannerupload cache null", bannerId);
            			continue;
            		}
            		BannerUploadCacheEntity bue = bannerUploadCache.query(request.getSite().getPublisherIncId()+CTRL_A+bannerId);
                	if(bue ==null){
                		ReqLog.debugWithDebugNew(logger, request, "BannerId {} does not qualify as BannerUploadCacheEntity  null", bannerId);
            			continue;
                	}
                	if(bue.getMub() == null){
                		ReqLog.debugWithDebugNew(logger, request, "BannerId {} does not qualify as MaterailBannerUploadEntity  null", bannerId);
            			continue;
                	}
                	if(AdxBasedExchangesStates.APPROVED.getCode() != bue.getMub().getAdxbasedexhangesstatus()){
                		ReqLog.debugWithDebugNew(logger, request, "BannerId {} does not qualify as MaterailBannerUploadEntity  not Approved", bannerId);
            			continue;                        		
                	}
            	}

                CreativeBanner creativeBanner = creativeBannerCache.query(bannerId);
                if(null == creativeBanner)
                {
                    logger.error("Creative banner is null(not found in cache) for banner id: {} " , bannerId);
                    break;
                }

                creativeBannerList.add(creativeBanner);
            }
            if(creativeBannerList.size()>0){
            	Collections.sort(creativeBannerList,comparator);
            	//done sorting banner uri ids on size.

            	bannerUriIds = new Integer[creativeBannerList.size()];
            	for(int i=0 ;i<creative.getBannerUriIds().length;i++)
            	{
            		bannerUriIds[i] = creativeBannerList.get(i).getId();
            	}
            }
        }
        return bannerUriIds;
    }
}
