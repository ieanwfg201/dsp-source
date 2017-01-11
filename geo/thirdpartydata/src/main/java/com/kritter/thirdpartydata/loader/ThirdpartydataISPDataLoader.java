package com.kritter.thirdpartydata.loader;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.thirdpartydata.entity.ThirdpartydataISPInputData;
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
 * This class reads CSV input file containing thirdpartydata isp database.
 * Uses the ip ranges to form the detection database as CSV file.
 * This class also needs to maintain data from sql for isp to look
 * for if the data is already present in sql or not.
 *
 * File used is GEO-124.
 * format: iprange | ispname.
 *
 * ispname is the actual entity name that operates the iprange,
 * so not much of use when it comes to mapping it to brands.
 */

public class ThirdpartydataISPDataLoader implements ThirdPartyDataLoader
{
    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private String ispDatabaseFileFullPath;
    private String ispDetectionFile;
    private String ispPreparationFile;

    //for country detector
    private Map<String,String> thirdpartydataDataSourceWithCountryDetectionFile;
    private String[] dataSourceValuesOnlyForThirdpartydata;
    private long largeReloadFrequencyForCountryDetectionCache;

    private DatabaseManager databaseManager;

    private int batchSizeForSqlInsertion;
    private Long lastRefreshStartTime = null;
    private static final String ISP_INPUT_DATA_DELIMITER = String.valueOf((char)1);
    private static final String ISP_SQL_DATA_QUERY_PREFIX = "insert into isp " +
                                                            "(country_id,isp_name,data_source_name," +
                                                            "modified_on) values ";

    /**
     * Thirdpartydata ISP data loader constructor, it uses country detection class,
     * the reload frequency for country detection cache has to be very high.
     * So that even before any reload the isp data loading is finished.
     * As country detection data is anyways loaded inside detector constructor.
     * @param loggerName
     * @param dataSourceName
     * @param reloadFrequency
     * @param ispDatabaseFileFullPath
     * @param ispDetectionFile
     * @param ispPreparationFile
     * @param thirdpartydataDataSourceWithCountryDetectionFile
     * @param dataSourceValuesOnlyForThirdpartydata
     * @param largeReloadFrequencyForCountryDetectionCache
     * @param batchSizeForSqlInsertion
     * @throws Exception
     */
    public ThirdpartydataISPDataLoader(
                                String loggerName,
                                String dataSourceName,
                                long reloadFrequency,
                                String ispDatabaseFileFullPath,
                                String ispDetectionFile,
                                String ispPreparationFile,
                                Map<String,String> thirdpartydataDataSourceWithCountryDetectionFile,
                                String[] dataSourceValuesOnlyForThirdpartydata,
                                long largeReloadFrequencyForCountryDetectionCache,
                                DatabaseManager databaseManager,
                                int batchSizeForSqlInsertion
                               ) throws Exception
    {
        this.logger = LogManager.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.ispDatabaseFileFullPath = ispDatabaseFileFullPath;
        this.ispDetectionFile = ispDetectionFile;
        this.ispPreparationFile = ispPreparationFile;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;

        this.thirdpartydataDataSourceWithCountryDetectionFile = thirdpartydataDataSourceWithCountryDetectionFile;
        this.dataSourceValuesOnlyForThirdpartydata = dataSourceValuesOnlyForThirdpartydata;
        this.largeReloadFrequencyForCountryDetectionCache = largeReloadFrequencyForCountryDetectionCache;

        this.databaseManager = databaseManager;
    }

    @Override
    public DATA_LOADER_TYPE getGeoDataLoaderType()
    {
        return DATA_LOADER_TYPE.ISP_DATA;
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
        this.timerTask = new ISPDataLoadingTask();
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
        readCSVFileForISPDataAndPrepareSqlAndCSVDatabase();
    }


    private void readCSVFileForISPDataAndPrepareSqlAndCSVDatabase() throws Exception
    {
        if(null == this.ispDatabaseFileFullPath)
            throw new RefreshException("ThirdpartydataISPDataFilePath provided is null, cannot proceed!");

        File inputDataFile = new File(this.ispDatabaseFileFullPath);
        long fileModifyTime = inputDataFile.lastModified();

        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            //prepare country detection class first to lookup the country ids for the ip-ranges of ISPs.
            CountryDetectionCache countryDetectionCache =
                    new CountryDetectionCache(this.logger.getName(),
                                              thirdpartydataDataSourceWithCountryDetectionFile,
                                              dataSourceValuesOnlyForThirdpartydata,
                                              largeReloadFrequencyForCountryDetectionCache,
                                              false);

            Set<ThirdpartydataISPInputData> thirdpartydataISPInputDataSet = new HashSet<ThirdpartydataISPInputData>();
            BufferedReader br = null;
            Connection connection = fetchConnectionToDatabase();

            Map<String,InternetServiceProvider> ispDataFromSql = GeoCommonUtils.
                                                fetchISPDataFromSqlDatabaseForDataSource(connection,dataSourceName);

            try
            {
                Long startTime = new Date().getTime();
                br = new BufferedReader(new FileReader(inputDataFile));
                String line = null;
                try
                {
                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(ISP_INPUT_DATA_DELIMITER);

                        if(lineParts.length != 3)
                            throw new RefreshException("The line in isp file is of incorrect length " + line);

                        BigInteger startIpValue = new BigInteger(lineParts[0]);
                        BigInteger endIpValue = new BigInteger(lineParts[1]);

                        //IMPORTANT: just in case if replacement character is present.
                        //remove it from string.
                        String ispNameInISOCharacterSet = new String(lineParts[2].getBytes(),"ISO-8859-1");
                        String ispName = new String(ispNameInISOCharacterSet.getBytes(),"UTF-8");

                        //search corresponding country, if not found throw exception.
                        //or for a safe way we may choose to log error and process
                        //rest of the available entries, however this involves risk
                        //of no country being associated with majority of isps and
                        //we loosing out on data.

                        Country country = null;

                        try
                        {
                            //take any start or end ip value to detect
                            //for now log the error if country not found for isp entry as
                            //few entries do not have corresponding countries.
                            country = countryDetectionCache.findCountryForIpAddress(startIpValue);
                            if(null == country)
                            {
                                logger.error("Country is null for operator ,start ip: {}" , startIpValue);
                                continue;
                            }
                        }
                        catch (Exception e)
                        {
                            throw new Exception("Exception inside ThirdpartydataISPDataLoader in fetching country, cannot proceed." , e);
                        }

                        ThirdpartydataISPInputData thirdpartydataISPInputData = new
                                ThirdpartydataISPInputData(
                                                    startIpValue,
                                                    endIpValue,
                                                    country.getCountryInternalId(),
                                                    ispName,
                                                    this.dataSourceName
                                                   );

                        if(thirdpartydataISPInputDataSet.contains(thirdpartydataISPInputData))
                            throw new RefreshException("The Thirdpartydata ISP input data is corrupted, entry is duplicate: "
                                    + thirdpartydataISPInputData.getStartIpValue() + ","
                                    + thirdpartydataISPInputData.getEndIpValue() );

                        thirdpartydataISPInputDataSet.add(thirdpartydataISPInputData);
                    }
                }
                catch (Exception e)
                {
                    throw new RefreshException("The Thirdpartydata ISP input data is corrupted ",e);
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
                            throw new RefreshException("IOException thrown while closing file handle during refresh in ThirdpartydataISPDataLoader: ", ioe);
                        }
                    }
                }
                //use the data set to insert into CSV detection file and sql database in batches.

                int batchSizeCounter = 0;
                int totalEntities = 0;

                Iterator<ThirdpartydataISPInputData> iterator = thirdpartydataISPInputDataSet.iterator();

                //initialize csv and sql buffer.
                StringBuffer csvFileDataBuffer = new StringBuffer();
                StringBuffer sqlDataBuffer = new StringBuffer();

                while (iterator.hasNext())
                {
                    totalEntities ++;

                    ThirdpartydataISPInputData thirdpartydataISPInputData = iterator.next();

                    //if the isp data already exists in sql database no need to insert.
                    //check for a given country if the isp name exists, case sensitive.
                    if(!ispDataFromSql.containsKey(
                                                   InternetServiceProvider.
                                                            prepareCountryIdIspNameUniqueKey(thirdpartydataISPInputData.getCountryId(),thirdpartydataISPInputData.getIspName()))
                                                  )
                    {
                        sqlDataBuffer.append(thirdpartydataISPInputData.prepareIspRowForInsertion());
                        ispDataFromSql.put(InternetServiceProvider.
                                prepareCountryIdIspNameUniqueKey(thirdpartydataISPInputData.getCountryId(),thirdpartydataISPInputData.getIspName()),null);
                        batchSizeCounter ++;
                    }

                    if(batchSizeCounter == batchSizeForSqlInsertion ||
                            totalEntities == thirdpartydataISPInputDataSet.size())
                    {

                        int rowsInserted = 0;
                        if(sqlDataBuffer.length() > 0)
                        {
                            sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                            StringBuffer finalISPDataInsertionQuery = new
                                    StringBuffer(ISP_SQL_DATA_QUERY_PREFIX);

                            finalISPDataInsertionQuery.append(sqlDataBuffer.toString());

                            Statement stmt = null;

                            try
                            {
                                stmt = connection.createStatement();
                                rowsInserted = stmt.executeUpdate(finalISPDataInsertionQuery.toString());
                            }
                            catch (SQLException sqle)
                            {
                                throw new Exception("Rows insertion of thirdpartydata isp data could not happen." +
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
                                throw new Exception("Rows insertion of thirdpartydata isp " +
                                        "data could not happen.Aborting!!!");

                            batchSizeCounter = 0;
                            sqlDataBuffer = new StringBuffer();
                        }
                    }
                }

                //run iterator again on the input data and prepare CSV data this time.
                ispDataFromSql = GeoCommonUtils.
                                                fetchISPDataFromSqlDatabaseForDataSource(
                                                        connection,
                                                        this.dataSourceName
                                                );

                //push isp ids to ui table for targeting exposure.
                GeoCommonUtils.feedISPDataForTargetingExposureOnUserInterface
                                                        (
                                                         logger,
                                                         new HashSet<InternetServiceProvider>(ispDataFromSql.values()),
                                                         connection,
                                                         this.dataSourceName,
                                                         batchSizeForSqlInsertion
                                                        );

                iterator = thirdpartydataISPInputDataSet.iterator();

                while(iterator.hasNext())
                {
                    ThirdpartydataISPInputData thirdpartydataISPInputData = iterator.next();
                    InternetServiceProvider internetServiceProviderFromSql = ispDataFromSql.get
                                                                                     (
                                                                                      InternetServiceProvider.
                                                                                                    prepareCountryIdIspNameUniqueKey(thirdpartydataISPInputData.getCountryId(),thirdpartydataISPInputData.getIspName())
                                                                                     );

                    csvFileDataBuffer.append(
                            thirdpartydataISPInputData.
                                    prepareLineForISPDetectionFilePopulation
                                            (
                                                    internetServiceProviderFromSql.getOperatorInternalId()
                                            )
                    );
                }

                //now write csv data into preparation file.
                BufferedWriter preparationWriter = new BufferedWriter
                        (new FileWriter(new File(this.ispPreparationFile)));

                preparationWriter.write(csvFileDataBuffer.toString());
                preparationWriter.close();

                //now move this file atomically to the detection file destination.
                GeoCommonUtils.moveFileAtomicallyToDestination(this.ispPreparationFile,this.ispDetectionFile);

                lastRefreshStartTime = startTime;
            }
            catch (IOException ioe)
            {
                throw new RefreshException("IOException in ThirdpartydataISPDataLoader, cause: ",ioe);
            }
            catch (Exception e)
            {
                throw new RefreshException("Exception in ThirdpartydataISPDataLoader, cause: ",e);
            }
            finally
            {
                DBExecutionUtils.closeConnection(connection);
                //also destroy the country detector instance.
                countryDetectionCache.releaseResources();
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
    private class ISPDataLoadingTask extends TimerTask
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
                cacheLogger.error("Exception while loading isp data inside ISPDataLoadingTask in the class ThirdpartydataISPDataLoader",e);
            }
        }
    }
}
