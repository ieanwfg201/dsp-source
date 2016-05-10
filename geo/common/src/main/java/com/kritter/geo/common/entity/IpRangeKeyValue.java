package com.kritter.geo.common.entity;

import lombok.Getter;

import java.math.BigInteger;

/**
 * This class is required to index country and operator or any other data
 * stored in mysql database.
 * This would be used while reading country or operator data from CSV
 * files.
 *
 * While storing ip range in in-memory cache, equals could be implemented
 * as => check for any intersection between requesting object and this
 * object.
 * Doing so will make sure that we do not have any intersection in any
 * two ranges and equals would work only if there is an intersection.
 *
 * Also include datasource into the equality check because any two
 * different datasources could have intersecting ip ranges.
 *
 * While detecting the datasource value could be obsolete or not used.
 *
 * Important: entityId could be either country id, operator id or anything
 * else, which could be found for a given IP.
 */

public class IpRangeKeyValue
{
    @Getter
    private BigInteger startIpValue;
    @Getter
    private BigInteger endIpValue;
    @Getter
    private int entityId;
    @Getter
    private String dataSourceName;

    public IpRangeKeyValue(BigInteger startIpValue, BigInteger endIpValue,
                           int entityId, String dataSourceName)
    {
        this.startIpValue = startIpValue;
        this.endIpValue = endIpValue;
        this.entityId = entityId;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This method looks for the object which is its own instance
     * and finds whether the given object intersects with this
     * instance.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        if (null == obj || getClass() != obj.getClass())
            return false;

        IpRangeKeyValue externalObject = (IpRangeKeyValue) obj;

        if(
           (this.startIpValue.compareTo(externalObject.getStartIpValue()) <= 0  &&
            this.endIpValue.compareTo(externalObject.getStartIpValue())   >= 0) ||
           (this.startIpValue.compareTo(externalObject.getEndIpValue())   <= 0) &&
            this.endIpValue.compareTo(externalObject.getEndIpValue())     >= 0)

            return true;

        return false;

    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash
                + this.startIpValue.hashCode()
                + this.endIpValue.hashCode();

        return hash;
    }
}