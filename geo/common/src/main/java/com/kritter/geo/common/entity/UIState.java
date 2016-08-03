package com.kritter.geo.common.entity;

import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;

import java.util.Set;

/**
 * This class keeps data for a state as exposed on user interface
 * and used for targeting matching.
 */
public class UIState
{
    @Getter
    private Integer countryUiId;
    @Getter
    private Integer stateUiId;
    @Getter
    private String stateName;
    @Getter
    private Set<Integer> stateIdSet;

    //constants
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    @Getter
    private static final String UPDATION_QUERY = "update ui_targeting_state set entity_id_set = ? where " +
                                                 "country_ui_id = ? and state_name = ?";
    @Getter
    private static final String INSERTION_QUERY = "insert into ui_targeting_state(country_ui_id,state_name" +
                                                  ",entity_id_set,modified_on) values ";

    private static final String CONTROL_A = String.valueOf((char)1);

    public UIState(Integer countryUiId,String stateName,Set<Integer> stateIdSet)
    {
        this.countryUiId = countryUiId;
        this.stateName = stateName;
        this.stateIdSet = stateIdSet;
    }

    public UIState(Integer stateUiId,Integer countryUiId,String stateName,Set<Integer> stateIdSet)
    {
        this.stateUiId = stateUiId;
        this.countryUiId = countryUiId;
        this.stateName = stateName;
        this.stateIdSet = stateIdSet;
    }

    public boolean doesIdExistInStateIdSet(Integer stateIdForSomeDatasource)
    {
        return stateIdSet.contains(stateIdForSomeDatasource);
    }

    public void addToStateIdSet(Integer stateIdForSomeDatasource)
    {
        stateIdSet.add(stateIdForSomeDatasource);
    }

    public String prepareNewUIStateRowForInsertionIntoSQL()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryUiId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(stateName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                                                    (stateIdSet.toArray(new Integer[stateIdSet.size()])));
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append("now())");
        sb.append(COMMA);

        return sb.toString();
    }

    public static String generateKeyForUniquenessCheck(int countryUiId,String stateName)
    {
        StringBuffer key = new StringBuffer();
        key.append(countryUiId);
        key.append(CONTROL_A);
        key.append(stateName);
        return key.toString();
    }
}