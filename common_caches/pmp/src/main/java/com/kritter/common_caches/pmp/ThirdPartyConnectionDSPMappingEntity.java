package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.pmp.ThirdPartyConnectionDspMapping;

import java.sql.Timestamp;

/**
 * This class keeps a third party connection and its DSP mapping information.
 */
public class ThirdPartyConnectionDSPMappingEntity
                                            extends ThirdPartyConnectionDspMapping implements IUpdatableEntity<Integer>
{
    private final Timestamp updateTime;

    public ThirdPartyConnectionDSPMappingEntity(Integer id,String thirdPartyConnectionId,
                                                String dspId,Timestamp updateTime)
    {
        super(id,thirdPartyConnectionId,dspId);
        this.updateTime = updateTime;
    }


    @Override
    public Long getModificationTime() {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return false;
    }

    @Override
    public Integer getId() {
        return super.getId();
    }
}
