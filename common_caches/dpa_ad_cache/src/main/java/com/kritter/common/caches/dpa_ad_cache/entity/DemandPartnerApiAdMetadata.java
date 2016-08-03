package com.kritter.common.caches.dpa_ad_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * This class keeps ad metadata defined for a demand partner api.
 */
@ToString
public class DemandPartnerApiAdMetadata implements IUpdatableEntity<String>
{
    @Getter
    private int internalId;
    @Getter
    private String adGuid;
    @Getter
    private String accountGuid;
    @Getter
    private Double ecpm;

    private final Timestamp updateTime;

    public DemandPartnerApiAdMetadata(
                                      int internalId,
                                      String adGuid,
                                      String accountGuid,
                                      Double ecpm,
                                      Timestamp updateTime
                                     )
    {
        this.internalId = internalId;
        this.adGuid = adGuid;
        this.accountGuid = accountGuid;
        this.ecpm = ecpm;
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
    public String getId()
    {
        return this.adGuid;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.adGuid.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        DemandPartnerApiAdMetadata externalObject = (DemandPartnerApiAdMetadata) obj;

        if (this.adGuid.equals(externalObject.adGuid))
            return true;

        return false;
    }
}
