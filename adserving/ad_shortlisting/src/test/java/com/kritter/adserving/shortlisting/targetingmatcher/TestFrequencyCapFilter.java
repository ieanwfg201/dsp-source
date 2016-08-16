package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.nosql.user.recenthistory.UserRecentImpressionHistoryCache;
import com.kritter.nosql.user.utils.ImpressionEventComparator;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.user.thrift.struct.ImpressionEvent;
import com.kritter.user.thrift.struct.RecentImpressionHistory;
import com.kritter.utils.nosql.common.impl.InMemoryCache;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class TestFrequencyCapFilter {
    private static final String NAMESPACE_NAME = "user_info";
    private static final String TABLE_NAME = "recent_imp_history";
    private static final String PRIMARY_KEY_NAME = "user_id";
    private static final String ATTRIBUTE_NAME_IMPRESSION_HISTORY = "user";

    private AdEntityCache adEntityCache;
    private UserRecentImpressionHistoryCache recentImpressionHistoryCache;
    private Set<Integer> adIds;
    private Context context;

    private static ImpressionEvent getImpressionEvent(int adId, long timestamp) {
        ImpressionEvent impressionEvent = new ImpressionEvent();
        impressionEvent.setAdId(adId);
        impressionEvent.setTimestamp(timestamp);
        return impressionEvent;
    }

    private static RecentImpressionHistory getRecentImpressionHistory(ImpressionEvent...impressionEvents) {
        RecentImpressionHistory recentImpressionHistory = new RecentImpressionHistory();
        List<ImpressionEvent> circularList = new ArrayList<ImpressionEvent>();
        recentImpressionHistory.setPosition(0);
        recentImpressionHistory.setCircularList(circularList);
        for(ImpressionEvent impressionEvent : impressionEvents) {
            circularList.add(impressionEvent);
        }
        return recentImpressionHistory;
    }

    private SortedSet<ImpressionEvent> getSortedImpressionEventSet(ImpressionEvent...impressionEvents) {
        SortedSet<ImpressionEvent> impressionEventSet = new TreeSet<ImpressionEvent>(new ImpressionEventComparator());
        for(ImpressionEvent impressionEvent : impressionEvents) {
            impressionEventSet.add(impressionEvent);
        }
        return impressionEventSet;
    }

    @Before
    public void setup() throws Exception {
        AdEntity nonFrequencyCapped1 = EasyMock.createMock(AdEntity.class);
        EasyMock.expect(nonFrequencyCapped1.isFrequencyCapped()).andReturn(false).anyTimes();
        EasyMock.expect(nonFrequencyCapped1.getMaxCap()).andReturn(0).anyTimes();
        EasyMock.expect(nonFrequencyCapped1.getFrequencyCapTimeWindowInHours()).andReturn(0).anyTimes();
        EasyMock.replay(nonFrequencyCapped1);

        AdEntity frequencyCapped1 = EasyMock.createMock(AdEntity.class);
        EasyMock.expect(frequencyCapped1.isFrequencyCapped()).andReturn(true).anyTimes();
        EasyMock.expect(frequencyCapped1.getMaxCap()).andReturn(5).anyTimes();
        EasyMock.expect(frequencyCapped1.getFrequencyCapTimeWindowInHours()).andReturn(6).anyTimes();
        EasyMock.replay(frequencyCapped1);

        AdEntity frequencyCapped2 = EasyMock.createMock(AdEntity.class);
        EasyMock.expect(frequencyCapped2.isFrequencyCapped()).andReturn(true).anyTimes();
        EasyMock.expect(frequencyCapped2.getMaxCap()).andReturn(5).anyTimes();
        EasyMock.expect(frequencyCapped2.getFrequencyCapTimeWindowInHours()).andReturn(12).anyTimes();
        EasyMock.replay(frequencyCapped2);

        AdEntity frequencyCapped3 = EasyMock.createMock(AdEntity.class);
        EasyMock.expect(frequencyCapped3.isFrequencyCapped()).andReturn(true).anyTimes();
        EasyMock.expect(frequencyCapped3.getMaxCap()).andReturn(5).anyTimes();
        EasyMock.expect(frequencyCapped3.getFrequencyCapTimeWindowInHours()).andReturn(24).anyTimes();
        EasyMock.replay(frequencyCapped3);

        this.adIds = new HashSet<Integer>();
        this.adIds.add(1);
        this.adIds.add(2);
        this.adIds.add(3);
        this.adIds.add(4);

        this.adEntityCache = EasyMock.createMock(AdEntityCache.class);
        EasyMock.expect(adEntityCache.query(1)).andReturn(nonFrequencyCapped1).anyTimes();
        EasyMock.expect(adEntityCache.query(2)).andReturn(frequencyCapped1).anyTimes();
        EasyMock.expect(adEntityCache.query(3)).andReturn(frequencyCapped2).anyTimes();
        EasyMock.expect(adEntityCache.query(4)).andReturn(frequencyCapped3).anyTimes();
        EasyMock.replay(adEntityCache);

        InMemoryCache inMemoryCache = new InMemoryCache();
        Properties properties = new Properties();
        properties.put(UserRecentImpressionHistoryCache.ATTRIBUTE_NAME_IMPRESSION_HISTORY_KEY, ATTRIBUTE_NAME_IMPRESSION_HISTORY);
        properties.put(UserRecentImpressionHistoryCache.PRIMARY_KEY_NAME_KEY, PRIMARY_KEY_NAME);
        properties.put(UserRecentImpressionHistoryCache.TABLE_NAME_KEY, TABLE_NAME);
        properties.put(UserRecentImpressionHistoryCache.NAMESPACE_NAME_KEY, NAMESPACE_NAME);
        this.recentImpressionHistoryCache = new UserRecentImpressionHistoryCache("recent_imp_history", "logger",
                100, 1, inMemoryCache, properties);

        // No Data available for user 1.
        // For user 2, no data available for ad 2 and ad 3. Frequency cap not reached for ad 4
        long currentTime = System.currentTimeMillis();
        long HOUR_TO_MILLISECONDS_UNIT = 60 * 60 * 1000;
        ImpressionEvent ad3Event1 = getImpressionEvent(4, currentTime - 2 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad3Event2 = getImpressionEvent(4, currentTime - HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad3Event3 = getImpressionEvent(4, currentTime - HOUR_TO_MILLISECONDS_UNIT / 2);
        SortedSet<ImpressionEvent> impressionEventSet = getSortedImpressionEventSet(ad3Event1, ad3Event2, ad3Event3);
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(
                "2", impressionEventSet);

        // For user 3, no data available for ad 2. Frequency cap not reached for ad 3 and cap reached for ad 4
        ImpressionEvent ad3Event4 = getImpressionEvent(4, currentTime - 4 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad2Event1 = getImpressionEvent(3, currentTime - HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad3Event5 = getImpressionEvent(4, currentTime - 5 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad2Event2 = getImpressionEvent(3, currentTime - 7 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad3Event6 = getImpressionEvent(4, currentTime - 8 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad2Event3 = getImpressionEvent(3, currentTime - 15 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad2Event4 = getImpressionEvent(3, currentTime - 22 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad3Event7 = getImpressionEvent(4, currentTime - 32 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad2Event5 = getImpressionEvent(3, currentTime - 33 * HOUR_TO_MILLISECONDS_UNIT);
        ImpressionEvent ad2Event6 = getImpressionEvent(3, currentTime - 37 * HOUR_TO_MILLISECONDS_UNIT);
        SortedSet<ImpressionEvent> impressionEventSet2 = getSortedImpressionEventSet(ad3Event1, ad3Event2, ad3Event3,
                ad3Event4, ad2Event1, ad3Event5, ad2Event2, ad3Event6, ad2Event3, ad2Event4, ad3Event7, ad2Event5,
                ad2Event6);
        this.recentImpressionHistoryCache.updateImpressionHistoryForUser(
                "3", impressionEventSet2);
    }

    @Test
    public void test1() {
        Request request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getUserId()).andReturn("1").anyTimes();
        EasyMock.expect(request.isRequestForSystemDebugging()).andReturn(false).anyTimes();
        EasyMock.expect(request.getNoFillReason()).andReturn(null).anyTimes();
        EasyMock.replay(request);

        context = new Context(false);

        FrequencyCapFilter filter = new FrequencyCapFilter("ff", "logger", this.adEntityCache, null, "ad-stats-map");
        Set<Integer> shortlistedAds = filter.shortlistAds(this.adIds, request, context);
        assertTrue(shortlistedAds.contains(1));
        assertFalse(shortlistedAds.contains(2));
        assertFalse(shortlistedAds.contains(3));
        assertFalse(shortlistedAds.contains(4));
    }

    @Test
    public void test2() {
        Request request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getUserId()).andReturn(null).anyTimes();
        EasyMock.expect(request.isRequestForSystemDebugging()).andReturn(false).anyTimes();
        EasyMock.expect(request.getNoFillReason()).andReturn(null).anyTimes();
        EasyMock.replay(request);

        context = new Context(false);

        FrequencyCapFilter filter = new FrequencyCapFilter("ff", "logger", this.adEntityCache,
                this.recentImpressionHistoryCache, "ad-stats-map");
        Set<Integer> shortlistedAds = filter.shortlistAds(this.adIds, request, context);
        assertTrue(shortlistedAds.contains(1));
        assertFalse(shortlistedAds.contains(2));
        assertFalse(shortlistedAds.contains(3));
        assertFalse(shortlistedAds.contains(4));
    }

    @Test
    public void test3() {
        Request request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getUserId()).andReturn("1").anyTimes();
        EasyMock.expect(request.isRequestForSystemDebugging()).andReturn(false).anyTimes();
        EasyMock.expect(request.getNoFillReason()).andReturn(null).anyTimes();
        request.setNoFillReason(NoFillReason.FREQUENCY_CAP);
        EasyMock.expectLastCall();
        EasyMock.replay(request);

        context = new Context(false);

        FrequencyCapFilter filter = new FrequencyCapFilter("ff", "logger", adEntityCache, null, "ad-stats-map");
        Set<Integer> shortlistedAds = filter.shortlistAds(null, request, context);
        assertEquals(null, shortlistedAds);
    }

    @Test
    public void test4() {
        Request request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getUserId()).andReturn("1").anyTimes();
        EasyMock.expect(request.isRequestForSystemDebugging()).andReturn(false).anyTimes();
        EasyMock.expect(request.getNoFillReason()).andReturn(null).anyTimes();
        EasyMock.replay(request);

        context = new Context(false);

        FrequencyCapFilter filter = new FrequencyCapFilter("ff", "logger", this.adEntityCache,
                this.recentImpressionHistoryCache, "ad-stats-map");

        Set<Integer> shortlistedAds = filter.shortlistAds(this.adIds, request, context);
        assertTrue(shortlistedAds.contains(1));
        assertTrue(shortlistedAds.contains(2));
        assertTrue(shortlistedAds.contains(3));
        assertTrue(shortlistedAds.contains(4));
    }

    @Test
    public void test5() {
        Request request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getUserId()).andReturn("2").anyTimes();
        EasyMock.expect(request.isRequestForSystemDebugging()).andReturn(false).anyTimes();
        EasyMock.expect(request.getNoFillReason()).andReturn(null).anyTimes();
        EasyMock.replay(request);

        context = new Context(false);

        FrequencyCapFilter filter = new FrequencyCapFilter("ff", "logger", this.adEntityCache,
                this.recentImpressionHistoryCache, "ad-stats-map");

        Set<Integer> shortlistedAds = filter.shortlistAds(this.adIds, request, context);
        assertTrue(shortlistedAds.contains(1));
        assertTrue(shortlistedAds.contains(2));
        assertTrue(shortlistedAds.contains(3));
        assertTrue(shortlistedAds.contains(4));
    }

    @Test
    public void test6() {
        Request request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getUserId()).andReturn("3").anyTimes();
        EasyMock.expect(request.isRequestForSystemDebugging()).andReturn(false).anyTimes();
        EasyMock.expect(request.getNoFillReason()).andReturn(null).anyTimes();
        EasyMock.replay(request);

        context = new Context(false);

        FrequencyCapFilter filter = new FrequencyCapFilter("ff", "logger", this.adEntityCache,
                this.recentImpressionHistoryCache, "ad-stats-map");

        Set<Integer> shortlistedAds = filter.shortlistAds(this.adIds, request, context);
        assertTrue(shortlistedAds.contains(1));
        assertTrue(shortlistedAds.contains(2));
        assertTrue(shortlistedAds.contains(3));
        assertFalse(shortlistedAds.contains(4));
    }
}
