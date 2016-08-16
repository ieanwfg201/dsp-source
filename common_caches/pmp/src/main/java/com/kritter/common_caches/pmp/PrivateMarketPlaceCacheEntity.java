package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.pmp.PrivateMarketPlaceDeal;
import lombok.ToString;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ToString
public class PrivateMarketPlaceCacheEntity extends PrivateMarketPlaceDeal implements IUpdatableEntity<String>
{
    private final Timestamp updateTime;

    public PrivateMarketPlaceCacheEntity(String dealId,
                                         String dealName,
                                         Integer[] adIdList,
                                         Integer[] siteIdList,
                                         String[] blockedIABCategories,
                                         String[] thirdPartyConnectionList,
                                         Integer[] dspIdList,
                                         Integer[] advertiserIdList,
                                         Map<String,String[]> whitelistedAdvertiserDomainsMap,
                                         Short auctionType,
                                         Integer requestCap,
                                         Timestamp startDate,
                                         Timestamp endDate,
                                         Double dealCPM,
                                         Timestamp updateTime)
    {
        super(dealId,dealName,adIdList,siteIdList,blockedIABCategories,thirdPartyConnectionList,
              dspIdList,advertiserIdList,whitelistedAdvertiserDomainsMap,auctionType,requestCap,
              startDate,endDate, dealCPM);

        this.updateTime = updateTime;
    }

    @Override
    public Long getModificationTime()
    {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public String getId()
    {
        return this.getDealId();
    }

    public static ISecondaryIndexWrapper getSecondaryIndexForClass(
                                                                   Class className,
                                                                   final PrivateMarketPlaceCacheEntity
                                                                           privateMarketPlaceCacheEntity
                                                                  )
    {
        if(className.equals(PrivateMarketPlaceSiteIncIdSecondaryIndex.class))
            return getSiteIdSecondaryIndex(privateMarketPlaceCacheEntity);

        return null;
    }

    private static ISecondaryIndexWrapper getSiteIdSecondaryIndex(final PrivateMarketPlaceCacheEntity
                                                                        privateMarketPlaceCacheEntity)
    {
        final Integer[] siteIdList = privateMarketPlaceCacheEntity.getSiteIdList();

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
                Set<ISecondaryIndex> siteIdSecondaryIndexSet = new HashSet<ISecondaryIndex>();

                if(null != siteIdList)
                {
                    for(Integer siteId: siteIdList)
                        siteIdSecondaryIndexSet.add(new PrivateMarketPlaceSiteIncIdSecondaryIndex(siteId));
                }

                return siteIdSecondaryIndexSet;
            }
        };
    }
}