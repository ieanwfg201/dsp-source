package com.kritter.maxmind.entity;

import lombok.Getter;

import java.math.BigInteger;

/**
 * This class represents isp input data as obtained by maxmind
 * as CSV database file having format:
 *
 * startIpLongValue,endIpLongValue,ispNameWithQuotes.
 */

public class MaxmindISPInputData
{
    @Getter
    private BigInteger startIpValue;
    @Getter
    private BigInteger endIpValue;
    @Getter
    private int countryId;
    @Getter
    private String ispName;
    @Getter
    private String dataSourceName;

    //constants.
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String ISP_DETECTION_FIELDS_DELIMITER = ",";
    private static final String NEW_LINE = "\n";

    public MaxmindISPInputData(BigInteger startIpValue,
                               BigInteger endIpValue,
                               int countryId,
                               String ispName,
                               String dataSourceName)
    {
        this.startIpValue = startIpValue;
        this.endIpValue = endIpValue;
        this.countryId = countryId;
        this.ispName = ispName;
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

    public String prepareIspRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(ispName);
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

    public String prepareLineForISPDetectionFilePopulation(int ispIdFromSqlDatabase)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.startIpValue);
        sb.append(ISP_DETECTION_FIELDS_DELIMITER);
        sb.append(this.endIpValue);
        sb.append(ISP_DETECTION_FIELDS_DELIMITER);
        sb.append(ispIdFromSqlDatabase);
        sb.append(NEW_LINE);

        return sb.toString();
    }
}
