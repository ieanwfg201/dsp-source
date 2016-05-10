package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * Date: 10/6/13
 * Class:
 */
public class TestStatsReloadableCache
{
    private static Logger logger = LoggerFactory.getLogger(TestStatsReloadableCache.class);

    @Test(expected = InitializationException.class)
    public void testConstruction() throws InitializationException
    {
        new SampleStatsReloadableCache(null, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionLogger() throws InitializationException
    {
        new SampleStatsReloadableCache(null, new Properties());
    }

    @Test(expected = InitializationException.class)
    public void testConstructionProperties() throws InitializationException
    {
        new SampleStatsReloadableCache(logger, null);
    }

    @Test
    public void testRefresh() throws InitializationException, InterruptedException, RefreshException
    {
        Properties properties = new Properties();
        properties.put("refresh_interval", "2");
        SampleStatsReloadableCache cache = new SampleStatsReloadableCache(logger, properties);
        assertEquals(true, cache.getConstructionTime() != null);

        Thread.sleep(1000);
        Date currDate = new Date();
        Thread.sleep(1000);
        cache.refresh();
        assertEquals(true, cache.getConstructionTime().before(currDate));
        assertEquals(true, cache.getLastRefreshStartTime().after(currDate));
        assertEquals(true, cache.getLastSuccessfulRefreshTime().after(currDate));
        assertEquals(true, cache.getLastFailedRefreshTime().before(currDate));
        assertEquals(true, cache.getCounter() == 1);
        assertEquals(true, cache.getRefreshInterval() == 2000);
        assertEquals(true, cache.getNoOfSuccesses() == 1);
        assertEquals(true, cache.getNoOfRefreshes() == 1);


        cache.throwException = true;
        Thread.sleep(1000);
        currDate = new Date();
        Thread.sleep(1000);
        cache.refresh();
        cache.throwException = false;
        assertEquals(true, cache.getConstructionTime().before(currDate));
        assertEquals(true, cache.getCounter() == 1);
        assertEquals(true, cache.getNoOfSuccesses() == 1);
        assertEquals(true, cache.getNoOfRefreshes() == 2);
        assertEquals(true, cache.getNoOfFailures() == 1);
        assertEquals(true, cache.getLastRefreshStartTime().after(currDate));
        assertEquals(true, cache.getLastFailedRefreshTime().after(currDate));
        assertEquals(true, cache.getLastSuccessfulRefreshTime().before(currDate));
        cache.destroy();
    }
}
