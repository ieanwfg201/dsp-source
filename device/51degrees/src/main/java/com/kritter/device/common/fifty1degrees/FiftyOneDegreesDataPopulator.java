package com.kritter.device.common.fifty1degrees;

import com.kritter.device.common.HandsetPopulationProvider;
import com.kritter.device.common.entity.HandsetBrowserData;
import com.kritter.device.common.entity.HandsetManufacturerData;
import com.kritter.device.common.entity.HandsetModelData;
import com.kritter.device.common.entity.HandsetOperatingSystemData;
import com.kritter.device.common.util.DeviceUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import fiftyone.mobile.detection.IReadonlyList;
import fiftyone.mobile.detection.Provider;
import fiftyone.mobile.detection.entities.Component;
import fiftyone.mobile.detection.entities.Profile;
import fiftyone.mobile.detection.factories.MemoryFactory;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiftyOneDegreesDataPopulator implements HandsetPopulationProvider {
    // Regular expression signifying valid versions. A valid version should be of the form
    // <number>'.'<number>'.'...'.'<number>
    // That is, a number followed by a dot, followed by a number, etc.
    // Thus 1.834.22, 1.1, 5, 2.55.1.3.5231 are valid versions while pocket pc, 1.x, 857., etc are not.
    // Note that a trailing .(dot) is not considered to be a valid pattern. Hence 857. is not a valid pattern.
    // The regex is equivalent to ([0-9]+.)*[0-9]+.
    private static String versionPatternRegex = "(\\d+.)*\\d+";

    //constants for usage here
    private static final String DATA_SOURCE_HANDSET = "51Degrees";
    private static final String COMMA = ",";
    private static final int HARDWARE_COMPONENT_ID = 1;
    private static final int SOFTWARE_COMPONENT_ID = 2;
    private static final int BROWSER_COMPONENT_ID = 3;
    private static final int CRAWLER_COMPONENT_ID = 4;
    private static final String QUERY_NEW_MANUFACTURER_DATA_ROW =
            "insert into handset_manufacturer (manufacturer_name, modified_by, modified_on) values (?, ?, ?)";
    private static final String QUERY_NEW_MODEL_DATA_ROW =
            "insert into handset_model (manufacturer_id, model_name, modified_by, modified_on) values " +
                    "((select manufacturer_id from handset_manufacturer where manufacturer_name = ?), ?, ?, ?)";
    private static final String QUERY_NEW_OS_DATA_ROW =
            "insert into handset_os (os_name, os_versions, modified_by, modified_on) values (?, ?, ?, ?)";
    private static final String QUERY_NEW_BROWSER_DATA_ROW =
            "insert into handset_browser (browser_name, browser_versions, modified_by, modified_on) " +
                    "values (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_OS_ID_VERSION = "update handset_os set os_versions = ? where os_id = " +
            "(select id from handset_os where os_name = ?)";
    private static final String QUERY_UPDATE_BROWSER_ID_VERSION = "update handset_browser set browser_versions = ? " +
            "where browser_id = ?";

    private Logger logger;
    private String fiftyOneDegreesFilesDirectory;
    private Long lastRefreshStartTime = null;
    private Timer dataUploadToDatabaseTimer;
    private TimerTask dataUploadToDatabaseTimerTask;
    private DatabaseManager databaseManager;
    private Pattern versionPattern;

    // data maps for already present data in database.
    private Map<String, HandsetManufacturerData> manufacturerDataFromDatabase;
    private Map<String, HandsetBrowserData> browserDataFromDatabase;
    private Map<String, HandsetModelData> modelDataFromDatabase;
    private Map<String, HandsetOperatingSystemData> operatingSystemDataFromDatabase;
    private Map<String, HandsetOperatingSystemData> operatingSystemsToAdd;
    private Set<HandsetOperatingSystemData> operatingSystemsToUpdate;
    private Map<String, HandsetBrowserData> browsersToAdd;
    private Set<HandsetBrowserData> browsersToUpdate;
    private Set<HandsetModelData> modelsToInsert;
    private Timestamp timestamp;

    public FiftyOneDegreesDataPopulator(String loggerName,
                                        String fiftyOneDegreesFilesDirectory,
                                        int newDataLoadFrequency,
                                        DatabaseManager databaseManager,
                                        String dataLoadMasterNodeParam,
                                        ServerConfig serverConfig) throws Exception {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.fiftyOneDegreesFilesDirectory = fiftyOneDegreesFilesDirectory;
        this.databaseManager = databaseManager;
        this.versionPattern = Pattern.compile(versionPatternRegex);

        //This variable tells whether this machine's/node's application
        //should load data into database or it is just a slave node.
        boolean dataLoadMasterNode = Boolean.valueOf(serverConfig.getValueForKey(dataLoadMasterNodeParam));

        //important message so in error mode.
        logger.error("The handset data populator on this node/machine/dsp-application is parent? {} , will be loading data from this node only if true.",dataLoadMasterNode);

        if(dataLoadMasterNode) {
            //run data uploading once in the constructor only.
            readFiftyOneDegreesDataAndPopulateIntoDatabase();

            //construct timer task and start it.Keep initial delay to repeat frequency.
            //ideally should be an hour or so.
            this.dataUploadToDatabaseTimer = new Timer();

            this.dataUploadToDatabaseTimerTask = new DataUploadToDatabaseTimerTask();
            this.dataUploadToDatabaseTimer.schedule(this.dataUploadToDatabaseTimerTask,
                    newDataLoadFrequency,
                    newDataLoadFrequency);
        }
    }

    private class DataUploadToDatabaseTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                readFiftyOneDegreesDataAndPopulateIntoDatabase();
            } catch (Exception e) {
                logger.error("Exception inside timer task to load data into database inside FiftyOneDegreesDataPopulator",e);
            }
        }
    }

    private void readFiftyOneDegreesDataAndPopulateIntoDatabase() throws Exception {
        logger.info("Inside readFiftyOneDegreesDataAndPopulateIntoDatabase... Beginning data loading.");
        DateTime dateTime = new DateTime();
        timestamp = new Timestamp(dateTime.getMillis());

        this.operatingSystemsToAdd = new HashMap<String, HandsetOperatingSystemData>();
        this.operatingSystemsToUpdate = new HashSet<HandsetOperatingSystemData>();
        this.browsersToAdd = new HashMap<String, HandsetBrowserData>();
        this.browsersToUpdate = new HashSet<HandsetBrowserData>();
        this.modelsToInsert = new HashSet<HandsetModelData>();

        if(null == fiftyOneDegreesFilesDirectory) {
            throw new Exception("The configured 51degrees file directory is null !!! FATAL !!!");
        }

        Connection conn = null;
        //load data present in database if any.
        try {
            conn = fetchConnectionToDatabase();
            loadHandsetDataFromDatabase(conn);
        } catch (SQLException sqle) {
            throw new Exception("Exception in data loading from database ",sqle);
        } finally {
            DBExecutionUtils.closeConnection(conn);
        }

        File fiftyOneDegreesFilesFolder = new File(fiftyOneDegreesFilesDirectory);
        Long maxLastModifiedTimeFromFiftyOneDegreesFiles = null;
        File lastModifiedFile = null;

        for(File prospectiveFile : fiftyOneDegreesFilesFolder.listFiles()) {
            if(prospectiveFile.getName().endsWith(ApplicationGeneralUtils.PROCESSED_51DEGREES_FILE_EXTENSION)) {
                logger.debug("The 51degree file {} is already processed by HandsetDataPopulator, continuing ...",
                        prospectiveFile.getName());
                continue;
            }

            // Of all the unprocessed files, find the one with maximum last modified time.
            long fileModifyTime = prospectiveFile.lastModified();
            if(maxLastModifiedTimeFromFiftyOneDegreesFiles == null ||
                    maxLastModifiedTimeFromFiftyOneDegreesFiles < fileModifyTime) {
                maxLastModifiedTimeFromFiftyOneDegreesFiles = fileModifyTime;
                lastModifiedFile = prospectiveFile;
            }
        }

        if(maxLastModifiedTimeFromFiftyOneDegreesFiles == null)
            return;
        if(lastRefreshStartTime != null && maxLastModifiedTimeFromFiftyOneDegreesFiles <= lastRefreshStartTime)
            return;

        try
        {
            conn = fetchConnectionToDatabase();
            loadHandsetDataFromDatabase(conn);
            populateDataFromFile(lastModifiedFile, conn);
        }
        finally
        {
            DBExecutionUtils.closeConnection(conn);
        }

        logger.info("Success executing readFiftyOneDegreesDataAndPopulateIntoDatabase... Data load complete.");
    }

    public void populateDataFromFile(File file, Connection connection) throws Exception {
        Provider provider = new Provider(MemoryFactory.create(new FileInputStream(file)));
        IReadonlyList<Profile> profiles =  provider.dataSet.getProfiles();
        boolean autoCommit = false;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            for (Profile profile : profiles) {
                Component component = profile.getComponent();
                logger.debug("Profile component = {}", profile.getComponent().getName());
                if (component.getComponentId() == HARDWARE_COMPONENT_ID) {
                    populateHardwareData(profile, connection);
                } else if (component.getComponentId() == SOFTWARE_COMPONENT_ID) {
                    populateSoftwareData(profile);
                } else if (component.getComponentId() == BROWSER_COMPONENT_ID) {
                    populateBrowserData(profile);
                }
            }

            logger.debug("New operating systems to add : {}, operating systems to update : {}",
                    operatingSystemsToAdd.size(), operatingSystemsToUpdate.size());
            logger.debug("New browsers to add : {}, browsers to update : {}",
                    browsersToAdd.size(), browsersToUpdate.size());

            // Populate manufacturers, os, browsers and models
            populateOperatingSystemDataInDatabase(connection);
            populateBrowserDataInDatabase(connection);
            populateModelsInDatabase(connection);
            connection.commit();

        } catch (SQLException sqle) {
            logger.error("Error in populating 51 degrees data {}", sqle);
            connection.rollback();
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException sqle) {
                // Bail out
                logger.error("Error in setting auto commit {}", sqle);
            }
        }
    }

    public void populateOperatingSystemDataInDatabase(Connection connection) throws Exception {
        // Populate new operating systems
        for(Map.Entry<String, HandsetOperatingSystemData> keyValuePair : operatingSystemsToAdd.entrySet()) {
            HandsetOperatingSystemData handsetOperatingSystemData = keyValuePair.getValue();
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(QUERY_NEW_OS_DATA_ROW);
                preparedStatement.setString(1, handsetOperatingSystemData.getOperatingSystemName().toLowerCase());
                preparedStatement.setString(2, ResultSetHelper.prepareStringArrayForMySQLInsertion(
                        handsetOperatingSystemData.getOperatingSystemVersions()));
                preparedStatement.setString(3, DATA_SOURCE_HANDSET);
                preparedStatement.setTimestamp(4, this.timestamp);
                preparedStatement.execute();
            } catch (SQLException sqle) {
                logger.error("Error in prepared statement while populating new os {}", sqle);
                throw sqle;
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        }

        for(HandsetOperatingSystemData handsetOperatingSystemData : operatingSystemsToUpdate) {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(QUERY_UPDATE_OS_ID_VERSION);
                preparedStatement.setString(1, ResultSetHelper.prepareStringArrayForMySQLInsertion(
                        handsetOperatingSystemData.getOperatingSystemVersions()));
                preparedStatement.execute();
            } catch (SQLException sqle) {
                logger.error("Error in prepared statement while populating new os {}", sqle);
                throw sqle;
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        }
    }

    public void populateBrowserDataInDatabase(Connection connection) throws Exception {
        for(Map.Entry<String, HandsetBrowserData> keyValuePair : browsersToAdd.entrySet()) {
            HandsetBrowserData handsetBrowserData = keyValuePair.getValue();
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(QUERY_NEW_BROWSER_DATA_ROW);
                preparedStatement.setString(1, handsetBrowserData.getBrowserName().toLowerCase());
                preparedStatement.setString(2, ResultSetHelper.prepareStringArrayForMySQLInsertion(
                        handsetBrowserData.getBrowserVersions()));
                preparedStatement.setString(3, DATA_SOURCE_HANDSET);
                preparedStatement.setTimestamp(4, this.timestamp);
                preparedStatement.execute();
            } catch (SQLException sqle) {
                logger.error("Error in prepared statement while populating new browser {}", sqle);
                throw sqle;
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        }

        for(HandsetBrowserData handsetBrowserData : browsersToUpdate) {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(QUERY_UPDATE_BROWSER_ID_VERSION);
                preparedStatement.setString(1, ResultSetHelper.prepareStringArrayForMySQLInsertion(
                        handsetBrowserData.getBrowserVersions()));
                preparedStatement.execute();
            } catch (SQLException sqle) {
                logger.error("Error in prepared statement while populating new browser {}", sqle);
                throw sqle;
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        }
    }

    public void populateModelsInDatabase(Connection connection) throws Exception {
        for(HandsetModelData handsetModelData : modelsToInsert) {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(QUERY_NEW_MODEL_DATA_ROW);
                preparedStatement.setString(1, handsetModelData.getManufacturerName());
                preparedStatement.setString(2, handsetModelData.getModelName());
                preparedStatement.setString(3, DATA_SOURCE_HANDSET);
                preparedStatement.setTimestamp(4, this.timestamp);
                preparedStatement.execute();
            } catch (SQLException sqle) {
                logger.error("Error in prepared statement while populating new browser {}", sqle);
                throw sqle;
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        }
    }

    public void populateHardwareData(Profile profile, Connection connection) throws Exception {
        String manufacturerName = profile.getValues("HardwareVendor").toString();
        String modelName = profile.getValues("HardwareModel").toString();

        PreparedStatement preparedStatement = null;
        try {
            if (!manufacturerDataFromDatabase.containsKey(manufacturerName.toLowerCase())) {
                preparedStatement = connection.prepareStatement(QUERY_NEW_MANUFACTURER_DATA_ROW);
                preparedStatement.setString(1, manufacturerName.toLowerCase());
                preparedStatement.setString(2, DATA_SOURCE_HANDSET);
                preparedStatement.setTimestamp(3, this.timestamp);
                preparedStatement.execute();
                manufacturerDataFromDatabase.put(manufacturerName.toLowerCase(),
                        new HandsetManufacturerData(manufacturerName.toLowerCase(), DATA_SOURCE_HANDSET));
            }
        } catch (SQLException sqle) {
            logger.error("Exception in prepared statement {}", sqle);
            throw sqle;
        } finally {
            if(preparedStatement != null)
                preparedStatement.close();
        }

        if(!modelDataFromDatabase.containsKey(modelName.toLowerCase())) {
            modelsToInsert.add(new HandsetModelData(manufacturerName.toLowerCase(), modelName.toLowerCase(),
                    DATA_SOURCE_HANDSET));
        }
    }

    public void populateSoftwareData(Profile profile) throws Exception {
        String osName = profile.getValues("PlatformName").toString();
        String osVersion = profile.getValues("PlatformVersion").toString();
        logger.debug("OS name : {}, version : {}", osName, osVersion);

        // Check whether the os version and mobile version are valid versions as per the version pattern
        // If not set them to null
        Matcher osVersionPatternMatcher = versionPattern.matcher(osVersion);
        if(!osVersionPatternMatcher.matches()) {
            logger.debug("OS version : {}, does not match the required format. Skipping it.", osVersion);
            osVersion = "";
        }

        if(operatingSystemDataFromDatabase.containsKey(osName.toLowerCase())) {
            HandsetOperatingSystemData operatingSystemData = operatingSystemDataFromDatabase.get(osName.toLowerCase());
            if(!StringUtils.isEmpty(osVersion) &&
                   !operatingSystemData.getOperatingSystemVersionSet().contains(osVersion.toLowerCase())) {
                logger.debug("New version found for os : {}, version : {}", osName.toLowerCase(),
                        osVersion.toLowerCase());
                operatingSystemsToUpdate.add(operatingSystemData);
                operatingSystemData.getOperatingSystemVersionSet().add(osVersion.toLowerCase());
            }
        } else {
            logger.debug("New os found : {}", osName.toLowerCase());
            HandsetOperatingSystemData handsetOperatingSystemData = null;
            if(operatingSystemsToAdd.containsKey(osName.toLowerCase())) {
                handsetOperatingSystemData = operatingSystemsToAdd.get(osName.toLowerCase());
            } else {
                handsetOperatingSystemData = new HandsetOperatingSystemData(osName.toLowerCase(), null);
                operatingSystemsToAdd.put(osName.toLowerCase(), handsetOperatingSystemData);
            }
            if(!StringUtils.isEmpty(osVersion))
                handsetOperatingSystemData.getOperatingSystemVersionSet().add(osVersion.toLowerCase());
        }
    }

    public void populateBrowserData(Profile profile) throws Exception {
        String browserName = profile.getValues("BrowserName").toString();
        String browserVersion = profile.getValues("BrowserVersion").toString();
        logger.debug("Browser name : {}, version : {}", browserName, browserVersion);

        // Check whether the os version and mobile version are valid versions as per the version pattern
        // If not set them to null
        Matcher osVersionPatternMatcher = versionPattern.matcher(browserVersion);
        if(!osVersionPatternMatcher.matches()) {
            logger.debug("Browser version : {}, does not match the required format. Skipping it.", browserVersion);
            browserVersion = "";
        }

        if(browserDataFromDatabase.containsKey(browserName.toLowerCase())) {
            HandsetBrowserData handsetBrowserData = browserDataFromDatabase.get(browserName.toLowerCase());
            if(!StringUtils.isEmpty(browserVersion) &&
                    !handsetBrowserData.getBrowserVersionSet().contains(browserVersion.toLowerCase())) {
                logger.debug("New version found for browser : {}, version : {}", browserName.toLowerCase(),
                        browserVersion.toLowerCase());
                if(!browsersToUpdate.contains(handsetBrowserData))
                    browsersToUpdate.add(handsetBrowserData);
                handsetBrowserData.getBrowserVersionSet().add(browserVersion.toLowerCase());
            }
        } else {
            logger.debug("New browser found : {}", browserName.toLowerCase());
            HandsetBrowserData handsetBrowserData = null;
            if(browsersToAdd.containsKey(browserName.toLowerCase())) {
                handsetBrowserData = browsersToAdd.get(browserName.toLowerCase());
            } else {
                handsetBrowserData = new HandsetBrowserData(browserName.toLowerCase(), null);
                browsersToAdd.put(browserName.toLowerCase(), handsetBrowserData);
            }
            if(!StringUtils.isEmpty(browserVersion))
                handsetBrowserData.getBrowserVersionSet().add(browserVersion.toLowerCase());
        }
    }

    private Connection fetchConnectionToDatabase() throws Exception {
        return this.databaseManager.getConnectionFromPool();
    }

    //load into memory the complete handset data.
    private void loadHandsetDataFromDatabase(Connection connectionToDatabase) throws Exception {
        manufacturerDataFromDatabase = DeviceUtils.fetchManufacturerDataFromDatabase(connectionToDatabase);
        browserDataFromDatabase = DeviceUtils.fetchBrowserDataFromDatabase(connectionToDatabase);
        modelDataFromDatabase = DeviceUtils.fetchHandsetModelDataFromDatabase(connectionToDatabase);
        operatingSystemDataFromDatabase = DeviceUtils.fetchOperatingSystemDataFromDatabase(connectionToDatabase);
    }

    /**
     * This function must be called on application shutdown time.
     */
    public void releaseResources() {
        if(null != this.dataUploadToDatabaseTimerTask && null != this.dataUploadToDatabaseTimer) {
            this.dataUploadToDatabaseTimerTask.cancel();
            this.dataUploadToDatabaseTimer.cancel();
            this.dataUploadToDatabaseTimer.purge();
        }
    }
}
