package com.kritter.common.caches.native_icon_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.native_icon_cache.entity.NativeIconCacheEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps native icon entities, this cache is used in formatting
 * icons of  , where if the request complies then appropriate size icon is
 * chosen for the serving.
 */
public class NativeIconCache extends AbstractDBStatsReloadableQueryableCache<Integer, NativeIconCacheEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    public NativeIconCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected NativeIconCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        String guid = null;
        try
        {
            id = resultSet.getInt("id");
            guid = resultSet.getString("guid");
            String account_guid = resultSet.getString("account_guid");
            int icon_size = resultSet.getInt("icon_size");
            String resourceUri = resultSet.getString("resource_uri");
            boolean isMarkedForDeletion = false;
            if(resourceUri != null){
                String resourceUriTrim = resourceUri.trim();
                if(!"".equals(resourceUriTrim)){
                    String[] resourceUriParts = resourceUriTrim.split("/");
                    if(resourceUriParts.length <=0 ){
                        isMarkedForDeletion = true;
                    }else{
                        resourceUri = resourceUriParts[resourceUriParts.length-1];
                    }
                }
            }

            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            NativeIconCacheEntity nice = new NativeIconCacheEntity(id, guid, account_guid, icon_size, 
                    resourceUri, lastModified, isMarkedForDeletion);
            return nice;
        } catch(Exception e) {
            addToErrorMap(id, "Exception while processing NativeIconCache entry: " + id);
            logger.error("Exception thrown while processing NativeIconCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing NativeIconCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, NativeIconCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
