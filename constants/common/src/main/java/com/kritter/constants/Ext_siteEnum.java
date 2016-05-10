package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Ext_siteEnum
{
            get_ext_site_by_pub(0,"get_ext_site_by_pub"),
            get_ext_site_by_site(1,"get_ext_site_by_site"),
            aprrove_ext_site_by_ids(2,"aprrove_ext_site_by_ids"),
            unaprrove_ext_site_by_ids(3,"unaprrove_ext_site_by_ids"),
            get_unapproved_ext_site(4,"get_unapproved_ext_site"),
            get_approved_ext_site(5,"get_approved_ext_site"),
            get_rejected_ext_site(6,"get_rejected_ext_site"),
            get_unapproved_ext_site_by_pub(7,"get_unapproved_ext_site_by_pub"),
            get_approved_ext_site_by_pub(8,"get_approved_ext_site_by_pub"),
            get_rejected_ext_site_by_pub(9,"get_rejected_ext_site_by_pub"),
            get_unapproved_ext_site_by_pub_os(10,"get_unapproved_ext_site_by_pub_os"),
            get_approved_ext_site_by_pub_os(11,"get_approved_ext_site_by_pub_os"),
            get_rejected_ext_site_by_pub_os(12,"get_rejected_ext_site_by_pub_os");
            
            private int code;
            private String name;
            private static Map<Integer, Ext_siteEnum> map = new HashMap<Integer, Ext_siteEnum>();
            static {
                for (Ext_siteEnum val : Ext_siteEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private Ext_siteEnum(int code,String name)
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
            
            public static Ext_siteEnum getEnum(int i)
            {
                return map.get(i);
            }
}
