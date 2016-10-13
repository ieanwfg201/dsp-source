package com.kritter.nosql.user.recenthistory;

import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.entity.user.recenthistory.RecentClickHistoryProvider;
import com.kritter.user.thrift.struct.ClickEvent;
import com.kritter.user.thrift.struct.RecentClickHistory;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlNamespaceTable;
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
import java.util.concurrent.TimeUnit;

public class UserRecentClickHistoryCache implements NoSqlNamespaceTable, ICache, RecentClickHistoryProvider {
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_CLICK_HISTORY_KEY = "attribute_name_click_history";

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
    private final String clickHistoryAttributeName;
    private Set<String> attributeNameSet;

    private int maxSizeClickList;
    @Getter
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;
    private final ExecutorService updaterService;

    public UserRecentClickHistoryCache(String name,
                                       String loggerName,
                                       int maxSizeClickList,
                                       int threadCount,
                                       NoSqlNamespaceOperations noSqlNamespaceOperations,
                                       Properties properties
                                       ) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.maxSizeClickList = maxSizeClickList;

        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperations;

        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.clickHistoryAttributeName = properties.getProperty(ATTRIBUTE_NAME_CLICK_HISTORY_KEY);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(clickHistoryAttributeName);

        this.updaterService = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Fetches the recent click history for given user Id.
     * @param kritterUserId
     */
    @Override
    public RecentClickHistory fetchClickHistoryForUser(String kritterUserId) throws Exception {
        //Fetch information for this user.
        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,kritterUserId);

        Map<String,NoSqlData> dataMap =
                noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes (
                        namespaceName, tableName,
                        primaryKeyValue,
                        this.attributeNameSet
                );

        RecentClickHistory recentClickHistory = null;
        if(dataMap != null && dataMap.size() != 0) {
            NoSqlData recentClickHistoryNoSqlObject = dataMap.get(this.clickHistoryAttributeName);

            if(null == recentClickHistoryNoSqlObject) {
                throw new Exception("RecentClickHistory is missing for user id : " +
                        kritterUserId + " , however the user id exists in no-sql store table: " +
                        tableName + " , some fault in updation/insertion of no-sql store...");
            }

            byte[] serializedObject = (byte[])recentClickHistoryNoSqlObject.getValue();
            if(serializedObject == null || serializedObject.length == 0) {
                this.logger.error("Null or empty recent history bytes found in recent click history cache for " +
                        "user id {}.", kritterUserId);
                return null;
            }
            try {
                recentClickHistory = fetchRecentClickHistoryObject(serializedObject);
            } catch (TException te) {
                this.logger.error("Exception caught while decoding recent click for user: {}. Byte array size : " +
                        "{}, Exception : {}", kritterUserId, serializedObject.length, te);
                recentClickHistory = null;
            }
        }

        return recentClickHistory;
    }

    /**
     * Updates recent click history for given user.
     * @param kritterUserId
     * @param eventSet
     */
    public void updateClickHistoryForUser(
            String kritterUserId,
            SortedSet<ClickEvent> eventSet
    ) throws Exception
    {
        RecentClickHistory recentClickHistory = fetchClickHistoryForUser(kritterUserId);

        if(null == recentClickHistory) {
            logger.debug("Empty event set for user id : {}", kritterUserId);

            List<ClickEvent> list = new ArrayList<ClickEvent>();

            int head = updateClickEventListAndReturnHead(list, eventSet, -1, this.maxSizeClickList);

            recentClickHistory = new RecentClickHistory();
            recentClickHistory.setClickEventCircularList(list);
            recentClickHistory.setClickEventListPosition(head);
        }
        else {
            List<ClickEvent> list = recentClickHistory.getClickEventCircularList();
            int head = recentClickHistory.getClickEventListPosition();
            logger.debug("Found existing events for user id : {}, current head of list : {}, list size : {}",
                    kritterUserId, head, list.size());

            head = updateClickEventListAndReturnHead(list, eventSet, head, this.maxSizeClickList);
            logger.debug("After updating events, new head : {}, new list size : {}", head, list.size());

            recentClickHistory.setClickEventCircularList(list);
            recentClickHistory.setClickEventListPosition(head);
        }

        //now update no-sql store with modified RecentClickHistory object.

        byte[] dataToStore = fetchRecentClickHistoryByteArray(recentClickHistory);
        logger.debug("Size of data to store : {}", dataToStore.length);

        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, kritterUserId);
        NoSqlData recentClickHistoryNoSqlData = new NoSqlData(NoSqlData.NoSqlDataType.BINARY, dataToStore);

        Map<String, NoSqlData> dataMap = new HashMap<String, NoSqlData>();
        dataMap.put(clickHistoryAttributeName,recentClickHistoryNoSqlData);

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
    public void updateClickHistory(String kritterUserId, SortedSet<ClickEvent> eventSet) {
        RecentHistoryUpdater updater = new RecentHistoryUpdater(this.logger, this, kritterUserId, eventSet);
        logger.debug("Updating user history for {}", kritterUserId);
        updaterService.submit(updater);
    }

    public static class RecentHistoryUpdater implements Runnable {
        private final Logger logger;
        private final UserRecentClickHistoryCache userRecentClickHistoryCache;
        private final String kritterUserId;
        private SortedSet<ClickEvent> eventSet;

        public RecentHistoryUpdater(Logger logger, UserRecentClickHistoryCache userRecentClickHistoryCache,
                                    String kritterUserId, SortedSet<ClickEvent> eventSet) {
            this.logger = logger;
            this.userRecentClickHistoryCache = userRecentClickHistoryCache;
            this.kritterUserId = kritterUserId;
            this.eventSet = eventSet;
        }

        @Override
        public void run() {
            try {
                if(logger.isDebugEnabled()) {
                    logger.debug("Updating user history for user id : {}", kritterUserId);
                    for(ClickEvent event : eventSet) {
                        logger.debug("\tad id : {}, click time : {}", event.getAdId(), event.getTimestamp());
                    }
                }
                userRecentClickHistoryCache.updateClickHistoryForUser(this.kritterUserId, this.eventSet);
            } catch (Exception e) {
                logger.error("Exception occurred in recent history updater. {}", e);
            }
        }
    }

    public byte[] fetchRecentClickHistoryByteArray(RecentClickHistory recentClickHistory)
            throws TException
    {
        TSerializer thriftSerializer = new TSerializer(new TBinaryProtocol.Factory());
        return thriftSerializer.serialize(recentClickHistory);
    }

    public RecentClickHistory fetchRecentClickHistoryObject(byte[] byteData) throws TException
    {
        TDeserializer thriftDeserializer = new TDeserializer(new TBinaryProtocol.Factory());
        RecentClickHistory recentClickHistory = new RecentClickHistory();
        thriftDeserializer.deserialize(recentClickHistory,byteData);

        return recentClickHistory;
    }


    private static int updateClickEventListAndReturnHead(List<ClickEvent> list,
                                                         SortedSet<ClickEvent> eventSet, int head,
                                                         int maxSizeClickList
    )
    {
        for(ClickEvent event : eventSet) {
            //if max size gets hit, then replace the first element of the list and change head.
            if( (list.size() + 1) > maxSizeClickList)
            {
                list.set(head, event);

                if(head == maxSizeClickList -1)
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

    private static ClickEvent prepareClickEvent(int adId, long timestamp)
    {
        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setAdId(adId);
        clickEvent.setTimestamp(timestamp);
        return clickEvent;
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
