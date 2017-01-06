package com.kritter.device.common.detector;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.device.common.entity.HandsetModelData;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class is used to keep model data in-memory.
 * Used in ad-exchange handset identification given
 * some model name and manufacturer id.
 * The model name in lowercase along with manufacturer id is the key for storage.
 */
public class HandsetModelCache extends AbstractDBStatsReloadableQueryableCache<String,HandsetModelData>
{

    private static Logger logger = LogManager.getLogger("cache.logger");
    private final String name;

    public HandsetModelCache(List<Class> secIndexKeyClassList, Properties props,
                             DatabaseManager dbMgr, String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected HandsetModelData buildEntity(ResultSet rs) throws RefreshException
    {
        try {
            Integer modelId = (Integer) rs.getObject("model_id");
            Integer manufacturerId = (Integer) rs.getObject("manufacturer_id");
            String modelName = rs.getString("model_name");
            String modifiedBy = rs.getString("modified_by");
            Timestamp modifiedOn = rs.getTimestamp("modified_on");

            HandsetModelData handsetModelData = new HandsetModelData(modelId, manufacturerId,
                                        modelName, modifiedBy,modifiedOn);
            //logger.info("Handset model data to populate : {}", handsetModelData);
            return handsetModelData;
        } catch (Exception e) {
            throw new RefreshException("Inside HandsetModelCache,Exception in getting handset model data",e);
        }
    }

    @Override
    protected void release() throws ProcessingException {

    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, HandsetModelData entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
