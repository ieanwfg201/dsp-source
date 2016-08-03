package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps database for dsp and its advertiser mapping.
 */
public class DSPAndAdvertiserMappingCache extends AbstractDBStatsReloadableQueryableCache<Integer, DSPAndAdvertiserMappingEntity>
{
    private static final Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public DSPAndAdvertiserMappingCache(List<Class> secIndexKeyClassList,
                                        Properties props,
                                        DatabaseManager dbMgr,
                                        String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected DSPAndAdvertiserMappingEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer  id = null;

        try
        {
            id = resultSet.getInt("id");
            String dspId = resultSet.getString("dsp_id");
            String advId = resultSet.getString("adv_id");
            Timestamp lastModified = resultSet.getTimestamp("last_modified");

            return new DSPAndAdvertiserMappingEntity(id,dspId,advId,lastModified);
        }
        catch (SQLException sqle)
        {
            addToErrorMap(id, "SQLException while processing DSPAndAdvertiserMappingEntity");
            logger.error("SQLException thrown while processing DSPAndAdvertiserMappingEntity Entry",sqle);
            throw new RefreshException("SQLException thrown while processing DSPAndAdvertiserMappingEntity Entry",sqle);
        }


    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, DSPAndAdvertiserMappingEntity entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}
