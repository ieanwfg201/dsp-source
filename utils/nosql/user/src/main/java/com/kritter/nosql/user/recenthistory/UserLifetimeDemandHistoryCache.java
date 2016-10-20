package com.kritter.nosql.user.recenthistory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.entity.user.recenthistory.LifetimeDemandHistoryProvider;
import com.kritter.user.thrift.struct.LifetimeDemandHistory;
import com.kritter.utils.common.ThreadLocalUtils;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import lombok.Getter;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class UserLifetimeDemandHistoryCache implements LifetimeDemandHistoryProvider, ICache {
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_KEY = "attribute_name_lifetime_history";

    private Logger logger;
    @Getter
    private String name;

    @Getter
    private final String namespaceName;
    @Getter
    private final String tableName;
    @Getter
    private final String primaryKeyName;
    @Getter
    private final NoSqlData.NoSqlDataType primaryKeyDataType = NoSqlData.NoSqlDataType.STRING;
    private final String attributeNameHistory;
    private Set<String> attributeNameSet;

    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;
    private final ExecutorService updaterService;

    public UserLifetimeDemandHistoryCache(String name,
                                          String loggerName,
                                          int threadCount,
                                          NoSqlNamespaceOperations noSqlNamespaceOperations,
                                          Properties properties) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);

        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperations;

        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.attributeNameHistory = properties.getProperty(ATTRIBUTE_NAME_KEY);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(attributeNameHistory);

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(name + "-executor-%d").build();
        this.updaterService = Executors.newFixedThreadPool(threadCount, namedThreadFactory);
    }

    @Override
    public LifetimeDemandHistory fetchLifetimeDemandHistoryForUser(String kritterUserId) throws Exception {
        //Fetch information for this user.
        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,kritterUserId);

        Map<String,NoSqlData> dataMap =
                noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes (
                        namespaceName, tableName,
                        primaryKeyValue,
                        this.attributeNameSet
                );

        LifetimeDemandHistory lifetimeDemandHistory = null;
        if(dataMap != null && dataMap.size() != 0) {
            NoSqlData historyNoSqlData = dataMap.get(this.attributeNameHistory);

            if(null == historyNoSqlData) {
                throw new Exception("History is missing for user id : " +
                        kritterUserId + " , however the user id exists in no-sql store table: " +
                        tableName + " , some fault in updation/insertion of no-sql store...");
            }

            byte[] serializedObject = (byte[])historyNoSqlData.getValue();
            if(serializedObject == null || serializedObject.length == 0) {
                this.logger.error("Null or empty history bytes found in history cache for user id {}.", kritterUserId);
                return null;
            }
            try {
                lifetimeDemandHistory = fetchHistoryObject(serializedObject);
            } catch (TException te) {
                this.logger.error("Exception caught while decoding recent impression for user: {}. Byte array size : " +
                        "{}, Exception : {}", kritterUserId, serializedObject.length, te);
                lifetimeDemandHistory = null;
            }
        }

        return lifetimeDemandHistory;
    }

    public byte[] fetchHistoryByteArray(LifetimeDemandHistory lifetimeDemandHistory) throws TException {
        TSerializer thriftSerializer = new TSerializer(new TBinaryProtocol.Factory());
        return thriftSerializer.serialize(lifetimeDemandHistory);
    }

    public LifetimeDemandHistory fetchHistoryObject(byte[] byteData) throws TException {
        TDeserializer thriftDeserializer = new TDeserializer(new TBinaryProtocol.Factory());
        LifetimeDemandHistory lifetimeDemandHistory = new LifetimeDemandHistory();
        thriftDeserializer.deserialize(lifetimeDemandHistory, byteData);

        return lifetimeDemandHistory;
    }

    @Override
    public void updateLifetimeDemandHistoryForUser(String kritterUserId, Map<Integer, Integer> demandEventCount) throws
            Exception {
        LifetimeDemandHistory lifetimeDemandHistory = fetchLifetimeDemandHistoryForUser(kritterUserId);
        if(null == lifetimeDemandHistory) {
            logger.debug("Null event set for user id : {}", kritterUserId);
            lifetimeDemandHistory = new LifetimeDemandHistory();
            lifetimeDemandHistory.demandEventCount = new HashMap<Integer, Long>();
        } else {
            logger.debug("Found event entry in history cache for user id : {}", kritterUserId);
            if(lifetimeDemandHistory.demandEventCount == null) {
                logger.debug("History cache entry found for user id : {}, but ad event map in the event object is " +
                        "null", kritterUserId);
                lifetimeDemandHistory.demandEventCount = new HashMap<Integer, Long>();
            } else {
                logger.debug("Ad event map in the event object present. Number of entries : {}",
                        lifetimeDemandHistory.demandEventCount.size());
            }
        }

        for(Map.Entry<Integer, Integer> entry : demandEventCount.entrySet()) {
            int demandId = entry.getKey();
            int eventCount = entry.getValue();
            long currentCount = 0;
            if(lifetimeDemandHistory.demandEventCount.containsKey(demandId)) {
                currentCount = lifetimeDemandHistory.demandEventCount.get(demandId);
            }
            lifetimeDemandHistory.demandEventCount.put(demandId, currentCount + eventCount);
            logger.debug("Updating ad impression count map with ad id : {}, initial impression count and additional " +
                    "impression count : {}", demandId, currentCount, eventCount);
        }

        byte[] dataToStore = fetchHistoryByteArray(lifetimeDemandHistory);
        logger.debug("Size of data to store : {}", dataToStore.length);

        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, kritterUserId);
        NoSqlData historyNoSqlData = new NoSqlData(NoSqlData.NoSqlDataType.BINARY, dataToStore);

        Map<String, NoSqlData> dataMap = new HashMap<String, NoSqlData>();
        dataMap.put(attributeNameHistory, historyNoSqlData);

        logger.debug("Updated history in the store");
        noSqlNamespaceOperationsInstance.updateAttributesInThisNamespace(namespaceName, tableName, primaryKeyValue,
                dataMap);
    }

    @Override
    public void updateUserHistory(String kritterUserId, Map<Integer, Integer> demandEventCount) throws Exception {
        HistoryUpdater updater = new HistoryUpdater(logger, this, kritterUserId, demandEventCount);
        logger.debug("Updating user history for {}", kritterUserId);
        updaterService.submit(updater);
    }

    public static class HistoryUpdater implements Runnable {
        private final Logger logger;
        private final UserLifetimeDemandHistoryCache userLifetimeDemandHistoryCache;
        private final String kritterUserId;
        private Map<Integer, Integer> demandEventCount;

        public HistoryUpdater(Logger logger, UserLifetimeDemandHistoryCache userLifetimeDemandHistoryCache,
                                    String kritterUserId, Map<Integer, Integer> demandEventCount) {
            this.logger = logger;
            this.userLifetimeDemandHistoryCache = userLifetimeDemandHistoryCache;
            this.kritterUserId = kritterUserId;
            this.demandEventCount = demandEventCount;
        }

        @Override
        public void run() {
            try {
                if(logger.isDebugEnabled()) {
                    logger.debug("Updating user history for user id : {}", kritterUserId);
                    for(Map.Entry<Integer, Integer> entry : demandEventCount.entrySet()) {
                        logger.debug("\tdemand id : {}, count", entry.getKey(), entry.getValue());
                    }
                }
                userLifetimeDemandHistoryCache.updateLifetimeDemandHistoryForUser(kritterUserId, demandEventCount);
            } catch (Exception e) {
                logger.error("Exception occurred in recent history updater. {}", e);
            } finally {
                ThreadLocalUtils.cleanThreadLocalsOfCurrentThread(logger);
            }
        }
    }

    @Override
    public void destroy() {
        noSqlNamespaceOperationsInstance.destroy();
        updaterService.shutdown();
        try {
            if(!updaterService.awaitTermination(60, TimeUnit.SECONDS)) {
                updaterService.shutdownNow();
                if(!updaterService.awaitTermination(60, TimeUnit.SECONDS))
                    logger.error("Updater service did not terminate");
            }
        } catch (InterruptedException ie) {
            updaterService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
