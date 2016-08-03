package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@ToString
public class IspUserInterfaceId implements IUpdatableEntity<Integer>
{
    @Getter
    private Integer ispUserInterfaceId;
    @Getter
    private Set<Integer> ispIdSet;
    @Getter
    private Timestamp modifiedOn;

    public IspUserInterfaceId(Integer ispUserInterfaceId,Set<Integer> ispIdSet,Timestamp modifiedOn)
    {
        this.ispUserInterfaceId = ispUserInterfaceId;

        if(null == ispIdSet )
            this.ispIdSet = new HashSet<Integer>();
        else
            this.ispIdSet = ispIdSet;

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
        return ispUserInterfaceId;
    }

    public static ISecondaryIndexWrapper getSecondaryIndexForClass(Class className,
                                                                   final IspUserInterfaceId ispUserInterfaceId)
    {
        if(className.equals(IspUserInterfaceIdSecondaryIndex.class))
            return getSecondaryIndex(ispUserInterfaceId);

        return null;
    }

    private static ISecondaryIndexWrapper getSecondaryIndex(final IspUserInterfaceId ispUserInterfaceId)
    {
        final Integer[] dataSourceIspIds = ispUserInterfaceId.getIspIdSet().
                toArray(new Integer[ispUserInterfaceId.getIspIdSet().size()]);

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
                Set<ISecondaryIndex> dataSourceIspIdSet = new HashSet<ISecondaryIndex>();

                if(dataSourceIspIds != null && dataSourceIspIds.length > 0)
                {
                    for(Integer dataSourceIspId :  dataSourceIspIds)
                        dataSourceIspIdSet.add(new IspUserInterfaceIdSecondaryIndex(dataSourceIspId));
                }
                else
                {
                    dataSourceIspIdSet.add(new IspUserInterfaceIdSecondaryIndex(null));
                }

                return dataSourceIspIdSet;
            }
        };
    }
}
