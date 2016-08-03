package com.kritter.geo.common.utils;

import com.google.common.io.Files;
import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.geo.common.entity.*;
import com.kritter.geo.common.entity.reader.CountryUserInterfaceIdCache;
import com.kritter.geo.common.entity.reader.StateUserInterfaceIdCache;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * This class has utility methods to manage geo sql database.
 */
public class GeoCommonUtils
{
    private static final String QUERY_COUNTRY_DATA_LOAD = "select * from country where data_source_name = ?";
    private static final String QUERY_ISP_DATA_LOAD = "select * from isp where data_source_name = ?";
    private static final String QUERY_STATE_DATA_LOAD = "select * from state where data_source_name = ?";
    private static final String QUERY_CITY_DATA_LOAD = "select * from city where data_source_name = ?";
    private static final String QUERY_COUNTRY_UI_DATA_LOAD = "select * from ui_targeting_country";
    private static final String QUERY_STATE_UI_DATA_LOAD = "select * from ui_targeting_state";
    private static final String QUERY_CITY_UI_DATA_LOAD = "select * from ui_targeting_city";
    private static final String QUERY_ISP_UI_DATA_LOAD = "select * from ui_targeting_isp";
    private static final String QUERY_ISP_MAPPINGS_DATA_LOAD = "select * from isp_mappings";
    private static final String QUERY_MCC_MNC_DATA_LOAD = "select * from mcc_mnc";
    private static final int EARTH_RADIUS_MILES = 3959;
    private static final Logger logger = LoggerFactory.getLogger("cache.logger");
    /**
     * This function fetches country data from sql database for a given data source.
     * @param connectionToDatabase
     * @param dataSourceName
     * @return
     * @throws Exception
     */
    public static Map<String,Country> fetchCountryDataFromSqlDatabaseForDataSource
                                                                    (Connection connectionToDatabase,
                                                                     String dataSourceName) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<String,Country> dataMapToReturn = new HashMap<String, Country>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_COUNTRY_DATA_LOAD);
            pstmt.setString(1,dataSourceName);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                int countryId = rs.getInt("id");
                String countryCode = rs.getString("country_code");
                String countryName = rs.getString("country_name");

                Country country = new Country(countryId,dataSourceName);
                country.setCountryCode(countryCode);
                country.setCountryName(countryName);

                dataMapToReturn.put(countryName, country);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting country database",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return dataMapToReturn;
    }

    /**
     * This function fetches state data from mysql for a given data source.
     * Data formed is countryid control-A stateName -> state.
     * WARNING: This method does not close connection, have to be closed
     * explicitly from method calling this method.
     * @param connectionToDatabase
     * @param dataSourceName
     * @return
     * @throws SQLException
     */
    public static Map<String,State> fetchStateDataFromSqlDatabaseForDataSource(
                                                                               Connection connectionToDatabase,
                                                                               String dataSourceName
                                                                              ) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<String,State> dataMapToReturn = new HashMap<String, State>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_STATE_DATA_LOAD);
            pstmt.setString(1,dataSourceName);
            rs = pstmt.executeQuery();

            String key = null;

            while(rs.next())
            {
                int stateId = rs.getInt("id");
                int countryId = rs.getInt("country_id");
                String stateName = rs.getString("state_name");
                String stateCode = rs.getString("state_code");
                key = State.generateKeyForUniquenessCheck(countryId,stateName);
                State state = new State(stateId,dataSourceName);
                state.setCountryId(countryId);
                state.setStateCode(stateCode);
                state.setStateName(stateName);

                dataMapToReturn.put(key,state);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting state database",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return dataMapToReturn;
    }

    /**
     * This function fetches city data from mysql for a given data source.
     * Data formed is stateId control-A cityName -> city.
     * WARNING: This method does not close connection, have to be closed
     * explicitly from method calling this method.
     * @param connectionToDatabase
     * @param dataSourceName
     * @return
     * @throws SQLException
     */
    public static Map<String,City> fetchCityDataFromSqlDatabaseForDataSource(
                                                                             Connection connectionToDatabase,
                                                                             String dataSourceName
                                                                            ) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<String,City> dataMapToReturn = new HashMap<String, City>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_CITY_DATA_LOAD);
            pstmt.setString(1,dataSourceName);
            rs = pstmt.executeQuery();

            String key = null;

            while(rs.next())
            {
                int cityId = rs.getInt("id");
                Integer stateId = rs.getInt("state_id");
                String cityCode = rs.getString("city_code");
                String cityName = rs.getString("city_name");
                Timestamp timestamp = rs.getTimestamp("modified_on");
                key = City.generateKeyForUniquenessCheck(stateId,cityName);
                City city = new City(cityId,dataSourceName);
                city.setStateId(stateId);
                city.setCityCode(cityCode);
                city.setCityName(cityName);
                city.setLastUpdated(timestamp);
                dataMapToReturn.put(key,city);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting city database",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return dataMapToReturn;
    }

    /**
     * This function fetches country data from sql database for a given data source
     * against the country code.
     * WARNING: This method does not close connection, have to be closed
     * explicitly from method calling this method.
     * @param connectionToDatabase
     * @param dataSourceName
     * @return
     * @throws Exception
     */
    public static Map<String,Country>
                            fetchCountryDataFromSqlDatabaseAgainstCodeForDataSource
                                                                                   (
                                                                                    Connection connectionToDatabase,
                                                                                    String dataSourceName
                                                                                   ) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<String,Country> dataMapToReturn = new HashMap<String, Country>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_COUNTRY_DATA_LOAD);
            pstmt.setString(1,dataSourceName);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                int countryId = rs.getInt("id");
                String countryCode = rs.getString("country_code");
                String countryName = rs.getString("country_name");

                Country country = new Country(countryId,dataSourceName);
                country.setCountryCode(countryCode);
                country.setCountryName(countryName);

                dataMapToReturn.put(countryCode, country);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting country database",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return dataMapToReturn;
    }

    /**
     * This method fetches country data against internal country id.
     * WARNING: This method does not close connection, have to be closed
     * explicitly from method calling this method.
     * @param connectionToDatabase
     * @param dataSourceName
     * @return
     * @throws Exception
     */
    public static Map<Integer,Country>
                              fetchCountryDataAgainstIdFromSqlDatabaseForDataSource(
                                                                                    Connection connectionToDatabase,
                                                                                    String dataSourceName
                                                                                   ) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<Integer,Country> dataMapToReturn = new HashMap<Integer, Country>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_COUNTRY_DATA_LOAD);
            pstmt.setString(1,dataSourceName);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                int countryId = rs.getInt("id");
                String countryTwoLetterCode = rs.getString("country_code");
                String countryName = rs.getString("country_name");

                Country country = new Country(countryId,dataSourceName);
                country.setCountryCode(countryTwoLetterCode);
                country.setCountryName(countryName);

                dataMapToReturn.put(countryId, country);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting country database",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.", e);
            }
        }

        return dataMapToReturn;
    }

    /**
     * This function fetches isp data from sql database for a given data source.
     * WARNING: This method does not close connection, have to be closed
     * explicitly from method calling this method.
     * @param connectionToDatabase
     * @param dataSourceName
     * @return
     * @throws Exception
     */
    public static Map<String,InternetServiceProvider> fetchISPDataFromSqlDatabaseForDataSource
                                                                (
                                                                 Connection connectionToDatabase,
                                                                 String dataSourceName
                                                                ) throws Exception
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<String,InternetServiceProvider> dataMapToReturn = new HashMap<String, InternetServiceProvider>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_ISP_DATA_LOAD);
            pstmt.setString(1,dataSourceName);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                int ispId = rs.getInt("id");
                int countryId = rs.getInt("country_id");
                String ispName = rs.getString("isp_name");

                InternetServiceProvider internetServiceProvider = new InternetServiceProvider(
                                                                                              ispId,
                                                                                              countryId,
                                                                                              dataSourceName
                                                                                             );
                internetServiceProvider.setOperatorName(ispName);

                dataMapToReturn.put(InternetServiceProvider.prepareCountryIdIspNameUniqueKey(countryId,ispName),
                                    internetServiceProvider);
            }
        }
        catch (SQLException e)
        {
            throw new Exception("Inside GeoCommonUtils, SQLException in getting isp database",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new Exception(
                        "Inside GeoCommonUtils, SQLException in closing connection meta resources.",
                        e);
            }
        }

        return dataMapToReturn;
    }



    /**
     * This method loads isp_mappings data into memory.
     * WARNING: This method does not close connection, have to be closed
     * explicitly from method calling this method.
     * @param connectionToDatabase
     * @return
     * @throws SQLException
     */
    public static Map<String,IspMappingsValueEntity> fetchIspNameVersusIspUINameMappingsData
                                                                            (
                                                                                Connection connectionToDatabase
                                                                            ) throws SQLException

    {
        Map<String,IspMappingsValueEntity> ispMappingsData = new HashMap<String, IspMappingsValueEntity>();

        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = connectionToDatabase.createStatement();
            rs = stmt.executeQuery(QUERY_ISP_MAPPINGS_DATA_LOAD);

            while(rs.next())
            {
                String countryName = rs.getString("country_name");
                String ispName = rs.getString("isp_name");
                String ispUIName = rs.getString("isp_ui_name");
                short isMarkedForDeletion = rs.getShort("is_marked_for_deletion");

                CountryIspUnique countryIspUnique = new CountryIspUnique(countryName,ispName,ispUIName);
                IspMappingsValueEntity ispMappingsValueEntity =
                                    new IspMappingsValueEntity(countryIspUnique.prepareValue(),isMarkedForDeletion);

                ispMappingsData.put(countryIspUnique.prepareKey(),ispMappingsValueEntity);
            }

        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting isp mappings database ",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != stmt)
                    stmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException(
                        "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return ispMappingsData;
    }

    private static Map<String,UICountry> fetchUiCountryData(Connection connectionToDatabase) throws SQLException
    {
        Map<String,UICountry> countryDataFromUIDatabase = new HashMap<String, UICountry>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_COUNTRY_UI_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                Integer id = rs.getInt("id");
                String countryCode = rs.getString("country_code");
                String countryName = rs.getString("country_name");
                Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(rs, "entity_id_set");
                Set<Integer> countryIdSetForAllDataSources = ( null != entityIdSet ?
                                                               new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                                                               new HashSet<Integer>());

                UICountry uiCountry = new UICountry(id,countryCode,countryName,countryIdSetForAllDataSources);

                countryDataFromUIDatabase.put(countryName, uiCountry);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting UICountry database ",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException(
                        "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return countryDataFromUIDatabase;
    }

    private static Map<String,UIState> fetchUiStateData(Connection connectionToDatabase) throws SQLException
    {
        Map<String,UIState> stateDataFromUIDatabase = new HashMap<String, UIState>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_STATE_UI_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                Integer id = rs.getInt("id");
                Integer countryUiId = rs.getInt("country_ui_id");
                String stateName = rs.getString("state_name");
                Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(rs, "entity_id_set");
                Set<Integer> stateIdSetForAllDataSources = (
                                                             null != entityIdSet ?
                                                             new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                                                             new HashSet<Integer>()
                                                           );

                UIState uiState = new UIState(id,countryUiId,stateName,stateIdSetForAllDataSources);

                String key = UIState.generateKeyForUniquenessCheck(countryUiId,stateName);
                stateDataFromUIDatabase.put(key, uiState);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting UICountry database ",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException(
                        "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return stateDataFromUIDatabase;
    }

    private static Map<String,UICity> fetchUiCityData(Connection connectionToDatabase) throws SQLException
    {
        Map<String,UICity> cityDataFromUIDatabase = new HashMap<String, UICity>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_CITY_UI_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                Integer id = rs.getInt("id");
                Integer stateUiId = rs.getInt("state_ui_id");
                String cityName = rs.getString("city_name");
                Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(rs, "entity_id_set");
                Set<Integer> cityIdSetForAllDataSources = (
                                                            null != entityIdSet ?
                                                            new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                                                            new HashSet<Integer>()
                                                          );

                UICity uiCity = new UICity(id,stateUiId,cityName,cityIdSetForAllDataSources);

                String key = UICity.generateKeyForUniquenessCheck(stateUiId,cityName);
                cityDataFromUIDatabase.put(key, uiCity);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting UIState database ",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException(
                        "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return cityDataFromUIDatabase;
    }

    /**
     * This function retrieves UICountry data from database against their countryCode.
     * This function closes the connection being passed to it.
     * @param connectionToDatabase
     * @return
     * @throws SQLException
     */
    public static Map<String,UICountry>
                            fetchUiCountryAgainstCountryCode(Connection connectionToDatabase) throws SQLException
    {
        Map<String,UICountry> uiCountryDatabase = new HashMap<String,UICountry>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_COUNTRY_UI_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                Integer id = rs.getInt("id");
                String countryCode = rs.getString("country_code");
                String countryName = rs.getString("country_name");

                Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(rs, "entity_id_set");
                Set<Integer> countryIdSetForAllDataSources = ( null != entityIdSet ?
                                                               new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                                                               new HashSet<Integer>());

                UICountry uiCountry = new UICountry(id,countryCode,countryName,countryIdSetForAllDataSources);

                uiCountryDatabase.put(countryCode,uiCountry);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting UICountry database ",e);
        }
        finally
        {
            DBExecutionUtils.closeResources(connectionToDatabase,pstmt,rs);
        }

        return uiCountryDatabase;
    }

    public static Map<Integer,Integer> fetchUiIspIdAgainstParentId(Connection connectionToDatabase) throws SQLException
    {
        Map<Integer,Integer> uiIspIdAgainstParentIdMap = new HashMap<Integer, Integer>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_ISP_UI_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                Integer id = rs.getInt("id");
                Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(rs, "entity_id_set");
                Set<Integer> ispIdSetForAllDataSources = ( null != entityIdSet ?
                                                           new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                                                           new HashSet<Integer>());

                if(null != ispIdSetForAllDataSources)
                {
                    for(Integer ispParentId : ispIdSetForAllDataSources)
                        uiIspIdAgainstParentIdMap.put(ispParentId,id);
                }
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting " +
                                   "UIIntenetServiceProvider database ",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
                DBExecutionUtils.closeConnection(connectionToDatabase);
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return uiIspIdAgainstParentIdMap;
    }

    /**
     * This function feeds a set of country entity into ui_targeting_country table
     * for targeting exposure on user interface and for targeting matching purposes.
     * @param countryDataMap
     * @param connectionToDatabase
     * @param sqlBatchSize
     * @throws SQLException
     */
    public static void feedCountryDataForTargetingExposureOnUserInterface(Map<String,Country> countryDataMap,
                                                                          Connection connectionToDatabase,
                                                                          int sqlBatchSize) throws SQLException
    {
        Map<String,UICountry> countryDataFromUIDatabase = fetchUiCountryData(connectionToDatabase);
        Map<String,UICountry> uiCountryNewDataForUIDatabaseStorage = new HashMap<String, UICountry>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //now use input data set to insert new rows or update existing ones.
        Iterator<Country> inputCountryDataSetIterator = countryDataMap.values().iterator();

        StringBuffer sqlDataBuffer = new StringBuffer();
        int batchSizeCounter = 0;
        int totalEntities = 0;

        while(inputCountryDataSetIterator.hasNext())
        {
            totalEntities ++;

            Country country = inputCountryDataSetIterator.next();

            UICountry uiCountry = countryDataFromUIDatabase.get(country.getCountryName());

            if(null == uiCountry)
            {
                Set<Integer> countryIdSet = new HashSet<Integer>();
                countryIdSet.add(country.getCountryInternalId());
                uiCountry = new UICountry(country.getCountryCode(),
                                          country.getCountryName(),
                                          countryIdSet);

                uiCountryNewDataForUIDatabaseStorage.put(country.getCountryName(),uiCountry);

                //update batch size counter only if uiCountry not present in database.
                batchSizeCounter ++;
            }
            else if(!uiCountry.doesIdExistInCountryIdSet(country.getCountryInternalId()))
            {
                uiCountry.addToCountryIdSet(country.getCountryInternalId());
                //since uiCountry is updated with new id, update into database too.
                try
                {

                    int rowsUpdated = 0;

                    pstmt = connectionToDatabase.prepareStatement(UICountry.getUPDATION_QUERY());
                    pstmt.setString(1,ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                                                    (
                                                     uiCountry.getCountryIdSet().
                                                     toArray(new Integer[uiCountry.getCountryIdSet().size()]))
                                                    );
                    pstmt.setString(2,uiCountry.getCountryCode());

                    rowsUpdated = pstmt.executeUpdate();

                    if(rowsUpdated <= 0)
                        throw new SQLException("UICountry entry not present in database for countryname: " +
                                                uiCountry.getCountryName() + " while adding into entityidset");
                }
                catch (SQLException e)
                {
                    throw new SQLException("Inside GeoCommonUtils, SQLException in updating UICountry entry ",e);
                }
                finally
                {
                    try
                    {
                        if (null != rs)
                            rs.close();
                        if (null != pstmt)
                            pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        throw new SQLException(
                                "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
                    }
                }
            }

            if(
               batchSizeCounter == sqlBatchSize       ||
               totalEntities == countryDataMap.size()
              )
            {
                int rowsInserted = 0;

                Iterator<UICountry> it = uiCountryNewDataForUIDatabaseStorage.values().iterator();
                while(it.hasNext())
                {
                    UICountry entry = it.next();
                    sqlDataBuffer.append(entry.prepareNewUICountryRowForInsertionIntoSQL());
                }

                if(sqlDataBuffer.length() > 0)
                {
                    sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                    StringBuffer finalUICountryDataInsertionQuery = new StringBuffer(UICountry.getINSERTION_QUERY());

                    finalUICountryDataInsertionQuery.append(sqlDataBuffer.toString());

                    Statement stmt = null;

                    try
                    {
                        stmt = connectionToDatabase.createStatement();
                        rowsInserted = stmt.executeUpdate(finalUICountryDataInsertionQuery.toString());
                    }
                    catch (SQLException sqle)
                    {
                        throw new SQLException("Rows insertion of uiCountry data could not happen." +
                                               "Aborting!!!",sqle);
                    }
                    finally
                    {
                        if(null != stmt)
                            stmt.close();
                    }

                    if(rowsInserted <= 0)
                        throw new SQLException("Rows insertion of uiCountry data could not happen.Aborting!!!");

                    batchSizeCounter = 0;
                    sqlDataBuffer = new StringBuffer();
                    uiCountryNewDataForUIDatabaseStorage = new HashMap<String, UICountry>();
                    countryDataFromUIDatabase = fetchUiCountryData(connectionToDatabase);
                }
            }
        }
    }

    /**
     * This function inserts isp data into ui_targeting_isp table for targeting exposure on user interface
     * without using isp_mappings data.
     * @param logger
     * @param ispDataSetToPopulate
     * @param connectionToDatabase
     * @param dataSourceName
     * @param sqlBatchSize
     * @throws SQLException
     */
    @Deprecated
    public static void feedISPDataForTargetingExposureOnUserInterfaceWithoutUsingIspMappings
                                                                    (
                                                                     Logger logger,
                                                                     Set<InternetServiceProvider> ispDataSetToPopulate,
                                                                     Connection connectionToDatabase,
                                                                     String dataSourceName,
                                                                     int sqlBatchSize
                                                                    ) throws SQLException
    {
        Map<String,UIInternetServiceProvider> ispUIDataAgainstISPUiNameAndCountryUiId =
                                                                    fetchUiTargetingIspData(connectionToDatabase);

        Map<String,UIInternetServiceProvider> tempIspUIDataAgainstISPUiNameAndCountryUiId =
                                                                    new HashMap<String, UIInternetServiceProvider>();

        Map<Integer,Country> countryData   =
                fetchCountryDataAgainstIdFromSqlDatabaseForDataSource(connectionToDatabase,dataSourceName);

        if(null != countryData)
            logger.debug("Country parent data against id loaded from database with size {}", countryData.size());

        Map<String,UICountry> countryUIData   = fetchUiCountryData(connectionToDatabase);

        if(null != countryUIData)
            logger.debug("country ui data loaded against country name from database, size: {}", countryUIData.size());


        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //now use input data set to insert new rows or update existing ones.
        Iterator<InternetServiceProvider> inputISPDataSetIterator = ispDataSetToPopulate.iterator();

        StringBuffer sqlDataBuffer = new StringBuffer();
        int batchSizeCounter = 0;
        int totalEntities = 0;

        while(inputISPDataSetIterator.hasNext())
        {
            totalEntities ++;
            InternetServiceProvider internetServiceProvider = inputISPDataSetIterator.next();

            Country country = countryData.get(internetServiceProvider.getCountryInternalId());

            UICountry uiCountry = countryUIData.get(country.getCountryName());

            //if country not found throw exception.
            if(null == country || null == uiCountry)
                throw new SQLException("Country and ui country not found for operator inside " +
                                       "feedISPDataForTargetingExposureOnUserInterfaceWithoutUsingIspMappings: " +
                                       internetServiceProvider.getOperatorName());

            UIInternetServiceProvider uiInternetServiceProvider =
                    ispUIDataAgainstISPUiNameAndCountryUiId.get(
                                                                UIInternetServiceProvider.
                                                                   prepareKeyToLookupISPUIEntry(
                                                                           uiCountry.getCountryId(),
                                                                           internetServiceProvider.getOperatorName()
                                                                   )
                                                               );

            if(null == uiInternetServiceProvider)
            {
                Set<Integer> ispIdSet = new HashSet<Integer>();
                ispIdSet.add(internetServiceProvider.getOperatorInternalId());

                logger.debug("ispIdSet populated with operator parent id : {}",
                             internetServiceProvider.getOperatorInternalId());

                logger.debug("Ui country name obtained as: {}", uiCountry.getCountryName());

                uiInternetServiceProvider = new UIInternetServiceProvider(
                                                                          uiCountry.getCountryId(),
                                                                          internetServiceProvider.getOperatorName(),
                                                                          ispIdSet
                                                                         );

                UIInternetServiceProvider absentFromDatabaseUIISP =
                        tempIspUIDataAgainstISPUiNameAndCountryUiId.
                                get(uiInternetServiceProvider.prepareKeyToLookupISPUIEntry());

                if(null != absentFromDatabaseUIISP)
                {
                    absentFromDatabaseUIISP.addToISPIdSet(internetServiceProvider.getOperatorInternalId());
                    tempIspUIDataAgainstISPUiNameAndCountryUiId.put(
                            absentFromDatabaseUIISP.prepareKeyToLookupISPUIEntry(),
                            absentFromDatabaseUIISP
                    );
                }
                else
                {
                    tempIspUIDataAgainstISPUiNameAndCountryUiId.put(
                            uiInternetServiceProvider.prepareKeyToLookupISPUIEntry(),
                            uiInternetServiceProvider
                    );
                }

                batchSizeCounter ++;
            }
            else if(!uiInternetServiceProvider.doesISPIdExistInISPIdSet
                    (internetServiceProvider.getOperatorInternalId()))
            {
                uiInternetServiceProvider.addToISPIdSet(internetServiceProvider.getOperatorInternalId());

                //since uiInternetServiceProvider is updated with new id, update into database too.
                try
                {
                    int rowsUpdated = 0;

                    pstmt = connectionToDatabase.prepareStatement(UIInternetServiceProvider.getUPDATION_QUERY());
                    pstmt.setString(1,ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                            (
                                    uiInternetServiceProvider.getIspIdSet().
                                            toArray(new Integer[uiInternetServiceProvider.getIspIdSet().size()]))
                    );
                    pstmt.setString(2,uiInternetServiceProvider.getIspUiName());
                    pstmt.setInt(3,uiInternetServiceProvider.getCountryUiId());

                    rowsUpdated = pstmt.executeUpdate();

                    if(rowsUpdated <= 0 )
                        throw new SQLException("Inside GeoCommonUtils, SQLException in updating " +
                                "UIInternetServiceProvider entry , for isp_ui_name " +
                                uiInternetServiceProvider.getIspUiName() + " , entry is missing " +
                                "inside ui_targeting_isp table, so no new id from isp table can be" +
                                "added");
                }
                catch (SQLException e)
                {
                    throw new SQLException("Inside GeoCommonUtils, SQLException in updating " +
                            "UIInternetServiceProvider entry ",e);
                }
                finally
                {
                    try
                    {
                        if (null != rs)
                            rs.close();
                        if (null != pstmt)
                            pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        throw new SQLException(
                                "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
                    }
                }
            }

            if(
               batchSizeCounter == sqlBatchSize ||
               totalEntities == ispDataSetToPopulate.size()
              )
            {
                int rowsInserted = 0;

                //use temp table to get all entries to be inserted.
                Iterator<UIInternetServiceProvider> it = tempIspUIDataAgainstISPUiNameAndCountryUiId.values().iterator();
                while(it.hasNext())
                {
                    UIInternetServiceProvider entry = it.next();
                    sqlDataBuffer.append(entry.prepareNewUIISPRowForInsertionIntoSQL());
                }

                if(sqlDataBuffer.length() > 0)
                {
                    sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                    StringBuffer finalUIISPDataInsertionQuery = new StringBuffer(
                            UIInternetServiceProvider.getINSERTION_QUERY());

                    finalUIISPDataInsertionQuery.append(sqlDataBuffer.toString());

                    Statement stmt = null;

                    try
                    {
                        stmt = connectionToDatabase.createStatement();
                        rowsInserted = stmt.executeUpdate(finalUIISPDataInsertionQuery.toString());
                    }
                    catch (SQLException sqle)
                    {
                        throw new SQLException("Rows insertion of uiInternetServiceProvider data could not happen." +
                                "Aborting!!!",sqle);
                    }
                    finally
                    {
                        if(null != stmt)
                            stmt.close();
                    }

                    if(rowsInserted <= 0)
                        throw new SQLException("Rows insertion of uiInternetServiceProvider data could not " +
                                "happen.Aborting!!!");

                    batchSizeCounter = 0;
                    tempIspUIDataAgainstISPUiNameAndCountryUiId = new HashMap<String, UIInternetServiceProvider>();
                    sqlDataBuffer = new StringBuffer();
                    //fetch all data from ui_targeting_isp inserted till now.
                    ispUIDataAgainstISPUiNameAndCountryUiId = fetchUiTargetingIspData(connectionToDatabase);
                }
            }
        }

    }

    /**
     * This function takes input as isp entries and feeds into ui_targeting_isp table for targeting exposure.
     * @param ispDataSetToPopulate
     * @param connectionToDatabase
     * @param sqlBatchSize
     * @throws SQLException
     */
    public static void feedISPDataForTargetingExposureOnUserInterface(
                                                                      Logger logger,
                                                                      Set<InternetServiceProvider> ispDataSetToPopulate,
                                                                      Connection connectionToDatabase,
                                                                      String dataSourceName,
                                                                      int sqlBatchSize
                                                                     ) throws SQLException
    {
        Map<String,UIInternetServiceProvider> ispUIDataAgainstISPUiNameAndCountryUiId =
                                                                fetchUiTargetingIspData(connectionToDatabase);

        Map<String,UIInternetServiceProvider> tempIspUIDataAgainstISPUiNameAndCountryUiId =
                                                                new HashMap<String, UIInternetServiceProvider>();

        // isp_mappings data into map as key:value => [country_name + isp_name : country_name + isp_ui_name]

        Map<String,IspMappingsValueEntity> ispMappingsData = fetchIspNameVersusIspUINameMappingsData(connectionToDatabase);

        if(null != ispMappingsData)
            logger.debug("Got isp mappings data from database with size {}", ispMappingsData.size());

        //isp_mappings data is empty inside feedISPDataForTargetingExposureOnUserInterface
        //of GeoCommonUtils, cannot populate ui_targeting_isp table.
        if(null == ispMappingsData || ispMappingsData.size() == 0)
        {
            logger.error("IspMappingsData is empty, so cannot populate ui_targeting_isp table...");
            return;
        }

        Map<Integer,Country> countryData   =
                            fetchCountryDataAgainstIdFromSqlDatabaseForDataSource(connectionToDatabase,dataSourceName);

        if(null != countryData)
            logger.debug("Country parent data against id loaded from database with size {}", countryData.size());

        Map<String,UICountry> countryUIData   = fetchUiCountryData(connectionToDatabase);

        if(null != countryUIData)
            logger.debug("country ui data loaded against country name from database, size: {}", countryUIData.size());


        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //now use input data set to insert new rows or update existing ones.
        Iterator<InternetServiceProvider> inputISPDataSetIterator = ispDataSetToPopulate.iterator();

        StringBuffer sqlDataBuffer = new StringBuffer();
        int batchSizeCounter = 0;
        int totalEntities = 0;

        while(inputISPDataSetIterator.hasNext())
        {
            totalEntities ++;
            InternetServiceProvider internetServiceProvider = inputISPDataSetIterator.next();

            Country country = countryData.get(internetServiceProvider.getCountryInternalId());
            UICountry uiCountry = countryUIData.get(country.getCountryName());

            //if country not found throw exception.
            if(null == country || null == uiCountry)
                throw new SQLException("Country not found for operator inside " +
                                       "feedISPDataForTargetingExposureOnUserInterface: " +
                                       internetServiceProvider.getOperatorName());

            String keyToLookupISPUiName = CountryIspUnique.
                    prepareKeyForCountryISPName(country.getCountryName(),internetServiceProvider.getOperatorName());

            logger.debug("keyToLookupISPUiName prepared is : {}", keyToLookupISPUiName);

            //find corresponding isp ui name for input isp name using isp_mappings database.
            IspMappingsValueEntity ispMappingsValueEntity = ispMappingsData.get(keyToLookupISPUiName);
            String ispUiNameWithCountryValue = null;
            if(null != ispMappingsValueEntity)
                ispUiNameWithCountryValue = ispMappingsValueEntity.getIspUiNameWithCountryValue();

            logger.debug("ispUiNameWithCountryValue fetched by lookup in isp mappings data is {}",
                         ispUiNameWithCountryValue);

            CountryIspUnique countryIspUnique = new CountryIspUnique(keyToLookupISPUiName,ispUiNameWithCountryValue);

            UIInternetServiceProvider uiInternetServiceProvider = null;

            if(null != ispUiNameWithCountryValue)
                uiInternetServiceProvider = ispUIDataAgainstISPUiNameAndCountryUiId.get
                                                    (
                                                     UIInternetServiceProvider.
                                                        prepareKeyToLookupISPUIEntry
                                                        (
                                                            uiCountry.getCountryId(),
                                                            countryIspUnique.getIspUiName()
                                                        )
                                                   );

            if(null == uiInternetServiceProvider)
            {
                //new ui entry has to be made into ui_targeting_isp table.
                if(null != ispUiNameWithCountryValue && ispMappingsValueEntity.getIsMarkedForDeletion() == 0)
                {

                    Set<Integer> ispIdSet = new HashSet<Integer>();
                    ispIdSet.add(internetServiceProvider.getOperatorInternalId());

                    logger.debug("ispIdSet populated with operator parent id : {}",
                                 internetServiceProvider.getOperatorInternalId());

                    logger.debug("Ui country name obtained as: {}", uiCountry.getCountryName());

                    uiInternetServiceProvider = new UIInternetServiceProvider(
                                                                              uiCountry.getCountryId(),
                                                                              countryIspUnique.getIspUiName(),
                                                                              ispIdSet
                                                                             );

                    UIInternetServiceProvider absentFromDatabaseUIISP =
                            tempIspUIDataAgainstISPUiNameAndCountryUiId.
                                                    get(uiInternetServiceProvider.prepareKeyToLookupISPUIEntry());

                    if(null != absentFromDatabaseUIISP)
                    {
                        absentFromDatabaseUIISP.addToISPIdSet(internetServiceProvider.getOperatorInternalId());
                        tempIspUIDataAgainstISPUiNameAndCountryUiId.put(
                                                                        absentFromDatabaseUIISP.prepareKeyToLookupISPUIEntry(),
                                                                        absentFromDatabaseUIISP
                                                                       );
                    }
                    else
                    {
                        tempIspUIDataAgainstISPUiNameAndCountryUiId.put(
                                                                        uiInternetServiceProvider.prepareKeyToLookupISPUIEntry(),
                                                                        uiInternetServiceProvider
                                                                       );
                    }

                    batchSizeCounter ++;
                }
            }
            else
            {
                //if datasource id missing against ui-id.
                if(!uiInternetServiceProvider.doesISPIdExistInISPIdSet(internetServiceProvider.getOperatorInternalId())
                   && ispMappingsValueEntity.getIsMarkedForDeletion() == 0)
                    uiInternetServiceProvider.addToISPIdSet(internetServiceProvider.getOperatorInternalId());

                //if datasource id exists and is now marked for deletion.
                else if(uiInternetServiceProvider.doesISPIdExistInISPIdSet(internetServiceProvider.getOperatorInternalId())
                        && ispMappingsValueEntity.getIsMarkedForDeletion() == 1)
                    uiInternetServiceProvider.removeFromISPIdSet(internetServiceProvider.getOperatorInternalId());


                int rowsUpdated = 0;
                //since uiInternetServiceProvider is updated with new id, update into database too.
                try
                {
                    pstmt = connectionToDatabase.prepareStatement(UIInternetServiceProvider.getUPDATION_QUERY());
                    pstmt.setString(1,ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                            (
                             uiInternetServiceProvider.getIspIdSet().
                             toArray(new Integer[uiInternetServiceProvider.getIspIdSet().size()]))
                            );
                    pstmt.setString(2,uiInternetServiceProvider.getIspUiName());
                    pstmt.setInt(3,uiInternetServiceProvider.getCountryUiId());

                    rowsUpdated = pstmt.executeUpdate();

                    if(rowsUpdated <= 0 )
                        throw new SQLException("Inside GeoCommonUtils, SQLException in updating " +
                                               "UIInternetServiceProvider entry , for isp_ui_name " +
                                               uiInternetServiceProvider.getIspUiName() + " , entry is missing " +
                                               "inside ui_targeting_isp table, so no new id from isp table can be" +
                                               "added/deleted");

                }
                catch (SQLException e)
                {
                    throw new SQLException("Inside GeoCommonUtils, SQLException in updating " +
                                           "UIInternetServiceProvider entry ",e);
                }
                finally
                {
                    try
                    {
                        if (null != rs)
                            rs.close();
                        if (null != pstmt)
                            pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        throw new SQLException(
                                "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
                    }
                }
            }

            if(
               batchSizeCounter == sqlBatchSize ||
               totalEntities == ispDataSetToPopulate.size()
              )
            {
                logger.debug("Going to insert new entries in ui_targeting_isp table...");

                int rowsInserted = 0;

                //use temp table to get all entries to be inserted.
                Iterator<UIInternetServiceProvider> it = tempIspUIDataAgainstISPUiNameAndCountryUiId.values().iterator();
                while(it.hasNext())
                {
                    UIInternetServiceProvider entry = it.next();
                    sqlDataBuffer.append(entry.prepareNewUIISPRowForInsertionIntoSQL());
                }

                if(sqlDataBuffer.length() > 0)
                {
                    sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                    StringBuffer finalUIISPDataInsertionQuery = new StringBuffer(
                                                                    UIInternetServiceProvider.getINSERTION_QUERY());

                    finalUIISPDataInsertionQuery.append(sqlDataBuffer.toString());

                    Statement stmt = null;

                    try
                    {
                        stmt = connectionToDatabase.createStatement();
                        rowsInserted = stmt.executeUpdate(finalUIISPDataInsertionQuery.toString());
                    }
                    catch (SQLException sqle)
                    {
                        throw new SQLException("Rows insertion of uiInternetServiceProvider data could not happen." +
                                               "Aborting!!!",sqle);
                    }
                    finally
                    {
                        if(null != stmt)
                            stmt.close();
                    }

                    if(rowsInserted <= 0)
                        throw new SQLException("Rows insertion of uiInternetServiceProvider data could not " +
                                               "happen.Aborting!!!");

                    batchSizeCounter = 0;
                    tempIspUIDataAgainstISPUiNameAndCountryUiId = new HashMap<String, UIInternetServiceProvider>();
                    sqlDataBuffer = new StringBuffer();
                    //fetch all data from ui_targeting_isp inserted till now.
                    ispUIDataAgainstISPUiNameAndCountryUiId = fetchUiTargetingIspData(connectionToDatabase);
                }
            }
        }
    }

    /**
     * This function feeds a set of state entity into ui_targeting_state table
     * for targeting exposure on user interface and for targeting matching purposes.
     * @param stateDataMap
     * @param connectionToDatabase
     * @param sqlBatchSize
     * @throws SQLException
     */
    public static void feedStateDataForTargetingExposureOnUserInterface
                                                       (
                                                        Map<String,State> stateDataMap,
                                                        Connection connectionToDatabase,
                                                        CountryUserInterfaceIdCache countryUserInterfaceIdCache,
                                                        int sqlBatchSize
                                                       ) throws SQLException,UnSupportedOperationException
    {
        Map<String,UIState> stateDataFromUIDatabase = fetchUiStateData(connectionToDatabase);
        Map<String,UIState> uiStateNewDataForUIDatabaseStorage = new HashMap<String, UIState>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //now use input data set to insert new rows or update existing ones.
        Iterator<State> inputStateDataSetIterator = stateDataMap.values().iterator();

        StringBuffer sqlDataBuffer = new StringBuffer();
        int batchSizeCounter = 0;
        int totalEntities = 0;

        while(inputStateDataSetIterator.hasNext())
        {
            totalEntities ++;

            State state = inputStateDataSetIterator.next();
            Set<Integer> uiCountryIdSet = countryUserInterfaceIdCache.query
                                                (new CountryUserInterfaceIdSecondaryIndex(state.getCountryId()));

            logger.debug("Country Id used is: {} ", state.getCountryId());

            CountryUserInterfaceId countryUserInterfaceIdEntity = null;

            logger.debug("ui country id set is not null: {} ", null != uiCountryIdSet);
            logger.debug("ui country id set is: {} ", uiCountryIdSet);

            if(null != uiCountryIdSet)
            {
                for (Integer uiCountryId : uiCountryIdSet)
                {
                    countryUserInterfaceIdEntity = countryUserInterfaceIdCache.query(uiCountryId);
                    break;
                }
            }

            if(null == countryUserInterfaceIdEntity)
            {
                throw new SQLException("Country user interface id entity could not be found for country id: " +
                                        state.getCountryId());
            }

            String key = UIState.generateKeyForUniquenessCheck
                                            (countryUserInterfaceIdEntity.getId(),state.getStateName());

            UIState uiState = stateDataFromUIDatabase.get(key);

            if(null == uiState)
            {
                Set<Integer> stateIdSet = new HashSet<Integer>();
                stateIdSet.add(state.getStateId());

                uiState = new UIState(countryUserInterfaceIdEntity.getId(),state.getStateName(),stateIdSet);
                uiStateNewDataForUIDatabaseStorage.put(key,uiState);

                //update batch size counter only if uiCountry not present in database.
                batchSizeCounter ++;
            }
            else if(!uiState.doesIdExistInStateIdSet(state.getStateId()))
            {
                uiState.addToStateIdSet(state.getStateId());
                //since uiState is updated with new id, update into database too.
                try
                {

                    int rowsUpdated = 0;

                    pstmt = connectionToDatabase.prepareStatement(UIState.getUPDATION_QUERY());
                    pstmt.setString(1,ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                            (
                                    uiState.getStateIdSet().
                                            toArray(new Integer[uiState.getStateIdSet().size()]))
                    );

                    pstmt.setInt(2,countryUserInterfaceIdEntity.getId());
                    pstmt.setString(3,state.getStateName());

                    rowsUpdated = pstmt.executeUpdate();

                    if(rowsUpdated <= 0)
                        throw new SQLException("UIState entry not present in database for statename: " +
                                               state.getStateName() + " while adding into entityidset");
                }
                catch (SQLException e)
                {
                    throw new SQLException("Inside GeoCommonUtils, SQLException in updating UIState entry ",e);
                }
                finally
                {
                    try
                    {
                        if (null != rs)
                            rs.close();
                        if (null != pstmt)
                            pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        throw new SQLException(
                                "Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
                    }
                }
            }

            if(
                batchSizeCounter == sqlBatchSize       ||
                totalEntities    == stateDataMap.size()
              )
            {
                int rowsInserted = 0;

                Iterator<UIState> it = uiStateNewDataForUIDatabaseStorage.values().iterator();
                while(it.hasNext())
                {
                    UIState entry = it.next();
                    sqlDataBuffer.append(entry.prepareNewUIStateRowForInsertionIntoSQL());
                }

                if(sqlDataBuffer.length() > 0)
                {
                    sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                    StringBuffer finalUIStateDataInsertionQuery = new StringBuffer(UIState.getINSERTION_QUERY());

                    finalUIStateDataInsertionQuery.append(sqlDataBuffer.toString());

                    Statement stmt = null;

                    try
                    {
                        stmt = connectionToDatabase.createStatement();
                        rowsInserted = stmt.executeUpdate(finalUIStateDataInsertionQuery.toString());
                    }
                    catch (SQLException sqle)
                    {
                        throw new SQLException("Rows insertion of uiState data could not happen,Aborting!!!",sqle);
                    }
                    finally
                    {
                        if(null != stmt)
                            stmt.close();
                    }

                    if(rowsInserted <= 0)
                        throw new SQLException("Rows insertion of uiState data could not happen.Aborting!!!");

                    batchSizeCounter = 0;
                    sqlDataBuffer = new StringBuffer();
                    uiStateNewDataForUIDatabaseStorage = new HashMap<String, UIState>();
                    stateDataFromUIDatabase = fetchUiStateData(connectionToDatabase);
                }
            }
        }
    }

    /**
     * This function feeds a set of state entity into ui_targeting_state table
     * for targeting exposure on user interface and for targeting matching purposes.
     * @param cityDataMap
     * @param connectionToDatabase
     * @param sqlBatchSize
     * @throws SQLException
     */
    public static void feedCityDataForTargetingExposureOnUserInterface
                                                        (
                                                                Map<String,City> cityDataMap,
                                                                Connection connectionToDatabase,
                                                                StateUserInterfaceIdCache stateUserInterfaceIdCache,
                                                                int sqlBatchSize
                                                        ) throws SQLException,UnSupportedOperationException
    {
        Map<String,UICity> cityDataFromUIDatabase = fetchUiCityData(connectionToDatabase);
        Map<String,UICity> uiCityNewDataForUIDatabaseStorage = new HashMap<String, UICity>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //now use input data set to insert new rows or update existing ones.
        Iterator<City> inputCityDataSetIterator = cityDataMap.values().iterator();

        StringBuffer sqlDataBuffer = new StringBuffer();
        int batchSizeCounter = 0;
        int totalEntities = 0;

        while(inputCityDataSetIterator.hasNext())
        {
            totalEntities ++;

            City city = inputCityDataSetIterator.next();
            Set<Integer> uiStateIdSet = stateUserInterfaceIdCache.query
                                                        (new StateUserInterfaceIdSecondaryIndex(city.getStateId()));

            StateUserInterfaceId stateUserInterfaceIdEntity = null;
            if(null != uiStateIdSet)
            {
                for (Integer uiStateId : uiStateIdSet)
                {
                    stateUserInterfaceIdEntity = stateUserInterfaceIdCache.query(uiStateId);
                    break;
                }
            }

            if(null == stateUserInterfaceIdEntity)
            {
                throw new SQLException("State user interface id entity could not be found for state id: " +
                                        city.getStateId());
            }

            String key = UICity.generateKeyForUniquenessCheck
                                            (stateUserInterfaceIdEntity.getId(),city.getCityName());

            UICity uiCity = cityDataFromUIDatabase.get(key);

            if(null == uiCity)
            {
                Set<Integer> cityIdSet = new HashSet<Integer>();
                cityIdSet.add(city.getCityId());

                uiCity = new UICity(stateUserInterfaceIdEntity.getId(),city.getCityName(),cityIdSet);
                uiCityNewDataForUIDatabaseStorage.put(key,uiCity);

                //update batch size counter only if uiCity not present in database.
                batchSizeCounter ++;
            }
            else if(!uiCity.doesIdExistInCityIdSet(city.getCityId()))
            {
                uiCity.addToCityIdSet(city.getCityId());

                //since uiCity is updated with new id, update into database too.
                try
                {

                    int rowsUpdated = 0;

                    pstmt = connectionToDatabase.prepareStatement(UICity.getUPDATION_QUERY());
                    pstmt.setString(1,ResultSetHelper.prepareIntegerArrayForMySQLInsertion
                                      (
                                        uiCity.getCityIdSet().
                                            toArray(new Integer[uiCity.getCityIdSet().size()])
                                      )
                                   );

                    pstmt.setInt(2,stateUserInterfaceIdEntity.getId());
                    pstmt.setString(3,city.getCityName());

                    rowsUpdated = pstmt.executeUpdate();

                    if(rowsUpdated <= 0)
                        throw new SQLException("UICity entry not present in database for cityname: " +
                                                city.getCityName() + " while adding into entityidset");
                }
                catch (SQLException e)
                {
                    throw new SQLException("Inside GeoCommonUtils, SQLException in updating UICity entry ",e);
                }
                finally
                {
                    try
                    {
                        if (null != rs)
                            rs.close();
                        if (null != pstmt)
                            pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        throw new SQLException
                                ("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
                    }
                }
            }

            if(
                batchSizeCounter == sqlBatchSize       ||
                totalEntities    == cityDataMap.size()
              )
            {
                int rowsInserted = 0;

                Iterator<UICity> it = uiCityNewDataForUIDatabaseStorage.values().iterator();
                while(it.hasNext())
                {
                    UICity entry = it.next();
                    sqlDataBuffer.append(entry.prepareNewUICityRowForInsertionIntoSQL());
                }

                if(sqlDataBuffer.length() > 0)
                {
                    sqlDataBuffer.deleteCharAt(sqlDataBuffer.length()-1);
                    StringBuffer finalUICityDataInsertionQuery = new StringBuffer(UICity.getINSERTION_QUERY());

                    finalUICityDataInsertionQuery.append(sqlDataBuffer.toString());

                    Statement stmt = null;

                    try
                    {
                        stmt = connectionToDatabase.createStatement();
                        rowsInserted = stmt.executeUpdate(finalUICityDataInsertionQuery.toString());
                    }
                    catch (SQLException sqle)
                    {
                        throw new SQLException("Rows insertion of uiCity data could not happen,Aborting!!!",sqle);
                    }
                    finally
                    {
                        if(null != stmt)
                            stmt.close();
                    }

                    if(rowsInserted <= 0)
                        throw new SQLException("Rows insertion of uiCity data could not happen.Aborting!!!");

                    batchSizeCounter = 0;
                    sqlDataBuffer = new StringBuffer();
                    uiCityNewDataForUIDatabaseStorage = new HashMap<String, UICity>();
                    cityDataFromUIDatabase = fetchUiCityData(connectionToDatabase);
                }
            }
        }
    }

    private static Map<String,UIInternetServiceProvider> fetchUiTargetingIspData(Connection connectionToDatabase)
                                                                                                throws SQLException
    {
        Map<String,UIInternetServiceProvider>  ispUIDataAgainstISPUiNameAndCountryUiId =
                                                                new HashMap<String, UIInternetServiceProvider>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_ISP_UI_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                Integer countryUiId = rs.getInt("country_ui_id");
                String ispUiName = rs.getString("isp_ui_name");
                Integer[] entityIdSet = ResultSetHelper.getResultSetIntegerArray(rs, "entity_id_set");
                Set<Integer> ispIdSetForAllDataSources = ( null != entityIdSet ?
                        new HashSet<Integer>(Arrays.asList(entityIdSet)) :
                        new HashSet<Integer>());

                UIInternetServiceProvider uiInternetServiceProvider = new
                        UIInternetServiceProvider(
                        countryUiId,
                        ispUiName,
                        ispIdSetForAllDataSources
                );

                ispUIDataAgainstISPUiNameAndCountryUiId.put(uiInternetServiceProvider.prepareKeyToLookupISPUIEntry(),
                        uiInternetServiceProvider);
            }
        }
        catch (SQLException e)
        {
            throw new SQLException("Inside GeoCommonUtils, SQLException in getting " +
                    "UIIntenetServiceProvider database ",e);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return ispUIDataAgainstISPUiNameAndCountryUiId;
    }

    /**
     * This function moves a source file to destination file atomically.
     * @param sourceFile
     * @param destinationFile
     * @throws IOException
     */
    public static void moveFileAtomicallyToDestination(String sourceFile,String destinationFile) throws IOException
    {
        File source = new File(sourceFile);
        File destination = new File(destinationFile);
        Files.move(source, destination);
    }

    public static Map<String,MCCMNCISPCountryEntity>
                                    fetchMCCMNCDataFromSQLDatabase(Connection connectionToDatabase) throws Exception
    {
        Map<String,MCCMNCISPCountryEntity> dataMap = new HashMap<String, MCCMNCISPCountryEntity>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = connectionToDatabase.prepareStatement(QUERY_MCC_MNC_DATA_LOAD);
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                String mcc = rs.getString("mcc");
                String mnc = rs.getString("mnc");
                String countryCode = rs.getString("country_code");
                String countryName = rs.getString("country_name");
                String networkName = rs.getString("network_name");

                MCCMNCISPCountryEntity mccmncispCountryEntity =
                        new MCCMNCISPCountryEntity(mcc,mnc,countryCode,countryName,networkName);

                dataMap.put(MCCMNCISPCountryEntity.prepareKey(mcc,mnc),mccmncispCountryEntity);
            }
        }
        catch (SQLException sqle)
        {
            throw new Exception("SQLException inside fetchMCCMNCDataFromSQLDatabase in loading mcc mnc data ",sqle);
        }
        finally
        {
            try
            {
                if (null != rs)
                    rs.close();
                if (null != pstmt)
                    pstmt.close();
            }
            catch (SQLException e)
            {
                throw new SQLException("Inside GeoCommonUtils, SQLException in closing connection meta resources.",e);
            }
        }

        return dataMap;
    }

    public static final double haversineDistanceInMiles(double latitude1,double longitude1,
                                                      double latitude2,double longitude2)
    {
        Double latDistance = toRad(latitude2-latitude1);
        Double lonDistance = toRad(longitude2-longitude1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(toRad(latitude1)) * Math.cos(toRad(latitude2)) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_MILES * c;
    }

    private static double toRad(double value)
    {
        return value * Math.PI / 180;
    }
}
