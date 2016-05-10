package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.CountryUserInterfaceId;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * This cache keeps country user interface id information against secondary indexes as
 * data source ids received from different geo data sources.
 */
public class CountryUserInterfaceIdCache extends AbstractDBStatsReloadableQueryableCache<Integer,CountryUserInterfaceId>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    @Getter
    private final String name;

    public CountryUserInterfaceIdCache(List<Class> secIndexKeyClassList,
                                       Properties props,
                                       DatabaseManager dbMgr,
                                       String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected CountryUserInterfaceId buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;

        try
        {
            id = resultSet.getInt("id");
            String countryCode = resultSet.getString("country_code");
            Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(resultSet, "entity_id_set");
            Timestamp modifiedOn = resultSet.getTimestamp("modified_on");

            Set<Integer> countryIdSetForAllDataSources = ( null != entityIdSet ?
                new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                new HashSet<Integer>());

            return new CountryUserInterfaceId(countryCode,id,countryIdSetForAllDataSources,modifiedOn);
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CountryUserInterfaceIdCache entry: " + id);
            logger.error("Exception thrown while processing CountryUserInterfaceIdCache Entry",e);
            throw new RefreshException("Exception thrown while processing CountryUserInterfaceIdCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, CountryUserInterfaceId entity)
    {
        return CountryUserInterfaceId.getSecondaryIndexForClass(className,entity);
    }
}