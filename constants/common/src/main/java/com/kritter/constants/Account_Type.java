package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Account_Type
{
            root(1,"root"),
            directpublisher(2,"directpublisher"),
            directadvertiser(3,"directadvertiser"),
            exchange(4,"exchange"),
            atd(5,"atd"),
            adops(6,"adops"),
            pubops(7,"pubops"),
            pubbd(8,"pubbd"),
            adsales(9,"adsales"),
            adnetwork(10,"adnetwork");

            private int id;
            private String name;
            

            private static Map<Integer, Account_Type> map = new HashMap<Integer, Account_Type>();
            static {
                for (Account_Type val : Account_Type.values()) {
                    map.put(val.id, val);
                }
            }


            private Account_Type(int id,String name)
            {
                this.id = id;
                this.name = name;
            }

            public String getName()
            {
                return this.name;
            }

            public int getId()
            {
                return this.id;
            }
            public static Account_Type getEnum(int i)
            {
                return map.get(i);
            }
}
