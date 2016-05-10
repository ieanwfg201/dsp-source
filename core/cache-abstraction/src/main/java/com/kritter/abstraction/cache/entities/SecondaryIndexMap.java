package com.kritter.abstraction.cache.entities;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 7-June-2013<br></br>
 * Class: Primary purpose of this entity is reduce clutter in the Queryable cache abstract implementation<br></br>
 * Note that while addition to the index we use the SecondaryIndexWrapper to accommodate for the special case of broad targeted entities<br></br>
 */
public class SecondaryIndexMap<I>
{
    Map<ISecondaryIndex, Set<I>> secIndexMap;
    Set<I> broadTargetedEntities;

    public SecondaryIndexMap()
    {
        secIndexMap = new ConcurrentHashMap<ISecondaryIndex, Set<I>>();
        broadTargetedEntities = new HashSet<I>();
    }

    public void add(Set<ISecondaryIndex> keySet, I entityId, boolean isAllTargeted)
    {
        if(isAllTargeted)
        {
            broadTargetedEntities.add(entityId);
            return;
        }
        for(ISecondaryIndex key : keySet)
            add(key, entityId);
    }

    private void add(ISecondaryIndex key, I entityId)
    {
        Set<I> currSet = secIndexMap.get(key);
        if(currSet == null)
        {
            currSet = new HashSet<I>();
            secIndexMap.put(key, currSet);
        }
        currSet.add(entityId);
    }

    public void remove(Set<ISecondaryIndex> keySet, I entityId, boolean isAllTargeted)
    {
        if(isAllTargeted)
        {
            broadTargetedEntities.remove(entityId);
            return;
        }
        for(ISecondaryIndex key : keySet)
            remove(key, entityId);
    }

    public void remove(ISecondaryIndex key, I entityId)
    {
        Set<I> currSet = secIndexMap.get(key);
        if(currSet == null)
            return;

        currSet.remove(entityId);
    }

    public Set<I> getPrimaryIndexSet(ISecondaryIndex key)
    {
        Set<I> returnSet = new HashSet<I>();
        Set<I> specificPrimaryIndexSet = secIndexMap.get(key);
        if(specificPrimaryIndexSet != null)
            returnSet.addAll(specificPrimaryIndexSet);

        returnSet.addAll(broadTargetedEntities);
        return returnSet;
    }

    public void clear()
    {
        for(Map.Entry<ISecondaryIndex, Set<I>> entry : secIndexMap.entrySet())
        {
            entry.getValue().clear();
        }
        secIndexMap.clear();
        broadTargetedEntities.clear();
    }

    public String toString()
    {
        JSONObject jsonObj = new JSONObject();

        for(Map.Entry<ISecondaryIndex, Set<I>> entry : secIndexMap.entrySet())
            jsonObj.put(entry.getKey(), entry.getValue().size());
        jsonObj.put("broad_targeted", broadTargetedEntities.size());
        return jsonObj.toString();
    }
}
