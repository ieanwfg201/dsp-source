package com.kritter.adserving.shortlisting.core.openrtbhelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.SetUtils;

public class ValidateCreativeAttribute {
    public static boolean validate(Logger logger, Request request, 
            BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO,
            Creative creative, AdEntity adEntity, BidRequestImpressionDTO bidRequestImpressionDTO){
        Set<Short> impressionCreativeAttributes =
                (
                 null == bidRequestImpressionBannerObjectDTO.getBlockedCreativeAttributes() ?
                 new HashSet<Short>() :
                 new HashSet<Short>
                        (Arrays.asList(bidRequestImpressionBannerObjectDTO.getBlockedCreativeAttributes()))
                );
        if(impressionCreativeAttributes != null){
            request.getSite().setCreativeAttributesForInclusionExclusion(bidRequestImpressionBannerObjectDTO.getBlockedCreativeAttributes());
        }

        Set<Short> adCreativeAttributes =
                (
                 null == creative.getCreativeAttributes() ?
                 new HashSet<Short>() :
                 new HashSet<Short>
                        (Arrays.asList(creative.getCreativeAttributes()))
                );

        Set<Short> resultingIntersection = SetUtils.intersectNSets(impressionCreativeAttributes,
                                                                   adCreativeAttributes);

        logger.debug("Impression banner object's creative attributes and ad creative attributes intersection  size: {} , if size non-zero then impression does not allow this ad to be served.",
                     resultingIntersection.size());

        if(null != resultingIntersection && resultingIntersection.size() > 0)
        {
            //the creative is not appropriate for the requesting impression.
            ReqLog.debugWithDebugNew(logger, request, "Creative id: {} does not qualify for creative attributes demanded by the impression: {}",
                    adEntity.getCreativeId(),
                    bidRequestImpressionDTO.getBidRequestImpressionId());
            return false;
        }
        else
            return true;
    }
    public static boolean validate(Logger logger, Request request, 
            com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO,
            Creative creative, AdEntity adEntity, 
            com.kritter.bidrequest.entity.common.openrtbversion2_4.BidRequestImpressionDTO bidRequestImpressionDTO){
        Set<Short> impressionCreativeAttributes =
                (
                 null == bidRequestImpressionBannerObjectDTO.getBattr() ?
                 new HashSet<Short>() :
                 new HashSet<Short>
                        (Arrays.asList(bidRequestImpressionBannerObjectDTO.getBattr()))
                );
        if(impressionCreativeAttributes != null){
            request.getSite().setCreativeAttributesForInclusionExclusion(bidRequestImpressionBannerObjectDTO.getBattr());
        }

        Set<Short> adCreativeAttributes =
                (
                 null == creative.getCreativeAttributes() ?
                 new HashSet<Short>() :
                 new HashSet<Short>
                        (Arrays.asList(creative.getCreativeAttributes()))
                );

        Set<Short> resultingIntersection = SetUtils.intersectNSets(impressionCreativeAttributes,
                                                                   adCreativeAttributes);

        logger.debug("Impression banner object's creative attributes and ad creative attributes intersection  size: {} , if size non-zero then impression does not allow this ad to be served.",
                     resultingIntersection.size());

        if(null != resultingIntersection && resultingIntersection.size() > 0)
        {
            //the creative is not appropriate for the requesting impression.
            ReqLog.debugWithDebugNew(logger, request, "Creative id: {} does not qualify for creative attributes demanded by the impression: {}",
                    adEntity.getCreativeId(),
                    bidRequestImpressionDTO.getId());
            return false;
        }
        else
            return true;
    }
}
