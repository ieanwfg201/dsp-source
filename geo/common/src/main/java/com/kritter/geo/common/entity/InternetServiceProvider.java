package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This entity keeps information about an operator which is internet service
 * provider for the end user's device.
 *
 * Attributes are:
 * integer id operator (sql auto increment)
 * integer id country(sql auto increment),
 * operator name,
 * datasource name,
 * last_updated
 *
 *
 */
@ToString
public class InternetServiceProvider implements IUpdatableEntity<IpRangeKeyValue>
{
    @Getter
    private int countryInternalId;
    @Getter
    private int operatorInternalId;
    @Getter @Setter
    private String operatorName;
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
    private static final String CONTROL_A = String.valueOf((char)1);

    //constructors.
    /**
     * This constructor is used for reading isp information from file database.
     *
     * @param operatorInternalId
     * @param dataSourceName
     */
    public InternetServiceProvider(int operatorInternalId,
                                   String dataSourceName)
    {
        this.operatorInternalId = operatorInternalId;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This constructor is used for reading isp information from sql database.
     *
     * @param operatorInternalId
     * @param countryInternalId
     * @param dataSourceName
     */
    public InternetServiceProvider(int operatorInternalId,
                                   int countryInternalId,
                                   String dataSourceName)
    {
        this.operatorInternalId = operatorInternalId;
        this.countryInternalId = countryInternalId;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This constructor is used to prepare isp information for insertion into database.
     * Country id is fetched via detection from country detection class.
     * @param countryInternalId
     * @param operatorName
     * @param dataSourceName
     * @param lastUpdated
     */
    public InternetServiceProvider(int countryInternalId,String operatorName,
                                   String dataSourceName,Timestamp lastUpdated)
    {
        this.countryInternalId = countryInternalId;
        this.operatorName = operatorName;
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

    public static String prepareCountryIdIspNameUniqueKey(Integer countryId,String ispName)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(countryId));
        sb.append(CONTROL_A);
        sb.append(ispName.toLowerCase());
        return sb.toString();
    }
}