package com.kritter.common.caches.iab.categories.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * This class keeps an IAB content category description.
 */
@ToString
public class IABCategoryEntity implements IUpdatableEntity<String>
{
    @Getter
    private Short internalId;
    @Getter
    private String code;
    @Getter
    private String value;
    private boolean isMarkedForDeletion;
    private final Timestamp updateTime;

    public IABCategoryEntity(Short internalId,String code,String value,Timestamp updateTime)
    {
        this.internalId = internalId;
        this.code = code;
        this.value = value;
        this.updateTime = updateTime;
        this.isMarkedForDeletion = false;
    }

    @Override
    public Long getModificationTime()
    {
        return updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return isMarkedForDeletion;
    }

    @Override
    public String getId()
    {
        return code;
    }
}
