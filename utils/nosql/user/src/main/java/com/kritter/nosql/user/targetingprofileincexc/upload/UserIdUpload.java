package com.kritter.nosql.user.targetingprofileincexc.upload;

import com.kritter.constants.UserConstant;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.nosql.user.targetingprofileincexc.cache.UserTargetingProfileIncExcCache;
import com.kritter.utils.nosql.aerospike.AerospikeNoSqlNamespaceOperations;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.io.Files;

import java.io.*;
import java.util.*;

public class UserIdUpload {
    private Logger logger;

    private UserTargetingProfileIncExcCache profileIncExcCache;

    public UserIdUpload(String loggerName,
                        UserTargetingProfileIncExcCache profileIncExcCache) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.profileIncExcCache = profileIncExcCache;
    }

    public void addUserIdTargetingProfile(String userId, int targetingProfileId) {
        // if user id empty, no need to do anything
        if(userId.isEmpty()) {
            logger.error("User id provided is empty.");
            return;
        }

        try {
            ExternalUserId.fromString(userId);
        } catch (RuntimeException re) {
            this.logger.error("Exception thrown when decoding user id : {}. {}", userId, re);
            return;
        }

        logger.error("Updating the targeting profile inclusion/exclusion cache for user id : {} and targeting profile" +
                "id : {}.", userId, targetingProfileId);
        // Query the targeting profile list for the given user id. If a targeting profile list exists, add the given
        // targeting profile to the list. Otherwise, create a new entry for this user id.
        profileIncExcCache.addIncExcTargetingProfileId(userId, targetingProfileId);
    }

    public void deleteUserIdTargetingProfile(String userId, int targetingProfileId) {
        // if user id empty, no need to do anything
        if(userId.isEmpty()) {
            logger.error("User id provided is empty.");
            return;
        }

        try {
            ExternalUserId.fromString(userId);
        } catch (RuntimeException re) {
            this.logger.error("Exception thrown when decoding user id : {}. {}", userId, re);
            return;
        }

        logger.error("Deleting the targeting profile inclusion/exclusion cache for user id : {} and targeting profile" +
                "id : {}.", userId, targetingProfileId);
        // Query the targeting profile list for the given user id. If a targeting profile list exists, add the given
        // targeting profile to the list. Otherwise, create a new entry for this user id.
        profileIncExcCache.deleteIncExcTargetingProfileId(userId, targetingProfileId);
    }

    public void addTargetingProfileForUsersFromFile(String filepath) {
        // Extract targeting profile id from the file.
        String fileName = new File(filepath).getName();
        int targetingProfileId = extractTargetingProfileIdFromFileName(fileName, logger);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));

            String line;
            while ((line = reader.readLine()) != null) {
                // Read each line one by one and update aerospike for the user
                addUserIdTargetingProfile(line.trim(), targetingProfileId);
            }
        } catch (IOException ioe) {
            logger.error("Exception occurred while opening the file : {} in addTargetingProfileForUsersFromFile. " +
                    "Exception : {}", filepath, ioe);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    logger.error("Exception while trying to close reader for file : {}", filepath);
                }
            }
        }
    }

    public void deleteTargetingProfileForUsersFromFile(String filepath) {
        // Extract targeting profile id from the file.
        String fileName = new File(filepath).getName();
        int targetingProfileId = extractTargetingProfileIdFromFileName(fileName, logger);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));

            String line;
            while ((line = reader.readLine()) != null) {
                // Read each line one by one and update aerospike for the user
                deleteUserIdTargetingProfile(line.trim(), targetingProfileId);
            }
        } catch (IOException ioe) {
            logger.error("Exception occurred while opening the file : {} in deleteTargetingProfileForUsersFromFile. " +
                    "Exception : {}", filepath, ioe);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    logger.error("Exception while trying to close reader for file : {}", filepath);
                }
            }
        }
    }

    /**
     * Extracts the targeting profile id from file name. The file name is formatted as
     * tpuidincexc_<Random String>_<targeting profile id>_<timestamp>.txt
     *
     * @param fileName Name of the file containing user id inclusion exclusion list. The filename should be just the
     *                 name of the file and not the absolute or relative path of the file.
     * @return Targeting profile id corresponding to this file. Does not check whether the targeting profile
     *         corresponding to this id actually exists in the database. That check must be done in UI.
     */
    public static int extractTargetingProfileIdFromFileName(String fileName, Logger logger) {
        // Remove the file extension.
        String filenameWithoutExtension = FilenameUtils.removeExtension(fileName);
        String[] tokens = filenameWithoutExtension.split(UserConstant.USER_ID_INC_EXC_FILE_DELIMITER);
        int targetingProfileId = Integer.parseInt(tokens[UserConstant.USER_ID_INC_EXC_FILE_TPID_POS]);
        logger.debug("File name : {}, targeting profile : {}", fileName, targetingProfileId);
        return targetingProfileId;
    }

    /**
     * Given a list of targeting profile user id inc exc file paths, finds the latest file. The latest file is found
     * according to the timestamp in the file name.
     *
     * @param filePaths Array of paths of all the files corresponding to user id inclusion exclusion. Typically all
     *                  files from a single directory
     * @return path of the file with the latest timestamp
     */
    public String getLatestFilePath(String[] filePaths) {
        long timestamp = -1;
        String latestFilepath = null;

        for(String filePath : filePaths) {
            logger.debug("File path : {}. Current latest file path : {}", filePath, latestFilepath);
            String fileName = new File(filePath).getName();
            String filenameWithoutExtension = FilenameUtils.removeExtension(fileName);
            // Get timestamp from the file name
            String[] tokens = filenameWithoutExtension.split(UserConstant.USER_ID_INC_EXC_FILE_DELIMITER);
            long fileTimestamp = Long.parseLong(tokens[UserConstant.USER_ID_INC_EXC_FILE_TIMESTAMP_POS]);
            if(timestamp == -1 || timestamp < fileTimestamp) {
                timestamp = fileTimestamp;
                latestFilepath = filePath;
            }
        }

        logger.debug("Latest file path to be returned : {}", latestFilepath);
        return latestFilepath;
    }

    /**
     * Returns all the targeting profile user id inc exc file paths from the given directory. For this all the eligible
     * files must start with UserConstant.USER_ID_INC_EXC_FILE_PREFIX
     *
     * @param dirName Directory from which all the targeting profile user id inc exc files have to be extracted.
     * @return list of file paths for all the eligible files, i.e., files that are targeting profile user id inc exc
     * files.
     */
    public String[] getUserIncExcFilesFromDirectory(String dirName) {
        File directory = new File(dirName);
        IOFileFilter fileFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.startsWith(UserConstant.USER_ID_INC_EXC_FILE_PREFIX);
            }

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(UserConstant.USER_ID_INC_EXC_FILE_PREFIX);
            }
        };
        Collection<File> matchingFiles = FileUtils.listFiles(directory, fileFilter, TrueFileFilter.INSTANCE);
        File[] files = FileUtils.convertFileCollectionToFileArray(matchingFiles);

        if(files == null) {
            logger.error("Directory name : {} does not correspond to an actual path.", dirName);
            return null;
        }

        String[] filePaths = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            filePaths[i] = files[i].getAbsolutePath();
            logger.debug("Found user id inc/exc file {} in directory {}", filePaths[i], dirName);
        }
        return filePaths;
    }

    /**
     * Given a list fo file paths, creates a map containing mapping from targeting profile id to a list of files
     * corresponding to that targeting profile id. The files contain user inclusion exclusion ids.
     *
     * @param filePaths Array of Strings. Each string contains paths to user inclusion exclusion files uploaded by the
     *                  user.
     * @return map containing mapping from targeting profile ids to user inclusion exclusion file paths.
     */
    public Map<Integer, List<String>> groupFilepathsByTargetingProfile(String[] filePaths) {
        if(filePaths == null) {
            logger.debug("No file paths to process.");
            return null;
        }

        Map<Integer, List<String>> tpIdFilepathMap = new HashMap<Integer, List<String>>();
        for(String filePath : filePaths) {
            // Extract targeting profile id from the file path
            String filename = new File(filePath).getName();
            logger.debug("File path : {}. Extracted file name : {}", filePath, filename);

            int targetingProfileId = extractTargetingProfileIdFromFileName(filename, this.logger);
            List<String> tpIdFiles = null;
            if(tpIdFilepathMap.containsKey(targetingProfileId)) {
                tpIdFiles = tpIdFilepathMap.get(targetingProfileId);
                logger.debug("Entry for targeting profile id : {} found in the map. Number of entries : {}",
                        targetingProfileId, tpIdFiles.size());
            } else {
                tpIdFiles = new ArrayList<String>();
                tpIdFilepathMap.put(targetingProfileId, tpIdFiles);
                logger.debug("Entry for targeting profile id : {} not found in the map. Creating new entry.",
                        targetingProfileId);
            }

            // Insert the file path in the file list for this targeting profile id.
            tpIdFiles.add(filePath);
            logger.debug("Added file path : {} in the entry for targeting profile id : {}", filePath,
                    targetingProfileId);
        }

        return tpIdFilepathMap;
    }

    public static List<File> moveFilesAndReturnDestHandle(String[] filePaths, String destPath)
            throws IOException {
        List<File> destFileList = new LinkedList<File>();

        for(String filePath : filePaths) {
            File fromFile = new File(filePath);
            String fileName = fromFile.getName();
            if(fromFile.isFile() && fromFile.exists() && !fileName.startsWith(".")) {
                File toFile = new File(destPath + File.separator + fileName);
                Files.move(fromFile, toFile);
                destFileList.add(toFile);
            }
        }

        return destFileList;
    }

    public void work(String incomingDirectory, String processedDirectory) {
        if(incomingDirectory.isEmpty()) {
            logger.error("Empty incoming directory name.");
            throw new RuntimeException("Empty incoming directory name.");
        }

        if(processedDirectory.isEmpty()) {
            logger.error("Empty processed directory name");
            throw new RuntimeException("Empty processed directory name.");
        }

        // Get user inclusion exclusion files from directory
        String[] incomingUserIncExcFiles = getUserIncExcFilesFromDirectory(incomingDirectory);
        if(incomingUserIncExcFiles == null) {
            throw new RuntimeException("Incoming directory name " + incomingDirectory + " doesn't correspond to a " +
                    "valid directory.");
        }

        String[] processedUserIncExcFiles = getUserIncExcFilesFromDirectory(processedDirectory);
        if(processedUserIncExcFiles == null) {
            throw new RuntimeException("Incoming directory name " + processedDirectory + " doesn't correspond to a " +
                    "valid directory.");
        }

        Map<Integer, List<String>> incomingDirTargetingProfileFilepathMap = groupFilepathsByTargetingProfile(
                incomingUserIncExcFiles);
        Map<Integer, List<String>> processedDirTargetingProfileFilepathMap = groupFilepathsByTargetingProfile(
                processedUserIncExcFiles);

        // Iterate the incoming targeting profile file path map and process for each targeting profile
        for(Map.Entry<Integer, List<String>> incomingTargetingProfileFilepaths :
                incomingDirTargetingProfileFilepathMap.entrySet()) {
            Integer targetingProfileId = incomingTargetingProfileFilepaths.getKey();
            List<String> filepaths = incomingTargetingProfileFilepaths.getValue();

            String[] incomingFilepaths = new String[filepaths.size()];
            incomingFilepaths = filepaths.toArray(incomingFilepaths);

            // Get the list of filepaths from processed directory
            List<String> processedFilepathList = processedDirTargetingProfileFilepathMap.get(targetingProfileId);
            String[] processedFilepaths = null;
            if(processedFilepathList != null) {
                processedFilepaths = new String[processedFilepathList.size()];
                processedFilepaths = processedFilepathList.toArray(processedFilepaths);
            }

            if(processedFilepaths != null) {
                String latestProcessedFilepath = getLatestFilePath(processedFilepaths);
                logger.debug("Latest processed file path : {}.", latestProcessedFilepath);
                deleteTargetingProfileForUsersFromFile(latestProcessedFilepath);
                logger.debug("Deleted all the entries from the latest file {} to aerospike db.",
                        latestProcessedFilepath);
            }

            // Get the latest file from incoming directory
            String latestIncomingFilepath = getLatestFilePath(incomingFilepaths);
            logger.debug("Latest incoming file path : {}.", latestIncomingFilepath);
            // Process this file and add all the user id to targeting profile mappings
            addTargetingProfileForUsersFromFile(latestIncomingFilepath);
            logger.debug("Added all the entries from the latest file {} to aerospike db.", latestIncomingFilepath);

            // Move all listed incoming files to processed directory.
            try {
                moveFilesAndReturnDestHandle(incomingFilepaths, processedDirectory);
            } catch (IOException ioe) {
                logger.error("Exception caught while moving files to destination directory. Exception : {}", ioe);
            }
        }
    }

    public static void main(String[] args) {
        String log4jPropertiesFilepath = args[0];
        PropertyConfigurator.configure(log4jPropertiesFilepath);
        String incomingDirectory = args[1];
        String processedDirectory = args[2];
        String loggerName = args[3];
        String namespaceName = args[4];
        String tableName = args[5];
        String primaryKeyName = args[6];
        String attributeName = args[7];
        String cacheName = args[8];
        String cacheLoggerName = args[9];
        String aerospikeHost = args[10];
        int aerospikePort = Integer.parseInt(args[11]);
        int maxRetries = Integer.parseInt(args[12]);
        int timeout = Integer.parseInt(args[13]);
        String aerospikeLoggerName = args[14];

        AerospikeNoSqlNamespaceOperations aerospikeNoSqlNamespaceOperations = new AerospikeNoSqlNamespaceOperations(
                aerospikeLoggerName,
                aerospikeHost,
                aerospikePort,
                maxRetries,
                timeout
        );

        Properties cacheProperties = new Properties();
        cacheProperties.setProperty(UserTargetingProfileIncExcCache.NAMESPACE_NAME_KEY, namespaceName);
        cacheProperties.setProperty(UserTargetingProfileIncExcCache.TABLE_NAME_KEY, tableName);
        cacheProperties.setProperty(UserTargetingProfileIncExcCache.PRIMARY_KEY_NAME_KEY, primaryKeyName);
        cacheProperties.setProperty(UserTargetingProfileIncExcCache.ATTRIBUTE_NAME_TARGETING_PROFILES_INC_EXC,
                attributeName);

        UserTargetingProfileIncExcCache userTargetingProfileIncExcCache = new UserTargetingProfileIncExcCache(
                cacheName,
                cacheLoggerName,
                aerospikeNoSqlNamespaceOperations,
                cacheProperties
        );

        UserIdUpload userIdUpload = new UserIdUpload(loggerName, userTargetingProfileIncExcCache);
        userIdUpload.work(incomingDirectory, processedDirectory);
    }
}
