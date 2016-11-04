package com.kritter.common.caches.banner_upload_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.banner_upload_cache.entity.BannerUploadCacheEntity;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadBanner;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps banner upload cache entities
 */
public class BannerUploadCache extends AbstractDBStatsReloadableQueryableCache<String, BannerUploadCacheEntity>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public BannerUploadCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected BannerUploadCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
    	Integer internalId = null;
        try{
        	MaterialUploadBanner mub= new MaterialUploadBanner();
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            internalId = resultSet.getInt("internalid");
            mub.setCreativeId(resultSet.getInt("creativeId"));
            mub.setBannerId(resultSet.getInt("bannerId"));
            mub.setAdId(resultSet.getInt("adId"));
            mub.setAdxbasedexhangesstatus(resultSet.getInt("adxbasedexhangesstatus"));
            mub.setPubIncId(resultSet.getInt("pubIncId"));
            isMarkedForDeletion=resultSet.getBoolean("banner_upload");
            BannerUploadCacheEntity vice = new BannerUploadCacheEntity(mub, lastModified, !isMarkedForDeletion);
            return vice;
        } catch(Exception e) {
            addToErrorMap(internalId+"id", "Exception while processing BannerUploadCacheEntity entry: " + internalId);
            logger.error("Exception thrown while processing BannerUploadCacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing BannerUploadCacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, BannerUploadCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
