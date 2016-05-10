package com.kritter.core.expressiontree.node;

import java.util.ArrayList;

/**
 * Node in the expression tree. Given a list of values, evaluate
 * the expression underneath this node's subtree.
 */
public interface Node {
    public boolean evaluate(ArrayList<Object> values);
}
