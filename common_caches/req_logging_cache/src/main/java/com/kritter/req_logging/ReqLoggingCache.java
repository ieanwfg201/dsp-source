package com.kritter.req_logging;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

public class ReqLoggingCache extends AbstractDBStatsReloadableQueryableCache<String, ReqLoggingCacheEntity> {

    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public ReqLoggingCache(List<Class> secIndexKeyClassList,
                           Properties props,
                           DatabaseManager dbMgr,
                           String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected ReqLoggingCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        String guid = null;
        try
        {
            guid = resultSet.getString("pubId");

            if(null == guid)
            {
                logger.error("Publisher id is null inside ReqLoggingCache ,skipping entry .");
                return null;
            }

            boolean enable = resultSet.getBoolean("enable");
            int timePeriod = resultSet.getInt("time_period");
            Long createdOn = resultSet.getTimestamp("created_on").getTime();
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();

            /*If time elapsed is more than timePeriod in minutes set enable to false.*/
            long timeRequired = timePeriod * 60 * 1000;
            if((System.currentTimeMillis() - lastModified) >= timeRequired)
                enable = false;

            ReqLoggingCacheEntity reqLoggingCacheEntity = new ReqLoggingCacheEntity();
            reqLoggingCacheEntity.setPubId(guid);
            reqLoggingCacheEntity.setEnable(enable);
            reqLoggingCacheEntity.setTime_period(timePeriod);
            reqLoggingCacheEntity.setCreated_on(createdOn);
            reqLoggingCacheEntity.setLast_modified(lastModified);

            return reqLoggingCacheEntity;
        }
        catch (Exception e)
        {
            addToErrorMap(guid, "Exception while processing ReqLoggingCache entry: " + guid);
            logger.error("Exception thrown while processing ReqLoggingCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing ReqLoggingCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, ReqLoggingCacheEntity entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return name;
    }
}