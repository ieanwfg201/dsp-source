package com.kritter.geo.common.entity;

import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;

import java.util.Set;

/**
 * This class keeps data for a city as exposed on user interface
 * and used for targeting matching.
 */
public class UICity
{
    @Getter
    private Integer stateUiId;
    @Getter
    private Integer cityUiId;
    @Getter
    private String cityName;
    @Getter
    private Set<Integer> cityIdSet;

    //constants
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    @Getter
    private static final String UPDATION_QUERY = "update ui_targeting_city set entity_id_set = ? where " +
                                                 "state_ui_id = ? and city_name = ?";
    @Getter
    private static final String INSERTION_QUERY = "insert into ui_targeting_city(state_ui_id,city_name" +
                                                  ",entity_id_set,modified_on) values ";

    private static final String CONTROL_A = String.valueOf((char)1);

    public UICity(Integer stateUiId,String cityName,Set<Integer> cityIdSet)
    {
        this.stateUiId = stateUiId;
        this.cityName = cityName;
        this.cityIdSet = cityIdSet;
    }

    public UICity(Integer cityUiId,Integer stateUiId,String cityName,Set<Integer> cityIdSet)
    {
        this.cityUiId = cityUiId;
        this.stateUiId = stateUiId;
        this.cityName = cityName;
        this.cityIdSet = cityIdSet;
    }

    public boolean doesIdExistInCityIdSet(Integer cityIdForSomeDatasource)
    {
        return cityIdSet.contains(cityIdForSomeDatasource);
    }

    public void addToCityIdSet(Integer cityIdForSomeDatasource)
    {
        cityIdSet.add(cityIdForSomeDatasource);
    }

    public String prepareNewUICityRowForInsertionIntoSQL()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(stateUiId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(cityName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                                                (cityIdSet.toArray(new Integer[cityIdSet.size()])));
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append("now())");
        sb.append(COMMA);

        return sb.toString();
    }

    public static String generateKeyForUniquenessCheck(int stateUiId,String cityName)
    {
        StringBuffer key = new StringBuffer();
        key.append(stateUiId);
        key.append(CONTROL_A);
        key.append(cityName);
        return key.toString();
    }
}