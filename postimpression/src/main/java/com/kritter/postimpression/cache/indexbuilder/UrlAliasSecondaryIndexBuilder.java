package com.kritter.postimpression.cache.indexbuilder;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.postimpression.cache.index.UrlAliasSecondaryIndex;
import com.kritter.postimpression.cache.entity.UrlAliasEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * This class builds secondary index for url alias entity.
 */

public class UrlAliasSecondaryIndexBuilder {

    public static ISecondaryIndexWrapper getUrlAliasSecondaryIndex(UrlAliasEntity entity){
        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new UrlAliasSecondaryIndex(entity.getAliasUrl()));
        return new ISecondaryIndexWrapper() {
            @Override
            public boolean isAllTargeted() {
                return false;
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet() {
                return indexKeyList;
            }
        };
    }
}
