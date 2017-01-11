package com.kritter.entity.user.userid;

import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.SignalingNotificationObject;

import java.util.Map;
import java.util.Set;

public interface UserIdProvider {
    /**
     * Given the external user ids in the request returns the internal user id used by the system
     * @param userIds Set of external user ids coming in the request
     * @return Internal user id used by the system
     */
    public void getInternalUserId(Set<ExternalUserId> userIds,
                                  SignalingNotificationObject<Map<String, NoSqlData>> noSqlDataMap);
}
