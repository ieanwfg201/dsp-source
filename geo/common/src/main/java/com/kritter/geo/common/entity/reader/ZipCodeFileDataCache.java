package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.common.SetUtils;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class keeps zipcode file data for different active targeting profiles
 * in the system, each zipcode file has data lines as:
 * ui_country_idDELIMITERzipcode, ie , country id from ui_targeting_country
 * table followed by DELIMITER followed by the zipcode being targeted.
 * In a special case where file is present but there is no data inside it
 * then even the targeting match would fail always and the case would not be
 * considered as being of no zipcode targeting.
 */
public class ZipCodeFileDataCache
{
    private Logger logger;
    private String queryToLoadZipCodeFileIds;
    private DatabaseManager databaseManager;
    private String zipCodeFileStorageDirectory;
    private Map<String,Set<String>> zipCodeFileDatabase;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String,Long> lastRefreshStartTimeMap;
    public static final String DELIMITER = String.valueOf((char)1);

    public ZipCodeFileDataCache(String loggerName,
                                String queryToLoadZipCodeFileIds,
                                DatabaseManager databaseManager,
                                String zipCodeFileStorageDirectory,
                                long reloadFrequency) throws InitializationException
    {
        this.logger = LogManager.getLogger(loggerName);
        this.queryToLoadZipCodeFileIds = queryToLoadZipCodeFileIds;
        this.databaseManager = databaseManager;
        this.zipCodeFileStorageDirectory = zipCodeFileStorageDirectory;
        this.zipCodeFileDatabase = new ConcurrentHashMap<String, Set<String>>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();

        //run data loading once then schedule the timer task.
        try
        {
            buildDatabase();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside ZipCodeFileDataCache ",re);
            throw new InitializationException("RefreshException inside ZipCodeFileDataCache ",re);
        }

        this.timer = new Timer();
        this.timerTask = new ZipCodeFileDatabaseReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    /**
     * This function fetches all the zipcode file ids from sql
     * database and then loads all the corresponding files to keep
     * in memory.
     * @throws RefreshException
     */
    public void buildDatabase() throws RefreshException
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Set<String> zipCodeFileIdSet = new HashSet<String>();
        Map<String,Set<String>> zipCodeFileDataAgainstIdMapTemp = new HashMap<String, Set<String>>();

        try
        {
            connection = databaseManager.getConnectionFromPool();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryToLoadZipCodeFileIds);

            while(resultSet.next())
            {
                String[] fileIdArray = ResultSetHelper.getResultSetStringArray(resultSet, "zipcode_file_id_set");
                if(null != fileIdArray)
                    zipCodeFileIdSet.addAll(new HashSet<String>(Arrays.asList(fileIdArray)));
            }
        }
        catch (SQLException sqle)
        {
            logger.error("SQLException inside ZipCodeFileDataCache in getting connection ",sqle);
        }
        finally
        {
            DBExecutionUtils.closeResources(connection,statement,resultSet);
        }

        //now load all the files which have been modified since last reload.
        for(String fileId : zipCodeFileIdSet)
        {
            StringBuffer filePath = new StringBuffer(this.zipCodeFileStorageDirectory);
            filePath.append(fileId);

            File inputDataFile = new File(filePath.toString());

            if(!inputDataFile.exists())
            {
                logger.error("File does not exist inside ZipCodeFileDataCache, the file looked up was:{} ", filePath.toString());
                continue;
            }

            long fileModifyTime = inputDataFile.lastModified();
            Long lastRefreshStartTime = this.lastRefreshStartTimeMap.get(fileId);

            //following means file has been modified since last reload.
            if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
            {
                Long startTime = new Date().getTime();
                Set<String> dataSet = new HashSet<String>();

                BufferedReader br = null;

                try
                {
                    br = new BufferedReader(new FileReader(inputDataFile));
                    String line = null;

                    while(null != (line = br.readLine()))
                    {
                        if(!checkLineForCorrectness(line))
                            throw new RefreshException("Line is not of correct format inside ZipCodeFileDataCache," +
                                                       "line being: " + line);

                        dataSet.add(line);
                    }

                    zipCodeFileDataAgainstIdMapTemp.put(fileId,dataSet);

                    this.lastRefreshStartTimeMap.put(fileId,startTime);
                }
                catch (IOException ioe)
                {
                    throw new RefreshException("IOException in ZipCodeFileDataCache, cause: ",ioe);
                }
                finally
                {
                    if(br != null)
                    {
                        try
                        {
                            br.close();
                        }
                        catch (IOException ioe)
                        {
                            throw new RefreshException("IOException thrown while closing file handle during " +
                                                       "refresh in ZipCodeFileDataCache: ", ioe);
                        }
                    }
                }
            }
        }

        //now all the modified data has been loaded , feed it into the main data map.
        for(String modifyDataForFileId : zipCodeFileDataAgainstIdMapTemp.keySet())
        {
            this.zipCodeFileDatabase.put(
                                         modifyDataForFileId,
                                         zipCodeFileDataAgainstIdMapTemp.get(modifyDataForFileId)
                                        );
        }
    }

    //check that the line is as per the data format required.
    private boolean checkLineForCorrectness(String line)
    {
        if(null == line)
            return false;

        String parts[] = line.split(DELIMITER);

        if(parts.length != 2)
            return false;

        try
        {
            Integer.parseInt(parts[0]);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        return true;
    }

    /**
     * This function returns true if the input zipcode set exist in one of the input file ids to check.
     * The input zipcode parameter zipCodeWithCountryId is of format uiCountryIdcontrol-Azipcode.
     * @param zipCodeWithCountryId
     * @param fileIdsForDataToCheck
     * @return
     */
    public boolean doesRequestingZipCodeSetExistInTargetedFiles(Set<String> zipCodeWithCountryId,
                                                                String[] fileIdsForDataToCheck)
    {
        if(null == zipCodeWithCountryId || null == fileIdsForDataToCheck)
            return false;

        for(String fileId : fileIdsForDataToCheck)
        {
            Set<String> zipCodeData = this.zipCodeFileDatabase.get(fileId);

            if(null == zipCodeData || zipCodeData.size() <= 0)
            {
                logger.error("ZipCodeData does not exist in ZipCodeFileDataCache for fileid: {},skipping applying zipcode filters for selected ads...",fileId);
                continue;
            }

            Set<String> result = SetUtils.intersectSets(zipCodeWithCountryId,zipCodeData);
            if(null != result && result.size() > 0)
                return true;
        }

        return false;
    }

    /**
     * This class is responsible for looking up for any updates in country data file.
     */
    private class ZipCodeFileDatabaseReloadTimerTask extends TimerTask
    {
        private Logger cacheLogger = LogManager.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                buildDatabase();
            }
            catch (RefreshException re)
            {
                cacheLogger.error("RefreshException while loading zipcode data inside ZipCodeFileDataCache",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.zipCodeFileDatabase = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
            this.timer.purge();
        }
    }
}
