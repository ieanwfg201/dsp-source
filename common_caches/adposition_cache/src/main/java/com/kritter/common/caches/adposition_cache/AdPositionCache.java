package com.kritter.common.caches.adposition_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.adposition_cache.entity.AdPositionCacheEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps adposition cache entities
 */
public class AdPositionCache extends AbstractDBStatsReloadableQueryableCache<String, AdPositionCacheEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    public AdPositionCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected AdPositionCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer internalid = null;
        String adposid = null;
        Integer pubIncId = null;
        try
        {
        	internalid = resultSet.getInt("internalid");
        	adposid = resultSet.getString("adposid");
        	pubIncId = resultSet.getInt("pubIncId");
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            AdPositionCacheEntity vice = new AdPositionCacheEntity(pubIncId, adposid, internalid, 
                    lastModified, isMarkedForDeletion);
            return vice;
        } catch(Exception e) {
            addToErrorMap(adposid+"id", "Exception while processing AdPositionCacheEntity entry: " + internalid);
            logger.error("Exception thrown while processing AdPositionCacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing AdPositionCacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, AdPositionCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
