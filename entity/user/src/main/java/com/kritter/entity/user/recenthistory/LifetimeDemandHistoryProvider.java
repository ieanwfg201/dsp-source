package com.kritter.entity.user.recenthistory;

import com.kritter.user.thrift.struct.LifetimeDemandHistory;

import java.util.Map;

public interface LifetimeDemandHistoryProvider {
    public LifetimeDemandHistory fetchLifetimeDemandHistoryForUser(String kritterUserId) throws Exception;

    public void updateLifetimeDemandHistoryForUser(String kritterUserId, Map<Integer, Integer> demandImpCount) throws
            Exception;

    public void updateUserHistory(String kritterUserId, Map<Integer, Integer> demandImpCount) throws Exception;
}
