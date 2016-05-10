package com.kritter.utils.common;

import java.util.*;

/**
 * Common utility functions that don't fit anywhere else go here.
 */
public class CommonUtils {
    /**
     * Function to return K unique random elements from a collection. All sets of size k have an equal probability of
     * getting selected. Each element has a probability of k / n (assuming n = size of collection and k < n) of getting
     * selected.
     * @param collection
     * @param k
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> getKRandomElementsFromCollection(Collection<T> collection, int k) {
        ArrayList<T> res = new ArrayList<T>();

        if(collection == null) {
            return res;
        }

        ArrayList<T> collectionList = new ArrayList<T>(collection.size());
        for(T element : collection) {
            collectionList.add(element);
        }

        int n = collection.size();
        if(n <= k) {
            return collectionList;
        }

        Random random = new Random();
        for(int i = 0; i < n; ++i) {
            int maxLim = n - i;
            int pos = random.nextInt(maxLim);
            if(pos != maxLim - 1) {
                T temp = collectionList.get(pos);
                collectionList.set(pos, collectionList.get(maxLim - 1));
                collectionList.set(maxLim - 1, temp);
            }
        }

        for(int i = 0; i < k; ++i) {
            res.add(collectionList.get(n - i - 1));
        }

        return res;
    }

    public static void main(String[] args) {
        int setSize = 10;
        Set<Integer> intSet = new HashSet<Integer>(setSize);
        for(int i = 0; i < setSize; ++i) {
            intSet.add(i);
        }

        for(int i = 0; i < 10; ++i) {
            Random random = new Random();
            int k = random.nextInt(setSize + 3);
            ArrayList<Integer> kRandom = getKRandomElementsFromCollection(intSet, k);
            System.out.println("K random where k = " + k + " and set size = " + setSize);
            for(int j = 0; j < kRandom.size(); ++j) {
                System.out.print(kRandom.get(j) + " ");
            }
            System.out.println();
        }
    }
}
