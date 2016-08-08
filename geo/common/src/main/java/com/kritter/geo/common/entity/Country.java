package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This entity keeps information about a country.
 * Attributes are
 * integer id(sql auto increment),
 * country code (2 letter code)
 * country name
 * datasource name
 * last_updated
 *
 *
 */
@ToString
public class Country implements IUpdatableEntity<IpRangeKeyValue>
{
    @Getter
    private int countryInternalId;
    @Getter @Setter
    private String countryCode;
    @Getter @Setter
    private String countryCodeThreeLetter;
    @Getter @Setter
    private String countryName;
    @Getter
    private String dataSourceName;
    @Getter
    private Timestamp lastUpdated;

    //fields to keep ip range for this country storage in csv database.
    @Getter @Setter
    private BigInteger startIpAddressValue;
    @Getter @Setter
    private BigInteger endIpAddressValue;

    //constants.
    private static final String COMMA = ",";
    private static final String APOSTROPHE = "'";

    //constructors.
    /**
     * This constructor is used for reading country information from file database.
     * @param countryInternalId
     * @param dataSourceName
     */
    public Country(int countryInternalId,String dataSourceName)
    {
        this.countryInternalId = countryInternalId;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This constructor to prepare country information for insertion into database.
     * @param countryCode
     * @param countryName
     * @param dataSourceName
     * @param lastUpdated
     */
    public Country(String countryCode,String countryName,
                   String dataSourceName,Timestamp lastUpdated)
    {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.dataSourceName = dataSourceName;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public Long getModificationTime()
    {
        return lastUpdated.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public IpRangeKeyValue getId()
    {
        return new IpRangeKeyValue(startIpAddressValue,
                                   endIpAddressValue,
                                   countryInternalId,
                                   dataSourceName);
    }
}
