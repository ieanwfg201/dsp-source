package com.kritter.serving.demand.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.serving.demand.entity.CreativeBanner;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps creative banner entities, this cache is used in formatting
 * banner ads , where if the request complies then appropriate size banner is
 * chosen for the serving.
 */
public class CreativeBannerCache extends AbstractDBStatsReloadableQueryableCache<Integer, CreativeBanner>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    public CreativeBannerCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected CreativeBanner buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        String guid = null;
        try
        {
            id = resultSet.getInt("id");
            guid = resultSet.getString("guid");
            String accountId = resultSet.getString("account_guid");
            Short slotId = resultSet.getShort("slot_id");
            String resourceUri = resultSet.getString("resource_uri");

            String[] resourceUriParts = resourceUri.split("/");

            boolean isMarkedForDeletion = false;

            if(resourceUriParts.length <=0 )
                isMarkedForDeletion = true;
            else
                resourceUri = resourceUriParts[resourceUriParts.length-1];

            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            return new CreativeBanner.
                       CreativeBannerBuilder(
                                             id, guid, accountId,slotId,
                                             resourceUri,isMarkedForDeletion,lastModified
                                            )
                                            .build();
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CreativeBannerCache entry: " + id);
            logger.error("Exception thrown while processing CreativeBannerCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing CreativeBannerCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, CreativeBanner entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
