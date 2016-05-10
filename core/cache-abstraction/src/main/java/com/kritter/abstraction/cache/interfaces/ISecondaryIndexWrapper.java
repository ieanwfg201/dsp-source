package com.kritter.abstraction.cache.interfaces;

import java.util.Set;

/**
 * Date: 13-June-2013<br></br>
 * Class: Wrapper around secondary index to handle 'All Targeted' case <br></br>
 * Note that this wrapper be only used while submitting the indices for secondary index building
 * and this should not be used while querying on the secondary index
 */
public interface ISecondaryIndexWrapper
{
    public abstract boolean isAllTargeted();
    public abstract Set<ISecondaryIndex> getSecondaryIndexSet();
}
