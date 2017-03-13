package com.kritter.common.caches.audience_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.audience_cache.entity.AudienceCacheEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps mma cache entities
 */
public class AudienceCache extends AbstractDBStatsReloadableQueryableCache<Integer, AudienceCacheEntity> {
    private static final String CTRL_A = String.valueOf((char) 1);
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    public AudienceCache(List<Class> secIndexKeyClassList, Properties props,
                         DatabaseManager dbMgr, String cacheName)
            throws InitializationException {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected AudienceCacheEntity buildEntity(ResultSet resultSet) throws RefreshException {
        Integer id = null;
        String name;
        Integer source_id;
        String tags;
        Integer type;

        try {
            id = resultSet.getInt("id");
            name = resultSet.getString("name");
            source_id = resultSet.getInt("source_id");
            tags = resultSet.getString("tags");
            type = resultSet.getInt("type");

            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();

            AudienceCacheEntity vice = new AudienceCacheEntity(id, name, source_id, tags, type, lastModified, isMarkedForDeletion);
            return vice;
        } catch (Exception e) {
            addToErrorMap(id, "Exception while processing MMACacheEntity entry: ");
            logger.error("Exception thrown while processing MMACacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing MMACacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, AudienceCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
