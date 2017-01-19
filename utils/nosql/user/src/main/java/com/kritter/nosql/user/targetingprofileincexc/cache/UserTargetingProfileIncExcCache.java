package com.kritter.nosql.user.targetingprofileincexc.cache;

import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.entity.user.targetingprofileincexc.UserTargetingProfileIncExcProvider;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlNamespaceTable;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class UserTargetingProfileIncExcCache implements NoSqlNamespaceTable, ICache,
        UserTargetingProfileIncExcProvider {
    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_TARGETING_PROFILES_INC_EXC = "attribute_name_tp_inc_exc";

    @Getter
    private String name;
    private Logger logger;

    @Getter
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;

    @Getter
    private final String namespaceName;
    @Getter
    private final String tableName;
    @Getter
    private final String primaryKeyName;
    @Getter
    private final NoSqlData.NoSqlDataType primaryKeyDataType = NoSqlData.NoSqlDataType.STRING;
    private final String attributeNameTargetingProfilesIncExc;
    private Set<String> attributeNameSet;

    public UserTargetingProfileIncExcCache(String name,
                                           String loggerName,
                                           NoSqlNamespaceOperations noSqlNamespaceOperations,
                                           Properties properties) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperations;

        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.attributeNameTargetingProfilesIncExc = properties.getProperty(ATTRIBUTE_NAME_TARGETING_PROFILES_INC_EXC);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(attributeNameTargetingProfilesIncExc);
    }

    public void addIncExcTargetingProfileId(String userId, int targetingProfileId) {
        Map<String, NoSqlData> userIdDataMap = this.noSqlNamespaceOperationsInstance.
                fetchSingleRecordAttributes(this.namespaceName, this.tableName,
                        new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId), this.attributeNameSet);

        NoSqlData targetingProfileIdsIncExcObject = userIdDataMap.get(this.attributeNameTargetingProfilesIncExc);

        List<Long> targetingProfileIdsForUser = null;
        if(targetingProfileIdsIncExcObject == null) {
            this.logger.debug("User id : {} has no targeting profile inclusion exclusion data.", userId);
            targetingProfileIdsForUser = new ArrayList<Long>();
        } else {
            targetingProfileIdsForUser = (List<Long>) targetingProfileIdsIncExcObject.getValue();
            this.logger.debug("Targeting profile ids list fetched for user id : {}. Number of targeting profiles : {}",
                    userId, targetingProfileIdsForUser.size());
        }

        if(targetingProfileIdsForUser.contains((long) targetingProfileId)) {
            this.logger.debug("Targeting profile ids list for user id : {} already contains targeting profile id : " +
                    "{}", userId, targetingProfileId);
        } else {
            this.logger.debug("Targeting profile ids list fetched for user id : {} does not contain targeting " +
                    "profile id : {}. Adding targeting profile id to the list.", userId, targetingProfileId);
            targetingProfileIdsForUser.add((long) targetingProfileId);

            NoSqlData primaryKeyNosqlData = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
            NoSqlData attributeValue = new NoSqlData(NoSqlData.NoSqlDataType.LIST_LONG, targetingProfileIdsForUser);
            this.noSqlNamespaceOperationsInstance.insertAttributeToThisNamespace(this.namespaceName, this.tableName,
                    primaryKeyNosqlData, this.attributeNameTargetingProfilesIncExc, attributeValue);
        }
    }

    public void deleteIncExcTargetingProfileId(String userId, int targetingProfileId) {
        Map<String, NoSqlData> userIdDataMap = this.noSqlNamespaceOperationsInstance.
                fetchSingleRecordAttributes(this.namespaceName, this.tableName,
                        new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId), this.attributeNameSet);

        NoSqlData targetingProfileIdsIncExcObject = userIdDataMap.get(this.attributeNameTargetingProfilesIncExc);

        List<Long> targetingProfileIdsForUser = null;
        if(targetingProfileIdsIncExcObject == null) {
            this.logger.debug("User id : {} has no targeting profile inclusion exclusion data.", userId);
            targetingProfileIdsForUser = new ArrayList<Long>();
        } else {
            targetingProfileIdsForUser = (List<Long>) targetingProfileIdsIncExcObject.getValue();
            this.logger.debug("Targeting profile ids list fetched for user id : {}. Number of targeting profiles : {}",
                    userId, targetingProfileIdsForUser.size());
        }

        if(targetingProfileIdsForUser.contains((long) targetingProfileId)) {
            this.logger.debug("Targeting profile ids list for user id : {} contains targeting profile id : " +
                    "{}. Removing the targeting profile id from it's list", userId, targetingProfileId);
            targetingProfileIdsForUser.remove(new Long(targetingProfileId));

            NoSqlData primaryKeyNosqlData = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
            NoSqlData attributeValue = new NoSqlData(NoSqlData.NoSqlDataType.LIST_LONG, targetingProfileIdsForUser);
            this.noSqlNamespaceOperationsInstance.insertAttributeToThisNamespace(this.namespaceName, this.tableName,
                    primaryKeyNosqlData, this.attributeNameTargetingProfilesIncExc, attributeValue);
        } else {
            this.logger.error("Targeting profile ids list fetched for user id : {} does not contain targeting " +
                    "profile id : {}. No removal to take place.", userId, targetingProfileId);
        }
    }

    @Override
    public List<Integer> getIncExcTargetingProfileIds(Set<String> userIds) {
        //Fetch information for this user.
        Set<NoSqlData> primaryKeys = new HashSet<NoSqlData>();
        for(String userId : userIds) {
            NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, userId);
            this.logger.debug("User id to fetch inc/exc/targeting profiles for : {}", userId);
            primaryKeys.add(primaryKeyValue);
        }

        Map<NoSqlData, Map<String, NoSqlData>> userIdDataMap =
                noSqlNamespaceOperationsInstance.fetchMultipleRecordsAttributes(this.namespaceName, this.tableName,
                        primaryKeys, this.attributeNameSet);

        if(userIdDataMap != null && userIdDataMap.size() != 0) {
            List<Integer> targetingProfileIds = new ArrayList<Integer>();

            for(Map.Entry<NoSqlData, Map<String, NoSqlData>> userIdDataMapEntry : userIdDataMap.entrySet()) {
                NoSqlData primaryKeyValue = userIdDataMapEntry.getKey();
                String userId = (String) primaryKeyValue.getValue();

                Map<String, NoSqlData> attributeKeyValueMap = userIdDataMapEntry.getValue();

                NoSqlData targetingProfileIdsIncExcObject = attributeKeyValueMap.get(this.attributeNameTargetingProfilesIncExc);
                if (targetingProfileIdsIncExcObject == null) {
                    this.logger.debug("Entry for user id : {} in namespace : {}, table : {}, with attribue : {}, " +
                            "not found.", userId, this.namespaceName, this.tableName,
                            this.attributeNameTargetingProfilesIncExc);
                    continue;
                }

                @SuppressWarnings("unchecked")
                List<Long> targetingProfileIdForUser = (List<Long>) targetingProfileIdsIncExcObject.getValue();
                this.logger.debug("Fetched list of targeting profile ids included/excluded for this user id : {}.",
                        userId);
                for(long targetingProfileId : targetingProfileIdForUser) {
                    targetingProfileIds.add((int) targetingProfileId);
                }
            }

            return targetingProfileIds;
        } else {
            this.logger.debug("No value for given user ids found in the datastore. No targeting profiles " +
                    "include/exclude this user");

            return null;
        }
    }

    @Override
    public void destroy() {
        /* Do nothing */
    }
}
