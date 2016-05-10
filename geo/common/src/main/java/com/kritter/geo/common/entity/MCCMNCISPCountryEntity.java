package com.kritter.geo.common.entity;

import lombok.Getter;

/**
 * This class keeps mcc mnc isp with country information from
 * mcc_mnc table for usage at various places.
 */
public class MCCMNCISPCountryEntity
{

    @Getter
    private String mcc;
    @Getter
    private String mnc;
    @Getter
    private String countryCode;
    @Getter
    private String countryName;
    @Getter
    private String ispName;

    //constants.
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String DELIMITER = "-";

    public MCCMNCISPCountryEntity(String mcc,String mnc,String countryCode,
                                  String countryName,String ispName)
    {
        this.mcc = mcc;
        this.mnc = mnc;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.ispName = ispName;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.mcc.hashCode() + this.mnc.hashCode() +
                           this.countryCode.hashCode() + this.countryName.hashCode() +
                           this.ispName.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        MCCMNCISPCountryEntity externalObject = (MCCMNCISPCountryEntity) obj;

        if (
            this.mcc.equals(externalObject.mcc) &&
            this.mnc.equals(externalObject.mnc)
           )
            return true;

        return false;
    }

    public static String prepareKey(String mcc,String mnc)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(mcc);
        sb.append(DELIMITER);
        sb.append(mnc);
        return sb.toString();
    }

    public String prepareCountryRowForInsertion(String dataSourceName)
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
        sb.append(dataSourceName);
        sb.append(QUOTE);
        sb.append(COMMA);
        sb.append("now())");
        sb.append(COMMA);

        return sb.toString();
    }

    public String prepareIspRowForInsertion(int countryIdFromSqlDatabase,String dataSourceName)
    {
        StringBuffer sb = new StringBuffer("(");
        sb.append(countryIdFromSqlDatabase);
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
}
