package com.kritter.common.caches.adxbasedexchanges_metadata_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.adxbasedexchanges_metadata_cache.entity.AdxBasedMetadataCacheEntity;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps adxbasedexchanges_metadata upload cache entities
 */
public class AdxBasedExchangesMetadataCache extends AbstractDBStatsReloadableQueryableCache<String, AdxBasedMetadataCacheEntity>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public AdxBasedExchangesMetadataCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected AdxBasedMetadataCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
    	Integer internalId = null;
        try{
        	AdxBasedExchangesMetadata mub= new AdxBasedExchangesMetadata();
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            internalId = resultSet.getInt("internalid");
            mub.setPubIncId(resultSet.getInt("pubIncId"));
            mub.setAdvertiser_upload(resultSet.getBoolean("advertiser_upload"));
            mub.setBanner_upload(resultSet.getBoolean("banner_upload"));
            mub.setVideo_upload(resultSet.getBoolean("video_upload"));
            AdxBasedMetadataCacheEntity vice = new AdxBasedMetadataCacheEntity(mub, lastModified, isMarkedForDeletion);
            return vice;
        } catch(Exception e) {
            addToErrorMap(internalId+"id", "Exception while processing AdxBasedMetadataCacheEntity entry: " + internalId);
            logger.error("Exception thrown while processing AdxBasedMetadataCacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing AdxBasedMetadataCacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, AdxBasedMetadataCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
