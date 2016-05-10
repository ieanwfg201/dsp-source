package com.kritter.device.populator;

import com.kritter.constants.HandsetConstants;
import com.kritter.device.HandsetPopulationProvider;
import com.kritter.device.entity.*;
import com.kritter.device.util.DeviceUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;

import lombok.Getter;
import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.GeneralWURFLEngine;
import net.sourceforge.wurfl.core.WURFLEngine;
import net.sourceforge.wurfl.core.WURFLUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads wurfl files directory looking for any new files that might have come.
 * The format of file would be wurfl_version.xml where version = yyyymmdd , an integer
 * value, for example: 20140728, so full name: wurfl_20140728.xml , this class is
 * responsible to check if any file has been modified since last reload time.
 */
public class HandsetDataPopulator implements HandsetPopulationProvider
{
    // Regular expression signifying valid versions. A valid version should be of the form
    // <number>'.'<number>'.'...'.'<number>
    // That is, a number followed by a dot, followed by a number, etc.
    // Thus 1.834.22, 1.1, 5, 2.55.1.3.5231 are valid versions while pocket pc, 1.x, 857., etc are not.
    // Note that a trailing .(dot) is not considered to be a valid pattern. Hence '857.' is not a valid pattern.
    // The regex is equivalent to ([0-9]+.)*[0-9]+.
    private static String versionPatternRegex = "(\\d+.)*\\d+";

    private Logger logger;
    private int sqlInsertQueriesBatchSize;
    private String wurflFilesDirectory;
    private Long lastRefreshStartTime = null;
    private ObjectMapper jacksonObjectMapper;
    private Timer dataUploadToDatabaseTimer;
    private TimerTask dataUploadToDatabaseTimerTask;
    private DatabaseManager databaseManager;
    private Pattern versionPattern;

    //constants for usage here
    private static final String DATA_SOURCE_HANDSET = "WURFL";
    private static final String COMMA = ",";

    //sql query prefixes.
    private static final String QUERY_NEW_MANUFACTURER_DATA_ROW =
            "insert into handset_manufacturer (manufacturer_name,modified_by,modified_on) values ";
    private static final String QUERY_NEW_OS_DATA_ROW =
            "insert into handset_os (os_name,modified_by,modified_on) values ";
    private static final String QUERY_NEW_BROWSER_DATA_ROW =
            "insert into handset_browser (browser_name,modified_by,modified_on) values ";
    private static final String QUERY_NEW_MODEL_DATA_ROW =
            "insert into handset_model (manufacturer_id,model_name,modified_by,modified_on) ";
    private static final String QUERY_NEW_MASTER_DATA_ROW =
            "insert into handset_detection_data (external_id,source,version,manufacturer_id,model_id,marketing_name," +
            "device_os_id,device_os_version,device_browser_id,device_browser_version,device_capability_json," +
            "modified_by,modified_on) values ";

    private static final String QUERY_UPDATE_OS_ID_VERSION = "update handset_os set os_versions = ? where os_id = ?";
    private static final String QUERY_UPDATE_BROWSER_ID_VERSION = "update handset_browser set browser_versions = ? " +
                                                                  "where browser_id = ?";

    // data maps for already present data in database.
    private Map<String, HandsetManufacturerData> manufacturerDataFromDatabase;
    private Map<String, HandsetBrowserData> browserDataFromDatabase;
    private Map<String, HandsetModelData> modelDataFromDatabase;
    private Map<String, HandsetOperatingSystemData> operatingSystemDataFromDatabase;

    //This variable tells whether this machine's/node's application
    //should load data into database or it is just a slave node.
    private boolean dataLoadMasterNode;

    public HandsetDataPopulator(
                                String loggerName,
                                int sqlInsertQueriesBatchSize,
                                String wurflFilesDirectory,
                                int newDataLoadFrequency,
                                DatabaseManager databaseManager,
                                String dataLoadMasterNodeParam,
                                ServerConfig serverConfig
                               ) throws Exception
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.sqlInsertQueriesBatchSize = sqlInsertQueriesBatchSize;
        this.wurflFilesDirectory = wurflFilesDirectory;
        this.jacksonObjectMapper = new ObjectMapper();
        this.databaseManager = databaseManager;
        this.dataLoadMasterNode = Boolean.valueOf(serverConfig.getValueForKey(dataLoadMasterNodeParam));
        this.versionPattern = Pattern.compile(versionPatternRegex);

        //important message so in error mode.
        logger.error("The handset data populator on this node/machine/dsp-application is parent? " +
                      dataLoadMasterNode + " , will be loading data from this node only if true.");

        if(this.dataLoadMasterNode)
        {
            //run data uploading once in the constructor only.
            readWurflDataAndPopulateIntoDatabase();

            //construct timer task and start it.Keep initial delay to repeat frequency.
            //ideally should be an hour or so.
            this.dataUploadToDatabaseTimer = new Timer();

            this.dataUploadToDatabaseTimerTask = new DataUploadToDatabaseTimerTask();
            this.dataUploadToDatabaseTimer.schedule(this.dataUploadToDatabaseTimerTask,
                                                    newDataLoadFrequency,
                                                    newDataLoadFrequency);
        }
    }

    private class DataUploadToDatabaseTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
           try
           {
               readWurflDataAndPopulateIntoDatabase();
           }
           catch (Exception e)
           {
               logger.error("Exception inside timer task to load data into database inside HandsetDataPopulator",e);
           }
        }
    }

    private Connection fetchConnectionToDatabase() throws Exception
    {
        return this.databaseManager.getConnectionFromPool();
    }

    //load into memory the complete handset data.
    private void loadHandsetDataFromDatabase(Connection connectionToDatabase) throws Exception
    {
        manufacturerDataFromDatabase = DeviceUtils.fetchManufacturerDataFromDatabase(connectionToDatabase);
        browserDataFromDatabase = DeviceUtils.fetchBrowserDataFromDatabase(connectionToDatabase);
        modelDataFromDatabase = DeviceUtils.fetchHandsetModelDataFromDatabase(connectionToDatabase);
        operatingSystemDataFromDatabase = DeviceUtils.fetchOperatingSystemDataFromDatabase(connectionToDatabase);
    }

    /**
     * This function reads various wurfl files from a given directory and pushes
     * handset data along with its different attributes into database. In doing
     * so it checks whether the entries are already present in database.
     * @throws Exception
     */
    private void readWurflDataAndPopulateIntoDatabase() throws Exception
    {
        logger.info("Inside readWurflDataAndPopulateIntoDatabase ... Beginning data loading.");

        if(null == wurflFilesDirectory)
            throw new Exception("The configured wurfl file directory is null !!! FATAL !!!");

        Connection conn = null;

        //load data present in database if any.
        try
        {
            conn = fetchConnectionToDatabase();
            loadHandsetDataFromDatabase(conn);
        }
        catch (SQLException sqle)
        {
            throw new Exception("Exception in data loading from database ",sqle);
        }
        finally
        {
            DBExecutionUtils.closeConnection(conn);
        }

        File wurflFilesFolder = new File(wurflFilesDirectory);
        Long maxLastModifiedTimeFromWurflFiles = null;

        WURFLEngine wurflEngine = null;
        WURFLUtils wurflUtils = null;
        Set<String> allDevicesIdSetFromThisWurflRelease = null;
        Iterator<String> deviceIdIterator = null;

        try
        {
            for(File prospectiveWurflFile : wurflFilesFolder.listFiles())
            {
                //check if this wurfl file is processed already or not.
                if(prospectiveWurflFile.getName().endsWith(ApplicationGeneralUtils.PROCESSED_WURFL_FILE_EXTENSION))
                {
                    logger.debug("The wurfl file {} is already processed by HandsetDataPopulator, continuing ...",
                                 prospectiveWurflFile.getName());
                    continue;
                }

                long fileModifyTime = prospectiveWurflFile.lastModified();

                if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
                {
                    logger.debug("File being looked up for wurfl data loading is : {}", prospectiveWurflFile.getPath());

                    int wurflFileVersion = DeviceUtils.verifyWurflFileNameAndFetchVersion(prospectiveWurflFile.getName());

                    if(wurflFileVersion == -1 || wurflFileVersion <= 0)
                    {
                        logger.debug("The file found does not have correct version, file name: {}", prospectiveWurflFile.getName());
                        continue;
                    }

                    //counter to keep batch size check on sql queries to be executed in database.
                    int batchSizeCounter = 0;

                    //data buffers to accumulate sql queries for processing.
                    StringBuffer manufacturerSqlQueriesBuffer = new StringBuffer();
                    StringBuffer modelSqlQueriesBuffer = new StringBuffer();
                    StringBuffer OSSqlQueriesBuffer = new StringBuffer();
                    StringBuffer browserSqlQueriesBuffer = new StringBuffer();
                    StringBuffer masterDataSqlQueriesBuffer = new StringBuffer();
                    Set<HandsetMasterData> masterDataSet = new HashSet<HandsetMasterData>();

                    //other than above keep all versions for all operating systems and browsers.
                    Map<Integer,Set<String>> osIdWithVersionSet      = new HashMap<Integer, Set<String>>();
                    Map<Integer,Set<String>> browserIdWithVersionSet = new HashMap<Integer, Set<String>>();

                    /**
                     * Use current file for WURFL data reading.
                     */
                    wurflEngine = new GeneralWURFLEngine(prospectiveWurflFile.getPath());
                    wurflUtils = wurflEngine.getWURFLUtils();
                    allDevicesIdSetFromThisWurflRelease = fetchAllDevicesIdSetInCurrentWurflFile(wurflUtils);
                    deviceIdIterator = allDevicesIdSetFromThisWurflRelease.iterator();

                    int totalDevices = allDevicesIdSetFromThisWurflRelease.size();
                    int totalDevicesCounter = 0;
                    Connection connection = fetchConnectionToDatabase();

                    logger.debug("Total size of wurfl devices set is {}", totalDevices);

                    Set<HandsetDataTopLevelKey> masterDataKeySet = null;

                    //load master detection data for the current version.
                    try
                    {
                        masterDataKeySet =
                                DeviceUtils.fetchHandsetMasterDataKeySetFromDatabaseForVersion(connection,wurflFileVersion);
                    }
                    catch (SQLException sqle)
                    {
                        throw new Exception("Exception loading master key set data from database ",sqle);
                    }
                    finally
                    {
                        DBExecutionUtils.closeConnection(connection);
                    }

                    while (deviceIdIterator.hasNext())
                    {
                        totalDevicesCounter ++;
                        String deviceId = deviceIdIterator.next();
                        Device device = wurflUtils.getDeviceById(deviceId);
                        String externalId = device.getId();

                        //even if one entry is wrong, throw exception, data must be correctly uploaded.
                        HandsetDataTopLevelKey key = new HandsetDataTopLevelKey(
                                                                                externalId,
                                                                                DATA_SOURCE_HANDSET,
                                                                                wurflFileVersion
                                                                               );

                        if (null != masterDataKeySet && masterDataKeySet.contains(key))
                            continue;

                        // fetch all the required properties from wurfl data.
                        String manufacturerName       = device.getCapability("brand_name");
                        String modelName              = device.getCapability("model_name");
                        String marketingName          = device.getCapability("marketing_name");
                        String operatingSystemName    = device.getCapability("device_os");
                        String operatingSystemVersion = device.getCapability("device_os_version");
                        String mobileBrowserName      = device.getVirtualCapability("advertised_browser");
                        String mobileBrowserVersion   = device.getVirtualCapability("advertised_browser_version");

                        /*If any of important attribute is not found, mark it as unknown*/
                        if(StringUtils.isEmpty(manufacturerName))
                            manufacturerName = HandsetConstants.UNKNOWN_MANUFACTURER_NAME;
                        if(StringUtils.isEmpty(modelName))
                            modelName = HandsetConstants.UNKNOWN_MODEL_NAME;
                        if(StringUtils.isEmpty(operatingSystemName))
                            operatingSystemName = HandsetConstants.UNKNOWN_OS_NAME;
                        if(StringUtils.isEmpty(mobileBrowserName))
                            mobileBrowserName = HandsetConstants.UNKNOWN_BROWSER_NAME;

                        // Check whether the os version and mobile version are valid versions as per the version pattern
                        // If not set them to empty string
                        Matcher osVersionPatternMatcher = versionPattern.matcher(operatingSystemVersion);
                        if(!osVersionPatternMatcher.matches()) {
                            logger.debug("OS version : {}, does not match the required format. Skipping it.",
                                    operatingSystemVersion);
                            operatingSystemVersion = "";
                        }

                        Matcher browserVersionPatternMatcher = versionPattern.matcher(mobileBrowserVersion);
                        if(!browserVersionPatternMatcher.matches()) {
                            logger.debug("Browser version : {}, does not match the required format. Skipping it.",
                                    mobileBrowserVersion);
                            mobileBrowserVersion = "";
                        }

                        // collect data for manufacturer if found to be new.
                        HandsetManufacturerData manufacturerData = manufacturerDataFromDatabase
                                .get(manufacturerName.toLowerCase());

                        // if data not present in database collect it.Also mark it as seen by adding into data map.
                        if (null == manufacturerData)
                        {
                            manufacturerData = new HandsetManufacturerData(manufacturerName, DATA_SOURCE_HANDSET);

                            manufacturerSqlQueriesBuffer.append(manufacturerData.prepareSQLRowForManufacturerData());
                            manufacturerSqlQueriesBuffer.append(COMMA);
                            manufacturerDataFromDatabase.put(manufacturerName.toLowerCase(),manufacturerData);
                        }

                        // collect data for model if found to be new
                        HandsetModelData modelData = modelDataFromDatabase.get(modelName.toLowerCase());

                        if (null == modelData)
                        {
                            modelData = new HandsetModelData(manufacturerData.getManufacturerName(),
                                                             modelName,
                                                             DATA_SOURCE_HANDSET);

                            if(modelSqlQueriesBuffer.length() == 0)
                                modelSqlQueriesBuffer.append(modelData.prepareSQLRowForModelData(true));
                            else
                                modelSqlQueriesBuffer.append(modelData.prepareSQLRowForModelData(false));

                            modelDataFromDatabase.put(modelName.toLowerCase(),modelData);
                        }

                        // collect data for operating system if found new.
                        HandsetOperatingSystemData operatingSystemData = operatingSystemDataFromDatabase
                                .get(operatingSystemName.toLowerCase());
                        if (null == operatingSystemData)
                        {
                            operatingSystemData = new HandsetOperatingSystemData(operatingSystemName,DATA_SOURCE_HANDSET);
                            OSSqlQueriesBuffer.append(operatingSystemData.prepareSQLRowForOperatingSystemData());
                            OSSqlQueriesBuffer.append(COMMA);
                            operatingSystemDataFromDatabase.put(operatingSystemName.toLowerCase(),operatingSystemData);
                        }

                        // collect data for browser if found new.
                        HandsetBrowserData browserData = browserDataFromDatabase
                                .get(mobileBrowserName.toLowerCase());

                        if (null == browserData)
                        {
                            browserData = new HandsetBrowserData(mobileBrowserName, DATA_SOURCE_HANDSET);
                            browserSqlQueriesBuffer.append(browserData.prepareSQLRowForBrowserData());
                            browserSqlQueriesBuffer.append(COMMA);
                            browserDataFromDatabase.put(mobileBrowserName.toLowerCase(),browserData);
                        }

                        // collect data for the master row, if already present flow won't come here.
                        // set null as ids for now(happens by default.).
                        HandsetMasterData
                           masterDataRow = new HandsetMasterData(key,manufacturerData.getManufacturerId(),manufacturerName,
                                modelData.getModelId(), modelName,marketingName,operatingSystemData.getOperatingSystemId(),
                                operatingSystemName,operatingSystemVersion, browserData.getBrowserId(),mobileBrowserName,
                                mobileBrowserVersion,populateHandsetCapabilitiesAndReturnJSON(device),DATA_SOURCE_HANDSET);

                        masterDataSet.add(masterDataRow);

                        batchSizeCounter ++;

                        if(batchSizeCounter == sqlInsertQueriesBatchSize ||
                           totalDevicesCounter== totalDevices)
                        {
                            logger.debug("batch size counter is {} total devices counter is {}", batchSizeCounter, totalDevicesCounter);
                            try
                            {
                                //get database connection, set auto commit to false.
                                connection = fetchConnectionToDatabase();

                                //use this flag to set connection commit
                                //status back to what it is now
                                boolean autoCommitFlag = connection.getAutoCommit();

                                connection.setAutoCommit(false);

                                //process batch sql queries in order.
                                if(manufacturerSqlQueriesBuffer.length() > 0)
                                {
                                    manufacturerSqlQueriesBuffer = manufacturerSqlQueriesBuffer.
                                            deleteCharAt(manufacturerSqlQueriesBuffer.length() - 1);
                                }

                                if(executeSqlBatchQueries(QUERY_NEW_MANUFACTURER_DATA_ROW,
                                                          manufacturerSqlQueriesBuffer.toString(),
                                                          connection) <=0 )
                                    throw new Exception("Data could not be loaded, look for errors in logs.");

                                if(executeSqlBatchQueries(QUERY_NEW_MODEL_DATA_ROW,
                                                          modelSqlQueriesBuffer.toString(),
                                                          connection) <=0 )
                                    throw new Exception("Data could not be loaded, look for errors in logs.");

                                if(OSSqlQueriesBuffer.length() > 0)
                                {
                                    OSSqlQueriesBuffer = OSSqlQueriesBuffer.
                                            deleteCharAt(OSSqlQueriesBuffer.length()-1);
                                }

                                if(executeSqlBatchQueries(QUERY_NEW_OS_DATA_ROW,
                                                          OSSqlQueriesBuffer.toString(),
                                                          connection) <=0 )
                                    throw new Exception("Data could not be loaded, look for errors in logs.");

                                if(browserSqlQueriesBuffer.length() > 0 )
                                {
                                    browserSqlQueriesBuffer = browserSqlQueriesBuffer.
                                            deleteCharAt(browserSqlQueriesBuffer.length() - 1);
                                }

                                if(executeSqlBatchQueries(QUERY_NEW_BROWSER_DATA_ROW,
                                                          browserSqlQueriesBuffer.toString(),
                                                          connection) <=0 )
                                    throw new Exception("Data could not be loaded, look for errors in logs.");

                                //push into database
                                connection.commit();

                                //now fetch data from database to lookup ids.
                                loadHandsetDataFromDatabase(connection);

                                //now iterate over collected handset master data set and set required ids
                                //then insert into database using batch sql query.
                                Iterator<HandsetMasterData> masterDataIterator = masterDataSet.iterator();

                                while (masterDataIterator.hasNext())
                                {
                                    HandsetMasterData handsetMasterData = masterDataIterator.next();
                                    Integer manufacturerId = null;

                                    HandsetManufacturerData handsetManufacturerDataTemp = this.manufacturerDataFromDatabase.
                                            get(handsetMasterData.getManufacturerName().toLowerCase());

                                    if(null!= handsetManufacturerDataTemp)
                                        manufacturerId = handsetManufacturerDataTemp.getManufacturerId();

                                    Integer modelId = null;

                                    HandsetModelData handsetModelDataTemp = this.modelDataFromDatabase.
                                            get(handsetMasterData.getModelName().toLowerCase());

                                    if(null != handsetModelDataTemp)
                                        modelId = handsetModelDataTemp.getModelId();

                                    //get os id and its versions
                                    HandsetOperatingSystemData osDataForUseInMaster = this.operatingSystemDataFromDatabase.
                                            get(handsetMasterData.getOsName().toLowerCase());
                                    Integer operatingSystemId = osDataForUseInMaster.getOperatingSystemId();
                                    String[] osVersionsForUpdationUsage = osDataForUseInMaster.getOperatingSystemVersions();

                                    //get browser id and its versions
                                    HandsetBrowserData browserDataForUseInMaster = this.browserDataFromDatabase.
                                            get(handsetMasterData.getBrowserName().toLowerCase());
                                    Integer browserId = browserDataForUseInMaster.getBrowserId();
                                    String[] browserVersionsForUpdationUsage = browserDataForUseInMaster.getBrowserVersions();

                                    //accumulate new versions for os and browser along with already present version in db.
                                    Set<String> osVersions = osIdWithVersionSet.get(operatingSystemId);
                                    if(null == osVersions)
                                        osVersions = new HashSet<String>();

                                    if(!StringUtils.isEmpty(handsetMasterData.getDeviceOperatingSystemVersion()))
                                        osVersions.add(handsetMasterData.getDeviceOperatingSystemVersion());

                                    if(null != osVersionsForUpdationUsage)
                                        osVersions.addAll(
                                                          new HashSet<String>(
                                                                              Arrays.asList(osVersionsForUpdationUsage)
                                                                             )
                                                         );
                                    osIdWithVersionSet.put(operatingSystemId,osVersions);

                                    Set<String> browserVersions = browserIdWithVersionSet.get(browserId);
                                    if(null == browserVersions)
                                        browserVersions = new HashSet<String>();

                                    if(!StringUtils.isEmpty(handsetMasterData.getDeviceBrowserVersion()))
                                        browserVersions.add(handsetMasterData.getDeviceBrowserVersion());

                                    if(null != browserVersionsForUpdationUsage)
                                        browserVersions.
                                                addAll(
                                                        new HashSet<String>(
                                                                            Arrays.asList(browserVersionsForUpdationUsage)
                                                                           )
                                                      );
                                    browserIdWithVersionSet.put(browserId,browserVersions);

                                    if(
                                       null == manufacturerId    ||
                                       null == modelId           ||
                                       null == operatingSystemId ||
                                       null == browserId
                                      )
                                    {
                                        logger.error("Data line is corrupted inside HandsetDataPopulator , skipping it...");
                                        continue;
                                    }

                                    handsetMasterData.setManufacturerId(manufacturerId);
                                    handsetMasterData.setModelId(modelId);
                                    handsetMasterData.setDeviceOperatingSystemId(operatingSystemId);
                                    handsetMasterData.setDeviceBrowserId(browserId);

                                    masterDataSqlQueriesBuffer.append(handsetMasterData.prepareSQLRowForMasterData());
                                    masterDataSqlQueriesBuffer.append(COMMA);
                                }

                                masterDataSqlQueriesBuffer = masterDataSqlQueriesBuffer.
                                        deleteCharAt(masterDataSqlQueriesBuffer.length()-1);

                                if(executeSqlBatchQueries(QUERY_NEW_MASTER_DATA_ROW,
                                                          masterDataSqlQueriesBuffer.toString(),
                                                          connection) <=0 )
                                {
                                    throw new Exception("Data could not be loaded, look for errors in logs.");
                                }
                                else
                                {
                                    //master data insertion is successful,also update osid and browser id with their
                                    //versions available in the input wurfl database.
                                    PreparedStatement pstmt = null;
                                    try
                                    {
                                        Iterator<Map.Entry<Integer,Set<String>>> osIdWithVersionIterator =
                                                                                     osIdWithVersionSet.entrySet().iterator();

                                        while(osIdWithVersionIterator.hasNext())
                                        {
                                            Map.Entry<Integer,Set<String>> entry = osIdWithVersionIterator.next();

                                            //if version set is empty no need to update.
                                            if(entry.getValue().size() == 0)
                                                continue;

                                            pstmt = connection.prepareStatement(QUERY_UPDATE_OS_ID_VERSION);

                                            pstmt.setString(1,
                                                              ResultSetHelper.prepareStringArrayForMySQLInsertion
                                                                             (
                                                                              entry.getValue().toArray
                                                                                        (
                                                                                         new String[entry.getValue().size()]
                                                                                        )
                                                                             )
                                                           );

                                            pstmt.setInt(2,entry.getKey());

                                            if(pstmt.executeUpdate() <= 0 )
                                                throw new SQLException("OS versions could not be updated");
                                        }
                                    }
                                    catch (SQLException sqle)
                                    {
                                        throw new Exception("OS versions could not be updated," +
                                                            "Aborting handset data population!!!!");
                                    }
                                    finally
                                    {
                                        DBExecutionUtils.closeResources(null,pstmt,null);
                                    }
                                    //done loading osid with their corresponding versions.

                                    //start updating browserid with their corresponding versions.
                                    try
                                    {
                                        Iterator<Map.Entry<Integer,Set<String>>> browserIdWithVersionIterator =
                                                browserIdWithVersionSet.entrySet().iterator();

                                        while(browserIdWithVersionIterator.hasNext())
                                        {
                                            Map.Entry<Integer,Set<String>> entry = browserIdWithVersionIterator.next();

                                            //if version set is empty no need to update.
                                            if(entry.getValue().size() == 0)
                                                continue;

                                            pstmt = connection.prepareStatement(QUERY_UPDATE_BROWSER_ID_VERSION);

                                            pstmt.setString(1,
                                                    ResultSetHelper.prepareStringArrayForMySQLInsertion
                                                            (
                                                                    entry.getValue().toArray
                                                                            (
                                                                                    new String[entry.getValue().size()]
                                                                            )
                                                            )
                                            );

                                            pstmt.setInt(2,entry.getKey());

                                            if(pstmt.executeUpdate() <= 0 )
                                                throw new SQLException("Browser versions could not be updated");
                                        }
                                    }
                                    catch (SQLException sqle)
                                    {
                                        throw new Exception("Browser versions could not be updated," +
                                                            "Aborting handset data population!!!!");
                                    }
                                    finally
                                    {
                                        DBExecutionUtils.closeResources(null,pstmt,null);
                                    }

                                    connection.commit();
                                    connection.setAutoCommit(true);
                                }

                                manufacturerSqlQueriesBuffer = new StringBuffer();
                                modelSqlQueriesBuffer = new StringBuffer();
                                OSSqlQueriesBuffer = new StringBuffer();
                                browserSqlQueriesBuffer = new StringBuffer();
                                masterDataSqlQueriesBuffer = new StringBuffer();
                                masterDataSet = new HashSet<HandsetMasterData>();
                                osIdWithVersionSet = new HashMap<Integer, Set<String>>();
                                browserIdWithVersionSet = new HashMap<Integer, Set<String>>();
                                batchSizeCounter = 0;

                                //set connection commit status back to what it was before.
                                connection.setAutoCommit(autoCommitFlag);
                            }
                            //One of the attributes insertion failed, so rollback.
                            catch (Exception e)
                            {
                                logger.error("Exception in loading handset data ",e);
                                connection.rollback();
                                throw new Exception("Data could not be loaded, look for errors in logs.",e);

                            }
                            finally
                            {
                                DBExecutionUtils.closeConnection(connection);
                            }
                        }
                    }

                    if(null == maxLastModifiedTimeFromWurflFiles || maxLastModifiedTimeFromWurflFiles < fileModifyTime)
                       maxLastModifiedTimeFromWurflFiles = fileModifyTime;

                    logger.debug("Total devices counted is {}", totalDevicesCounter);
                }

                StringBuffer processedWurflFileName = new StringBuffer();
                processedWurflFileName.append(prospectiveWurflFile.getPath());
                processedWurflFileName.append(ApplicationGeneralUtils.PROCESSED_WURFL_FILE_EXTENSION);

                /*If wurfl data loading for current file is successful, then move to done extension.*/
                ApplicationGeneralUtils.moveFileAtomicallyToDestination(prospectiveWurflFile.getPath(),
                                                                        processedWurflFileName.toString());

                logger.debug("Renamed the just processed wurfl file: {} , to : {} ",
                             prospectiveWurflFile.getPath(),processedWurflFileName);

            }
        }
        finally
        {
            /*mark used instances as null, just in case required for GC.*/
            wurflUtils = null;
            wurflEngine = null;
            allDevicesIdSetFromThisWurflRelease = null;
            deviceIdIterator = null;
        }

        if(null != maxLastModifiedTimeFromWurflFiles)
        {
            lastRefreshStartTime = maxLastModifiedTimeFromWurflFiles;
            logger.debug("Inside HandsetDataPopulator , Last Refresh time set to : {}", lastRefreshStartTime);
        }
    }

    private int executeSqlBatchQueries(String queryPrefix, String queryData, Connection conn)
    {

        //if no data to insert then simply return positive value to go ahead.
        if(queryData.length() <=0 )
            return 1;

        Statement stmt = null;
        int result = 0;

        StringBuffer sb = new StringBuffer(queryPrefix);
        sb.append(queryData);

        try
        {
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sb.toString());
            conn.commit();
        }
        catch (SQLException e)
        {
            logger.error("Error in executing batch query ", e);
        }
        finally
        {
            try
            {
                if (null != stmt)
                    stmt.close();
            }
            catch (SQLException e)
            {
                logger.error("Error in closing statement while processing batch sql query.");
            }
        }

        return result;
    }


    private Set<String> fetchAllDevicesIdSetInCurrentWurflFile(WURFLUtils wurflUtils)
    {
        if (null == wurflUtils)
            return null;

        return wurflUtils.getAllDevicesId();
    }

    private String populateHandsetCapabilitiesAndReturnJSON(Device device) throws IOException
    {
        /**
        String camera = device.getCapability("built_in_camera");
        Boolean builtInCamera = StringUtils.isEmpty(camera) ? false : Boolean.valueOf(camera);

        String wifi = device.getCapability("wifi");
        Boolean canDeviceConnectToWifi = StringUtils.isEmpty(wifi) ? false : Boolean.valueOf(wifi);

        String uploadFiles = device.getCapability("xhtml_file_upload");
        Boolean canUploadFiles = StringUtils.isEmpty(uploadFiles) ? false : Boolean.valueOf(uploadFiles);

        String chtmlPrefixToMakeCall = device.getCapability("chtml_make_phone_call_string");

        String orientation = device.getCapability("dual_orientation");
        Boolean dualOrientation = StringUtils.isEmpty(orientation) ? false : Boolean.valueOf(orientation);

        String embedVideo = device.getCapability("xhtml_can_embed_video");

        String qwerty = device.getCapability("has_qwerty_keyboard");
        Boolean hasQwertyKeyboard = StringUtils.isEmpty(qwerty) ? false : Boolean.valueOf(qwerty);

        String cookie = device.getCapability("cookie_support");
        Boolean isCookieSupported = StringUtils.isEmpty(cookie) ? false : Boolean.valueOf(cookie);

        String smartTv = device.getCapability("is_smarttv");
        Boolean isDeviceSmartTv = StringUtils.isEmpty(smartTv) ? false : Boolean.valueOf(smartTv);

        String wireless = device.getCapability("is_wireless_device");
        Boolean isDeviceWireless = StringUtils.isEmpty(wireless) ? false : Boolean.valueOf(wireless);

        String javaScript = device.getCapability("ajax_support_javascript");
        Boolean isJavascriptEnabled = StringUtils.isEmpty(javaScript) ? false : Boolean.valueOf(javaScript);

        String phoneIdProvided = device.getCapability("phone_id_provided");
        Boolean isPhoneNumberAvailable = StringUtils.isEmpty(phoneIdProvided) ?
                                         false : Boolean.valueOf(phoneIdProvided);
         */

        String tablet = device.getCapability("is_tablet");
        Boolean isTablet =  StringUtils.isEmpty(tablet) ? false : Boolean.valueOf(tablet);

        /*
        String cookiesAccepted = device.getCapability("accept_third_party_cookie");

        Boolean isThirdPartyCookiesAccepted = StringUtils.isEmpty(cookiesAccepted) ?
                                              false : Boolean.valueOf(cookiesAccepted);

        String transcoder = device.getCapability("is_transcoder");
        Boolean isTranscoder = StringUtils.isEmpty(transcoder) ? false : Boolean.valueOf(transcoder);

        String dataRate = device.getCapability("max_data_rate");
        Integer maxDataRate = StringUtils.isEmpty(dataRate) ? null : Integer.valueOf(dataRate);

        String imageHeight = device.getCapability("max_image_height");
        Integer maxImageHeight = StringUtils.isEmpty(imageHeight) ? null : Integer.valueOf(imageHeight);

        String imageWidth = device.getCapability("max_image_width");
        Integer maxImageWidth = StringUtils.isEmpty(imageWidth) ? null : Integer.valueOf(imageWidth);

        String pointingMethod = device.getCapability("pointing_method");

        String preferredGeolocationApi = device.getCapability("ajax_preferred_geoloc_api");
        **/
        String resHeight = device.getCapability("resolution_height");
        Integer resolutionHeight = StringUtils.isEmpty(resHeight) ? null : Integer.valueOf(resHeight);

        String resWidth = device.getCapability("resolution_width");
        Integer resolutionWidth = StringUtils.isEmpty(resWidth) ? null : Integer.valueOf(resWidth);

        /**
        String sendMmsString = device.getCapability("xhtml_send_mms_string");

        String sendSmsString = device.getCapability("xhtml_send_sms_string");

        String transcoderUserAgentHeader = device.getCapability("transcoder_ua_header");

        String streamingVideo = device.getCapability("streaming_video");
        Boolean videoStreamingSupported = StringUtils.isEmpty(streamingVideo) ? false : Boolean.valueOf(streamingVideo);

        String wmlPrefixToMakeCall = device.getCapability("wml_make_phone_call_string");

        String xhtmlPrefixToMakeCall = device.getCapability("xhtml_make_phone_call_string");
        **/
        String midp1 = device.getCapability("j2me_midp_1_0");
        Boolean handsetIsMidp1 = StringUtils.isEmpty(midp1) ? false : Boolean.valueOf(midp1);

        String midp2 = device.getCapability("j2me_midp_2_0");
        Boolean handsetIsMidp2 = StringUtils.isEmpty(midp2) ? false : Boolean.valueOf(midp2);

        HandsetCapabilities handsetCapabilities = new HandsetCapabilities();
        /**
        handsetCapabilities.setBuiltInCamera(builtInCamera);
        handsetCapabilities.setCanDeviceConnectToWifi(canDeviceConnectToWifi);
        handsetCapabilities.setCanUploadFiles(canUploadFiles);
        handsetCapabilities.setChtmlPrefixToMakeCall(chtmlPrefixToMakeCall);
        handsetCapabilities.setDualOrientation(dualOrientation);
        handsetCapabilities.setEmbedVideo(embedVideo);
        handsetCapabilities.setHasQwertyKeyboard(hasQwertyKeyboard);
        handsetCapabilities.setIsCookieSupported(isCookieSupported);
        handsetCapabilities.setIsDeviceSmartTv(isDeviceSmartTv);
        handsetCapabilities.setIsDeviceWireless(isDeviceWireless);
        handsetCapabilities.setIsJavascriptEnabled(isJavascriptEnabled);
        handsetCapabilities.setIsPhoneNumberAvailable(isPhoneNumberAvailable);
        **/
        handsetCapabilities.setIsTablet(isTablet);
        /**
        handsetCapabilities
                .setIsThirdPartyCookiesAccepted(isThirdPartyCookiesAccepted);
        handsetCapabilities.setIsTranscoder(isTranscoder);
        handsetCapabilities.setMaxDataRate(maxDataRate);
        handsetCapabilities.setMaxImageHeight(maxImageHeight);
        handsetCapabilities.setMaxImageWidth(maxImageWidth);
        handsetCapabilities.setPointingMethod(pointingMethod);
        handsetCapabilities.setPreferredGeolocationApi(preferredGeolocationApi);
        **/
        handsetCapabilities.setResolutionHeight(resolutionHeight);
        handsetCapabilities.setResolutionWidth(resolutionWidth);
        /**
        handsetCapabilities.setSendMmsString(sendMmsString);
        handsetCapabilities.setSendSmsString(sendSmsString);
        handsetCapabilities
                .setTranscoderUserAgentHeader(transcoderUserAgentHeader);
        handsetCapabilities.setVideoStreamingSupported(videoStreamingSupported);
        handsetCapabilities.setWmlPrefixToMakeCall(wmlPrefixToMakeCall);
        handsetCapabilities.setXhtmlPrefixToMakeCall(xhtmlPrefixToMakeCall);
        **/
        handsetCapabilities.setMidp1(handsetIsMidp1);
        handsetCapabilities.setMidp2(handsetIsMidp2);

        return jacksonObjectMapper.writeValueAsString(handsetCapabilities);
    }

    /**
     * This function must be called on application shutdown time.
     */
    public void releaseResources()
    {
        if(null != this.dataUploadToDatabaseTimerTask && null != this.dataUploadToDatabaseTimer)
        {
            this.dataUploadToDatabaseTimerTask.cancel();
            this.dataUploadToDatabaseTimer.cancel();
            this.dataUploadToDatabaseTimer.purge();
        }
    }
}
