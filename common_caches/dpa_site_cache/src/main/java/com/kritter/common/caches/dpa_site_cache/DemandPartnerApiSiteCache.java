package com.kritter.common.caches.dpa_site_cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.cache.dpa_site_cache.entity.DemandPartnerApiSiteMetadata;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps site metadata for demand partner api.
 * This metadata is primarily for defining rules based on which demand partner
 * apis are invoked and used.
 */
public class DemandPartnerApiSiteCache extends AbstractDBStatsReloadableQueryableCache<String, DemandPartnerApiSiteMetadata>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private final String name;

    public DemandPartnerApiSiteCache(List<Class> secIndexKeyClassList,
                                     Properties props,
                                     DatabaseManager dbMgr,
                                     String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
                                                       DemandPartnerApiSiteMetadata entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    protected DemandPartnerApiSiteMetadata buildEntity(ResultSet resultSet) throws RefreshException
    {
        String pubGuid = null;
        try
        {
            Integer id = resultSet.getInt("id");
            pubGuid = resultSet.getString("pub_guid");
            String siteGuid = resultSet.getString("site_guid");
            String ruleJson = resultSet.getString("rule_json");
            Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

            return new DemandPartnerApiSiteMetadata(id,siteGuid,pubGuid,ruleJson,lastModifiedOn);

        }
        catch (SQLException e)
        {
            addToErrorMap(pubGuid,"SQLException while processing DemandPartnerApiSiteMetadata pubid: " + pubGuid);
            logger.error("SQLException thrown while processing DemandPartnerApiSiteMetadata Entry",e);
            throw new RefreshException("SQLException thrown while processing DemandPartnerApiSiteMetadata Entry",e);
        }
        catch (IOException e)
        {
            addToErrorMap(pubGuid,"IOException while processing DemandPartnerApiSiteMetadata pubid: " + pubGuid);
            logger.error("IOException thrown while processing DemandPartnerApiSiteMetadata Entry",e);
            throw new RefreshException("IOException thrown while processing DemandPartnerApiSiteMetadata Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }
}
