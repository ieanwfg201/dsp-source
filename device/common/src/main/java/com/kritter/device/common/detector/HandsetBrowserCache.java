package com.kritter.device.common.detector;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.device.common.entity.HandsetBrowserData;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class HandsetBrowserCache extends AbstractDBStatsReloadableQueryableCache<String, HandsetBrowserData> {
    private static Logger logger = LogManager.getLogger("cache.logger");
    @Getter
    private final String name;

    public HandsetBrowserCache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName) throws InitializationException {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected HandsetBrowserData buildEntity(ResultSet rs) throws RefreshException {
        try {
            Integer browserId = (Integer) rs.getObject("browser_id");
            String browserName = rs.getString("browser_name");
            String[] browserVersions = ResultSetHelper.getResultSetStringArray(rs, "browser_versions");
            String modifiedBy = rs.getString("modified_by");
            Timestamp modifiedOn = rs.getTimestamp("modified_on");

            HandsetBrowserData handsetBrowserData =
                    new HandsetBrowserData(browserId, browserName, browserVersions, modifiedBy, modifiedOn);
            //logger.info("Handset browser data to populate : {}", handsetBrowserData);
            return handsetBrowserData;
        } catch (Exception e) {
            throw new RefreshException("Inside HandsetBrowserCache,Exception in getting manufacturer data",e);
        }
    }


    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, HandsetBrowserData entity) {
        return null;
    }
}
