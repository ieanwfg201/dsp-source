package com.kritter.adserving.shortlisting.core.twodotthreehelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;

import com.kritter.serving.demand.cache.CreativeBannerCache;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.entity.CreativeBanner;

public class ValidateCreativeSize {

    public static Integer[] fetchBannerUids(Logger logger, Creative creative, CreativeBannerCache creativeBannerCache,
            List<CreativeBanner> creativeBannerList, Comparator<CreativeBanner> comparator){
        Integer bannerUriIds[] = null;
        if(null != creative.getBannerUriIds())
        {
            for(Integer bannerId : creative.getBannerUriIds())
            {
                CreativeBanner creativeBanner = creativeBannerCache.query(bannerId);
                if(null == creativeBanner)
                {
                    logger.error("Creative banner is null(not found in cache) for banner id: " + bannerId);
                    break;
                }

                creativeBannerList.add(creativeBanner);
            }

            Collections.sort(creativeBannerList,comparator);
            //done sorting banner uri ids on size.

            bannerUriIds = new Integer[creative.getBannerUriIds().length];
            for(int i=0 ;i<creative.getBannerUriIds().length;i++)
            {
                bannerUriIds[i] = creativeBannerList.get(i).getId();
            }
        }
        return bannerUriIds;
    }
}
