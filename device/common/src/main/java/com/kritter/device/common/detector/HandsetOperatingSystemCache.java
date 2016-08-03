package com.kritter.device.common.detector;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.device.common.entity.HandsetOperatingSystemData;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class HandsetOperatingSystemCache
        extends AbstractDBStatsReloadableQueryableCache<String, HandsetOperatingSystemData> {
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    @Getter
    private final String name;

    public HandsetOperatingSystemCache(List<Class> secIndexKeyClassList, Properties props,
                                       DatabaseManager dbMgr, String cacheName) throws InitializationException {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected HandsetOperatingSystemData buildEntity(ResultSet rs) throws RefreshException {
        try {
            Integer osId = (Integer) rs.getObject("os_id");
            String osName = rs.getString("os_name");
            String[] osVersions = ResultSetHelper.getResultSetStringArray(rs, "os_versions");
            String modifiedBy = rs.getString("modified_by");
            Timestamp modifiedOn = rs.getTimestamp("modified_on");

            HandsetOperatingSystemData handsetOperatingSystemData =
                    new HandsetOperatingSystemData(osId, osName, osVersions, modifiedBy, modifiedOn);
            //logger.info("Handset os data to populate : {}", handsetOperatingSystemData);
            return handsetOperatingSystemData;
        } catch (Exception e) {
            throw new RefreshException("Inside HandsetOperatingSystemCache,Exception in getting manufacturer data",e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, HandsetOperatingSystemData entity) {
        return null;
    }
}
