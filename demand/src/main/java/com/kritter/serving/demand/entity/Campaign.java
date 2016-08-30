package com.kritter.serving.demand.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.freqcap_entity.FreqCap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * This class represents a campaign entity in the system.
 * A campaign is supposed to contain status and flight dates along with budget information.
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"campaignGuid"})
public class Campaign implements IUpdatableEntity<Integer>
{
    private final Integer campaignIncId;
    private final String campaignGuid;
    private final String accountId;
    private final Integer statusId;
    private final Timestamp startDate;
    private final Timestamp endDate;
    private final Double dailyBudgetRemaining;
    private final Double campaignTotalBudgetRemaining;
    private final Double campaignAdvertiserDailyRemainingBudget;
    private final Double campaignAdvertiserTotalRemainingBudget;
    private final Double accountTotalBudgetRemaining;
    private final Double accountAdvertiserTotalBudgetRemaining;
    private final FreqCap frequencyCap;

    private final boolean markedForDeletion;
    private final Long modificationTime;

    public Campaign(CampaignBuilder campaignBuilder)
    {
        this.campaignIncId = campaignBuilder.campaignIncId;
        this.campaignGuid = campaignBuilder.campaignGuid;
        this.accountId = campaignBuilder.accountId;
        this.statusId = campaignBuilder.statusId;
        this.startDate = campaignBuilder.startDate;
        this.endDate = campaignBuilder.endDate;
        this.dailyBudgetRemaining = campaignBuilder.dailyBudgetRemaining;
        this.campaignTotalBudgetRemaining = campaignBuilder.campaignTotalBudgetRemaining;
        this.accountTotalBudgetRemaining = campaignBuilder.accountTotalBudgetRemaining;
        this.campaignAdvertiserDailyRemainingBudget = campaignBuilder.campaignAdvertiserDailyRemainingBudget;
        this.campaignAdvertiserTotalRemainingBudget = campaignBuilder.campaignAdvertiserTotalRemainingBudget;
        this.accountAdvertiserTotalBudgetRemaining = campaignBuilder.accountAdvertiserTotalBudgetRemaining;
        this.frequencyCap = campaignBuilder.frequencyCap;
        this.markedForDeletion = campaignBuilder.isMarkedForDeletion;
        this.modificationTime = campaignBuilder.updateTime;
    }

    @Override
    public Integer getId()
    {
        return this.campaignIncId;
    }

    public static class CampaignBuilder
    {
        private final Integer campaignIncId;
        private final String campaignGuid;
        private final String accountId;
        private final Integer statusId;
        private final Timestamp startDate;
        private final Timestamp endDate;
        private final Double dailyBudgetRemaining;
        private final Double campaignTotalBudgetRemaining;
        private final Double campaignAdvertiserDailyRemainingBudget;
        private final Double campaignAdvertiserTotalRemainingBudget;
        private final Double accountTotalBudgetRemaining;
        private final Double accountAdvertiserTotalBudgetRemaining;
        private FreqCap frequencyCap;

        private boolean isMarkedForDeletion;
        private Long updateTime;

        public CampaignBuilder(Integer campaignIncId, String campaignGuid,
                               String accountId, Integer statusId,
                               Timestamp startDate, Timestamp endDate,
                               Double dailyBudgetRemaining,
                               Double campaignTotalBudgetRemaining,
                               Double campaignAdvertiserDailyRemainingBudget,
                               Double campaignAdvertiserTotalRemainingBudget,
                               Double accountTotalBudgetRemaining,
                               Double accountAdvertiserTotalBudgetRemaining,
                               boolean isMarkedForDeletion, Long updateTime)
        {
            this.campaignIncId = campaignIncId;
            this.campaignGuid = campaignGuid;
            this.accountId = accountId;
            this.statusId = statusId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.dailyBudgetRemaining = dailyBudgetRemaining;
            this.campaignTotalBudgetRemaining = campaignTotalBudgetRemaining;
            this.campaignAdvertiserDailyRemainingBudget = campaignAdvertiserDailyRemainingBudget;
            this.campaignAdvertiserTotalRemainingBudget = campaignAdvertiserTotalRemainingBudget;
            this.accountTotalBudgetRemaining = accountTotalBudgetRemaining;
            this.accountAdvertiserTotalBudgetRemaining = accountAdvertiserTotalBudgetRemaining;
            this.isMarkedForDeletion = isMarkedForDeletion;
            this.updateTime = updateTime;
        }

        public CampaignBuilder setFrequencyCap(String frequencyCapStr) throws Exception {
            if(frequencyCapStr != null && !frequencyCapStr.isEmpty()) {
                this.frequencyCap = FreqCap.getObject(frequencyCapStr);
            }
            return this;
        }

        public Campaign build()
        {
            return new Campaign(this);
        }
    }
}