package com.kritter.rtb.ds.trie.node;

import com.kritter.rtb.ds.trie.index.Index;

/**
 * Interface for the trie node. Each node has a list of children. A child corresponds to a dimension value.
 * The dimension could be country for example. Each dimension is mapped to an integer value.
 */
public interface TrieNode<T> {
    /**
     * @param dimension Winner dimension for this node.
     */
    public void setDimension(int dimension);

    /**
     * @return The dimension to which the children of this node correspond
     */
    public int getDimension();

    /**
     * @return Returns the index containing the children.
     */
    public Index getIndex();

    /**
     *
     * @param index Index object to contain all the children for this node
     */
    public void setIndex(Index index);

    /**
     * @return The value stored in this node. Could be null for internal nodes
     */
    public T getValue();

    /**
     * @return Set the value
     */
    public void setValue(T value);

    /**
     * @return Returns whether this node is a leaf or not. True if yes, false if not
     */
    public boolean isLeaf();
}
