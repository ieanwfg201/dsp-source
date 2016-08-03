package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This class keeps state data from state table.
 */
@ToString
public class State implements IUpdatableEntity<IpRangeKeyValue>
{
    @Getter @Setter
    private int countryId;
    @Getter
    private int stateId;
    @Getter @Setter
    private String stateCode;
    @Getter @Setter
    private String stateName;
    @Getter
    private String dataSourceName;
    @Getter @Setter
    private Timestamp lastUpdated;

    //fields to keep ip range for this country storage in csv database.
    @Getter @Setter
    private BigInteger startIpAddressValue;
    @Getter @Setter
    private BigInteger endIpAddressValue;

    private static final String CONTROL_A = String.valueOf((char)1);

    /*constructors.*/
    /**
     * This constructor is used for reading state information from detection file database.
     * @param stateId
     * @param dataSourceName
     */
    public State(int stateId,String dataSourceName)
    {
        this.stateId = stateId;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This constructor is to prepare state information for insertion into database.
     * @param countryId
     * @param stateCode
     * @param stateName
     * @param dataSourceName
     * @param lastUpdated
     */
    public State(int countryId,String stateCode,String stateName,String dataSourceName,Timestamp lastUpdated)
    {
        this.countryId = countryId;
        this.stateCode = stateCode;
        this.stateName = stateName;
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
        return new IpRangeKeyValue(startIpAddressValue,endIpAddressValue,stateId,dataSourceName);
    }

    public static String generateKeyForUniquenessCheck(int countryId,String stateName)
    {
        StringBuffer key = new StringBuffer();
        key.append(countryId);
        key.append(CONTROL_A);
        key.append(stateName);
        return key.toString();
    }
}
