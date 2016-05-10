package com.kritter.rtb.ds.trie.utils;

import com.kritter.rtb.ds.trie.index.impl.HashIndexImpl;
import com.kritter.rtb.ds.trie.index.impl.LinearArrayIndexImpl;
import com.kritter.rtb.ds.trie.index.impl.SortedArrayIndexImpl;
import com.kritter.rtb.ds.trie.node.TrieNode;

/**
 * Utility functions for the trie
 */
public class TrieUtils {
    /**
     * Returns a linear array index when given a hash index.
     * @param hashIndex Hash index
     * @return Linear array index object.
     */
    public static LinearArrayIndexImpl convertHashIndexToLinearIndex(HashIndexImpl hashIndex) {
        if(hashIndex == null) {
            return new LinearArrayIndexImpl(null, null);
        }

        int[] dimValues = hashIndex.getDimensions();
        TrieNode[] nodes = hashIndex.getChildren();

        return new LinearArrayIndexImpl(dimValues, nodes);
    }

    /**
     * Returns a sorted array index when given a hash index.
     * @param hashIndex Hash index
     * @return Sorted array index object
     */
    public static SortedArrayIndexImpl convertHashIndextoSortedIndex(HashIndexImpl hashIndex) {
        if(hashIndex == null) {
            return new SortedArrayIndexImpl(null, null);
        }

        int[] dimValues = hashIndex.getDimensions();
        TrieNode[] nodes = hashIndex.getChildren();

        return new SortedArrayIndexImpl(dimValues, nodes);
    }
}
