package com.kritter.utils.common;

import java.util.*;

/**
 * Class containing the utility functions for Sets.
 */
public class SetUtils {
    /**
     * Returns the intersection of sets first and second. Doesn't modify
     * any of the two provided sets
     * @param first First set
     * @param second Second set
     * @param <T> Type of the set
     * @return Intersection of the two sets. empty if any of them is null
     */
    public static <T> Set<T> intersectSets(Set<T> first, Set<T> second) {
        if(first == null || second == null) {
            return new HashSet<T>();
        }

        Set<T> result;
        if(first instanceof SortedSet && second instanceof SortedSet) {
            result = new TreeSet<T>();
        } else {
            result = new HashSet<T>();
        }

        Set<T> small = first.size() < second.size() ? first : second;
        Set<T> large = first.size() < second.size() ? second : first;

        int smallSize = small.size();
        int largeSize = large.size();

        if(!(first instanceof SortedSet) || !(second instanceof SortedSet) || (smallSize <= largeSize * 0.8)) {
            for(T element : small) {
                if(large.contains(element)) {
                    result.add(element);
                }
            }
        } else {
            result = intersectSortedSets((SortedSet<T>) first, (SortedSet<T>) second);
        }

        return result;
    }

    /**
     * Computes the intersection between two sorted sets. If either of the sorted sets is null or
     * empty, returns an empty set.
     * The complexity of the method is O(max(size(first), size(second))) or O(m + n)
     * where m = size of the first set
     * and n = size of the second set
     * @param first First set
     * @param second Second set
     * @param <T> Type of the set
     * @return Intersection of the two sets as a sorted set. Empty set if any of the two sets is empty or null
     */
    public static <T> SortedSet<T> intersectSortedSets(SortedSet<T> first, SortedSet<T> second) {
        SortedSet<T> result = new TreeSet<T>();

        if(first == null || second == null || first.isEmpty() || second.isEmpty())
            return result;

        Iterator<T> firstIterator = first.iterator();
        Iterator<T> secondIterator = second.iterator();
        T firstElement = firstIterator.next();
        T secondElement = secondIterator.next();

        Comparator<? super T> comparator = first.comparator();
        while(true) {
            int comparisonResult = compareElements(firstElement, secondElement, comparator);

            boolean getFirstNext = false;
            boolean getSecondNext = false;

            if(comparisonResult == 0) {
                result.add(firstElement);
                getFirstNext = true;
                getSecondNext = true;
            } else if(comparisonResult == -1) {
                getFirstNext = true;
            } else {
                getSecondNext = true;
            }

            if(getFirstNext) {
                if(firstIterator.hasNext()) {
                    firstElement = firstIterator.next();
                } else {
                    break;
                }
            }

            if(getSecondNext) {
                if(secondIterator.hasNext()) {
                    secondElement = secondIterator.next();
                } else {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Perform N way intersection of n sets. If all the sets are of type SortedSet, then call the
     * alternative function for intersection of n sorted sets. Else pick the smallest set and check
     * for it's elements in all the sets.
     * @param sets Array of sets whose intersection is to be calculated
     * @param <T> Type of set
     * @return Intersection of all the sets. Empty set if any of the sets is null or empty or the array
     *          is null or empty or the intersection is empty.
     */
    public static <T> Set<T> intersectNSets(Set<T> ... sets) {
        boolean allSorted = true;

        if(sets == null || sets.length == 0) {
            return new HashSet<T>();
        }

        for(Set set : sets) {
            if(set == null) {
                return new HashSet<T>();
            }
            if(!(set instanceof SortedSet)) {
                allSorted = false;
            }
        }

        if(allSorted) {
            SortedSet<T>[] sortedSets = new SortedSet[sets.length];
            for(int i = 0; i < sets.length; ++i) {
                sortedSets[i] = (SortedSet<T>) sets[i];
            }
            return intersectNSets(sortedSets);
        } else {
            Set<T> result = new HashSet<T>();

            // Take the smallest set.
            int smallestSize = sets[0].size();
            Set<T> smallestSet = sets[0];
            for(Set<T> set : sets) {
                int size = set.size();
                if(size < smallestSize) {
                    smallestSize = size;
                    smallestSet = set;
                }
            }

            forLoop:
            for(T element : smallestSet) {
                for(Set<T> set : sets) {
                    if(!set.contains(element)) {
                        continue forLoop;
                    }
                }
                result.add(element);
            }

            return result;
        }
    }

    /**
     * Intersects N sorted sets and returns the intersection in a single set. Doesn't modify any of the supplied,
     * sets.
     * Time Complexity = O(sum of lengths of all the sorted sets)
     * @param sortedSets Array os sorted sets
     * @param <T> Type of the sorted sets. If the sorted sets don't use a comparator then T must have a
     *           natural ordering defined.
     * @return If any of the sets is null or empty, returns an empty SortedSet. Else, returns the intersection
     *          of the sorted sets.
     */
    public static <T> SortedSet<T> intersectNSets(SortedSet<T> ... sortedSets) {
        SortedSet<T> result = new TreeSet<T>();

        if(sortedSets == null || sortedSets.length == 0)
            return result;

        for(SortedSet<T> set : sortedSets) {
            if(set == null || set.isEmpty())
                return result;
        }

        if(sortedSets.length == 1) {
            result.addAll(sortedSets[0]);
        }

        if(sortedSets.length == 2) {
            return intersectSortedSets(sortedSets[0], sortedSets[1]);
        }

        Object[] elements = new Object[sortedSets.length];
        Iterator<T>[] iterators = new Iterator[sortedSets.length];
        for(int i = 0; i < sortedSets.length; ++i) {
            iterators[i] = sortedSets[i].iterator();
            elements[i] = iterators[i].next();
        }

        Comparator<? super T> comparator = sortedSets[0].comparator();

        whileLoop :
        while(true) {
            while(true) {
                T firstElement = (T) elements[0];
                T secondElement = (T) elements[1];

                int comparisonResult = compareElements(firstElement, secondElement, comparator);
                if(comparisonResult == 0)
                    break;

                if(comparisonResult < 0) {
                    if(!iterators[0].hasNext()) {
                        break whileLoop;
                    } else {
                        elements[0] = iterators[0].next();
                    }
                } else {
                    if(!iterators[1].hasNext()) {
                        break whileLoop;
                    } else {
                        elements[1] = iterators[1].next();
                    }
                }
            }

            T elementToFind = (T) elements[0];
            int equalUntil = 2;

            forLoop :
            for(; equalUntil < sortedSets.length; ++equalUntil) {
                while(true) {
                    T element = (T) elements[equalUntil];
                    int comparisonResult = compareElements(element, elementToFind, comparator);

                    if(comparisonResult == 0) {
                        break;
                    } else if(comparisonResult > 0) {
                        break forLoop;
                    } else if(comparisonResult < 0) {
                        if(!iterators[equalUntil].hasNext()) {
                            break whileLoop;
                        } else {
                            elements[equalUntil] = iterators[equalUntil].next();
                        }
                    }
                }
            }

            if(equalUntil == sortedSets.length) {
                result.add(elementToFind);
            }

            for(int i = 0; i < equalUntil; ++i) {
                if(!iterators[i].hasNext()) {
                    break whileLoop;
                } else {
                    elements[i] = iterators[i].next();
                }
            }
        }

        return result;
    }

    /**
     * Returns the comparison between the first and second element. If comparator is null,
     * returns the natural ordering
     * @param first First element
     * @param second Second element
     * @param comparator Comparator between the 2 objects. Can be null
     * @param <T> If comparator is null, then T must have a natural ordering, i.e., must implement
     *           Comparable<T></T>
     * @return 0 if first == second
     *          1 if first > second
     *          -1 if first < second
     */
    private static <T> int compareElements(T first, T second, Comparator<? super T> comparator) {
        if(first == null && second == null)
            return 0;
        if(first == null)
            return -1;
        if(second == null)
            return 1;

        if(comparator == null) {
            return  ((Comparable<T>) first).compareTo(second);
        } else {
            return comparator.compare(first, second);
        }
    }
}
