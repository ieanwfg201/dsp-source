package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.SupplySourceEnum;
import com.kritter.core.workflow.Context;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class InventorySourceTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;

    public InventorySourceTargetingMatcher(String name, String loggerName, AdEntityCache adEntityCache) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        logger.info("Inside filterAdsOnInventorySource of AdTargetingMatcher...");
        ReqLog.requestDebug(request,"Inside filterAdsOnInventorySource of AdTargetingMatcher...");

        Set<Integer> shortlistedAdIdSet = new HashSet<Integer>();

        for(Integer adId : adIdSet)
        {
            AdEntity adEntity = adEntityCache.query(adId);

            if(null == adEntity)
            {
                ReqLog.errorWithDebug(logger,request,"AdEntity not found in cache id : {}" , adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();

            logger.debug("AdId: {} supplysource targeting id: {} and request's inventory source is : {}", adEntity.getId(), targetingProfile.getSupplySource(), request.getInventorySource());

            if
                    (
                    targetingProfile.getSupplySource() == SupplySourceEnum.EXCHANGE_NETWORK.getCode() ||

                            (
                                    targetingProfile.getSupplySource() == SupplySourceEnum.EXCHANGE.getCode() &&
                                            request.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode()
                            )                                                                                 ||

                            (
                                    targetingProfile.getSupplySource() == SupplySourceEnum.NETWORK.getCode() &&
                                            (
                                                    (request.getInventorySource() == INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode()) ||
                                                            (request.getInventorySource() == INVENTORY_SOURCE.AGGREGATOR.getCode())       ||
                                                            (request.getInventorySource() == INVENTORY_SOURCE.DCP.getCode())              ||
                                                            (request.getInventorySource() == INVENTORY_SOURCE.SSP.getCode())
                                            )
                            )
                    )
                shortlistedAdIdSet.add(adId);

            else
            {
                ReqLog.debugWithDebug(logger,request,"SupplySource targeting for adId: {}  ,which is: {}  , does not qualify for inventory supply source , which is:  {}",
                        adEntity.getAdGuid(),SupplySourceEnum.getEnum(targetingProfile.getSupplySource()).getName(), INVENTORY_SOURCE.getEnum(request.getInventorySource()).getInventorySource());
            }
        }

        if(null == request.getNoFillReason() && shortlistedAdIdSet.size() <= 0)
            request.setNoFillReason(Request.NO_FILL_REASON.AD_INV_SRC);

        return shortlistedAdIdSet;
    }
}
