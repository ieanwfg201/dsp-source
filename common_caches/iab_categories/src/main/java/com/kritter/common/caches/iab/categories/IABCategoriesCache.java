package com.kritter.common.caches.iab.categories;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.iab.categories.entity.IABCategoryEntity;
import com.kritter.common.caches.iab.indexbuilder.IABCategoryEntitySecondaryIndexBuilder;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps iab category data into memory for lookup by exchange bid requests.
 */
public class IABCategoriesCache extends AbstractDBStatsReloadableQueryableCache<String, IABCategoryEntity>
{

    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private final String name;

    public IABCategoriesCache(List<Class> secIndexKeyClassList,
                              Properties props,
                              DatabaseManager dbMgr,
                              String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected IABCategoryEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Short categoryId = null;

        try
        {
            categoryId = resultSet.getShort("id");
            String code = resultSet.getString("code");
            String value = resultSet.getString("value");
            Timestamp lastModified = resultSet.getTimestamp("last_modified");

            return new IABCategoryEntity(categoryId,code,value,lastModified);
        }
        catch (SQLException sqle)
        {
            addToErrorMap(String.valueOf(categoryId), "SQL exception while processing IABCategoriesCache entry: " +
                    String.valueOf(categoryId));
            logger.error("SQLException thrown while processing IABCategoriesCache Entry",sqle);
            throw new RefreshException("SQLException thrown while processing IABCategoriesCache Entry",sqle);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {

    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, IABCategoryEntity entity)
    {
        return IABCategoryEntitySecondaryIndexBuilder.getIndex(className,entity);
    }

    @Override
    public String getName()
    {
        return name;
    }
}
