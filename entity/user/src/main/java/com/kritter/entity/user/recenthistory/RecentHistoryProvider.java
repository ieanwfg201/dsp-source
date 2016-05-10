package com.kritter.entity.user.recenthistory;

import com.kritter.user.thrift.struct.ImpressionEvent;
import com.kritter.user.thrift.struct.RecentImpressionHistory;

import java.util.SortedSet;

public interface RecentHistoryProvider {
    public RecentImpressionHistory fetchImpressionHistoryForUser(String kritterUserId) throws Exception;

    public void updateImpressionHistoryForUser(String kritterUserId, SortedSet<ImpressionEvent> eventSet)
            throws Exception;

    public void updateRecentHistory(String kritterUserId, SortedSet<ImpressionEvent> eventSet);
}
