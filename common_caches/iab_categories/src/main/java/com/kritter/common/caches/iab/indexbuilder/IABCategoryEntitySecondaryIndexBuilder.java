package com.kritter.common.caches.iab.indexbuilder;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.common.caches.iab.categories.entity.IABCategoryEntity;
import com.kritter.common.caches.iab.index.IABIDIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


/**
 * This class builds secondary indexes for ad entity.
 */

public class IABCategoryEntitySecondaryIndexBuilder {

    private static Logger logger = LoggerFactory.getLogger("cache.logger");

    public static ISecondaryIndexWrapper getIndex(Class className, IABCategoryEntity entity)
    {
        if(className.equals(IABIDIndex.class))
            return IABCategoryEntitySecondaryIndexBuilder.getIABIDSecondaryIndex(entity);
        return null;
    }

    private static ISecondaryIndexWrapper getIABIDSecondaryIndex(IABCategoryEntity entity){

        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new IABIDIndex(entity.getInternalId()));

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
