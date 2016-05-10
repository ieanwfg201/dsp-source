package com.kritter.utils.nosql.common;

/**
 * This interface defines methods for any concrete no-sql namespace or database
 * with table for storage in no-sql database such as dynamo-db or aerospike etc.
 * This can be used to model any cache that operates on data stored in no-sql
 * store such as aerospike.
 */
public interface NoSqlNamespaceTable
{
    /*Returns the name of the database or namespace in the no-sql storage.*/
    public String getNamespaceName();

    /*Returns the name of the table in the no-sql database*/
    public String getTableName();

    /*This method returns the name of primary key for this namespace or table if supported in the no-sql database*/
    public String getPrimaryKeyName();

    /*This method returns the primary key data type for this namespace or table.*/
    public NoSqlData.NoSqlDataType getPrimaryKeyDataType();

    /*This method returns the underlying nosql implementation being used by the
    * namespace for all its operations by some client.*/
    public NoSqlNamespaceOperations getNoSqlNamespaceOperationsInstance();
}
