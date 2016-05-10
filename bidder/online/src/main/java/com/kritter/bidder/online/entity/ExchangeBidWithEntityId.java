package com.kritter.bidder.online.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * This class is output of online bidder as entity id
 * with its bid value for the exchange.
 */
@EqualsAndHashCode(of={"entityId"})
public class ExchangeBidWithEntityId
{
    @Getter
    private int entityId;
    @Getter
    private double bidValue;

    public ExchangeBidWithEntityId(int entityId, double bidValue)
    {
        this.entityId = entityId;
        this.bidValue = bidValue;
    }
}