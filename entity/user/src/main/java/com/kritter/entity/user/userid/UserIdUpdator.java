package com.kritter.entity.user.userid;

import java.util.Set;

public interface UserIdUpdator {
    /**
     * Updates (in an external cache or db etc) the internal user id for a set of external user ids
     * @param externalUserIds Set of external user ids.
     * @param internalUserId internal user id to be used by the system for user identification and storage of user
     *                       information
     */
    public void updateUserId(Set<String> externalUserIds, String internalUserId);
}
