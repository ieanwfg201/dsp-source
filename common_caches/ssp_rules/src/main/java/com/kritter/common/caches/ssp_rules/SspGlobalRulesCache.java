package com.kritter.common.caches.ssp_rules;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.ssp_rules.entity.SspGlobalRulesEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**

 */
public class SspGlobalRulesCache extends AbstractDBStatsReloadableQueryableCache<Integer, SspGlobalRulesEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private final String name;

    public SspGlobalRulesCache(List<Class> secIndexKeyClassList,
                                     Properties props,
                                     DatabaseManager dbMgr,
                                     String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
                                                       SspGlobalRulesEntity entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    protected SspGlobalRulesEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        try
        {
            Integer id = resultSet.getInt("id");
            String rule_def = resultSet.getString("rule_def");
            Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

            return new SspGlobalRulesEntity(id,rule_def,lastModifiedOn);

        }
        catch (SQLException e)
        {
            logger.error("SQLException thrown while processing SspGlobalRulesEntity Entry",e);
            throw new RefreshException("SQLException thrown while processing SspGlobalRulesEntity Entry",e);
        }
        catch (IOException e)
        {
            logger.error("IOException thrown while processing SspGlobalRulesEntity Entry",e);
            throw new RefreshException("IOException thrown while processing SspGlobalRulesEntity Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
    }
}
