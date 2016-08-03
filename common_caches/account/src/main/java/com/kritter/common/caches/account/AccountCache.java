package com.kritter.common.caches.account;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.constants.OpenRTBVersion;
import com.kritter.constants.ThirdPartyDemandChannel;
import com.kritter.entity.demand_props.DemandProps;
import com.kritter.entity.supply_props.SupplyProps;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**

 */
public class AccountCache extends AbstractDBStatsReloadableQueryableCache<String, AccountEntity>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private final String name;

    public AccountCache(List<Class> secIndexKeyClassList,
                                     Properties props,
                                     DatabaseManager dbMgr,
                                     String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
            AccountEntity entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    protected AccountEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        try
        {
            int id = resultSet.getInt("id");
            String guid = resultSet.getString("guid");
            int demandtype = resultSet.getInt("demandtype");
            int demandpreference = resultSet.getInt("demandpreference");
            int qps = resultSet.getInt("qps");
            int timeout = resultSet.getInt("timeout");
            int currency = resultSet.getInt("currency");
            Timestamp last_modified = resultSet.getTimestamp("last_modified");
            String demand_props = resultSet.getString("demand_props");
            boolean test = false;
            String demand_url = null;
            if(demand_props != null){
                String demand_props_trim = demand_props.trim();
                if(!"".equals(demand_props_trim)){
                    DemandProps demandProps = DemandProps.getObject(demand_props_trim);
                    test=demandProps.isTest();
                    demand_url = demandProps.getDemand_url();
                }
            }
            String supply_props = resultSet.getString("supply_props");
            if(supply_props != null){
                String supply_props_trim = supply_props.trim();
                if(!"".equals(supply_props_trim)){
                    SupplyProps supplyProps = SupplyProps.getObject(supply_props_trim);
                    return new AccountEntity(id, guid, demandtype, demandpreference, qps, 
                            timeout, last_modified,currency,test, supplyProps.getBtype(),demand_url);
                }
            }

            AccountEntity accountEntity = new AccountEntity(id, guid, demandtype, demandpreference, qps,
                                                            timeout, last_modified,currency,test, null,demand_url);

            int thirdPartyDemandChannelType = resultSet.getInt("third_party_demand_channel_type");
            if(thirdPartyDemandChannelType == ThirdPartyDemandChannel.MARKETPLACE_OF_DSP.getCode())
                accountEntity.setThirdPartyDemandChannel(ThirdPartyDemandChannel.MARKETPLACE_OF_DSP);
            else if(thirdPartyDemandChannelType == ThirdPartyDemandChannel.STANDALONE_DSP_BIDDER.getCode())
                accountEntity.setThirdPartyDemandChannel(ThirdPartyDemandChannel.STANDALONE_DSP_BIDDER);

            int openRTBVersionRequired = resultSet.getInt("open_rtb_ver_required");
            if(openRTBVersionRequired == OpenRTBVersion.VERSION_2_0.getCode())
                accountEntity.setOpenRTBVersion(OpenRTBVersion.VERSION_2_0);
            else if(openRTBVersionRequired == OpenRTBVersion.VERSION_2_1.getCode())
                accountEntity.setOpenRTBVersion(OpenRTBVersion.VERSION_2_1);
            else if(openRTBVersionRequired == OpenRTBVersion.VERSION_2_2.getCode())
                accountEntity.setOpenRTBVersion(OpenRTBVersion.VERSION_2_2);
            else if(openRTBVersionRequired == OpenRTBVersion.VERSION_2_3.getCode())
                accountEntity.setOpenRTBVersion(OpenRTBVersion.VERSION_2_3);

            return accountEntity;
        }
        catch (SQLException e)
        {
            addToErrorMap("GLOBAL","SQLException while processing AccountEntity ");
            logger.error("SQLException thrown while processing AccountEntity Entry",e);
            throw new RefreshException("SQLException thrown while processing AccountEntity Entry",e);
        }
        catch (IOException e)
        {
            addToErrorMap("GLOBAL","IOException while processing AccountEntity ");
            logger.error("IOException thrown while processing AccountEntity Entry",e);
            throw new RefreshException("IOException thrown while processing AccountEntity Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public AccountEntity query(String primaryIndexKey) {
        return super.query(primaryIndexKey);
    }
}
