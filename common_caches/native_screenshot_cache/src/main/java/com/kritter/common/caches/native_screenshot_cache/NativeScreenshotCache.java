package com.kritter.common.caches.native_screenshot_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.native_screenshot_cache.entity.NativeScreenshotCacheEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps native screenshot entities, this cache is used in formatting
 * screenshots of  , where if the request complies then appropriate size screenshot is
 * chosen for the serving.
 */
public class NativeScreenshotCache extends AbstractDBStatsReloadableQueryableCache<Integer, NativeScreenshotCacheEntity>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public NativeScreenshotCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected NativeScreenshotCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        String guid = null;
        try
        {
            id = resultSet.getInt("id");
            guid = resultSet.getString("guid");
            String account_guid = resultSet.getString("account_guid");
            int ss_size = resultSet.getInt("ss_size");
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
            NativeScreenshotCacheEntity nice = new NativeScreenshotCacheEntity(id, guid, account_guid, ss_size, 
                    resourceUri, lastModified, isMarkedForDeletion);
            return nice;
        } catch(Exception e) {
            addToErrorMap(id, "Exception while processing NativeScreenshotCache entry: " + id);
            logger.error("Exception thrown while processing NativeScreenshotCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing NativeScreenshotCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, NativeScreenshotCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
