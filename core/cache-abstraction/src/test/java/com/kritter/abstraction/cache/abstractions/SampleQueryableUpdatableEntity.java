package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Date: 11-06-2013
 * Class:
 */
@Getter
@AllArgsConstructor
public class SampleQueryableUpdatableEntity implements IUpdatableEntity<Integer>
{
    private final Integer id;
    private Long version;
    private int secIndexId;
    private boolean markedForDeletion;
    private Long modificationTime;
}
