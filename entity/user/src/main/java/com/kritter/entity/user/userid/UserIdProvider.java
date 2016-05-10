package com.kritter.entity.user.userid;

import java.util.Set;

public interface UserIdProvider {
    public String getInternalUserId(Set<ExternalUserId> userIds);
}
