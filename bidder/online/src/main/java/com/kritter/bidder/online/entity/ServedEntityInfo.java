package com.kritter.bidder.online.entity;

import com.kritter.constants.MarketPlace;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * This class models the serving entity of any adserver.
 * For our purposes it is adid, some other adserver may
 * have some other notion of serving entity.
 */
@EqualsAndHashCode(of={"entityId"})
public class ServedEntityInfo
{
    @Getter
    private int entityId;
    @Getter
    private double maxBid;
    @Getter
    private double dailyRemainingBudget;
    @Getter
    private double ctrProbabilityForEntity;
    @Getter
    private double cpaProbabilityForEntity;
    @Getter
    private MarketPlace marketPlace;

    public ServedEntityInfo(int entityId,
                            double maxBid,
                            double dailyRemainingBudget,
                            double ctrProbabilityForEntity,
                            double cpaProbabilityForEntity,
                            MarketPlace marketPlace)
    {
        this.entityId = entityId;
        this.maxBid = maxBid;
        this.dailyRemainingBudget = dailyRemainingBudget;
        this.ctrProbabilityForEntity = ctrProbabilityForEntity;
        this.cpaProbabilityForEntity = cpaProbabilityForEntity;
        this.marketPlace = marketPlace;
    }
}
