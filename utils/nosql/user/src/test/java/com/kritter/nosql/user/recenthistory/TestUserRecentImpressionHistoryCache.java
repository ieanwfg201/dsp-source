package com.kritter.nosql.user.recenthistory;

import com.kritter.nosql.user.utils.ImpressionEventComparator;
import com.kritter.user.thrift.struct.ImpressionEvent;
import com.kritter.user.thrift.struct.RecentImpressionHistory;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.impl.InMemoryCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class TestUserRecentImpressionHistoryCache {
    private static final String NAMESPACE_NAME = "user_info";
    private static final String TABLE_NAME = "recent_imp_history";
    private static final String PRIMARY_KEY_NAME = "user_id";
    private static final String ATTRIBUTE_NAME_IMPRESSION_HISTORY = "user";

    private InMemoryCache inMemoryCache;
    private UserRecentImpressionHistoryCache recentImpressionHistoryCache;

    @Before
    public void setup() {
        this.inMemoryCache = new InMemoryCache();

        Properties properties = new Properties();
        properties.put(UserRecentImpressionHistoryCache.ATTRIBUTE_NAME_IMPRESSION_HISTORY_KEY, ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        properties.put(UserRecentImpressionHistoryCache.PRIMARY_KEY_NAME_KEY, PRIMARY_KEY_NAME);
        properties.put(UserRecentImpressionHistoryCache.TABLE_NAME_KEY, TABLE_NAME);
        properties.put(UserRecentImpressionHistoryCache.NAMESPACE_NAME_KEY, NAMESPACE_NAME);
        this.recentImpressionHistoryCache = new UserRecentImpressionHistoryCache("test", "test", 10, 1, this.inMemoryCache, properties);
    }

    @After
    public void finish() {
        this.recentImpressionHistoryCache.destroy();
    }

    @Test
    public void test1() throws Exception {
        String userId = "user1";
        SortedSet<ImpressionEvent> eventSet = new TreeSet<ImpressionEvent>(new ImpressionEventComparator());
        int[] adIds = {1, 1, 1, 113, 123};
        long[] impressionTime = {7, 11, 23, 121, 2332323};
        for(int i = 0; i < adIds.length; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(adIds[i]);
            event.setTimestamp(impressionTime[i]);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(userId, eventSet);

        Set<String> attributeSet = new HashSet<String>();
        attributeSet.add(ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        NoSqlData data = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
        Map<String, NoSqlData> resData = this.inMemoryCache.fetchSingleRecordAttributes(
                NAMESPACE_NAME, TABLE_NAME, data, attributeSet
        );
        RecentImpressionHistory impressionHistory = this.recentImpressionHistoryCache.
                fetchRecentImpressionHistoryObject((byte[]) resData.get(ATTRIBUTE_NAME_IMPRESSION_HISTORY).getValue());

        assertEquals(impressionHistory.getPosition(), 0);
        for(int i = 0; i < adIds.length; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), adIds[i]);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), impressionTime[i]);
        }
    }

    @Test
    public void test2() throws Exception {
        String userId = "user1";
        SortedSet<ImpressionEvent> eventSet = new TreeSet<ImpressionEvent>(new ImpressionEventComparator());
        int[] adIds = {1, 7, 9};
        long[] impressionTime = {564912, 2324, 948};
        for(int i = 0; i < adIds.length; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(adIds[i]);
            event.setTimestamp(impressionTime[i]);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(userId, eventSet);

        adIds[0] = 9;
        impressionTime[0] = 948;
        adIds[1] = 7;
        impressionTime[1] = 2324;
        adIds[2] = 1;
        impressionTime[2] = 564912;

        eventSet.clear();
        ImpressionEvent event = new ImpressionEvent();
        event.setAdId(7);
        event.setTimestamp(888389);
        eventSet.add(event);
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(userId, eventSet);

        Set<String> attributeSet = new HashSet<String>();
        attributeSet.add(ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        NoSqlData data = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
        Map<String, NoSqlData> resData = this.inMemoryCache.fetchSingleRecordAttributes(
                NAMESPACE_NAME, TABLE_NAME, data, attributeSet
        );
        RecentImpressionHistory impressionHistory = this.recentImpressionHistoryCache.
                fetchRecentImpressionHistoryObject((byte[]) resData.get(ATTRIBUTE_NAME_IMPRESSION_HISTORY).getValue());

        assertEquals(impressionHistory.getPosition(), 0);
        for(int i = 0; i < adIds.length; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), adIds[i]);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), impressionTime[i]);
        }
        assertEquals(impressionHistory.getCircularList().get(3).getAdId(), 7);
        assertEquals(impressionHistory.getCircularList().get(3).getTimestamp(), 888389);
    }

    @Test
    public void test3() throws Exception {
        String userId = "user1";
        SortedSet<ImpressionEvent> eventSet = new TreeSet<ImpressionEvent>(new ImpressionEventComparator());
        for(int i = 0; i < 10; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(i + 1);
            event.setTimestamp(i + 1);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(userId, eventSet);

        int[] adIds = {1, 7, 9};
        long[] impressionTime = {114, 742, 948};
        for(int i = 0; i < adIds.length; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(adIds[i]);
            event.setTimestamp(impressionTime[i]);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(userId, eventSet);

        Set<String> attributeSet = new HashSet<String>();
        attributeSet.add(ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        NoSqlData data = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
        Map<String, NoSqlData> resData = this.inMemoryCache.fetchSingleRecordAttributes(NAMESPACE_NAME, TABLE_NAME, data, attributeSet);
        RecentImpressionHistory impressionHistory = this.recentImpressionHistoryCache.fetchRecentImpressionHistoryObject((byte[]) resData.get(ATTRIBUTE_NAME_IMPRESSION_HISTORY).getValue());

        assertEquals(impressionHistory.getPosition(), 3);
        for(int i = 0; i < adIds.length; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), adIds[i]);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), impressionTime[i]);
        }
        for(int i = adIds.length; i < 10; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), i + 1);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), i + 1);
        }
    }

    @Test
    public void test4() throws Exception {
        String userId = "user1";
        SortedSet<ImpressionEvent> eventSet = new TreeSet<ImpressionEvent>(new ImpressionEventComparator());
        for(int i = 0; i < 10; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(i + 1);
            event.setTimestamp(i + 1);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateRecentHistory(userId, eventSet);

        Thread.sleep(1000);

        Set<String> attributeSet = new HashSet<String>();
        attributeSet.add(ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        NoSqlData data = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
        Map<String, NoSqlData> resData = this.inMemoryCache.fetchSingleRecordAttributes(NAMESPACE_NAME, TABLE_NAME, data, attributeSet);
        RecentImpressionHistory impressionHistory = this.recentImpressionHistoryCache.fetchRecentImpressionHistoryObject((byte[]) resData.get(ATTRIBUTE_NAME_IMPRESSION_HISTORY).getValue());

        assertEquals(impressionHistory.getPosition(), 0);
        for(int i = 0; i < 10; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), i + 1);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), i + 1);
        }
    }

    @Test
    public void test5() throws Exception {
        String userId = "user1";
        SortedSet<ImpressionEvent> eventSet = new TreeSet<ImpressionEvent>(new ImpressionEventComparator());
        for(int i = 0; i < 10; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(i + 1);
            event.setTimestamp(i + 1);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateRecentHistory(userId, eventSet);

        Thread.sleep(1000);

        int[] adIds = {1, 7, 9};
        long[] impressionTime = {114, 742, 948};
        for(int i = 0; i < adIds.length; ++i) {
            ImpressionEvent event = new ImpressionEvent();
            event.setAdId(adIds[i]);
            event.setTimestamp(impressionTime[i]);
            eventSet.add(event);
        }
        this.recentImpressionHistoryCache.updateRecentHistory(userId, eventSet);

        Thread.sleep(1000);

        Set<String> attributeSet = new HashSet<String>();
        attributeSet.add(ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        NoSqlData data = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
        Map<String, NoSqlData> resData = this.inMemoryCache.fetchSingleRecordAttributes(NAMESPACE_NAME, TABLE_NAME, data, attributeSet);
        RecentImpressionHistory impressionHistory = this.recentImpressionHistoryCache.fetchRecentImpressionHistoryObject((byte[]) resData.get(ATTRIBUTE_NAME_IMPRESSION_HISTORY).getValue());

        assertEquals(impressionHistory.getPosition(), 3);
        for(int i = 0; i < adIds.length; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), adIds[i]);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), impressionTime[i]);
        }
        for(int i = adIds.length; i < 10; ++i) {
            assertEquals(impressionHistory.getCircularList().get(i).getAdId(), i + 1);
            assertEquals(impressionHistory.getCircularList().get(i).getTimestamp(), i + 1);
        }
    }

}
