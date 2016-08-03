package com.kritter.entity.user.userid;

import java.util.Set;

public interface UserIdProvider {
    /**
     * Given the external user ids in the request returns the internal user id used by the system
     * @param userIds Set of external user ids coming in the request
     * @return Internal user id used by the system
     */
    public String getInternalUserId(Set<ExternalUserId> userIds);
}
