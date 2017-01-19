package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;


/**
 * Date: 10/6/13
 * Class:
 */
public class TestStatsReloadableQueryableCache
{
    private static Logger logger = LogManager.getLogger(TestStatsReloadableQueryableCache.class);

    @Test(expected = InitializationException.class)
    public void testConstruction() throws InitializationException
    {
        new SampleStatsReloadableQueryableCache(null, null, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionLogger() throws InitializationException
    {
        new SampleStatsReloadableQueryableCache(null, null, new Properties());
    }

    @Test(expected = InitializationException.class)
    public void testConstructionProperties() throws InitializationException
    {
        new SampleStatsReloadableQueryableCache(null, logger, null);
    }

    @Test(expected=RefreshException.class)
    public void testRefresh() throws InitializationException, InterruptedException, RefreshException {
        Long refreshInterval = 600L;
        Properties properties = new Properties();
        properties.put("refresh_interval", refreshInterval.toString());
        SampleStatsReloadableQueryableCache cache = new SampleStatsReloadableQueryableCache(null, logger, properties);

        assertEquals(true, cache.getConstructionTime() != null);
        cache.refresh();
        cache.refresh();

        assertEquals(2, cache.getTestCounter());
        assertEquals(true, refreshInterval.equals(cache.getRefreshInterval()));
        assertEquals(0, cache.getEntityCount());
        assertEquals(cache.getTestCounter(), cache.getNoOfRefreshes());
        assertEquals(0, cache.getNoOfFailures());
        assertEquals(cache.getTestCounter(), cache.getNoOfSuccesses());

        Date oldSuccRefTime = cache.getLastSuccessfulRefreshTime();
        Date oldStartTime = cache.getLastRefreshStartTime();
        Date oldFailedRefTime = cache.getLastFailedRefreshTime();
        Thread.sleep(1000);
        cache.refresh();
        assertEquals(true, cache.getLastRefreshStartTime().after(oldStartTime));
        assertEquals(true, cache.getLastSuccessfulRefreshTime().after(oldSuccRefTime));
        assertEquals(true, cache.getLastFailedRefreshTime().equals(oldFailedRefTime));

        cache.throwException = true;
        oldFailedRefTime = cache.getLastFailedRefreshTime();
        cache.refresh();
        assertEquals(cache.getNoOfFailures(), 1);
        cache.throwException = false;
        assertEquals(true, cache.getLastFailedRefreshTime().after(oldFailedRefTime));

        SampleQueryableEntity entity = new SampleQueryableEntity(1, 0L, null, 10);
        cache.add(entity);
        assertEquals(true, cache.getEntityCount() == 1);

        SampleQueryableEntity fetchedEntity = cache.query(1);
        assertEquals(true, entity.equals(fetchedEntity));

        entity = new SampleQueryableEntity(1, 0L, null, 12);
        cache.add(entity);
        assertEquals(1, cache.getEntityCount());
        fetchedEntity = cache.query(1);
        assertEquals(entity, fetchedEntity);

        cache.remove(1);
        assertEquals(0, cache.getEntityCount());
        cache.destroy();

        properties.put("is_version_maintained", "false");
        cache = new SampleStatsReloadableQueryableCache(null, logger, properties);

        cache.refresh();
        assertEquals(1, cache.getTestCounter());
        assertEquals(true, cache.getConstructionTime() != null);
        assertEquals(true, cache.getRefreshInterval() == refreshInterval);
        assertEquals(0, cache.getEntityCount());

        entity = new SampleQueryableEntity(1, 0L, null, 10);
        cache.add(entity);
        assertEquals(1, cache.getEntityCount());

        fetchedEntity = cache.query(1);
        assertEquals(entity, fetchedEntity);

        entity = new SampleQueryableEntity(1, 0L, null, 12);
        cache.add(entity); // this add since id is same and version is not upgraded but the cache does not take the responsibility
        assertEquals(1, cache.getEntityCount());
        fetchedEntity = cache.query(1);
        assertEquals(entity, fetchedEntity);

        cache.remove(1);
        assertEquals(0, cache.getEntityCount());
    }
}
