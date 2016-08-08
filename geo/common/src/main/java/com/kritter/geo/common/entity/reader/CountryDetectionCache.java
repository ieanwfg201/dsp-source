package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.CountryCodesEntity;
import com.kritter.geo.common.entity.CountryTwoLetterCodeSecondaryIndex;
import com.kritter.geo.common.entity.IpRangeKeyValue;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.geo.common.utils.GeoDetectionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible to read data file for country detection.
 * Primarily the data file would have
 * [ip_range -> country_id_sql_integer -> data_source_name]
 * format [ip_start_long_value control-A ip_end_long_value control-A country_id control-A,data_source_name]
 *
 * This class reads the data file and keeps IpRangeKeyValue as the index for
 * lookup in real time for a given ip.
 */
public class CountryDetectionCache
{
    private Logger logger;
    Map<String,String> dataSourceWithDetectionFileMap;
    private String[] dataSourceValuesForLookupInOrder;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String,Long> lastRefreshStartTimeMap;

    //keep country data indexed on data-source and as sorted array for binary search.
    private Map<String,IpRangeKeyValue[]> sortedCountryArrayAgainstDataSource;
    private static final String COLON = ":";

    private static final String DELIMITER = ",";
    private Map<String,Map<Integer,Country>> countryDataAgainstIdForAllDatasource;
    private CountryCodesMappingsCache countryCodesMappingsCache;

    public CountryDetectionCache(String loggerName,
                                 Map<String,String> dataSourceWithDetectionFileMap,
                                 String[] dataSourceValuesForLookupInOrder,
                                 long reloadFrequencyForDetectionData) throws InitializationException
    {

        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceWithDetectionFileMap = dataSourceWithDetectionFileMap;
        this.dataSourceValuesForLookupInOrder = dataSourceValuesForLookupInOrder;

        this.sortedCountryArrayAgainstDataSource = new ConcurrentHashMap<String, IpRangeKeyValue[]>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();
        //run data loading once then schedule the timer task.
        try
        {
            buildDatabaseFromFile();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside CountryDetectionCache",re);
            throw new InitializationException("RefreshException inside CountryDetectionCache",re);
        }

        this.timer = new Timer();
        this.timerTask = new CountryDataFileReloadTimerTask();
        this.timer.schedule(this.timerTask,
                            reloadFrequencyForDetectionData,
                            reloadFrequencyForDetectionData);
    }

    public CountryDetectionCache(String loggerName,
                                 Map<String,String> dataSourceWithDetectionFileMap,
                                 String[] dataSourceValuesForLookupInOrder,
                                 long reloadFrequencyForDetectionData,
                                 boolean scheduleTimerAtInstanceCreation) throws InitializationException
    {

        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceWithDetectionFileMap = dataSourceWithDetectionFileMap;
        this.dataSourceValuesForLookupInOrder = dataSourceValuesForLookupInOrder;

        this.sortedCountryArrayAgainstDataSource = new ConcurrentHashMap<String, IpRangeKeyValue[]>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();
        //run data loading once then schedule the timer task.
        try
        {
            buildDatabaseFromFile();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside CountryDetectionCache",re);
            throw new InitializationException("RefreshException inside CountryDetectionCache",re);
        }

        if(scheduleTimerAtInstanceCreation)
        {
            this.timer = new Timer();
            this.timerTask = new CountryDataFileReloadTimerTask();
            this.timer.schedule(this.timerTask,
                    reloadFrequencyForDetectionData,
                    reloadFrequencyForDetectionData);
        }
    }

    public CountryDetectionCache(String loggerName,
                                 Map<String,String> dataSourceWithDetectionFileMap,
                                 String[] dataSourceValuesForLookupInOrder,
                                 CountryCodesMappingsCache countryCodesMappingsCache,
                                 DatabaseManager databaseManager) throws InitializationException
    {

        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceWithDetectionFileMap = dataSourceWithDetectionFileMap;
        this.dataSourceValuesForLookupInOrder = dataSourceValuesForLookupInOrder;

        this.sortedCountryArrayAgainstDataSource = new ConcurrentHashMap<String, IpRangeKeyValue[]>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();
        //run data loading once then schedule the timer task.
        try
        {
            buildDatabaseFromFile();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside CountryDetectionCache",re);
            throw new InitializationException("RefreshException inside CountryDetectionCache",re);
        }

        try
        {

            this.countryDataAgainstIdForAllDatasource = new HashMap<String, Map<Integer, Country>>();

            for (String dataSource : this.dataSourceValuesForLookupInOrder)
            {
                this.countryDataAgainstIdForAllDatasource.
                        put(
                            dataSource,
                            GeoCommonUtils.fetchCountryDataAgainstIdFromSqlDatabaseForDataSource
                                                    (databaseManager.getConnectionFromPool(),dataSource)
                           );
            }
        }
        catch (Exception e)
        {
            logger.error("Exception inside CountryDetectionCache ",e);
            throw new InitializationException("Could not initialize country detection cache ", e);
        }

        this.countryCodesMappingsCache = countryCodesMappingsCache;
    }

    private void buildDatabaseFromFile() throws RefreshException
    {
        if(null == this.dataSourceWithDetectionFileMap)
            throw new RefreshException("CountryDataFilePaths with datasource provided are null, cannot proceed!");

        Iterator<Map.Entry<String,String>> detectionFileWithDatasourceIterator =
                                                            this.dataSourceWithDetectionFileMap.entrySet().iterator();

        logger.debug("Inside CountryDetectionCache, going to load country detection files ");
        long t = System.currentTimeMillis();

        while(detectionFileWithDatasourceIterator.hasNext())
        {
            Map.Entry<String,String> entry = detectionFileWithDatasourceIterator.next();

            logger.debug("Going to load data from file : {}", entry.getValue());

            File inputDataFile = new File(entry.getValue());
            long fileModifyTime = inputDataFile.lastModified();
            String dataSource = entry.getKey();

            Long lastRefreshStartTime = lastRefreshStartTimeMap.get(dataSource);

            if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
            {
                Long startTime = new Date().getTime();
                Map<String,List<IpRangeKeyValue>> countryDataForProcessing =
                                                                       new HashMap<String, List<IpRangeKeyValue>>();

                BufferedReader br = null;


                List<IpRangeKeyValue> countryDataForProcessingIpRangeKeyValueList =
                                                                         countryDataForProcessing.get(dataSource);

                try
                {
                    br = new BufferedReader(new FileReader(inputDataFile));
                    String line = null;

                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(DELIMITER);

                        if(lineParts.length != 3)
                            throw new RefreshException("The line in file is incorrect, not of length 3 : " + line);

                        String startIp = lineParts[0];
                        String endIp = lineParts[1];
                        String countryId = lineParts[2];

                        Country country = new Country(Integer.valueOf(countryId),dataSource);

                        country.setStartIpAddressValue(new BigInteger(startIp));
                        country.setEndIpAddressValue(new BigInteger(endIp));

                        IpRangeKeyValue ipRangeKeyValue = new IpRangeKeyValue
                                                                           (
                                                                            new BigInteger(startIp),
                                                                            new BigInteger(endIp),
                                                                            Integer.valueOf(countryId),
                                                                            dataSource
                                                                           );


                        if(null == countryDataForProcessingIpRangeKeyValueList)
                        {
                            countryDataForProcessingIpRangeKeyValueList = new ArrayList<IpRangeKeyValue>();
                        }

                        /****-------------------Hard check for data duplication.------------------------- ***/
                        //TODO - later on if hard check is required,uncomment following.
                        //since loader is responsible for correct data generation no need to check for duplicacy.
                        //also it takes a lot of time, not to be recommended in online system.
                        //if(countryDataForProcessingIpRangeKeyValueList.indexOf(ipRangeKeyValue) != -1)
                            //throw new RefreshException("Exception inside CountryDetectionCache, duplicate " +
                                                       //"range found as startip : " + startIp + " , endip: " +
                                                      //endIp + " for datasource: " + dataSource);
                        /****-------------------Hard check for data duplication.------------------------- ***/

                        countryDataForProcessingIpRangeKeyValueList.add(ipRangeKeyValue);
                    }

                    if(null != countryDataForProcessingIpRangeKeyValueList)
                        countryDataForProcessing.put(entry.getKey(),countryDataForProcessingIpRangeKeyValueList);

                    //File reading is complete and we have the new data set.
                    //Since no exceptions till here so we can replace existing
                    //database with new one after sorting.
                    Set<Map.Entry<String,List<IpRangeKeyValue>>> inputSetForProcessing =
                                                                                   countryDataForProcessing.entrySet();

                    if(null != inputSetForProcessing)
                    {
                        Iterator<Map.Entry<String,List<IpRangeKeyValue>>> it = inputSetForProcessing.iterator();

                        while (it.hasNext())
                        {
                            try
                            {
                                Map.Entry<String,List<IpRangeKeyValue>> inputSetEntry = it.next();

                                String key = entry.getKey();
                                List<IpRangeKeyValue> value = inputSetEntry.getValue();
                                GeoDetectionUtils.sortIpRangeKeyValueSet(value);

                                //form arrays and put into master data store.
                                IpRangeKeyValue[] countryArrayForDataSource =
                                                                      value.toArray(new IpRangeKeyValue[value.size()]);

                                sortedCountryArrayAgainstDataSource.put(key,countryArrayForDataSource);
                            }
                            catch (Exception e)
                            {
                                throw new RefreshException("Exception inside CountryDetectionCache ", e);
                            }
                        }
                    }

                    this.lastRefreshStartTimeMap.put(dataSource,startTime);
                }
                catch (IOException ioe)
                {
                    throw new RefreshException("IOException in CountryDetectionCache, cause: ",ioe);
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
                                                       "refresh in CountryDetectionCache: ", ioe);
                        }
                    }
                }
            }

            logger.debug("Total time taken in country detection cache data loading is {}", (System.currentTimeMillis() - t));
        }
    }

    /**
     * This function detects country for a given ip address.
     * @param ipAddress
     * @return
     */
    public Country findCountryForIpAddress(String ipAddress) throws Exception
    {
        // check whether its ipv4 or ipv6
        if (null == ipAddress)
            throw new Exception(
                    "IPAddress received is null inside findCountryForIpAddress() of CountryDetectionCache");

        IpRangeKeyValue ipRangeKeyValue = null;

        // ipv6
        if (ipAddress.contains(COLON))
        {
            BigInteger valueToSearch = GeoDetectionUtils.fetchBigIntValueForIPV6(ipAddress);
            ipRangeKeyValue = fetchIpRangeKeyValue(-1,valueToSearch);

        }

        // ipv4
        else
        {
            long valueToSearch = GeoDetectionUtils.fetchLongValueForIPV4(ipAddress);
            logger.debug("Value To Search for country id is {}", valueToSearch);
            ipRangeKeyValue = fetchIpRangeKeyValue(-1, BigInteger.valueOf(valueToSearch));
        }

        if(null == ipRangeKeyValue)
            return null;

        Country country = new Country(ipRangeKeyValue.getEntityId(),ipRangeKeyValue.getDataSourceName());

        try
        {
            if (null != this.countryDataAgainstIdForAllDatasource)
            {
                Map<Integer, Country> countryMap =
                                this.countryDataAgainstIdForAllDatasource.get(ipRangeKeyValue.getDataSourceName());

                Country countryFromDatabase = countryMap.get(ipRangeKeyValue.getEntityId());

                if (null != countryFromDatabase && null != this.countryCodesMappingsCache)
                {
                    logger.debug("Country code fetched from database is: {} ", countryFromDatabase.getCountryCode());

                    Set<String> threeLetterCodes =
                            this.countryCodesMappingsCache.query
                                    (new CountryTwoLetterCodeSecondaryIndex(countryFromDatabase.getCountryCode()));

                    for(String code : threeLetterCodes)
                    {
                        logger.debug("Country three letter code fetched: {} ", code);
                        country.setCountryCodeThreeLetter(code);
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception in getting three letter code for the detected country.",e);
        }

        return country;
    }

    /**
     * This function detects country for a given numeric value.
     * @param ipAddressNumericValueToSearch
     * @return
     */
    public Country findCountryForIpAddress(BigInteger ipAddressNumericValueToSearch) throws Exception
    {
        IpRangeKeyValue ipRangeKeyValue = null;
            ipRangeKeyValue = fetchIpRangeKeyValue(-1,ipAddressNumericValueToSearch);

        if(null == ipRangeKeyValue)
            return null;

        return new Country(ipRangeKeyValue.getEntityId(),ipRangeKeyValue.getDataSourceName());
    }

    private IpRangeKeyValue fetchIpRangeKeyValue(int startIndex, BigInteger valueToFind)
    {
        IpRangeKeyValue result = null;

        try
        {

            for(String dataSourceToLookup : dataSourceValuesForLookupInOrder)
            {
                IpRangeKeyValue[] databaseForDataSource = this.sortedCountryArrayAgainstDataSource.
                                                                                        get(dataSourceToLookup);


                if(null == databaseForDataSource) {
                    logger.debug("database is null for datasrc {}", dataSourceToLookup);
                    return result;
                }

                int index = GeoDetectionUtils.
                                              fetchIndexByBinarySearchForIPRange(databaseForDataSource,
                                                      startIndex,
                                                      databaseForDataSource.length,
                                                      valueToFind);

                if (index != -1)
                    return databaseForDataSource[index];
            }
        }
        finally
        {
        }

        return result;
    }

    /**
     * This class is responsible for looking up for any updates in country data file.
     */
    private class CountryDataFileReloadTimerTask extends TimerTask
    {
        private Logger cacheLogger = LoggerFactory.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                buildDatabaseFromFile();
            }
            catch (RefreshException re)
            {
                cacheLogger.error("RefreshException while loading country data inside CountryDataFileReloadTimerTask in the class CountryDetectionCache",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.sortedCountryArrayAgainstDataSource = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}
