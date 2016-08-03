package com.kritter.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import lombok.Getter;

/**
 * Metrics for the workflow and jobs.
 *
 * For the overall workflow, three metrics are exposed
 * 1) Total invocations : Number of invocations for the workflow
 * 2) Total latency : Total time taken in the workflow
 * 3) Total failures : Number of failures in the workflow
 *
 * For each job, the same three metrics are exposed.
 */
public class YammerMetrics {
    @Getter
    private final MetricRegistry metricRegistry = new MetricRegistry();

    private static final String INVOCATIONS_COUNTER_NAME = "invocations";
    private static final String LATENCY_COUNTER_NAME = "latency";
    private static final String FAILURES_COUNTER_NAME = "failures";

    public void incrementTotalInvocations() {
        Counter counter = metricRegistry.counter(INVOCATIONS_COUNTER_NAME);
        counter.inc();
    }

    public void incrementTotalLatency(long millis) {
        Counter counter = metricRegistry.counter(LATENCY_COUNTER_NAME);
        counter.inc(millis);
    }

    public void incrementTotalFailures() {
        Counter counter = metricRegistry.counter(FAILURES_COUNTER_NAME);
        counter.inc();
    }

    public void incrementJobInvocations(String jobName) {
        Counter counter = metricRegistry.counter(jobName + ":" + INVOCATIONS_COUNTER_NAME);
        counter.inc();
    }

    public void incrementJobLatency(String jobName, long millis) {
        Counter counter = metricRegistry.counter(jobName + ":" + LATENCY_COUNTER_NAME);
        counter.inc(millis);
    }

    public void incrementJobFailures(String jobName) {
        Counter counter = metricRegistry.counter(jobName + ":" + FAILURES_COUNTER_NAME);
        counter.inc();
    }

    public long getTotalInvocations() {
        return metricRegistry.counter(INVOCATIONS_COUNTER_NAME).getCount();
    }

    public long getTotalLatency() {
        return metricRegistry.counter(LATENCY_COUNTER_NAME).getCount();
    }

    public long getTotalFailures() {
        return metricRegistry.counter(FAILURES_COUNTER_NAME).getCount();
    }

    public long getJobInvocations(String jobName) {
        return metricRegistry.counter(jobName + ":" + INVOCATIONS_COUNTER_NAME).getCount();
    }

    public long getJobLatency(String jobName) {
        return metricRegistry.counter(jobName + ":" + LATENCY_COUNTER_NAME).getCount();
    }

    public long getJobFailures(String jobName) {
        return metricRegistry.counter(jobName + ":" + FAILURES_COUNTER_NAME).getCount();
    }
}
