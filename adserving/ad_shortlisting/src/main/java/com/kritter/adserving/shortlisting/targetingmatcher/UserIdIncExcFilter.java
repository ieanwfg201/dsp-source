package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.InclusionExclusionType;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.targetingprofileincexc.UserTargetingProfileIncExcProvider;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserIdIncExcFilter implements TargetingMatcher {
    private static final NoFillReason noFillReason = NoFillReason.USER_ID_INC_EXC_FILTER;

    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private UserTargetingProfileIncExcProvider userTargetingProfileIncExcProvider;
    private String adNoFillReasonMapKey;

    public UserIdIncExcFilter(String name,
                              String loggerName,
                              AdEntityCache adEntityCache,
                              UserTargetingProfileIncExcProvider userTargetingProfileIncExcProvider,
                              String adNoFillReasonMapKey) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.userTargetingProfileIncExcProvider = userTargetingProfileIncExcProvider;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    /**
     * Given a set of ad ids, return the ads which have user id inclusion/exclusion
     * @param adIdSet set of ad ids
     * @return Set of ad ids that have user id inclusion/exclusion
     */
    private Set<Integer> getUserIdIncExcAds(Set<Integer> adIdSet) {
        Set<Integer> userIdIncExcAds = new HashSet<Integer>();

        for(int adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);
            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            if(targetingProfile.getUserIdInclusionExclusionType() != InclusionExclusionType.None)
                userIdIncExcAds.add(adId);
        }

        return userIdIncExcAds;
    }

    /**
     *
     * Given a set of ad ids, return the ads which don't have user id inclusion/exclusion
     * @param adIdSet set of ad ids
     * @return Set of ad ids that don't have user id inclusion/exclusion
     */
    private Set<Integer> getNonUserIdIncExcAds(Set<Integer> adIdSet) {
        Set<Integer> nonUserIdIncExcAds = new HashSet<Integer>();

        for(int adId : adIdSet) {
            AdEntity adEntity = this.adEntityCache.query(adId);
            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            if(targetingProfile.getUserIdInclusionExclusionType() == InclusionExclusionType.None)
                nonUserIdIncExcAds.add(adId);
        }

        return nonUserIdIncExcAds;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        ReqLog.debugWithDebugNew(logger, request, "Inside {} of AdTargetingMatcher...", this.name);

        if(adIdSet == null || adIdSet.size() == 0) {
            ReqLog.debugWithDebugNew(logger,request, "No adIdSet supplied to {}, returning null/empty set...", this.name);
            return adIdSet;
        }

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null || externalUserIds.isEmpty() || this.userTargetingProfileIncExcProvider == null) {
            Set<Integer> userIdIncExcAds = getUserIdIncExcAds(adIdSet);
            for(int adId : userIdIncExcAds) {
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey,
                        context);
            }

            if(externalUserIds == null || externalUserIds.isEmpty()) {
                ReqLog.debugWithDebugNew(logger,request, "No user ids in this request. Hence allowing only those ads " +
                        "that do not specify user id inclusion exclusion.");
            } else {
                ReqLog.debugWithDebugNew(logger,request, "User id inclusion exclusion cache is not available. So " +
                        "allowing only ads not specifying user id inclusion exclusion.");
            }

            return getNonUserIdIncExcAds(adIdSet);
        }

        Set<String> externalUserIdStrs = new HashSet<String>();
        for(ExternalUserId externalUserId : externalUserIds) {
            externalUserIdStrs.add(externalUserId.toString());
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        List<Integer> targetingProfilesIncExc =
                this.userTargetingProfileIncExcProvider.getIncExcTargetingProfileIds(externalUserIdStrs);

        if(targetingProfilesIncExc == null || targetingProfilesIncExc.size() == 0) {
            // If the set of targeting profiles included or excluded against this user id is absent, that means no ad
            // has included or excluded this ad. So inclusion criterion should fail, i.e., all ads specifying user id
            // inclusion should get dropped.
            ReqLog.debugWithDebugNew(logger, request, "No targeting profiles included/excluded for user ids.");

            for(Integer adId : adIdSet) {
                AdEntity adEntity = this.adEntityCache.query(adId);
                TargetingProfile targetingProfile = adEntity.getTargetingProfile();

                if(targetingProfile.getUserIdInclusionExclusionType() == InclusionExclusionType.None) {
                    ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} doesn't specify user id inclusion/exclusion" +
                            ". Passing filter.", adId);
                    adIdSetToReturn.add(adId);
                } else if(targetingProfile.getUserIdInclusionExclusionType() == InclusionExclusionType.Exclusion) {
                    ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} specifies user id exclusion but doesn't " +
                            "exclude this user. Passing filter.", adId);
                    adIdSetToReturn.add(adId);
                } else {
                    ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} specifies user id inclusion but doesn't " +
                            "include this user. Failing filter.", adId);
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                            this.adNoFillReasonMapKey, context);
                }
            }
        } else {
            Set<Integer> targetingProfileSet = new HashSet<Integer>(targetingProfilesIncExc.size());
            ReqLog.debugWithDebugNew(logger, request, "Targeting profiles included/excluded for this user :");
            for(Integer targetingProfileId : targetingProfilesIncExc) {
                ReqLog.debugWithDebugNew(logger, request, "\t{}", targetingProfileId);
                targetingProfileSet.add(targetingProfileId);
            }

            for(Integer adId : adIdSet) {
                AdEntity adEntity = this.adEntityCache.query(adId);
                TargetingProfile targetingProfile = adEntity.getTargetingProfile();

                if(targetingProfile.getUserIdInclusionExclusionType() == InclusionExclusionType.None) {
                    ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} doesn't specify user id inclusion/exclusion" +
                            ". Passing filter.", adId);
                    adIdSetToReturn.add(adId);
                } else if(targetingProfile.getUserIdInclusionExclusionType() == InclusionExclusionType.Inclusion) {
                    /** Inclusion case */
                    int targetingProfileId = targetingProfile.getTargetingId();

                    if(targetingProfileSet.contains(targetingProfileId)) {
                        ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} specifies user id inclusion for this " +
                                "user. Passing filter.", adId);
                        adIdSetToReturn.add(adId);
                    } else {
                        ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} specifies user id inclusion, but " +
                                "doesn't include this user. Failing filter.", adId);
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                this.adNoFillReasonMapKey, context);
                    }
                } else if(targetingProfile.getUserIdInclusionExclusionType() == InclusionExclusionType.Exclusion) {
                    /** Exclusion case */
                    int targetingProfileId = targetingProfile.getTargetingId();

                    if(!targetingProfileSet.contains(targetingProfileId)) {
                        ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} specifies user id exclusion, but doesn't " +
                                "exclude this user. Passing filter.", adId);
                        adIdSetToReturn.add(adId);
                    } else {
                        ReqLog.debugWithDebugNew(logger, request, "\tAd id : {} specifies user id exclusion for this " +
                                "user. Failing filter.", adId);
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                this.adNoFillReasonMapKey, context);
                    }
                }
            }
        }

        if(null == request.getNoFillReason() && adIdSetToReturn.size() <= 0)
            request.setNoFillReason(noFillReason);

        return adIdSetToReturn;
    }
}
