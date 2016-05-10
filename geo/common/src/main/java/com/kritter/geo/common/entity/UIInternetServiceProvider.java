package com.kritter.geo.common.entity;

import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;

import java.util.Set;

/**
 * This class models ui entity for internet service provider data storage.
 */

public class UIInternetServiceProvider
{
    @Getter
    private Integer countryUiId;
    @Getter
    private String ispUiName;
    @Getter
    private Set<Integer> ispIdSet;

    //constants
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    private static final String CONTROL_A = String.valueOf((char)1);

    @Getter
    private static final String UPDATION_QUERY = "update ui_targeting_isp set entity_id_set = ? where " +
                                                 "isp_ui_name = ? and country_ui_id = ?";
    @Getter
    private static final String INSERTION_QUERY = "insert into ui_targeting_isp(country_ui_id,isp_ui_name," +
                                                  "entity_id_set,modified_on) values ";

    public UIInternetServiceProvider(Integer countryUiId,String ispUiName,Set<Integer> ispIdSet)
    {
        this.countryUiId = countryUiId;
        this.ispUiName = ispUiName;
        this.ispIdSet = ispIdSet;
    }

    public boolean doesISPIdExistInISPIdSet(Integer ispIdForSomeDatasource)
    {
        return ispIdSet.contains(ispIdForSomeDatasource);
    }

    public void addToISPIdSet(Integer ispIdForSomeDatasource)
    {
        ispIdSet.add(ispIdForSomeDatasource);
    }

    public void removeFromISPIdSet(Integer ispIdForSomeDatasource)
    {
        ispIdSet.remove(ispIdForSomeDatasource);
    }

    public String prepareKeyToLookupISPUIEntry()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(countryUiId);
        sb.append(CONTROL_A);
        sb.append(ispUiName.toLowerCase());
        return sb.toString();
    }

    public static String prepareKeyToLookupISPUIEntry(Integer countryUiId,String ispUiName)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(countryUiId);
        sb.append(CONTROL_A);
        sb.append(ispUiName.toLowerCase());
        return sb.toString();
    }

    public String prepareNewUIISPRowForInsertionIntoSQL()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryUiId);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(ispUiName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(ResultSetHelper.prepareIntegerArrayForMySQLInsertion(
                ispIdSet.toArray(new Integer[ispIdSet.size()])));
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append("now())");
        sb.append(COMMA);

        return sb.toString();
    }
}