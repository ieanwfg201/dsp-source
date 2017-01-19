package com.kritter.openrtb.loader;

import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.MCCMNCISPCountryEntity;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * This class loads country and carrier data into
 * country,isp, ui_targeting_country and ui_targeting_isp
 * table by using mcc_mnc table data made available from
 * http://mcc-mnc.com site which has the most updated
 * mcc-mnc codes.
 */
public class OpenRTBCountryISPDataLoader implements ThirdPartyDataLoader
{
    private Logger logger;
    private String dataSourceName;
    private long reloadFrequency;
    private DatabaseManager databaseManager;
    private int batchSizeForSqlInsertion;
    private Timer timer;
    private TimerTask timerTask;
    private static final String COUNTRY_SQL_DATA_QUERY_PREFIX = "insert into country " +
                                                                "(country_code,country_name,data_source_name," +
                                                                "modified_on) values ";
    private static final String ISP_SQL_DATA_QUERY_PREFIX = "insert into isp " +
                                                            "(country_id,isp_name,data_source_name," +
                                                            "modified_on) values ";

    public OpenRTBCountryISPDataLoader(String loggerName,
                                       String dataSourceName,
                                       long reloadFrequency,
                                       DatabaseManager databaseManager,
                                       int batchSizeForSqlInsertion) throws Exception
    {
        this.logger = LogManager.getLogger(loggerName);
        this.dataSourceName = dataSourceName;
        this.reloadFrequency = reloadFrequency;
        this.databaseManager = databaseManager;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
    }

    @Override
    public DATA_LOADER_TYPE getGeoDataLoaderType()
    {
        return DATA_LOADER_TYPE.COUNTRY_ISP_DATA;
    }

    @Override
    public String getDataSourceName()
    {
        return dataSourceName;
    }

    @Override
    public void scheduleInputDatabaseConversionAndPopulationForInternalUsage() throws Exception
    {
        //load data once at the startup
        prepareDatabase();

        this.timer = new Timer();
        this.timerTask = new CountryISPDataLoadingTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    private Connection fetchConnectionToDatabase() throws Exception
    {
        return this.databaseManager.getConnectionFromPool();
    }

    /**
     * Populate country and isp tables along with
     * ui_targeting_country and ui_targeting_isp tables.
     * @throws Exception
     */
    private void prepareDatabase() throws Exception
    {
        Connection connection = fetchConnectionToDatabase();

        try
        {

            //fetch mcc mnc data from sql database.
            Map<String,MCCMNCISPCountryEntity> mccmncispCountryEntityMap =
                                                        GeoCommonUtils.fetchMCCMNCDataFromSQLDatabase(connection);

            //fetch country data from sql database for this datasource.
            Map<String,Country> countryDataFromSqlDatabase = GeoCommonUtils.
                                                                fetchCountryDataFromSqlDatabaseAgainstCodeForDataSource
                                                                        (
                                                                                connection,
                                                                                this.dataSourceName
                                                                        );

            if(null == mccmncispCountryEntityMap || mccmncispCountryEntityMap.size() == 0)
            {
                logger.error("No MCC MNC data could be found for loading, skipping further workflow, inside prepareDatabase of OpenRTBCountryISPDataLoader");
                return;
            }

            Set<Map.Entry<String,MCCMNCISPCountryEntity>> entrySet = mccmncispCountryEntityMap.entrySet();

            Iterator<Map.Entry<String,MCCMNCISPCountryEntity>> iterator = entrySet.iterator();

            StringBuffer dataBuffer = new StringBuffer();

            int batchSizeCounter = 0;
            int totalEntities = 0;

            while(iterator.hasNext())
            {
                totalEntities ++;

                Map.Entry<String,MCCMNCISPCountryEntity> entityEntry = iterator.next();
                MCCMNCISPCountryEntity value = entityEntry.getValue();

                //if the country data already exists in sql database no need to insert.
                if(!countryDataFromSqlDatabase.containsKey(value.getCountryCode()))
                {
                    dataBuffer.append(value.prepareCountryRowForInsertion(dataSourceName));
                    countryDataFromSqlDatabase.put(value.getCountryCode(),null);
                    batchSizeCounter ++;
                }

                if(
                   batchSizeCounter == batchSizeForSqlInsertion ||
                   totalEntities == mccmncispCountryEntityMap.size()
                  )
                {
                    int rowsInserted = 0;
                    if(dataBuffer.length() > 0)
                    {
                        dataBuffer.deleteCharAt(dataBuffer.length()-1);
                        StringBuffer finalCountryDataInsertionQuery = new StringBuffer(COUNTRY_SQL_DATA_QUERY_PREFIX);

                        finalCountryDataInsertionQuery.append(dataBuffer.toString());

                        Statement stmt = null;

                        try
                        {
                            stmt = connection.createStatement();
                            rowsInserted = stmt.executeUpdate(finalCountryDataInsertionQuery.toString());
                        }
                        catch (SQLException sqle)
                        {
                            throw new Exception("Rows insertion of openrtb country data could not happen." +
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
                            throw new Exception("Rows insertion of openrtb country " +
                                                "data could not happen.Aborting!!!");

                        batchSizeCounter = 0;
                        dataBuffer = new StringBuffer();
                    }
                }
            }

            /*---------------Insertion of country database complete, now insert isp data--------------*/

            countryDataFromSqlDatabase = GeoCommonUtils.fetchCountryDataFromSqlDatabaseAgainstCodeForDataSource
                                                                                        (
                                                                                            connection,
                                                                                            this.dataSourceName
                                                                                        );

            Map<String,InternetServiceProvider> ispDataFromSqlDatabase = GeoCommonUtils.
                                                   fetchISPDataFromSqlDatabaseForDataSource(connection, dataSourceName);

            batchSizeCounter = 0;
            totalEntities = 0;
            dataBuffer = new StringBuffer();
            iterator = entrySet.iterator();

            while(iterator.hasNext())
            {
                totalEntities ++;

                Map.Entry<String,MCCMNCISPCountryEntity> entityEntry = iterator.next();
                MCCMNCISPCountryEntity value = entityEntry.getValue();

                Country country = countryDataFromSqlDatabase.get(value.getCountryCode());

                String keyCountryIdIspName =  InternetServiceProvider.
                        prepareCountryIdIspNameUniqueKey(country.getCountryInternalId(), value.getIspName());

                if(!ispDataFromSqlDatabase.containsKey(keyCountryIdIspName))
                {
                    dataBuffer.append(value.prepareIspRowForInsertion(country.getCountryInternalId(), dataSourceName));

                    ispDataFromSqlDatabase.put(keyCountryIdIspName,null);

                    batchSizeCounter ++;
                }

                if(batchSizeCounter == batchSizeForSqlInsertion ||
                   totalEntities == mccmncispCountryEntityMap.size())
                {
                    int rowsInserted = 0;
                    if(dataBuffer.length() > 0)
                    {
                        dataBuffer.deleteCharAt(dataBuffer.length()-1);
                        StringBuffer finalISPDataInsertionQuery = new StringBuffer(ISP_SQL_DATA_QUERY_PREFIX);

                        finalISPDataInsertionQuery.append(dataBuffer.toString());

                        Statement stmt = null;

                        try
                        {
                            stmt = connection.createStatement();
                            rowsInserted = stmt.executeUpdate(finalISPDataInsertionQuery.toString());
                        }
                        catch (SQLException sqle)
                        {
                            throw new Exception("Rows insertion of open rtb isp data could not happen." +
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
                            throw new Exception("Rows insertion of openrtb isp " +
                                                "data could not happen.Aborting!!!");

                        batchSizeCounter = 0;
                        dataBuffer = new StringBuffer();
                    }
                }
            }


            /*--------Insertion into country and isp table done, now insert into ui targeting tables.*/

            countryDataFromSqlDatabase = GeoCommonUtils.fetchCountryDataFromSqlDatabaseAgainstCodeForDataSource
                    (
                            connection,
                            this.dataSourceName
                    );

            ispDataFromSqlDatabase = GeoCommonUtils.fetchISPDataFromSqlDatabaseForDataSource(connection,dataSourceName);

            GeoCommonUtils.feedCountryDataForTargetingExposureOnUserInterface
                                                                             (
                                                                              countryDataFromSqlDatabase,
                                                                              connection,
                                                                              batchSizeForSqlInsertion
                                                                             );

            GeoCommonUtils.feedISPDataForTargetingExposureOnUserInterfaceWithoutUsingIspMappings
                           (
                            logger,
                            new HashSet<InternetServiceProvider>(ispDataFromSqlDatabase.values()),
                            connection,
                            dataSourceName,
                            batchSizeForSqlInsertion
                           );
        }
        catch (Exception e)
        {
            logger.error("Exception inside OpenRTBCountryISPDataLoader ",e);
            throw new Exception("Exception inside OpenRTBCountryISPDataLoader ",e);
        }
        finally
        {
            DBExecutionUtils.closeConnection(connection);
        }
    }

    @Override
    public void releaseResources()
    {

    }

    private class CountryISPDataLoadingTask extends TimerTask
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
                cacheLogger.error("Exception while loading country and isp data inside CountryISPDataLoadingTask in the class OpenRTBCountryISPDataLoader",e);
            }
        }
    }
}
