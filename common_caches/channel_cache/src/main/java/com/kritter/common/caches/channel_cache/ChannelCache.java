package com.kritter.common.caches.channel_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.channel_cache.entity.ChannelCacheEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps channel cache entities
 */
public class ChannelCache extends AbstractDBStatsReloadableQueryableCache<String, ChannelCacheEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    public ChannelCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected ChannelCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer internalid = null;
        String channelcode = null;
        Integer pubIncId = null;
        try
        {
        	internalid = resultSet.getInt("internalid");
        	channelcode = resultSet.getString("channelcode");
        	pubIncId = resultSet.getInt("pubIncId");
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            ChannelCacheEntity vice = new ChannelCacheEntity(pubIncId, channelcode, internalid, 
                    lastModified, isMarkedForDeletion);
            return vice;
        } catch(Exception e) {
            addToErrorMap(channelcode+" id", "Exception while processing ChannelCacheEntity entry: " + internalid);
            logger.error("Exception thrown while processing ChannelCacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing ChannelCacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, ChannelCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
