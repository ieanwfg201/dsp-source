package com.kritter.geo.common.entity.reader;

import java.net.InetAddress;

import com.kritter.constants.ConnectionType;

public interface IConnectionTypeDetectionCache {
    ConnectionType getConnectionType(String ipAddress);
}
