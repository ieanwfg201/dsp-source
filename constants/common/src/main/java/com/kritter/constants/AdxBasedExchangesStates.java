package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AdxBasedExchangesStates
{
	BRINGINQUEUE(0,"BRINGINQUEUE"),
	READYTOSUBMIT(1,"READYTOSUBMIT"),
	SUBMITTED(2,"SUBMITTED"),
	REMOVEFROMQUEUE(3,"REMOVEFROMQUEUE"),
	GETINITIATED(4,"GETINITIATED"),
	GETOBTAINED(5,"GETOBTAINED"),
	UPLOADINITIATED(6,"UPLOADINITIATED"),
	UPLOADSUCCESS(7,"UPLOADSUCCESS"),
	ERROR(8,"ERROR"),
	REFUSED(9,"REFUSED"),
	APPROVING(10,"APPROVING"),
	APPROVED(11,"APPROVED"),
	UPLOADFAIL(12,"UPLOADFAIL"),
	AUGITORGETFAIL(13,"AUGITORGETFAIL");

            private int code;
            private String name;
            private static Map<Integer, AdxBasedExchangesStates> map = new HashMap<Integer, AdxBasedExchangesStates>();
            static {
                for (AdxBasedExchangesStates val : AdxBasedExchangesStates.values()) {
                    map.put(val.code, val);
                }
            }

            
            private AdxBasedExchangesStates(int code,String name)
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
            
            public static AdxBasedExchangesStates getEnum(int i)
            {
                return map.get(i);
            }
}
