package com.kritter.api_caller.core.util;

import lombok.Getter;

/**
 * This class represents a column value and its corresponding type
 * to be used while setting a parameter in prepared statement.
 */
public class DBTableColumnValueWithType {

    @Getter
    private Object value;

    /*Values to be taken from java.sql.Types enum class*/
    /*Example. java.sql.Types.VARCHAR,java.sql.Types.INTEGER
    * Use these enum values as it is to populate variable sqlType.*/
    @Getter
    private Integer sqlType;

    public DBTableColumnValueWithType(Object value,Integer sqlType)
    {
        this.value = value;
        this.sqlType = sqlType;
    }
}
