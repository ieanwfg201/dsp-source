package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This class keeps city related information for reading from detection file database
 * or for using while inserting into sql database.
 */
public class City implements IUpdatableEntity<IpRangeKeyValue>
{
    @Getter
    private int cityId;
    @Getter @Setter
    private int stateId;
    @Getter @Setter
    private String cityCode;
    @Getter @Setter
    private String cityName;
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
     * This constructor is used for reading city information from detection file database.
     * @param cityId
     * @param dataSourceName
     */
    public City(int cityId,String dataSourceName)
    {
        this.cityId = cityId;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This constructor is to prepare city information for insertion into database.
     * @param stateId
     * @param cityCode
     * @param cityName
     * @param dataSourceName
     * @param lastUpdated
     */
    public City(int stateId,String cityCode,String cityName,String dataSourceName,Timestamp lastUpdated)
    {
        this.stateId = stateId;
        this.cityCode = cityCode;
        this.cityName = cityName;
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

    public static String generateKeyForUniquenessCheck(int stateId,String cityName)
    {
        StringBuffer key = new StringBuffer();
        key.append(stateId);
        key.append(CONTROL_A);
        key.append(cityName);
        return key.toString();
    }
}
