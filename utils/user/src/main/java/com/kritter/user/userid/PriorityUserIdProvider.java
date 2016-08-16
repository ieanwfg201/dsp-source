package com.kritter.user.userid;

import com.kritter.constants.ExternalUserIdType;
import com.kritter.entity.user.userid.UserIdProvider;
import com.kritter.entity.user.userid.ExternalUserId;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cache corresponding to the table containing id -> kritter user id mapping
 */
public class PriorityUserIdProvider implements UserIdProvider {
    @Getter
    private String name;
    private Logger logger;

    @Getter @NonNull
    List<ExternalUserIdType> priorityList;

    /**
     * @param name Name of the cache
     * @param loggerName logger to be used
     * @param priorityList list of external user id type in order of priority. The type that appears earlier in the list
     *                     has higher priority. This list "must" not contain duplicate elements, else the initialization
     *                     fails.
     */
    public PriorityUserIdProvider(String name,
                                  String loggerName,
                                  List<ExternalUserIdType> priorityList) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.priorityList = priorityList;

        Set<ExternalUserIdType> prioritySet = new HashSet<ExternalUserIdType>();
        for(ExternalUserIdType userType : priorityList) {
            if(prioritySet.contains(userType))
                throw new RuntimeException("Duplicate elements for " + userType + " found in the priority list for " +
                        "PriotityUserIdProvider with bean name : " + name);

            prioritySet.add(userType);
        }
    }

    /**
     * Given a set of user ids find which of the user id type supplied in the priority list is present. The type present
     * first in the priority list is given highest priority.
     *
     * @param userIds set of user ids obtained from the request
     * @return internal user id
     */
    @Override
    public String getInternalUserId(Set<ExternalUserId> userIds) {
        if(userIds == null || userIds.size() == 0) {
            logger.debug("No user information present in the request. Returning null user id.");
            return null;
        }

        logger.debug("user ids from request :");

        for(ExternalUserIdType type : priorityList) {
            for(ExternalUserId userId : userIds) {
                if(userId.getIdType() == type) {
                    String userIdStr = userId.toString();
                    logger.debug("Found user id : " + userId.getUserId() + " with type " + type.getTypeName() + ", " +
                            "return internal user id : " + userIdStr);
                    return userIdStr;
                }
            }
        }

        // To make the compiler happy
        return null;
    }
}
