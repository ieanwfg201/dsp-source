package com.kritter.utils.nosql.common;

import java.util.Map;
import java.util.Set;

/**
 * This interface defines operations possible on a nosql storage.
 */
public interface NoSqlNamespaceOperations
{
    /* This method is used for insertion of a single attribute to a namespace and table.
     * Returns true if the insertion is successful*/
    public boolean insertAttributeToThisNamespace(String namespace,
                                                  String tableName,
                                                  NoSqlData primaryKeyValue,
                                                  String attributeName,
                                                  NoSqlData attributeValue);

    /* This method is used for asynchronous insertion of a single attribute to a namespace and table.
     * Returns true if the insertion is successful*/
    public boolean insertAttributeToThisNamespaceAsync(String namespace,
                                                  String tableName,
                                                  NoSqlData primaryKeyValue,
                                                  String attributeName,
                                                  NoSqlData attributeValue);

    /* This method is used for batch insertion of attributes to a namespace and table.
     * Returns true if the insertion is successful.*/
    public boolean insertMultipleAttributesToThisNamespace(String namespace,
                                                           String tableName,
                                                           NoSqlData primaryKeyValue,
                                                           Map<String,NoSqlData> attributesNameValueMap);

    /* This method is used for asynchronous batch insertion of attributes to a namespace and table.
     * Returns true if the insertion is successful.*/
    public boolean insertMultipleAttributesToThisNamespaceAsync(String namespace,
                                                           String tableName,
                                                           NoSqlData primaryKeyValue,
                                                           Map<String,NoSqlData> attributesNameValueMap);

    /* This method updates attributes for a given primary key in this namespace and table.*/
    public boolean updateAttributesInThisNamespace(String namespace,
                                                   String tableName,
                                                   NoSqlData primaryKeyValue,
                                                   Map<String,NoSqlData> attributesNameUpdatedValueMap);

    /* This method updates attributes for a given primary key in this namespace and table.*/
    public boolean updateAttributesInThisNamespaceAsync(String namespace,
                                                   String tableName,
                                                   NoSqlData primaryKeyValue,
                                                   Map<String,NoSqlData> attributesNameUpdatedValueMap);

    /* This method deletes a given record for a given primary key in this namespace and table.*/
    public boolean deleteRecordInThisNamespace(String namespace,
                                               String tableName,
                                               NoSqlData primaryKeyValue);

    /*This method fetches attributes for a given primary key in this namespace and table*/
    public Map<String,NoSqlData> fetchSingleRecordAttributes(String namespace,
                                                             String tableName,
                                                             NoSqlData primaryKeyValue,
                                                             Set<String> attributes);

    /*This method fetches attributes for a given primary key in this namespace and table*/
    public void fetchSingleRecordAttributesAsync(String namespace,
                                                 String tableName,
                                                 NoSqlData primaryKeyValue,
                                                 Set<String> attributes,
                                                 SignalingNotificationObject<Map<String,NoSqlData>> synchronizingResultMap);

    /*This method fetches attributes for a set of primary keys in this namespace and table*/
    public Map<NoSqlData,Map<String,NoSqlData>> fetchMultipleRecordsAttributes(String namespace,
                                                                               String tableName,
                                                                               Set<NoSqlData> primaryKeyValues,
                                                                               Set<String> attributes);

    /*This method fetches attributes for a set of primary keys in this namespace and table*/
    public void fetchMultipleRecordsAttributesAsync(String namespace,
                                                    String tableName,
                                                    Set<NoSqlData> primaryKeyValues,
                                                    Set<String> attributes,
                                                    SignalingNotificationObject<Map<NoSqlData, Map<String,NoSqlData>>> synchronizingResultMap);

    /**
     * Method to release all the resources held by the cache
     */
    public void destroy();
}
