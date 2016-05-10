package com.kritter.serving.demand.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.CreativeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class models creative banner which stores banner files for each account.
 */
@Getter
@ToString
@EqualsAndHashCode(of={"guid"})
public class CreativeBanner implements IUpdatableEntity<Integer>
{
    private final Integer creativeBannerIncId;
    private final String guid;
    private final String accountGuid;
    private final Short slotId;
    private final String resourceURI;
    private final boolean markedForDeletion;
    private final Long modificationTime;

    public CreativeBanner(CreativeBannerBuilder creativeBannerBuilder)
    {
        this.creativeBannerIncId = creativeBannerBuilder.creativeBannerIncId;
        this.guid = creativeBannerBuilder.guid;
        this.accountGuid = creativeBannerBuilder.accountGuid;
        this.slotId = creativeBannerBuilder.slotId;
        this.resourceURI = creativeBannerBuilder.resourceURI;
        this.markedForDeletion = creativeBannerBuilder.markedForDeletion;
        this.modificationTime = creativeBannerBuilder.modificationTime;
    }

    @Override
    public Integer getId()
    {
        return this.creativeBannerIncId;
    }

    public static class CreativeBannerBuilder
    {
        private final Integer creativeBannerIncId;
        private final String guid;
        private final String accountGuid;
        private final Short slotId;
        private final String resourceURI;
        private final boolean markedForDeletion;
        private final Long modificationTime;

        public CreativeBannerBuilder(Integer creativeBannerIncId,
                                     String guid,
                                     String accountGuid,
                                     Short slotId,
                                     String resourceURI,
                                     boolean markedForDeletion,
                                     long modificationTime)
        {
            this.creativeBannerIncId = creativeBannerIncId;
            this.guid = guid;
            this.accountGuid = accountGuid;
            this.slotId = slotId;
            this.resourceURI = resourceURI;
            this.markedForDeletion = markedForDeletion;
            this.modificationTime = modificationTime;
        }

        public CreativeBanner build()
        {
            return new CreativeBanner(this);
        }
    }
}

