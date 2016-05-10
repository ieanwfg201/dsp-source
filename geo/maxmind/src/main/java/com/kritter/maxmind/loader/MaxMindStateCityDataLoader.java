package com.kritter.maxmind.loader;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.maxmind.entity.MaxmindCountryStateCityData;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * This class loads Maxmind's state and city database to mysql
 * and creates a detection file as:
 * start_ip_numeric_value,end_ip_numeric_Value,state_id_from_sql,city_id_from_sql.
 */
public class MaxMindStateCityDataLoader implements ThirdPartyDataLoader
{
    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private Long lastRefreshStartTime = null;
    private String stateCityBlockIdDatabaseFileFullPath;
    private String stateCityDatabaseFileFullPath;
    private String stateCodeNameDatabaseFileFullPath;
    private String stateCityDetectionFile;
    private String stateCityPreparationFile;
    private DatabaseManager databaseManager;
    private int batchSizeForSqlInsertion;
    private static final String QUOTE_DELIMITER = "\"";
    private static final String COMMA_DELIMITER = ",";
    private static final String EMPTY_STRING = "";
    private static final String CONTROL_A = String.valueOf((char)1);
    private static final String STATE_SQL_DATA_INSERT_QUERY = "insert into state(country_id,state_name," +
                                                              "data_source_name,modified_on) values ";
    private static final String CITY_SQL_DATA_INSERT_QUERY = "insert into city(state_id,city_name," +
                                                             "data_source_name,modified_on) values ";

    public MaxMindStateCityDataLoader(
                                      String loggerName,
                                      String dataSourceName,
                                      long reloadFrequency,
                                      String stateCityBlockIdDatabaseFileFullPath,
                                      String stateCityDatabaseFileFullPath,
                                      String stateCodeNameDatabaseFileFullPath,
                                      String stateCityDetectionFile,
                                      String stateCityPreparationFile,
                                      DatabaseManager databaseManager,
                                      int batchSizeForSqlInsertion
                                     ) throws Exception
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.stateCityBlockIdDatabaseFileFullPath = stateCityBlockIdDatabaseFileFullPath;
        this.stateCityDatabaseFileFullPath = stateCityDatabaseFileFullPath;
        this.stateCodeNameDatabaseFileFullPath = stateCodeNameDatabaseFileFullPath;
        this.stateCityDetectionFile = stateCityDetectionFile;
        this.stateCityPreparationFile = stateCityPreparationFile;
        this.databaseManager = databaseManager;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
    }

    @Override
    public DATA_LOADER_TYPE getGeoDataLoaderType()
    {
        return DATA_LOADER_TYPE.STATE_CITY_DATA;
    }

    @Override
    public String getDataSourceName()
    {
        return dataSourceName;
    }

    @Override
    public void scheduleInputDatabaseConversionAndPopulationForInternalUsage() throws Exception
    {
        //load data once at the startup.
        prepareDatabase();

        this.timer = new Timer();
        this.timerTask = new MaxMindStateCityDataLoadingTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    private Connection fetchConnectionFromPool() throws SQLException
    {
        return this.databaseManager.getConnectionFromPool();
    }

    private Map<String,String> fetchStateNameAgainstCountryCodeStateCodeFromFile(String inputFile) throws Exception
    {
        BufferedReader br = null;
        Map<String,String> dataMap = new HashMap<String, String>();

        try
        {
            br = new BufferedReader(new FileReader(inputFile));

            String line = null;
            StringBuffer sb = null;

            while(null != (line = br.readLine()))
            {
                String lineParts[] = line.split(COMMA_DELIMITER);

                String countryCode = null;
                String stateCode = null;
                String stateName = null;

                if(!EMPTY_STRING.equals(lineParts[0]))
                {
                    countryCode = lineParts[0];
                }
                if(!EMPTY_STRING.equals(lineParts[1]))
                {
                    stateCode = lineParts[1];
                }
                if(!EMPTY_STRING.equals(lineParts[2]))
                {
                    stateName = lineParts[2];
                    String stateNameParts[] = stateName.split(QUOTE_DELIMITER);
                    if(stateNameParts.length > 0)
                        stateName = stateNameParts[1];
                    else
                        stateName = null;
                }

                if(null == countryCode || null == stateCode || null == stateName)
                {
                    logger.error("Country code or state code or state name is null inside " +
                                 "fetchCountryCodeStateCodeStateNameDataFromFile of MaxMindStateCityDataLoader " +
                                 line);
                    continue;
                }

                sb = new StringBuffer();
                sb.append(countryCode);
                sb.append(CONTROL_A);
                sb.append(stateCode);

                dataMap.put(sb.toString(),stateName);
            }
        }
        catch (Exception e)
        {
            throw new Exception("Exception inside fetchStateCodeNameDataFromFile of MaxMindStateCityDataLoader ",e);
        }
        finally
        {
            if(null != br)
                br.close();
        }

        return dataMap;
    }

    private Map<Integer,MaxmindCountryStateCityData>
                                    fetchMaxmindCountryStateCityDataFromFile(String inputFile) throws Exception
    {
        BufferedReader br = null;

        Map<Integer,MaxmindCountryStateCityData> dataMap = new HashMap<Integer, MaxmindCountryStateCityData>();
        Map<String,String> stateCodeNameData =
                            fetchStateNameAgainstCountryCodeStateCodeFromFile(this.stateCodeNameDatabaseFileFullPath);

        Connection connection = null;

        try
        {
            connection = fetchConnectionFromPool();
            Map<String,Country>
                    countryMap = GeoCommonUtils.
                                     fetchCountryDataFromSqlDatabaseAgainstCodeForDataSource(connection,
                                                                                             dataSourceName);

            br = new BufferedReader(new FileReader(inputFile));

            String line = null;
            StringBuffer keyForStateNameLookup = null;


            while(null != (line = br.readLine()))
            {
                String lineParts[] = line.split(COMMA_DELIMITER);

                if(lineParts.length < 4)
                {
                    throw new Exception("The line in file: " + inputFile + " is incorrect, line: " + line +
                                        " , requires atleast four fields, as locId,country,region,city...");
                }

                Integer blockId = Integer.parseInt(lineParts[0]);
                String countryCode = null;
                String stateCode = null;
                String cityName = null;

                if(!EMPTY_STRING.equals(lineParts[1]))
                {
                    String countryCodeParts[] = lineParts[1].split(QUOTE_DELIMITER);
                    if(null != countryCodeParts && countryCodeParts.length > 1)
                    {
                        countryCode = countryCodeParts[1];
                    }
                }
                if(!EMPTY_STRING.equals(lineParts[2]))
                {
                    String stateCodeParts[] = lineParts[2].split(QUOTE_DELIMITER);
                    if(null != stateCodeParts && stateCodeParts.length > 1)
                    {
                        stateCode = stateCodeParts[1];
                    }
                }
                if(!EMPTY_STRING.equals(lineParts[3]))
                {
                    String cityNameParts[] = lineParts[3].split(QUOTE_DELIMITER);

                    if(null != cityNameParts && cityNameParts.length > 1)
                    {
                        cityName = cityNameParts[1];
                    }
                }
                //if any of attributes is null we can ignore the entry.
                if(null == countryCode || null == stateCode || null == cityName)
                {
                    logger.error("CountryCode or stateCode or cityname is null " +
                                 "inside fetchMaxmindCountryStateCityDataFromFile of MaxMindStateCityDataLoader: " +
                                 line);
                    continue;
                }

                MaxmindCountryStateCityData maxmindCountryStateCityData =
                                                new MaxmindCountryStateCityData(countryCode,
                                                                                stateCode,
                                                                                cityName,
                                                                                dataSourceName);

                Country country = countryMap.get(countryCode);
                if(null == country)
                {
                    logger.error("Country not found for country code: " + countryCode +
                                 " inside fetchMaxmindCountryStateCityDataFromFile of MaxMindStateCityDataLoader");
                    continue;
                }

                int countryId = country.getCountryInternalId();
                keyForStateNameLookup = new StringBuffer();
                keyForStateNameLookup.append(countryCode);
                keyForStateNameLookup.append(CONTROL_A);
                keyForStateNameLookup.append(stateCode);
                String stateName = stateCodeNameData.get(keyForStateNameLookup.toString());

                if(null == stateName)
                {
                    logger.error("StateName could not be found for key prepared: {}",keyForStateNameLookup);
                    continue;
                }

                maxmindCountryStateCityData.setCountryId(countryId);
                maxmindCountryStateCityData.setStateName(stateName);
                dataMap.put(blockId,maxmindCountryStateCityData);
            }
        }
        catch (Exception e)
        {
            throw new Exception("Exception inside fetchMaxmindCountryStateCityDataFromFile ",e);
        }
        finally
        {
            DBExecutionUtils.closeConnection(connection);
        }

        return dataMap;
    }

    /**
     * This function looks for block id database file if changed then reload of database
     * happens.
     * If file found is modified then the state city database is reloaded at once, after
     * that given the batch size of sql block ids are read and processing is done.
     * @throws Exception
     */
    private void prepareDatabase() throws Exception
    {
        if(null == this.stateCityBlockIdDatabaseFileFullPath)
            throw new RefreshException("MaxmindStateCityBlockIdDatabaseFilePath provided is null, cannot proceed!");

        File inputDataFile = new File(this.stateCityBlockIdDatabaseFileFullPath);
        long fileModifyTime = inputDataFile.lastModified();

        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            BufferedReader br = null;

            try
            {
                Long startTime = new Date().getTime();

                //first read complete state city data into a map.
                Map<Integer,MaxmindCountryStateCityData> maxmindCountryStateCityDataMap =
                                    fetchMaxmindCountryStateCityDataFromFile(this.stateCityDatabaseFileFullPath);

                //now read block id file line by line.
                br = new BufferedReader(new FileReader(inputDataFile));
                String line = null;

                Connection connection = null;
                boolean autoCommitFlag = true;

                try
                {
                    int batchSizeCounter = 0;
                    int totalEntities = 0;
                    connection = fetchConnectionFromPool();
                    autoCommitFlag = connection.getAutoCommit();
                    connection.setAutoCommit(false);
                    StringBuffer stateSqlDataForInsertion =     new StringBuffer();
                    StringBuffer citySqlDataForInsertion  =     new StringBuffer();

                    /*This data set is for keeping all the valid state-city entries processed*/
                    Set<MaxmindCountryStateCityData> maxmindCountryStateCityDataSet =
                                                                new HashSet<MaxmindCountryStateCityData>();

                    /*Query already present state data from sql, key is concat(country_id,Control-A,stateName)*/
                    Map<String,Integer> stateIdFromSqlMap = GeoCommonUtils.
                            fetchStateIdDataFromSqlDatabaseForDataSource(connection, dataSourceName);
                    Map<String,Integer> cityIdFromSqlMap = GeoCommonUtils.
                            fetchCityIdDataFromSqlDatabaseForDataSource(connection, dataSourceName);

                    /*First read the complete data into a set to be used later for population*/
                    while(null != (line = br.readLine()))
                    {
                        //format: "16777216","16777471","17"
                        String lineParts[] = line.split(QUOTE_DELIMITER);
                        if(lineParts.length != 6)
                            throw new RefreshException("The line in file"  +
                                    this.stateCityBlockIdDatabaseFileFullPath +
                                    "is incorrect, not correct length : " + line);

                        BigInteger startIpValue = new BigInteger(lineParts[1]);
                        BigInteger endIpValue = new BigInteger(lineParts[3]);
                        int blockId = Integer.parseInt(lineParts[5]);

                        MaxmindCountryStateCityData maxmindCountryStateCityData =
                                maxmindCountryStateCityDataMap.get(blockId);

                        if(null == maxmindCountryStateCityData)
                        {
                            logger.error("No data found for state city for blockid: " + blockId);
                            continue;
                        }

                        maxmindCountryStateCityData.setStartIp(startIpValue);
                        maxmindCountryStateCityData.setEndIp(endIpValue);
                        maxmindCountryStateCityDataSet.add(maxmindCountryStateCityData);
                    }

                    logger.debug("State City database size is  : {} " , maxmindCountryStateCityDataSet.size());

                    for(MaxmindCountryStateCityData maxmindCountryStateCityData : maxmindCountryStateCityDataSet)
                    {
                        /** Now check if state data not already present
                         ** in sql append it for insertion using batch query
                         **/
                        if(!stateIdFromSqlMap.containsKey(maxmindCountryStateCityData.prepareKeyForStateDataMapStorage()))
                        {
                            stateSqlDataForInsertion.append(maxmindCountryStateCityData.prepareStateRowForInsertion());
                            stateIdFromSqlMap.put(maxmindCountryStateCityData.prepareKeyForStateDataMapStorage(),null);
                            batchSizeCounter ++;
                        }

                        totalEntities ++;

                        //in case batch size is encountered.
                        if(batchSizeCounter == batchSizeForSqlInsertion ||
                           totalEntities    == maxmindCountryStateCityDataSet.size())
                        {
                            if(stateSqlDataForInsertion.length() > 0)
                            {
                                stateSqlDataForInsertion.deleteCharAt(stateSqlDataForInsertion.length()-1);
                                //now insert into state table.
                                StringBuffer finalStateDataQuery = new StringBuffer(STATE_SQL_DATA_INSERT_QUERY);
                                finalStateDataQuery.append(stateSqlDataForInsertion.toString());
                                insertBatchDataIntoSql(finalStateDataQuery.toString(), connection);
                            }

                            stateSqlDataForInsertion = new StringBuffer();
                            batchSizeCounter = 0;
                        }
                    }
                    /*Done with state data insertion into database.*/

                    /*Now proceed with city data insertion using the same data set.*/
                    //reset counters for city data.
                    batchSizeCounter = 0;
                    totalEntities = 0;

                    /*Query state id database from sql again for usage in city database creation.*/
                    stateIdFromSqlMap = GeoCommonUtils.
                            fetchStateIdDataFromSqlDatabaseForDataSource(connection, dataSourceName);

                    for(MaxmindCountryStateCityData entry : maxmindCountryStateCityDataSet)
                    {
                        Integer stateId = stateIdFromSqlMap.get(entry.prepareKeyForStateDataMapStorage());

                        if(null == stateId)
                        {
                            throw new Exception("StateId could not be found from sql for countryid " +
                                                entry.getCountryId() + " and statename: " +
                                                entry.getStateName());
                        }

                        entry.setStateId(stateId);

                        if(!cityIdFromSqlMap.containsKey(entry.prepareKeyForCityDataMapStorage()))
                        {
                            citySqlDataForInsertion.append(entry.prepareCityRowForInsertion());
                            cityIdFromSqlMap.put(entry.prepareKeyForCityDataMapStorage(),null);
                            batchSizeCounter ++;
                        }

                        totalEntities ++;
                        if(batchSizeCounter == batchSizeForSqlInsertion ||
                           totalEntities    == maxmindCountryStateCityDataSet.size())
                        {
                            /*now insert city data into sql database.*/
                            if(citySqlDataForInsertion.length() > 0)
                            {
                                citySqlDataForInsertion.deleteCharAt(citySqlDataForInsertion.length()-1);
                                StringBuffer finalCityDataForInsertion = new StringBuffer(CITY_SQL_DATA_INSERT_QUERY);
                                finalCityDataForInsertion.append(citySqlDataForInsertion);
                                insertBatchDataIntoSql(finalCityDataForInsertion.toString(), connection);
                            }

                            citySqlDataForInsertion = new StringBuffer();
                            batchSizeCounter = 0;
                        }
                    }

                    //now write to output file contents required for detection
                    /*Prepare detection file content and write into detection output files.*/
                    String detectionDataOutput = accumulateDataForDetectionFileGeneration(
                                                                                          maxmindCountryStateCityDataSet,
                                                                                          connection
                                                                                         );

                    //write into preparation file.
                    BufferedWriter preparationWriter =
                            new BufferedWriter(new FileWriter(new File(this.stateCityPreparationFile)));

                    preparationWriter.write(detectionDataOutput);
                    preparationWriter.close();

                    //now move this file atomically to the detection file destination.
                    GeoCommonUtils.moveFileAtomicallyToDestination(
                                                                   this.stateCityPreparationFile,
                                                                   this.stateCityDetectionFile
                                                                  );

                    lastRefreshStartTime = startTime;
                }
                catch (Exception e)
                {
                    throw new RefreshException("The Maxmind state city blockid input data is corrupted ",e);
                }
                finally
                {
                    if(null != connection)
                    {
                        connection.commit();
                        connection.setAutoCommit(autoCommitFlag);
                        DBExecutionUtils.closeConnection(connection);
                    }
                }
            }
            catch (IOException ioe)
            {
                throw new RefreshException("IOException inside MaxMindStateCityDataLoader ",ioe);
            }
        }
    }

    private void insertBatchDataIntoSql(String dataToInsert,Connection connection) throws Exception
    {
        Statement stmt = null;
        int rowsInserted = 0;

        try
        {
            stmt = connection.createStatement();
            rowsInserted = stmt.executeUpdate(dataToInsert.toString());
            logger.error("NO ERROR !!! Rows inserted count in state-city data population : {} ", rowsInserted);
        }
        catch (SQLException sqle)
        {
            logger.error("SQLException in executing query: {} ",dataToInsert.toString());
            throw new Exception("Rows insertion of maxmind state/city data could not happen." +
                                "Aborting!!!",sqle);
        }
        finally
        {
            if(null != stmt)
                stmt.close();
        }

        if(rowsInserted <= 0)
            throw new Exception("Rows insertion of maxmind state/city " +
                                "data could not happen.Aborting!!!");
    }

    private String accumulateDataForDetectionFileGeneration
                                            (
                                             Set<MaxmindCountryStateCityData> maxmindCountryStateCityDataSet,
                                             Connection connection
                                            ) throws Exception
    {
        Integer cityId = null;
        Integer stateId = null;

        //fetch state and city data present in sql database.
        Map<String,Integer> stateIdFromSqlMap =
                GeoCommonUtils.fetchStateIdDataFromSqlDatabaseForDataSource(connection, dataSourceName);
        Map<String,Integer> cityIdDatabaseFromSql =
                GeoCommonUtils.fetchCityIdDataFromSqlDatabaseForDataSource(connection,dataSourceName);

        StringBuffer stateCityDetectionDataBuffer = new StringBuffer();

        //form detection file data and push into file.
        for(MaxmindCountryStateCityData dataElement : maxmindCountryStateCityDataSet)
        {
            stateId = stateIdFromSqlMap.get(dataElement.prepareKeyForStateDataMapStorage());
            cityId = cityIdDatabaseFromSql.get(dataElement.prepareKeyForCityDataMapStorage());

            if(null == stateId || null == cityId)
            {
                throw new Exception(" No state id or city id found in sql database " +
                                    " for statecode: " + dataElement.getStateCode()  +
                                    " for statename: " + dataElement.getStateName()  +
                                    " and cityname: " + dataElement.getCityName());
            }

            stateCityDetectionDataBuffer.append
                    (dataElement.prepareLineForStateCityDetectionFilePopulation(dataElement.getStartIp(),
                                                                                dataElement.getEndIp(),
                                                                                stateId,cityId)
                                                                               );
        }

        return stateCityDetectionDataBuffer.toString();
    }

    @Override
    public void releaseResources()
    {
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
            this.timer.purge();
        }
    }

    /**
     * This class is responsible for looking up for any updates in country data file.
     */
    private class MaxMindStateCityDataLoadingTask extends TimerTask
    {
        private Logger cacheLogger = LoggerFactory.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                prepareDatabase();
            }
            catch (Exception e)
            {
                cacheLogger.error("Exception while loading state/city data inside " +
                                  "MaxMindStateCityDataLoadingTask in the class MaxMindStateCityDataLoader",e);
            }
        }
    }
}