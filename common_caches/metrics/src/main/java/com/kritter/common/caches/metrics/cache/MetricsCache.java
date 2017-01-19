package com.kritter.common.caches.metrics.cache;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.core.metrics.YammerMetrics;
import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class MetricsCache implements ICache {
    @Getter
    private String name;
    private Logger logger;
    public YammerMetrics metrics;
    private static final ObjectMapper mapper = new ObjectMapper();

    public MetricsCache(String name, String loggerName) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.metrics = new YammerMetrics();
    }

    public void incrementInvocations(String callerName) {
        metrics.incrementJobInvocations(callerName);
    }

    public void incrementLatency(String callerName, long time) {
        metrics.incrementJobLatency(callerName, time);
    }

    public void incrementFailures(String callerName) {
        metrics.incrementJobFailures(callerName);
    }

    /**
     * This method returns stats of each of the jobs
     * @return gives a map with job invocations, latency in nanoseconds and failures
     */
    public String getJobStats() {
        SortedMap<String, Counter> jobCounters = metrics.getMetricRegistry().getCounters(new MetricFilter() {
            @Override
            public boolean matches(String s, Metric metric) {
                return s != null && !s.isEmpty() && s.indexOf(':') != -1;
            }
        });

        Map<String, Map<String, Long>> jobStatsMap = new HashMap<String, Map<String, Long>>();
        for(Map.Entry<String, Counter> entry : jobCounters.entrySet()) {
            String name = entry.getKey();
            Counter value = entry.getValue();

            String[] tokens = name.split(":");
            if(tokens.length < 2) continue;
            String jobName = tokens[0];
            String statName = tokens[1];
            Map<String, Long> statsMap = jobStatsMap.get(jobName);
            if(statsMap == null) {
                statsMap = new HashMap<String, Long>();
                jobStatsMap.put(jobName, statsMap);
            }
            statsMap.put(statName, value.getCount());
        }

        try {
            return mapper.writeValueAsString(jobStatsMap);
        } catch (IOException ioe) {
            // Do nothing
        }

        return "";
    }

    @Override
    public String toString() {
        return getJobStats();
    }

    @Override
    public void destroy() {
    }
}
