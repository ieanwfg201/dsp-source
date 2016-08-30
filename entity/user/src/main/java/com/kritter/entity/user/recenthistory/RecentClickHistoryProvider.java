package com.kritter.entity.user.recenthistory;

import com.kritter.user.thrift.struct.RecentClickHistory;
import com.kritter.user.thrift.struct.ClickEvent;

import java.util.SortedSet;

public interface RecentClickHistoryProvider {
    public RecentClickHistory fetchClickHistoryForUser(String kritterUserId) throws Exception;

    public void updateClickHistoryForUser(String kritterUserId, SortedSet<ClickEvent> eventSet)
            throws Exception;

    public void updateClickHistory(String kritterUserId, SortedSet<ClickEvent> eventSet);
}
