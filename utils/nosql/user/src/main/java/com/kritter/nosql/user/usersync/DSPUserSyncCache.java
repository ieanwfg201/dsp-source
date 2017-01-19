package com.kritter.nosql.user.usersync;

import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.entity.user.userid.DSPUserIdProvider;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class DSPUserSyncCache implements DSPUserIdProvider, ICache {
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";

    @Getter
    private String name;
    private Logger logger;
    private String namespaceName;
    private String tableName;
    private String primaryKeyname;
    @Getter
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;

    public DSPUserSyncCache(String name,
                            String loggerName,
                            Properties properties,
                            NoSqlNamespaceOperations noSqlNamespaceOperations) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyname = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperations;
    }

    @Override
    public void destroy() {
        if(this.noSqlNamespaceOperationsInstance != null)
            this.noSqlNamespaceOperationsInstance.destroy();
    }

    @Override
    public Map<Integer, String> getDSPUserIdForExchangeId(String exchangeUserId, Set<Integer> dspIds) {
        if(exchangeUserId == null || exchangeUserId.isEmpty()) {
            logger.debug("No exchange user id specified.");
            return null;
        }

        logger.debug("Exchange user id provided : {}.", exchangeUserId);

        if(dspIds == null || dspIds.isEmpty()) {
            logger.debug("No DSP's specified for which to get user id.");
            return null;
        }

        Set<String> dspIdSet = new HashSet<String>();
        logger.debug("List of dsp ids for which to fetch user ids :");
        for(int dspId : dspIds) {
            logger.debug("\t{}", dspId);
            dspIdSet.add(Integer.toString(dspId));
        }

        NoSqlData primaryKey = new NoSqlData(NoSqlData.NoSqlDataType.STRING, exchangeUserId);
        Map<String, NoSqlData> records = this.noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes(
                namespaceName, tableName, primaryKey, dspIdSet);

        if(records == null || records.isEmpty()) {
            logger.debug("No entries in the database found for exchange user id : {} for the given DSP's",
                    exchangeUserId);
            return null;
        }

        Map<Integer, String> dspUserIdMap = new HashMap<Integer, String>();
        for(Map.Entry<String, NoSqlData> record : records.entrySet()) {
            String key = record.getKey();
            NoSqlData value = record.getValue();
            int dspId = Integer.parseInt(key);
            String dspUserId = (String) value.getValue();
            logger.debug("For dsp id : {}, user id : {}", dspId, dspUserId);
            dspUserIdMap.put(dspId, dspUserId);
        }

        return dspUserIdMap;
    }

    @Override
    public void updateDSPUserIdForExchangeId(String exchangeUserId, Map<Integer, String> dspIdToUserIdMap) {
        if(exchangeUserId == null || exchangeUserId.isEmpty()) {
            logger.debug("No exchange user id provided for update.");
            return;
        }

        logger.debug("Exchange user id provided : {}.", exchangeUserId);

        if(dspIdToUserIdMap == null || dspIdToUserIdMap.isEmpty()) {
            logger.debug("No dsp ids provided for update.");
            return;
        }

        NoSqlData primaryKey = new NoSqlData(NoSqlData.NoSqlDataType.STRING, exchangeUserId);

        Map<String, NoSqlData> binNosqlDataMap = new HashMap<String, NoSqlData>();
        logger.debug("Map of dsp id to user ids to update :");
        for(Map.Entry<Integer, String> dspIdUserIdPair : dspIdToUserIdMap.entrySet()) {
            int dspId = dspIdUserIdPair.getKey();
            String dspUserId = dspIdUserIdPair.getValue();
            logger.debug("\t{}:{}", dspId, dspUserId);

            String dspIdString = Integer.toString(dspId);
            NoSqlData dspUserIdNoSqlData = new NoSqlData(NoSqlData.NoSqlDataType.STRING, dspUserId);
            binNosqlDataMap.put(dspIdString, dspUserIdNoSqlData);
        }

        // Update dsp user ids for exchange id in the database.
        logger.debug("Updating in the database.");
        this.noSqlNamespaceOperationsInstance.updateAttributesInThisNamespace(this.namespaceName, this.tableName,
                primaryKey, binNosqlDataMap);
    }
}
