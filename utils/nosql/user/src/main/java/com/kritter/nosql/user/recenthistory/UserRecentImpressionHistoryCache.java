package com.kritter.nosql.user.recenthistory;

import com.kritter.entity.user.recenthistory.RecentHistoryProvider;
import com.kritter.user.thrift.struct.ImpressionEvent;
import com.kritter.user.thrift.struct.RecentImpressionHistory;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlNamespaceTable;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.abstraction.cache.interfaces.ICache;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class implements methods to operate on aerospike store
 * to manage user related operations.
 * Table name -> user_recent_imp_history
 * key -> kritter-user-id
 * value = RecentImpressionHistory thrift struct.
 */
public class UserRecentImpressionHistoryCache implements NoSqlNamespaceTable, ICache, RecentHistoryProvider
{
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_IMPRESSION_HISTORY_KEY = "attribute_name_impression_history";

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
    private final String attributeNameImpressionHistory;
    private Set<String> attributeNameSet;

    private int maxSizeImpressionList;
    @Getter
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;
    private final ExecutorService updaterService;

    public UserRecentImpressionHistoryCache(
                                            String name,
                                            String loggerName,
                                            int maxSizeImpressionList,
                                            int threadCount,
                                            NoSqlNamespaceOperations noSqlNamespaceOperations,
                                            Properties properties
                                           )
    {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.maxSizeImpressionList = maxSizeImpressionList;

        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperations;

        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.attributeNameImpressionHistory = properties.getProperty(ATTRIBUTE_NAME_IMPRESSION_HISTORY_KEY);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(attributeNameImpressionHistory);

        this.updaterService = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Fetches the recent impression history for given user Id.
     * @param kritterUserId
     */
    @Override
    public RecentImpressionHistory fetchImpressionHistoryForUser(String kritterUserId) throws Exception {
        //Fetch information for this user.
        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,kritterUserId);

        Map<String,NoSqlData> dataMap =
                noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes (
                        namespaceName, tableName,
                                     primaryKeyValue,
                                     this.attributeNameSet
                                     );

        RecentImpressionHistory recentImpressionHistory = null;
        if(dataMap != null && dataMap.size() != 0) {
            NoSqlData recentImpHistoryNoSqlObject = dataMap.get(this.attributeNameImpressionHistory);

            if(null == recentImpHistoryNoSqlObject) {
                throw new Exception("RecentImpressionHistory is missing for user id : " +
                                    kritterUserId + " , however the user id exists in no-sql store table: " +
                        tableName + " , some fault in updation/insertion of no-sql store...");
            }

            byte[] serializedObject = (byte[])recentImpHistoryNoSqlObject.getValue();
            if(serializedObject == null || serializedObject.length == 0) {
                this.logger.error("Null or empty recent history bytes found in recent impression history cache for " +
                        "user id {}.", kritterUserId);
                return null;
            }
            try {
                recentImpressionHistory = fetchRecentImpressionHistoryObject(serializedObject);
            } catch (TException te) {
                this.logger.error("Exception caught while decoding recent impression for user: {}. Byte array size : " +
                        "{}, Exception : {}", kritterUserId, serializedObject.length, te);
                Base64 base64Encoder = new Base64(0);
                this.logger.error("Size of serialized object = {}, serialized object = {}.", serializedObject.length,
                        new String(base64Encoder.encode(serializedObject)));
                recentImpressionHistory = null;
            }
        }

        return recentImpressionHistory;
    }

    /**
     * Updates recent impression history for given user.
     * @param kritterUserId
     * @param eventSet
     */
    @Override
    public void updateImpressionHistoryForUser(
                                                String kritterUserId,
                                                SortedSet<ImpressionEvent> eventSet
                                                ) throws Exception
    {
        RecentImpressionHistory recentImpressionHistory = fetchImpressionHistoryForUser(kritterUserId);

        if(null == recentImpressionHistory) {
            logger.debug("Empty event set for user id : {}", kritterUserId);

            List<ImpressionEvent> list = new ArrayList<ImpressionEvent>();

            int head = updateImpressionEventListAndReturnHead(list, eventSet, -1, this.maxSizeImpressionList);

            recentImpressionHistory = new RecentImpressionHistory();
            recentImpressionHistory.setCircularList(list);
            recentImpressionHistory.setPosition(head);
        }
        else {
            List<ImpressionEvent> list = recentImpressionHistory.getCircularList();
            int head = recentImpressionHistory.getPosition();
            logger.debug("Found existing events for user id : {}, current head of list : {}, list size : {}",
                    kritterUserId, head, list.size());

            head = updateImpressionEventListAndReturnHead(list, eventSet, head, this.maxSizeImpressionList);
            logger.debug("After updating events, new head : {}, new list size : {}", head, list.size());

            recentImpressionHistory.setCircularList(list);
            recentImpressionHistory.setPosition(head);
        }

        //now update no-sql store with modified RecentImpressionHistory object.

        byte[] dataToStore = fetchRecentImpressionHistoryByteArray(recentImpressionHistory);
        logger.debug("Size of data to store : {}", dataToStore.length);

        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, kritterUserId);
        NoSqlData recentImpressionHistoryNoSqlData = new NoSqlData(NoSqlData.NoSqlDataType.BINARY, dataToStore);

        Map<String, NoSqlData> dataMap = new HashMap<String, NoSqlData>();
        dataMap.put(attributeNameImpressionHistory,recentImpressionHistoryNoSqlData);

        logger.debug("Updated recent history in the store");
        noSqlNamespaceOperationsInstance.updateAttributesInThisNamespace(
                namespaceName, tableName, primaryKeyValue, dataMap
        );
    }

    /**
     * Function that uses the executor service to update recent history
     * @param kritterUserId
     * @param eventSet
     */
    @Override
    public void updateRecentHistory(String kritterUserId, SortedSet<ImpressionEvent> eventSet) {
        RecentHistoryUpdater updater = new RecentHistoryUpdater(this.logger, this, kritterUserId, eventSet);
        logger.debug("Updating user history for {}", kritterUserId);
        updaterService.submit(updater);
    }

    public static class RecentHistoryUpdater implements Runnable {
        private final Logger logger;
        private final UserRecentImpressionHistoryCache userRecentImpressionHistoryCache;
        private final String kritterUserId;
        private SortedSet<ImpressionEvent> eventSet;

        public RecentHistoryUpdater(Logger logger, UserRecentImpressionHistoryCache userRecentImpressionHistoryCache,
                                    String kritterUserId, SortedSet<ImpressionEvent> eventSet) {
            this.logger = logger;
            this.userRecentImpressionHistoryCache = userRecentImpressionHistoryCache;
            this.kritterUserId = kritterUserId;
            this.eventSet = eventSet;
        }

        @Override
        public void run() {
            try {
                if(logger.isDebugEnabled()) {
                    logger.debug("Updating user history for user id : {}", kritterUserId);
                    for(ImpressionEvent event : eventSet) {
                        logger.debug("\tad id : {}, impression time : {}", event.getAdId(), event.getTimestamp());
                    }
                }
                userRecentImpressionHistoryCache.updateImpressionHistoryForUser(this.kritterUserId, this.eventSet);
            } catch (Exception e) {
                logger.error("Exception occurred in recent history updater. {}", e);
            }
        }
    }

    public byte[] fetchRecentImpressionHistoryByteArray(RecentImpressionHistory recentImpressionHistory)
                                                                                                    throws TException
    {
        TSerializer thriftSerializer = new TSerializer(new TBinaryProtocol.Factory());
        return thriftSerializer.serialize(recentImpressionHistory);
    }

    public RecentImpressionHistory fetchRecentImpressionHistoryObject(byte[] byteData) throws TException
    {
        TDeserializer thriftDeserializer = new TDeserializer(new TBinaryProtocol.Factory());
        RecentImpressionHistory recentImpressionHistory = new RecentImpressionHistory();
        thriftDeserializer.deserialize(recentImpressionHistory,byteData);

        return recentImpressionHistory;
    }


    private static int updateImpressionEventListAndReturnHead(List<ImpressionEvent> list,
                                                              SortedSet<ImpressionEvent> eventSet, int head,
                                                              int maxSizeImpressionList
                                                      )
    {
        for(ImpressionEvent event : eventSet) {
            //if max size gets hit, then replace the first element of the list and change head.
            if( (list.size() + 1) > maxSizeImpressionList)
            {
                list.set(head, event);

                if(head == maxSizeImpressionList -1)
                    head = 0;
                else
                    head = head + 1;
            }
            //else the list is capable of accommodating the new event,where head would always be first element.
            else
            {
                list.add(event);
                head = 0;
            }
        }

        return head;
    }

    private static ImpressionEvent prepareImpressionEvent(int adId,long timestamp)
    {
        ImpressionEvent impressionEvent = new ImpressionEvent();
        impressionEvent.setAdId(adId);
        impressionEvent.setTimestamp(timestamp);
        return impressionEvent;
    }

    @Override
    public void destroy() {
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
