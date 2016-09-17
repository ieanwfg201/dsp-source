package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.constants.LatLonRadiusUnit;
import com.kritter.geo.common.entity.LatLonRadius;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * This class keeps all the ids of lat lon files which have
 * to be loaded into cache for detection.
 * It also loads all the custom lat lon files into memory for
 * detection purposes.
 * Each line represents one single lat lon radius range separated by ','
 *
 * Data line example: lat,lon,radius
 * 12,12,3
 */
public class FileBasedLatLonDetectionCache
{
    private Logger logger;
    private String queryToLoadCustomFileIds;
    private DatabaseManager databaseManager;
    private String latlonFileStorageDirectory;
    private ConcurrentHashMap<String,HashSet<LatLonRadius>> latlonradiusFileMap;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String,Long> lastRefreshStartTimeMap;
    private static final String DELIMITER = ",";

    public FileBasedLatLonDetectionCache(String loggerName,
                                      String queryToLoadCustomFileIds,
                                      DatabaseManager databaseManager,
                                      String latlonFileStorageDirectory,
                                      long reloadFrequency) throws InitializationException
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.queryToLoadCustomFileIds = queryToLoadCustomFileIds;
        this.databaseManager = databaseManager;
        this.latlonFileStorageDirectory = latlonFileStorageDirectory;
        this.latlonradiusFileMap = new ConcurrentHashMap<String,HashSet<LatLonRadius>>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();

        //run data loading once then schedule the timer task.
        try
        {
            buildDatabase();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside FileBasedLatLonDetectionCache ",re);
            throw new InitializationException("RefreshException inside FileBasedLatLonDetectionCache ",re);
        }

        this.timer = new Timer();
        this.timerTask = new FileBasedLatlonReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    /**
     * This function fetches all the custom latlon csv file ids from sql
     * database and then loads all the corresponding files to keep
     * in memory.
     * @throws RefreshException
     */
    public void buildDatabase() throws RefreshException
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Set<String> latlonradiusFileIdSet = new HashSet<String>();

        try
        {
            connection = databaseManager.getConnectionFromPool();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryToLoadCustomFileIds);

            while(resultSet.next())
            {
                String[] fileIdArray = ResultSetHelper.getResultSetStringArray(resultSet, "lat_lon_radius_file");
                if(null != fileIdArray)
                    latlonradiusFileIdSet.addAll(new HashSet<String>(Arrays.asList(fileIdArray)));
            }
        }
        catch (SQLException sqle)
        {
            logger.error("SQLException inside FileBasedLatLonDetectionCache in getting connection ",sqle);
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
                logger.error("SQLException inside FileBasedLatLonDetectionCache in closing connection ",e);
            }
        }

        //now load all the files which have been modified since last reload.
        for(String fileId : latlonradiusFileIdSet)
        {
            StringBuffer filePath = new StringBuffer(this.latlonFileStorageDirectory);
            filePath.append(fileId);
            HashSet<LatLonRadius> tempLatLonSet = new HashSet<LatLonRadius>();
            File inputDataFile = new File(filePath.toString());

            if(!inputDataFile.exists())
            {
                logger.error("File does not exist inside FileBasedLatLonDetectionCache, the file looked up was: {}", filePath.toString());
                continue;
            }

            long fileModifyTime = inputDataFile.lastModified();
            Long lastRefreshStartTime = this.lastRefreshStartTimeMap.get(fileId);

            //this means file has been modified since last reload.
            if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
            {
                Long startTime = new Date().getTime();
                BufferedReader br = null;

                try
                {
                    br = new BufferedReader(new FileReader(inputDataFile));
                    String line = null;

                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(DELIMITER);

                        if(null == lineParts || lineParts.length != 3)
                        {
                            logger.error("The line inside latlon range file " +
                                                       inputDataFile.getPath() + " is invalid, line : " + line);
                        }else{
                        	try{
                        		Double lat = Double.parseDouble(lineParts[0]);
                        		Double lon = Double.parseDouble(lineParts[1]);
                        		Double radius = Double.parseDouble(lineParts[2]);
                        		LatLonRadius llr = new LatLonRadius();
                        		llr.setLatitude(lat);
                        		llr.setLongitude(lon);
                        		llr.setRadius(radius);
                        		tempLatLonSet.add(llr);
                        	}catch(Exception e){
                        		logger.error("The line inside latlon range file " +
                                        inputDataFile.getPath() + " is invalid, line : " + line);
                        	}
                        }
                    }

                    this.lastRefreshStartTimeMap.put(fileId,startTime);
                }
                catch (IOException ioe)
                {
                    throw new RefreshException("IOException in FileBasedLatLonDetectionCache, cause: ",ioe);
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
                                                       "refresh in FileBasedLatLonDetectionCache: ", ioe);
                        }
                    }
                }
            }
            if(tempLatLonSet != null && tempLatLonSet.size()>0){
            	latlonradiusFileMap.put(fileId,tempLatLonSet);
            }
        }
    }

    public boolean doesLatLonExistsinLatLonFiles(Double lat,Double lon,String[] fileIdsForDataToCheck,
    		LatLonRadiusUnit llru)
    {
        if(null == lat || null == lon || null == fileIdsForDataToCheck)
            return false;
        try
        {
            for(String fileId : fileIdsForDataToCheck)
            {
            	HashSet<LatLonRadius> thisSet= this.latlonradiusFileMap.get(fileId);
                for(LatLonRadius llr:thisSet){
                	double distanceFromRequestingPosition =
                        GeoCommonUtils.haversineDistanceInMiles(llr.getLatitude(), llr.getLongitude(),
                        		lat,
                        		lon);
                	double radius=llr.getRadius();
                	if(llru==LatLonRadiusUnit.KM){
                    	/*Converting to miles*/
                    	radius = radius*0.621371;
                	}
                	if(distanceFromRequestingPosition <= radius){
                		return true;
                	}
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception inside doesLatLonExistsinLatLonFiles in getting lat lon address numeric value.{} , {}", lat,lon);
            return false;
        }
        return false;
    }

    /**
     * This class is responsible for looking up for any updates in country data file.
     */
    private class FileBasedLatlonReloadTimerTask extends TimerTask
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
                cacheLogger.error("RefreshException while loading custom latlon data inside FileBasedLatLonDetectionCache in the class CountryDetectionCache ",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.latlonradiusFileMap = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
            this.timer.purge();
        }
    }
}
