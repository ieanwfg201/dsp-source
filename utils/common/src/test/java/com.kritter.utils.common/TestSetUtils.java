package com.kritter.utils.common;

import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class TestSetUtils {
    SortedSet<Integer>[] sortedSets = new SortedSet[5];
    Set<Integer>[] sets = new HashSet[5];
    private static SortedSet<Integer>[] randomSortedSets;
    private static Set<Integer>[] randomSets;
    private static Random random = new Random();
    private static int setSizeBound = 101;
    private static int minSize = 50;
    private static int maxElementInSet = 20000;

    private static void generateRandomSets() {
        randomSets = new Set[random.nextInt(3) + 2];
        for(int i = 0; i < randomSets.length; ++i) {
            randomSets[i] = new HashSet<Integer>();
            int thisSetSize = random.nextInt(setSizeBound - minSize) + minSize;
            for(int j = 0; j < thisSetSize; ++j) {
                randomSets[i].add(random.nextInt(maxElementInSet));
            }
        }
    }

    private static void generateRandomSortedSets() {
        randomSortedSets = new SortedSet[random.nextInt(3) + 2];
        for(int i = 0; i < randomSortedSets.length; ++i) {
            randomSortedSets[i] = new TreeSet<Integer>();
            int thisSetSize = random.nextInt(setSizeBound - minSize) + minSize;
            for(int j = 0; j < thisSetSize; ++j) {
                randomSortedSets[i].add(random.nextInt(maxElementInSet));
            }
        }
    }

    @Before
    public void setup() {
        for(int i = 0; i < 5; ++i) {
            sortedSets[i] = new TreeSet<Integer>();
            sets[i] = new HashSet<Integer>();
        }

        Collections.addAll(sortedSets[0], new Integer[] {1, 3, 5, 8, 9, 100000, 1123122, 112323333});
        Collections.addAll(sets[0], new Integer[] {1, 3, 5, 8, 9, 100000, 1123122, 112323333});
        Collections.addAll(sortedSets[1], new Integer[] {2, 4, 5, 8, 102, 111, 123, 100000});
        Collections.addAll(sets[1], new Integer[] {2, 4, 5, 8, 102, 111, 123, 100000});
        Collections.addAll(sortedSets[2], new Integer[] {3, 5, 9, 41, 99, 1134, 23213, 100000, 123132332});
        Collections.addAll(sets[2], new Integer[] {3, 5, 9, 41, 99, 1134, 23213, 100000, 123132332});
        sortedSets[4] = null;
        sets[4] = null;
    }

    @Test
    public void testIntersectionTwoSets() {
        Set<Integer> result = SetUtils.intersectSets(sets[0], sets[1]);
        Set<Integer> expected = new HashSet<Integer>();
        Collections.addAll(expected, new Integer[] {5, 8, 100000});
        assertEquals(expected, result);

        result = SetUtils.intersectSets(sets[0], sets[4]);
        expected = new HashSet<Integer>();
        assertEquals(expected, result);

        result = SetUtils.intersectSets(sets[4], sets[4]);
        expected = new HashSet<Integer>();
        assertEquals(expected, result);

        result = SetUtils.intersectSets(sets[0], sets[0]);
        expected = new HashSet<Integer>();
        Collections.addAll(expected, new Integer[] { 1, 3, 5, 8, 9, 100000, 1123122, 112323333 });
        assertEquals(expected, result);

        Set<Integer> first = new HashSet<Integer>();
        Set<Integer> second = new HashSet<Integer>();
        int firstSize = random.nextInt(1000);
        int secondSize = random.nextInt(1000);
        for(int i = 0; i < firstSize; ++i)
            first.add(random.nextInt(5000));
        for(int i = 0; i < secondSize; ++i)
            second.add(random.nextInt(5000));

        result = SetUtils.intersectSets(first, second);
        expected.clear();
        expected.addAll(first);
        expected.retainAll(second);
        assertEquals(result, expected);
    }

    @Test
    public void testIntersectionTwoSortedSets() {
        SortedSet<Integer> result = SetUtils.intersectSortedSets(sortedSets[0], sortedSets[1]);
        SortedSet<Integer> expected = new TreeSet<Integer>();
        Collections.addAll(expected, new Integer[] {5, 8, 100000});
        assertEquals(expected, result);

        result = SetUtils.intersectSortedSets(sortedSets[0], sortedSets[4]);
        expected = new TreeSet<Integer>();
        assertEquals(expected, result);

        result = SetUtils.intersectSortedSets(sortedSets[4], sortedSets[4]);
        expected = new TreeSet<Integer>();
        assertEquals(expected, result);

        result = SetUtils.intersectSortedSets(sortedSets[0], sortedSets[0]);
        expected = new TreeSet<Integer>();
        Collections.addAll(expected, new Integer[] { 1, 3, 5, 8, 9, 100000, 1123122, 112323333 });
        assertEquals(expected, result);

        SortedSet<Integer> first = new TreeSet<Integer>();
        SortedSet<Integer> second = new TreeSet<Integer>();
        int firstSize = random.nextInt(1000);
        int secondSize = random.nextInt(1000);
        for(int i = 0; i < firstSize; ++i)
            first.add(random.nextInt(5000));
        for(int i = 0; i < secondSize; ++i)
            second.add(random.nextInt(5000));

        result = SetUtils.intersectSortedSets(first, second);
        expected.clear();
        expected.addAll(first);
        expected.retainAll(second);
        assertEquals(result, expected);
    }

    private static SortedSet<Integer> dummyNSetIntersection(SortedSet<Integer> ... sortedSets) {
        SortedSet<Integer> result = new TreeSet<Integer>();
        if(sortedSets == null || sortedSets.length == 0)
            return result;

        result.addAll(sortedSets[0]);
        for(int i = 1; i < sortedSets.length; ++i) {
            result.retainAll(sortedSets[i]);
        }

        return result;
    }

    private static Set<Integer> dummyNSetIntersection(Set<Integer> ... sets) {
        Set<Integer> result = new HashSet<Integer>();
        if(sets == null || sets.length == 0)
            return result;

        result.addAll(sets[0]);
        for(int i = 1; i < sets.length; ++i) {
            result.retainAll(sets[i]);
        }

        return result;
    }

    @Test
    public void intersectNSets() {
        Set<Integer> result = SetUtils.intersectNSets(sets);
        Set<Integer> expected = new HashSet<Integer>();
        assertEquals(expected, result);

        Set<Integer>[] subset = new Set[] {sets[0], sets[1], sets[2]};
        result = SetUtils.intersectNSets(subset);
        expected.clear();
        Collections.addAll(expected, new Integer[] {5, 100000});
        assertEquals(expected, result);

        subset = new Set[] {sets[4], sets[1], sets[2]};
        result = SetUtils.intersectNSets(subset);
        expected.clear();
        assertEquals(expected, result);

        for(int i = 0; i < 100; ++i) {
            generateRandomSets();
            result = SetUtils.intersectNSets(randomSets);
            expected = dummyNSetIntersection(randomSets);

            /*
            System.out.println("Number of sets = " + randomSets.length + "\n");
            for(Set<Integer> set : randomSets) {
                printSet(set);
            }

            System.out.println("\nExpected Values = \n");
            for(int value : expected) {
                System.out.println("Value = " + value);
            }
            */

            assertEquals(expected, result);
        }
    }

    @Test
    public void testIntersectionNSortedSets() {
        SortedSet<Integer> result = SetUtils.intersectNSets(sortedSets);
        SortedSet<Integer> expected = new TreeSet<Integer>();
        assertEquals(expected, result);

        SortedSet<Integer>[] subset = new SortedSet[] {sortedSets[0], sortedSets[1], sortedSets[2]};
        result = SetUtils.intersectNSets(subset);
        expected.clear();
        Collections.addAll(expected, new Integer[] {5, 100000});
        assertEquals(expected, result);

        subset = new SortedSet[] {sortedSets[4], sortedSets[1], sortedSets[2]};
        result = SetUtils.intersectNSets(subset);
        expected.clear();
        assertEquals(expected, result);

        for(int i = 0; i < 100; ++i) {
            generateRandomSortedSets();
            result = SetUtils.intersectNSets(randomSortedSets);
            expected = dummyNSetIntersection(randomSortedSets);

            /*
            System.out.println("Number of sets = " + randomSortedSets.length + "\n");
            for(SortedSet<Integer> set : randomSortedSets) {
                printSet(set);
            }

            System.out.println("\nExpected Values = \n");
            for(int value : expected) {
                System.out.println("Value = " + value);
            }
            */

            assertEquals(expected, result);
        }
    }

    private static void printSet(Set<Integer> set) {
        System.out.println("Going to print set");
        for(int value : set) {
            System.out.println("\t" + value);
        }
    }
}
