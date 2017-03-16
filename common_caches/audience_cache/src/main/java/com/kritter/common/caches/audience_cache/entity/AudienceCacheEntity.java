package com.kritter.common.caches.audience_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AudienceCacheEntity implements IUpdatableEntity<Integer> {
    private static final String CTRL_A = String.valueOf((char) 1);

    @Setter
    private boolean markedForDeletion = false;

    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String source_id;
    @Getter
    @Setter
    private String tags;
    @Getter
    @Setter
    private Integer type;
    @Getter
    @Setter
    private long lastModified;


    public AudienceCacheEntity(int id, String name, String source_id, String tags, Integer type, long lastModified, boolean isMarkedForDeletion) {
        this.markedForDeletion = isMarkedForDeletion;
        this.id = id;
        this.name = name;
        this.source_id = source_id;
        this.tags = tags;
        this.type = type;
        this.lastModified = lastModified;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Long getModificationTime() {
        return this.lastModified;
    }

    @Override
    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }
}
