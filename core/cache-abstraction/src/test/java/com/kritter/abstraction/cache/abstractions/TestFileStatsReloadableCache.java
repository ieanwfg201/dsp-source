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
public class TestFileStatsReloadableCache
{
    private final static Logger logger = LoggerFactory.getLogger(TestFileStatsReloadableCache.class);
    private final long sysTime = new Date().getTime(), sysTime2 = sysTime + 2000L, sysTime3 = sysTime2 + 2000L;

    @Before
    public void setUp()
    {
    }

    @Test(expected = InitializationException.class)
    public void testConstructionAll() throws InitializationException
    {
        new SampleFileStatsReloadableCache(null, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionProps() throws InitializationException
    {
        new SampleFileStatsReloadableCache(logger, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionEmptyProps() throws InitializationException
    {
        Properties props = new Properties();
        new SampleFileStatsReloadableCache(logger, props);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionEmptyPropsRefresh() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        new SampleFileStatsReloadableCache(logger, props);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionMissingFile() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        props.put("file_name", "no/file");
        new SampleFileStatsReloadableCache(logger, props);
    }

    @Test
    public void testConstruction() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        props.put("file_name", "src/test/resources/filedata1.csv");
        new SampleFileStatsReloadableCache(logger, props);
    }

    @Test
    public void testRefreshEntities() throws InitializationException, RefreshException, InterruptedException {
        Properties props = new Properties();
        String refreshInterval = "15";
        props.put("refresh_interval", refreshInterval);
        String fileName = "src/test/resources/filedata1.csv";
        props.put("file_name", fileName);

        SampleFileStatsReloadableCache cache = new SampleFileStatsReloadableCache(logger, props);
        cache.refresh();
        assertEquals(1, cache.getCounter());

        cache.refresh();
        assertEquals(1, cache.getCounter()); //won't increment since no modified on file
        assertEquals(1, cache.getEntityCount()); // hardcoded in stats

        Thread.sleep(1000);
        new File(fileName).setLastModified(new Date().getTime());
        cache.refresh();
        assertEquals(2, cache.getCounter()); //will increment since file was modified
        assertEquals(1, cache.getEntityCount()); // hardcoded in stats

        cache.destroy();
    }
}
