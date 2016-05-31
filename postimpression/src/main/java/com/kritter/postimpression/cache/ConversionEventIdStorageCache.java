package com.kritter.postimpression.cache;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class is to store conversion event ids only.
 * This has blocking read to check if conversion exists already,
 * not to be used for other events, as blocking read will hamper
 * latency however in case of conversion events are less.
 */
public class ConversionEventIdStorageCache
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");

    //Keep a map to store event id
    private Map<String,Long> eventIdStorage;
    private long eventIdExpiryTime;

    private Timer timer;
    private TimerTask timerTask;

    public ConversionEventIdStorageCache (
                                          long eventIdExpiryTime,
                                          long cleanupFrequency
                                         )
    {
        this.eventIdExpiryTime = eventIdExpiryTime;
        this.eventIdStorage = new Hashtable<String, Long>();
        this.timer = new Timer();
        this.timerTask = new EventIdCleanUpTimerTask();
        this.timer.schedule(this.timerTask,cleanupFrequency,cleanupFrequency);
    }

    public boolean doesEventIdExistInStorage(String eventId)
    {
        if(null == eventId)
            return false;

        return (null != this.eventIdStorage.get(eventId));
    }

    public void addEventIdToStorage(String eventId)
    {
        if(null == eventId)
            return;

        this.eventIdStorage.put(eventId, System.currentTimeMillis());
    }

    private class EventIdCleanUpTimerTask extends TimerTask
    {
        private Logger cacheLogger = LoggerFactory.getLogger("cache.logger");

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


