package com.kritter.rtb.ds.trie.node.impl;

import com.kritter.rtb.ds.trie.index.Index;
import com.kritter.rtb.ds.trie.index.impl.LinearArrayIndexImpl;
import com.kritter.rtb.ds.trie.node.TrieNode;

import java.io.Serializable;

/**
 * Implementation for the trie node interface.
 */
public class TrieNodeImpl<T> implements TrieNode<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private int dimension;
    private Index index;
    private T value;

    public TrieNodeImpl() {
        this.dimension = -1;
        this.index = new LinearArrayIndexImpl();
        this.value = null;
    }

    public TrieNodeImpl(int dimension, Index index, T value) {
        this.dimension = dimension;
        this.index = index;
        this.value = value;
    }

    /**
     * @param dimension Winner dimension for this node.
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * @return The dimension to which the children of this node correspond
     */
    @Override
    public int getDimension() {
        return dimension;
    }

    /**
     * @return Returns the index containing the children.
     */
    public Index getIndex() {
        return index;
    }

    /**
     *
     * @param index Index object to contain all the children for this node
     */
    public void setIndex(Index index) {
        this.index = index;
    }

    /**
     * @return The value stored in this node. Could be null for internal nodes
     */
    public T getValue() {
        return value;
    }

    /**
     * @return Set the value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * @return Returns whether this node is a leaf or not. True if yes, false if not
     */
    public boolean isLeaf() {
        return dimension == -1;
    }
}
