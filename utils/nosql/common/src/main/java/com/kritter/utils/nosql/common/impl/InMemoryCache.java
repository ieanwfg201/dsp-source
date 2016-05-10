package com.kritter.utils.nosql.common.impl;

import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryCache implements NoSqlNamespaceOperations {
    @Getter
    private Map<String, Map<String, Map<String, Map<NoSqlData, NoSqlData>>>> cache = null;

    public InMemoryCache() {
        this.cache = new HashMap<String, Map<String, Map<String, Map<NoSqlData, NoSqlData>>>>();
    }

    @Override
    public boolean insertAttributeToThisNamespace(String namespace, String tableName, NoSqlData primaryKeyValue, String attributeName, NoSqlData attributeValue) {
        if(cache == null) {
            cache = new HashMap<String, Map<String, Map<String, Map<NoSqlData, NoSqlData>>>>();
        }

        Map<String, Map<String, Map<NoSqlData, NoSqlData>>> namespaceCache = cache.get(namespace);
        if(namespaceCache == null) {
            namespaceCache = new HashMap<String, Map<String, Map<NoSqlData, NoSqlData>>>();
            cache.put(namespace, namespaceCache);
        }

        Map<String, Map<NoSqlData, NoSqlData>> tableCache = namespaceCache.get(tableName);
        if(tableCache == null) {
            tableCache = new HashMap<String, Map<NoSqlData, NoSqlData>>();
            namespaceCache.put(tableName, tableCache);
        }

        Map<NoSqlData, NoSqlData> attributeCache = tableCache.get(attributeName);
        if(attributeCache == null) {
            attributeCache = new HashMap<NoSqlData, NoSqlData>();
            tableCache.put(attributeName, attributeCache);
        }

        attributeCache.put(primaryKeyValue, attributeValue);
        return true;
    }

    @Override
    public boolean insertMultipleAttributesToThisNamespace(String namespace, String tableName, NoSqlData primaryKeyValue, Map<String, NoSqlData> attributesNameValueMap) {
        if(cache == null) {
            cache = new HashMap<String, Map<String, Map<String, Map<NoSqlData, NoSqlData>>>>();
        }

        Map<String, Map<String, Map<NoSqlData, NoSqlData>>> namespaceCache = cache.get(namespace);
        if(namespaceCache == null) {
            namespaceCache = new HashMap<String, Map<String, Map<NoSqlData, NoSqlData>>>();
            cache.put(namespace, namespaceCache);
        }

        Map<String, Map<NoSqlData, NoSqlData>> tableCache = namespaceCache.get(tableName);
        if(tableCache == null) {
            tableCache = new HashMap<String, Map<NoSqlData, NoSqlData>>();
            namespaceCache.put(tableName, tableCache);
        }

        for(Map.Entry<String, NoSqlData> entry : attributesNameValueMap.entrySet()) {
            String attributeName = entry.getKey();
            NoSqlData attributeValue = entry.getValue();
            Map<NoSqlData, NoSqlData> attributeCache = tableCache.get(attributeName);
            if(attributeCache == null) {
                attributeCache = new HashMap<NoSqlData, NoSqlData>();
                tableCache.put(attributeName, attributeCache);
            }
            attributeCache.put(primaryKeyValue, attributeValue);
        }

        return true;
    }

    @Override
    public boolean updateAttributesInThisNamespace(String namespace, String tableName, NoSqlData primaryKeyValue, Map<String, NoSqlData> attributesNameUpdatedValueMap) {
        if(cache == null) {
            cache = new HashMap<String, Map<String, Map<String, Map<NoSqlData, NoSqlData>>>>();
        }

        Map<String, Map<String, Map<NoSqlData, NoSqlData>>> namespaceCache = cache.get(namespace);
        if(namespaceCache == null) {
            namespaceCache = new HashMap<String, Map<String, Map<NoSqlData, NoSqlData>>>();
            cache.put(namespace, namespaceCache);
        }

        Map<String, Map<NoSqlData, NoSqlData>> tableCache = namespaceCache.get(tableName);
        if(tableCache == null) {
            tableCache = new HashMap<String, Map<NoSqlData, NoSqlData>>();
            namespaceCache.put(tableName, tableCache);
        }

        for(Map.Entry<String, NoSqlData> entry : attributesNameUpdatedValueMap.entrySet()) {
            String attributeName = entry.getKey();
            NoSqlData attributeValue = entry.getValue();
            Map<NoSqlData, NoSqlData> attributeCache = tableCache.get(attributeName);
            if(attributeCache == null) {
                attributeCache = new HashMap<NoSqlData, NoSqlData>();
                tableCache.put(attributeName, attributeCache);
            }
            attributeCache.put(primaryKeyValue, attributeValue);
        }

        return true;
    }

    @Override
    public boolean deleteRecordInThisNamespace(String namespace, String tableName, NoSqlData primaryKeyValue) {
        if(cache == null) {
            return true;
        }

        Map<String, Map<String, Map<NoSqlData, NoSqlData>>> namespaceCache = cache.get(namespace);
        if(namespaceCache == null) {
            return true;
        }

        Map<String, Map<NoSqlData, NoSqlData>> tableCache = namespaceCache.get(tableName);
        if(tableCache == null) {
            return true;
        }

        for(Map.Entry<String, Map<NoSqlData, NoSqlData>> entry : tableCache.entrySet()) {
            Map<NoSqlData, NoSqlData> attributeCache = entry.getValue();
            if(attributeCache.containsKey(primaryKeyValue)) {
                attributeCache.remove(primaryKeyValue);
            }
        }

        return true;
    }

    @Override
    public Map<String, NoSqlData> fetchSingleRecordAttributes(String namespace, String tableName, NoSqlData primaryKeyValue, Set<String> attributes) {
        if(cache == null) {
            return null;
        }

        Map<String, Map<String, Map<NoSqlData, NoSqlData>>> namespaceCache = cache.get(namespace);
        if(namespaceCache == null) {
            return null;
        }

        Map<String, Map<NoSqlData, NoSqlData>> tableCache = namespaceCache.get(tableName);
        if(tableCache == null) {
            return null;
        }

        Map<String, NoSqlData> resMap = null;
        for(String attribute : attributes) {
            if(tableCache.containsKey(attribute)) {
                Map<NoSqlData, NoSqlData> attributeCache = tableCache.get(attribute);
                if(attributeCache.containsKey(primaryKeyValue)) {
                    if(resMap == null)
                        resMap = new HashMap<String, NoSqlData>();
                    resMap.put(attribute, attributeCache.get(primaryKeyValue));
                }
            }
        }

        return resMap;
    }

    @Override
    public Map<NoSqlData, Map<String, NoSqlData>> fetchMultipleRecordsAttributes(String namespace, String tableName, Set<NoSqlData> primaryKeyValues, Set<String> attributes) {
        if(cache == null) {
            return null;
        }

        Map<String, Map<String, Map<NoSqlData, NoSqlData>>> namespaceCache = cache.get(namespace);
        if(namespaceCache == null) {
            return null;
        }

        Map<String, Map<NoSqlData, NoSqlData>> tableCache = namespaceCache.get(tableName);
        if(tableCache == null) {
            return null;
        }

        Map<NoSqlData, Map<String, NoSqlData>> resMap = new HashMap<NoSqlData, Map<String, NoSqlData>>();
        for(String attribute : attributes) {
            if (tableCache.containsKey(attribute)) {
                Map<NoSqlData, NoSqlData> attributeCache = tableCache.get(attribute);
                for(NoSqlData primaryKeyValue : primaryKeyValues) {
                    if(attributeCache.containsKey(primaryKeyValue)) {
                        Map<String, NoSqlData> userInfoMap = resMap.get(primaryKeyValue);
                        if(userInfoMap == null) {
                            userInfoMap = new HashMap<String, NoSqlData>();
                            resMap.put(primaryKeyValue, userInfoMap);
                        }
                        userInfoMap.put(attribute, attributeCache.get(primaryKeyValue));
                    }
                }
            }
        }

        return resMap;
    }
}
