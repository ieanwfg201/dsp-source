package com.kritter.common.caches.advinfo_upload_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.advinfo_upload_cache.entity.AdvInfoUploadCacheEntity;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadAdvInfo;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps video upload cache entities
 */
public class AdvInfoUploadCache extends AbstractDBStatsReloadableQueryableCache<String, AdvInfoUploadCacheEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;
    private String[] approvedStateException;
    
    public AdvInfoUploadCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName,
                               String[] approvedStateException)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
        this.approvedStateException = approvedStateException;
    }

    @Override
    protected AdvInfoUploadCacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
    	Integer internalId = null;
        try{
        	MaterialUploadAdvInfo mua= new MaterialUploadAdvInfo();
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            internalId = resultSet.getInt("internalid");
            mua.setAdvIncId(resultSet.getInt("advIncId"));
            mua.setPubIncId(resultSet.getInt("pubIncId"));
            mua.setAdxbasedexhangesstatus(resultSet.getInt("adxbasedexhangesstatus"));
            if(this.approvedStateException != null && this.approvedStateException.length>0){
            	String str=mua.getPubIncId()+"";
            	for(String s : this.approvedStateException){
            		if(s.equals(str)){
            			if(mua.getAdxbasedexhangesstatus() == AdxBasedExchangesStates.UPLOADFAIL.getCode()){
            				mua.setAdxbasedexhangesstatus(AdxBasedExchangesStates.APPROVED.getCode());
                			break;
            			}
            		}
            	}
            	
            }
            isMarkedForDeletion=resultSet.getBoolean("advertiser_upload");
            AdvInfoUploadCacheEntity vice = new AdvInfoUploadCacheEntity(mua, lastModified, !isMarkedForDeletion);
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
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, AdvInfoUploadCacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
