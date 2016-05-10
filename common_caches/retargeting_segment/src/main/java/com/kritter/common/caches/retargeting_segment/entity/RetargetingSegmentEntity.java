package com.kritter.common.caches.retargeting_segment.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * This class keeps Retargeting Segment Entity
 */
public class RetargetingSegmentEntity extends RetargetingSegment implements IUpdatableEntity<Integer>{

    private final Timestamp updateTime;

    public RetargetingSegmentEntity(int id, String account_guid, Timestamp updateTime) throws IOException{
        this.retargeting_segment_id = id;
        this.account_guid = account_guid;
        this.updateTime = updateTime;
    }
    @Override
    public Long getModificationTime(){
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion(){
        return false;
    }
    public Integer getId(){
        return this.retargeting_segment_id;
    }
}
