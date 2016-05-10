package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.*;

/**
 * Date: 11-06-2013
 * Class:
 */
public class TestDBStatsReloadableQueryableCache
{
    private final static Logger logger = LoggerFactory.getLogger(TestDBStatsReloadableQueryableCache.class);
    private DatabaseManager mockMgr;
    private Connection mockConn;
    private PreparedStatement mockPrepStmt;
    private ResultSet mockResultSet;
    private final String query = "select monkey from animals where last_modified > '\\$last_modified'";
    private final String refreshInterval = "15";
    private final long sysTime = new Date().getTime(), sysTime2 = sysTime + 2000L, sysTime3 = sysTime2 + 2000L;

    public static List<SampleQueryableUpdatableEntity> list;

    @Before
    public void setUp() throws InitializationException, SQLException, RefreshException
    {
        mockMgr = createMock(DatabaseManager.class);
        mockConn = createMock(Connection.class);
        mockPrepStmt = createMock(PreparedStatement.class);
        mockResultSet = createMock(ResultSet.class);
        expect(mockMgr.getConnectionFromPool()).andReturn(mockConn).anyTimes();
        expect(mockConn.prepareStatement(query.replaceAll("\\$last_modified", new Timestamp(0).toString()), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)).andReturn(mockPrepStmt).times(1);
        expect(mockConn.prepareStatement(query.replaceAll("\\$last_modified", new Timestamp(sysTime).toString()), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)).andReturn(mockPrepStmt).times(1);
        expect(mockConn.prepareStatement(query.replaceAll("\\$last_modified", new Timestamp(sysTime2).toString()), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)).andReturn(mockPrepStmt).times(2);
        expect(mockConn.prepareStatement(query.replaceAll("\\$last_modified", new Timestamp(sysTime3).toString()), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)).andReturn(mockPrepStmt).times(2);
        mockConn.close();
        EasyMock.expectLastCall().times(5);
        expect(mockPrepStmt.executeQuery()).andReturn(mockResultSet).anyTimes();
        expect(mockResultSet.next()).andReturn(true).times(3);
        expect(mockResultSet.next()).andReturn(false).times(1);
        expect(mockResultSet.next()).andReturn(true).times(2);
        expect(mockResultSet.next()).andReturn(false).times(1);
        expect(mockResultSet.next()).andReturn(true).times(2);
        expect(mockResultSet.next()).andReturn(false).times(1);
        expect(mockResultSet.next()).andReturn(true).times(1);
        expect(mockResultSet.next()).andReturn(false).times(1);
        expect(mockResultSet.next()).andReturn(true).times(1);
        expect(mockResultSet.next()).andReturn(false).times(1);
        replay(mockMgr);
        replay(mockConn);
        replay(mockPrepStmt);
        replay(mockResultSet);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionAll() throws InitializationException
    {
        new SampleDBStatsReloadableQueryableCache(null, null, null, null);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionProps() throws InitializationException
    {
        new SampleDBStatsReloadableQueryableCache(null, logger, null, mockMgr);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionEmptyProps() throws InitializationException
    {
        Properties props = new Properties();
        new SampleDBStatsReloadableQueryableCache(null, logger, props, mockMgr);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionEmptyPropsRefresh() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        new SampleDBStatsReloadableQueryableCache(null, logger, props, mockMgr);
    }

    @Test(expected = InitializationException.class)
    public void testConstructionDBMgr() throws InitializationException
    {
        Properties props = new Properties();
        props.put("refresh_interval", "15");
        props.put("queryEntities", query);
        new SampleDBStatsReloadableQueryableCache(null, logger, props, null);
    }

    @Test
    public void testRefreshEntities() throws InitializationException, SQLException, RefreshException
    {
        list = new ArrayList<SampleQueryableUpdatableEntity>();
        list.add(new SampleQueryableUpdatableEntity(1, sysTime, 10, false, sysTime));
        list.add(new SampleQueryableUpdatableEntity(2, sysTime, 20, false, sysTime));
        list.add(new SampleQueryableUpdatableEntity(3, sysTime, 30, false, sysTime));

        Properties props = new Properties();
        props.put("refresh_interval", refreshInterval);
        props.put("query", query);

        SampleDBStatsReloadableQueryableCache cache = new SampleDBStatsReloadableQueryableCache(null, logger, props, mockMgr);
        assertEquals(new Timestamp(0), cache.mostRecentEntityTime);
        cache.refresh();
        assertEquals(new Timestamp(sysTime), cache.mostRecentEntityTime);
        assertEquals(3, cache.getEntityCount());

        cache.remove(1);
        assertEquals(2, cache.getEntityCount());

        list.add(new SampleQueryableUpdatableEntity(4, sysTime2, 20, false, sysTime2));
        list.add(new SampleQueryableUpdatableEntity(5, sysTime2, 30, false, sysTime2));
        cache.refresh();
        assertEquals(new Timestamp(sysTime2), cache.mostRecentEntityTime);
        assertEquals(4, cache.getEntityCount());

        list.add(new SampleQueryableUpdatableEntity(6, sysTime, 20, false, sysTime));
        list.add(new SampleQueryableUpdatableEntity(7, sysTime, 30, false, sysTime));
        cache.refresh();
        assertEquals(new Timestamp(sysTime2), cache.mostRecentEntityTime);
        assertEquals(6, cache.getEntityCount());

        list.add(new SampleQueryableUpdatableEntity(7, sysTime3, 30, true, sysTime3));
        cache.refresh();
        assertEquals(new Timestamp(sysTime3), cache.mostRecentEntityTime);
        assertEquals(5, cache.getEntityCount());

        cache.addToError = true;
        list.add(new SampleQueryableUpdatableEntity(420, sysTime3, 421, true, sysTime3));
        cache.refresh();
        cache.addToError = false;
        assertEquals(true, cache.queryErrorMessage(420) != null);
        assertEquals(5, cache.getEntityCount());
        assertEquals(1, cache.getFailedEntityCount());
    }
}
