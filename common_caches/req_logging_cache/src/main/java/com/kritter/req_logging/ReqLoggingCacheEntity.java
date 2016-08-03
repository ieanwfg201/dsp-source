package com.kritter.req_logging;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.req_logging.ReqLoggingEntity;
import lombok.ToString;

/**
 * Entity being used for maintaining cache for request logging of bidrequests.
 */
@ToString
public class ReqLoggingCacheEntity extends ReqLoggingEntity implements IUpdatableEntity<String>
{

    public ReqLoggingCacheEntity()
    {
        super();
    }

    @Override
    public Long getModificationTime()
    {
        return getLast_modified();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public String getId()
    {
        return getPubId();
    }
}
