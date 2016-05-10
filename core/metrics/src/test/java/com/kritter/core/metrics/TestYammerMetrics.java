package com.kritter.core.metrics;

import lombok.Getter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.Assert.assertEquals;

public class TestYammerMetrics {
    private YammerMetrics yammerMetrics = new YammerMetrics();
    ;
    private static final String[] jobNames = {"a", "b", "c"};
    private static final int threadCount = 500;
    private static final int totalMetricEvents = 50000000;

    @Getter
    private static class Metrics {
        private AtomicLong totalInvocations = new AtomicLong();
        private AtomicLong totalLatency = new AtomicLong();
        private AtomicLong totalFailures = new AtomicLong();

        private Map<String, AtomicLong> jobInvocations = new ConcurrentHashMap<String, AtomicLong>();;
        private Map<String, AtomicLong> jobLatency = new ConcurrentHashMap<String, AtomicLong>();
        private Map<String, AtomicLong> jobFailures = new ConcurrentHashMap<String, AtomicLong>();

        private void fill(Map<String, AtomicLong> map) {
            for(int i = 0; i < TestYammerMetrics.jobNames.length; ++i)
                map.put(TestYammerMetrics.jobNames[i], new AtomicLong());
        }

        public Metrics() {
            fill(jobInvocations);
            fill(jobLatency);
            fill(jobFailures);
        }
    }

    private static class MetricsGenerator implements Runnable {
        private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        @Getter private static final AtomicLong totalTime = new AtomicLong();

        @Getter private final YammerMetrics yammerMetrics;
        private final Random random;
        @Getter private final int requests;
        @Getter private static final Metrics cumulativeMetrics = new Metrics();

        public MetricsGenerator(YammerMetrics yammerMetrics, int requests) {
            this.yammerMetrics = yammerMetrics;
            this.random = new Random(System.currentTimeMillis());
            this.requests = requests;
        }

        @Override
        public void run() {
            long beginTime = System.currentTimeMillis();

            for(int i = 0; i < requests; ++i) {
                int event = random.nextInt(6);
                switch (event) {

                    case 0 :
                        yammerMetrics.incrementTotalInvocations();
                        cumulativeMetrics.getTotalInvocations().incrementAndGet();
                        break;

                    case 1 :
                        int millis = random.nextInt(1000);
                        yammerMetrics.incrementTotalLatency(millis);
                        cumulativeMetrics.getTotalLatency().addAndGet(millis);
                        break;

                    case 2 :
                        yammerMetrics.incrementTotalFailures();
                        cumulativeMetrics.getTotalFailures().incrementAndGet();
                        break;

                    case 3 :
                        int job = random.nextInt(3);
                        yammerMetrics.incrementJobInvocations(jobNames[job]);
                        cumulativeMetrics.getJobInvocations().get(jobNames[job]).incrementAndGet();
                        break;

                    case 4 :
                        job = random.nextInt(3);
                        millis = random.nextInt(1000);
                        yammerMetrics.incrementJobLatency(jobNames[job], millis);
                        cumulativeMetrics.getJobLatency().get(jobNames[job]).addAndGet(millis);
                        break;

                    case 5 :
                        job = random.nextInt(3);
                        yammerMetrics.incrementJobFailures(jobNames[job]);
                        cumulativeMetrics.getJobFailures().get(jobNames[job]).incrementAndGet();
                        break;
                }
            }

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - beginTime;
            totalTime.addAndGet(timeTaken);
        }
    }

    public void setup() {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        List<Future> futures = new ArrayList<Future>();

        long beginTime = System.currentTimeMillis();

        for(int i = 0; i < threadCount; ++i) {
            MetricsGenerator metricsGenerator = new MetricsGenerator(yammerMetrics, totalMetricEvents / threadCount);
            futures.add(service.submit(metricsGenerator));
        }

        try {
            for(Future future : futures) {
                future.get();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - beginTime;

            System.out.println("Total time = " + MetricsGenerator.getTotalTime() + ", total events = " + totalMetricEvents);
            System.out.println("Total system time = " + timeTaken);
            service.shutdown();
        }
    }

    @Test
    public void test() {
        setup();

        Metrics metrics = MetricsGenerator.getCumulativeMetrics();
        assertEquals(metrics.getTotalInvocations().get(), yammerMetrics.getTotalInvocations());
        assertEquals(metrics.getTotalLatency().get(), yammerMetrics.getTotalLatency());
        assertEquals(metrics.getTotalFailures().get(), yammerMetrics.getTotalFailures());

        for(int i = 0; i < jobNames.length; ++i) {
            String jobName = jobNames[i];
            assertEquals(metrics.getJobInvocations().get(jobName).get(), yammerMetrics.getJobInvocations(jobName));
            assertEquals(metrics.getJobLatency().get(jobName).get(), yammerMetrics.getJobLatency(jobName));
            assertEquals(metrics.getJobFailures().get(jobName).get(), yammerMetrics.getJobFailures(jobName));
        }
    }
}
