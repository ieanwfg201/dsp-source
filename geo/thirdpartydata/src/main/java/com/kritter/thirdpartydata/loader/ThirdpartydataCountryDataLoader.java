package com.kritter.thirdpartydata.loader;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.geo.common.utils.GeoDetectionUtils;
import com.kritter.thirdpartydata.entity.ThirdpartydataCountryInputData;
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
 * This class reads CSV input file containing thirdpartydata country database.
 * Uses the ip ranges to form the detection database as CSV file.
 * This class also needs to maintain data from sql for country to look
 * for if the data is already present in sql or not.
 */

public class ThirdpartydataCountryDataLoader implements ThirdPartyDataLoader
{

    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private String countryDatabaseFileFullPath;
    private String countryDetectionFile;
    private String countryPreparationFile;
    private DatabaseManager databaseManager;
    private int batchSizeForSqlInsertion;
    private Long lastRefreshStartTime = null;
    private static final String COUNTRY_INPUT_DATA_DELIMITER = String.valueOf((char)1);
    private static final String COUNTRY_SQL_DATA_QUERY_PREFIX = "insert into country " +
                                                                "(country_code,country_name,data_source_name," +
                                                                "modified_on) values ";
    private int LINES_TO_SKIP = 0;

    public ThirdpartydataCountryDataLoader(
                                            String loggerName,
                                            String dataSourceName,
                                            long reloadFrequency,
                                            String countryDatabaseFileFullPath,
                                            String countryDetectionFile,
                                            String countryPreparationFile,
                                            DatabaseManager databaseManager,
                                            int batchSizeForSqlInsertion,
                                            int LINES_TO_SKIP
                                         ) throws Exception
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.countryDatabaseFileFullPath = countryDatabaseFileFullPath;
        this.countryDetectionFile = countryDetectionFile;
        this.countryPreparationFile = countryPreparationFile;
        this.databaseManager = databaseManager;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
        this.LINES_TO_SKIP = LINES_TO_SKIP;
    }

    @Override
    public DATA_LOADER_TYPE getGeoDataLoaderType()
    {
        return DATA_LOADER_TYPE.COUNTRY_DATA;
    }

    @Override
    public String getDataSourceName()
    {
        return this.dataSourceName;
    }

    @Override
    public void scheduleInputDatabaseConversionAndPopulationForInternalUsage() throws Exception
    {
        //load data once at the startup
        prepareDatabase();

        this.timer = new Timer();
        this.timerTask = new CountryDataLoadingTask();
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
        readCSVFileForCountryDataAndPrepareSqlAndCSVDatabase();
    }

    private void readCSVFileForCountryDataAndPrepareSqlAndCSVDatabase() throws Exception
    {
        if(null == this.countryDatabaseFileFullPath)
            throw new RefreshException("ThirdpartydataCountryDataFilePath provided is null, cannot proceed!");

        File inputDataFile = new File(this.countryDatabaseFileFullPath);
        long fileModifyTime = inputDataFile.lastModified();

        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            Set<ThirdpartydataCountryInputData> thirdpartydataCountryInputDataSet = new HashSet<ThirdpartydataCountryInputData>();
            BufferedReader br = null;
            Connection connection = fetchConnectionToDatabase();

            Map<String,Country> countryDataFromSqlDatabase = GeoCommonUtils.
                                        fetchCountryDataFromSqlDatabaseForDataSource(
                                                                                     connection,
                                                                                     this.dataSourceName
                                                                                    );

            try
            {
                Long startTime = new Date().getTime();
                br = new BufferedReader(new FileReader(inputDataFile));
                String line = null;

                long t = System.currentTimeMillis();

                logger.debug("Loading data for thirdpartydata country, prepairing thirdpartydata country input data set.");

                try
                {
                    //skip first few lines to avoid read error.
                    for(int counter = 0;counter < LINES_TO_SKIP ; counter++)
                    {
                        br.readLine();
                    }

                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(COUNTRY_INPUT_DATA_DELIMITER);

                        if(lineParts.length != 4)
                            throw new RefreshException("The line in file is incorrect, not correct length : " + line);

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
                        String countryName = lineParts[3];

                        ThirdpartydataCountryInputData thirdpartydataCountryInputData = new
                                                                          ThirdpartydataCountryInputData(
                                                                                                   startIpValue,
                                                                                                   endIpValue,
                                                                                                   countryCode,
                                                                                                   countryName,
                                                                                                   dataSourceName);

                        if(thirdpartydataCountryInputDataSet.contains(thirdpartydataCountryInputData))
                            throw new RefreshException("The Thirdpartydata country input data is corrupted, " +
                                                       "entry is duplicate: "
                                                       + thirdpartydataCountryInputData.getStartIpValue() + ","
                                                       + thirdpartydataCountryInputData.getEndIpValue() );

                        thirdpartydataCountryInputDataSet.add(thirdpartydataCountryInputData);
                    }
                }
                catch (Exception e)
                {
                    throw new RefreshException("The Thirdpartydata country input data is corrupted ",e);
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
                                                       "refresh in ThirdpartydataCountryDataLoader: ", ioe);
                        }
                    }
                }

                logger.debug("Country data reading from input file is over, time taken : {}", (System.currentTimeMillis() - t));

                //use the data set to insert into CSV detection file and sql database in batches.

                int batchSizeCounter = 0;
                int totalEntities = 0;

                Iterator<ThirdpartydataCountryInputData> iterator = thirdpartydataCountryInputDataSet.iterator();

                //initialize csv and sql buffer along with what all country names are present in sql database.
                StringBuffer csvFileDataBuffer = new StringBuffer();
                StringBuffer sqlDataBuffer = new StringBuffer();

                while (iterator.hasNext())
                {
                    totalEntities ++;

                    ThirdpartydataCountryInputData thirdpartydataCountryInputData = iterator.next();

                    String countryName = thirdpartydataCountryInputData.getCountryName();
                    //if the country data already exists in sql database no need to insert.
                    if(!countryDataFromSqlDatabase.containsKey(countryName))
                    {
                        sqlDataBuffer.append(thirdpartydataCountryInputData.prepareCountryRowForInsertion());
                        countryDataFromSqlDatabase.put(countryName,null);
                        batchSizeCounter ++;
                    }

                    if(batchSizeCounter == batchSizeForSqlInsertion ||
                       totalEntities == thirdpartydataCountryInputDataSet.size())
                    {

                        int rowsInserted = 0;
                        if(sqlDataBuffer.length() > 0)
                        {
                            sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                            StringBuffer finalCountryDataInsertionQuery = new
                                                                            StringBuffer(COUNTRY_SQL_DATA_QUERY_PREFIX);

                            finalCountryDataInsertionQuery.append(sqlDataBuffer.toString());

                            Statement stmt = null;

                            try
                            {
                                stmt = connection.createStatement();
                                rowsInserted = stmt.executeUpdate(finalCountryDataInsertionQuery.toString());
                            }
                            catch (SQLException sqle)
                            {
                                throw new Exception("Rows insertion of thirdpartydata country data could not happen." +
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
                                throw new Exception("Rows insertion of thirdpartydata country " +
                                                    "data could not happen.Aborting!!!");

                            batchSizeCounter = 0;
                            sqlDataBuffer = new StringBuffer();
                        }
                    }
                }

                //run iterator again on the input data and prepare CSV data this time.
                countryDataFromSqlDatabase = GeoCommonUtils.
                                                     fetchCountryDataFromSqlDatabaseForDataSource(
                                                                                                  connection,
                                                                                                  this.dataSourceName
                                                                                                 );

                //push country ids to ui table for targeting exposure.
                GeoCommonUtils.feedCountryDataForTargetingExposureOnUserInterface(
                                                                                  countryDataFromSqlDatabase,
                                                                                  connection,
                                                                                  batchSizeForSqlInsertion
                                                                                 );

                iterator = thirdpartydataCountryInputDataSet.iterator();

                while(iterator.hasNext())
                {
                    ThirdpartydataCountryInputData thirdpartydataCountryInputData = iterator.next();
                    Country countryEntityFromSqlDatabase = countryDataFromSqlDatabase.get(
                                                                                          thirdpartydataCountryInputData.
                                                                                              getCountryName()
                                                                                         );

                    csvFileDataBuffer.append(
                                             thirdpartydataCountryInputData.
                                                     prepareLineForCountryDetectionFilePopulation
                                                             (
                                                              countryEntityFromSqlDatabase.getCountryInternalId()
                                                             )
                                            );
                }

                //now write csv data into preparation file.
                BufferedWriter preparationWriter = new BufferedWriter
                                                            (new FileWriter(new File(this.countryPreparationFile)));

                preparationWriter.write(csvFileDataBuffer.toString());
                preparationWriter.close();

                //now move this file atomically to the detection file destination.
                GeoCommonUtils.moveFileAtomicallyToDestination(this.countryPreparationFile,this.countryDetectionFile);

                lastRefreshStartTime = startTime;
            }
            catch (IOException ioe)
            {
                throw new RefreshException("IOException in ThirdpartydataCountryDataLoader, cause: ",ioe);
            }
            catch (Exception e)
            {
                throw new RefreshException("Exception in ThirdpartydataCountryDataLoader, cause: ",e);
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
     * This class is responsible for looking up for any updates in country data file.
     */
    private class CountryDataLoadingTask extends TimerTask
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
                cacheLogger.error("Exception while loading country data inside CountryDataLoadingTask in the class ThirdpartydataCountryDataLoader",e);
            }
        }
    }
}
