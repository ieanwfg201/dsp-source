package com.kritter.device.common.realtime.cache;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class will keep configured maximum number of user agents with its detected properties
 * and usage metrics.
 */
public class RealTimeUsageHandsetCache
{
    private ConcurrentHashMap<String,RealTimeHandset> realTimeHandsetCache;
    private Logger logger;
    private int maxCacheSize;
    private int percentOfMedianToCleanUp;
    private Timer timer;
    private TimerTask timerTask;

    public RealTimeUsageHandsetCache(String loggerName,
                                     int maxCacheSize,
                                     int percentOfMedianToCleanUp,
                                     int cleanUpFrequency)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.maxCacheSize = maxCacheSize;
        this.percentOfMedianToCleanUp = percentOfMedianToCleanUp;
        this.realTimeHandsetCache = new ConcurrentHashMap<String, RealTimeHandset>();

        this.timer = new Timer();
        this.timerTask = new RealTimeUsageHandsetCleanUpJob();
        this.timer.schedule(this.timerTask,cleanUpFrequency,cleanUpFrequency);
    }

    public boolean doesRealTimeHandsetExist(String userAgent)
    {
        return realTimeHandsetCache.containsKey(userAgent);
    }

    public boolean addRealTimeHandset(String userAgent,String brandName,String modelName,String marketingName,
                                      String deviceOs,String deviceOsVersion,String browserName,String browserVersion,
                                      boolean isTablet,boolean isWirelessDevice,boolean j2meMidp2,
                                      String resolutionWidth,String resolutionHeight,boolean ajaxSupportJava,
                                      boolean isBot,String deviceType,long detectionTimeMillis)
    {
        if(this.realTimeHandsetCache.size() >= this.maxCacheSize)
            return false;

        if(doesRealTimeHandsetExist(userAgent))
            return true;

        RealTimeHandset realTimeHandset = new RealTimeHandset();

        realTimeHandset =    realTimeHandset.setBrandName(brandName)
                                            .setModelName(modelName)
                                            .setMarketingName(marketingName)
                                            .setDeviceOs(deviceOs)
                                            .setDeviceOsVersion(deviceOsVersion)
                                            .setBrowserName(browserName)
                                            .setBrowserVersion(browserVersion)
                                            .setTablet(isTablet)
                                            .setWirelessDevice(isWirelessDevice)
                                            .setResolutionWidth(resolutionWidth)
                                            .setResolutionHeight(resolutionHeight)
                                            .setJ2meMidp2(j2meMidp2)
                                            .setAjaxSupportJava(ajaxSupportJava)
                                            .setBot(isBot)
                                            .setDeviceType(deviceType);

        realTimeHandset.setDetectionTimeMillis(detectionTimeMillis);
        realTimeHandset.incrementUsageCount();
        this.realTimeHandsetCache.putIfAbsent(userAgent,realTimeHandset);

        return true;
    }

    public RealTimeHandset getRealTimeHandset(String userAgent)
    {
        return this.realTimeHandsetCache.get(userAgent);
    }

    /**
     * This class is responsible for cleaning up unused handset data.
     */
    private class RealTimeUsageHandsetCleanUpJob extends TimerTask
    {
        private Logger cacheLogger = LogManager.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                cleanUpCache();
            }
            catch (Exception e)
            {
                cacheLogger.error("Exception inside RealTimeUsageHandsetCleanUpJob",e);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.realTimeHandsetCache = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }

    private void cleanUpCache() throws Exception
    {
        long medianDenominator = this.realTimeHandsetCache.size() + 1;
        long totalSum = 0;

        for(Map.Entry<String,RealTimeHandset> entry : this.realTimeHandsetCache.entrySet())
        {
            totalSum += entry.getValue().getUsageCount();
        }

        //print statistics before cleanup.
        logger.error("Before cleanup RealTimeUsageHandsetCache Stats are: total cache size: {} , total requests " +
                     "handled since beginning: {} ", this.realTimeHandsetCache.size(), totalSum);

        long median = totalSum / medianDenominator;
        long percentageBelowCleanUp = median/this.percentOfMedianToCleanUp;

        ConcurrentHashMap<String,RealTimeHandset> cacheUpdated = new ConcurrentHashMap<String, RealTimeHandset>();
        for(Map.Entry<String,RealTimeHandset> entry : this.realTimeHandsetCache.entrySet())
        {
            if(entry.getValue().getUsageCount() < percentageBelowCleanUp &&
               entry.getValue().getDetectionTimeMillis() < 10)
            {
                logger.debug("UA: [{}] , removed because usage count: {} and percentageBelowCleanUp : {} ",
                              entry.getKey(),entry.getValue().getUsageCount(),percentageBelowCleanUp);
                continue;
            }
            else
                cacheUpdated.put(entry.getKey(),entry.getValue());
        }

        if(null != cacheUpdated && cacheUpdated.size() > 0)
            this.realTimeHandsetCache = cacheUpdated;

        //print statistics after cleanup.
        logger.error("After cleanup RealTimeUsageHandsetCache Stats are: total cache size: {} , total requests " +
                     "handled since beginning: {} ", this.realTimeHandsetCache.size(), totalSum);
    }
}