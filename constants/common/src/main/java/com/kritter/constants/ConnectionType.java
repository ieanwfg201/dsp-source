package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
* Created by kshitij on 4/5/15.
*/
public enum ConnectionType {
	/** Connection type greater than 50 is not honored . Used for build purposes*/
    UNKNOWN("Unknown", (short)0),
    Ethernet("Ethernet", (short)1),
    WIFI("WIFI", (short)2),
    MobiledataUnknown("MobiledataUnknown", (short)3),
    MobiledataTwoG("MobiledataTwoG", (short)4),
    MobiledataThreeG("MobiledataThreeG", (short)5),
    MobiledataFourG("MobiledataFourG", (short)6),
    CARRIER("Carrier", (short)51);


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
