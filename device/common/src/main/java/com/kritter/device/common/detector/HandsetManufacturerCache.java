package com.kritter.device.common.detector;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.device.common.entity.HandsetManufacturerData;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps handset manufacturer data from database into memory.
 */

public class HandsetManufacturerCache extends AbstractDBStatsReloadableQueryableCache<String,HandsetManufacturerData>
{

    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private final String name;

    public HandsetManufacturerCache(List<Class> secIndexKeyClassList, Properties props,
                                    DatabaseManager dbMgr, String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected HandsetManufacturerData buildEntity(ResultSet rs) throws RefreshException {
        try {
            Integer manufacturerId = (Integer) rs.getObject("manufacturer_id");
            String manufacturerName = rs.getString("manufacturer_name");
            String modifiedBy = rs.getString("modified_by");
            Timestamp modifiedOn = rs.getTimestamp("modified_on");

            HandsetManufacturerData handsetManufacturerData =
                    new HandsetManufacturerData(manufacturerId, manufacturerName, modifiedBy, modifiedOn);
            //logger.info("Handset manufacturer data to populate : {}", handsetManufacturerData);
            return handsetManufacturerData;
        } catch (Exception e) {
            throw new RefreshException("Inside HandsetManufacturerCache,Exception in getting manufacturer data",e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, HandsetManufacturerData entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
