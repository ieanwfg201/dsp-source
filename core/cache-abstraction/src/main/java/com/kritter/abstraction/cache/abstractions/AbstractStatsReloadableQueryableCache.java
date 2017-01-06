package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.IEntity;
import com.kritter.abstraction.cache.interfaces.IRefreshable;
import com.kritter.abstraction.cache.utils.StatsDumper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Date: 8-June-2013<br></br>
 * Class: Wrapper abstraction for capturing stats on a Queryable cache<br></br>
 * Caution: This class does not set any timers for the refresh interval. A wrapper for setting the timer would be required
 * in a real implementation.<br></br>
 * @see com.kritter.abstraction.cache.entities.CacheReloadTimerTask Sample Timer task
 */
public abstract class AbstractStatsReloadableQueryableCache<I, E extends IEntity<I>> extends AbstractQueryableCache<I, E> implements IRefreshable
{
    @Getter private long refreshInterval;
    @Getter private Date lastRefreshStartTime = new Date();
    @Getter private Date lastSuccessfulRefreshTime = new Date();
    @Getter private Date lastFailedRefreshTime = new Date();
    @Getter private int noOfRefreshes = 0, noOfFailures = 0, noOfSuccesses = 0;
    private Map<I, String> errorMap = new HashMap<I,String>();
    @Getter Logger logger;

    public AbstractStatsReloadableQueryableCache(List<Class> secIndexKeyClassList, Logger logger, Properties props) throws InitializationException
    {
        super(secIndexKeyClassList, logger);
        this.logger = logger;
        if(props == null)
            throw new InitializationException("Properties object cannot be null");
        String refreshInterval = props.getProperty("refresh_interval");
        // logger null check is not required since it is done in AbstractQueryableCache constructor
        if(StringUtils.isEmpty(refreshInterval))
            throw new InitializationException("refresh_interval key not populated");
        this.refreshInterval = Long.parseLong(refreshInterval);
    }

    @Override
    public void refresh() throws RefreshException
    {
        this.lastRefreshStartTime = new Date();
        ++noOfRefreshes;
        try
        {
            refreshEntities();
            this.lastSuccessfulRefreshTime = new Date();
            ++this.noOfSuccesses;
        }
        catch (RefreshException e)
        {
            this.lastFailedRefreshTime = new Date();
            ++this.noOfFailures;
            throw new RefreshException(e);
        }
    }

    protected void removeFromErrorMap(I entityId)
    {
        this.errorMap.remove(entityId);
    }

    public void addToErrorMap(I entityId, String errorMsg)
    {
        this.remove(entityId);
        this.errorMap.put(entityId, errorMsg);
    }

    @Override
    public int getFailedEntityCount()
    {
        return this.errorMap.size();
    }

    @Override
    public String queryErrorMessage(I entityId)
    {
        String errorMsg = this.errorMap.get(entityId);
        return errorMsg == null ? "" : errorMsg;
    }

    @Override
    public String getErrorMessages()
    {
        JSONObject jsonObj = new JSONObject();
        for(Map.Entry entry : errorMap.entrySet())
        {
            jsonObj.put(entry.getKey(), entry.getValue());
        }

        return jsonObj.toString();
    }

    @Override
    public String getStats()
    {
        return StatsDumper.getStatsJSON(this);
    }

    @Override
    protected void cleanUp() throws ProcessingException
    {
        if(errorMap != null)
        {
            errorMap.clear();
        }
        errorMap = null;

        // let the child develop good sanitary habits of cleaning up after itself
        clean();
    }

    protected abstract void refreshEntities() throws RefreshException;
    protected abstract void clean() throws ProcessingException;
}
