package com.kritter.geo.common.entity;

import lombok.Getter;

public class CountryIspUnique
{
    @Getter
    private String countryName;
    @Getter
    private String ispName;
    @Getter
    private String ispUiName;
    private static final String CONTROL_A = String.valueOf((char)1);

    public CountryIspUnique(String countryName,String ispName,String ispUiName)
    {
        this.countryName = countryName;
        this.ispName = ispName;
        this.ispUiName = ispUiName;
    }

    public CountryIspUnique(String key,String value)
    {
        if(null != key && null != value)
        {
            String parts[] = key.split(CONTROL_A);
            if(parts.length == 2)
            {
                countryName = parts[0];
                ispName = parts[1];
            }

            if(null == value)
                ispUiName = null;
            else
            {
                parts = value.split(CONTROL_A);
                if(parts.length == 2)
                {
                    ispUiName = parts[1];
                }
            }
        }
    }

    public String prepareKey()
    {
        if(null == countryName || null == ispName)
            return null;

        StringBuffer sb = new StringBuffer(countryName);
        sb.append(CONTROL_A);
        sb.append(ispName);
        return sb.toString();
    }

    public String prepareValue()
    {
        if(null == countryName || null == ispUiName)
            return null;

        StringBuffer sb = new StringBuffer(countryName);
        sb.append(CONTROL_A);
        sb.append(ispUiName);
        return sb.toString();
    }

    public static String prepareKeyForCountryISPName(String countryName,String ispName)
    {
        StringBuffer sb = new StringBuffer(countryName);
        sb.append(CONTROL_A);
        sb.append(ispName);
        return sb.toString();
    }

    public static String prepareValueForCountryISPUiName(String countryName,String ispUiName)
    {
        StringBuffer sb = new StringBuffer(countryName);
        sb.append(CONTROL_A);
        sb.append(ispUiName);
        return sb.toString();
    }
}