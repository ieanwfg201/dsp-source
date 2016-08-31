package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.FreqEventType;
import com.kritter.entity.user.recenthistory.LifetimeDemandHistoryProvider;
import com.kritter.serving.demand.cache.AdEntityCache;
import lombok.Getter;

public class LifetimeAdClickCapFilter extends LifetimeAdCapFilter {
    private static final NoFillReason noFillReason = NoFillReason.FREQUENCY_CAP;

    @Getter
    private String name;

    public LifetimeAdClickCapFilter(String name,
                                   String loggerName,
                                   AdEntityCache adEntityCache,
                                   LifetimeDemandHistoryProvider lifetimeAdHistoryProvider,
                                   String adNoFillReasonMapKey) {
        super(name, loggerName, adEntityCache, lifetimeAdHistoryProvider, adNoFillReasonMapKey);
        this.name = name;
    }

    @Override
    public FreqEventType getEventType() {
        return FreqEventType.CLK;
    }

    @Override
    public NoFillReason getNoFillReason() {
        return noFillReason;
    }
}
