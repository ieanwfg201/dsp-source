package com.kritter.utils.nosql.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * This class defines the data types available in java using which corresponding
 * no-sql data type can be defined and implemented in corresponding no-sql
 * database api implementation for usage by online applications.
 */
public class NoSqlData
{
    public enum NoSqlDataType
    {
        STRING(0),
        INTEGER(1),
        LONG(2),
        DOUBLE(3),
        FLOAT(4),
        BOOLEAN(5),
        /*Any complex object can be stored as binary data type*/
        BINARY(6),
        LIST_STRING(12),
        LIST_INTEGER(13),
        LIST_LONG(14),
        LIST_DOUBLE(15),
        LIST_FLOAT(16),
        /*generic type data type whenever data type is not clear.*/
        OBJECT(17);

        @Getter
        private int code;

        private NoSqlDataType(int code)
        {
            this.code = code;
        }
    }

    @Getter @Setter
    private Object value;
    @Getter
    private NoSqlDataType noSqlDataType;

    public NoSqlData(NoSqlDataType noSqlDataType,Object value)
    {
        this.noSqlDataType = noSqlDataType;
        this.value = value;
    }

    public NoSqlData(NoSqlDataType noSqlDataType)
    {
        this.noSqlDataType = noSqlDataType;
    }

    @Override
    public boolean equals(Object other) {
        NoSqlData data = (NoSqlData) other;

        if(noSqlDataType.getCode() != data.noSqlDataType.getCode())
            return false;
        if(noSqlDataType != NoSqlDataType.BINARY)
            return value.equals(data.value);

        return Arrays.equals((byte[]) value, (byte[]) data.value);
    }

    @Override
    public int hashCode() {
        if(noSqlDataType != NoSqlDataType.BINARY)
            return value.hashCode();
        return Arrays.hashCode((byte[]) value);
    }
}