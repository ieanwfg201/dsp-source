package com.kritter.constants;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum MarketPlace
{
    CPC(1, "CPC", "Cost per click in dollars"),
    CPM(2, "CPM", "Cost per thousand impressions in dollars"),
    CPD(3, "CPD", "Cost per download in dollars");

    @Getter private int code;
    @Getter private String label;
    @Getter private String description;
    /**
     * A mapping between the integer code and its corresponding Ad Status to facilitate lookup by code.
     */
    private static Map<Integer, MarketPlace> codeToTerminationReasonMapping;

    private MarketPlace(int code, String label, String description)
    {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public static MarketPlace getMarketPlace(int i)
    {
        if(codeToTerminationReasonMapping == null)
            initMapping();
        return codeToTerminationReasonMapping.get(i);
    }

    private static void initMapping()
    {
        codeToTerminationReasonMapping = new HashMap<Integer, MarketPlace>();
        for (MarketPlace s : values())
            codeToTerminationReasonMapping.put(s.code, s);
    }
/*
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("MarketPlace");
        sb.append("{code=").append(code);
        sb.append(", label='").append(label).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }*/
}
