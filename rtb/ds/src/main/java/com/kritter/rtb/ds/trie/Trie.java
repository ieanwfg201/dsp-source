package com.kritter.rtb.ds.trie;

import com.kritter.rtb.ds.trie.index.Index;
import com.kritter.rtb.ds.trie.index.impl.HashIndexImpl;
import com.kritter.rtb.ds.trie.index.impl.LinearArrayIndexImpl;
import com.kritter.rtb.ds.trie.index.impl.SortedArrayIndexImpl;
import com.kritter.rtb.ds.trie.node.TrieNode;
import com.kritter.rtb.ds.trie.node.impl.TrieNodeImpl;
import com.kritter.rtb.ds.trie.utils.TrieUtils;
import org.apache.commons.configuration.Configuration;

import java.io.Serializable;

/**
 * Class implementing the "trie" data structure. More specifically, it's an array mapped trie.
 * Each node has children corresponding to dimension values.
 * @param <T>
 */
public class Trie<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private TrieNode<T> root;
    private int linearIndexlimit;
    private boolean returnFromInternalNode;

    private final int defaultLinearIndexLimit = 5;
    public static final String LINEAR_INDEX_LIMIT_KEY = "linear-index-limit";
    public static final String INNER_NODE_RETURN_KEY = "return-from-inner-node";

    public Trie(Configuration configuration) {
        this.root = null;
        this.linearIndexlimit = defaultLinearIndexLimit;
        this.returnFromInternalNode = true;

        if(configuration != null) {
            this.linearIndexlimit = configuration.getInt(LINEAR_INDEX_LIMIT_KEY, defaultLinearIndexLimit);
            this.returnFromInternalNode = configuration.getBoolean(INNER_NODE_RETURN_KEY);
        }
    }

    public void build() {
        if(root.getIndex().getClass() != HashIndexImpl.class)
            return;

        build(root);
    }

    private void build(TrieNode<T> node) {
        Index index = node.getIndex();
        TrieNode[] childNodes = null;

        if(index != null) {
            childNodes = index.getChildren();
        }
        if(index == null || childNodes.length <= linearIndexlimit) {
            Index newIndex = TrieUtils.convertHashIndexToLinearIndex((HashIndexImpl) index);
            node.setIndex(newIndex);
        } else {
            Index newIndex = TrieUtils.convertHashIndextoSortedIndex((HashIndexImpl) index);
            node.setIndex(newIndex);
        }

        if(childNodes != null)
            for(TrieNode child : childNodes)
                build(child);
    }

    public void insertPath(TriePath<T> path) {
        if(root == null)
            root = new TrieNodeImpl<T>(-1, null, null);
        insertPath(root, path, 0);
    }

    private void insertPath(TrieNode<T> node, TriePath<T> path, int position) {
        int dimension = node.getDimension();
        if(path == null || path.getDimensionList() == null || path.getDimensionValue() == null)
            return;

        int[] pathDimensionList = path.getDimensionList();
        int[] pathDimensionValue = path.getDimensionValue();
        if(pathDimensionList.length == 0)
            return;
        if(pathDimensionList.length != pathDimensionValue.length) {
            throw new RuntimeException("Malformed path sent.");
        }


        if(position > path.getDimensionList().length) {
            throw new RuntimeException("A child path has been added into the trie before" +
                    "the parent path. Check the sequence of paths being added. They must be" +
                    "sorted.");
        }

        if(position == pathDimensionList.length) {
            node.setValue(path.getValue());
        } else {
            int currentDimension = pathDimensionList[position];
            if(dimension != -1 && dimension != currentDimension)
                throw new RuntimeException("Wrong dimension value. The one in node is " +
                        dimension + " while that in the path is " + position);

            if(dimension == -1) {
                node.setDimension(currentDimension);
                TrieNode<T> child = new TrieNodeImpl<T>(-1, null, null);
                Index index = node.getIndex();
                if(index == null) {
                    index = new HashIndexImpl();
                    node.setIndex(index);
                }
                index.insertChild(pathDimensionValue[position], child);
                insertPath(child, path, position + 1);
            } else {
                TrieNode<T> child = node.getIndex().getChild(pathDimensionValue[position]);
                if(child == null) {
                    child = new TrieNodeImpl(-1, null, null);
                    node.getIndex().insertChild(pathDimensionValue[position], child);
                }
                insertPath(child, path, position + 1);
            }
        }
    }

    public T getValueForPath(int[] dimensionValues) {
        return getValueForPath(root, dimensionValues);
    }

    private T getValueForPath(TrieNode<T> node, int[] dimensionValues) {
        int dimension = node.getDimension();
        if(dimension >= dimensionValues.length) {
            StringBuilder exceptionMessage = new StringBuilder("Numer of dimensions queried for is wrong.");
            for(int i = 0; i < dimensionValues.length; ++i) {
                exceptionMessage.append(dimensionValues[i]);
                if(i != dimensionValues.length - 1)
                    exceptionMessage.append(",");
            }
            throw new RuntimeException(exceptionMessage.toString());

        }
        if(dimension == -1)
            return node.getValue();

        int dimensionValue = dimensionValues[dimension];
        TrieNode<T> child = node.getIndex().getChild(dimensionValue);
        if(child == null) {
            if(returnFromInternalNode)
                return node.getValue();
            else
                return null;
        }
        return getValueForPath(child, dimensionValues);
    }
}
