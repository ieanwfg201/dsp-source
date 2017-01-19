package com.kritter.nosql.user.usersegment;

import com.kritter.entity.user.usersegment.UserSegmentProvider;
import com.kritter.user.thrift.struct.Segment;
import com.kritter.user.thrift.struct.UserSegment;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlNamespaceTable;
import lombok.Getter;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.kritter.abstraction.cache.interfaces.ICache;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class implements methods to operate on aerospike store
 * to manage user related operations.
 * Table name -> user_segment
 * key -> kritter-user-id
 * value = UserSegment thrift struct.
 */
public class UserSegmentCache implements NoSqlNamespaceTable, ICache, UserSegmentProvider {
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_USER_SEGMENT_KEY = "attribute_name_user_segment";

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

    @Getter
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;
    private TSerializer thriftSerializer;
    private TDeserializer thriftDeserializer;
    private final ExecutorService updaterService;

    public UserSegmentCache(
                                            String name,
                                            String loggerName,
                                            int threadCount,
                                            NoSqlNamespaceOperations noSqlNamespaceOperations,
                                            Properties properties
                                           ){
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperations;
        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.attributeNameImpressionHistory = properties.getProperty(ATTRIBUTE_NAME_USER_SEGMENT_KEY);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(attributeNameImpressionHistory);
        this.thriftSerializer = new TSerializer();
        this.thriftDeserializer = new TDeserializer();
        this.updaterService = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Fetches  User Segment for given user Id.
     * @param kritterUserId
     */
    @Override
    public UserSegment fetchUserSegment(String kritterUserId) throws Exception {
        //Fetch information for this user.
        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,kritterUserId);

        Map<String,NoSqlData> dataMap =
                noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes (
                        namespaceName, tableName,
                                     primaryKeyValue,
                                     this.attributeNameSet
                                     );

        UserSegment userSegment = null;
        if(dataMap != null && dataMap.size() != 0) {
            NoSqlData userSegmentNoSqlObject = dataMap.get(this.attributeNameImpressionHistory);

            if(null == userSegmentNoSqlObject) {
                throw new Exception("UserSegment is missing for user id : " +
                                    kritterUserId + " , however the user id exists in no-sql store table: " +
                        tableName + " , some fault in updation/insertion of no-sql store...");
            }

            byte[] serializedObject = (byte[])userSegmentNoSqlObject.getValue();
            userSegment = deserializeUserSegment(serializedObject);
        }

        return userSegment;
    }

    /**
     * Updates UserSegment for given user.
     * @param kritterUserId
     * @param eventSet
     */
    @Override
    public void updateRetargetingSegmentInUserSegment(
                                                String kritterUserId,
                                                int segmentId
                                                ) throws Exception {
        UserSegment userSegment = fetchUserSegment(kritterUserId);

        if(null == userSegment){
            userSegment = new UserSegment();
            Set<Segment> segmentSet = new HashSet<Segment>();
            Segment segment = new Segment();
            segment.setSegmentId(segmentId);
            segmentSet.add(segment);
            userSegment.setSegmentSet(segmentSet);
        } else {
            Set<Segment> segmentSet = userSegment.getSegmentSet();
            Segment segment = new Segment();
            segment.setSegmentId(segmentId);
            segmentSet.add(segment);
            userSegment.setSegmentSet(segmentSet);
        }

        //now update no-sql store with modified UserSegment object.

        byte[] dataToStore = serializeUserSegment(userSegment);

        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,kritterUserId);
        NoSqlData recentImpressionHistoryNoSqlData = new NoSqlData(NoSqlData.NoSqlDataType.BINARY,
                                                                   dataToStore);

        Map<String, NoSqlData> dataMap = new HashMap<String, NoSqlData>();
        dataMap.put(attributeNameImpressionHistory,recentImpressionHistoryNoSqlData);

        noSqlNamespaceOperationsInstance.updateAttributesInThisNamespace(
                namespaceName, tableName, primaryKeyValue, dataMap
        );
    }

    /**
     * Function that uses the executor service to update UserSegment
     * @param kritterUserId
     * @param eventSet
     */
    @Override
    public void updateRetargetingSegmentInUserSegmentUsingExecutorService(String kritterUserId, int segmentId) {
        UserSegmentUpdator updater = new UserSegmentUpdator(this.logger, this, kritterUserId, segmentId);
        logger.debug("Updating user segment for {}", kritterUserId);
        updaterService.submit(updater);
    }

    public static class UserSegmentUpdator implements Runnable {
        private final Logger logger;
        private final UserSegmentCache userSegmentCache;
        private final String kritterUserId;
        private int segmentId;

        public UserSegmentUpdator(Logger logger, UserSegmentCache userSegmentCache,
                                    String kritterUserId, int segmentId) {
            this.logger = logger;
            this.userSegmentCache = userSegmentCache;
            this.kritterUserId = kritterUserId;
            this.segmentId = segmentId;
        }

        @Override
        public void run() {
            try {
                if(logger.isDebugEnabled()) {
                    logger.debug("Updating user segment for user id : {}", kritterUserId);
                    logger.debug("\tsegment id : {}", segmentId);
                }
                userSegmentCache.updateRetargetingSegmentInUserSegment(this.kritterUserId, this.segmentId);
            } catch (Exception e) {
                logger.error("Exception occurred in user segment updater. {}", e);
            }
        }
    }

    public byte[] serializeUserSegment(UserSegment userSegment) throws TException
    {
        return thriftSerializer.serialize(userSegment);
    }

    public UserSegment deserializeUserSegment(byte[] byteData) throws TException
    {
        UserSegment userSegment = new UserSegment();
        thriftDeserializer.deserialize(userSegment,byteData);

        return userSegment;
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
