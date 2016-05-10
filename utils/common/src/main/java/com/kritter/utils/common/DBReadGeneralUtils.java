package com.kritter.utils.common;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBReadGeneralUtils
{
    public static List<Integer> getIntegerArrayList(ResultSet resultSet, String columnName) throws SQLException
    {
        Array array = resultSet.getArray(columnName);
        if(array == null)
            return new ArrayList<Integer>();
        Integer[] intArr = (Integer[])resultSet.getArray(columnName).getArray();
        if(intArr == null)
            return new ArrayList<Integer>();
        return Arrays.asList(intArr);
    }
}
