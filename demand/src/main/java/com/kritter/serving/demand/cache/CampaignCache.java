package com.kritter.serving.demand.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.serving.demand.entity.Campaign;
import com.kritter.constants.StatusIdEnum;
import com.kritter.serving.demand.indexbuilder.CampaignSecondaryIndexBuilder;
import com.kritter.utils.databasemanager.DatabaseManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This cache is responsible for loading/unloading Campaign entities periodically to the in-memory Cache.
 */
public class CampaignCache extends AbstractDBStatsReloadableQueryableCache<Integer, Campaign>
{
    public static final String ABSOLUTE_PAYOUT_THRESHOLD_KEY = "absolute_payout_threshold";
    public static final String PERCENT_PAYOUT_THRESHOLD_KEY = "percent_payout_threshold";

    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    @Getter private final String name;
    @Getter private final Double absolutePayoutThreshold;
    @Getter private final Double percentPayoutThreshold;

    public CampaignCache(List<Class> secIndexKeyClassList, Properties props,
                         DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
        this.absolutePayoutThreshold = Double.parseDouble(props.getProperty(ABSOLUTE_PAYOUT_THRESHOLD_KEY));
        this.percentPayoutThreshold = Double.parseDouble(props.getProperty(PERCENT_PAYOUT_THRESHOLD_KEY));
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, Campaign entity)
    {
        return CampaignSecondaryIndexBuilder.getIndex(className, entity);
    }

    @Override
    protected Campaign buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        try{

            id = resultSet.getInt("id");
            Double accountBalance = resultSet.getDouble("account_balance");
            Double campaignTotalBalance = resultSet.getDouble("internal_total_remaining_budget");
            String guid = resultSet.getString("guid");
            String accountId = resultSet.getString("account_id");
            Timestamp startDate = resultSet.getTimestamp("start_date");
            Timestamp endDate = resultSet.getTimestamp("end_date");
            Double campaignDailyBudget = resultSet.getDouble("campaign_adv_daily_budget");
            Double dailyBudgetRemaining = resultSet.getDouble("internal_daily_remaining_budget");
            Double accountAdvTotalBudget = resultSet.getDouble("adv_balance");
            Double campaignAdvertiserDailyRemainingBudget = resultSet.getDouble("campaign_adv_daily_remaining_budget");
            Double campaignAdvertiserTotalRemainingBudget = resultSet.getDouble("campaign_adv_total_remaining_budget");
            int impressionCap = resultSet.getInt("impression_cap");
            int impressionsAccrued = resultSet.getInt("impressions_accrued");
            Integer statusId = resultSet.getInt("status_id");
            boolean isMarkedForDeletion = false;
            Double campaignPayout = resultSet.getDouble("campaign_payout_exchange_payout");
            if(!(statusId == StatusIdEnum.Active.getCode()))
                isMarkedForDeletion = true;

            // If the impression cap has already been hit, mark the ad for deletion
            if(impressionCap != 0 && impressionsAccrued >= impressionCap)
                isMarkedForDeletion = true;

            // If the campaign payout has already hit the limit, don't serve it any more
            Double campaignBurn = campaignDailyBudget - dailyBudgetRemaining;
            if(campaignBurn + absolutePayoutThreshold < campaignPayout) {
                isMarkedForDeletion = true;
            } else if((campaignPayout - campaignBurn) > (percentPayoutThreshold * campaignDailyBudget) / 100) {
                isMarkedForDeletion = true;
            }

            Long updateTime = resultSet.getTimestamp("last_modified").getTime();

            return new Campaign.CampaignBuilder(id,guid,accountId,
                                                statusId,
                                                startDate,endDate,
                                                dailyBudgetRemaining,
                                                campaignTotalBalance,
                                                campaignAdvertiserDailyRemainingBudget,
                                                campaignAdvertiserTotalRemainingBudget,
                                                accountBalance,
                                                accountAdvTotalBudget,
                                                isMarkedForDeletion,
                                                updateTime).build();
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CampaignCache entry: " + id);
            logger.error("Exception thrown while processing CampaignCache Entry",e);
            throw new RefreshException("Exception thrown while processing CampaignCache Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {}
}
