package com.kritter.common.caches.dpa_ad_cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.dpa_ad_cache.entity.DemandPartnerApiAdMetadata;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps demand api ad metadata in-memory.
 */
public class DemandPartnerApiAdMetadataCache extends AbstractDBStatsReloadableQueryableCache<String, DemandPartnerApiAdMetadata>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private final String name;

    public DemandPartnerApiAdMetadataCache(List<Class> secIndexKeyClassList,
                                           Properties props,
                                           DatabaseManager dbMgr,
                                           String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
                                                       DemandPartnerApiAdMetadata entity)
    {
        return SecondaryIndexBuilder.getAccountIdIndex(entity);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    protected DemandPartnerApiAdMetadata buildEntity(ResultSet resultSet) throws RefreshException
    {
        String adGuid = null;
        try
        {
            Integer id = resultSet.getInt("id");
            adGuid = resultSet.getString("ad_guid");
            String accountGuid = resultSet.getString("account_guid");
            Double ecpm = resultSet.getDouble("ecpm");
            Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

            return new DemandPartnerApiAdMetadata(id, adGuid, accountGuid,ecpm, lastModifiedOn);
        }
        catch (SQLException e)
        {
            addToErrorMap(adGuid,"SQL exception while processing DemandPartnerApiAdMetadata entry: " + adGuid);
            logger.error("SQLException thrown while processing DemandPartnerApiAdMetadata Entry",e);
            throw new RefreshException("SQLException thrown while processing DemandPartnerApiAdMetadata Entry",e);
        }

    }

    @Override
    protected void release() throws ProcessingException
    {
        // no data structures held. Nothing to release
    }
}
