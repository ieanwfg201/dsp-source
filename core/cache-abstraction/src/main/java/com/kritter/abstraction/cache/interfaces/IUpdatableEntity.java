package com.kritter.abstraction.cache.interfaces;

import lombok.NonNull;

/**
 * Date: 8-June-2013<br></br>
 * Class: Updatable Entity used in Reloadable Queryable caches. This interface is to indicate to the ICache whether the update on
 * the entity is for addition or removal
 */
public interface IUpdatableEntity<I> extends IEntity<I>
{
    // Each update-able entity is forced to implement this
    @NonNull public Long getModificationTime();
    public boolean isMarkedForDeletion();
}
