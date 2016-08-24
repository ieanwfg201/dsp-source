package com.kritter.entity.user.targetingprofileincexc;

import java.util.List;
import java.util.Set;

public interface UserTargetingProfileIncExcProvider {
    public List<Integer> getIncExcTargetingProfileIds(Set<String> userIds);
}
