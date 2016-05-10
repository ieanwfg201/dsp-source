package com.kritter.common.caches.dpa_ad_cache;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.common.caches.dpa_ad_cache.entity.AccountIdSecondaryKey;
import com.kritter.common.caches.dpa_ad_cache.entity.DemandPartnerApiAdMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * Secondary index builder for ad metadata for demand partner api.
 */
public class SecondaryIndexBuilder
{
    public static ISecondaryIndexWrapper getAccountIdIndex(DemandPartnerApiAdMetadata demandPartnerApiAdMetadata)
    {
        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new AccountIdSecondaryKey(demandPartnerApiAdMetadata.getAccountGuid()));

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