package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.FreqEventType;
import com.kritter.entity.user.recenthistory.LifetimeDemandHistoryProvider;
import com.kritter.serving.demand.cache.AdEntityCache;
import lombok.Getter;

public class LifetimeAdImpCapFilter extends LifetimeAdCapFilter {
    private static final NoFillReason noFillReason = NoFillReason.FREQUENCY_CAP;

    @Getter
    private String name;

    public LifetimeAdImpCapFilter(String name,
                                  String loggerName,
                                  AdEntityCache adEntityCache,
                                  LifetimeDemandHistoryProvider lifetimeAdImpHistoryProvider,
                                  String adNoFillReasonMapKey) {
        super(name, loggerName, adEntityCache, lifetimeAdImpHistoryProvider, adNoFillReasonMapKey);
        this.name = name;
    }

    @Override
    public FreqEventType getEventType() {
        return FreqEventType.IMP;
    }

    @Override
    public NoFillReason getNoFillReason() {
        return noFillReason;
    }
}
