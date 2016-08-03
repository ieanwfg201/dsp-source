package com.kritter.utils.common.url;

public enum URLFieldType
{
    STRING(1),
    INTEGER(2),
    FLOAT(3),
    SHORT(4),
    LONG(5);

    private int code;
    private URLFieldType(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }
}
