package com.kritter.device.wurfl;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.metrics.cache.MetricsCache;
import com.kritter.constants.DeviceType;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetDataTopLevelKey;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.device.common.index.HandsetMakeModelSecondaryIndex;
import com.kritter.device.common.index.HandsetSecondaryIndexBuilder;
import com.kritter.utils.databasemanager.DatabaseManager;
import lombok.Setter;
import net.sourceforge.wurfl.core.Device;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class loads handset data from database on a regular basis ,
 * map to form is
 * function(version,data_source,external_id) => handset_detection_data.
 * Using above data we can detect as: (For now only wurfl is used as database)
 */
public class HandsetDetector extends AbstractDBStatsReloadableQueryableCache<HandsetDataTopLevelKey,HandsetMasterData>
    implements HandsetDetectionProvider
{

    private static Logger logger = LogManager.getLogger("cache.logger");
    private final String name;
    /**
     * keep latest wurfl version of the file, as well as keep the full
     * path of the latest file for detection to work. these can be static
     * members, so that they can be set by the populator without the need
     * of this instance.
     */
    private static final String WURFL_DATA_SOURCE_VALUE = "WURFL";
    private static final String IS_BOT_CAPABILITY_NAME = "is_bot";
    private static final String IS_JAVASCRIPT_ENABLED_CAPABILITY_NAME = "ajax_support_javascript";
    private static final String IS_TABLET_CAPABILITY_NAME = "is_tablet";
    private static final String IS_WIRELESS_DEVICE_CAPABILITY_NAME = "is_wireless_device";
    private static final String DEVICE_CLAIMS_WEB_SUPPORT_CAPABILITY_NAME = "device_claims_web_support";

    private static WurflFilesManager wurflFilesManager;
    private MetricsCache metricsCache;

    public HandsetDetector(List<Class> secIndexKeyClassList,
                           Properties props,
                           DatabaseManager dbMgr,
                           String cacheName,
                           WurflFilesManager wurflFilesManager,
                           MetricsCache metricsCache) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr,prepareQueryMapToSetAndInitializeWurflFileManager(wurflFilesManager));
        this.name = cacheName;
        this.metricsCache = metricsCache;
    }

    private static Map<Integer,Object> prepareQueryMapToSetAndInitializeWurflFileManager(WurflFilesManager wurflFilesManager)
    {
        HandsetDetector.wurflFilesManager = wurflFilesManager;
        Map<Integer,Object> map = new HashMap<Integer, Object>();
        map.put(1,HandsetDetector.wurflFilesManager.getLatestWurflFileVersion());
        return map;
    }

    /*implement method where wurfl version is modified at each run of the timer task*/
    @Override
    public Map<Integer,Object> modifyQueryParametersMap()
    {
        Map<Integer,Object> map = new HashMap<Integer, Object>();
        map.put(1,HandsetDetector.wurflFilesManager.getLatestWurflFileVersion());
        return map;
    }

    @Override
    protected HandsetMasterData buildEntity(ResultSet rs) throws RefreshException
    {
        try
        {
            Long internalId = (Long) rs.getObject("internal_id");
            String externalId = rs.getString("external_id");
            String source = rs.getString("source");
            Integer version = (Integer) rs.getObject("version");
            Integer manufacturerId = (Integer) rs
                    .getObject("manufacturer_id");
            Integer modelId = (Integer) rs.getObject("model_id");
            String marketingName = rs.getString("marketing_name");
            Integer deviceOsId = (Integer) rs.getObject("device_os_id");
            String deviceOsVersion = rs.getString("device_os_version");
            Integer deviceBrowserId = (Integer) rs
                    .getObject("device_browser_id");
            String deviceBrowserVersion = rs
                    .getString("device_browser_version");
            String deviceCapabilityJSON = rs
                    .getString("device_capability_json");
            String modifiedBy = rs.getString("modified_by");
            Timestamp modifiedOn = rs.getTimestamp("modified_on");

            HandsetDataTopLevelKey key = new HandsetDataTopLevelKey(
                    externalId, source, version);

            HandsetMasterData handsetMasterData = new HandsetMasterData(internalId,
                    key, manufacturerId, modelId, marketingName,
                    deviceOsId, deviceOsVersion, deviceBrowserId,
                    deviceBrowserVersion, deviceCapabilityJSON, modifiedBy,
                    modifiedOn);

            logger.debug("Inside {}, new handset master data object created : {}", getName(), handsetMasterData);
            return handsetMasterData;
        }
        catch (Exception e)
        {
            throw new RefreshException("Inside HandsetDetector Cache,Exception in getting handsetmasterdata",e);
        }
    }

    /**
     * This function takes input as user agent and detected the corresponding
     * external_id as specified in latest wurfl file.
     * Using external id we get handset detection data from database, so that
     * internal ids of different attributes like manufacturer,model,os,browser
     * etc. are known for usage in postimpression url formation and as well
     * for reporting purposes.
     * @param userAgent
     * @return
     */
    public HandsetMasterData detectHandsetForUserAgent(String userAgent) throws Exception
    {
        metricsCache.incrementInvocations(getName());
        if(null == wurflFilesManager || null == wurflFilesManager.getWurflEngine())
            throw new Exception("Wurfl Manager for handset detection has not been initialized properly");

        long beginTime = System.nanoTime();
        HandsetMasterData handsetMasterData = null;
        Device device = wurflFilesManager.getWurflEngine().getDeviceForRequest(userAgent);

        Boolean isBot = "is_bot".equalsIgnoreCase(device.getCapability(IS_BOT_CAPABILITY_NAME)) ?
                        true : false;

        String externalId = device.getId();
        logger.debug("External id detected: {}", externalId);

        /****************************Detect other attributes required at runtime.*************************************/
        String javascriptAttributeValue = device.getCapability(IS_JAVASCRIPT_ENABLED_CAPABILITY_NAME);
        boolean isJavascriptEnabled = false;

        if(null != javascriptAttributeValue)
            isJavascriptEnabled = Boolean.valueOf(javascriptAttributeValue);

        DeviceType deviceType = null;

        String isWirelessDevice = device.getCapability(IS_WIRELESS_DEVICE_CAPABILITY_NAME);
        String deviceClaimsWebSupport = device.getCapability(DEVICE_CLAIMS_WEB_SUPPORT_CAPABILITY_NAME);

        if(
           null != isWirelessDevice &&
           null != deviceClaimsWebSupport &&
           "false".equalsIgnoreCase(isWirelessDevice) &&
           "true".equalsIgnoreCase(deviceClaimsWebSupport)
          )
            deviceType = DeviceType.DESKTOP;
        else if(
                null != isWirelessDevice &&
                null != deviceClaimsWebSupport &&
                "true".equalsIgnoreCase(isWirelessDevice) &&
                "true".equalsIgnoreCase(deviceClaimsWebSupport)
               )
            deviceType = DeviceType.MOBILE;
        /*************************************************************************************************************/

        try
        {
            if(null != externalId)
            {
                if(logger.isDebugEnabled())
                    logger.debug("External id looked up using wurfl detection is {}", externalId);

                HandsetDataTopLevelKey key = new HandsetDataTopLevelKey(externalId,
                        WURFL_DATA_SOURCE_VALUE, wurflFilesManager.getLatestWurflFileVersion());

                handsetMasterData = this.query(key);

                /*************************set other attributes values required at run time***************************/
                if(null != isBot)
                    handsetMasterData.setBot(isBot);
                else
                    handsetMasterData.setBot(false);

                handsetMasterData.setDeviceJavascriptCompatible(isJavascriptEnabled);

                handsetMasterData.setDeviceType(deviceType);
                /************************************run time attributes setting done.*******************************/
            }
        }
        catch (Exception e)
        {
            logger.error("Exception in handset detection inside HandsetDetector,returning null handset",e);
            return null;
        }

        long endTime = System.nanoTime();
        metricsCache.incrementLatency(getName(), (endTime - beginTime + 500) / 1000);

        return handsetMasterData;
    }

    @Override
    protected void release() throws ProcessingException
    {
        this.wurflFilesManager.releaseResources();
    }

    //Secondary indexes might be required while integrating exchange requests, as when model,
    //manufacturer,os, browser names come into picture, then key on incoming request data
    //could be formed as function(manufacturer_name,model_name,os_name,os_version) to uniquely
    //identify the internal handset id.
    //Update: not required.
    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, HandsetMasterData entity)
    {
        if(className.equals(HandsetMakeModelSecondaryIndex.class))
            return HandsetSecondaryIndexBuilder.getMakeModelSecondaryIndex(entity.getManufacturerId(),
                    entity.getModelId());

        return null;
    }

    @Override
    public String getName()
    {
        return name;
    }
}