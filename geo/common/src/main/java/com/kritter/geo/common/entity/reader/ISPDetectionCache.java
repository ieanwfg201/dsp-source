package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.IpRangeKeyValue;
import com.kritter.geo.common.utils.GeoDetectionUtils;
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

public class ISPDetectionCache
{
    private Logger logger;
    Map<String,String> dataSourceWithDetectionFileMap;
    private String[] dataSourceValuesForLookupInOrder;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String,Long> lastRefreshStartTimeMap;

    //keep country data indexed on data-source and as sorted array for binary search.
    private Map<String,IpRangeKeyValue[]> sortedISPArrayAgainstDataSource;
    private static final String COLON = ":";

    private static final String DELIMITER = ",";

    public ISPDetectionCache(String loggerName,
                             Map<String,String> dataSourceWithDetectionFileMap,
                             String[] dataSourceValuesForLookupInOrder,
                             long reloadFrequencyForDetectionData) throws InitializationException
    {

        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceWithDetectionFileMap = dataSourceWithDetectionFileMap;
        this.dataSourceValuesForLookupInOrder = dataSourceValuesForLookupInOrder;

        this.sortedISPArrayAgainstDataSource = new ConcurrentHashMap<String, IpRangeKeyValue[]>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();

        //run data loading once then schedule the timer task.
        try
        {
            buildDatabaseFromFile();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside ISPDetectionCache",re);
            throw new InitializationException("RefreshException inside ISPDetectionCache",re);
        }

        this.timer = new Timer();
        this.timerTask = new ISPDataFileReloadTimerTask();
        this.timer.schedule(this.timerTask,
                            reloadFrequencyForDetectionData,
                            reloadFrequencyForDetectionData);

    }

    private void buildDatabaseFromFile() throws RefreshException
    {
        if(null == this.dataSourceWithDetectionFileMap)
            throw new RefreshException("ISPDataFilePaths with datasource provided are null, cannot proceed!");

        Iterator<Map.Entry<String,String>> detectionFileWithDatasourceIterator =
                this.dataSourceWithDetectionFileMap.entrySet().iterator();

        logger.debug("Inside ISPDetectionCache, going to load isp detection files ");
        long t = System.currentTimeMillis();

        while(detectionFileWithDatasourceIterator.hasNext())
        {
            Map.Entry<String,String> entry = detectionFileWithDatasourceIterator.next();

            logger.debug("Going to load data from file : {}", entry.getValue());

            String dataSource = entry.getKey();
            File inputDataFile = new File(entry.getValue());
            long fileModifyTime = inputDataFile.lastModified();

            Long lastRefreshStartTime = lastRefreshStartTimeMap.get(dataSource);

            if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
            {
                Long startTime = new Date().getTime();
                Map<String,List<IpRangeKeyValue>> ispDataForProcessing =
                        new HashMap<String, List<IpRangeKeyValue>>();

                BufferedReader br = null;

                List<IpRangeKeyValue> ispDataForProcessingIpRangeKeyValueList = ispDataForProcessing.get(dataSource);

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
                        String carrierId = lineParts[2];

                        InternetServiceProvider internetServiceProvider = new InternetServiceProvider
                                                                                    (
                                                                                     Integer.valueOf(carrierId),
                                                                                     dataSource
                                                                                    );

                        internetServiceProvider.setStartIpAddressValue(new BigInteger(startIp));
                        internetServiceProvider.setEndIpAddressValue(new BigInteger(endIp));

                        IpRangeKeyValue ipRangeKeyValue = new IpRangeKeyValue
                                                                           (
                                                                            BigInteger.valueOf(Long.valueOf(startIp)),
                                                                            BigInteger.valueOf(Long.valueOf(endIp)),
                                                                            Integer.valueOf(carrierId),
                                                                            dataSource
                                                                           );


                        if(null == ispDataForProcessingIpRangeKeyValueList)
                        {
                            ispDataForProcessingIpRangeKeyValueList = new ArrayList<IpRangeKeyValue>();
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

                        ispDataForProcessingIpRangeKeyValueList.add(ipRangeKeyValue);
                    }

                    if(null != ispDataForProcessingIpRangeKeyValueList)
                        ispDataForProcessing.put(entry.getKey(),ispDataForProcessingIpRangeKeyValueList);
                    //File reading is complete and we have the new data set.
                    //Since no exceptions till here so we can replace existing
                    //database with new one after sorting.
                    Set<Map.Entry<String,List<IpRangeKeyValue>>> inputSetForProcessing =
                            ispDataForProcessing.entrySet();

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
                                IpRangeKeyValue[] ispArrayForDataSource =
                                        value.toArray(new IpRangeKeyValue[value.size()]);

                                sortedISPArrayAgainstDataSource.put(key, ispArrayForDataSource);
                            }
                            catch (Exception e)
                            {
                                throw new RefreshException("Exception inside ISPDetectionCache ", e);
                            }
                        }
                    }

                    this.lastRefreshStartTimeMap.put(dataSource,startTime);
                }
                catch (IOException ioe)
                {
                    throw new RefreshException("IOException in ISPDetectionCache, cause: ",ioe);
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
                                                       "refresh in ISPDetectionCache: ", ioe);
                        }
                    }
                }
            }

            logger.debug("Total time taken in isp detection cache data loading is {}", (System.currentTimeMillis() - t));
        }
    }

    /**
     * This function detects isp for a given ip address.
     * @param ipAddress
     * @return
     */
    public InternetServiceProvider fetchISPForIpAddress(String ipAddress) throws Exception
    {
        // check whether its ipv4 or ipv6
        if (null == ipAddress)
            throw new Exception(
                    "IPAddress received is null inside fetchISPForIpAddress() of ISPDetectionCache");

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
            logger.debug("Value To Search for isp id is {}", valueToSearch);
            ipRangeKeyValue = fetchIpRangeKeyValue(-1, BigInteger.valueOf(valueToSearch));
        }

        if(null == ipRangeKeyValue)
            return null;

        return new InternetServiceProvider(ipRangeKeyValue.getEntityId(),ipRangeKeyValue.getDataSourceName());
    }

    private IpRangeKeyValue fetchIpRangeKeyValue(int startIndex, BigInteger valueToFind)
    {
        IpRangeKeyValue result = null;

        try
        {
            for(String dataSourceToLookup : dataSourceValuesForLookupInOrder)
            {
                IpRangeKeyValue[] databaseForDataSource = this.sortedISPArrayAgainstDataSource.get(dataSourceToLookup);

                if(null == databaseForDataSource){
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
    private class ISPDataFileReloadTimerTask extends TimerTask
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
                cacheLogger.error("RefreshException while loading country data inside " +
                        "CountryDataFileReloadTimerTask in the class CountryDetectionCache",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.sortedISPArrayAgainstDataSource = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}
