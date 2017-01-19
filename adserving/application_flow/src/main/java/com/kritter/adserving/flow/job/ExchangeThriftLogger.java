package com.kritter.adserving.flow.job;

import com.kritter.common.caches.thrift_logging.cache.ThriftLoggingCache;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;

import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ExchangeThriftLogger implements Job {
    @Getter
    private String name;
    private Logger applicationLogger;
    private ThriftLoggingCache thriftLoggingCache;
    private String requestObjectKey;


    public ExchangeThriftLogger(String name,
                                String applicationLoggerName,
                                ThriftLoggingCache thriftLoggingCache,
                                String requestObjectKey) {
        this.name = name;
        this.applicationLogger = LogManager.getLogger(applicationLoggerName);
        this.thriftLoggingCache = thriftLoggingCache;
        this.requestObjectKey = requestObjectKey;
    }

    @Override
    public void execute(Context context) {
        // TODO : create the log line for logging.
        Request request = (Request)context.getValue(this.requestObjectKey);

        if(null == request || request.isRequestForSystemDebugging())
        {
            this.applicationLogger.error("Aborting!!!, Request object is null inside Thrift logger of adserving " +
                    "flow. Or is a Test Debug Request...");
            return;
        }
        if(request.getCreateExchangeThrift() == null)
        {
            this.applicationLogger.debug("Aborting!!!, CreateExchangeThrift object is null inside Thrift logger of adserving " +
                    "flow");
            return;
        }
        
        String serializedImpressionRequest = request.getCreateExchangeThrift().serializeAndBase64Encode();
        if(serializedImpressionRequest == null)
        {
            this.applicationLogger.debug("Aborting!!!, serializedImpressionRequest object is null inside Thrift logger of adserving " +
                    "flow");
            return;
        }

        applicationLogger.debug("Serialized exchange information : {}", serializedImpressionRequest);
        thriftLoggingCache.submitLogLineForLogging(serializedImpressionRequest);
    }
}
