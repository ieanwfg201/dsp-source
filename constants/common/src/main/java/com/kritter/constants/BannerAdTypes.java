package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum BannerAdTypes
{
            NONE(0,"XHTMLTextAd"),
            XHTMLTextAd(1,"XHTMLTextAd"),
            XHTMLBannerAd(2,"XHTMLBannerAd"),
            JavaScriptAd(3,"JavaScriptAd;"),
            iframe(4,"iframe");

            private int code;
            private String name;
            private static Map<Integer, BannerAdTypes> map = new HashMap<Integer, BannerAdTypes>();
            static {
                for (BannerAdTypes val : BannerAdTypes.values()) {
                    map.put(val.code, val);
                }
            }

            private BannerAdTypes(int code,String name)
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
            public static BannerAdTypes getEnum(int i)
            {
                return map.get(i);
            }
}
