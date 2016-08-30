package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.constants.FreqEventType;
import com.kritter.entity.user.recenthistory.LifetimeDemandHistoryProvider;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import lombok.Getter;

public class LifetimeCampaignClickCapFilter extends LifetimeCampaignCapFilter {
    private static final NoFillReason noFillReason = NoFillReason.FREQUENCY_CAP;

    @Getter
    private String name;

    public LifetimeCampaignClickCapFilter(String name,
                                        String loggerName,
                                        AdEntityCache adEntityCache,
                                        CampaignCache campaignCache,
                                        LifetimeDemandHistoryProvider lifetimeDemandHistoryProvider,
                                        String adNoFillReasonMapKey) {
        super(name, loggerName, adEntityCache, campaignCache, lifetimeDemandHistoryProvider, adNoFillReasonMapKey);
        this.name = name;
    }

    @Override
    public FreqEventType getEventType() {
        return FreqEventType.CLK;
    }
}
