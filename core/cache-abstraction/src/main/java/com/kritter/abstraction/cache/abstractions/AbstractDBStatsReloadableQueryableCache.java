package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Date: 9-June-2013<br></br>
 * Class: Abstraction for a DB based Reloadable Query-able cache<br></br>
 * ICache refreshes maintain the most recent modified time across entities and use that while running the next refresh templatized queryEntities
 * <ul><b>Expectation from the concrete implementation:</b>
 *  <li>refresh_interval and queryEntities should be provided in the Properties configuration under the keys 'refresh_interval' and 'queryEntities'</li>
 *  <li>Query should be templatized with the timestamp key '$last_modified'</li>
 *  <li>Implementation of buildEntity should simply build an entity from the ResultSet and NEVER invoke a resultSet.next()</li>
 *  <li>During a refresh, the entity has to be marked for deletion if the entity's indices have to be removed from the ICache</li>
 *  <li>If a entity data is inconsistent, add its details to the error map and return null from buildEntity</li>
 *  <li>Entities should be versioned on the entity's modified timestamp</li>
 *  <li>The secondary index key list provided should contain the Secondary index class names and not any wrappers. Same would be used while querying</li>
 *  <li>For each secondary index, an index builder should provide a ISecondaryIndexWrapper per entity</li>
 * </ul>
 */
public abstract class AbstractDBStatsReloadableQueryableCache<I, E extends IUpdatableEntity<I>> extends AbstractStatsReloadableQueryableCache<I,E>
{
    private final String query;
    private final DatabaseManager dbManager;
    Timestamp mostRecentEntityTime = new Timestamp(0);
    private Logger logger;
    private Map<Integer,Object> queryParametersMapToSet;

    /**
     * Constructor reads the queryEntities and saves a reference to the DB manager
     * @param secIndexKeyClassList List of class for Secondary indices to be passed to super class
     * @param log Initialized Logger
     * @param props Properties with queryEntities information it
     * @param dbMgr Initialized Database manager
     * @throws InitializationException
     */
    public AbstractDBStatsReloadableQueryableCache(List<Class> secIndexKeyClassList,
                                                   Logger log,
                                                   Properties props,
                                                   DatabaseManager dbMgr) throws InitializationException
    {
        super(secIndexKeyClassList, log, props);

        this.logger = log;
        // Assumption: all DB based updatable caches will have update_time column in their schema for the database table
        query = props.getProperty("query");
        if(StringUtils.isEmpty(query))
            throw new InitializationException("Query for DB caches cannot be null/empty");

        if(dbMgr == null)
            throw new InitializationException("Database Manager cannot be null");
        dbManager = dbMgr;
    }

    /**
     * This constructor is used by caches where query(mysql database) parameters are required to be set.
     * @param secIndexKeyClassList
     * @param log
     * @param props
     * @param dbMgr
     * @param queryParametersMapToSet
     * @throws InitializationException
     */
    public AbstractDBStatsReloadableQueryableCache(List<Class> secIndexKeyClassList,
                                                   Logger log,
                                                   Properties props,
                                                   DatabaseManager dbMgr,
                                                   Map<Integer,Object> queryParametersMapToSet) throws InitializationException
    {
        super(secIndexKeyClassList, log, props);

        this.logger = log;
        // Assumption: all DB based updatable caches will have update_time column in their schema for the database table
        query = props.getProperty("query");
        if (StringUtils.isEmpty(query))
            throw new InitializationException("Query for DB caches cannot be null/empty");

        if (dbMgr == null)
            throw new InitializationException("Database Manager cannot be null");
        dbManager = dbMgr;
        this.queryParametersMapToSet = queryParametersMapToSet;
    }

    /**
     * This method executes the queryEntities fetching the entities modified after the time we saved as the most recent modification time
     * It loops over the ResultSet and expects the concrete child class to build an entity out of it or return null
     * in cases of errors in entity completeness. Error map could be used for saving info on failed/inconsistent entities.
     *
     * Use super.getLogger(), as refreshEntities is called in parent class' constructor, till which time logger
     * instance of this class is not initialized,
     * @throws RefreshException
     */
    @Override
    protected synchronized void refreshEntities() throws RefreshException
    {
        super.getLogger().debug("Inside refreshEntities() of AbstractDBStatsReloadableQueryableCache class. Bean " +
                "name : {}", getName());
        Connection conn = null;
        try
        {
            String refreshQuery = query.replaceAll("\\$last_modified", mostRecentEntityTime.toString());

            super.getLogger().debug("Refresh query is : {}", refreshQuery);

            conn = dbManager.getConnectionFromPool();

            /*invoke method where query parameters map might have been modified.*/
            this.queryParametersMapToSet = modifyQueryParametersMap();

            ResultSet resultSet;

            if(null != queryParametersMapToSet && queryParametersMapToSet.size() > 0)
                resultSet = DBExecutionUtils.executeQuery(conn, refreshQuery,queryParametersMapToSet);
            else
                resultSet = DBExecutionUtils.executeQuery(conn,refreshQuery);

            logger.debug("Result set fetched is null? : {}",(null==resultSet));

            while(resultSet.next())
            {
                // Make the child class return the entity representing the resultset row
                E entity = buildEntity(resultSet);

                // if the derived class added this to error map
                if(entity == null)
                    continue;

                // Check if the entity is updated for addition or removal
                if(!entity.isMarkedForDeletion()) {
                    this.add(entity);
                    logger.debug("Inside {}. Added entity id : {} to primary index.", getName(), entity.getId());
                }
                else {
                    this.remove(entity.getId());
                    logger.debug("Inside {}. Removed entity id : {} from primary index.", getName(), entity.getId());
                }

                // Always remove from error Map
                // Expectation is that the derived will take care of additions to the error map itself
                // If the entity has reached here, it is a valid entity even if it is marked for deletion
                // In the derived class, Deletion case should be handled first before checking for validity of entity
                // Otherwise, an entity if marked for deactivation got corrupt data, it won't get removed from the cache
                this.removeFromErrorMap(entity.getId());

                // save the most recently modified entity time for subsequent updates
                Timestamp entityTimestamp = new Timestamp(entity.getModificationTime());
                if(mostRecentEntityTime.before(entityTimestamp))
                    mostRecentEntityTime = entityTimestamp;
            }
            logger.debug("Inside {}. Entry count in primary index = {}.", getName(), primaryIndex.size());
        }
        catch (SQLException e)
        {
            super.getLogger().error("SQLException thrown while running DB based refresh in cache: {}", this.getName());
            super.getLogger().error("SQLException:", e);
            throw new RefreshException("SQLException thrown while running DB based refresh in cache: " + this.getName(), e);
        }
        catch (RuntimeException e)
        {
            super.getLogger().error("RuntimeException thrown while running DB based refresh in cache: {}", this.getName());
            super.getLogger().error("RuntimeException:", e);
            throw new RefreshException("RuntimeException thrown while running DB based refresh in cache: " + this.getName(), e);
        }
        finally
        {
            DBExecutionUtils.closeConnection(conn);
        }
    }

    @Override
    protected void clean() throws ProcessingException
    {
        // Nothing to clean up but provides the child an option to clean itself
        release();
    }

    protected abstract E buildEntity(ResultSet resultSet) throws RefreshException;
    protected abstract void release() throws ProcessingException;

    /*Method to modify parameters for query. This method is invoked at the time of timer reload,
    * if at all query parameters are modified then new query is executed which would give new
    * results.*/
    public Map<Integer,Object> modifyQueryParametersMap()
    {
        return this.queryParametersMapToSet;
    }
}
