package com.kritter.postimpression.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.postimpression.cache.entity.UrlAliasEntity;
import com.kritter.postimpression.cache.index.UrlAliasSecondaryIndex;
import com.kritter.postimpression.cache.indexbuilder.UrlAliasSecondaryIndexBuilder;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class represents cache for alias url, the data is read from url_alias table.
 */

public class AliasUrlCache extends
        AbstractDBStatsReloadableQueryableCache<Integer, UrlAliasEntity> {

    private static Logger logger = LoggerFactory.getLogger("cache.logger");

    private final String name;

    public AliasUrlCache(List<Class> secIndexKeyClassList,
                         Properties props, DatabaseManager dbMgr, String cacheName) throws InitializationException{

        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
        logger.debug("AliasUrlCache initialized successfully!!!");

    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class aClass, UrlAliasEntity urlAliasEntity) {
        if(aClass.equals(UrlAliasSecondaryIndex.class))
            return UrlAliasSecondaryIndexBuilder.getUrlAliasSecondaryIndex(urlAliasEntity);

        return null;
    }

    @Override
    protected UrlAliasEntity buildEntity(ResultSet resultSet) throws RefreshException {

        logger.debug("Inside build entity method of AliasUrlCache");

        Integer id = null;
        try{

            id = resultSet.getInt("id");
            String actualUrl = resultSet.getString("actual_url");
            String aliasUrl = resultSet.getString("alias_url");
            Timestamp modifiedOn = resultSet.getTimestamp("last_modified");
            Timestamp createdOn = resultSet.getTimestamp("created_on");
            boolean status = resultSet.getBoolean("status");

            return new UrlAliasEntity.UrlAliasBuilder(id,actualUrl,aliasUrl,!status,modifiedOn.getTime(),createdOn)
                    .build();

        }catch(Exception e){

            addToErrorMap(id, "Exception while processing UrlAliasEntity in AliasUrlCache " + id );
            logger.error("Exception while processing UrlAliasEntity in AliasUrlCache {}", id);
            throw new RefreshException("Exception while processing UrlAliasEntity in AliasUrlCache ", e);

        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected void release() throws ProcessingException {

    }

}
