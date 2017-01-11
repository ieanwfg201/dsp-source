package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.IRefreshable;
import com.kritter.abstraction.cache.utils.StatsDumper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Properties;

/**
 * Date: 9-June-2013<br></br>
 * Class: Wrapper abstraction around reloadable caches to capture stats
 * <ul><b>Expectations from the concrete implementation:</b>
 *  <li> refreshCache method will be implemented and scheduling will be taken care of</li>
 *  <li> There is no notion of entities</li>
 * </ul>
 * <ul> <b>Note:</b>
 *  <li>This will be useful in cases where external APIs are provided where data is refreshed periodically
 * but no entities are exposed</li>
 * </ul>
 * @see com.kritter.abstraction.cache.entities.CacheReloadTimerTask Sample Timer task
 * @see AbstractStatsReloadableQueryableCache Entity aware reloadable cache
 */
@Getter
public abstract class AbstractStatsReloadableCache implements IRefreshable
{
    private Date lastRefreshStartTime = new Date();
    private Date lastSuccessfulRefreshTime = new Date();
    private Date lastFailedRefreshTime = new Date();
    private int noOfRefreshes = 0, noOfFailures = 0, noOfSuccesses = 0;
    private long refreshInterval;
    private Date constructionTime;

    // stats that are defaulted because they do not apply to the context of AbstractStatsReloadableCache
    private int failedEntityCount = 0;
    private int entityCount = 1;
    private String secondaryIndexCount = "{}";
    private String errorMessages = "{}";

    public AbstractStatsReloadableCache(Logger log, Properties props) throws InitializationException
    {
        if(log == null || props == null || StringUtils.isEmpty(props.getProperty("refresh_interval")))
            throw new InitializationException("Logger or Properties object cannot be null. \"refresh_interval\" info should be provided");
        this.refreshInterval = Long.parseLong(props.getProperty("refresh_interval"));
        constructionTime = new Date();
    }

    @Override
    public void refresh() throws RefreshException
    {
        this.lastRefreshStartTime = new Date();
        noOfRefreshes++;
        try
        {
            refreshCache();
            this.lastSuccessfulRefreshTime = new Date();
            this.noOfSuccesses++;
        }
        catch (RefreshException e)
        {
            this.lastFailedRefreshTime = new Date();
            this.noOfFailures++;
        }
    }

    @Override
    public String getStats()
    {
        return StatsDumper.getStatsJSON(this);
    }

    @Override
    public void destroy()
    {
        try
        {
            cleanUp();
        }
        catch (ProcessingException procExcp)
        {
            throw new RuntimeException("Cleaning up resources failed in ICache: " + this.getName(), procExcp);
        }
    }

    protected abstract void refreshCache() throws RefreshException;
    protected abstract void cleanUp() throws ProcessingException;
}
