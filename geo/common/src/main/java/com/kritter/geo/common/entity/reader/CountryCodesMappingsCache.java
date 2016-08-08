package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.CountryCodesEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This class loads inmemory data for country code mappings for two letter codes
 * and three letter codes.
 */
public class CountryCodesMappingsCache extends AbstractDBStatsReloadableQueryableCache<String,CountryCodesEntity>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");

    @Getter
    private final String name;

    public CountryCodesMappingsCache(List<Class> secIndexKeyClassList, Properties props,
                                     DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }


    @Override
    protected CountryCodesEntity buildEntity(ResultSet resultSet) throws RefreshException {

        String id = null;

        try{

            id = resultSet.getString("country_code_two");
            String countryCodeThreeLetters = resultSet.getString("country_code_three");

            Long updateTime = resultSet.getTimestamp("last_modified").getTime();

            return new CountryCodesEntity(id,countryCodeThreeLetters,updateTime,false);
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CountryCodesMappingsCache entry: " + id);
            logger.error("Exception thrown while processing CountryCodesMappingsCache Entry",e);
            throw new RefreshException("Exception thrown while processing CountryCodesMappingsCache Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException {

    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, CountryCodesEntity entity)
    {
        return CountryCodesEntity.getCountryTwoLetterCodeSecondaryIndex(entity);
    }
}
