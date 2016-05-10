package com.kritter.abstraction.cache.abstractions.examples.site;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Date: 13-06-2013
 * Class:
 */
public class SiteCache extends AbstractDBStatsReloadableQueryableCache<Integer, SiteEntity>
{
    private final Logger logger;
    private final String name;

    public SiteCache(List<Class> secIndexKeyClassList, Logger log, Properties props, DatabaseManager dbMgr, String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, log, props, dbMgr);
        this.logger = log;
        this.name = cacheName;
    }

    @Override
    protected SiteEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        try
        {
            id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String url = resultSet.getString("url");
            if(StringUtils.isEmpty(name) || StringUtils.isEmpty(url))
            {
                addToErrorMap(id, "Site name or url is empty");
                return null;
            }
            String siteGuid = resultSet.getString("guid");
            List<Integer> catList = null;
            if(resultSet.getArray("categories_list") != null)
            {
                Integer[] categoriesList = (Integer[])resultSet.getArray("categories_list").getArray();
                catList = Arrays.asList(categoriesList);
            }
            Long updateTime = resultSet.getTimestamp("update_time").getTime();
            boolean status = true;
            String statusStr = resultSet.getString("status");
            if(!statusStr.equals("activated"))
                status = false;

            // All the above checks of null values and status could potentially move inside the build
            // method of the builder
            // optional are set through setters. Mandatory are in the builder constructor
            return new SiteEntity.SiteEntityBuilder(id, name, url, siteGuid, status, updateTime)
                    .setCatList(catList)
                    .build();
        }
        catch (SQLException sqlExcp)
        {
            addToErrorMap(id, "SQL exception while processing SiteCache entry: " + id);
            logger.error("SQLException thrown while processing SiteCache Entry");
            throw new RefreshException("SQLException thrown while processing SiteCache Entry");
        }
    }

    @Override
    protected void release() throws ProcessingException
    {
        //no data structures held. Nothing to release
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, SiteEntity entity)
    {
        if(className.equals(GuidSecondaryIndex.class))
            return SecondaryIndexBuilder.getGuidIndex(entity);

        if(className.equals(UrlNameSecondaryIndex.class))
            return SecondaryIndexBuilder.getUrlNameIndex(entity);

        return null;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
