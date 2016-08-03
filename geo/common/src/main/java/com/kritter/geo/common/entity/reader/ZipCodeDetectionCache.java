package com.kritter.geo.common.entity.reader;

import com.kritter.geo.common.entity.ZipCodeInfoDetected;
import com.kritter.geo.common.entity.ZipCodeLatLongDatabase;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class queries sql database for zip code lat-long data
 * and loads into in-memory for detection by use of lat-long.
 */
public class ZipCodeDetectionCache
{
    private static final String ZIP_CODE_DATA_LOAD_QUERY = "select * from zipcode_lat_long " +
                                                           "where data_source_name in ";
    private static final String DELIMITER = String.valueOf((char)1);

    private String[] dataSourceNamesToUse;
    private Logger logger;

    //ZipCodeLatLongDatabase type being atomic reference, so that
    //can be updated atomically without use of lock etc.
    private AtomicReference<ZipCodeLatLongDatabase<ZipCodeLatLongDatabase.ZipCodeInfo>> zipCodeLatLongDatabase;
    private DatabaseManager databaseManager;
    private StringBuffer dataSourcesForQuery;
    private Timer timer;
    private TimerTask timerTask;

    public ZipCodeDetectionCache(String loggerName,
                                 String[] dataSourceNamesToUse,
                                 DatabaseManager databaseManager,
                                 long reloadFrequency)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceNamesToUse = dataSourceNamesToUse;
        this.databaseManager = databaseManager;
        this.dataSourcesForQuery = new StringBuffer();
        this.dataSourcesForQuery.append("(");

        for(String dataSource : dataSourceNamesToUse)
        {
            this.dataSourcesForQuery.append("'");
            this.dataSourcesForQuery.append(dataSource);
            this.dataSourcesForQuery.append("'");
            this.dataSourcesForQuery.append(",");
        }

        this.dataSourcesForQuery.deleteCharAt(this.dataSourcesForQuery.length()-1);
        this.dataSourcesForQuery.append(")");

        //allocate space for database
        this.zipCodeLatLongDatabase =
                        new AtomicReference<ZipCodeLatLongDatabase<ZipCodeLatLongDatabase.ZipCodeInfo>>();

        //load database for the first time.
        loadDatabase();

        //schedule for further reloading as per configured frequency.
        this.timer = new Timer();
        this.timerTask = new ZipCodeDatabaseReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    public List<ZipCodeInfoDetected> findNearestZipCodeInfo(double requestingLatitude,
                                                            double requestingLongitude)
    {

        this.logger.debug("Inside findNearestZipCodeInfo of ZipCodeDetectionCache...");

        ZipCodeLatLongDatabase.ZipCodeInfo query = new ZipCodeLatLongDatabase.ZipCodeInfo(requestingLatitude,
                                                                                          requestingLongitude,
                                                                                          null,
                                                                                          null,
                                                                                          (short)-1);

        logger.debug("Finding nearest zipcode for latitude {}, longitude {} ",requestingLatitude,requestingLongitude);

        Collection<ZipCodeLatLongDatabase.ZipCodeInfo> result =
                                            this.zipCodeLatLongDatabase.get().nearestNeighbourSearch(1, query);

        List<ZipCodeInfoDetected> list = new ArrayList<ZipCodeInfoDetected>();

        logger.debug("Got the result for nearest zip code search ");

        if(null != result && result.size() > 0)
        {
            logger.debug("Result for nearest zipcode is not null and has size {} ", result.size());

            Iterator<ZipCodeLatLongDatabase.ZipCodeInfo> iterator = result.iterator();

            while(iterator.hasNext())
            {
                ZipCodeLatLongDatabase.ZipCodeInfo zipCodeInfo = iterator.next();

                ZipCodeInfoDetected zipCodeInfoDetected =
                    new ZipCodeInfoDetected(zipCodeInfo.getZipCodeSet(),zipCodeInfo.getLatitude(),
                                            zipCodeInfo.getLongitude(),zipCodeInfo.getCountryCode(),
                                            zipCodeInfo.getAccuracy());

                list.add(zipCodeInfoDetected);
            }
        }

        return list;
    }

    private static String prepareKeyForZipCodeMap(String countryCode,double latitude,double longitude)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(countryCode);
        sb.append(DELIMITER);
        sb.append(latitude);
        sb.append(DELIMITER);
        sb.append(longitude);
        return sb.toString();
    }

    /**
     * This function queries mysql database and fetches all the zipcode information
     * for the datasources configured to be used.
     */
    private void loadDatabase()
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ZipCodeLatLongDatabase.ZipCodeInfo> dataList = new ArrayList<ZipCodeLatLongDatabase.ZipCodeInfo>();

        //keep a map with key as country_code,latitude,longitude and value as set of zipcodes.
        Map<String,Set<String>> zipCodeMap = new HashMap<String, Set<String>>();


        try
        {
            connection = this.databaseManager.getConnectionFromPool();

            StringBuffer completeQuery = new StringBuffer();
            completeQuery.append(ZIP_CODE_DATA_LOAD_QUERY);
            completeQuery.append(this.dataSourcesForQuery.toString());

            pstmt = connection.prepareStatement(completeQuery.toString());

            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String countryCode = rs.getString("country_code");
                String zipCode = rs.getString("zip_code");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                short accuracy = rs.getShort("accuracy");

                String lookupKey = prepareKeyForZipCodeMap(countryCode,latitude,longitude);
                Set<String> zipCodeSet = null;

                if(zipCodeMap.containsKey(lookupKey))
                {
                    zipCodeSet = zipCodeMap.get(lookupKey);
                }
                else
                {
                    zipCodeSet = new HashSet<String>();
                }
                zipCodeSet.add(zipCode);
                zipCodeMap.put(lookupKey,zipCodeSet);

                ZipCodeLatLongDatabase.ZipCodeInfo zipCodeInfo =
                        new ZipCodeLatLongDatabase.ZipCodeInfo(latitude,longitude,zipCodeSet,countryCode,accuracy);

                dataList.add(zipCodeInfo);
            }
        }
        catch (SQLException sqle)
        {
            this.logger.error("SQLException inside ZipCodeDetectionCache ",sqle);
        }
        finally
        {
            DBExecutionUtils.closeResources(connection,pstmt,rs);
        }

        ZipCodeLatLongDatabase<ZipCodeLatLongDatabase.ZipCodeInfo> zipCodeLatLongDatabaseTemp = new ZipCodeLatLongDatabase<ZipCodeLatLongDatabase.ZipCodeInfo>(dataList);

        //now replace old database with new constructed kd tree database.
        this.zipCodeLatLongDatabase.getAndSet(zipCodeLatLongDatabaseTemp);
    }

    /**
     * This class is responsible for reloading Zipcode database.
     */
    private class ZipCodeDatabaseReloadTimerTask extends TimerTask
    {
        private Logger cacheLogger = LoggerFactory.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                loadDatabase();
            }
            catch (Exception e)
            {
                cacheLogger.error("Exception while loading zipcode database in the class ZipCodeDatabaseReloadTimerTask",e);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.zipCodeLatLongDatabase = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}
