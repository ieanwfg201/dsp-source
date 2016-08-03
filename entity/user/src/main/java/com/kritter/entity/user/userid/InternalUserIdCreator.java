package com.kritter.entity.user.userid;


import java.util.Set;

public interface InternalUserIdCreator {
    /**
     * Given a list of external user ids, creates a new(or existing in case of de-duplication) user id
     * @param externalUserIds Set of external user ids obtained in the request
     * @return Internal user id to be used by the system
     */
    public String createInternalUserIdFromExternalUserIds(Set<ExternalUserId> externalUserIds);
}
