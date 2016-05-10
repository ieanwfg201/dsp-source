package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * Date: 11-06-2013
 * Class:
 */
public class TestFileStatsReloadableQueryableCache
{
    private final static Logger logger = LoggerFactory.getLogger(TestFileStatsReloadableQueryableCache.class);
    private final long sysTime = new Date().getTime(), sysTime2 = sysTime + 2000L, sysTime3 = sysTime2 + 2000L;

    @Before
    public void setUp()
    {
    }

    @Test(expected = InitializationException.class)
    public void testConstructionAll() throws InitializationException
    {
        new SampleFileStatsReloadableQueryableCache(null, null, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionProps() throws InitializationException
    {
        new SampleFileStatsReloadableQueryableCache(null, logger, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionEmptyProps() throws InitializationException
    {
        Properties props = new Properties();
        new SampleFileStatsReloadableQueryableCache(null, logger, props);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionEmptyPropsRefresh() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        new SampleFileStatsReloadableQueryableCache(null, logger, props);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionMissingFile() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        props.put("file_name", "no/file");
        new SampleFileStatsReloadableQueryableCache(null, logger, props);
    }

    @Test
    public void testConstruction() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        String fileName = "src/test/resources/filedata.csv";
        props.put("file_name", fileName);
        new SampleFileStatsReloadableQueryableCache(null, logger, props);
    }

    @Test
    public void testRefreshEntities() throws InitializationException, RefreshException, InterruptedException {
        Properties props = new Properties();
        String refreshInterval = "15";
        props.put("refresh_interval", refreshInterval);
        String fileName = "src/test/resources/filedata.csv";
        props.put("file_name", fileName);

        SampleFileStatsReloadableQueryableCache cache = new SampleFileStatsReloadableQueryableCache(null, logger, props);
        cache.refresh();
        assertEquals(5, cache.getEntityCount());

        cache.remove(1);
        assertEquals(4, cache.getEntityCount());

        cache.refresh();
        assertEquals(4, cache.getEntityCount());

        cache.returnFixed = true;
        Thread.sleep(1000);
        new File(fileName).setLastModified(new Date().getTime());
        cache.refresh();
        cache.returnFixed = false;
        assertEquals(5, cache.getEntityCount());

        cache.addToError = true;
        Thread.sleep(1000);
        new File(fileName).setLastModified(new Date().getTime());
        cache.refresh();
        cache.addToError = false;
        assertEquals(5, cache.getEntityCount());
        assertEquals(1, cache.getFailedEntityCount());

        cache.destroy();
    }
}
