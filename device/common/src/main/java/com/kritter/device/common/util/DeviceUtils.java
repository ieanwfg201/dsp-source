package com.kritter.device.common.util;

import com.google.common.io.Files;
import com.kritter.constants.DeviceType;
import com.kritter.device.common.entity.*;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Device utils for managing utility methods.
 */
public class DeviceUtils
{
    public static final String FIFTYONE_DEGREES_DEVICE_TYPE_DESKTOP = "Desktop";
    public static final String FIFTYONE_DEGREES_DEVICE_TYPE_MOBILE  = "Mobile";
    private static final String WURFL_FILE_NAME_PREFIX = "wurfl";
    private static final String FIFTYONEDEGREES_FILE_NAME_PREFIX = "51degrees";
    private static final String UNDERSCORE = "_";
    private static final String DOT = "\\.";
    private static final String HANDSET_MASTER_DATA_FETCH_QUERY = "select * from handset_detection_data where " +
                                                                  "version = ?";
    private static final String HANDSET_MANUFACTURER_DATA_FETCH_QUERY = "select * from handset_manufacturer";
    private static final String HANDSET_BROWSER_DATA_FETCH_QUERY = "select * from handset_browser";
    private static final String HANDSET_OS_DATA_FETCH_QUERY = "select * from handset_os";
    private static final String HANDSET_MODEL_DATA_FETCH_QUERY = "select * from handset_model";

    public static String convertTextDataToDBInsertionCapable(String data)
    {
        data = data.replace("\"", "\\\"");
        data = data.replace("'", "\\'");
        return data;
    }

    public static int verifyWurflFileNameAndFetchVersion(String fileName) throws Exception
    {
        if(null == fileName)
            throw new Exception("Wurfl File name provided is null!!! FATAL!!!");

        if( ! fileName.toLowerCase().startsWith(WURFL_FILE_NAME_PREFIX) )
        {
            throw new Exception("The file name in wurfl data file folder is not correct ,name: " + fileName);
        }

        String fileParts[] = fileName.split(UNDERSCORE);
        if(fileParts.length != 2)
            throw new Exception("File name format is wrong, no version found, should be : wurfl_version.xml " +
                                "file name being: " + fileName);

        String versionParts[] = fileParts[1].split(DOT);

        int version = -1;

        try
        {
            version = Integer.parseInt(versionParts[0]);
        }
        catch (NumberFormatException nfe)
        {
            throw new Exception("Version specified is wrong,not an integer", nfe);
        }

        return version;
    }

    public static int verify51DegreesFileNameAndFetchVersion(String fileName) throws Exception {
        if(null == fileName)
            throw new Exception("51Degrees File name provided is null!!! FATAL!!!");

        if( ! fileName.toLowerCase().startsWith(FIFTYONEDEGREES_FILE_NAME_PREFIX) ) {
            throw new Exception("The file name in wurfl data file folder is not correct ,name: " + fileName);
        }

        String fileParts[] = fileName.split(UNDERSCORE);
        if(fileParts.length != 2)
            throw new Exception("File name format is wrong, no version found, should be : 51Degrees_version.dat " +
                    "file name being: " + fileName);

        String versionParts[] = fileParts[1].split(DOT);

        int version = -1;
        try {
            version = Integer.parseInt(versionParts[0]);
        } catch (NumberFormatException nfe) {
            throw new Exception("Version specified is wrong,not an integer", nfe);
        }

        return version;
    }

    public static Set<HandsetDataTopLevelKey> fetchHandsetMasterDataKeySetFromDatabaseForVersion
                                                                              (
                                                                               Connection connectionToDatabase,
                                                                               int dataFileVersion
                                                                              ) throws Exception
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<HandsetDataTopLevelKey> masterDataKeySet = new HashSet<HandsetDataTopLevelKey>();

        try
        {
            pstmt = connectionToDatabase.prepareStatement(HANDSET_MASTER_DATA_FETCH_QUERY);
            pstmt.setInt(1,dataFileVersion);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                String externalId = rs.getString("external_id");
                String source = rs.getString("source");

                HandsetDataTopLevelKey key = new HandsetDataTopLevelKey(
                        externalId, source, dataFileVersion);


                masterDataKeySet.add(key);
            }

        }
        catch (SQLException e)
        {
            throw new Exception("Inside HandsetDataFetcherFromDatabase SQLException in getting handsetmasterdata",e);

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
                throw new Exception("Inside HandsetDataFetcherFromDatabase SQLException in closing connection " +
                                    "meta resources.",e);
            }
        }

        return masterDataKeySet;
    }

    public static DeviceType getDeviceTypeFrom51DegreesDeviceType(String fiftyOneDegreesDeviceType) {
        if(fiftyOneDegreesDeviceType.toLowerCase().equals(FIFTYONE_DEGREES_DEVICE_TYPE_DESKTOP.toLowerCase())) {
            return DeviceType.DESKTOP;
        } else if(fiftyOneDegreesDeviceType.toLowerCase().equals(FIFTYONE_DEGREES_DEVICE_TYPE_MOBILE.toLowerCase())) {
            return DeviceType.MOBILE;
        }
        return null;
    }

    public static Map<String, HandsetManufacturerData> fetchManufacturerDataFromDatabase(
            Connection connectionToDatabase) throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;
        Map<String, HandsetManufacturerData> manufacturerDataMap = new Hashtable<String, HandsetManufacturerData>();

        try {
            stmt = connectionToDatabase.createStatement();
            rs = stmt.executeQuery(HANDSET_MANUFACTURER_DATA_FETCH_QUERY);

            while (rs.next())
            {
                Integer manufacturerId = (Integer) rs.getObject("manufacturer_id");
                String manufacturerName = rs.getString("manufacturer_name");
                String modifiedBy = rs.getString("modified_by");
                Timestamp modifiedOn = rs.getTimestamp("modified_on");

                HandsetManufacturerData handsetManufacturerData = new HandsetManufacturerData(
                        manufacturerId, manufacturerName, modifiedBy,
                        modifiedOn);
                manufacturerDataMap.put(handsetManufacturerData
                        .getManufacturerName().toLowerCase(),
                        handsetManufacturerData);
            }
        }
        catch (SQLException e)
        {
            throw new Exception(
                    "Inside HandsetDataFetcherFromDatabase SQLException in getting handsetmanufacturerdata",
                    e);

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
                throw new Exception(
                        "Inside HandsetDataFetcherFromDatabase SQLException in closing connection meta resources.",
                        e);
            }
        }
        return manufacturerDataMap;
    }

    public static Map<String, HandsetBrowserData> fetchBrowserDataFromDatabase(
            Connection connectionToDatabase) throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;
        Map<String, HandsetBrowserData> browserDataMap = new Hashtable<String, HandsetBrowserData>();

        try
        {
            stmt = connectionToDatabase.createStatement();
            rs = stmt.executeQuery(HANDSET_BROWSER_DATA_FETCH_QUERY);
            while (rs.next())
            {
                Integer browserId = (Integer) rs.getObject("browser_id");
                String browserName = rs.getString("browser_name");
                String[] browserVersions = ResultSetHelper.getResultSetStringArray(rs,"browser_versions");
                String modifiedBy = rs.getString("modified_by");
                Timestamp modifiedOn = rs.getTimestamp("modified_on");

                HandsetBrowserData handsetBrowserData =
                        new HandsetBrowserData(
                                               browserId,
                                               browserName,
                                               browserVersions,
                                               modifiedBy,
                                               modifiedOn
                                              );
                browserDataMap.put(
                                   handsetBrowserData.getBrowserName().toLowerCase(),
                                   handsetBrowserData
                                  );
            }
        }
        catch (SQLException e)
        {
            throw new Exception("Inside HandsetDataFetcherFromDatabase SQLException in getting handsetbrowerdata", e);

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
                throw new Exception(
                        "Inside HandsetDataFetcherFromDatabase SQLException in closing connection meta resources.",
                        e);
            }
        }

        return browserDataMap;
    }

    public static Map<String, HandsetOperatingSystemData> fetchOperatingSystemDataFromDatabase(
            Connection connectionToDatabase) throws Exception
    {

        Statement stmt = null;
        ResultSet rs = null;
        Map<String, HandsetOperatingSystemData> operatingSystemDataMap = new Hashtable<String, HandsetOperatingSystemData>();

        try
        {
            stmt = connectionToDatabase.createStatement();
            rs = stmt.executeQuery(HANDSET_OS_DATA_FETCH_QUERY);

            while (rs.next())
            {
                Integer operatingSystemId = (Integer) rs.getObject("os_id");
                String operatingSystemName = rs.getString("os_name");
                String[] osVersions = ResultSetHelper.getResultSetStringArray(rs,"os_versions");
                String modifiedBy = rs.getString("modified_by");
                Timestamp modifiedOn = rs.getTimestamp("modified_on");

                HandsetOperatingSystemData handsetOperatingSystemData =
                        new HandsetOperatingSystemData(
                                                       operatingSystemId, operatingSystemName,
                                                       osVersions,modifiedBy,modifiedOn
                                                      );

                operatingSystemDataMap.put(
                                           handsetOperatingSystemData.getOperatingSystemName().toLowerCase(),
                                           handsetOperatingSystemData
                                          );
            }
        }
        catch (SQLException e)
        {
            throw new Exception("Inside HandsetDataFetcherFromDatabase SQLException in getting handsetOsdata", e);

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
                throw new Exception(
                        "Inside HandsetDataFetcherFromDatabase SQLException in closing connection meta resources.",
                        e);
            }
        }

        return operatingSystemDataMap;
    }

    public static Map<String, HandsetModelData> fetchHandsetModelDataFromDatabase(
            Connection connectionToDatabase) throws Exception
    {

        Statement stmt = null;
        ResultSet rs = null;
        Map<String, HandsetModelData> handsetModelDataMap = new Hashtable<String, HandsetModelData>();

        try
        {
            stmt = connectionToDatabase.createStatement();
            rs = stmt.executeQuery(HANDSET_MODEL_DATA_FETCH_QUERY);

            while (rs.next())
            {
                Integer modelId = (Integer) rs.getObject("model_id");
                Integer manufacturerId = (Integer) rs
                        .getObject("manufacturer_id");
                String modelName = rs.getString("model_name");
                String modifiedBy = rs.getString("modified_by");
                Timestamp modifiedOn = rs.getTimestamp("modified_on");

                HandsetModelData handsetModelData = new HandsetModelData(
                        modelId, manufacturerId, modelName, modifiedBy,
                        modifiedOn);
                handsetModelDataMap.put(modelName.toLowerCase(),
                        handsetModelData);
            }
        }
        catch (SQLException e)
        {
            throw new Exception("Inside HandsetDataFetcherFromDatabase SQLException in getting handsetOsdata", e);

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
                throw new Exception(
                        "Inside HandsetDataFetcherFromDatabase SQLException in closing connection meta resources.",
                        e);
            }
        }

        return handsetModelDataMap;
    }
}