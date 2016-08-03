package com.kritter.entity.user.usersegment;

import com.kritter.user.thrift.struct.UserSegment;

public interface UserSegmentProvider {
    public UserSegment fetchUserSegment(String kritterUserId) throws Exception;

    public void updateRetargetingSegmentInUserSegment(String kritterUserId, int segmentId)
            throws Exception;

    public void updateRetargetingSegmentInUserSegmentUsingExecutorService(String kritterUserId, int segmentId)
            throws Exception;

}
