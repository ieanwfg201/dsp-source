package com.kritter.rtb.ds.trie.index;

import com.kritter.rtb.ds.trie.node.TrieNode;

/**
 * Index interface. Used for indexing the children of the trie.
 */
public interface Index {
    /**
     * @return Returns all dimensions in this index
     */
    public int[] getDimensions();

    /**
     * @return Returns all the nodes in this index.
     */
    public TrieNode[] getChildren();

    /**
     * @param dimValue Value of the dimension for which the node is to be returned
     * @return Trie node corresponding to the dimension. Null if absent
     */
    public TrieNode getChild(int dimValue);

    /**
     * @param dimValue Value of the dimension for which node is to be inserted
     * @param node Trie node to be inserted for the dimension
     */
    public void insertChild(int dimValue, TrieNode node);

    /**
     * Sets the dimension values and nodes for this index.
     * @param dimValues Values of all the dimensions
     * @param nodes Nodes corresponding to the dimension values
     */
    public void setChildren(int[] dimValues, TrieNode[] nodes);
}
