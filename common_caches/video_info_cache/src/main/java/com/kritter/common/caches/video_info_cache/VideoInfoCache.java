package com.kritter.common.caches.video_info_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.video_info_cache.entity.VideoInfoCacheEntity;
import com.kritter.entity.video_props.VideoInfoExt;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps video info entities, this cache is used in formatting
 * icons of  , where if the request complies then appropriate size icon is
 * chosen for the serving.
 */
public class VideoInfoCache extends AbstractDBStatsReloadableQueryableCache<Integer, VideoInfoCacheEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    public VideoInfoCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected VideoInfoCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        String guid = null;
        try
        {
            id = resultSet.getInt("id");
            guid = resultSet.getString("guid");
            String account_guid = resultSet.getString("account_guid");
            int video_size = resultSet.getInt("video_size");
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
            String ext = resultSet.getString("ext");
            VideoInfoExt vie = null;
            if(ext != null){
            	String extStr = ext.trim();
            	if(!"".equals(extStr)){
            		vie = VideoInfoExt.getObject(extStr);
            	}
            }
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            VideoInfoCacheEntity vice = new VideoInfoCacheEntity(id, guid, account_guid, video_size, 
                    resourceUri, lastModified, isMarkedForDeletion, vie);
            return vice;
        } catch(Exception e) {
            addToErrorMap(id, "Exception while processing VideoInfoCache entry: " + id);
            logger.error("Exception thrown while processing VideoInfoCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing VideoInfoCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, VideoInfoCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
