package com.kritter.rtb.ds.trie.index.impl;

import com.kritter.rtb.ds.trie.index.Index;
import com.kritter.rtb.ds.trie.node.TrieNode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of index. Stores the trie nodes in a hash map.
 */
public class HashIndexImpl implements Index, Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<Integer, TrieNode> dimNodeMap;

    public HashIndexImpl() {
        dimNodeMap = new HashMap<Integer, TrieNode>();
    }

    public HashIndexImpl(HashMap<Integer, TrieNode> dimNodeMap) {
        this.dimNodeMap = dimNodeMap;
    }

    /**
     * @return Returns all dimensions in this index
     */
    public int[] getDimensions() {
        int[] dimValues = new int[dimNodeMap.size()];

        int i = 0;
        for(Map.Entry<Integer, TrieNode> entry : dimNodeMap.entrySet()) {
            dimValues[i++] = entry.getKey();
        }

        return dimValues;
    }

    /**
     * @return Returns all the children for this index
     */
    @Override
    public TrieNode[] getChildren() {
        TrieNode[] nodes = new TrieNode[dimNodeMap.size()];

        int i = 0;
        for(Map.Entry<Integer, TrieNode> entry : dimNodeMap.entrySet()) {
            nodes[i++] = entry.getValue();
        }
        return nodes;
    }

    /**
     * @param dimValue Value of the dimension for which the node is to be returned
     * @return Trie node corresponding to the dimension. Null if absent
     */
    @Override
    public TrieNode getChild(int dimValue) {
        return dimNodeMap.get(dimValue);
    }

    /**
     * Inserts the node for dimension value "dimValue". Overwrites the node if already present.
     * @param dimValue Value of the dimension for which the node is to be returned
     * @return Trie node corresponding to the dimension
     */
    @Override
    public void insertChild(int dimValue, TrieNode node) {
        dimNodeMap.put(dimValue, node);
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

        dimNodeMap.clear();

        for(int i = 0; i < dimValues.length; ++i)
            dimNodeMap.put(dimValues[i], nodes[i]);
    }
}
