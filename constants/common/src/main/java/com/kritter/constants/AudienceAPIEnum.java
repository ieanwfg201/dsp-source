package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AudienceAPIEnum {
    get_audience_by_id_and_deleted(0, "get_audience_by_id_and_deleted"),
    insert_audience_tags(1, "insert_audience_tags"),
    updata_audience_tags(2, "updata_audience_tags"),
    list_all_expired_audience_of_account(3, "list_all_expired_audience_of_account"),
    list_non_expired_audience_by_status(4, "list_audience_by_status"),
    list_non_expired_audience_of_account_by_status(5, "list_audience_of_account_by_status"),
    list_all_non_expired_audience_of_account(6, "list_all_audience_of_account"),
    get_audience_of_account(7, "get_audience_of_account"),
    list_audience_of_account(8, "list_audience_of_account"),
    list_audience_of_accounts(9, "list_audience_of_accounts"),
    list_audience_by_account_ids(10, "list_audience_by_account_ids"),
    list_audience_by_account_ids_with_account_id(11, "list_audience_by_account_ids_with_account_id"),
    get_audience_list_of_account(12, "get_audience_list_of_account");


    private int code;
    private String name;
    private static Map<Integer, AudienceAPIEnum> map = new HashMap<Integer, AudienceAPIEnum>();

    static {
        for (AudienceAPIEnum val : AudienceAPIEnum.values()) {
            map.put(val.code, val);
        }
    }

    private AudienceAPIEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }

    public static AudienceAPIEnum getEnum(int i) {
        return map.get(i);
    }
}
