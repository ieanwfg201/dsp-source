package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * This class keeps data for user interface entity of city for targeting purposes.
 */
public class CityUserInterfaceId  implements IUpdatableEntity<Integer>
{
    @Getter
    private Integer cityUserInterfaceId;
    @Getter
    private Set<Integer> cityIdSet;
    @Getter
    private Timestamp modifiedOn;

    public CityUserInterfaceId(
                               Integer cityUserInterfaceId,
                               Set<Integer> cityIdSet,
                               Timestamp modifiedOn
                              )
    {
        this.cityUserInterfaceId = cityUserInterfaceId;

        if(null == cityIdSet)
            this.cityIdSet = new HashSet<Integer>();
        else
            this.cityIdSet = cityIdSet;

        this.modifiedOn = modifiedOn;
    }

    @Override
    public Long getModificationTime()
    {
        return modifiedOn.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public Integer getId()
    {
        return cityUserInterfaceId;
    }

    public static ISecondaryIndexWrapper getSecondaryIndexForClass(
                                                                    Class className,
                                                                    final  CityUserInterfaceId cityUserInterfaceId
                                                                  )
    {
        if(className.equals(CityUserInterfaceIdSecondaryIndex.class))
            return getSecondaryIndex(cityUserInterfaceId);
        return null;
    }

    private static ISecondaryIndexWrapper getSecondaryIndex(final CityUserInterfaceId cityUserInterfaceId)
    {
        final Integer[] dataSourceCityIds = cityUserInterfaceId.getCityIdSet().
                                                    toArray(new Integer[cityUserInterfaceId.getCityIdSet().size()]);

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
                Set<ISecondaryIndex> dataSourceCityIdSet = new HashSet<ISecondaryIndex>();

                if(dataSourceCityIds != null && dataSourceCityIds.length > 0)
                {
                    for(Integer dataSourceCityId :  dataSourceCityIds)
                        dataSourceCityIdSet.add(new CityUserInterfaceIdSecondaryIndex(dataSourceCityId));
                }
                else
                {
                    dataSourceCityIdSet.add(new CityUserInterfaceIdSecondaryIndex(null));
                }

                return dataSourceCityIdSet;
            }
        };
    }
}