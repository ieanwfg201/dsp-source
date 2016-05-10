package com.kritter.maxmind.entity;

import lombok.Getter;

import java.math.BigInteger;

/**
 * This class contains data required for an organization entity.
 */
public class MaxmindORGInputData
{
    @Getter
    private BigInteger startIpValue;
    @Getter
    private BigInteger endIpValue;
    @Getter
    private int countryId;
    @Getter
    private String countryCode;
    @Getter
    private String orgName;
    @Getter
    private String dataSourceName;

    //constants.
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String ORG_DETECTION_FIELDS_DELIMITER = ",";
    private static final String NEW_LINE = "\n";

    public MaxmindORGInputData(BigInteger startIpValue,
                               BigInteger endIpValue,
                               int countryId,
                               String countryCode,
                               String orgName,
                               String dataSourceName)
    {
        this.startIpValue = startIpValue;
        this.endIpValue = endIpValue;
        this.countryId = countryId;
        this.countryCode = countryCode;
        this.orgName = orgName;
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
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        MaxmindISPInputData externalObject = (MaxmindISPInputData) obj;

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

    public String prepareOrgRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(orgName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(dataSourceName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append("now())");
        sb.append(COMMA);

        return sb.toString();
    }

    public String prepareLineForORGDetectionFilePopulation(int orgIdFromSqlDatabase)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.startIpValue);
        sb.append(ORG_DETECTION_FIELDS_DELIMITER);
        sb.append(this.endIpValue);
        sb.append(ORG_DETECTION_FIELDS_DELIMITER);
        sb.append(orgIdFromSqlDatabase);
        sb.append(NEW_LINE);

        return sb.toString();
    }
}
