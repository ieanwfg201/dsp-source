package com.kritter.serving.demand.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.serving.demand.entity.CreativeSlot;
import com.kritter.utils.databasemanager.DatabaseManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 *This class is responsible to load creative slot data from database to in-memory cache.
 */

public class CreativeSlotCache extends AbstractDBStatsReloadableQueryableCache<Short,CreativeSlot>
{

    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    @Getter
    private final String name;

    public CreativeSlotCache(List<Class> secIndexKeyClassList, Properties props,
                         DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,CreativeSlot entity)
    {
        return null;
    }

    @Override
    protected CreativeSlot buildEntity(ResultSet resultSet) throws RefreshException
    {
        Short id = null;
        try
        {
            id = resultSet.getShort("id");
            Short width = resultSet.getShort("width");
            Short height = resultSet.getShort("height");
            boolean isMarkedForDeletion = false;

            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            return new CreativeSlot.CreativeSlotBuilder(id, width, height,isMarkedForDeletion, lastModified).build();
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CreativeSlotCache entry: " + id);
            logger.error("Exception thrown while processing CreativeSlotCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing CreativeSlotCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }
}