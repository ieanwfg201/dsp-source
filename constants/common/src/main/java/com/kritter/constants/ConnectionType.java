package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
* Created by kshitij on 4/5/15.
*/
public enum ConnectionType {
    WIFI("WiFi", (short)1),
    CARRIER("Carrier", (short)2),
    UNKNOWN("Unknown", (short)-1);

    private String connectionTypeName;
    private short id;

    private static Map<Short, ConnectionType> map = new HashMap<Short, ConnectionType>();
    static {
        for (ConnectionType val : ConnectionType.values()) {
            map.put(val.id, val);
        }
    }

    private ConnectionType(String connectionTypeName, short id) {
        this.connectionTypeName = connectionTypeName;
        this.id = id;
    }

    public short getId() { return id; }
    
    public String getName()
    {
        return this.connectionTypeName;
    }
    public static ConnectionType getEnum(short i)
    {
        return map.get(i);
    }

}
