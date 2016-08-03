package com.kritter.device.mad;

import com.kritter.abstraction.cache.abstractions.AbstractFileStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MadFileCache extends AbstractFileStatsReloadableCache {
    private static final String DELIMITER = "";
    private Logger logger;
    @Getter
    private final String name;
    private ConcurrentHashMap<String, HandsetInfo> dataMap = null;

    public MadFileCache(String name, String loggerName, Properties properties)
            throws InitializationException {
        super(LoggerFactory.getLogger(loggerName), properties);
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = name;
    }

    @Override
    protected void refreshFile(File file) throws RefreshException {
        logger.debug("Inside refresh file of {}", this.name);
        if(file == null) {
            logger.error("File provided for mad one degrees file cache refresh is null");
            return;
        }
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String str;
            ConcurrentHashMap<String, HandsetInfo> tempdataMap = new ConcurrentHashMap<String, MadFileCache.HandsetInfo>();
            while((str = br.readLine()) != null){
                /**
                 * ua<CTRLA>md5(ua)<CTRLA>is_tablet<CTRLA>is_wireless_device<CTRLA>brand_name
                 * <CTRLA>model_name<CTRLA>device_os<CTRLA>device_os_version<CTRLA>resolution_height
                 * <CTRLA>resolution_width
                 */
                String strSplit[] = str.split(DELIMITER);
                if(strSplit.length == 10){
                    HandsetInfo handSetInfo = new HandsetInfo();
                    handSetInfo.setAjaxSupportJava(true);
                    handSetInfo.setBot(false);
                    handSetInfo.setBrandName(strSplit[4]);
                    handSetInfo.setBrowserName(null);
                    handSetInfo.setBrowserVersion(null);
                    handSetInfo.setDeviceOs(strSplit[6]);
                    handSetInfo.setDeviceOsVersion(strSplit[7]);
                    handSetInfo.setDeviceType(null);
                    handSetInfo.setJ2meMidp2(false);
                    handSetInfo.setMarketingName("");
                    handSetInfo.setModelName(strSplit[5]);
                    handSetInfo.setResolutionHeight(strSplit[8]);
                    handSetInfo.setResolutionWidth(strSplit[9]);
                    if("Y".equals(strSplit[2])){
                        handSetInfo.setTablet(true);
                    }else{
                        handSetInfo.setTablet(false);
                    }
                    
                    if("Y".equals(strSplit[3])){
                        handSetInfo.setWirelessDevice(true);
                    }else{
                        handSetInfo.setWirelessDevice(false);
                    }
                }

            }
            dataMap=tempdataMap;
            logger.debug("Refreshed MadFileCache");
        } catch (Exception ioe) {
            throw new RefreshException(ioe);
        }finally{
            if(fr!=null){
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

    @Accessors(chain = true)
    @ToString
    @EqualsAndHashCode
    @Getter @Setter
    public static class HandsetInfo {
        private String brandName;
        private String modelName;
        private String marketingName;
        private String deviceOs;
        private String deviceOsVersion;
        private String browserName;
        private String browserVersion;
        private boolean isTablet;
        private boolean isWirelessDevice;
        private boolean j2meMidp2;
        private String resolutionWidth;
        private String resolutionHeight;
        private boolean ajaxSupportJava;
        private boolean isBot;
        private String deviceType;
    }

    public HandsetInfo getHandsetInfo(String userAgent) {
        if(userAgent == null) {
            logger.debug("null user agent sent to query");
            return null;
        }
        HandsetInfo handsetInfo = null;
        if(dataMap != null){
            handsetInfo = dataMap.get(userAgent);
        }
        logger.debug("Handset info for user agent : {} is {}", handsetInfo);
        return handsetInfo;
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
