package com.kritter.common.site.entity;

import java.util.HashSet;
import java.util.Set;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;

public class SecondaryIndexBuilder
{
        public static ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
                                                           Site entity)
        {
            if(className.equals(SiteIncIdSecondaryKey.class))
                return getIncIdIndex(entity);
            else if(className.equals(PublisherIdSecondaryKey.class))
                return getPublisherIdIndex(entity);

            return null;
        }

	    public static ISecondaryIndexWrapper getIncIdIndex(Site site)
	    {
	        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
	        indexKeyList.add(new SiteIncIdSecondaryKey(site.getSiteIncId()));
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

        public static ISecondaryIndexWrapper getPublisherIdIndex(Site site)
        {
            final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
            indexKeyList.add(new PublisherIdSecondaryKey(site.getPublisherId()));
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