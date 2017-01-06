package com.kritter.bidder.online.cache;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.bidder.online.entity.BidderDataDBEntity;
import com.kritter.utils.databasemanager.DatabaseManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Properties;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Loads up the logistic regression CTR cache. 
 */
public class ServedEntityBidderDataDBCache extends AbstractDBStatsReloadableQueryableCache<Integer, BidderDataDBEntity>
{
    private String cacheName;
    private static Logger logger = LogManager.getLogger("cache.logger");

    public ServedEntityBidderDataDBCache(List<Class> secIndexKeyClassList,
                                         Properties properties,
                                         DatabaseManager dbMgr,
                                         String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, properties, dbMgr);
        this.cacheName = cacheName;
    }

    @Override
    public String getName()
    {
        return cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class classname, BidderDataDBEntity entity)
    {
        return null;
    }

    @Override
    protected BidderDataDBEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        try
        {
            Integer id = resultSet.getInt("id");
            String data = resultSet.getString("data");
            Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

            return BidderDataDBEntity.populateEntityUsingData(logger,id, data, lastModifiedOn.getTime());
        }
        catch(SQLException e)
        {
            logger.error("SQLException thrown while processing model {}", e);
            throw new RefreshException("SQLException thrown while processing model {}", e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }
}