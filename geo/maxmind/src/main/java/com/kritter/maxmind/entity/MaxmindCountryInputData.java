package com.kritter.maxmind.entity;

import lombok.Getter;

import java.math.BigInteger;

/**
 * This class models essential data from maxmind database.
 */
public class MaxmindCountryInputData
{
    @Getter
    private BigInteger startIpValue;
    @Getter
    private BigInteger endIpValue;
    @Getter
    private String countryTwoLetterCode;
    @Getter
    private String countryName;
    @Getter
    private String dataSourceName;

    //constants.
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String COUNTRY_DETECTION_FIELDS_DELIMITER = ",";
    private static final String NEW_LINE = "\n";

    public MaxmindCountryInputData(BigInteger startIpValue,
                                   BigInteger endIpValue,
                                   String countryTwoLetterCode,
                                   String countryName,
                                   String dataSourceName)
    {
        this.startIpValue = startIpValue;
        this.endIpValue = endIpValue;
        this.countryTwoLetterCode = countryTwoLetterCode;
        this.countryName = countryName;
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

        MaxmindCountryInputData externalObject = (MaxmindCountryInputData) obj;

        if (
                (this.startIpValue.compareTo(externalObject.getStartIpValue()) <= 0  &&
                 this.endIpValue.compareTo(externalObject.getStartIpValue())   >= 0) ||
                (this.startIpValue.compareTo(externalObject.getEndIpValue())   <= 0  &&
                 this.endIpValue.compareTo(externalObject.getEndIpValue())     >= 0  ))

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

    public String prepareCountryRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(QUOTE);
        sb.append(countryTwoLetterCode);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(countryName);
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

    public String prepareLineForCountryDetectionFilePopulation(int countryIdFromSqlDatabase)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.startIpValue);
        sb.append(COUNTRY_DETECTION_FIELDS_DELIMITER);
        sb.append(this.endIpValue);
        sb.append(COUNTRY_DETECTION_FIELDS_DELIMITER);
        sb.append(countryIdFromSqlDatabase);
        sb.append(NEW_LINE);

        return sb.toString();
    }
}