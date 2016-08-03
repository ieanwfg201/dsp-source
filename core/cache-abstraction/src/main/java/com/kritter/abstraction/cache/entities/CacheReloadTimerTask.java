package com.kritter.abstraction.cache.entities;

import com.kritter.abstraction.cache.interfaces.IRefreshable;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.slf4j.Logger;

import java.util.TimerTask;

/**
 * Date: 11-June-2013<br></br>
 * Class: Takes a handle to the refresher and calls the refresh at the scheduled interval
 */
public class CacheReloadTimerTask extends TimerTask
{
    private Logger logger;
    private IRefreshable refresher;
    private String cacheName;

    public CacheReloadTimerTask(Logger log, IRefreshable IRefreshable, String name)
    {
        logger = log;
        refresher = IRefreshable;
        cacheName = name;
    }

    @Override
    public void run()
    {
        try
        {
            refresher.refresh();
        }
        catch(RefreshException refreshExcp)
        {
            logger.error("ICache Refresh failed for cache: {}", cacheName);
        }
    }
}
