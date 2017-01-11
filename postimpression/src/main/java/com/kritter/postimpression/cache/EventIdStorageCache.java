package com.kritter.postimpression.cache;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class stores event id of a particular type such as
 * click, csc, conversion or win , time to live for an id
 * is equal to the expiry time of the event.
 * In a machine getting 100 million impressions in a day,
 * the time to live for impression being 5 minutes will have
 * to hold around 15MB of data for every 5 minutes ,
 * for clicks, win and conversions this size would be even less.
 * The cleanup job to be kept at more than 5 minutes like
 * 7 minutes etc.
 * For clicks since the data has to be for 3 hours, the data
 * size would be around 6 MB assuming 1 million clicks on the
 * machine.
 */
public class EventIdStorageCache
{
    private static Logger logger = LogManager.getLogger("cache.logger");

    //Keep a map to store event id
    private Map<String,Long> eventIdStorage;
    private long eventIdExpiryTime;

    private Timer timer;
    private TimerTask timerTask;

    public EventIdStorageCache(
                               long eventIdExpiryTime,
                               long cleanupFrequency
                              )
    {
        this.eventIdExpiryTime = eventIdExpiryTime;
        this.eventIdStorage = new ConcurrentHashMap<String, Long>();
        this.timer = new Timer();
        this.timerTask = new EventIdCleanUpTimerTask();
        this.timer.schedule(this.timerTask,cleanupFrequency,cleanupFrequency);
    }

    public boolean doesEventIdExistInStorage(String eventId)
    {
        if(null == eventId)
            return false;

        return this.eventIdStorage.containsKey(eventId);
    }

    public void addEventIdToStorage(String eventId)
    {
        this.eventIdStorage.put(eventId,System.currentTimeMillis());
    }

    private class EventIdCleanUpTimerTask extends TimerTask
    {
        private Logger cacheLogger = LogManager.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                cleanUpEventIdStorageForExpiredEvents();
            }
            catch (RefreshException re)
            {
                cacheLogger.error("RefreshException while cleaning up event id data inside EventIdCleanUpTimerTask in the class EventIdStorageCache",re);
            }
        }
    }

    private void cleanUpEventIdStorageForExpiredEvents() throws RefreshException
    {
        //collect event ids to be removed.
        Set<String> eventIdForRemovalSet = new HashSet<String>();

        for(String eventId : this.eventIdStorage.keySet())
        {
            Long eventTime = this.eventIdStorage.get(eventId);

            if(null == eventTime || (System.currentTimeMillis() - eventTime.longValue()) >= this.eventIdExpiryTime)
                eventIdForRemovalSet.add(eventId);
        }

        //remove event ids which are found to be eligible for removal.
        for(String eventId : eventIdForRemovalSet)
        {
            this.eventIdStorage.remove(eventId);
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.eventIdStorage = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}


