package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.pmp.DSPAndItsAdvertiserAgencyMapping;

import java.sql.Timestamp;

/**
 * This class keeps mapping for a dsp and its advertiser.
 */
public class DSPAndAdvertiserMappingEntity extends DSPAndItsAdvertiserAgencyMapping implements IUpdatableEntity<Integer>
{

    private final Timestamp updateTime;

    public DSPAndAdvertiserMappingEntity(Integer id,String dspId,String advertiserId,Timestamp updateTime)
    {
        super(id,dspId,advertiserId);
        this.updateTime = updateTime;
    }

    @Override
    public Long getModificationTime()
    {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public Integer getId() {
        return super.getId();
    }
}
