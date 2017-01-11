package com.kritter.common.site.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.site.entity.SiteMetaDataEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps site metadata in memory.
 */
public class SiteMetaDataCache extends AbstractDBStatsReloadableQueryableCache<String, SiteMetaDataEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private final String name;

    public SiteMetaDataCache(
                              List<Class> secIndexKeyClassList,
                              Properties props,
                              DatabaseManager dbMgr,
                              String cacheName
                             ) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(
                                                       Class className,
                                                       SiteMetaDataEntity entity
                                                      )
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    protected SiteMetaDataEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        String siteId = null;

        try
        {
            siteId = resultSet.getString("site_guid");
            String passbackUrl = resultSet.getString("passback_url");
            String responseContentType = resultSet.getString("response_content_type");
            String responseContent = resultSet.getString("response_content");

            return new SiteMetaDataEntity(
                                          siteId,
                                          passbackUrl,
                                          responseContentType,
                                          responseContent,
                                          System.currentTimeMillis()
                                         );
        }
        catch (SQLException e)
        {
            addToErrorMap(siteId,"SQL exception while processing SiteMetaDataEntity entry: " + siteId);
            logger.error("SQLException thrown while processing SiteMetaDataEntity Entry",e);
            throw new RefreshException("SQLException thrown while processing SiteMetaDataEntity Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
        // no data structures held. Nothing to release
    }
}