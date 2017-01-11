package com.kritter.device.common.fifty1degrees;

import com.kritter.abstraction.cache.abstractions.AbstractFileStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.metrics.cache.MetricsCache;
import fiftyone.mobile.detection.Match;
import fiftyone.mobile.detection.factories.MemoryFactory;
import fiftyone.mobile.detection.factories.StreamFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import fiftyone.mobile.detection.Provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.IOUtils;

public class FiftyOneDegreesFileCache extends AbstractFileStatsReloadableCache
{
    public static final String HARDWARE_VENDOR_KEY = "HardwareVendor";
    public static final String HARDWARE_MODEL_KEY = "HardwareModel";
    public static final String HARDWARE_MARKETING_KEY = "HardwareName";
    public static final String DEVICE_OS_KEY = "PlatformName";
    public static final String DEVICE_OS_VERSION_KEY = "PlatformVersion";
    public static final String BROWSER_NAME_KEY = "BrowserName";
    public static final String BROWSER_VERSION_KEY = "BrowserVersion";
    public static final String IS_TABLET_KEY = "IsTablet";
    public static final String IS_WIRELESS_KEY = "IsMobile";
    public static final String RESOLUTION_WIDTH_KEY = "ScreenPixelsWidth";
    public static final String RESOLUTION_HEIGHT_KEY = "ScreenPixelsHeight";
    public static final String J2ME_MIDP2_KEY = "MIDP";
    public static final String AJAX_SUPPORT_JAVA_KEY = "Javascript";
    public static final String IS_BOT_KEY = "IsCrawler";
    public static final String DEVICE_TYPE_KEY = "DeviceType";

    private Logger logger;
    @Getter
    private final String name;
    private Provider provider;
    private MetricsCache metricsCache;

    public FiftyOneDegreesFileCache(String name, String loggerName, Properties properties, MetricsCache metricsCache)
                                                                                    throws InitializationException
    {
        super(LogManager.getLogger(loggerName), properties);
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.metricsCache = metricsCache;
    }

    @Override
    protected void refreshFile(File file) throws RefreshException
    {
        logger.debug("Inside refresh file of {}", this.name);

        if(file == null)
        {
            logger.error("File provided for fifty one degrees file cache refresh is null");
            return;
        }

        FileInputStream fiftyOneDegreesDataFileInputStream = null;

        try
        {

            fiftyOneDegreesDataFileInputStream = new FileInputStream(file);
            Provider updatedProvider = new Provider(MemoryFactory.create(fiftyOneDegreesDataFileInputStream));

            //read bytes from the input data file.
            //byte[] data = IOUtils.toByteArray(fiftyOneDegreesDataFileInputStream);
            //Provider updatedProvider    = new Provider(StreamFactory.create(data));

            //close previous data set.
            if(null != provider && null != provider.dataSet && null != updatedProvider)
            {
                provider.dataSet.close();
                provider = null;
            }

            if(null != updatedProvider)
                provider = updatedProvider;

            logger.debug("Refreshed FiftyOneDegreesFileCache as part of its timer task.");
        }
        catch (IOException ioe)
        {
            throw new RefreshException(ioe);
        }
        finally
        {
            try
            {
                if(null != fiftyOneDegreesDataFileInputStream)
                    fiftyOneDegreesDataFileInputStream.close();

                logger.debug("Successfully closed input stream as well inside FiftyOneDegreesFileCache");
            }
            catch (IOException ioe)
            {
                logger.error("IOException in closing 51 degrees data file input stream ", ioe);
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

    public HandsetInfo getHandsetInfo(String userAgent)
    {
        metricsCache.incrementInvocations(getName());
        if(userAgent == null || null == provider || null == provider.dataSet || provider.dataSet.getDisposed())
        {
            logger.debug("Null user agent or Provider not available inside FiftyOneDegreesFileCache, cannot detect.");
            return null;
        }

        HandsetInfo handsetInfo = null;

        try
        {
            long beginTime = System.nanoTime();
            Match match = provider.match(userAgent);

            handsetInfo = new HandsetInfo();
            handsetInfo.setBrandName(match.getValues(HARDWARE_VENDOR_KEY).toString())
                    .setModelName(match.getValues(HARDWARE_MODEL_KEY).toString())
                    .setMarketingName(match.getValues(HARDWARE_MARKETING_KEY).toString())
                    .setDeviceOs(match.getValues(DEVICE_OS_KEY).toString())
                    .setDeviceOsVersion(match.getValues(DEVICE_OS_VERSION_KEY).toString())
                    .setBrowserName(match.getValues(BROWSER_NAME_KEY).toString())
                    .setBrowserVersion(match.getValues(BROWSER_VERSION_KEY).toString())
                    .setTablet(match.getValues(IS_TABLET_KEY).toBool())
                    .setWirelessDevice(match.getValues(IS_WIRELESS_KEY).toBool())
                    .setResolutionWidth(match.getValues(RESOLUTION_WIDTH_KEY).toString())
                    .setResolutionHeight(match.getValues(RESOLUTION_HEIGHT_KEY).toString())
                    .setJ2meMidp2(match.getValues(J2ME_MIDP2_KEY).toDouble() == 2.0)
                    .setAjaxSupportJava(match.getValues(AJAX_SUPPORT_JAVA_KEY).toBool())
                    .setBot(match.getValues(IS_BOT_KEY).toBool())
                    .setDeviceType(match.getValues(DEVICE_TYPE_KEY).toString());

            long endTime = System.nanoTime();
            metricsCache.incrementLatency(getName(), (endTime - beginTime + 500) / 1000);

            logger.debug("Handset info for user agent : {} is {}", handsetInfo);
        }
        catch (IOException ioe)
        {
            logger.error("Error finding handset info for user agent : {}. Error : {}", userAgent, ioe);
        }

        return handsetInfo;
    }

    @Override
    protected void release() throws ProcessingException
    {
        try
        {
            if (provider != null)
            {
                provider.dataSet.close();
            }
        }
        catch (IOException ioe)
        {
            logger.error("Exception releasing resources in release of FiftyOneDegreesFileCache.", ioe);
        }
    }
}
