package com.kritter.thirdpartydata.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * This class keeps state data information from a third party datasource.
 */
public class ThirdpartydataStateInputData
{
    @Getter
    private BigInteger startIpValue;
    @Getter
    private BigInteger endIpValue;
    @Getter
    private int countryId;
    @Getter
    private String stateCode;
    @Getter
    private String stateName;
    @Getter
    private String dataSourceName;

    /*constants for data loading preparation and detection data loading.*/
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String STATE_DETECTION_FIELDS_DELIMITER = ",";
    private static final String NEW_LINE = "\n";

    public ThirdpartydataStateInputData(BigInteger startIpValue,
                                        BigInteger endIpValue,
                                        int countryId,
                                        String stateCode,
                                        String stateName,
                                        String dataSourceName)
    {
        this.startIpValue = startIpValue;
        this.endIpValue = endIpValue;
        this.countryId = countryId;
        this.stateCode = stateCode;
        this.stateName = stateName;
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

        ThirdpartydataStateInputData externalObject = (ThirdpartydataStateInputData) obj;

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

    public String prepareStateRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(stateCode);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(stateName);
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

    public String prepareLineForStateDetectionFilePopulation(int stateIdFromSqlDatabase)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.startIpValue);
        sb.append(STATE_DETECTION_FIELDS_DELIMITER);
        sb.append(this.endIpValue);
        sb.append(STATE_DETECTION_FIELDS_DELIMITER);
        sb.append(stateIdFromSqlDatabase);
        sb.append(NEW_LINE);
        return sb.toString();
    }
}