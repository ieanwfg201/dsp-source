package com.kritter.maxmind.loader;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.geo.utils.IPTransformer;
import com.kritter.maxmind.entity.MaxmindORGInputData;
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
 * This class reads CSV input file containing maxmind organization database.
 * Uses the ip ranges to form the detection database as CSV file.
 * This class also needs to maintain data from sql for organization to look
 * for if the data is already present in sql or not.
 *
 * format: ip1,ip2,country_code,org_name.
 *
 * org_name is the actual brand name (local name ) owning that iprange.
 * So when it comes to mapping ipranges to brands like Airtel,Vodafone,
 * this data should be referred to for usage in isp_mappings table.
 *
 * Data format: "27.56.0.1","27.56.255.254","IN","Airtel"
 */
public class MaxMindOrgDataLoader implements ThirdPartyDataLoader
{
    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private String orgDatabaseFileFullPath;
    private String orgDetectionFile;
    private String orgPreparationFile;

    //for country detector
    private Map<String,String> maxmindDataSourceWithCountryDetectionFile;
    private String[] dataSourceValuesOnlyForMaxmind;
    private long largeReloadFrequencyForCountryDetectionCache;

    private DatabaseManager databaseManager;

    private int batchSizeForSqlInsertion;
    private Long lastRefreshStartTime = null;
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    private static final String ORG_SQL_DATA_QUERY_PREFIX = "insert into isp " +
                                                            "(country_id,isp_name,data_source_name," +
                                                            "modified_on) values ";

    /**
     * Maxmind Org data loader constructor, it uses country detection class,
     * the reload frequency for country detection cache has to be very high.
     * So that even before any reload the isp data loading is finished.
     * As country detection data is anyways loaded inside detector
     * constructor.
     * @param loggerName
     * @param dataSourceName
     * @param reloadFrequency
     * @param orgDatabaseFileFullPath
     * @param orgDetectionFile
     * @param orgPreparationFile
     * @param maxmindDataSourceWithCountryDetectionFile
     * @param dataSourceValuesOnlyForMaxmind
     * @param largeReloadFrequencyForCountryDetectionCache
     * @param batchSizeForSqlInsertion
     * @throws Exception
     */
    public MaxMindOrgDataLoader(
                                String loggerName,
                                String dataSourceName,
                                long reloadFrequency,
                                String orgDatabaseFileFullPath,
                                String orgDetectionFile,
                                String orgPreparationFile,
                                Map<String,String> maxmindDataSourceWithCountryDetectionFile,
                                String[] dataSourceValuesOnlyForMaxmind,
                                long largeReloadFrequencyForCountryDetectionCache,
                                DatabaseManager databaseManager,
                                int batchSizeForSqlInsertion
                               ) throws Exception
    {
        this.logger = LogManager.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.orgDatabaseFileFullPath = orgDatabaseFileFullPath;
        this.orgDetectionFile = orgDetectionFile;
        this.orgPreparationFile = orgPreparationFile;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
        this.maxmindDataSourceWithCountryDetectionFile = maxmindDataSourceWithCountryDetectionFile;
        this.dataSourceValuesOnlyForMaxmind = dataSourceValuesOnlyForMaxmind;
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
        this.timerTask = new ORGDataLoadingTask();
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
        readCSVFileForOrganizationDataAndPrepareSqlAndCSVDatabase();
    }

    private void readCSVFileForOrganizationDataAndPrepareSqlAndCSVDatabase() throws Exception
    {
        if(null == this.orgDatabaseFileFullPath)
            throw new RefreshException("MaxmindOrganizationDataFilePath provided is null, cannot proceed!");

        //if input data file for organization database is not present then skip everything
        //and log error.
        File inputDataFile = null;
        try
        {
            inputDataFile = new File(this.orgDatabaseFileFullPath);
            if(null == inputDataFile || !inputDataFile.exists())
            {
                logger.error("Maxmind Organization Geo-141 CSV at the file path: ,{} could not be read due to error being ",this.orgDatabaseFileFullPath);
                return;
            }
        }
        catch (Exception e)
        {
            logger.error("Maxmind Organization Geo-141 CSV at the file path: {}",this.orgDatabaseFileFullPath);
	    logger.error("could not be read due to error being ", e);
            return;
        }

        //prepare country detection class first to lookup the country ids for the ip-ranges of ISPs.
        CountryDetectionCache countryDetectionCache =
                                    new CountryDetectionCache(this.logger.getName(),
                                                              maxmindDataSourceWithCountryDetectionFile,
                                                              dataSourceValuesOnlyForMaxmind,
                                                              largeReloadFrequencyForCountryDetectionCache);


        long fileModifyTime = inputDataFile.lastModified();

        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            Set<MaxmindORGInputData> maxmindORGInputDataSet = new HashSet<MaxmindORGInputData>();
            BufferedReader br = null;
            Connection connection = fetchConnectionToDatabase();

            Map<String,InternetServiceProvider> orgDataFromSql = GeoCommonUtils.
                                                fetchISPDataFromSqlDatabaseForDataSource(connection, dataSourceName);

            try
            {
                Long startTime = new Date().getTime();
                br = new BufferedReader(new FileReader(inputDataFile));
                String line = null;
                try
                {

                    /*"27.56.0.1","27.56.255.254","IN","Airtel"*/
                    while(null != (line = br.readLine()))
                    {
                        String lineParts[] = line.split(COMMA);

                        if(lineParts.length != 4)
                            throw new RefreshException("The line in organization file is of incorrect length " + line);

                        String ip1[] = lineParts[0].split(QUOTE);
                        String ip2[] = lineParts[1].split(QUOTE);

                        if(ip1.length != 2 && ip2.length != 2)
                            throw new RefreshException("The ip parts in file is incorrect " + line);

                        BigInteger startIpValue =
                                new BigInteger(String.valueOf(IPTransformer.fetchLongValueForIPV4(ip1[1])));
                        BigInteger endIpValue =
                                new BigInteger(String.valueOf(IPTransformer.fetchLongValueForIPV4(ip2[1])));

                        String countryCodeParts[] = lineParts[2].split(QUOTE);
                        if(countryCodeParts.length != 2)
                            throw new RefreshException("The country code part in file is incorrect " + line);

                        String countryCode = countryCodeParts[1];

                        String orgNameParts[] = lineParts[3].split(QUOTE);
                        if(orgNameParts.length != 2)
                            throw new RefreshException("The organization name part in file is incorrect " + line);

                        String orgNameValue = orgNameParts[1];

                        //IMPORTANT: just in case if replacement character is present.
                        //remove it from string.
                        String orgNameInISOCharacterSet = new String(orgNameValue.getBytes(),"ISO-8859-1");
                        String orgName = new String(orgNameInISOCharacterSet.getBytes(),"UTF-8");

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
                                logger.error("Country is null for operator ,start ip: {}", startIpValue);
                                continue;
                            }
                        }
                        catch (Exception e)
                        {
                            throw new Exception("Exception inside MaxMindOrgDataLoader in " +
                                    "fetching country, cannot proceed." , e);
                        }

                        MaxmindORGInputData maxmindORGInputData =
                                                         new MaxmindORGInputData(
                                                                                 startIpValue,
                                                                                 endIpValue,
                                                                                 country.getCountryInternalId(),
                                                                                 countryCode,
                                                                                 orgName,
                                                                                 this.dataSourceName
                                                                                );

                        if(maxmindORGInputDataSet.contains(maxmindORGInputData))
                            throw new RefreshException("The Maxmind ISP input data is corrupted, entry is duplicate: "
                                                       + maxmindORGInputData.getStartIpValue() + ","
                                                       + maxmindORGInputData.getEndIpValue() );

                        maxmindORGInputDataSet.add(maxmindORGInputData);
                    }
                }
                catch (Exception e)
                {
                    throw new RefreshException("The Maxmind organization input data is corrupted ",e);
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
                                                       "refresh in MaxMindOrgDataLoader : ", ioe);
                        }
                    }
                }
                //use the data set to insert into CSV detection file and sql database in batches.

                int batchSizeCounter = 0;
                int totalEntities = 0;

                Iterator<MaxmindORGInputData> iterator = maxmindORGInputDataSet.iterator();

                //initialize csv and sql buffer.
                StringBuffer csvFileDataBuffer = new StringBuffer();
                StringBuffer sqlDataBuffer = new StringBuffer();

                while (iterator.hasNext())
                {
                    totalEntities ++;

                    MaxmindORGInputData maxmindORGInputData = iterator.next();

                    //if the org data already exists in sql database no need to insert.
                    //check for a given country if the isp name exists.
                    if(!orgDataFromSql.containsKey(
                            InternetServiceProvider.
                                    prepareCountryIdIspNameUniqueKey(maxmindORGInputData.getCountryId(),maxmindORGInputData.getOrgName()))
                            )
                    {
                        sqlDataBuffer.append(maxmindORGInputData.prepareOrgRowForInsertion());
                        orgDataFromSql.put(InternetServiceProvider.
                                prepareCountryIdIspNameUniqueKey(maxmindORGInputData.getCountryId(),maxmindORGInputData.getOrgName()),null);
                        batchSizeCounter ++;
                    }

                    if(
                       batchSizeCounter == batchSizeForSqlInsertion      ||
                       totalEntities    == maxmindORGInputDataSet.size()
                      )
                    {

                        int rowsInserted = 0;
                        if(sqlDataBuffer.length() > 0)
                        {
                            sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                            StringBuffer finalORGDataInsertionQuery = new StringBuffer(ORG_SQL_DATA_QUERY_PREFIX);

                            finalORGDataInsertionQuery.append(sqlDataBuffer.toString());

                            Statement stmt = null;

                            try
                            {
                                stmt = connection.createStatement();
                                rowsInserted = stmt.executeUpdate(finalORGDataInsertionQuery.toString());
                            }
                            catch (SQLException sqle)
                            {
                                throw new Exception("Rows insertion of maxmind org data could not happen." +
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
                                throw new Exception("Rows insertion of maxmind org " +
                                                    "data could not happen.Aborting!!!");

                            batchSizeCounter = 0;
                            sqlDataBuffer = new StringBuffer();
                        }
                    }
                }

                //run iterator again on the input data and prepare CSV data this time.
                orgDataFromSql = GeoCommonUtils.fetchISPDataFromSqlDatabaseForDataSource(
                                                                                         connection,
                                                                                         this.dataSourceName
                                                                                        );

                //push isp ids to ui table for targeting exposure.
                GeoCommonUtils.feedISPDataForTargetingExposureOnUserInterface
                        (
                                logger,
                                new HashSet<InternetServiceProvider>(orgDataFromSql.values()),
                                connection,
                                this.dataSourceName,
                                batchSizeForSqlInsertion
                        );

                iterator = maxmindORGInputDataSet.iterator();

                while(iterator.hasNext())
                {
                    MaxmindORGInputData maxmindORGInputData = iterator.next();
                    InternetServiceProvider internetServiceProviderFromSql = orgDataFromSql.get
                            (
                                    InternetServiceProvider.
                                            prepareCountryIdIspNameUniqueKey(maxmindORGInputData.getCountryId(),
                                                                             maxmindORGInputData.getOrgName())
                            );

                    csvFileDataBuffer.append(
                            maxmindORGInputData.
                                    prepareLineForORGDetectionFilePopulation
                                            (
                                                    internetServiceProviderFromSql.getOperatorInternalId()
                                            )
                    );
                }

                //now write csv data into preparation file.
                BufferedWriter preparationWriter = new BufferedWriter
                        (new FileWriter(new File(this.orgPreparationFile)));

                preparationWriter.write(csvFileDataBuffer.toString());
                preparationWriter.close();

                //now move this file atomically to the detection file destination.
                GeoCommonUtils.moveFileAtomicallyToDestination(this.orgPreparationFile,this.orgDetectionFile);

                lastRefreshStartTime = startTime;
            }
            catch (IOException ioe)
            {
                throw new RefreshException("IOException in MaxmindOrgDataLoader, cause: ",ioe);
            }
            catch (Exception e)
            {
                throw new RefreshException("Exception in MaxmindOrgDataLoader, cause: ",e);
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
     * This class is responsible for looking up for any updates in org data file.
     */
    private class ORGDataLoadingTask extends TimerTask
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
                cacheLogger.error("Exception while loading isp data inside OrganizationDataLoadingTask in the class MaxMindOrgDataLoader",e);
            }
        }
    }
}
