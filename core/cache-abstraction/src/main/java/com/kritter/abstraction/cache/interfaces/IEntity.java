package com.kritter.abstraction.cache.interfaces;

import lombok.NonNull;

/**
 * Date: 6-June-2013<br></br>
 * Class: All entities saved into a ICache have to implement this interface. Each entity is forced to have a primary key for lookup
 */
public interface IEntity<I>
{
    // Get the primary index key for the entity
    public @NonNull I getId();
}
