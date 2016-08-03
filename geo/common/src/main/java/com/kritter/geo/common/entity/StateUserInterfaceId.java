package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * This class keeps data for user interface ids for state entity.
 */
public class StateUserInterfaceId implements IUpdatableEntity<Integer>
{
    @Getter
    private Integer stateUserInterfaceId;
    @Getter
    private Set<Integer> stateIdSet;
    @Getter
    private Timestamp modifiedOn;

    public StateUserInterfaceId(Integer stateUserInterfaceId,
                                Set<Integer> stateIdSet,
                                Timestamp modifiedOn)
    {
        this.stateUserInterfaceId = stateUserInterfaceId;

        if(null == stateIdSet)
            this.stateIdSet = new HashSet<Integer>();
        else
            this.stateIdSet = stateIdSet;

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
        return stateUserInterfaceId;
    }

    public static ISecondaryIndexWrapper getSecondaryIndexForClass(
                                                                    Class className,
                                                                    final  StateUserInterfaceId stateUserInterfaceId
                                                                  )
    {
        if(className.equals(StateUserInterfaceIdSecondaryIndex.class))
            return getSecondaryIndex(stateUserInterfaceId);
       // if(className.equals(StateUserInterfaceIdStateCodeSecondaryIndex.class))
         //   return getStateCodeSecondaryIndex(stateUserInterfaceId);
        return null;
    }

    private static ISecondaryIndexWrapper getSecondaryIndex(final StateUserInterfaceId stateUserInterfaceId)
    {
        final Integer[] dataSourceStateIds = stateUserInterfaceId.getStateIdSet().
                                                toArray(new Integer[stateUserInterfaceId.getStateIdSet().size()]);

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
                Set<ISecondaryIndex> dataSourceStateIdSet = new HashSet<ISecondaryIndex>();

                if(dataSourceStateIds != null && dataSourceStateIds.length > 0)
                {
                    for(Integer dataSourceStateId :  dataSourceStateIds)
                        dataSourceStateIdSet.add(new StateUserInterfaceIdSecondaryIndex(dataSourceStateId));
                }
                else
                {
                    dataSourceStateIdSet.add(new StateUserInterfaceIdSecondaryIndex(null));
                }

                return dataSourceStateIdSet;
            }
        };
    }
}