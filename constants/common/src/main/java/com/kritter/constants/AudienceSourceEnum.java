package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AudienceSourceEnum
{

    Madhouse(1,"Madhouse"),
    TalkingData(2,"TalkingData"),
    UnionPay(3,"UnionPay"),
    Admaster(4,"Admaster"),
    QianXun(5,"QianXun");


    private int code;
    private String name;
    private static Map<Integer, AudienceSourceEnum> map = new HashMap<Integer, AudienceSourceEnum>();
    static {
        for (AudienceSourceEnum val : AudienceSourceEnum.values()) {
            map.put(val.code, val);
        }
    }

    private AudienceSourceEnum(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getName()
            {
                return this.name;
            }

    public int getCode()
            {
                return this.code;
            }
            
    public static AudienceSourceEnum getEnum(int i)
            {
                return map.get(i);
            }
}
