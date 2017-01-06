package com.kritter.utils.dbextractionutil;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ResultSetHelper
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Integer[] getResultSetIntegerArray(ResultSet resultSet, String columnName) throws SQLException
    {
        String valueToRead = resultSet.getString(columnName);
        Integer[] valueToReturn = null;

        if(null == valueToRead)
            return valueToReturn;

        try
        {
            valueToReturn = objectMapper.readValue(valueToRead, new TypeReference<Integer[]>(){});
        }
        catch (IOException ioe)
        {
        }

        if(null != valueToReturn && valueToReturn.length == 0)
            return null;

        return valueToReturn;
    }

    public static Short[] getTier1Tier2CategoriesUnion(ResultSet resultSet,
                                                       String columnName,
                                                       Logger logger) throws SQLException
    {
        String valueToRead = resultSet.getString(columnName);
        Short[] valueToReturn = null;

        if(null == valueToRead || valueToRead.isEmpty())
            return valueToReturn;

        try
        {
            Map<String,Short[]> categoryMap = objectMapper.readValue(
                                                                     valueToRead,
                                                                     new TypeReference<HashMap<String,Short[]>>(){}
                                                                    );

            Iterator<Map.Entry<String,Short[]>> it = categoryMap.entrySet().iterator();
            ArrayList<Short> arrayList = new ArrayList<Short>();

            while(it.hasNext())
            {
                Map.Entry<String,Short[]> entry = it.next();
                Short[] values = entry.getValue();
                arrayList.addAll(Arrays.asList(values));
                valueToReturn = (0 == arrayList.size()) ? null : arrayList.toArray(new Short[arrayList.size()]);
            }
        }
        catch (Exception e)
        {
            logger.error("Error reading content categories map: {}", valueToRead);
	    logger.error("Exception: ",e);
        }

        if(null != valueToReturn && valueToReturn.length == 0)
            return null;

        return valueToReturn;
    }

    public static Short[] getResultSetShortArray(ResultSet resultSet, String columnName) throws SQLException
    {
        String valueToRead = resultSet.getString(columnName);
        Short[] valueToReturn = null;

        if(null == valueToRead)
            return valueToReturn;

        try
        {
            valueToReturn = objectMapper.readValue(valueToRead, new TypeReference<Short[]>(){});
        }
        catch (IOException ioe)
        {
        }

        if(null != valueToReturn && valueToReturn.length == 0)
            return null;

        return valueToReturn;
    }

    public static String[] getResultSetStringArray(ResultSet resultSet, String columnName) throws SQLException
    {
        String valueToRead = resultSet.getString(columnName);
        String[] valueToReturn = null;

        if(null == valueToRead)
            return valueToReturn;

        try
        {
            valueToReturn = objectMapper.readValue(valueToRead, new TypeReference<String[]>(){});
        }
        catch (IOException ioe)
        {
        }

        if(null != valueToReturn && valueToReturn.length == 0)
            return null;

        return valueToReturn;
    }

    public static String prepareIntegerArrayForMySQLInsertion(Integer... values)
    {
        String valueToReturn = null;

        try
        {
            valueToReturn = objectMapper.writeValueAsString(values);
        }
        catch (IOException ioe)
        {
        }

        return valueToReturn;
    }

    public static String prepareStringArrayForMySQLInsertion(String... values)
    {
        String valueToReturn = null;

        try
        {
            valueToReturn = objectMapper.writeValueAsString(values);
        }
        catch (IOException ioe)
        {
        }

        return valueToReturn;
    }
}
