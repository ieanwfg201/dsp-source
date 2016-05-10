package com.kritter.rtb.ds.trie.index.impl;

import com.kritter.rtb.ds.trie.index.Index;
import com.kritter.rtb.ds.trie.node.TrieNode;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Implementation of index. Stores the trie nodes in a sorted array.
 */
public class SortedArrayIndexImpl implements Index, Serializable {
    private static final long serialVersionUID = 1L;

    int[] dimValues;
    TrieNode[] nodes;

    public SortedArrayIndexImpl() {
        dimValues = null;
        nodes = null;
    }

    public SortedArrayIndexImpl(int[] dimValues, TrieNode[] nodes) {
        if(dimValues.length != nodes.length)
            throw new RuntimeException("Number of dimension values doesn't match number of nodes. " +
                    "Bailing out");

        IntNodePair[] pairs = new IntNodePair[dimValues.length];
        for(int i = 0; i < pairs.length; ++i) {
            pairs[i] = new IntNodePair(dimValues[i], nodes[i]);
        }

        Arrays.sort(pairs);
        this.dimValues = new int[dimValues.length];
        this.nodes = new TrieNode[nodes.length];
        for(int i = 0; i < pairs.length; ++i) {
            this.dimValues[i] = pairs[i].dimValue;
            this.nodes[i] = pairs[i].node;
        }
    }

    /**
     * @return Returns all the nodes in this index.
     */
    @Override
    public TrieNode[] getChildren() {
        return nodes;
    }

    /**
     * @return Returtns all dimensions in this index
     */
    public int[] getDimensions() {
        return dimValues;
    }

    /**
     * Returns the node corresponding to the dimension value. Binary searches for this dimension.
     * @param dimValue Value of the dimension for which the node is to be returned
     * @return Trie node corresponding to the dimension. Null if absent
     */
    @Override
    public TrieNode getChild(int dimValue) {
        int position = Arrays.binarySearch(dimValues, dimValue);
        if(position >= 0)
            return nodes[position];

        return null;
    }

    /**
     * @param dimValue Value of the dimension for which node is to be inserted
     * @param node Trie node to be inserted for the dimension
     */
    @Override
    public void insertChild(int dimValue, TrieNode node) {
        if(dimValues == null) {
            dimValues = new int[1];
            nodes = new TrieNode[1];
            dimValues[0] = dimValue;
            nodes[0] = node;
            return;
        }

        int[] newDimValues = new int[dimValues.length + 1];
        TrieNode[] newNodes = new TrieNode[nodes.length + 1];
        boolean found = false;
        for(int i = 0, j = 0; i < newDimValues.length; ++j) {
            if(!found && dimValues[i] == dimValue) {
                newDimValues[j] = dimValue;
                newNodes[j] = node;
                found = true;
            } else {
                newDimValues[j] = dimValues[i];
                newNodes[j] = nodes[i];
                ++i;
            }
        }
        dimValues = newDimValues;
        nodes = newNodes;
    }

    /**
     * Sets the dimension values and nodes for this index.
     * @param dimValues Values of all the dimensions
     * @param nodes Nodes corresponding to the dimension values
     */
    @Override
    public void setChildren(int[] dimValues, TrieNode[] nodes) {
        if(dimValues.length != nodes.length)
            throw new RuntimeException("Number of dimension values doesn't match number of nodes. " +
                    "Bailing out");

        IntNodePair[] pairs = new IntNodePair[dimValues.length];
        for(int i = 0; i < pairs.length; ++i) {
            pairs[i] = new IntNodePair(dimValues[i], nodes[i]);
        }

        Arrays.sort(pairs);
        this.dimValues = new int[dimValues.length];
        this.nodes = new TrieNode[nodes.length];
        for(int i = 0; i < pairs.length; ++i) {
            this.dimValues[i] = pairs[i].dimValue;
            this.nodes[i] = pairs[i].node;
        }
    }

    /**
     * Class having dimension value and node pair. Used for sorting the nodes.
     */
    public static class IntNodePair implements Comparable<IntNodePair> {
        private int dimValue;
        private TrieNode node;

        public IntNodePair(int dimValue, TrieNode node) {
            this.dimValue = dimValue;
            this.node = node;
        }

        @Override
        public int compareTo(IntNodePair o) {
            if(dimValue < o.dimValue)
                return -1;
            if(dimValue > o.dimValue)
                return 1;
            return 0;
        }
    }
}
