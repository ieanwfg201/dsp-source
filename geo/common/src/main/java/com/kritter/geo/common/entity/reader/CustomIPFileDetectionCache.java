package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.IpRangeKeyValue;
import com.kritter.geo.common.utils.GeoDetectionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class keeps all the ids of custom ip files which have
 * to be loaded into cache for detection.
 * It also loads all the custom ip files into memory for
 * detection purposes.
 * Each line represents one single ip range separated by '-',could be ipv4 or ipv6.
 *
 * Data line example: startip-endip
 * 12323232-423232398
 */
public class CustomIPFileDetectionCache
{
    private Logger logger;
    private String queryToLoadCustomFileIds;
    private DatabaseManager databaseManager;
    private String customIPFileStorageDirectory;
    private Map<String,IpRangeKeyValue[]> customIPFileDataAgainstIdMap;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String,Long> lastRefreshStartTimeMap;
    private static final String COLON = ":";
    private static final String DELIMITER = "-";

    public CustomIPFileDetectionCache(String loggerName,
                                      String queryToLoadCustomFileIds,
                                      DatabaseManager databaseManager,
                                      String customIPFileStorageDirectory,
                                      long reloadFrequency) throws InitializationException
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.queryToLoadCustomFileIds = queryToLoadCustomFileIds;
        this.databaseManager = databaseManager;
        this.customIPFileStorageDirectory = customIPFileStorageDirectory;
        this.customIPFileDataAgainstIdMap = new ConcurrentHashMap<String, IpRangeKeyValue[]>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();

        //run data loading once then schedule the timer task.
        try
        {
            buildDatabase();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside CustomIPFileDetectionCache ",re);
            throw new InitializationException("RefreshException inside CustomIPFileDetectionCache ",re);
        }

        this.timer = new Timer();
        this.timerTask = new CustomIpDataFileReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    /**
     * This function fetches all the custom ip csv file ids from sql
     * database and then loads all the corresponding files to keep
     * in memory.
     * @throws RefreshException
     */
    public void buildDatabase() throws RefreshException
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Set<String> customIpFileIdSet = new HashSet<String>();
        Map<String,IpRangeKeyValue[]> customIPFileDataAgainstIdMapTemp = new HashMap<String, IpRangeKeyValue[]>();

        try
        {
            connection = databaseManager.getConnectionFromPool();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryToLoadCustomFileIds);

            while(resultSet.next())
            {
                String[] fileIdArray = ResultSetHelper.getResultSetStringArray(resultSet, "custom_ip_file_id_set");
                if(null != fileIdArray)
                    customIpFileIdSet.addAll(new HashSet<String>(Arrays.asList(fileIdArray)));
            }
        }
        catch (SQLException sqle)
        {
            logger.error("SQLException inside CustomIPFileDetectionCache in getting connection ",sqle);
        }
        finally
        {
            //close statement and result set handles first.
            //return connection to the pool.
            try
            {
                if(null != resultSet)
                    resultSet.close();

                if(null != statement)
                    statement.close();

                if(null != connection)
                    connection.close();
            }
            catch (SQLException e)
            {
                logger.error("SQLException inside CustomIPFileDetectionCache in closing connection ",e);
            }
        }

        //now load all the files which have been modified since last reload.
        for(String fileId : customIpFileIdSet)
        {
            StringBuffer filePath = new StringBuffer(this.customIPFileStorageDirectory);
            filePath.append(fileId);

            File inputDataFile = new File(filePath.toString());

            if(!inputDataFile.exists())
            {
                logger.error("File does not exist inside CustomIPFileDetectionCache, the file looked up was: {}", filePath.toString());
                continue;
            }

            long fileModifyTime = inputDataFile.lastModified();
            Long lastRefreshStartTime = this.lastRefreshStartTimeMap.get(fileId);

            //this means file has been modified since last reload.
            if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
            {
                Long startTime = new Date().getTime();
                List<IpRangeKeyValue> inputData = new ArrayList<IpRangeKeyValue>();

                BufferedReader br = null;

                try
                {
                    br = new BufferedReader(new FileReader(inputDataFile));
                    String line = null;

                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(DELIMITER);

                        if(null == lineParts || lineParts.length != 2)
                        {
                            throw new RefreshException("The line inside custom ip range file " +
                                                       inputDataFile.getPath() + " is invalid, line : " + line);
                        }

                        BigInteger startIpValue = null;
                        BigInteger endIpValue = null;

                        if(line.contains(COLON))
                        {
                            try
                            {
                                startIpValue = GeoDetectionUtils.fetchBigIntValueForIPV6(lineParts[0]);
                                endIpValue = GeoDetectionUtils.fetchBigIntValueForIPV6(lineParts[1]);
                            }
                            catch(Exception e)
                            {
                                throw new RefreshException("The input ip range from file " +
                                                           filePath.toString()             +
                                                           " is invalid ipv6, line: "      +
                                                           line, e);
                            }
                        }
                        else
                        {
                            try
                            {
                                startIpValue = BigInteger.
                                                    valueOf(GeoDetectionUtils.fetchLongValueForIPV4(lineParts[0]));
                                endIpValue = BigInteger.
                                                    valueOf(GeoDetectionUtils.fetchLongValueForIPV4(lineParts[1]));
                            }
                            catch (Exception e)
                            {
                                throw new RefreshException("The input ip range from file " +
                                                           filePath.toString()             +
                                                           " is invalid ipv4, line: " + line);
                            }
                        }

                        //prepare new entry , the entity id and data source do not matter here.
                        IpRangeKeyValue ipRangeKeyValue = new IpRangeKeyValue(startIpValue,endIpValue,-1,null);
                        inputData.add(ipRangeKeyValue);
                    }

                    GeoDetectionUtils.sortIpRangeKeyValueSet(inputData);
                    inputData = GeoDetectionUtils.mergeAndCleanupIpRanges(inputData);
                    logger.debug("Custom IP ranges:");
                    for(IpRangeKeyValue rangeKeyValue : inputData) {
                        logger.debug("range key value = {}, {}", rangeKeyValue.getStartIpValue(), rangeKeyValue.getEndIpValue());
                    }
                    customIPFileDataAgainstIdMapTemp.put(
                                                         fileId,
                                                         inputData.toArray(new IpRangeKeyValue[inputData.size()])
                                                        );

                    this.lastRefreshStartTimeMap.put(fileId,startTime);
                }
                catch (IOException ioe)
                {
                    throw new RefreshException("IOException in CustomIPFileDetectionCache, cause: ",ioe);
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
                                                       "refresh in CustomIPFileDetectionCache: ", ioe);
                        }
                    }
                }
            }
        }

        //now all the modified data has been loaded , feed it into the main data map.
        for(String modifyDataForFileId : customIPFileDataAgainstIdMapTemp.keySet())
        {
            this.customIPFileDataAgainstIdMap.put(modifyDataForFileId,
                                                      customIPFileDataAgainstIdMapTemp.get(modifyDataForFileId));
        }
    }

    public boolean doesIPExistInCustomIPFiles(String ipAddress,String[] fileIdsForDataToCheck)
    {
        if(null == ipAddress || null == fileIdsForDataToCheck)
            return false;

        BigInteger ipValueToFind = null;

        try
        {
            if(ipAddress.contains(COLON))
                ipValueToFind = GeoDetectionUtils.fetchBigIntValueForIPV6(ipAddress);
            else
                ipValueToFind = BigInteger.valueOf(GeoDetectionUtils.fetchLongValueForIPV4(ipAddress));
        }
        catch (Exception e)
        {
            logger.error("Exception inside doesIPExistInCustomIPFiles in getting ip address numeric value.{}", ipAddress);
        }

        for(String fileId : fileIdsForDataToCheck)
        {
            IpRangeKeyValue[] customIpData = this.customIPFileDataAgainstIdMap.get(fileId);

            if(null == customIpData || customIpData.length <= 0)
            {
                logger.error("Custom ip data does not exist in CustomIPFileDetectionCache for fileid: {},skipping applying custom ip filters to it from selected ads...", fileId);
                continue;
            }

            int index = -1;

            if(null != customIpData)
                index = GeoDetectionUtils.fetchIndexByBinarySearchForIPRange(
                                                                             customIpData,
                                                                             -1,
                                                                             customIpData.length,
                                                                             ipValueToFind
                                                                            );

            logger.debug("Index found inside CustomIPFileDetectionCache is : {}", index);

            if(index != -1)
                return true;
        }

        return false;
    }

    /**
     * This class is responsible for looking up for any updates in country data file.
     */
    private class CustomIpDataFileReloadTimerTask extends TimerTask
    {
        private Logger cacheLogger = LoggerFactory.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                buildDatabase();
            }
            catch (RefreshException re)
            {
                cacheLogger.error("RefreshException while loading custom ip data inside CustomIPFileDetectionCache in the class CountryDetectionCache ",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.customIPFileDataAgainstIdMap = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
            this.timer.purge();
        }
    }
}
