package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.userid.InternalUserIdCreator;
import com.kritter.entity.user.usersegment.UserSegmentProvider;
import com.kritter.core.workflow.Context;
import com.kritter.entity.targeting_profile.column.Retargeting;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.user.thrift.struct.Segment;
import com.kritter.user.thrift.struct.UserSegment;

import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ReTargetingMatcher implements TargetingMatcher {
    private static final NoFillReason noFillReason = NoFillReason.RETARGETING;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private UserSegmentProvider userSegmentProvider;
    private String adNoFillReasonMapKey;

    public ReTargetingMatcher(String name,
                              String loggerName,
                              AdEntityCache adEntityCache,
                              UserSegmentProvider userSegmentProvider,
                              String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.userSegmentProvider = userSegmentProvider;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    /**
     * Return a set of ads which do not retarget
     * @param adIdSet Set of ids of all ads
     * @return Set of ad ids which do not retarget
     */
    private Set<Integer> getNonRetargetedAds(Set<Integer> adIdSet) {
        if(adIdSet == null || adIdSet.size() == 0)
            return adIdSet;

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);

            if(!adEntity.isRetargeted()) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Return a set of ads which retarget
     * @param adIdSet Set of ids of all ads
     * @return Set of ad ids which do not retarget
     */
    private Set<Integer> getRetargetedAds(Set<Integer> adIdSet) {
        if(adIdSet == null || adIdSet.size() == 0)
            return adIdSet;

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(Integer adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);
            if(adEntity.isRetargeted()) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * This method takes input ad id set,requesting kritter user id
     * and returns whether the ad is eligible for ad serving.
     * @param adIdSet set of ad ids of shortlisted ads
     * @param request request object corresponding to the current request
     * @param context context object corresponding to the current request
     * @return set of ad ids passing the frequency capping filter
     */
    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        ReqLog.debugWithDebug(logger, request, "Inside ReTargetingMatcher of AdTargetingMatcher");

        String kritterUserId = request.getUserId();
        if(kritterUserId == null || null == userSegmentProvider) {
            Set<Integer> frequencyCappedAds = getRetargetedAds(adIdSet);
            for(Integer adId : frequencyCappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            if(kritterUserId == null) {
                ReqLog.debugWithDebug(logger, request, "User info not available for this request dropping all " +
                        "retargeting ads");
            } else {
                ReqLog.debugWithDebug(logger, request, "UserSegment for userId: {} is null, allowing only non " +
                        "retargeted ads", kritterUserId);
            }

            Set<Integer> shortlistedAdIdSet = getNonRetargetedAds(adIdSet);
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(noFillReason);
            return shortlistedAdIdSet;
        }

        if (null == adIdSet || adIdSet.size() <= 0) {
            ReqLog.debugWithDebug(logger, request, "No adIdSet supplied to UserTargetingMatcher, returning " +
                    "null/empty set...");
            return adIdSet;
        }
        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        try {
            UserSegment userSegment =
                    userSegmentProvider.fetchUserSegment(kritterUserId);
            if (null == userSegment) {
                ReqLog.debugWithDebug(logger, request, "UserSegment for userId: {} is null, allowing all non " +
                        "retargeted ads", kritterUserId);
                return getNonRetargetedAds(adIdSet);
            }

            for (Integer adId : adIdSet) {
                AdEntity adEntity = this.adEntityCache.query(adId);
                if(adEntity.isRetargeted()) {
                    Retargeting  retargeting = adEntity.getTargetingProfile().getRetargeting();
                    List<Integer> targetedSegmenList = retargeting.getSegment();
                    Set<Segment> usersegmentSet = userSegment.getSegmentSet();
                    if(usersegmentSet != null && targetedSegmenList != null) {
                        for(Integer targetedSegment:targetedSegmenList) {
                            Segment seg = new Segment();
                            seg.setSegmentId(targetedSegment);
                            if(usersegmentSet.contains(seg)) {
                                adIdSetToReturn.add(adId);
                            } else {
                                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                        this.adNoFillReasonMapKey, context);
                            }
                        }
                    }
                } else {
                    adIdSetToReturn.add(adId);
                }
            }
        } catch (Exception e) {
            logger.error("Exception occur while trying to fetch user history");
            logger.error("{}", e);
            ReqLog.debugWithDebug(logger,request, "Exception inside FrequencyCapFilter reason: {}  ", e);
            // If an exception has occurred then drop all the ads that are retargeted

            Set<Integer> frequencyCappedAds = getRetargetedAds(adIdSet);
            for(Integer adId : frequencyCappedAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                        this.adNoFillReasonMapKey, context);
            }

            Set<Integer> shortlistedAdIdSet = getNonRetargetedAds(adIdSet);
            if(null == request.getNoFillReason() && (shortlistedAdIdSet == null || shortlistedAdIdSet.size() <= 0))
                request.setNoFillReason(noFillReason);
            return shortlistedAdIdSet;
        }

        return adIdSetToReturn;
    }
}
