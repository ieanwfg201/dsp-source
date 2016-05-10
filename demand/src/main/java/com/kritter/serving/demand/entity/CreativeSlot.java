package com.kritter.serving.demand.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class keeps creative slot information.
 */
@Getter
@ToString
@EqualsAndHashCode(of={"creativeGuid"})
public class CreativeSlot implements IUpdatableEntity<Short>
{
    private final Short creativeSlotIncId;
    private final Short creativeSlotWidth;
    private final Short creativeSlotHeight;
    private final boolean markedForDeletion;
    private final Long modificationTime;

    public CreativeSlot(CreativeSlotBuilder creativeSlotBuilder)
    {
        this.creativeSlotIncId = creativeSlotBuilder.creativeSlotIncId;
        this.creativeSlotWidth = creativeSlotBuilder.creativeSlotWidth;
        this.creativeSlotHeight = creativeSlotBuilder.creativeSlotHeight;
        this.markedForDeletion = creativeSlotBuilder.markedForDeletion;
        this.modificationTime = creativeSlotBuilder.modificationTime;
    }

    public static class CreativeSlotBuilder
    {
        private final Short creativeSlotIncId;
        private final Short creativeSlotWidth;
        private final Short creativeSlotHeight;
        private final boolean markedForDeletion;
        private final Long modificationTime;

        public CreativeSlotBuilder(Short creativeSlotIncId,
                                   Short creativeSlotWidth,
                                   Short creativeSlotHeight,
                                   boolean markedForDeletion,
                                   Long modificationTime)
        {
            this.creativeSlotIncId = creativeSlotIncId;
            this.creativeSlotWidth = creativeSlotWidth;
            this.creativeSlotHeight = creativeSlotHeight;
            this.markedForDeletion = markedForDeletion;
            this.modificationTime = modificationTime;
        }

        public CreativeSlot build()
        {
            return new CreativeSlot(this);
        }
    }

    @Override
    public Long getModificationTime() {
        return modificationTime;
    }

    @Override
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    @Override
    public Short getId() {
        return creativeSlotIncId;
    }
}
