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
 * This class keeps countryUserInterfaceId for a country.
 */
@ToString
public class CountryUserInterfaceId implements IUpdatableEntity<Integer>
{
    @Getter
    private String countryCode;
    @Getter
    private Integer countryUserInterfaceId;
    @Getter
    private Set<Integer> countryIdSet;
    @Getter
    private Timestamp modifiedOn;
    private static final String SEPARATOR = ":";

    public CountryUserInterfaceId(String countryCode,
                                  Integer countryUserInterfaceId,
                                  Set<Integer> countryIdSet,
                                  Timestamp modifiedOn)
    {
        this.countryCode = countryCode;
        this.countryUserInterfaceId = countryUserInterfaceId;

        if(null == countryIdSet )
            this.countryIdSet = new HashSet<Integer>();
        else
            this.countryIdSet = countryIdSet;

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
        return countryUserInterfaceId;
    }

    public static ISecondaryIndexWrapper getSecondaryIndexForClass(
                                                                   Class className,
                                                                   final  CountryUserInterfaceId countryUserInterfaceId
                                                                  )
    {
        if(className.equals(CountryUserInterfaceIdSecondaryIndex.class))
            return getSecondaryIndex(countryUserInterfaceId);
        if(className.equals(CountryUserInterfaceIdCountryCodeSecondaryIndex.class))
            return getCountryCodeSecondaryIndex(countryUserInterfaceId);
        return null;
    }

    private static ISecondaryIndexWrapper
                                getCountryCodeSecondaryIndex(final CountryUserInterfaceId countryUserInterfaceId)
    {
        final String countryCode = countryUserInterfaceId.getCountryCode();

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
                Set<ISecondaryIndex> countryCodeSecondaryIndexSet = new HashSet<ISecondaryIndex>();

                if(countryCode != null)
                {
                    countryCodeSecondaryIndexSet.
                            add(new CountryUserInterfaceIdCountryCodeSecondaryIndex(countryCode));
                }
                else
                {
                    countryCodeSecondaryIndexSet.add(new CountryUserInterfaceIdCountryCodeSecondaryIndex(null));
                }

                return countryCodeSecondaryIndexSet;
            }
        };
    }

    private static ISecondaryIndexWrapper getSecondaryIndex(final CountryUserInterfaceId countryUserInterfaceId)
    {
        final Integer[] dataSourceCountryIds = countryUserInterfaceId.getCountryIdSet().
                                                toArray(new Integer[countryUserInterfaceId.getCountryIdSet().size()]);

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
                Set<ISecondaryIndex> dataSourceCountryIdSet = new HashSet<ISecondaryIndex>();

                if(dataSourceCountryIds != null && dataSourceCountryIds.length > 0)
                {
                    for(Integer dataSourceCountryId :  dataSourceCountryIds)
                        dataSourceCountryIdSet.add(new CountryUserInterfaceIdSecondaryIndex(dataSourceCountryId));
                }
                else
                {
                    dataSourceCountryIdSet.add(new CountryUserInterfaceIdSecondaryIndex(null));
                }

                return dataSourceCountryIdSet;
            }
        };
    }

    public String toString()
    {
        StringBuffer signature = new StringBuffer();

        signature.append(countryCode);
        signature.append(SEPARATOR);
        signature.append(countryUserInterfaceId);
        signature.append(SEPARATOR);
        signature.append(countryIdSet);
        return signature.toString();
    }
}
