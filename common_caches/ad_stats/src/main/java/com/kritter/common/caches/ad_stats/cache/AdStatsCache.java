package com.kritter.common.caches.ad_stats.cache;

import com.kritter.abstraction.cache.abstractions.AbstractStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.adserving.thrift.struct.AdToNofillReasonCount;
import com.kritter.adserving.thrift.struct.NoFillReason;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.joda.time.DateTimeZone;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.joda.time.DateTime;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.kritter.adserving.thrift.struct.AdStats;

/**
 *
 */
@ToString(of={"perHourRequests", "timestampToAdNofillReasonCountMap"})
public class AdStatsCache extends AbstractStatsReloadableCache {
    private String name;
    private Logger applicationLogger;
    private Logger adStatsThriftLogger;
    private ReadWriteLock readWriteLock;
    private final ExecutorService executorService;

    private volatile ConcurrentMap<Long, AtomicLong> perHourRequests;
    private volatile ConcurrentMap<Long, ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>>>
            timestampToAdNofillReasonCountMap;

    public AdStatsCache(String name, String loggerName, String adStatsThriftLoggerName, int threadCount,
                        Properties properties)
            throws InitializationException {
        super(LogManager.getLogger(loggerName), properties);
        this.name = name;
        this.applicationLogger = LogManager.getLogger(loggerName);
        this.adStatsThriftLogger = LogManager.getLogger(adStatsThriftLoggerName);
        this.readWriteLock = new ReentrantReadWriteLock();
        this.perHourRequests = new ConcurrentHashMap<Long, AtomicLong>();
        this.timestampToAdNofillReasonCountMap = new ConcurrentHashMap<Long, ConcurrentMap<Integer,
                ConcurrentMap<String, AtomicLong>>>();
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    private static class AdStatsUpdator implements Runnable {
        private final AdStatsCache adStatsCache;
        private final long timestamp;
        private final Map<Integer, Integer> adToNofillReasonMap;

        public AdStatsUpdator(AdStatsCache adStatsCache, long timestamp, Map<Integer, Integer> adToNofillReasonMap) {
            this.adStatsCache = adStatsCache;
            this.timestamp = timestamp;
            this.adToNofillReasonMap = adToNofillReasonMap;
        }

        @Override
        public void run() {
            adStatsCache.updatetimestampToNofillReasonMap(timestamp, adToNofillReasonMap);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void updateAdNofillReasonMap(long timestamp, Map<Integer, Integer> adToNofillReasonMap) {
        AdStatsUpdator adStatsUpdator = new AdStatsUpdator(this, timestamp, adToNofillReasonMap);
        executorService.submit(adStatsUpdator);
    }

    private void updatetimestampToNofillReasonMap(long timestamp, Map<Integer, Integer> adToNofillReasonMap) {
        this.applicationLogger.debug("Updating time stamp no fill");

        // Change the timestamp from that in the request that is upto the millis to the milli for the corresponding
        // hour.
        DateTime requestDateTime = new DateTime(timestamp);
        DateTime requestDateTimeToHour = requestDateTime.withTime(requestDateTime.getHourOfDay(), 0, 0, 0);
        long requestHourTimestamp = requestDateTimeToHour.getMillis();

        this.applicationLogger.debug("Request timestamp : {}, hour timestamp : {}", timestamp, requestHourTimestamp);

        this.readWriteLock.readLock().lock();

        AtomicLong requestsForHour = this.perHourRequests.get(requestHourTimestamp);
        if(requestsForHour == null) {
            AtomicLong value = new AtomicLong();
            requestsForHour = this.perHourRequests.putIfAbsent(requestHourTimestamp, value);
            if(requestsForHour == null) {
                requestsForHour = value;
            }
        }

        long requestsForHourValue = requestsForHour.incrementAndGet();
        this.applicationLogger.debug("Total number of requests now = {}", requestsForHourValue);

        ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>> currentTimestampMap =
                this.timestampToAdNofillReasonCountMap.get(requestHourTimestamp);
        if(currentTimestampMap == null) {
            ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>> value =
                    new ConcurrentHashMap<Integer, ConcurrentMap<String, AtomicLong>>();
            currentTimestampMap = this.timestampToAdNofillReasonCountMap.putIfAbsent(requestHourTimestamp, value);
            if(currentTimestampMap == null) {
                currentTimestampMap = value;
            }
        }

        this.applicationLogger.debug("Updating ad to no fill reason.");

        if(adToNofillReasonMap != null) {
            for (Map.Entry<Integer, Integer> adToNofillReason : adToNofillReasonMap.entrySet()) {
                int adId = adToNofillReason.getKey();
                int nofillReason = adToNofillReason.getValue();

                this.applicationLogger.debug("\tAd id : {}", adId);

                ConcurrentMap<String, AtomicLong> nofillReasonCountMap = currentTimestampMap.get(adId);
                if (nofillReasonCountMap == null) {
                    final ConcurrentMap<String, AtomicLong> value = new ConcurrentHashMap<String, AtomicLong>();
                    nofillReasonCountMap = currentTimestampMap.putIfAbsent(adId, value);
                    if (nofillReasonCountMap == null) {
                        nofillReasonCountMap = value;
                    }
                }

                AtomicLong count = nofillReasonCountMap.get(nofillReason);
                if (count == null) {
                    final AtomicLong value = new AtomicLong(0);
                    count = nofillReasonCountMap.putIfAbsent(NoFillReason.findByValue(nofillReason).name(), value);
                    if (count == null) {
                        count = value;
                    }
                }
                long currentCount = count.incrementAndGet();
                this.applicationLogger.debug("No fill reason : {}, count : {}", nofillReason, currentCount);
            }
        }

        this.readWriteLock.readLock().unlock();
    }

    @Override
    protected void refreshCache() throws RefreshException {
        this.applicationLogger.debug("Inside refresh of {}", this.name);

        // This is the function that serializes the global map
        // Just keeping the map as volatile doesn't help. Ensure that the reads and writes are synchronous. There must
        // be no read happening when the swap takes place. Will need read and write locks.
        ConcurrentMap<Long, ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>>> toSwap =
                new ConcurrentHashMap<Long, ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>>>();

        ConcurrentMap<Long, AtomicLong> perHourSwap = new ConcurrentHashMap<Long, AtomicLong>();

        this.applicationLogger.debug("Acquiring write lock in {}", this.name);

        this.readWriteLock.writeLock().lock();

        this.applicationLogger.debug("Inside write lock section of refresh");

        ConcurrentMap<Long, ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>>> toSerialize =
                this.timestampToAdNofillReasonCountMap;
        this.applicationLogger.debug("Total size of ad no fill reason count map = {}",
                this.timestampToAdNofillReasonCountMap.size());
        this.timestampToAdNofillReasonCountMap = toSwap;
        this.applicationLogger.debug("Size of map to swap = {}", toSwap.size());

        ConcurrentMap<Long, AtomicLong> perHourToSerialize = this.perHourRequests;
        this.applicationLogger.debug("Size of per hour requests map = {}", this.perHourRequests.size());
        this.perHourRequests = perHourSwap;
        this.applicationLogger.debug("Size of swap map per hour = {}", perHourSwap.size());

        this.applicationLogger.debug("Exiting write lock section of refresh");

        this.readWriteLock.writeLock().unlock();

        if(toSerialize.size() == 0 && perHourToSerialize.size() == 0) {
            // Nothing to serialize, continue
            this.applicationLogger.debug("No entries in the map to serialize.");
            return;
        }

        // Create an object of type ad stats that would be serialized.
        AdStats adStats = new AdStats();
        Map<Long, Map<Integer, AdToNofillReasonCount>> adStatsMap = new HashMap<Long, Map<Integer,
                AdToNofillReasonCount>>();
        adStats.setAdStatsMap(adStatsMap);

        this.applicationLogger.debug("Number of entries in the map to serialize : {}", toSerialize.size());

        for(Map.Entry<Long, ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>>> firstLevelEntry :
                toSerialize.entrySet()) {
            long timestamp = firstLevelEntry.getKey();
            this.applicationLogger.debug("Creating entry for timestamp : {}", timestamp);
            ConcurrentMap<Integer, ConcurrentMap<String, AtomicLong>> adToNoFillReasonCount =
                    firstLevelEntry.getValue();

            Map<Integer, AdToNofillReasonCount> adToNofillReasonCounts = null;

            if(!adStatsMap.containsKey(timestamp)) {
                adToNofillReasonCounts = new HashMap<Integer, AdToNofillReasonCount>();
                adStatsMap.put(timestamp, adToNofillReasonCounts);
            } else {
                adToNofillReasonCounts = adStatsMap.get(timestamp);
            }

            for(Map.Entry<Integer, ConcurrentMap<String, AtomicLong>> secondLevelEntry :
                    adToNoFillReasonCount.entrySet()) {
                int adId = secondLevelEntry.getKey();

                this.applicationLogger.debug("Creating entry for ad id : {}", adId);

                ConcurrentMap<String, AtomicLong> noFillReasonToCountMap = secondLevelEntry.getValue();
                AdToNofillReasonCount adToNofillReasonCount = null;
                if(adToNofillReasonCounts.containsKey(adId)) {
                    adToNofillReasonCount = adToNofillReasonCounts.get(adId);
                } else {
                    adToNofillReasonCount = new AdToNofillReasonCount();
                    Map<String, Long> nofillReasonCountMap = new HashMap<String, Long>();
                    adToNofillReasonCount.setAdId(adId);
                    adToNofillReasonCount.setNofillReasonCount(nofillReasonCountMap);
                    adToNofillReasonCounts.put(adId, adToNofillReasonCount);
                }

                for(Map.Entry<String, AtomicLong> thirdLevelEntry : noFillReasonToCountMap.entrySet()) {
                    String nofillReason = thirdLevelEntry.getKey();
                    long nofillCount = thirdLevelEntry.getValue().get();

                    this.applicationLogger.debug("No fill reason : {}, count : {}", nofillReason, nofillCount);

                    long existingNofillCount = 0;
                    if(adToNofillReasonCount.getNofillReasonCount().containsKey(nofillReason)) {
                        existingNofillCount = adToNofillReasonCount.getNofillReasonCount().get(nofillReason);
                    }
                    long currentNofillCount = existingNofillCount + nofillCount;
                    applicationLogger.debug("No fill count for time : {} ad id : {} and no fill reason : {} is {}",
                            timestamp, adId, nofillReason, currentNofillCount);
                    adToNofillReasonCount.getNofillReasonCount().put(nofillReason, currentNofillCount);
                }
            }
        }

        Map<Long, Long> adStatsRequestMap = new HashMap<Long, Long>();
        adStats.setHourTotalRequestsMap(adStatsRequestMap);
        for(Map.Entry<Long, AtomicLong> perHourRequestsEntry : perHourToSerialize.entrySet()) {
            Long hourTimestamp = perHourRequestsEntry.getKey();
            AtomicLong requests = perHourRequestsEntry.getValue();
            applicationLogger.debug("Total number of requests for hour with timestamp : {} is {}", hourTimestamp,
                    requests.get());
            adStatsRequestMap.put(hourTimestamp, requests.get());
        }

        byte[] serializedAdStats = null;
        Base64 base64Encoder = new Base64(0);

        // Now that the AdStats object is created, we need to serialize it and write to the thrift applicationLogger
        try {
            TSerializer thriftSerializer = new TSerializer();
            serializedAdStats = thriftSerializer.serialize(adStats);
            serializedAdStats =  base64Encoder.encode(serializedAdStats);
        } catch(TException t) {
            this.applicationLogger.error("TException inside ThriftLogger in adserving workflow.",t);
            return;
        }

        this.applicationLogger.debug("Prepared the ad stats object for serialization. Writing it to the log");
        this.adStatsThriftLogger.info(new String(serializedAdStats));
    }

    @Override
    protected void cleanUp() throws ProcessingException {
        executorService.shutdown();
        try {
            if(!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if(!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    applicationLogger.error("Executor service did not terminate");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
