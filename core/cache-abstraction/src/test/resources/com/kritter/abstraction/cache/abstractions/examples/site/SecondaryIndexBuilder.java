package com.kritter.abstraction.cache.abstractions.examples.site;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Date: 13-06-2013
 * Class:
 */
public class SecondaryIndexBuilder
{
    public static ISecondaryIndexWrapper getGuidIndex(SiteEntity site)
    {
        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new GuidSecondaryIndex(site.getSiteGuid()));
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

    public static ISecondaryIndexWrapper getUrlNameIndex(SiteEntity site)
    {
        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new UrlNameSecondaryIndex(site.getSiteName(), site.getSiteUrl()));
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
