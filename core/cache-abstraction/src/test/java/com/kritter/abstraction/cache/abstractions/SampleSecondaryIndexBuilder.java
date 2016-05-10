package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Date: 9/6/13
 * Class:
 */
@Getter
public class SampleSecondaryIndexBuilder
{
    public static ISecondaryIndexWrapper getSecIndexSet(SampleQueryableEntity entity)
    {
        Set<ISecondaryIndex> secIndexSet = new HashSet<ISecondaryIndex>();
        //build the set here. in this case, its a single secondary index object in the set
        secIndexSet.add(new SampleSecondaryIndex(entity.getSecIndexId()));
        return new SampleSecondaryIndexWrapper(secIndexSet, false);
    }

    public static ISecondaryIndexWrapper getSecIndexSet2(SampleQueryableEntity entity)
    {
        SampleSecondaryIndexWrapper2 indexWrapper2;
        Set<ISecondaryIndex> secIndexSet = new HashSet<ISecondaryIndex>();
        //build the set here. in this case, its a single secondary index object in the set
        if(entity.getValuesList() == null || entity.getValuesList().size() == 0)
        {
            indexWrapper2 = new SampleSecondaryIndexWrapper2(null, true);
        }
        else
        {
            for(Integer id : entity.getValuesList())
                secIndexSet.add(new SampleSecondaryIndex2(id));
            indexWrapper2 = new SampleSecondaryIndexWrapper2(secIndexSet, false);
        }
        return indexWrapper2;
    }
}
