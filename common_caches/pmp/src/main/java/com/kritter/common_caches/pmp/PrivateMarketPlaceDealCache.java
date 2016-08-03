package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class maintains private marketplace entities.
 */
public class PrivateMarketPlaceDealCache extends AbstractDBStatsReloadableQueryableCache<String, PrivateMarketPlaceCacheEntity>
{
    private static final Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public PrivateMarketPlaceDealCache(List<Class> secIndexKeyClassList,
                                       Properties props,
                                       DatabaseManager dbMgr,
                                       String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected PrivateMarketPlaceCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        String dealId = null;

        try
        {
            dealId = resultSet.getString("deal_id");
            String dealName = resultSet.getString("deal_name");
            String adGuid = resultSet.getString("ad_guid");
            Integer[] siteIdList = ResultSetHelper.getResultSetIntegerArray(resultSet,"site_id_list");
            String[] blockedAdvertiserCategories = ResultSetHelper.getResultSetStringArray(resultSet,"bcat");
            String[] thirdPartyConnectionIdList = ResultSetHelper.getResultSetStringArray
                                                                            (resultSet,"third_party_conn_list");
            Integer[] dspIdList = ResultSetHelper.getResultSetIntegerArray(resultSet,"dsp_id_list");
            Integer[] advertiserIdList = ResultSetHelper.getResultSetIntegerArray(resultSet,"adv_id_list");

            String whitelistedDomainsTextValue = resultSet.getString("wadomain");
            Map<String,String[]> whiteListedDomains = null;
            if(null != whitelistedDomainsTextValue)
            {
                try
                {
                    whiteListedDomains = PrivateMarketPlaceCacheEntity.
                                                    populateWhitelistedAdvertiserDomains(whitelistedDomainsTextValue);
                }
                catch (IOException ioe)
                {
                    logger.error("IOException inside PrivateMarketPlaceDealCache in processing entity ",ioe);
                }
            }

            Short auctionType = resultSet.getShort("auction_type");
            Integer requestCap = resultSet.getInt("request_cap");
            Timestamp startDate = resultSet.getTimestamp("start_date");
            Timestamp endDate = resultSet.getTimestamp("end_date");
            Double dealCpm = resultSet.getDouble("deal_cpm");
            Timestamp lastModified = resultSet.getTimestamp("last_modified");

            return new PrivateMarketPlaceCacheEntity(dealId, dealName, adGuid, siteIdList,blockedAdvertiserCategories,
                                                     thirdPartyConnectionIdList,dspIdList,advertiserIdList,
                                                     whiteListedDomains, auctionType,requestCap,
                                                     startDate, endDate, dealCpm,lastModified);
        }
        catch (SQLException sqle)
        {
            addToErrorMap(dealId, "SQLException while processing PrivateMarketPlaceCacheEntity");
            logger.error("SQLException thrown while processing PrivateMarketPlaceCacheEntity Entry",sqle);
            throw new RefreshException("SQLException thrown while processing PrivateMarketPlaceCacheEntity Entry",sqle);
        }


    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, PrivateMarketPlaceCacheEntity entity)
    {
        return PrivateMarketPlaceCacheEntity.getSecondaryIndexForClass(className,entity);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
