package com.kritter.common.caches.video_upload_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.video_upload_cache.entity.VideoUploadCacheEntity;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadVideo;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps video upload cache entities
 */
public class VideoUploadCache extends AbstractDBStatsReloadableQueryableCache<String, VideoUploadCacheEntity>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public VideoUploadCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected VideoUploadCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
    	Integer internalId = null;
        try{
        	MaterialUploadVideo muv= new MaterialUploadVideo();
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            internalId = resultSet.getInt("internalid");
            muv.setCreativeId(resultSet.getInt("creativeId"));
            muv.setVideoInfoId(resultSet.getInt("videoInfoId"));
            muv.setAdxbasedexhangesstatus(resultSet.getInt("adxbasedexhangesstatus"));
            muv.setPubIncId(resultSet.getInt("pubIncId"));
            isMarkedForDeletion=resultSet.getBoolean("video_upload");
            VideoUploadCacheEntity vice = new VideoUploadCacheEntity(muv, lastModified, !isMarkedForDeletion);
            return vice;
        } catch(Exception e) {
            addToErrorMap(internalId+"id", "Exception while processing VideoUploadCacheEntity entry: " + internalId);
            logger.error("Exception thrown while processing VideoUploadCacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing VideoUploadCacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, VideoUploadCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
