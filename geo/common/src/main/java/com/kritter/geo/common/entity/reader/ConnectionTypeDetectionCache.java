package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.abstractions.AbstractFileStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.constants.ConnectionType;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.ConnectionTypeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Loads up the Maxmind netspeed database from maxmind database file.
 * The file type is *.mmdb
 * Loads up and invokes the maxmind api to get the connection type
 */
public class ConnectionTypeDetectionCache extends AbstractFileStatsReloadableCache {
    private Logger logger;
    @Getter
    private final String name;
    private DatabaseReader maxmindDbReader;

    public ConnectionTypeDetectionCache(String name, String loggerName, Properties properties) throws InitializationException {
        super(LoggerFactory.getLogger(loggerName), properties);
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    protected void refreshFile(File file) throws RefreshException {
        if(file == null) {
            logger.error("File provided for refresh is null! Check if the maxmind netspeed database file exists at the correct location.");
            return;
        }

        try {
            DatabaseReader tempMaxmindDbReader = new DatabaseReader.Builder(file).build();
            maxmindDbReader = tempMaxmindDbReader;
            logger.debug("Successfully refreshed {} cache", this.getName());
        } catch (IOException ioe) {
            logger.error("Some error handling the file for maxmind netspeed database. Please check the file");
            throw new RefreshException();
        }
    }

    private ConnectionTypeResponse.ConnectionType getConnectionTypeForIp(InetAddress address) throws IOException, GeoIp2Exception {
        ConnectionTypeResponse response = maxmindDbReader.connectionType(address);
        ConnectionTypeResponse.ConnectionType connectionType = response.getConnectionType();
        return connectionType;
    }

    public ConnectionType getConnectionType(String ipAddress) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException uhe) {
            logger.error("Unknown host {} for connection type detection", ipAddress);
            return ConnectionType.UNKNOWN;
        }

        try {
            ConnectionTypeResponse.ConnectionType type = getConnectionTypeForIp(address);
            if (type == ConnectionTypeResponse.ConnectionType.CABLE_DSL || type == ConnectionTypeResponse.ConnectionType.CORPORATE || type == ConnectionTypeResponse.ConnectionType.DIALUP) {
                logger.debug("Connection type for ip {} is {}", ipAddress, ConnectionType.WIFI);
                return ConnectionType.WIFI;
            }

            if (type == ConnectionTypeResponse.ConnectionType.CELLULAR) {
                logger.debug("Connection type for ip {} is {}", ipAddress, ConnectionType.CARRIER);
                return ConnectionType.CARRIER;
            }
        } catch (GeoIp2Exception gie) {
            logger.debug("Could not find connection type for ip {} as it is not present in the maxmind database", address);
        } catch (IOException ioe) {
            logger.debug("IO Exception in connection type detection cache. {}", ioe.getMessage());
        }

        return ConnectionType.UNKNOWN;
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
