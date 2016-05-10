package com.kritter.rtb.ds.trie.index.impl;

import com.kritter.rtb.ds.trie.index.Index;
import com.kritter.rtb.ds.trie.node.TrieNode;

import java.io.Serializable;

/**
 * Implementation of index. Stores the trie nodes in a linear array.
 */
public class LinearArrayIndexImpl implements Index, Serializable {
    private static final long serialVersionUID = 1L;

    private int[] dimValues;
    private TrieNode[] nodes;

    public LinearArrayIndexImpl() {
        dimValues = null;
        nodes = null;
    }

    public LinearArrayIndexImpl(int[] dimValues, TrieNode[] nodes) {
        this.dimValues = dimValues;
        this.nodes = nodes;
    }

    public LinearArrayIndexImpl(int nodeCount) {
        this.dimValues = new int[nodeCount];
        this.nodes = new TrieNode[nodeCount];
    }

    /**
     * @return Returns all dimensions in this index
     */
    public int[] getDimensions() {
        return dimValues;
    }

    /**
     * @return Returns all the children for this index
     */
    @Override
    public TrieNode[] getChildren() {
        return nodes;
    }

    /**
     * @param dimValue Value of the dimension for which the node is to be returned
     * @return Trie node corresponding to the dimension. Null if absent
     */
    @Override
    public TrieNode getChild(int dimValue) {
        for(int i = 0; i < dimValues.length; ++i)
            if(dimValues[i] == dimValue)
                return nodes[i];

        return null;
    }

    /**
     * Increases the length of the nodes and dimensions arrays by 1 each and puts the dimension
     * value and node in the array. Very inefficient.
     * @param dimValue Value of the dimension for which node is to be inserted
     * @param node Trie node to be inserted for the dimension
     */
    @Override
    public void insertChild(int dimValue, TrieNode node) {
        int[] newDimValues = null;
        if(dimValues == null)
            newDimValues = new int[1];
        else
            newDimValues = new int[dimValues.length + 1];
        if(dimValues != null)
            System.arraycopy(dimValues, 0, newDimValues, 0, dimValues.length);
        newDimValues[newDimValues.length - 1] = dimValue;
        dimValues = newDimValues;

        TrieNode[] newNodes = null;
        if(nodes == null)
            newNodes = new TrieNode[1];
        else
            newNodes = new TrieNode[nodes.length + 1];
        if(nodes == null)
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        newNodes[newNodes.length - 1] = node;
        nodes = newNodes;
    }

    /**
     * Sets the dimension values and nodes for this index. Preferably use this over the stock insertChild
     * @param dimValues Values of all the dimensions
     * @param nodes Nodes corresponding to the dimension values
     */
    @Override
    public void setChildren(int[] dimValues, TrieNode[] nodes) {
        if(dimValues.length != nodes.length)
            throw new RuntimeException("Number of dimension values doesn't match number of nodes. " +
                    "Bailing out");

        this.dimValues = dimValues;
        this.nodes = nodes;
    }
}
