package com.kritter.adserving.user.job;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.user.userid.InternalUserIdCreator;
import com.kritter.entity.user.userid.UserIdUpdator;
import com.kritter.utils.common.ThreadLocalUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class UserIdUpdateJob implements Job {
    private Logger logger;
    private String name;
    private String requestObjectKey;
    private UserIdUpdator userIdUpdator;
    private InternalUserIdCreator internalUserIdCreator;

    public UserIdUpdateJob(String loggerName,
                           String name,
                           String requestObjectKey,
                           InternalUserIdCreator internalUserIdCreator,
                           UserIdUpdator userIdUpdator) {
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.requestObjectKey = requestObjectKey;
        this.internalUserIdCreator = internalUserIdCreator;
        this.userIdUpdator = userIdUpdator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(Context context) {
        logger.debug("Inside execute of {}", this.name);

        Request request = (Request)context.getValue(requestObjectKey);

        if(null == request) {
            // logger.error("Request, Response are null inside {}", this.name);
            context.setTerminated(true);
            return;
        }

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("No external user ids in the request. No update required.");
            return;
        }

        Set<String> externalUserIdStrs = new HashSet<String>();
        for(ExternalUserId externalUserId : externalUserIds) {
            externalUserIdStrs.add(externalUserId.toString());
        }

        // If the internal user id is already present, that means there is already an entry for external id in
        // the user id matching table
        String internalUserId = request.getUserId();
        if(internalUserId == null) {
            internalUserId = internalUserIdCreator.createInternalUserIdFromExternalUserIds(externalUserIds);
        }

        logger.debug("Given the list of external user ids, internal id : {}", internalUserId);

        userIdUpdator.updateUserId(externalUserIdStrs, internalUserId);
        ThreadLocalUtils.cleanThreadLocalsOfCurrentThread(logger);
    }
}
