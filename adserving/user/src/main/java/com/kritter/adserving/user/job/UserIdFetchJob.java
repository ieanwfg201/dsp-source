package com.kritter.adserving.user.job;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.user.userid.UserIdProvider;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.SignalingNotificationObject;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserIdFetchJob implements Job {
    private Logger logger;
    @Getter
    private String name;
    private String requestObjectKey;
    private UserIdProvider userIdProvider;
    private String contextUserIdFetchMapKey;

    public UserIdFetchJob(String loggerName,
                          String name,
                          String requestObjectKey,
                          UserIdProvider userIdProvider,
                          String contextUserIdFetchMapKey) {
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.requestObjectKey = requestObjectKey;
        this.userIdProvider = userIdProvider;
        this.contextUserIdFetchMapKey = contextUserIdFetchMapKey;
    }

    @Override
    public void execute(Context context) {
        this.logger.info("Inside execute of {}.", name);

        Request request = (Request)context.getValue(requestObjectKey);

        if(request == null) {
            // this.logger.error("Request object is null inside {}.", this.name);
            context.setTerminated(true);
            return;
        }

        if(this.userIdProvider != null) {
            Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
            if(externalUserIds != null && !externalUserIds.isEmpty()) {
                // Find internal user id for the supplied external user ids and set the internal user id in the
                // request
                ReqLog.debugWithDebugNew(this.logger, request, "Number of external user ids : {}", externalUserIds.size());
                SignalingNotificationObject<Map<String, NoSqlData>> noSqlDataMap= new SignalingNotificationObject<Map<String, NoSqlData>>();
                this.userIdProvider.getInternalUserId(externalUserIds, noSqlDataMap);
                context.setValue(this.contextUserIdFetchMapKey, noSqlDataMap);
            } else {
                ReqLog.debugWithDebugNew(this.logger, request, "External User ids not present in this request.");
            }
        } else {
            ReqLog.debugWithDebugNew(this.logger, request, "User id provider inside {} is null.", this.name);
        }
    }
}
