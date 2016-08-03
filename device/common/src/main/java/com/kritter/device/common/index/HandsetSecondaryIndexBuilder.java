package com.kritter.device.common.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * This class builds all of the secondary indexes required to manage handset data lookup
 * in memory cache.
 */
public class HandsetSecondaryIndexBuilder
{

    public static ISecondaryIndexWrapper getMakeModelSecondaryIndex(int manufacturerId, int modelId)
    {
        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new HandsetMakeModelSecondaryIndex(manufacturerId, modelId));

        return new ISecondaryIndexWrapper()
        {
            @Override
            public boolean isAllTargeted()
            {
                return false;
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet()
            {
                return indexKeyList;
            }
        };
    }
}