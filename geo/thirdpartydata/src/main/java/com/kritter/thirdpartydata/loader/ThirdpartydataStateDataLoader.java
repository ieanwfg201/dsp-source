package com.kritter.thirdpartydata.loader;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.CountryUserInterfaceId;
import com.kritter.geo.common.entity.State;
import com.kritter.geo.common.entity.reader.CountryUserInterfaceIdCache;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.geo.common.utils.GeoDetectionUtils;
import com.kritter.thirdpartydata.entity.ThirdpartydataCountryInputData;
import com.kritter.thirdpartydata.entity.ThirdpartydataStateInputData;
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
 * This class loads state data from an input file, prepares sql database and then prepares
 * input file for detection purposes.
 *
 * Input data format is:
 *
 * startip<CTRL-A>endip<CTRL-A>countrycode<CTRLA>statecode<CTRL-A>statename<CTRL-A>citycode<CTRL-A>cityname
 *
 * Output data format is:
 *
 * startip_numeric,endip_numeric,sql_id_for_state
 */
public class ThirdpartydataStateDataLoader implements ThirdPartyDataLoader
{
    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private String stateDatabaseFileFullPath;
    private String stateDetectionFile;
    private String statePreparationFile;
    private DatabaseManager databaseManager;
    private CountryUserInterfaceIdCache countryUserInterfaceIdCache;
    private int batchSizeForSqlInsertion;
    private Long lastRefreshStartTime = null;
    private static final String STATE_INPUT_DATA_DELIMITER = String.valueOf((char)1);
    private static final String STATE_SQL_DATA_QUERY_PREFIX = "insert into state " +
                                                              "(country_id,state_code,state_name,data_source_name," +
                                                              "modified_on) values ";
    private int LINES_TO_SKIP = 0;

    public ThirdpartydataStateDataLoader(
                                            String loggerName,
                                            String dataSourceName,
                                            long reloadFrequency,
                                            String stateDatabaseFileFullPath,
                                            String stateDetectionFile,
                                            String statePreparationFile,
                                            DatabaseManager databaseManager,
                                            CountryUserInterfaceIdCache countryUserInterfaceIdCache,
                                            int batchSizeForSqlInsertion,
                                            int LINES_TO_SKIP
                                        ) throws Exception
    {
        this.logger = LogManager.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.stateDatabaseFileFullPath = stateDatabaseFileFullPath;
        this.stateDetectionFile = stateDetectionFile;
        this.statePreparationFile = statePreparationFile;
        this.databaseManager = databaseManager;
        this.countryUserInterfaceIdCache = countryUserInterfaceIdCache;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
        this.LINES_TO_SKIP = LINES_TO_SKIP;
    }

    @Override
    public DATA_LOADER_TYPE getGeoDataLoaderType()
    {
        return DATA_LOADER_TYPE.STATE_DATA;
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
        this.timerTask = new StateDataLoadingTask();
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
        readCSVFileForStateDataAndPrepareSqlAndCSVDatabase();
    }

    private void readCSVFileForStateDataAndPrepareSqlAndCSVDatabase() throws Exception
    {
        /*call refresh once so that we have latest data, as country data loaders would have finished by now*/
        this.countryUserInterfaceIdCache.refresh();

        Collection<CountryUserInterfaceId> countryUserInterfaceIds = this.countryUserInterfaceIdCache.getAllEntities();

        logger.debug("Size of country user interface id entities is: {} ",countryUserInterfaceIds.size());
        logger.debug("Data from country user interface id cache is : {} ", countryUserInterfaceIds);

        if(null == this.stateDatabaseFileFullPath)
            throw new RefreshException("ThirdpartydataStateDataFilePath provided is null, cannot proceed!");

        File inputDataFile = new File(this.stateDatabaseFileFullPath);
        long fileModifyTime = inputDataFile.lastModified();

        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            Set<ThirdpartydataStateInputData> thirdpartydataStateInputDataSet = new HashSet<ThirdpartydataStateInputData>();
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

            try
            {
                Long startTime = new Date().getTime();
                br = new BufferedReader(new FileReader(inputDataFile));
                String line = null;

                long t = System.currentTimeMillis();

                logger.debug("Loading data for thirdpartydata state, preparing thirdpartydata state input data set.");

                try
                {
                    //skip first few lines to avoid read error.
                    for(int counter = 0;counter < LINES_TO_SKIP ; counter++)
                    {
                        br.readLine();
                    }

                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(STATE_INPUT_DATA_DELIMITER);

                        /* startip<CTRL-A>endip<CTRL-A>countrycode<CTRLA>statecode<CTRL-A>statename<CTRL-A>citycode<CTRL-A>cityname */

                        /*The data row must atleast contain values upto state name, so that its a valid entry
                        * for usage, if city is missing then it could be the valid case.*/
                        if(lineParts.length < 5)
                            throw new RefreshException("The line in file is incorrect,should atleast be 5 values " +
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

                        /*Fetch corresponding country for this data row.*/
                        Country country = countryDataAgainstCountryCodeFromSqlDatabase.get(countryCode);
                        if(null == country)
                        {
                            logger.error("The data line/row could not be processed" + line +", as country could not " +
                                         "be found for country code: " + countryCode);
                            continue;
                        }

                        ThirdpartydataStateInputData thirdpartydataStateInputData = new
                                                                                        ThirdpartydataStateInputData
                                                                                        (
                                                                                         startIpValue,
                                                                                         endIpValue,
                                                                                         country.getCountryInternalId(),
                                                                                         stateCode,
                                                                                         stateName,
                                                                                         dataSourceName
                                                                                        );

                        if(thirdpartydataStateInputDataSet.contains(thirdpartydataStateInputData))
                            throw new RefreshException("The Thirdpartydata state input data is corrupted, " +
                                                       "entry is duplicate: " +
                                                       thirdpartydataStateInputData.getStartIpValue() + "," +
                                                       thirdpartydataStateInputData.getEndIpValue() );

                        thirdpartydataStateInputDataSet.add(thirdpartydataStateInputData);
                    }
                }
                catch (Exception e)
                {
                    throw new RefreshException("The Thirdpartydata state input data is corrupted ",e);
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
                                                       "refresh in ThirdpartydataStateDataLoader: ", ioe);
                        }
                    }
                }

                logger.debug("State data reading from input file is over, time taken : {}",
                              (System.currentTimeMillis() - t));

                /*use the data set to insert into CSV detection file and sql database in batches.*/
                int batchSizeCounter = 0;
                int totalEntities = 0;

                Iterator<ThirdpartydataStateInputData> iterator = thirdpartydataStateInputDataSet.iterator();

                /*Initialize csv and sql buffer, also fetch all state names that are present in sql database.*/
                StringBuffer csvFileDataBuffer = new StringBuffer();
                StringBuffer sqlDataBuffer = new StringBuffer();

                while (iterator.hasNext())
                {
                    totalEntities ++;

                    ThirdpartydataStateInputData thirdpartydataStateInputData = iterator.next();

                    String stateName = thirdpartydataStateInputData.getStateName();
                    //if the state data already exists in sql database no need to insert.
                    String key = State.generateKeyForUniquenessCheck
                                    (
                                     thirdpartydataStateInputData.getCountryId(),
                                     thirdpartydataStateInputData.getStateName()
                                    );
                    if(!stateDataFromSqlDatabase.containsKey(key))
                    {
                        sqlDataBuffer.append(thirdpartydataStateInputData.prepareStateRowForInsertion());
                        stateDataFromSqlDatabase.put(key,null);
                        batchSizeCounter ++;
                    }

                    if(batchSizeCounter == batchSizeForSqlInsertion ||
                       totalEntities == thirdpartydataStateInputDataSet.size())
                    {

                        int rowsInserted = 0;
                        if(sqlDataBuffer.length() > 0)
                        {
                            sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                            StringBuffer finalStateDataInsertionQuery = new
                                    StringBuffer(STATE_SQL_DATA_QUERY_PREFIX);

                            finalStateDataInsertionQuery.append(sqlDataBuffer.toString());

                            Statement stmt = null;

                            try
                            {
                                stmt = connection.createStatement();
                                rowsInserted = stmt.executeUpdate(finalStateDataInsertionQuery.toString());
                            }
                            catch (SQLException sqle)
                            {
                                throw new Exception("Rows insertion of thirdpartydata state data could not happen." +
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
                                throw new Exception("Rows insertion of thirdpartydata state data could not happen." +
                                                    "Aborting!!!");

                            batchSizeCounter = 0;
                            sqlDataBuffer = new StringBuffer();
                        }
                    }
                }

                /*run iterator again on the input data and prepare CSV data this time.*/
                stateDataFromSqlDatabase = GeoCommonUtils.
                                                fetchStateDataFromSqlDatabaseForDataSource
                                                        (
                                                         connection,
                                                         this.dataSourceName
                                                        );

                /*push state ids to ui table for targeting exposure.*/
                GeoCommonUtils.feedStateDataForTargetingExposureOnUserInterface(
                                                                                stateDataFromSqlDatabase,
                                                                                connection,
                                                                                countryUserInterfaceIdCache,
                                                                                batchSizeForSqlInsertion
                                                                               );

                iterator = thirdpartydataStateInputDataSet.iterator();

                while(iterator.hasNext())
                {
                    ThirdpartydataStateInputData thirdpartydataStateInputData = iterator.next();
                    String key = State.generateKeyForUniquenessCheck
                            (thirdpartydataStateInputData.getCountryId(),thirdpartydataStateInputData.getStateName());

                    State stateEntityFromSqlDatabase = stateDataFromSqlDatabase.get(key);

                    csvFileDataBuffer.append(
                                             thirdpartydataStateInputData.
                                                    prepareLineForStateDetectionFilePopulation
                                                                (stateEntityFromSqlDatabase.getStateId())
                                            );
                }

                //now write csv data into preparation file.
                BufferedWriter preparationWriter = new BufferedWriter
                                                            (new FileWriter(new File(this.statePreparationFile)));

                preparationWriter.write(csvFileDataBuffer.toString());
                preparationWriter.close();

                //now move this file atomically to the detection file destination.
                GeoCommonUtils.moveFileAtomicallyToDestination(this.statePreparationFile,this.stateDetectionFile);

                lastRefreshStartTime = startTime;
            }
            catch (IOException ioe)
            {
                throw new RefreshException("IOException in ThirdpartydataStateDataLoader, cause: ",ioe);
            }
            catch (Exception e)
            {
                throw new RefreshException("Exception in ThirdpartydataStateDataLoader, cause: ",e);
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
     * This class is responsible for looking up for any updates in state data file.
     */
    private class StateDataLoadingTask extends TimerTask
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
                cacheLogger.error("Exception while loading state data inside StateDataLoadingTask in the class " +
                                  "ThirdpartydataStateDataLoader",e);
            }
        }
    }
}