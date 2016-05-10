package com.kritter.bidder.online.bidcalculator;

import com.kritter.bidder.online.entity.ExchangeBidWithEntityId;
import com.kritter.bidder.online.entity.ServedEntityInfo;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface RealTimeBidder
{
    public Map<Integer,ExchangeBidWithEntityId> calculateBidForEntities(Set<ServedEntityInfo> servedEntityInfos);
}
