package com.kritter.device.mad;

import com.kritter.device.common.HandsetPopulationProvider;
import com.kritter.device.common.entity.HandsetManufacturerData;
import com.kritter.device.common.entity.HandsetModelData;
import com.kritter.device.common.entity.HandsetOperatingSystemData;
import com.kritter.device.common.util.DeviceUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MadDataPopulator implements HandsetPopulationProvider {
    // Regular expression signifying valid versions. A valid version should be of the form
    // <number>'.'<number>'.'...'.'<number>
    // That is, a number followed by a dot, followed by a number, etc.
    // Thus 1.834.22, 1.1, 5, 2.55.1.3.5231 are valid versions while pocket pc, 1.x, 857., etc are not.
    // Note that a trailing .(dot) is not considered to be a valid pattern. Hence 857. is not a valid pattern.
    // The regex is equivalent to ([0-9]+.)*[0-9]+.
    private static String versionPatternRegex = "(\\d+.)*\\d+";
    private static final String DELIMITER = "";

    //constants for usage here
    public static final String PROCESSED_MAD_FILE_EXTENSION = ".done";
    private static final String DATA_SOURCE_HANDSET = "51Degrees";
    private static final String QUERY_NEW_MANUFACTURER_DATA_ROW =
            "insert into handset_manufacturer (manufacturer_name, modified_by, modified_on) values (?, ?, ?)";
    private static final String QUERY_NEW_MODEL_DATA_ROW =
            "insert into handset_model (manufacturer_id, model_name, modified_by, modified_on) values " +
                    "((select manufacturer_id from handset_manufacturer where manufacturer_name = ?), ?, ?, ?)";
    private static final String QUERY_NEW_OS_DATA_ROW =
            "insert into handset_os (os_name, os_versions, modified_by, modified_on) values (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_OS_ID_VERSION = "update handset_os set os_versions = ? where os_id = " +
            "(select id from handset_os where os_name = ?)";

    private Logger logger;
    private String madFilesDirectory;
    private Long lastRefreshStartTime = null;
    private Timer dataUploadToDatabaseTimer;
    private TimerTask dataUploadToDatabaseTimerTask;
    private DatabaseManager databaseManager;
    private Pattern versionPattern;

    // data maps for already present data in database.
    private Map<String, HandsetManufacturerData> manufacturerDataFromDatabase;
    private Map<String, HandsetModelData> modelDataFromDatabase;
    private Map<String, HandsetOperatingSystemData> operatingSystemDataFromDatabase;
    private Map<String, HandsetOperatingSystemData> operatingSystemsToAdd;
    private Set<HandsetOperatingSystemData> operatingSystemsToUpdate;
    private Set<HandsetModelData> modelsToInsert;
    private Timestamp timestamp;

    public MadDataPopulator(String loggerName,
                                        String madFilesDirectory,
                                        int newDataLoadFrequency,
                                        DatabaseManager databaseManager,
                                        String dataLoadMasterNodeParam,
                                        ServerConfig serverConfig) throws Exception {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.madFilesDirectory = madFilesDirectory;
        this.databaseManager = databaseManager;
        this.versionPattern = Pattern.compile(versionPatternRegex);

        //This variable tells whether this machine's/node's application
        //should load data into database or it is just a slave node.
        boolean dataLoadMasterNode = Boolean.valueOf(serverConfig.getValueForKey(dataLoadMasterNodeParam));

        //important message so in error mode.
        logger.error("The handset data populator on this node/machine/dsp-application is parent? {} , will be loading data from this node only if true.",dataLoadMasterNode);

        if(dataLoadMasterNode) {
            //run data uploading once in the constructor only.
            readMadDataAndPopulateIntoDatabase();

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
                readMadDataAndPopulateIntoDatabase();
            } catch (Exception e) {
                logger.error("Exception inside timer task to load data into database inside MadDataPopulator",e);
            }
        }
    }

    private void readMadDataAndPopulateIntoDatabase() throws Exception {
        logger.info("Inside readMadDataAndPopulateIntoDatabase... Beginning data loading.");
        DateTime dateTime = new DateTime();
        timestamp = new Timestamp(dateTime.getMillis());

        this.operatingSystemsToAdd = new HashMap<String, HandsetOperatingSystemData>();
        this.operatingSystemsToUpdate = new HashSet<HandsetOperatingSystemData>();
        this.modelsToInsert = new HashSet<HandsetModelData>();

        if(null == madFilesDirectory) {
            throw new Exception("The configured mad file directory is null !!! FATAL !!!");
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

        File madFilesFolder = new File(madFilesDirectory);
        Long maxLastModifiedTimeFromMadFiles = null;
        File lastModifiedFile = null;

        for(File prospectiveFile : madFilesFolder.listFiles()) {
            if(prospectiveFile.getName().endsWith(PROCESSED_MAD_FILE_EXTENSION)) {
                logger.debug("The mad file {} is already processed by HandsetDataPopulator, continuing ...",
                        prospectiveFile.getName());
                continue;
            }

            // Of all the unprocessed files, find the one with maximum last modified time.
            long fileModifyTime = prospectiveFile.lastModified();
            if(maxLastModifiedTimeFromMadFiles == null ||
                    maxLastModifiedTimeFromMadFiles < fileModifyTime) {
                maxLastModifiedTimeFromMadFiles = fileModifyTime;
                lastModifiedFile = prospectiveFile;
            }
        }

        if(maxLastModifiedTimeFromMadFiles == null)
            return;
        if(lastRefreshStartTime != null && maxLastModifiedTimeFromMadFiles <= lastRefreshStartTime)
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

        logger.info("Success executing readMadDataAndPopulateIntoDatabase... Data load complete.");
    }

    public void populateDataFromFile(File file, Connection connection) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        boolean autoCommit = false;
        try {
            fr = new FileReader(file);
            br= new BufferedReader(fr);
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String str;
            while((str = br.readLine()) != null) {
                /**
                 * ua<CTRLA>md5(ua)<CTRLA>is_tablet<CTRLA>is_wireless_device<CTRLA>brand_name
                 * <CTRLA>model_name<CTRLA>device_os<CTRLA>device_os_version<CTRLA>resolution_height
                 * <CTRLA>resolution_width
                 */
                String strSplit[] = str.split(DELIMITER);
                if(strSplit.length == 10){
                    populateHardwareData(strSplit[4], strSplit[5], connection);
                    populateSoftwareData(strSplit[6], strSplit[7]);
                    
                }
            }

            logger.debug("New operating systems to add : {}, operating systems to update : {}",
                    operatingSystemsToAdd.size(), operatingSystemsToUpdate.size());
            // Populate manufacturers, os and models
            populateOperatingSystemDataInDatabase(connection);
            populateModelsInDatabase(connection);
            connection.commit();

        } catch (Exception sqle) {
            logger.error("Error in populating 51 degrees data {}", sqle);
            connection.rollback();
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (Exception sqle) {
                // Bail out
                logger.error("Error in setting auto commit", sqle);
            }
            if(fr != null){
                try {
                    fr.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
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
                logger.error("Error in prepared statement while populating new model", sqle);
                throw sqle;
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        }
    }

    public void populateHardwareData(String manufacturerName,String modelName, Connection connection) throws Exception {
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

    public void populateSoftwareData(String osName, String osVersion) throws Exception {
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


    private Connection fetchConnectionToDatabase() throws Exception {
        return this.databaseManager.getConnectionFromPool();
    }

    //load into memory the complete handset data.
    private void loadHandsetDataFromDatabase(Connection connectionToDatabase) throws Exception {
        manufacturerDataFromDatabase = DeviceUtils.fetchManufacturerDataFromDatabase(connectionToDatabase);
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
