package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the Database metadata table inclusion_exclusion_type.
 * The enum names should ideally match the entries in column "name" inside the database.
 */
public enum InclusionExclusionType {
    None(0),
    Inclusion(1),
    Exclusion(2);

    private int code;

    private static Map<Integer, InclusionExclusionType> map = new HashMap<Integer, InclusionExclusionType>();
    static {
        for(InclusionExclusionType inclusionExclusionType : InclusionExclusionType.values()) {
            map.put(inclusionExclusionType.code, inclusionExclusionType);
        }
    }

    InclusionExclusionType(int code) {
        this.code = code;
    }
    public int getCode()
    {
        return this.code;
    }

    public static InclusionExclusionType getEnum(int code) {
        return map.get(code);
    }
}
