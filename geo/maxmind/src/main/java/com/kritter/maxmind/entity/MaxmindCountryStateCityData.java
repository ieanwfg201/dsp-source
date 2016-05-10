package com.kritter.maxmind.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigInteger;

/**
 * This class contains maxmind's country code,state code,city name.
 */
public class MaxmindCountryStateCityData
{
    @Getter @Setter
    private BigInteger startIp;
    @Getter @Setter
    private BigInteger endIp;
    @Getter
    private String countryCode;
    @Getter
    private String stateCode;
    @Getter
    private String cityName;
    @Getter
    private String dataSourceName;

    //other parameters used for mysql population.
    @Getter @Setter
    private int countryId;
    @Getter @Setter
    private int stateId;
    @Getter @Setter
    private String stateName;

    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    private static final String STATE_CITY_DETECTION_FIELDS_DELIMITER = ",";
    private static final String NEW_LINE = "\n";
    private static final String CONTROL_A = String.valueOf((char)1);

    public MaxmindCountryStateCityData(
                                       String countryCode,
                                       String stateCode,
                                       String cityName,
                                       String dataSourceName
                                      )
    {
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.cityName = cityName;
        this.dataSourceName = dataSourceName;
    }

    public String prepareStateRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryId);
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

    public String prepareCityRowForInsertion()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(stateId);
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

    public String prepareLineForStateCityDetectionFilePopulation(BigInteger startIpValue,
                                                                 BigInteger endIpValue,
                                                                 int stateId,
                                                                 int cityId)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(startIpValue);
        sb.append(STATE_CITY_DETECTION_FIELDS_DELIMITER);
        sb.append(endIpValue);
        sb.append(STATE_CITY_DETECTION_FIELDS_DELIMITER);
        sb.append(stateId);
        sb.append(STATE_CITY_DETECTION_FIELDS_DELIMITER);
        sb.append(cityId);
        sb.append(NEW_LINE);

        return sb.toString();
    }

    public String prepareKeyForStateDataMapStorage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(countryId);
        sb.append(CONTROL_A);
        sb.append(stateName);

        return sb.toString();
    }

    public String prepareKeyForCityDataMapStorage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(stateId);
        sb.append(CONTROL_A);
        sb.append(cityName);

        return sb.toString();
    }
}