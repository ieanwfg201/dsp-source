package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.ApplicationGeneralUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class HourOfDayTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public HourOfDayTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdIdsBasedOnHourOfDayTargeting of AdTargetingMatcher ...");
        ReqLog.requestDebug(request, "Inside filterAdIdsBasedOnHourOfDayTargeting of AdTargetingMatcher ...");

        int hourOfDay = ApplicationGeneralUtils.getHourOfDay();
        Short hourOfDayToUse = new Short((short)hourOfDay);

        ReqLog.debugWithDebug(logger,request, "Hour of day is : {}", hourOfDay);

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger,request, "AdEntity not found in cache id : {}" , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            Set<Short> hoursTargeted = targetingProfile.getHoursTargetedInTheDay();

            ReqLog.debugWithDebug(logger,request, "The adid: {}, targets hours of day : ", adEntity.getAdGuid(),  hoursTargeted);

            if(null == hoursTargeted || hoursTargeted.size() == 0)
            {
                shortlistedAdIdSet.add(adId);
            }
            else if(hoursTargeted.contains(hourOfDayToUse))
            {
                shortlistedAdIdSet.add(adId);
            }
            else
            {
                ReqLog.debugWithDebug(logger,request, "Hour of day targeting fails for adGuId: ",adEntity.getAdGuid());
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.AD_HOUR_DAY);

        return shortlistedAdIdSet;
    }
}
