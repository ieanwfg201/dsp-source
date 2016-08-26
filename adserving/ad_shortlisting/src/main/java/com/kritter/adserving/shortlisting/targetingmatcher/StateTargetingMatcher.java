package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.geo.common.entity.State;
import com.kritter.geo.common.entity.StateUserInterfaceId;
import com.kritter.geo.common.entity.StateUserInterfaceIdSecondaryIndex;
import com.kritter.geo.common.entity.reader.StateDetectionCache;
import com.kritter.geo.common.entity.reader.StateUserInterfaceIdCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.url.URLField;
import com.kritter.utils.common.url.URLFieldProcessingException;
import com.kritter.utils.entity.TargetingProfileLocationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class performs detection and targeting matching for states targeted by an ad.
 **/
public class StateTargetingMatcher implements TargetingMatcher
{
    private static NoFillReason noFillReason = NoFillReason.NO_ADS_STATE;
    private String name;
    private Logger logger;
    private String adNoFillReasonMapKey;
    private AdEntityCache adEntityCache;
    private StateDetectionCache stateDetectionCache;
    private StateUserInterfaceIdCache stateUserInterfaceIdCache;

    public StateTargetingMatcher(
                                 String name,
                                 String loggerName,
                                 String adNoFillReasonMapKey,
                                 AdEntityCache adEntityCache,
                                 StateDetectionCache stateDetectionCache,
                                 StateUserInterfaceIdCache stateUserInterfaceIdCache
                                )
    {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
        this.adEntityCache = adEntityCache;
        this.stateDetectionCache = stateDetectionCache;
        this.stateUserInterfaceIdCache = stateUserInterfaceIdCache;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context)
    {
        logger.info("Inside StateTargetingMatcher of AdTargetingMatcher ...");
        ReqLog.requestDebug(request, "Inside StateTargetingMatcher of AdTargetingMatcher ...");

        if(adIdSet == null || adIdSet.size() == 0)
        {
            logger.debug("No ads to shortlist from inside StateTargetingMatcher. Returning!");
            ReqLog.requestDebug(request,"No ads to shortlist from inside StateTargetingMatcher. Returning!");
            return adIdSet;
        }

        String ipAddress = request.getIpAddressUsedForDetection();
        State state = null;
        StateUserInterfaceId stateUserInterfaceId = null;

        try
        {
            state = stateDetectionCache.findStateForIpAddress(ipAddress);

            if(null != state)
            {
                logger.debug("State detected id is: {} " , state.getStateId());
                ReqLog.requestDebug(request,"State detected id is: " + state.getStateId());
                request.setDataSourceNameUsedForStateDetection(state.getDataSourceName());

                Set<Integer> uiIdSetState = stateUserInterfaceIdCache.query(
                                                        new StateUserInterfaceIdSecondaryIndex(state.getStateId()));
                if(null != uiIdSetState)
                {
                    for(Integer stateUiId : uiIdSetState)
                    {
                        stateUserInterfaceId = stateUserInterfaceIdCache.query(stateUiId);
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception inside StateTargetingMatcher in detecting state for ip address: {} ",
                          ipAddress,e);
            logger.error("State cannot be detected, some error happened, only ads with no state targeting " +
                         "will be shortlisted.");
            ReqLog.requestDebug(request,"State cannot be detected, some error happened, only ads with " +
                                                  "no state targeting will be shortlisted, {} "+ e.getMessage());
        }

        if(null != stateUserInterfaceId)
        {
            request.setStateUserInterfaceId(stateUserInterfaceId.getStateUserInterfaceId());

            try
            {
                URLField urlField = URLField.STATE_ID;
                urlField.getUrlFieldProperties().setFieldValue(stateUserInterfaceId.getStateUserInterfaceId());
                request.getUrlFieldFactory().stackFieldForStorage(urlField);
            }
            catch (URLFieldProcessingException urfpe)
            {
                logger.error("URLFieldProcessingException inside StateTargetingMatcher ", urfpe);
            }

            logger.debug("State user interface id detected is: {} ", stateUserInterfaceId.getStateUserInterfaceId());
            ReqLog.requestDebug(request,"State user interface id detected is: " +
                                         stateUserInterfaceId.getStateUserInterfaceId());
        }

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if (null == adEntity)
            {
                ReqLog.errorWithDebug(logger, request, "AdEntity not found in cache id : {}", adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            TargetingProfileLocationEntity stateTargetingEntity = targetingProfile.getTargetedStates();

            Integer[] targetedStateUIIdSet = null;

            if(null != stateTargetingEntity)
                targetedStateUIIdSet = stateTargetingEntity.fetchAllKeyArrayFromDataMap();

            if (null == stateTargetingEntity || null == targetedStateUIIdSet)
            {
                logger.debug("AdId: {} does not target any state , so passing it... ", adId);
                ReqLog.requestDebug(request,"AdId: "+ adId + " does not target any state , so passing it...");
                shortlistedAdIdSet.add(adId);
                continue;
            }

            if(null == stateUserInterfaceId)
            {
                logger.error("State is null, only ads with no state targeting will pass...");
                ReqLog.requestDebug(request,"State is null, only ads with no state targeting will pass...");
                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                                              this.adNoFillReasonMapKey, context);
                continue;
            }

            boolean targetingFound = false;
            for(Integer stateIdTargeted : targetedStateUIIdSet)
            {
                if(stateIdTargeted.intValue() == stateUserInterfaceId.getStateUserInterfaceId().intValue())
                {
                    logger.debug("StateUserInterfaceId: {} targeted by ad: {} and is detected in request,passing ad.",
                                 stateUserInterfaceId.getStateUserInterfaceId(),adId);

                    ReqLog.requestDebug(request,"StateUserInterfaceId:"+
                                        stateUserInterfaceId.getStateUserInterfaceId()  +
                                        "targeted by ad: {} " + adId +
                                        " and is detected in request,passing ad.");
                    shortlistedAdIdSet.add(adId);
                    targetingFound = true;
                    break;
                }
            }
            if(!targetingFound)
            {
                logger.debug("StateUserInterfaceId: {} targeted by ad: {} and is not detected in request,failing ad.",
                             stateUserInterfaceId.getStateUserInterfaceId(),adId);
                ReqLog.requestDebug(request,"StateUserInterfaceId: " + stateUserInterfaceId.getStateUserInterfaceId()
                                    + " targeted by ad: " + adId + " and is not detected in request, failing ad.");

                AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(),
                                                              this.adNoFillReasonMapKey, context);
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(noFillReason);

        return shortlistedAdIdSet;
    }
}