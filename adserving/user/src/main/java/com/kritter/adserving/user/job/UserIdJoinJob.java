package com.kritter.adserving.user.job;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.SignalingNotificationObject;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

public class UserIdJoinJob implements Job {
    @Getter
    private String name;
    private Logger logger;
    private String requestObjectKey;
    private String contextUserIdFetchMapKey;
    private String attributeNameUserId;

    public UserIdJoinJob(String loggerName,
                         String name,
                         String requestObjectKey,
                         String contextUserIdFetchMapKey,
                         String attributeNameUserId) {
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.requestObjectKey = requestObjectKey;
        this.contextUserIdFetchMapKey = contextUserIdFetchMapKey;
        this.attributeNameUserId = attributeNameUserId;
    }


    @Override
    public void execute(Context context) {
        Request request = (Request)context.getValue(requestObjectKey);

        ReqLog.debugWithDebugNew(logger, request, "Inside execute of {}", getName());

        if(request == null) {
            logger.debug("Request object null inside : {}.", getName());
            return;
        }

        SignalingNotificationObject<Map<String, NoSqlData>> noSqlDataMap=
                (SignalingNotificationObject<Map<String, NoSqlData>>) context.getValue(contextUserIdFetchMapKey);
        if(noSqlDataMap == null) {
            ReqLog.debugWithDebugNew(logger, request, "No entry set in context for {}.", contextUserIdFetchMapKey);
            return;
        }
        Map<String, NoSqlData> queryResult = noSqlDataMap.get();
        if(queryResult == null) {
            ReqLog.debugWithDebugNew(logger, request, "User id fetch unsuccessful.");
            return;
        }

        NoSqlData kritterUserIdNoSqlData = queryResult.get(this.attributeNameUserId);
        if(kritterUserIdNoSqlData == null) {
            logger.debug("Could not find entry corresponding to the internal id.");
            return;
        }

        logger.debug("Found internal user id {}", kritterUserIdNoSqlData.getValue());
        request.setUserId((String) kritterUserIdNoSqlData.getValue());

    }
}
