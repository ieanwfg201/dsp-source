package com.kritter.api_caller.core.util;

import lombok.Getter;

import java.sql.Types;

/**
 * This class represents a column value and its corresponding type
 * to be used while setting a parameter in prepared statement.
 */
public class DBTableColumnValueWithType {

    @Getter
    private Object value;
    @Getter
    private Integer sqlType;

    public DBTableColumnValueWithType(Object value,Integer sqlType){
        this.value = value;
        this.sqlType = sqlType;
    }
}
