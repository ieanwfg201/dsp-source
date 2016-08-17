package com.kritter.geo.common.entity.reader;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.IpRangeKeyValue;
import com.kritter.geo.common.entity.State;
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
 * This class keeps data required for state detection via usage of ip address.
 */
public class StateDetectionCache
{
    private Logger logger;
    Map<String,String> dataSourceWithDetectionFileMap;
    private String[] dataSourceValuesForLookupInOrder;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String,Long> lastRefreshStartTimeMap;

    //keep state data indexed on data-source and as sorted array for binary search.
    private Map<String,IpRangeKeyValue[]> sortedStateArrayAgainstDataSource;
    private static final String COLON = ":";

    private static final String DELIMITER = ",";

    public StateDetectionCache(String loggerName,
                               Map<String,String> dataSourceWithDetectionFileMap,
                               String[] dataSourceValuesForLookupInOrder,
                               long reloadFrequencyForDetectionData) throws InitializationException
    {

        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceWithDetectionFileMap = dataSourceWithDetectionFileMap;
        this.dataSourceValuesForLookupInOrder = dataSourceValuesForLookupInOrder;

        this.sortedStateArrayAgainstDataSource = new ConcurrentHashMap<String, IpRangeKeyValue[]>();

        this.lastRefreshStartTimeMap = new HashMap<String, Long>();

        //run data loading once then schedule the timer task.
        try
        {
            buildDatabaseFromFile();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside StateDetectionCache",re);
            throw new InitializationException("RefreshException inside StateDetectionCache",re);
        }

        this.timer = new Timer();
        this.timerTask = new StateDataFileReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequencyForDetectionData,reloadFrequencyForDetectionData);
    }

    private void buildDatabaseFromFile() throws RefreshException
    {
        if(null == this.dataSourceWithDetectionFileMap)
            throw new RefreshException("StateDataFilePaths with datasource provided are null, cannot proceed!");

        Iterator<Map.Entry<String,String>> detectionFileWithDatasourceIterator =
                                                            this.dataSourceWithDetectionFileMap.entrySet().iterator();

        logger.debug("Inside StateDetectionCache, going to load state detection files ");
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
                Map<String,List<IpRangeKeyValue>> stateDataForProcessing =
                                                                        new HashMap<String, List<IpRangeKeyValue>>();

                BufferedReader br = null;

                List<IpRangeKeyValue> stateDataForProcessingIpRangeKeyValueList =
                                                                        stateDataForProcessing.get(dataSource);

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
                        String stateId = lineParts[2];

                        State state = new State(Integer.valueOf(stateId),dataSource);

                        state.setStartIpAddressValue(new BigInteger(startIp));
                        state.setEndIpAddressValue(new BigInteger(endIp));

                        IpRangeKeyValue ipRangeKeyValue = new IpRangeKeyValue
                                                                            (
                                                                                    new BigInteger(startIp),
                                                                                    new BigInteger(endIp),
                                                                                    Integer.valueOf(stateId),
                                                                                    dataSource
                                                                            );


                        if(null == stateDataForProcessingIpRangeKeyValueList)
                        {
                            stateDataForProcessingIpRangeKeyValueList = new ArrayList<IpRangeKeyValue>();
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

                        stateDataForProcessingIpRangeKeyValueList.add(ipRangeKeyValue);
                    }

                    if(null != stateDataForProcessingIpRangeKeyValueList)
                        stateDataForProcessing.put(entry.getKey(),stateDataForProcessingIpRangeKeyValueList);

                    //File reading is complete and we have the new data set.
                    //Since no exceptions till here so we can replace existing
                    //database with new one after sorting.
                    Set<Map.Entry<String,List<IpRangeKeyValue>>> inputSetForProcessing =
                                                                                stateDataForProcessing.entrySet();

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
                                IpRangeKeyValue[] stateArrayForDataSource =
                                                                    value.toArray(new IpRangeKeyValue[value.size()]);

                                sortedStateArrayAgainstDataSource.put(key,stateArrayForDataSource);
                            }
                            catch (Exception e)
                            {
                                throw new RefreshException("Exception inside StateDetectionCache ", e);
                            }
                        }
                    }

                    this.lastRefreshStartTimeMap.put(dataSource,startTime);
                }
                catch (IOException ioe)
                {
                    throw new RefreshException("IOException in StateDetectionCache, cause: ",ioe);
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
                                                       "refresh in StateDetectionCache: ", ioe);
                        }
                    }
                }
            }

            logger.debug("Total time taken in state detection cache data loading is {}", (System.currentTimeMillis() - t));
        }
    }

    /**
     * This function detects state for a given ip address.
     * @param ipAddress
     * @return
     */
    public State findStateForIpAddress(String ipAddress) throws Exception
    {
        // check whether its ipv4 or ipv6
        if (null == ipAddress){
                    logger.error("IPAddress received is null inside findStateForIpAddress() of StateDetectionCache");
                    return null;
        }

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
            logger.debug("Value To Search for state id is {}", valueToSearch);
            ipRangeKeyValue = fetchIpRangeKeyValue(-1, BigInteger.valueOf(valueToSearch));
        }

        if(null == ipRangeKeyValue)
            return null;

        return new State(ipRangeKeyValue.getEntityId(),ipRangeKeyValue.getDataSourceName());
    }

    /**
     * This function detects state for a given numeric value.
     * @param ipAddressNumericValueToSearch
     * @return
     */
    public State findStateForIpAddress(BigInteger ipAddressNumericValueToSearch) throws Exception
    {
        IpRangeKeyValue ipRangeKeyValue = null;
        ipRangeKeyValue = fetchIpRangeKeyValue(-1,ipAddressNumericValueToSearch);

        if(null == ipRangeKeyValue)
            return null;

        return new State(ipRangeKeyValue.getEntityId(),ipRangeKeyValue.getDataSourceName());
    }

    private IpRangeKeyValue fetchIpRangeKeyValue(int startIndex, BigInteger valueToFind)
    {
        IpRangeKeyValue result = null;

        try
        {

            for(String dataSourceToLookup : dataSourceValuesForLookupInOrder)
            {
                IpRangeKeyValue[] databaseForDataSource = this.sortedStateArrayAgainstDataSource.
                                                                                            get(dataSourceToLookup);

                if(null == databaseForDataSource)
                {
                    logger.debug("Database is null for datasrc {}", dataSourceToLookup);
                    return result;
                }

                int index = GeoDetectionUtils.
                        fetchIndexByBinarySearchForIPRange(
                                                           databaseForDataSource,
                                                           startIndex,
                                                           databaseForDataSource.length,
                                                           valueToFind
                                                          );

                if (index != -1)
                    return databaseForDataSource[index];
            }
        }
        finally{}

        return result;
    }

    /**
     * This class is responsible for looking up for any updates in state data file.
     */
    private class StateDataFileReloadTimerTask extends TimerTask
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
                cacheLogger.error("RefreshException while loading country data inside StateDataFileReloadTimerTask " +
                                  "in the class StateDetectionCache",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.sortedStateArrayAgainstDataSource = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}