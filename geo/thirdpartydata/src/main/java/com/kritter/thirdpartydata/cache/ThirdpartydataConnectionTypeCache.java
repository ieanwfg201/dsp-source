package com.kritter.thirdpartydata.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentSkipListMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.abstraction.cache.abstractions.AbstractFileStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.constants.ConnectionType;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.utils.GeoDetectionUtils;
import com.kritter.thirdpartydata.entity.ThirdpartydataConnectionType;

import lombok.Getter;

public class ThirdpartydataConnectionTypeCache extends AbstractFileStatsReloadableCache implements IConnectionTypeDetectionCache {
    private static final String DELIMITER = "";
    private Logger logger;
    @Getter
    private final String name;
    private ConcurrentSkipListMap<BigInteger, ThirdpartydataConnectionType> connectionTypeMap;

    public ThirdpartydataConnectionTypeCache(String name, String loggerName, Properties properties) throws InitializationException {
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

        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            ConcurrentSkipListMap<BigInteger, ThirdpartydataConnectionType> tempConnectionTypeMap = 
                    new ConcurrentSkipListMap<BigInteger, ThirdpartydataConnectionType>();
            String str;
            while((str = br.readLine()) != null){
                String strSplit[] =  str.split(DELIMITER);
                if(strSplit.length==3){
                    try{
                        if (strSplit[0].contains(":")){
                            BigInteger startIp = GeoDetectionUtils.fetchBigIntValueForIPV6(strSplit[0]);
                            BigInteger endIp = GeoDetectionUtils.fetchBigIntValueForIPV6(strSplit[1]);
                            ThirdpartydataConnectionType tpdConnectionType = new ThirdpartydataConnectionType();
                            tpdConnectionType.setConnectionTypeInt(Integer.parseInt(strSplit[2]));
                            tpdConnectionType.setEndIp(endIp);
                            tempConnectionTypeMap.put(startIp, tpdConnectionType);
                        }else{
                            // ipv4
                            long startIp = GeoDetectionUtils.fetchLongValueForIPV4(strSplit[0]);
                            long endIp = GeoDetectionUtils.fetchLongValueForIPV4(strSplit[1]);
                            ThirdpartydataConnectionType tpdConnectionType = new ThirdpartydataConnectionType();
                            tpdConnectionType.setConnectionTypeInt(Integer.parseInt(strSplit[2]));
                            tpdConnectionType.setEndIp(BigInteger.valueOf(endIp));
                            tempConnectionTypeMap.put(BigInteger.valueOf(startIp), tpdConnectionType);
                        }

                    }catch(Exception e){
                        logger.error("Error in ip conversion: {} inside ThirdpartydataConnectionTypeCache", str,e);
                    }
                }
            }
            connectionTypeMap = tempConnectionTypeMap;
            logger.debug("Successfully refreshed {} cache", this.getName());
        } catch (Exception ioe) {
            logger.error("Some error handling the file for maxmind netspeed database. Please check the file");
            throw new RefreshException();
        }finally{
            if(fr != null){
                try {
                    fr.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
    }


    public ConnectionType getConnectionType(String ipAddress) {
        try{
            BigInteger ip;
            if (ipAddress.contains(":")){
                ip = GeoDetectionUtils.fetchBigIntValueForIPV6(ipAddress);
            }else{
                // ipv4
                ip = BigInteger.valueOf(GeoDetectionUtils.fetchLongValueForIPV4(ipAddress));
            }
            Map.Entry<BigInteger, ThirdpartydataConnectionType> entry = connectionTypeMap.floorEntry(ip);
            if (entry != null && ip.compareTo(entry.getValue().getEndIp()) <1) {
                int i = entry.getValue().getConnectionTypeInt();
                if (i == 2) {
                    logger.debug("Connection type for ip {} is {}", ipAddress, ConnectionType.WIFI);
                    return ConnectionType.WIFI;
                }
                if (i == 3 || i == 4 || i == 5 || i == 6) {
                    logger.debug("Connection type for ip {} is {}", ipAddress, ConnectionType.CARRIER);
                    return ConnectionType.CARRIER;
                }
            }
            return ConnectionType.UNKNOWN;
        }catch(Exception e){
            logger.error("error in ip conversion {}", ipAddress);
        }
        return ConnectionType.UNKNOWN;
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
