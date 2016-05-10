package com.kritter.geo.common.entity;

import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;

import java.util.Set;

/**
 * This class keeps country database for user interface operations.
 */
public class UICountry
{
    @Getter
    private Integer countryId;
    @Getter
    private String countryCode;
    @Getter
    private String countryName;
    @Getter
    private Set<Integer> countryIdSet;

    //constants
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    @Getter
    private static final String UPDATION_QUERY = "update ui_targeting_country set entity_id_set = ? where " +
                                                 "country_code = ?";
    @Getter
    private static final String INSERTION_QUERY = "insert into ui_targeting_country(country_code,country_name" +
                                                  ",entity_id_set,modified_on) values ";

    public UICountry(String countryCode,String countryName,Set<Integer> countryIdSet)
    {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryIdSet = countryIdSet;
    }

    public UICountry(Integer countryId,String countryCode,String countryName,Set<Integer> countryIdSet)
    {
        this.countryId = countryId;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryIdSet = countryIdSet;
    }

    public boolean doesIdExistInCountryIdSet(Integer countryIdForSomeDatasource)
    {
        return countryIdSet.contains(countryIdForSomeDatasource);
    }

    public void addToCountryIdSet(Integer countryIdForSomeDatasource)
    {
        countryIdSet.add(countryIdForSomeDatasource);
    }

    public String prepareNewUICountryRowForInsertionIntoSQL()
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(QUOTE);
        sb.append(countryCode);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(countryName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append(QUOTE);
        sb.append(ResultSetHelper.prepareIntegerArrayForMySQLInsertion(
                countryIdSet.toArray(new Integer[countryIdSet.size()])));
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append("now())");
        sb.append(COMMA);

        return sb.toString();
    }
}