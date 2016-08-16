package com.kritter.adserving.user.job;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.user.userid.UserIdProvider;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class UserIdFetchJob implements Job {
    private Logger logger;
    @Getter
    private String name;
    private String requestObjectKey;
    private UserIdProvider userIdProvider;

    public UserIdFetchJob(String loggerName,
                          String name,
                          String requestObjectKey,
                          UserIdProvider userIdProvider) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = name;
        this.requestObjectKey = requestObjectKey;
        this.userIdProvider = userIdProvider;
    }

    @Override
    public void execute(Context context) {
        this.logger.info("Inside execute of {}.", name);

        Request request = (Request)context.getValue(requestObjectKey);

        if(request == null) {
            this.logger.error("Request object is null inside {}.", this.name);
            return;
        }

        if(this.userIdProvider != null) {
            Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
            if(externalUserIds != null && !externalUserIds.isEmpty()) {
                // Find internal user id for the supplied external user ids and set the internal user id in the
                // request
                ReqLog.debugWithDebug(this.logger, request, "Number of external user ids : {}", externalUserIds.size());
                String internalUserId = this.userIdProvider.getInternalUserId(externalUserIds);
                ReqLog.debugWithDebug(this.logger, request, "Internal user id found : {}", internalUserId);
                request.setUserId(internalUserId);
            } else {
                ReqLog.debugWithDebug(this.logger, request, "External User ids not present in this request.");
            }
        } else {
            ReqLog.debugWithDebug(this.logger, request, "User id provider inside {} is null.", this.name);
        }
    }
}
