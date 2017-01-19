package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.CityUserInterfaceId;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class keeps city user interface information, required for targeting matching etc.
 */
public class CityUserInterfaceIdCache extends AbstractDBStatsReloadableQueryableCache<Integer,CityUserInterfaceId>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    @Getter
    private final String name;

    public CityUserInterfaceIdCache(List<Class> secIndexKeyClassList,
                                     Properties props,
                                     DatabaseManager dbMgr,
                                     String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected CityUserInterfaceId buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;

        try
        {
            id = resultSet.getInt("id");
            Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(resultSet, "entity_id_set");
            Timestamp modifiedOn = resultSet.getTimestamp("modified_on");

            Set<Integer> cityIdSetForAllDataSources =  ( null != entityIdSet ?
                                                         new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                                                         new HashSet<Integer>()
                                                       );

            return new CityUserInterfaceId(id,cityIdSetForAllDataSources,modifiedOn);
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CityUserInterfaceIdCache entry: " + id);
            logger.error("Exception thrown while processing CityUserInterfaceIdCache Entry",e);
            throw new RefreshException("Exception thrown while processing CityUserInterfaceIdCache Entry", e);
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
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, CityUserInterfaceId entity)
    {
        return CityUserInterfaceId.getSecondaryIndexForClass(className,entity);
    }
}