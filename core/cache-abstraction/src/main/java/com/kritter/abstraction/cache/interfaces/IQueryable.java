package com.kritter.abstraction.cache.interfaces;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;

import java.util.Set;

/**
 * Date: 6-June-2013<br></br>
 * Class: Each queryable ICache should provide for:
 * (1) addition and deletion of entities<br></br>
 * (2) Query methods for querying on primary and secondary keys<br></br>
 * (3) Implement IStats and publish whether version-ing of entities is provided for
 */
public interface IQueryable<I, V extends IEntity<I>> extends IStats, ICache
{
    public Set<I> query(ISecondaryIndex secIndexKey) throws UnSupportedOperationException;
    public V query(I primaryIndexKey);
    public void add(V entity);
    public void remove(I entityId);
    public String queryErrorMessage(I entityId);
}
