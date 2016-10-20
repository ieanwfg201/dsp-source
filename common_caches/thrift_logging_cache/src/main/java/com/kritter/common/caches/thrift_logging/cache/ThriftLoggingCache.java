package com.kritter.common.caches.thrift_logging.cache;

import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 *
 */
@ToString
public class ThriftLoggingCache implements ICache {
    private String name;
    private Logger applicationLogger;
    private Logger thriftLogger;
    private final ExecutorService executorService;

    public ThriftLoggingCache(String name,
                              String loggerName,
                              String thriftLoggerName,
                              int threadCount)
            throws InitializationException {
        this.name = name;
        this.applicationLogger = LoggerFactory.getLogger(loggerName);
        this.thriftLogger = LoggerFactory.getLogger(thriftLoggerName);
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    private static class ThriftLogUpdator implements Runnable {
        private final ThriftLoggingCache thriftLoggingCache;
        private String serializedImpressionRequest;

        public ThriftLogUpdator(ThriftLoggingCache thriftLoggingCache, String serializedImpressionRequest) {
            this.thriftLoggingCache = thriftLoggingCache;
            this.serializedImpressionRequest = serializedImpressionRequest;
        }

        @Override
        public void run() {
            thriftLoggingCache.thriftLogger.info(serializedImpressionRequest);
            thriftLoggingCache.applicationLogger.debug("Writing request log : {}", serializedImpressionRequest);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void submitLogLineForLogging(String serializedImpressionRequest) {
        ThriftLogUpdator thriftLogUpdator = new ThriftLogUpdator(this, serializedImpressionRequest);
        executorService.submit(thriftLogUpdator);
        applicationLogger.debug("Sending request log : {} to executor service.", serializedImpressionRequest);
    }

    @Override
    public void destroy() {
        // Shutdown the executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS))
                    applicationLogger.error("Updater service did not terminate");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
