package com.kritter.nosql.user.matchid;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.entity.user.userid.InternalUserIdCreator;
import com.kritter.entity.user.userid.UserIdProvider;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.user.userid.UserIdUpdator;
import com.kritter.utils.common.ThreadLocalUtils;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlNamespaceTable;
import com.kritter.utils.nosql.common.SignalingNotificationObject;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Cache corresponding to the table containing id -> kritter user id mapping
 */
public class UserIdCache implements NoSqlNamespaceTable, ICache, UserIdProvider, UserIdUpdator, InternalUserIdCreator {
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_USERID = "attribute_name_user_id";

    @Getter
    private String name;
    private Logger logger;
    @Getter
    private String namespaceName;
    @Getter
    private String tableName;
    @Getter
    private String primaryKeyName;
    @Getter
    private final NoSqlData.NoSqlDataType primaryKeyDataType = NoSqlData.NoSqlDataType.STRING;
    @Getter
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;
    private String attributeNameUserId;
    private Set<String> attributeNameSet;
    @Getter @NonNull
    private List<ExternalUserIdType> priorityList;
    // private ExecutorService updatorService;
    private List<ExternalUserIdType> idTypesNotInPriorityList;
    /*
    @Getter
    private final int threadCount;
    */

    /**
     * @param name Name of the cache
     * @param loggerName logger to be used
     * @param noSqlNamespaceOperationsInstance Object encapsulating the database containing id -> kritter user id
     *                                         mapping
     * @param properties Properties object containing namespace, table, primary key and attributes details
     */
    public UserIdCache(String name,
                       String loggerName,
                       NoSqlNamespaceOperations noSqlNamespaceOperationsInstance,
                       Properties properties,
                       List<ExternalUserIdType> priorityList,
                       int threadCount) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);

        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperationsInstance;

        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.attributeNameUserId = properties.getProperty(ATTRIBUTE_NAME_USERID);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(this.attributeNameUserId);
        this.priorityList = priorityList;
        // this.threadCount = threadCount;

        // ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(name + "-executor-%d").build();
        /// this.updatorService = Executors.newFixedThreadPool(threadCount, namedThreadFactory);

        this.idTypesNotInPriorityList = new ArrayList<ExternalUserIdType>();
        for(ExternalUserIdType userIdType : ExternalUserIdType.values()) {
            if(!priorityList.contains(userIdType)) {
                idTypesNotInPriorityList.add(userIdType);
            }
        }
    }


    /**
     * Given a set of user ids find which of the user id type supplied in the priority list is present. The type present
     * first in the priority list is given highest priority.
     *
     * @param userIds set of user ids obtained from the request
     * @return internal user id
     */
    public ExternalUserId getHighestPriorityId(Set<ExternalUserId> userIds) {
        if(userIds == null || userIds.size() == 0) {
            logger.debug("No user information present in the request. Returning null user id.");
            return null;
        }

        logger.debug("user ids from request :");

        for(ExternalUserIdType type : priorityList) {
            for(ExternalUserId userId : userIds) {
                if(userId.getIdType() == type) {
                    String userIdStr = userId.toString();
                    logger.debug("Found user id : {} with type {}, return internal user id : {} ", userId.getUserId(),
                            type.getTypeName(), userId.toString());
                    return userId;
                }
            }
        }

        for(ExternalUserIdType type : idTypesNotInPriorityList) {
            logger.debug("Id type not present in priority list, type {}.", type.getTypeName());
            for(ExternalUserId userId : userIds) {
                if(userId.getIdType() == type) {
                    String userIdStr = userId.toString();
                    logger.debug("Found user id : {} with type {}, return internal user id : {} ", userId.getUserId(),
                            type.getTypeName(), userId.toString());
                    return userId;
                }
            }
        }

        // To make the compiler happy
        return null;
    }

    /**
     * Given a set of user ids from the request, queries the database containing external user id to internal user id
     * mapping and returns internal user id.
     * If there are multiple different user ids for the external user ids, returns the first hit (the strategy to
     * resolve ties might be changed).
     *
     * @param userIds set of user ids obtained from the request
     * @return internal user id
     */
    @Override
    public void getInternalUserId(Set<ExternalUserId> userIds, SignalingNotificationObject<Map<String, NoSqlData>> noSqlDataMap) {
        if(userIds == null || userIds.size() == 0) {
            logger.debug("No user information present in the request. Returning null user id.");
            return;
        }

        logger.debug("Attribute names : ");
        for(String attribute : attributeNameSet) {
            logger.debug("\t{}", attribute);
        }

        if(this.noSqlNamespaceOperationsInstance == null) {
            logger.debug("Matching/de-duplication database not available. Internal user id is null.");
            return;
        }

        ExternalUserId highestPriorityUserId = getHighestPriorityId(userIds);
        logger.debug("Highest priority user id : {}.", highestPriorityUserId.toString());

        try {
            NoSqlData noSqlData = new NoSqlData(this.primaryKeyDataType, highestPriorityUserId.toString());
            this.noSqlNamespaceOperationsInstance.fetchSingleRecordAttributesAsync(this.namespaceName, this.tableName,
                    noSqlData, this.attributeNameSet, noSqlDataMap);

            /*
            NoSqlData kritterUserIdNoSqlData = queryResult.get(this.attributeNameUserId);
            if(kritterUserIdNoSqlData == null) {
                logger.debug("Could not find entry corresponding to user id : {}", highestPriorityUserId.toString());
                return;
            }

            logger.debug("Found internal user id {}", kritterUserIdNoSqlData.getValue());
            return (String) kritterUserIdNoSqlData.getValue();
            */
        } catch (Exception e) {
            logger.debug("Error occurred while fetching matched/de-duplicated user id. {}", e);
            return;
        }
    }

    private static class UserIdRunnable implements Runnable {
        private final Logger logger;
        private final UserIdCache userIdCache;
        private final Set<String> externalUserIds;
        private final String internalUserId;

        UserIdRunnable(Logger logger, UserIdCache userIdCache, Set<String> externalUserIds,
                              String internalUserId) {
            this.logger = logger;
            this.userIdCache = userIdCache;
            this.externalUserIds = externalUserIds;
            this.internalUserId = internalUserId;
        }

        @Override
        public void run() {
            logger.debug("Updating user id from runnable");
            userIdCache.updateInternalUserIdForExternalIds(externalUserIds, internalUserId);
            ThreadLocalUtils.cleanThreadLocalsOfCurrentThread(logger);
        }
    }

    @Override
    public void updateUserId(Set<String> externalUserIds, String internalUserId) {
        // Nothing to update.
        if(externalUserIds == null || externalUserIds.size() == 0 || internalUserId == null)
            return;

        updateInternalUserIdForExternalIds(externalUserIds, internalUserId);
        /*
        UserIdRunnable userIdRunnable = new UserIdRunnable(this.logger, this, externalUserIds, internalUserId);
        logger.debug("Submitting task to executor service for internal user id {}", internalUserId);
        updatorService.submit(userIdRunnable);
        */
    }

    /**
     *
     * @param externalUserIds Set of external user ids.
     * @param internalUserId internal user id to be used by the system for user identification and storage of user
     */
    private void updateInternalUserIdForExternalIds(Set<String> externalUserIds, String internalUserId) {
        // For all the given user ids, update the match table with external user id to internal user id mapping
        if(internalUserId == null) {
            logger.debug("No target user id specified. No update possible.");
            return;
        }

        if(externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("No external user ids to be updated against.");
            return;
        }

        NoSqlData attributeValue = new NoSqlData(this.primaryKeyDataType, internalUserId);
        Map<String, NoSqlData> attributeNameValueMap = new HashMap<String, NoSqlData>();

        if(null != attributeValue && null != attributeValue.getValue())
            attributeNameValueMap.put(this.attributeNameUserId, attributeValue);

        // Go through the set of external user ids and update the internal user id for each of them
        for(String externalUserId : externalUserIds)  {
            // For each of the external user id, update the internal user id in the database
            NoSqlData primaryKeyValue =  new NoSqlData(this.primaryKeyDataType, externalUserId);

            logger.debug("For external user id : {}, inserting internal id : {}", externalUserId, internalUserId);

            /*
            this.noSqlNamespaceOperationsInstance.updateAttributesInThisNamespace(this.namespaceName, this.tableName,
                    primaryKeyValue, attributeNameValueMap);
                    */
            this.noSqlNamespaceOperationsInstance.updateAttributesInThisNamespaceAsync(this.namespaceName,
                    this.tableName, primaryKeyValue, attributeNameValueMap);
        }
    }

    /**
     * From the given set of external user id's, choose one to be used as internal user id. Typically (but not
     * limited to) cookie id or device id
     * @param externalUserIds Set of external user ids obtained in the request
     * @return Internal user id to be used by the system
     */
    @Override
    public String createInternalUserIdFromExternalUserIds(Set<ExternalUserId> externalUserIds) {
        ExternalUserId chosenUserId = getHighestPriorityId(externalUserIds);
        if(chosenUserId != null)
            return chosenUserId.toString();

        return null;
    }

    @Override
    public void destroy() {
        noSqlNamespaceOperationsInstance.destroy();
        /*
        // Shutdown the executor service
        updatorService.shutdown();
        try {
            if(!updatorService.awaitTermination(30, TimeUnit.SECONDS)) {
                updatorService.shutdownNow();
                if(!updatorService.awaitTermination(30, TimeUnit.SECONDS))
                    logger.error("Updater service did not terminate");
            }
        } catch (InterruptedException ie) {
            updatorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        */
    }
}
