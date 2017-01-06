package com.kritter.thirdpartydata.loader;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.City;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.State;
import com.kritter.geo.common.entity.StateUserInterfaceId;
import com.kritter.geo.common.entity.reader.StateUserInterfaceIdCache;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.geo.common.utils.GeoDetectionUtils;
import com.kritter.thirdpartydata.entity.ThirdpartydataCityInputData;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * This class loads city data from an input file, prepares sql database and then prepares
 * input file for detection purposes.
 *
 * Input data format is:
 *
 * startip<CTRL-A>endip<CTRL-A>countrycode<CTRLA>statecode<CTRL-A>statename<CTRL-A>citycode<CTRL-A>cityname
 *
 * Output data format is:
 *
 * startip_numeric,endip_numeric,sql_id_for_city
 */
public class ThirdpartydataCityDataLoader implements ThirdPartyDataLoader
{
    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private String cityDatabaseFileFullPath;
    private String cityDetectionFile;
    private String cityPreparationFile;
    private DatabaseManager databaseManager;
    private StateUserInterfaceIdCache stateUserInterfaceIdCache;
    private int batchSizeForSqlInsertion;
    private Long lastRefreshStartTime = null;
    private static final String CITY_INPUT_DATA_DELIMITER = String.valueOf((char)1);
    private static final String CITY_SQL_DATA_QUERY_PREFIX = "insert into city (state_id,city_code,city_name," +
                                                              "data_source_name,modified_on) values ";
    private int LINES_TO_SKIP = 0;

    public ThirdpartydataCityDataLoader(
                                        String loggerName,
                                        String dataSourceName,
                                        long reloadFrequency,
                                        String cityDatabaseFileFullPath,
                                        String cityDetectionFile,
                                        String cityPreparationFile,
                                        DatabaseManager databaseManager,
                                        StateUserInterfaceIdCache stateUserInterfaceIdCache,
                                        int batchSizeForSqlInsertion,
                                        int LINES_TO_SKIP
                                       ) throws Exception
    {
        this.logger = LogManager.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.cityDatabaseFileFullPath = cityDatabaseFileFullPath;
        this.cityDetectionFile = cityDetectionFile;
        this.cityPreparationFile = cityPreparationFile;
        this.databaseManager = databaseManager;
        this.stateUserInterfaceIdCache = stateUserInterfaceIdCache;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
        this.LINES_TO_SKIP = LINES_TO_SKIP;
    }

    @Override
    public DATA_LOADER_TYPE getGeoDataLoaderType()
    {
        return DATA_LOADER_TYPE.CITY_DATA;
    }

    @Override
    public String getDataSourceName()
    {
        return this.dataSourceName;
    }

    @Override
    public void scheduleInputDatabaseConversionAndPopulationForInternalUsage() throws Exception
    {
        /*Load data once at the startup*/
        prepareDatabase();

        this.timer = new Timer();
        this.timerTask = new CityDataLoadingTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
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

    private void prepareDatabase() throws Exception
    {
        readCSVFileForCityDataAndPrepareSqlAndCSVDatabase();
    }

    private void readCSVFileForCityDataAndPrepareSqlAndCSVDatabase() throws Exception
    {
        /*Load state ui data once, as all state loaders would have finished by now.*/
        this.stateUserInterfaceIdCache.refresh();
        Collection<StateUserInterfaceId> stateUserInterfaceIds = this.stateUserInterfaceIdCache.getAllEntities();
        logger.debug("Size of state user interface id entities is: {} ", stateUserInterfaceIds.size());
        logger.debug("Data from state user interface id cache is : {} ", stateUserInterfaceIds);

        if(null == this.cityDatabaseFileFullPath)
            throw new RefreshException("ThirdpartydataCityDataFilePath provided is null, cannot proceed!");

        File inputDataFile = new File(this.cityDatabaseFileFullPath);
        long fileModifyTime = inputDataFile.lastModified();

        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            Set<ThirdpartydataCityInputData> thirdpartydataCityInputDataSet = new HashSet<ThirdpartydataCityInputData>();
            BufferedReader br = null;
            Connection connection = fetchConnectionToDatabase();

            Map<String,Country> countryDataAgainstCountryCodeFromSqlDatabase =
                    GeoCommonUtils.fetchCountryDataFromSqlDatabaseAgainstCodeForDataSource
                            (
                                    connection,
                                    this.dataSourceName
                            );

            Map<String,State> stateDataFromSqlDatabase =
                            GeoCommonUtils.fetchStateDataFromSqlDatabaseForDataSource(connection,this.dataSourceName);

            Map<String,City> cityDataFromSqlDatabase =
                            GeoCommonUtils.fetchCityDataFromSqlDatabaseForDataSource(connection,this.dataSourceName);

            try
            {
                Long startTime = new Date().getTime();
                br = new BufferedReader(new FileReader(inputDataFile));
                String line = null;

                long t = System.currentTimeMillis();

                logger.debug("Loading data for thirdpartydata city, preparing thirdpartydata city input data set.");

                try
                {
                    //skip first few lines to avoid read error.
                    for(int counter = 0;counter < LINES_TO_SKIP ; counter++)
                    {
                        br.readLine();
                    }

                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(CITY_INPUT_DATA_DELIMITER);

                        /* startip<CTRL-A>endip<CTRL-A>countrycode<CTRLA>statecode<CTRL-A>statename<CTRL-A>citycode<CTRL-A>cityname */

                        /*The data row must atleast contain values upto state name, so that its a valid entry
                        * for usage, if city is missing then it could be the valid case.*/
                        if(lineParts.length != 7)
                            throw new RefreshException("The line in file is incorrect,should be 7 values " +
                                                       "separated by control A , not correct length : " + line);

                        BigInteger startIpValue = null;
                        if(lineParts[0].contains(":"))
                            startIpValue = GeoDetectionUtils.fetchBigIntValueForIPV6(lineParts[0]);
                        else
                            startIpValue = BigInteger.valueOf(GeoDetectionUtils.fetchLongValueForIPV4(lineParts[0]));

                        BigInteger endIpValue = null;
                        if(lineParts[1].contains(":"))
                            endIpValue = GeoDetectionUtils.fetchBigIntValueForIPV6(lineParts[1]);
                        else
                            endIpValue = BigInteger.valueOf(GeoDetectionUtils.fetchLongValueForIPV4(lineParts[1]));

                        String countryCode = lineParts[2];
                        String stateCode = lineParts[3];
                        String stateName = lineParts[4];
                        String cityCode = lineParts[5];
                        String cityName = lineParts[6];

                        Country country = countryDataAgainstCountryCodeFromSqlDatabase.get(countryCode);
                        if(null == country)
                        {
                            logger.error("The data line/row could not be processed" + line +", as country could not " +
                                         "be found for country code: inside ThirdpartydataCityDataLoader" +
                                          countryCode);
                            continue;
                        }

                        String key = State.generateKeyForUniquenessCheck(country.getCountryInternalId(),stateName);
                        State state = stateDataFromSqlDatabase.get(key);

                        if(null == state)
                        {
                            logger.error(" The data line/row could not be processed" + line +", as state could not " +
                                         " be found for key (country,state_name):" + key +
                                         " inside ThirdpartydataCityDataLoader");
                            continue;
                        }

                        ThirdpartydataCityInputData thirdpartydataCityInputData = new   ThirdpartydataCityInputData
                                                                                        (
                                                                                                startIpValue,
                                                                                                endIpValue,
                                                                                                state.getStateId(),
                                                                                                cityCode,
                                                                                                cityName,
                                                                                                dataSourceName
                                                                                        );

                        if(thirdpartydataCityInputDataSet.contains(thirdpartydataCityInputData))
                            throw new RefreshException("The Thirdpartydata city input data is corrupted, " +
                                                       "entry is duplicate: " +
                                                        thirdpartydataCityInputData.getStartIpValue() + "," +
                                                        thirdpartydataCityInputData.getEndIpValue() );

                        thirdpartydataCityInputDataSet.add(thirdpartydataCityInputData);
                    }
                }
                catch (Exception e)
                {
                    throw new RefreshException("The Thirdpartydata city input data is corrupted ",e);
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
                                    "refresh in ThirdpartydataCityDataLoader: ", ioe);
                        }
                    }
                }

                logger.debug("City data reading from input file is over, time taken : {}",
                        (System.currentTimeMillis() - t));

                /*use the data set to insert into CSV detection file and sql database in batches.*/
                int batchSizeCounter = 0;
                int totalEntities = 0;

                Iterator<ThirdpartydataCityInputData> iterator = thirdpartydataCityInputDataSet.iterator();

                /*Initialize csv and sql buffer, also fetch all state names that are present in sql database.*/
                StringBuffer csvFileDataBuffer = new StringBuffer();
                StringBuffer sqlDataBuffer = new StringBuffer();

                while (iterator.hasNext())
                {
                    totalEntities ++;

                    ThirdpartydataCityInputData thirdpartydataCityInputData = iterator.next();

                    String cityName = thirdpartydataCityInputData.getCityName();

                    //if the city data already exists in sql database no need to insert.
                    String key = City.generateKeyForUniquenessCheck
                                                                    (
                                                                            thirdpartydataCityInputData.getStateId(),
                                                                            thirdpartydataCityInputData.getCityName()
                                                                    );

                    if(!cityDataFromSqlDatabase.containsKey(key))
                    {
                        sqlDataBuffer.append(thirdpartydataCityInputData.prepareCityRowForInsertion());
                        cityDataFromSqlDatabase.put(key,null);
                        batchSizeCounter ++;
                    }

                    if(batchSizeCounter == batchSizeForSqlInsertion ||
                       totalEntities == thirdpartydataCityInputDataSet.size())
                    {
                        int rowsInserted = 0;
                        if(sqlDataBuffer.length() > 0)
                        {
                            sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                            StringBuffer finalCityDataInsertionQuery = new StringBuffer(CITY_SQL_DATA_QUERY_PREFIX);

                            finalCityDataInsertionQuery.append(sqlDataBuffer.toString());

                            Statement stmt = null;

                            try
                            {
                                stmt = connection.createStatement();
                                rowsInserted = stmt.executeUpdate(finalCityDataInsertionQuery.toString());
                            }
                            catch (SQLException sqle)
                            {
                                throw new Exception("Rows insertion of thirdpartydata city data could not happen." +
                                                    "Aborting!!!",sqle);
                            }
                            finally
                            {
                                if(null != stmt)
                                    stmt.close();
                            }

                            //if rows updated are positive and no exceptions we can fetch back this data
                            //from sql and prepare csv data buffer in another go of iterator.
                            if(rowsInserted <= 0)
                                throw new Exception("Rows insertion of thirdpartydata city data could not happen." +
                                                    "Aborting!!!");

                            batchSizeCounter = 0;
                            sqlDataBuffer = new StringBuffer();
                        }
                    }
                }

                /*run iterator again on the input data and prepare CSV data this time.*/
                cityDataFromSqlDatabase = GeoCommonUtils.fetchCityDataFromSqlDatabaseForDataSource
                                                                                            (
                                                                                             connection,
                                                                                             this.dataSourceName
                                                                                            );

                /*push city ids to ui table for targeting exposure.*/
                GeoCommonUtils.feedCityDataForTargetingExposureOnUserInterface(
                                                                                cityDataFromSqlDatabase,
                                                                                connection,
                                                                                stateUserInterfaceIdCache,
                                                                                batchSizeForSqlInsertion
                                                                              );

                iterator = thirdpartydataCityInputDataSet.iterator();

                while(iterator.hasNext())
                {
                    ThirdpartydataCityInputData thirdpartydataCityInputData = iterator.next();
                    String key = City.generateKeyForUniquenessCheck
                                (thirdpartydataCityInputData.getStateId(),thirdpartydataCityInputData.getCityName());

                    City cityEntityFromSqlDatabase = cityDataFromSqlDatabase.get(key);

                    csvFileDataBuffer.append(
                                             thirdpartydataCityInputData.
                                                    prepareLineForCityDetectionFilePopulation
                                                            (cityEntityFromSqlDatabase.getCityId())
                                            );
                }

                //now write csv data into preparation file.
                BufferedWriter preparationWriter = new BufferedWriter
                        (new FileWriter(new File(this.cityPreparationFile)));

                preparationWriter.write(csvFileDataBuffer.toString());
                preparationWriter.close();

                //now move this file atomically to the detection file destination.
                GeoCommonUtils.moveFileAtomicallyToDestination(this.cityPreparationFile,this.cityDetectionFile);

                lastRefreshStartTime = startTime;
            }
            catch (IOException ioe)
            {
                throw new RefreshException("IOException in ThirdpartydataCityDataLoader, cause: ",ioe);
            }
            catch (Exception e)
            {
                throw new RefreshException("Exception in ThirdpartydataCityDataLoader, cause: ",e);
            }
            finally
            {
                DBExecutionUtils.closeConnection(connection);
            }
        }
    }

    private Connection fetchConnectionToDatabase() throws Exception
    {
        return this.databaseManager.getConnectionFromPool();
    }

    /**
     * This class is responsible for looking up for any updates in city data file.
     */
    private class CityDataLoadingTask extends TimerTask
    {
        private Logger cacheLogger = LogManager.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                prepareDatabase();
            }
            catch (Exception e)
            {
                cacheLogger.error("Exception while loading state data inside CityDataLoadingTask in the class " +
                                  "ThirdpartydataCityDataLoader",e);
            }
        }
    }
}