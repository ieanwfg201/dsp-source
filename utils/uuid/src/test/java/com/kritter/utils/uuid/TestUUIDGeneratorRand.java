package com.kritter.utils.uuid;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.kritter.utils.uuid.rand.UUIDGenerator;
import lombok.Getter;
import org.junit.*;
import static org.junit.Assert.*;

public class TestUUIDGeneratorRand {
    private List<MultiThreadedUUIDGenerator> generators;
    private int totalUUIDCount = 1000000;

    @Getter
    private static class MultiThreadedUUIDGenerator implements Runnable {
        private int numUUIDs;
        @Getter
        private List<UUID> uuidList;

        public MultiThreadedUUIDGenerator(int numUUIDs) {
            this.numUUIDs = numUUIDs;
            this.uuidList = new ArrayList<UUID>();
        }

        @Override
        public void run() {
            UUIDGenerator uuidGenerator = new UUIDGenerator();
            uuidList = new ArrayList<UUID>();
            for(int i = 0; i < numUUIDs; ++i) {
                UUID uuid = uuidGenerator.generateUniversallyUniqueIdentifier();
                uuidList.add(uuid);
            }
        }
    }

    public void setup(int threadCount) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        generators = new ArrayList<MultiThreadedUUIDGenerator>(threadCount);
        List<Future> futures = new ArrayList<Future>();
        long beginTime = System.currentTimeMillis();

        for(int i = 0; i < threadCount; ++i) {
            MultiThreadedUUIDGenerator generator = new MultiThreadedUUIDGenerator(totalUUIDCount / threadCount);
            generators.add(generator);
            futures.add(executorService.submit(generator));
        }

        try {
            for(Future future : futures) {
                future.get();
            }

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - beginTime;
            double averageTime = timeTaken * 1. / totalUUIDCount;
            System.out.format("Average time for %d threads = %.6f ms\n", threadCount, averageTime);
        } catch (Exception e) {
            throw new RuntimeException("RuntimeException in UUID Rand test ",e);
        } finally {
            executorService.shutdown();
        }
    }

    Set<UUID> runTest(int threadCount) {
        setup(threadCount);

        Set<UUID> uuidSet = new HashSet<UUID>();
        for(MultiThreadedUUIDGenerator generator : generators) {
            uuidSet.addAll(generator.getUuidList());
        }

        return uuidSet;
    }

    @Test
    public void test1() {
        assertEquals(totalUUIDCount, runTest(1).size());
    }

    @Test
    public void Test10() {
        assertEquals(totalUUIDCount, runTest(10).size());
    }

    @Test
    public void Test100() {
        assertEquals(totalUUIDCount, runTest(100).size());
    }
}
