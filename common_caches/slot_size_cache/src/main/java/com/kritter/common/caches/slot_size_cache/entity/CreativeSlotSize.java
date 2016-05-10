package com.kritter.common.caches.slot_size_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;

import java.sql.Timestamp;

/**
 * This class keeps creative slot information as size attributes
 * and slot id.
 */
public class CreativeSlotSize
{
    @Getter
    private short width;
    @Getter
    private short height;
    @Getter
    private short slotId;

    public CreativeSlotSize(short width,short height,short slotId)
    {
        this.width = width;
        this.height = height;
        this.slotId = slotId;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.width) + (this.height);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        CreativeSlotSize externalObject = (CreativeSlotSize) obj;

        if(
            this.width == externalObject.width &&
            this.height== externalObject.height
          )
            return true;

        return false;
    }
}