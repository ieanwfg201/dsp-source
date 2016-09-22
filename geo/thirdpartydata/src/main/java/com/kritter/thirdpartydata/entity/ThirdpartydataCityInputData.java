package com.kritter.thirdpartydata.entity;

import lombok.Getter;

import java.math.BigInteger;

/**
 * This class keeps city data information from a third party data source.
 */
public class ThirdpartydataCityInputData
{
    @Getter
    private BigInteger startIpValue;
    @Getter
    private BigInteger endIpValue;
    @Getter
    private int stateId;
    @Getter
    private String cityCode;
    @Getter
    private String cityName;
    @Getter
    private String dataSourceName;

    /*constants for data loading preparation and detection data loading.*/
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String CITY_DETECTION_FIELDS_DELIMITER = ",";
    private static final String NEW_LINE = "\n";

    public ThirdpartydataCityInputData(BigInteger startIpValue,
                                       BigInteger endIpValue,
                                       int stateId,
                                       String cityCode,
                                       String cityName,
                                       String dataSourceName)
    {
        this.startIpValue = startIpValue;
        this.endIpValue = endIpValue;
        this.stateId = stateId;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.dataSourceName = dataSourceName;
    }

    /**
     * This method looks for the object which is its own instance
     * and finds whether the given object equates to this instance.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        ThirdpartydataCityInputData externalObject = (ThirdpartydataCityInputData) obj;

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

    public String prepareCityRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(stateId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(cityCode);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(cityName);
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

    public String prepareLineForCityDetectionFilePopulation(int cityIdFromSqlDatabase)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.startIpValue);
        sb.append(CITY_DETECTION_FIELDS_DELIMITER);
        sb.append(this.endIpValue);
        sb.append(CITY_DETECTION_FIELDS_DELIMITER);
        sb.append(cityIdFromSqlDatabase);
        sb.append(NEW_LINE);
        return sb.toString();
    }
}