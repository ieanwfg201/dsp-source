package com.kritter.core.metrics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * All job level metrics go into this object.
 *
 * The following metrics are exposed :
 *  Total workflow latency (including all the jobs and workflow)
 *  Per job latency
 *  Total number of invocations of the workflow
 *  Total number of invocations for each job
 *  Number of failures for each job.
 */
@Getter
@EqualsAndHashCode
@ToString
public class Metrics {
    /**
     * Total number of invocations of the workflow.
     */
    private long totalInvocations;

    /**
     * Total latency across all jobs and workflow. This is the
     * total time taken from the time when a request enters the
     * system to the time it is processed.
     */
    private long totalLatency;

    /**
     * Total failures across all jobs.
     */
    private long totalFailures;

    /**
     * For each job, maintain the number of times it was invoked.
     * The map is from job name to invocation count. The job
     * name must be unique.
     */
    private Map<String, Long> perJobInvocations;

    /**
     * For each job, maintain the total latency for the job.
     * This also includes the latency when the job was invoked but
     * failed.
     * The map is from job name to latency. The job
     * name must be unique.
     */
    private Map<String, Long> perJobLatency;

    /**
     * For each job, maintain the total number of times the
     * job failed.
     * The map is from job name to latency. The job
     * name must be unique.
     */
    private Map<String, Long> perJobFailures;

    public Metrics() {
        this.totalInvocations = 0;
        this.totalLatency = 0;
        this.totalFailures = 0;
        this.perJobInvocations = new HashMap<String, Long>();
        this.perJobLatency = new HashMap<String, Long>();
        this.perJobFailures = new HashMap<String, Long>();
    }

    /**
     * Increment the total number of invocations by count.
     * @param count Count to increase invocations by.
     */
    public void incrementTotalInvocations(long count) {
        totalInvocations += count;
    }

    /**
     * Increment the total latency by the given number of milliseconds.
     * @param milliSeconds Number of milliseconds to increase total latency by.
     */
    public void incrementTotalLatency(long milliSeconds) {
        totalLatency += milliSeconds;
    }

    /**
     * Increment the total number of failures by count.
     * @param count Count to increase failures by.
     */
    public void incrementTotalFailures(long count) {
        totalFailures += count;
    }

    /**
     * Increment the number of job invocations for the job with the given
     * jobName by count. If the job doesn't exist in the invocations map
     * initialize it to 1.
     * @param jobName Name of the job for which invocations have to be
     *                incremented
     * @param count Count to increase invocations by.
     */
    public void incrementPerJobInvocations(String jobName, long count) {
        if(!perJobInvocations.containsKey(jobName)) {
            perJobInvocations.put(jobName, 1L);
        } else {
            long currentValue = perJobInvocations.get(jobName);
            perJobInvocations.put(jobName, currentValue + count);
        }
    }

    /**
     * Increment the job latency for the job with the given jobName by
     * the supplied number of milliseconds. If the job doesn't exist
     * in the latency map initialize it to milliseconds.
     * @param jobName Name of the job for which latency has to be
     *                incremented
     * @param milliSeconds Number of milliseconds to increase latency by.
     */
    public void incrementPerJobLatency(String jobName, long milliSeconds) {
        if(!perJobLatency.containsKey(jobName)) {
            perJobLatency.put(jobName, milliSeconds);
        } else {
            long currentValue = perJobLatency.get(jobName);
            perJobLatency.put(jobName, currentValue + milliSeconds);
        }
    }

    /**
     * Increment the number of job failures for the job with the given
     * jobName by count. If the job doesn't exist in the failures map
     * initialize it to 1.
     * @param jobName Name of the job for which failures have to be incremented
     * @param count Count to increase failures by.
     */
    public void incrementPerJobFailures(String jobName, long count) {
        if(!perJobFailures.containsKey(jobName)) {
            perJobFailures.put(jobName, 1L);
        } else {
            long currentValue = perJobFailures.get(jobName);
            perJobFailures.put(jobName, currentValue + count);
        }
    }
}
