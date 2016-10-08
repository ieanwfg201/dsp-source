package com.kritter.adserving.shortlisting.utils;

import com.kritter.serving.demand.cache.CreativeSlotCache;
import com.kritter.serving.demand.entity.CreativeBanner;
import com.kritter.serving.demand.entity.CreativeSlot;

import java.util.Comparator;

public class CreativeUtils {
    public static class BannerSizeComparator implements Comparator<CreativeBanner> {
        private CreativeSlotCache creativeSlotCache;

        public BannerSizeComparator(CreativeSlotCache creativeSlotCache) {
            this.creativeSlotCache = creativeSlotCache;
        }

        @Override
        public int compare(CreativeBanner creativeBannerFirst, CreativeBanner creativeBannerSecond) {
            CreativeSlot creativeSlotFirst = creativeSlotCache.query(creativeBannerFirst.getSlotId());
            CreativeSlot creativeSlotSecond = creativeSlotCache.query(creativeBannerSecond.getSlotId());

            if(creativeSlotFirst == creativeSlotSecond)
                return 0;

            if(null == creativeSlotFirst)
                return 1;

            if(null == creativeSlotSecond)
                return -1;

            if(creativeSlotFirst.getCreativeSlotWidth() > creativeSlotSecond.getCreativeSlotWidth())
                return -1;

            if(creativeSlotFirst.getCreativeSlotWidth() < creativeSlotSecond.getCreativeSlotWidth())
                return 1;

            if(creativeSlotFirst.getCreativeSlotHeight() > creativeSlotSecond.getCreativeSlotHeight())
                return -1;

            if(creativeSlotFirst.getCreativeSlotHeight() < creativeSlotSecond.getCreativeSlotHeight())
                return 1;

            return 0;
        }
    }
}
