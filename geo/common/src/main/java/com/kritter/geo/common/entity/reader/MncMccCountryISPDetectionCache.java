package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.CountryIspUiDataUsingMccMnc;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class takes input as mcc,mnc values and use them
 * to find country_ui_id and isp_ui_id, which in turn
 * would be used to fetch ads inside AdTargetingMatcher.
 */
public class MncMccCountryISPDetectionCache extends
                                            AbstractDBStatsReloadableQueryableCache<String,CountryIspUiDataUsingMccMnc>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public MncMccCountryISPDetectionCache(
                                          List<Class> secIndexKeyClassList,
                                          Properties props,
                                          DatabaseManager dbMgr,
                                          String cacheName
                                         ) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }


    @Override
    protected CountryIspUiDataUsingMccMnc buildEntity(ResultSet resultSet) throws RefreshException
    {
        String id = null;

        try
        {
            String mnc = resultSet.getString("mnc");
            String mcc = resultSet.getString("mcc");
            Integer countryUiId = resultSet.getInt("country_ui_id");
            Integer ispUiId = resultSet.getInt("isp_ui_id");
            Timestamp updateTime = resultSet.getTimestamp("last_modified");

            if(StringUtils.isEmpty(mnc) || StringUtils.isEmpty(mcc) ||
               null == countryUiId || null == ispUiId ||
               null == updateTime)
                return null;

            id= CountryIspUiDataUsingMccMnc.prepareKeyForEntityLookup(mcc,mnc);
            return new CountryIspUiDataUsingMccMnc(mnc,mcc,countryUiId,ispUiId,updateTime);
        }
        catch (SQLException e)
        {
            if(null != id)
                addToErrorMap(id,"SQL exception while processing MncMccCountryISPDetectionCache entry: " + id);

            logger.error("SQLException thrown while processing MncMccCountryISPDetectionCache Entry",e);
            throw new RefreshException("SQLException thrown while processing MncMccCountryISPDetectionCache Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {

    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, CountryIspUiDataUsingMccMnc entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return name;
    }
}